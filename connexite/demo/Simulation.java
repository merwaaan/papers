import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class Simulation {

	private Graph net;

	private int worldSize;
	private int sensorCount;
	private int sensorRadius;

	private int frameLength = 1000 / 60;

	private static String style =
		"edge { size: 1px; }";

	public Simulation() {

		this.net = new SingleGraph("sensor network");
		this.net.addAttribute("ui.stylesheet", Simulation.style);

		this.worldSize = 1000;
		this.sensorCount = 500;
		this.sensorRadius = 100;

		for(int i = 0; i < this.sensorCount; ++i)
			this.spawn();

		this.net.display(false);
	}

	private void spawn() {

		// Add a new sensor.
		Node sensor = this.net.addNode(Integer.toString(this.net.getNodeCount()));

		// Give it a random starting positioN.
		this.move(sensor, Math.random() * this.worldSize, Math.random() * this.worldSize);
	}

	private void move(Node sensor, double x, double y) {

		// Retrieve the current position.
		Double x_old = sensor.getAttribute("x");
		Double y_old = sensor.getAttribute("y");

		// Compute the new position.
		double x_new = x_old == null ? x : x + x_old;
		double y_new = y_old == null ? y : y + y_old;

		// Apply.
		sensor.setAttribute("x", x_new + x);
		sensor.setAttribute("y", y_new + y);
	}

	private double distance(Node s1, Node s2) {

		// Retrieve the two positions.
		Double x1 = s1.getAttribute("x");
		Double y1 = s1.getAttribute("y");
		Double x2 = s2.getAttribute("x");
		Double y2 = s2.getAttribute("y");

		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}

	public void run() {

		while(true) {

			this.update();

			this.pause(this.frameLength);
		}
	}

	private void pause(int ms) {

		try {
			Thread.sleep(ms);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void update() {

		for(Node sensor : this.net)
			this.updatePosition(sensor);

		this.checkForNeighbors();
	}

	private void updatePosition(Node sensor) {

		this.move(sensor, Math.random(), Math.random());
	}

	private void checkForNeighbors() {

		Object[] sensors = this.net.getNodeSet().toArray();

		for(int i = 0, l = sensors.length; i < l; ++i)
			for(int j = i + 1; j < l; ++j) {

				Node s1 = (Node)sensors[i];
				Node s2 = (Node)sensors[j];

				double d = this.distance(s1, s2);
				//System.out.println(s1.getId()+" "+s2.getId()+" "+d+" "+s1.hasEdgeBetween(s2));
				// If the sensors are already linked.
				if(s1.hasEdgeBetween(s2)) {

					if(d > this.sensorRadius)
						this.net.removeEdge(s1, s2);
				}
				// If the sensors are not linked yet.
				else if(d <= this.sensorRadius)
					this.net.addEdge(s1.getId() + "_" + s2.getId(), s1, s2);
			}
	}

}
