package network.testing.core.generation;

import java.util.Random;

import network.testing.core.generation.topology.FullGridTopology;
import network.testing.core.generation.topology.RandomizedSpanningModifier;
import network.testing.core.generation.topology.TopologyGenerator;
import network.testing.core.generation.topology.TopologyType;
import network.testing.core.generation.weight.PopulationDistributor;
import network.testing.core.generation.weight.SuperVertexModifier;
import network.testing.core.generation.weight.UniformityModifier;
import network.testing.core.generation.weight.WeightDistributor;
import network.testing.core.generation.weight.WeightStrategy;
import network.testing.domain.model.network.Topology;

public class GeneratorService {
	private static final double K_REALISTIC = 1.5;
	private static final double K_UNIFORMITY = 4.0;

	private int side;
	private Random random;

	public GeneratorService() {
		this.side = 20;
		this.initialize(20, "");
	}

	public void initialize(int side, String seed) {
		this.side = side;
		if (seed == null || seed.trim().isEmpty()) {
			this.random = new Random();
		} else {
			try {
				this.random = new Random(Long.parseLong(seed.trim()));
			} catch (NumberFormatException e) {
				this.random = new Random();
			}
		}
	}

	public Topology generateTopology(TopologyType type, double extraRatio) {
		TopologyGenerator base = new FullGridTopology(this.side);
		TopologyGenerator finalGen = (type == TopologyType.RANDOMIZED_SPANNING)
				? new RandomizedSpanningModifier(base, extraRatio, this.random)
				: base;

		finalGen.generate();
		return new Topology(
				finalGen.getNumOfVerts(),
				finalGen.getNumOfEdges(),
				finalGen.getU(),
				finalGen.getV());
	}

	public int[] generateWeights(int population, double cityChance, WeightStrategy strategy, int topN) {
		int numVerts = this.side * this.side;
		WeightDistributor dist = new PopulationDistributor(population, cityChance, K_REALISTIC);

		switch (strategy) {
			case WeightStrategy.UNIFORMITY ->
				dist = new UniformityModifier(dist, topN, population, K_UNIFORMITY);
			case WeightStrategy.SUPER_VERTEX ->
				dist = new SuperVertexModifier(dist, topN);
			default -> {
			}
		}

		return dist.distribute(numVerts, this.random);
	}
}
