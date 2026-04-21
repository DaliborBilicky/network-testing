package network.testing.core.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import network.testing.app.ProjectContext;
import network.testing.core.execution.engine.PMedianSolver;
import network.testing.core.execution.engine.SnapshotFactory;
import network.testing.core.execution.strategy.AllKStrategy;
import network.testing.core.execution.strategy.ExperimentStrategy;
import network.testing.core.execution.strategy.FirstKStrategy;
import network.testing.domain.model.result.SnapshotData;
import network.testing.persistence.database.ResultRepository;

public class ExperimentTask implements Runnable {
	private final ProjectContext context;
	private final ExperimentParameters params;
	private final ResultRepository repository;
	private final long experimentId;

	private final Consumer<String> log;
	private final BiConsumer<Integer, Integer> progress;
	private final Runnable onFinished;

	public ExperimentTask(ProjectContext context, ExperimentParameters params,
			ResultRepository repository, long experimentId,
			Consumer<String> log, BiConsumer<Integer, Integer> progress, Runnable onFinished) {
		this.context = context;
		this.params = params;
		this.repository = repository;
		this.experimentId = experimentId;
		this.log = log;
		this.progress = progress;
		this.onFinished = onFinished;
	}

	@Override
	public void run() {
		try {
			this.log.accept("Experiment started: " + params.type().getLabel());
			int totalSteps = this.params.pMax() - this.params.pMin() + 1;

			SnapshotFactory factory = new SnapshotFactory(this.context.network(), this.params.baseSpeed());
			ExperimentStrategy strategy = this.createStrategy();

			for (int p = this.params.pMin(); p <= this.params.pMax(); p++) {
				if (Thread.interrupted())
					throw new InterruptedException();

				this.log.accept("Computing for p = " + p);
				long resultId = this.repository.createResult(this.experimentId, p);

				PMedianSolver solver = new PMedianSolver(this.context, p);

				List<SnapshotData> pResults = new ArrayList<>();

				strategy.execute(solver, factory, snapshot -> {
					pResults.add(snapshot);
				});

				this.log.accept("Saving " + pResults.size() + " snapshots for p = " + p);

				this.repository.insertSnapshots(resultId, pResults);

				int currentProgress = p - this.params.pMin() + 1;
				this.progress.accept(currentProgress, totalSteps);
			}

			this.log.accept("Experiment finished successfully.");
		} catch (InterruptedException e) {
			this.log.accept("Experiment cancelled by user.");
		} catch (Exception e) {
			this.log.accept("Critical Error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			this.onFinished.run();
		}
	}

	private ExperimentStrategy createStrategy() {
		ExperimentType type = this.params.type();

		if (type == ExperimentType.FIRST_K) {
			return new FirstKStrategy(this.params.precision());
		} else if (type == ExperimentType.ALL_K) {
			return new AllKStrategy(this.params.precision());
		}

		throw new IllegalArgumentException("Unknown experiment type: " + type);
	}
}
