import org.graphstream.ui.geom.Vector2;

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
