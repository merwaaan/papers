import java.io.IOException;
import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.util.Camera;

public class Simulation {

	public Graph net;

	public int step;
	public int worldSize;
	public double sensorSpeed;

	public int communicationRadius;
	public int repulsionRadius;
	public int attractionRadius;

	public int sensorCount;

	public ArrayList<Obstacle> obstacles;

	public boolean displayRadii;
	private int frameLength;
	private FileSinkImages fileSink;

	private static String style =
		"node { size: 7px; fill-mode: dyn-plain; fill-color: black, red; }" +
		"edge { size: 1px; }";

	public Simulation(int sensorCount, boolean enableObstacles, int worldSize, boolean displayRadii, String capturePrefix) {

		// Create the network.
		this.net = new SingleGraph("sensor network");
		this.net.addAttribute("ui.stylesheet", Simulation.style);

		this.step = 0;
		this.worldSize = worldSize;
		this.sensorSpeed = 2;

		this.communicationRadius = 450;
		this.repulsionRadius = 400 - 30;
		this.attractionRadius = 400 + 30;

		// Spawn obstacles.
		this.obstacles = new ArrayList<Obstacle>();
		if(enableObstacles) {
			this.obstacles.add(new Obstacle(0, 0, 200));
			this.obstacles.add(new Obstacle(1000, 400, 400));
			this.obstacles.add(new Obstacle(-2000, -1000, 800));
		}

		this.displayRadii = displayRadii;

		View view = this.net.display(false).getDefaultView();
		view.setBackLayerRenderer(new BackgroundRenderer(this));

		Camera camera = view.getCamera();
		//camera.setGraphViewport(-7000, 0, 0, 0);

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

		this.net.setNodeFactory(new SensorFactory(this));

		// Spawn sensors.
		this.sensorCount = sensorCount;
		for(int i = 0; i < sensorCount; ++i)
			this.net.addNode("" + (this.net.getNodeCount() + 1));
	}

	public void run() {

		while(true) {

			// update the simulation.
			this.update();

			// Record a screenshot if necessary.
			if(this.fileSink != null)
				this.fileSink.outputNewImage();

			// Pause until next frame.
			this.pause(this.frameLength);
		}
	}

	private void update() {

		/******
		GUIDING
		*******/

		// First, compute the next move.
		for(Node sensor : this.net)
			((Sensor)sensor).computeNextMove();

		// Then move.
		for(Node sensor : this.net)
			((Sensor)sensor).move();

		// Check for new neighbors when the moves are over.
		for(Node sensor : this.net)
			((Sensor)sensor).checkForNeighbors();

		/******
		COUNTING
		*******/

		for(Node sensor : this.net) {
			((Sensor)sensor).count();
			((Sensor)sensor).color();
		}

		++this.step;
	}

	private void pause(int ms) {

		try {
			Thread.sleep(ms);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
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
