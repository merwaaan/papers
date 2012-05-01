import java.util.ArrayList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.SingleNode;
import org.graphstream.ui.geom.Vector2;

public class Sensor extends SingleNode {

	private Simulation sim;

	private Vector2 nextMove;

	private int communicationRadius;
	private int repulsionRadius;
	private int attractionRadius;

	private int sensorCount;

	private ArrayList<Sensor> N_subjects;
	private ArrayList<Integer> N_times;

	private ArrayList<Sensor> C_subjects;
	private ArrayList<Sensor> C_sources;
	private ArrayList<Integer> C_times;

	private ArrayList<Sensor> D_subjects;
	private ArrayList<Integer> D_times;

	private int nextMessageCountdown;
	private int maxMessageCountdown;
	private int neighborAlarm;

	public Sensor(Graph graph, String id, Simulation sim) {

		super((AbstractGraph)graph, id);

		this.sim = sim;

		this.communicationRadius = this.sim.communicationRadius;
		this.repulsionRadius = this.sim.repulsionRadius;
		this.attractionRadius = this.sim.attractionRadius;

		this.sensorCount = this.sim.sensorCount;

		int halfWorldSize = this.sim.worldSize / 2;
		this.setPosition(Math.random() * this.sim.worldSize - halfWorldSize, Math.random() * this.sim.worldSize - halfWorldSize);

		this.N_subjects = new ArrayList<Sensor>();
		this.N_times = new ArrayList<Integer>();

		this.C_subjects = new ArrayList<Sensor>();
		this.C_sources = new ArrayList<Sensor>();
		this.C_times = new ArrayList<Integer>();

		this.D_subjects = new ArrayList<Sensor>();
		this.D_times = new ArrayList<Integer>();

		this.maxMessageCountdown = 200;
		this.nextMessageCountdown = (int)(Math.random() * maxMessageCountdown);
		this.neighborAlarm = 100000;
	}

	private double getDistanceFrom(Sensor s) {

		// Retrieve the two positions.
		Double x1 = this.getAttribute("x");
		Double y1 = this.getAttribute("y");
		Double x2 = s.getAttribute("x");
		Double y2 = s.getAttribute("y");

		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}

	public Vector2 getPosition(Sensor s) {

		Double x = s.getAttribute("x");
		Double y = s.getAttribute("y");

		return new Vector2(x, y);
	}

	private void setPosition(double x, double y) {

		this.setAttribute("x", x);
		this.setAttribute("y", y);
	}

	private Vector2 limitToSpeed(Vector2 force, double speed) {

		double length = force.length();

		if(length > speed) {

			double ratio = 1 / (length / speed);
			force.scalarMult(ratio);
		}

		return force;
	}

	/******
	       GUIDING
	*******/

	public void computeNextMove() {

		Vector2 avoidance = this.avoidance();
		Vector2 repulsion = this.repulsion();
		Vector2 attraction = this.attraction();
		Vector2 gravity = this.gravity();

		Vector2 force = new Vector2();

		/*
		// Average
		force.add(attraction);
		force.add(repulsion);
		force.add(gravity);
		force.scalarDiv(3);
		*/

		// Proritization.
		Vector2[] forces = new Vector2[] {avoidance, repulsion, attraction, gravity};
		for(int i = 0; i < forces.length; ++i) {
			forces[i] = this.limitToSpeed(forces[i], this.sim.sensorSpeed - force.length());
			force.add(forces[i]);
		}

		this.nextMove = force;
	}

	public void move() {

		// Retrieve the current position.
		Double x_old = this.getAttribute("x");
		Double y_old = this.getAttribute("y");

		// Compute the new position.
		double x_new = x_old + this.nextMove.x();
		double y_new = y_old + this.nextMove.y();

		// Apply.
		this.setAttribute("x", x_new);
		this.setAttribute("y", y_new);
	}

	private Vector2 avoidance() {

		Vector2 position = this.getPosition(this);
		Vector2 force = new Vector2();

		for(Obstacle o : this.sim.obstacles) {

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

		this.limitToSpeed(force, this.sim.sensorSpeed);

		return force;
	}

	private Vector2 repulsion() {

		Vector2 position = this.getPosition(this);
		Vector2 force = new Vector2();

		for(Edge link : this.getEachEdge()) {

			Sensor neighbor = link.getOpposite(this);
			Vector2 positionNeighbor = this.getPosition(neighbor);

			Double distance = this.getDistanceFrom(neighbor);

			if(distance < this.repulsionRadius) {

				Vector2 force_sub = new Vector2(position);
				force_sub.sub(positionNeighbor);
				force_sub.scalarDiv(distance);
				force_sub.scalarMult(this.repulsionRadius - distance);

				force.add(force_sub);
			}
		}

		this.limitToSpeed(force, this.sim.sensorSpeed);

		return force;
	}

	private Vector2 attraction() {

		Vector2 position = this.getPosition(this);
		Vector2 force = new Vector2();

		for(Edge link : this.getEachEdge()) {

			Sensor neighbor = link.getOpposite(this);
			Vector2 positionNeighbor = this.getPosition(neighbor);

			Double distance = this.getDistanceFrom(neighbor);

			if(distance > this.attractionRadius) {

				Vector2 force_sub = new Vector2(positionNeighbor);
				force_sub.sub(position);
				force_sub.scalarMult(distance * distance);

				force.add(force_sub);
			}
		}

		this.limitToSpeed(force, this.sim.sensorSpeed);

		return force;
	}

	private Vector2 gravity() {

		Vector2 position = this.getPosition(this);

		Vector2 force = new Vector2(position);
		force.scalarMult(-1);
		force.scalarDiv(position.length() / this.sim.worldSize * 0.5);

		this.limitToSpeed(force, this.sim.sensorSpeed);

		return force;
	}

	public void checkForNeighbors() {

		Object[] sensors = this.sim.net.getNodeSet().toArray();

		for(int i = 0, l = sensors.length; i < l; ++i) {

			Sensor sensor = (Sensor)sensors[i];

			if(this == sensor)
				continue;

			double distance = this.getDistanceFrom(sensor);

			// If the sensors are already linked.
			if(this.hasEdgeBetween(sensor)) {
				if(distance > this.communicationRadius)
					this.sim.net.removeEdge(this, sensor);
			}
			// If the sensors are not linked yet.
			else if(distance <= this.communicationRadius)
				this.sim.net.addEdge(this.getId() + "_" + sensor.getId(), this, sensor);
		}
	}

	/******
	       COUNTING
	*******/

	public void count() {

		--this.nextMessageCountdown;

		// Send a message to every neighbor.
		if(this.nextMessageCountdown == 0) {

			// Check if recorded neighbors are still connected (surveillance).
			for(int i = 0, l = N_subjects.size(); i < l; ++i)
				if(this.sim.step - N_times.get(i) > this.neighborAlarm) {

					// N -> D
					Sensor s = N_subjects.remove(i);
					N_times.remove(i);
					D_subjects.add(s);
					D_times.add(new Integer(this.sim.step));

					// C ->
					int j = C_subjects.indexOf(s);
					if(j > -1) {
						C_subjects.remove(j);
						C_sources.remove(j);
						C_times.remove(j);
					}

					--i;
					--l;
				}

			// Send our lists to each connected sensor.
			for(Edge link : this.getEachEdge()) {
				Sensor neighbor = link.getOpposite(this);
				neighbor.receive(this, C_subjects, C_sources, C_times, D_subjects, D_times);
			}

			this.nextMessageCountdown = (int)(Math.random() * maxMessageCountdown);
		}
	}

	public void receive(Sensor source,
	                    ArrayList<Sensor> OC_subjects,
	                    ArrayList<Sensor> OC_sources,
	                    ArrayList<Integer> OC_times,
	                    ArrayList<Sensor> OD_subjects,
	                    ArrayList<Integer> OD_times) {

		// Add the source to the connectivity list if it's not yet registered.
		if(!N_subjects.contains(source)) {
			// -> N
			N_subjects.add(source);
			N_times.add(new Integer(this.sim.step));
		}
		if(!C_subjects.contains(source)) {
			// -> C
			C_subjects.add(source);
			C_sources.add(source);
			C_times.add(new Integer(this.sim.step));
		}
		if(D_subjects.contains(source)) {
			int k = D_subjects.indexOf(source);
			D_subjects.remove(k);
			D_times.remove(k);
		}

		// General Idea : Only keep an information trnasmitted by a
		// neighbor if it's more recent. Also, update times and sources whe necesary.

		for(int i = 0, l = OC_subjects.size(); i < l; ++i) {

			if(OC_subjects.get(i) == this)
				continue;

			if(!C_subjects.contains(OC_subjects.get(i))) {
				if(!D_subjects.contains(OC_subjects.get(i))) {
					// -> C
					C_subjects.add(OC_subjects.get(i));
					C_sources.add(source);
					C_times.add(OC_times.get(i));
				}
				else {
					int j = D_subjects.indexOf(OC_subjects.get(i));

					if(j > -1 && OC_times.get(i) > D_times.get(j)) {
						// D -> C
						Sensor s = D_subjects.remove(j);
						D_times.remove(j);
						C_subjects.add(s);
						C_sources.add(source);
						C_times.add(OC_times.get(i));
					}
				}
			}
			else {
				int j = C_subjects.indexOf(OC_subjects.get(i));
				if(j > -1 && OC_times.get(i) > C_times.get(j))
					C_times.set(j, OC_times.get(i));
			}
		}

		for(int i = 0, l = OD_subjects.size(); i < l; ++i) {

			// Doubt is not present.
			if(!D_subjects.contains(OD_subjects.get(i))) {
				if(!C_subjects.contains(OD_subjects.get(i))) {
					// -> D
					D_subjects.add(OD_subjects.get(i));
					D_times.add(OD_times.get(i));
				}
				else {
					int j = C_subjects.indexOf(OD_subjects.get(i));
					if(j > -1 && OD_times.get(i) > C_times.get(j)) {
						// C -> D
						Sensor s = C_subjects.remove(j);
						C_sources.remove(j);
						C_times.remove(j);
						D_subjects.add(s);
						D_times.add(OD_times.get(i));
					}
				}
			}
			// Doubt is already present.
			else {
				int j = D_subjects.indexOf(OD_subjects.get(i));
				if(j > -1 && OD_times.get(i) > D_times.get(j))
					D_times.set(j, OD_times.get(i));
			}
		}

		// TODO : Remove from C if source has just been removed.

		// ...

	}

	public boolean isNetworkConnected() {

		return this.C_subjects.size() + 1 == this.sensorCount;
	}

	public double getNetworkConnectivity() {

		return (double)(this.C_subjects.size() + 1) / this.sensorCount;
	}

	public void color() {

		double connectivity = this.getNetworkConnectivity();

		this.setAttribute("ui.color", connectivity);
		this.setAttribute("ui.label", connectivity);
	}

}
