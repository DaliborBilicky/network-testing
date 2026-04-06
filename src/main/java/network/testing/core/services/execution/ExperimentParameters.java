package network.testing.core.services.execution;

public record ExperimentParameters(
		int pMin,
		int pMax,
		ExperimentType type,
		double precision,
		double baseSpeed,
		String outputPath,
		boolean applyImmediately) {
}
