package network.testing.core.execution.engine;

import network.testing.core.algorithms.WeightedPMedianProblem;
import network.testing.domain.model.network.DistanceMatrix;

public record SolverResult(
		double k,
		WeightedPMedianProblem problem,
		DistanceMatrix distMatrix,
		double[] costs) {
}
