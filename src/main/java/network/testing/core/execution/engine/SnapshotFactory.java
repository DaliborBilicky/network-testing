package network.testing.core.execution.engine;

import network.testing.core.utils.NetworkMetrics;
import network.testing.core.utils.StatisticsUtils;
import network.testing.domain.model.network.Network;
import network.testing.domain.model.result.SnapshotData;

public class SnapshotFactory {
	private final Network network;
	private final double baseSpeed;

	public SnapshotFactory(Network network, double baseSpeed) {
		this.network = network;
		this.baseSpeed = baseSpeed;
	}

	public SnapshotData create(SolverResult trial) {
		int[] ids = trial.problem().getSelectedMedians();
		int[] weights = new int[ids.length];
		for (int i = 0; i < ids.length; i++)
			weights[i] = this.network.getVertexWeight(ids[i]);

		double[] declines = NetworkMetrics.calculateDeclines(
				this.network.copyEdgeCosts(), trial.costs(), this.baseSpeed);

		return new SnapshotData(
				trial.k(),
				trial.problem().getObjectiveValue(),
				ids,
				StatisticsUtils.sum(weights),
				NetworkMetrics.calculateIrregularity(weights),
				StatisticsUtils.min(declines),
				StatisticsUtils.max(declines),
				StatisticsUtils.avg(declines),
				StatisticsUtils.mode(declines));
	}
}
