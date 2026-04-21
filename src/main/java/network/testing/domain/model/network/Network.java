package network.testing.domain.model.network;

import java.util.Arrays;

public class Network {
	private final Topology topology;
	private final double[] edgeCosts;
	private final int[] vertexWeights;

	public Network(Topology topology, double[] costs, int[] weights) {
		this.topology = topology;

		this.edgeCosts = costs.clone();
		this.vertexWeights = weights.clone();
	}

	public Network(Topology topology, int[] weights) {
		this.topology = topology;
		this.vertexWeights = weights.clone();

		this.edgeCosts = new double[topology.getNumOfEdges()];
		Arrays.fill(this.edgeCosts, 1.0);
	}

	public Network(Topology topology, double[] costs) {
		this.topology = topology;

		this.edgeCosts = costs.clone();
		this.vertexWeights = null;
	}

	public Topology getTopology() {
		return this.topology;
	}

	public double getEdgeCost(int i) {
		return this.edgeCosts[i];
	}

	public int getVertexWeight(int i) {
		return this.vertexWeights[i];
	}

	public int[] copyVertexWeights() {
		return this.vertexWeights.clone();
	}

	public double[] copyEdgeCosts() {
		return this.edgeCosts.clone();
	}
}
