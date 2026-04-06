package network.testing.core.services.generation;

import network.testing.core.services.generation.topology.TopologyType;
import network.testing.core.services.generation.weight.WeightStrategy;

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
