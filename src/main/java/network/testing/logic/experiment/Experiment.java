package network.testing.logic.experiment;

import network.testing.core.model.DistanceMatrix;
import network.testing.core.model.Network;
import network.testing.core.model.result.DeclineStats;
import network.testing.core.model.result.MedianResult;
import network.testing.core.model.result.SolutionSnapshot;
import network.testing.logic.math.MetricsUtils;
import network.testing.logic.pathfinding.Dijkstra;
import network.testing.logic.sensitivity.EdgeElongator;
import network.testing.logic.solver.PMedianProblem;

public abstract class Experiment<T> {
	protected record TrialResult(PMedianProblem problem, DistanceMatrix distMatrix, double[] costs) {
	}

	protected final Network network;
	protected final int p;
	protected final DistanceMatrix baseDistMatrix;
	protected final EdgeElongator elongator;
	protected final double baseSpeed;

	protected final double precision;

	protected Experiment(Network network, int p, DistanceMatrix baseDistMatrix,
			EdgeElongator elongator, double baseSpeed, double precision) {
		this.network = network;
		this.p = p;
		this.baseDistMatrix = baseDistMatrix;
		this.elongator = elongator;
		this.baseSpeed = baseSpeed;
		this.precision = precision;
	}

	public abstract T run();

	protected TrialResult solveForK(double k) {
		double[] costs = this.elongator.getElongatedCosts(k);

		Network netK = new Network(
				this.network.getTopology(),
				costs,
				this.network.copyVertexWeights());

		DistanceMatrix distMatrixForK = Dijkstra.calculateDistanceMatrix(netK);
		PMedianProblem problem = new PMedianProblem(netK.copyVertexWeights(), distMatrixForK, this.p);

		if (problem.solve())
			return new TrialResult(problem, distMatrixForK, costs);
		return null;
	}

	protected SolutionSnapshot createSnapshot(double k, TrialResult trial) {
		int[] ids = trial.problem().getSelectedMedians();
		int[] weights = this.extractMedianWeights(ids);

		double[] declines = MetricsUtils.calculateDeclines(
				this.network.copyEdgeCosts(),
				trial.costs(),
				this.baseSpeed);

		MedianResult medians = new MedianResult(
				ids,
				weights,
				MetricsUtils.calculateSum(weights),
				MetricsUtils.calculateIrregularity(weights));

		DeclineStats stats = new DeclineStats(
				MetricsUtils.min(declines),
				MetricsUtils.max(declines),
				MetricsUtils.avg(declines),
				MetricsUtils.mode(declines));

		return new SolutionSnapshot(k, trial.problem().getObjectiveValue(), medians, stats);
	}

	protected int[] extractMedianWeights(int[] ids) {
		int[] weights = new int[ids.length];
		for (int i = 0; i < ids.length; i++)
			weights[i] = this.network.getVertexWeight(ids[i]);

		return weights;
	}

	protected double calculateObjective(int[] assignments, DistanceMatrix distMatrix) {
		double total = 0;
		for (int i = 0; i < assignments.length; i++) {
			int weight = this.network.getVertexWeight(i);
			if (weight > 0 && assignments[i] != -1)
				total += (double) weight * distMatrix.getDistance(i, assignments[i]);
		}
		return total;
	}
}
