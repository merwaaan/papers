import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class Simulation {

	private Graph net;

	private int worldSize;
	private int sensorCount;

	private int frameLength = 1000 / 60;

	public Simulation() {

		this.net = new SingleGraph("sensor network");

		this.worldSize = 1000;
		this.sensorCount = 50;

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

		// Compute the ew position.
		double x_new = x_old == null ? x : x + x_old;
		double y_new = y_old == null ? y : y + y_old;

		// Apply.
		sensor.setAttribute("x", x_new + x);
		sensor.setAttribute("y", y_new + y);
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

	}

	private void updatePosition(Node sensor) {

		this.move(sensor, Math.random(), Math.random());
	}

}
