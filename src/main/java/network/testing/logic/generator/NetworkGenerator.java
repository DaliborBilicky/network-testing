package network.testing.logic.generator;

import java.util.Random;

import network.testing.core.model.Network;
import network.testing.core.model.Topology;
import network.testing.logic.generator.weight.WeightDistributor;

public class NetworkGenerator {
	private final Topology topology;

	public NetworkGenerator(Topology topology) {
		this.topology = topology;
	}

	public Network generate(WeightDistributor weightDist, Random random) {
		int[] weights = weightDist.distribute(this.topology.getNumOfVerts(), random);
		return new Network(this.topology, weights);
	}

	public Topology getTopology() {
		return this.topology;
	}
}
