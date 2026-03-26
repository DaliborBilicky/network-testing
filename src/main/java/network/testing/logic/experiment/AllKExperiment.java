package network.testing.logic.experiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import network.testing.core.model.DistanceMatrix;
import network.testing.core.model.Network;
import network.testing.core.model.result.AllKResult;
import network.testing.core.model.result.SolutionSnapshot;
import network.testing.logic.math.MetricsUtils;
import network.testing.logic.sensitivity.EdgeElongator;

public class AllKExperiment extends Experiment<AllKResult> {
	public AllKExperiment(Network network, int p, DistanceMatrix baseDistMatrix, EdgeElongator elongator,
			double baseSpeed, double precision) {
		super(network, p, baseDistMatrix, elongator, baseSpeed, precision);
	}

	@Override
	public AllKResult run() {
		List<SolutionSnapshot> snapshots = new ArrayList<>();
		int[] lastMedians = null;
		double kLim = this.elongator.getKLim();
		double k = 0;
		double step = kLim;

		while (!MetricsUtils.isClose(k, kLim, this.precision)) {
			TrialResult trial = this.solveForK(k);

			if (trial != null) {
				int[] currentMedians = trial.problem().getSelectedMedians();
				if (lastMedians == null || !Arrays.equals(lastMedians, currentMedians)) {
					snapshots.add(this.createSnapshot(k, trial));
					lastMedians = currentMedians;
				}
			}

			step /= 2.0;
			k += step;
		}

		return new AllKResult(
				this.p,
				this.elongator.getKLim(),
				MetricsUtils.calculateIrregularity(this.network.copyVertexWeights()),
				snapshots);
	}
}
