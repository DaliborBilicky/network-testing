package network.testing.core.execution.strategy;

import java.util.function.Consumer;

import network.testing.core.execution.engine.PMedianSolver;
import network.testing.core.execution.engine.SnapshotFactory;
import network.testing.domain.model.result.SnapshotData;

public interface ExperimentStrategy {
	void execute(PMedianSolver solver, SnapshotFactory factory, Consumer<SnapshotData> collector)
			throws InterruptedException;
}
