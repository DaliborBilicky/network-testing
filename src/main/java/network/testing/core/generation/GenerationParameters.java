package network.testing.core.generation;

import network.testing.core.generation.topology.TopologyType;
import network.testing.core.generation.weight.WeightStrategy;

public record GenerationParameters(
		int gridSide,
		TopologyType topologyType,
		double extraEdgeRatio,
		int totalPopulation,
		double cityChance,
		WeightStrategy weightStrategy,
		int modifierTopN,
		String seed) {
}
