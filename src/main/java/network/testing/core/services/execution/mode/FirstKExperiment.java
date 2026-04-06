package network.testing.core.services.execution.mode;

import java.util.Arrays;

import network.testing.core.algorithms.EdgeElongator;
import network.testing.core.algorithms.WeightedPMedianProblem;
import network.testing.core.utils.MetricsUtils;
import network.testing.domain.model.DistanceMatrix;
import network.testing.domain.model.Network;
import network.testing.domain.model.result.FirstKResult;
import network.testing.domain.model.result.SolutionSnapshot;

public class FirstKExperiment extends Experiment<FirstKResult> {
	private TrialResult baseTrial;
	private TrialResult changeTrial;
	private double kAtChange;

	public FirstKExperiment(Network network, int p, DistanceMatrix baseDistMatrix,
			EdgeElongator elongator, double baseSpeed, double precision) {
		super(network, p, baseDistMatrix, elongator, baseSpeed, precision);
		this.kAtChange = 0.0;
	}

	@Override
	public FirstKResult run() {
		WeightedPMedianProblem problem0 = new WeightedPMedianProblem(this.network.copyVertexWeights(),
				this.baseDistMatrix, this.p);

		if (!problem0.solve())
			return null;

		this.baseTrial = new TrialResult(problem0, this.baseDistMatrix, this.network.copyEdgeCosts());

		this.performBinarySearch();

		return this.wrapFinalResult();
	}

	private void performBinarySearch() {
		int[] medians0 = this.baseTrial.problem().getSelectedMedians();
		double currentK = 0.0;
		double step = this.elongator.getKLim() / 2.0;

		while (step >= this.precision) {
			double probeK = currentK + step;
			if (probeK > this.elongator.getKLim()) {
				step /= 2.0;
				continue;
			}

			TrialResult probeTrial = this.solveForK(probeK);
			if (this.isSolutionChanged(medians0, probeTrial)) {
				this.kAtChange = probeK;
				this.changeTrial = probeTrial;
				step /= 2.0;
			} else {
				currentK = probeK;
			}
		}
	}

	private boolean isSolutionChanged(int[] originalMedians, TrialResult probe) {
		if (probe == null)
			return false;
		return !Arrays.equals(originalMedians, probe.problem().getSelectedMedians());
	}

	private FirstKResult wrapFinalResult() {
		SolutionSnapshot snap0 = this.createSnapshot(0.0, this.baseTrial);
		SolutionSnapshot snapK = null;
		double objNewOnOrig = 0.0;
		double objOrigOnNew = 0.0;

		if (this.changeTrial != null) {
			snapK = this.createSnapshot(this.kAtChange, this.changeTrial);

			objNewOnOrig = this.calculateObjective(
					this.changeTrial.problem().getAssignments(), this.baseDistMatrix);
			objOrigOnNew = this.calculateObjective(
					this.baseTrial.problem().getAssignments(), this.changeTrial.distMatrix());
		}

		return new FirstKResult(
				this.p,
				this.kAtChange,
				this.elongator.getKLim(),
				MetricsUtils.calculateIrregularity(this.network.copyVertexWeights()),
				snap0,
				snapK,
				objNewOnOrig,
				objOrigOnNew);
	}
}
