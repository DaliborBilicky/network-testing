package network.testing.domain.model.result;

public record SolutionSnapshot(
		double k,
		double objective,
		MedianResult medians,
		DeclineStats declines) {
}
