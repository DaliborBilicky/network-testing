package network.testing.core.model.result;

public record SolutionSnapshot(
		double k,
		double objective,
		MedianResult medians,
		DeclineStats declines) {
}
