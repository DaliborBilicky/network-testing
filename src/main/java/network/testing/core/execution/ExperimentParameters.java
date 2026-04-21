package network.testing.core.execution;

public record ExperimentParameters(
		String name,
		int pMin,
		int pMax,
		ExperimentType type,
		double precision,
		double baseSpeed) {
}
