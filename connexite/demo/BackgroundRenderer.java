import java.awt.Color;
import java.awt.Graphics2D;

import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swingViewer.LayerRenderer;

public class BackgroundRenderer implements LayerRenderer {

	private Simulation sim;

	public BackgroundRenderer(Simulation sim) {

		this.sim = sim;
	}

	public void render(Graphics2D g, GraphicGraph graph, double ratio, int w, int h, double minx, double miny, double maxx, double maxy) {

		for(Node sensor : graph) {

			// Retrieve the sensor position.
			double x_gu = ((Double)sensor.getAttribute("x")).doubleValue();
			double y_gu = ((Double)sensor.getAttribute("y")).doubleValue();

			// Convert graph coordinates to screen coordinates.
			int x_s = (int)(w / 2 + x_gu * ratio);
			int y_s = h - (int)(h / 2 + y_gu * ratio);

			int cr_s = (int)(this.sim.communicationRadius * ratio);
			int ar_s = (int)(this.sim.attractionRadius * ratio);
			int rr_s = (int)(this.sim.repulsionRadius * ratio);

			// Draw the outline of the communication radius.
			g.setColor(Color.ORANGE);
			g.drawOval(x_s - cr_s, y_s - cr_s, 2 * cr_s, 2 * cr_s);

			// Draw the outline of the attraction radius.
			g.setColor(Color.GREEN);
			g.drawOval(x_s - ar_s, y_s - ar_s, 2 * ar_s, 2 * ar_s);

			// Draw the outline of the repulsion radius.
			g.setColor(Color.RED);
			g.drawOval(x_s - rr_s, y_s - rr_s, 2 * rr_s, 2 * rr_s);
		}
	}
}
