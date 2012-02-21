import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.util.Camera;
import org.graphstream.ui.geom.Vector2;

public class Simulation {

	private Graph net;

	private int worldSize;
	private int sensorCount;

	public int communicationRadius;

	private int separationRadius;
	private int neutralInterval;

	public int repulsionRadius;
	public int attractionRadius;

	private static double sensorSpeed = 2;

	public boolean displayRadii;
	private int frameLength;
	private FileSinkImages fileSink;

	private static String style =
		"node { size: 7px; }" +
		"edge { size: 1px; }";

	class Obstacle {

		public Vector2 position;
		public int radius;
		public int securityDistance;

		public Obstacle(double x, double y, int radius) {

			this.position = new Vector2(x, y);
			this.radius = radius;
			this.securityDistance = 200;
		}
	}

	public boolean obstaclesEnabled;
	public ArrayList<Obstacle> obstacles;

	public Simulation(int sensorCount, boolean obstacles, int worldSize, boolean displayRadii, String capturePrefix) {

		this.net = new SingleGraph("sensor network");
		this.net.addAttribute("ui.stylesheet", Simulation.style);

		this.worldSize = worldSize;
		this.sensorCount = sensorCount;

		this.communicationRadius = 450;
		this.separationRadius = 400;
		this.neutralInterval = 30;

		this.repulsionRadius = this.separationRadius - this.neutralInterval;
		this.attractionRadius = this.separationRadius + this.neutralInterval;

		// Spawn obstacles
		this.obstaclesEnabled = obstacles;
		this.obstacles = new ArrayList<Obstacle>();
		if(this.obstaclesEnabled) {
			this.obstacles.add(new Obstacle(0, 0, 200));
			this.obstacles.add(new Obstacle(1000, 400, 400));
			this.obstacles.add(new Obstacle(-2000, -1000, 800));
		}

		this.displayRadii = displayRadii;

		View view = this.net.display(false).getDefaultView();
		view.setBackLayerRenderer(new BackgroundRenderer(this));

		Camera camera = view.getCamera();
		camera.setGraphViewport(-15000, 0, 0, 0);

		this.net.setAttribute("ui.antialias", true);

		this.frameLength = 1000 / 60;

		if(capturePrefix != null) {
			this.fileSink = new FileSinkImages(capturePrefix,
			                                   FileSinkImages.OutputType.PNG,
			                                   FileSinkImages.Resolutions.HD720,
			                                   FileSinkImages.OutputPolicy.NONE);
			this.fileSink.setQuality(FileSinkImages.Quality.HIGH);
			this.net.addSink(this.fileSink);
		}

		for(int i = 0; i < this.sensorCount; ++i)
			this.spawn();
	}

	private void spawn() {

		// Add a new sensor.
		Node sensor = this.net.addNode(Integer.toString(this.net.getNodeCount()));

		// Give it a random starting position.
		int halfWorldSize = this.worldSize / 2;
		this.move(sensor, Math.random() * this.worldSize - halfWorldSize, Math.random() * this.worldSize - halfWorldSize);
	}

	private void move(Node sensor, double x, double y) {

		// Retrieve the current position.
		Double x_old = sensor.getAttribute("x");
		Double y_old = sensor.getAttribute("y");

		// Compute the new position.
		double x_new = x_old == null ? x : x + x_old;
		double y_new = y_old == null ? y : y + y_old;

		// Apply.
		sensor.setAttribute("x", x_new);
		sensor.setAttribute("y", y_new);
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

			if(this.fileSink != null)
				this.fileSink.outputNewImage();
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

		HashMap<String, Vector2> displacements = new HashMap<String, Vector2>();

		for(Node sensor : this.net)
			displacements.put(sensor.getId(), this.displacementVector(sensor));

		for(Node sensor : this.net) {

			Vector2 displacement = displacements.get(sensor.getId());
			this.move(sensor, displacement.x(), displacement.y());
		}

		this.checkForNeighbors();
	}

	private Vector2 displacementVector(Node sensor) {

		Vector2 avoidance = this.avoidance(sensor);
		Vector2 repulsion = this.repulsion(sensor);
		Vector2 attraction = this.attraction(sensor);
		Vector2 gravity = this.gravity(sensor);

		Vector2 force = new Vector2();

		/*
		// Average
		force.add(attraction);
		force.add(repulsion);
		force.add(gravity);
		force.scalarDiv(3);
		*/

		// Proritize forces.
		Vector2[] forces = new Vector2[] {avoidance, repulsion, attraction, gravity};
		for(int i = 0; i < forces.length; ++i) {
			forces[i] = this.limitToSpeed(forces[i], Simulation.sensorSpeed - force.length());
			force.add(forces[i]);
		}

		return force;
	}

	private Vector2 getPosition(Node sensor) {

		Double x = sensor.getAttribute("x");
		Double y = sensor.getAttribute("y");

		return new Vector2(x, y);
	}

	private Vector2 limitToSpeed(Vector2 force, double speed) {

		double length = force.length();

		if(length > speed) {

			double ratio = 1 / (length / speed);
			force.scalarMult(ratio);
		}

		return force;
	}

	private Vector2 avoidance(Node sensor) {

		Vector2 position = this.getPosition(sensor);
		Vector2 force = new Vector2();

		for(Obstacle o : this.obstacles) {

			Vector2 positionObstacle = o.position;

			Vector2 distanceVec = new Vector2(position);
			distanceVec.sub(positionObstacle);
			double distance = distanceVec.length();

			if(distance < o.radius + o.securityDistance) {

				Vector2 force_sub = distanceVec;
				force_sub.scalarDiv(distance);
				force_sub.scalarMult(o.radius + o.securityDistance - distance);

				force.add(force_sub);
			}
		}

		this.limitToSpeed(force, Simulation.sensorSpeed);

		return force;
	}

	private Vector2 repulsion(Node sensor) {

		Vector2 position = this.getPosition(sensor);
		Vector2 force = new Vector2();

		for(Edge link : sensor.getEachEdge()) {

			Node neighbor = link.getOpposite(sensor);
			Vector2 positionNeighbor = this.getPosition(neighbor);

			Double distance = this.distance(sensor, neighbor);

			if(distance < this.repulsionRadius) {

				Vector2 force_sub = new Vector2(position);
				force_sub.sub(positionNeighbor);
				force_sub.scalarDiv(distance);
				force_sub.scalarMult(this.repulsionRadius - distance);

				force.add(force_sub);
			}
		}

		this.limitToSpeed(force, Simulation.sensorSpeed);

		return force;
	}

	private Vector2 attraction(Node sensor) {

		Vector2 position = this.getPosition(sensor);
		Vector2 force = new Vector2();

		for(Edge link : sensor.getEachEdge()) {

			Node neighbor = link.getOpposite(sensor);
			Vector2 positionNeighbor = this.getPosition(neighbor);

			Double distance = this.distance(sensor, neighbor);

			if(distance > this.attractionRadius) {

				Vector2 force_sub = new Vector2(positionNeighbor);
				force_sub.sub(position);
				force_sub.scalarMult(distance * distance);

				force.add(force_sub);
			}
		}

		this.limitToSpeed(force, Simulation.sensorSpeed);

		return force;
	}

	private Vector2 gravity(Node sensor) {

		Vector2 position = this.getPosition(sensor);

		Vector2 force = new Vector2(position);
		force.scalarMult(-1);
		force.scalarDiv(position.length() / this.worldSize * 0.5);

		this.limitToSpeed(force, Simulation.sensorSpeed);

		return force;
	}

	private void checkForNeighbors() {

		Object[] sensors = this.net.getNodeSet().toArray();

		for(int i = 0, l = sensors.length; i < l; ++i)
			for(int j = i + 1; j < l; ++j) {

				Node s1 = (Node)sensors[i];
				Node s2 = (Node)sensors[j];

				double d = this.distance(s1, s2);

				// If the sensors are already linked.
				if(s1.hasEdgeBetween(s2)) {

					if(d > this.communicationRadius)
						this.net.removeEdge(s1, s2);
				}
				// If the sensors are not linked yet.
				else if(d <= this.communicationRadius)
					this.net.addEdge(s1.getId() + "_" + s2.getId(), s1, s2).addAttribute("distance", d);
			}
	}

	public static void main(String[] args) {

		int sensors = args[0] == null ? 200 : Integer.parseInt(args[0]);
		boolean obstacles = args[1] == null ? false : Boolean.parseBoolean(args[1]);
		int size = args[2] == null ? 1000 : Integer.parseInt(args[2]);
		boolean displayRadii = args[3] == null ? false : Boolean.parseBoolean(args[3]);
		String capturePrefix = args[4] == null || args[4].length() == 0 ? null : args[4];

		new Simulation(sensors, obstacles, size, displayRadii, capturePrefix).run();
	}

}
