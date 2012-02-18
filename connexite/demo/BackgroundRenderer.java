import java.awt.Color;
import java.awt.Graphics2D;

import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swingViewer.LayerRenderer;

public class BackgroundRenderer implements LayerRenderer {

	private int radius;
	private int halfRadius;

	public BackgroundRenderer(int radius) {

		this.radius = radius;
		this.halfRadius = radius / 2;
	}

	public void render(Graphics2D g, GraphicGraph graph, double ratio, int w, int h, double minx, double miny, double maxx, double maxy) {

		for(Node sensor : graph) {

			// Retrieve the sensor position.
			double x_gu = ((Double)sensor.getAttribute("x")).doubleValue();
			double y_gu = ((Double)sensor.getAttribute("y")).doubleValue();

			// Convert graph coordinates to screen coordinates.
			int x_s = (int)(w / 2 + x_gu * ratio);
			int y_s = h - (int)(h / 2 + y_gu * ratio);
			int r_s = (int)(this.radius * ratio);
			int hr_s = (int)(r_s / 2);

			// Draw the outline of the detection radius.
			g.setColor(Color.ORANGE);
			g.drawOval(x_s - hr_s, y_s - hr_s, r_s, r_s);
		}
	}
}
