package network.testing.core.execution.strategy;

import java.util.Arrays;
import java.util.function.Consumer;

import network.testing.core.execution.engine.PMedianSolver;
import network.testing.core.execution.engine.SnapshotFactory;
import network.testing.core.execution.engine.SolverResult;
import network.testing.core.utils.MathUtils;
import network.testing.domain.model.result.SnapshotData;

public class AllKStrategy implements ExperimentStrategy {
	private final double precision;

	public AllKStrategy(double precision) {
		this.precision = precision;
	}

	@Override
	public void execute(PMedianSolver solver, SnapshotFactory factory, Consumer<SnapshotData> collector)
			throws InterruptedException {
		int[] lastMedians = null;
		double kLim = solver.getKLim();
		double k = 0;
		double step = kLim;

		while (!MathUtils.isClose(k, kLim, this.precision)) {
			SolverResult trial = solver.solve(k);

			if (trial != null) {
				int[] currentMedians = trial.problem().getSelectedMedians();

				if (lastMedians == null || !Arrays.equals(lastMedians, currentMedians)) {
					collector.accept(factory.create(trial));
					lastMedians = currentMedians;
				}
			}

			step /= 2.0;
			k += step;
		}
	}
}
