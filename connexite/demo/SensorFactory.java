import org.graphstream.graph.Graph;
import org.graphstream.graph.NodeFactory;

public class SensorFactory implements NodeFactory {

	private Simulation sim;

	public SensorFactory(Simulation sim) {

		this.sim = sim;
	}

	public Sensor newInstance(String id, Graph graph) {

		return new Sensor(graph, id, sim);
	}

}
