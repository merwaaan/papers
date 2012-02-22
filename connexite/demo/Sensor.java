import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.AbstractEdge;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.SingleNode;
import org.graphstream.ui.geom.Vector2;

public class Sensor extends SingleNode {

	private Simulation sim;

	private int communicationRadius;
	private int repulsionRadius;
	private int attractionRadius;

	private Vector2 nextMove;

	public Sensor(Graph graph, String id, Simulation sim) {

		super((AbstractGraph)graph, id);

		this.sim = sim;

		this.communicationRadius = this.sim.communicationRadius;
		this.repulsionRadius = this.sim.repulsionRadius;
		this.attractionRadius = this.sim.attractionRadius;

		int halfWorldSize = this.sim.worldSize / 2;
		this.setPosition(Math.random() * this.sim.worldSize - halfWorldSize, Math.random() * this.sim.worldSize - halfWorldSize);
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

}

