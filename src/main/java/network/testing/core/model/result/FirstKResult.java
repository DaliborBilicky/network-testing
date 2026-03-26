package network.testing.core.model.result;

public record FirstKResult(
		int p,
		double kAtChange,
		double kLim,
		double graphIrregularity,
		SolutionSnapshot original,
		SolutionSnapshot modified,
		double objNewOnOrig,
		double objOrigOnNew) {
}
