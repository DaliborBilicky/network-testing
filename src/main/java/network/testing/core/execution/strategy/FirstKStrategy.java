package network.testing.core.execution.strategy;

import java.util.Arrays;
import java.util.function.Consumer;

import network.testing.core.execution.engine.PMedianSolver;
import network.testing.core.execution.engine.SnapshotFactory;
import network.testing.core.execution.engine.SolverResult;
import network.testing.domain.model.result.SnapshotData;

public class FirstKStrategy implements ExperimentStrategy {
	private final double precision;

	public FirstKStrategy(double precision) {
		this.precision = precision;
	}

	@Override
	public void execute(PMedianSolver solver, SnapshotFactory factory, Consumer<SnapshotData> collector)
			throws InterruptedException {

		SolverResult baseline = solver.solve(0.0);
		if (baseline == null)
			return;

		int[] originalMedians = baseline.problem().getSelectedMedians();
		collector.accept(factory.create(baseline));

		double kLim = solver.getKLim();
		SolverResult limitTrial = solver.solve(kLim);

		if (this.isStableAtLimit(originalMedians, limitTrial))
			return;

		SolverResult firstChange = this.findFirstChangePoint(solver, originalMedians, kLim, limitTrial);

		collector.accept(factory.create(firstChange));
	}

	private boolean isStableAtLimit(int[] originalMedians, SolverResult limitTrial) {
		if (limitTrial == null)
			return true;
		int[] limitMedians = limitTrial.problem().getSelectedMedians();
		return Arrays.equals(originalMedians, limitMedians);
	}

	private SolverResult findFirstChangePoint(PMedianSolver solver, int[] originalMedians,
			double kLim, SolverResult limitTrial) throws InterruptedException {
		double low = 0.0;
		double high = kLim;
		SolverResult bestMatch = limitTrial;

		while ((high - low) > this.precision) {
			double mid = (low + high) / 2.0;
			SolverResult midTrial = solver.solve(mid);

			if (this.isSameSolution(originalMedians, midTrial)) {
				low = mid;
			} else if (midTrial != null) {
				bestMatch = midTrial;
				high = mid;
			}
		}
		return bestMatch;
	}

	private boolean isSameSolution(int[] originalMedians, SolverResult trial) {
		if (trial == null)
			return true;
		return Arrays.equals(originalMedians, trial.problem().getSelectedMedians());
	}
}
