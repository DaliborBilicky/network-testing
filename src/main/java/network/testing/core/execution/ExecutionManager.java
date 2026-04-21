package network.testing.core.execution;

import java.util.concurrent.atomic.AtomicReference;
import network.testing.app.ProjectContext;
import network.testing.app.events.EventHub;
import network.testing.app.events.NotificationType;
import network.testing.persistence.database.ResultRepository;

public class ExecutionManager {
	private final AtomicReference<Thread> currentThread;
	private final EventHub eventHub;

	public ExecutionManager(EventHub eventHub) {
		this.currentThread = new AtomicReference<>();
		this.eventHub = eventHub;
	}

	public void start(ProjectContext context, ExperimentParameters params, String name, ResultRepository repo) {
		if (this.isBusy())
			throw new IllegalStateException("An experiment is already running.");

		try {
			long experimentId = repo.createExperiment(
					name, params.type().name(), params.precision(), params.baseSpeed());

			ExperimentTask task = new ExperimentTask(
					context,
					params,
					repo,
					experimentId,
					this.eventHub::publishLog,
					this.eventHub::publishProgress,
					this::handleExecutionCleanup);

			this.launchVirtualThread(task, name);

		} catch (Exception e) {
			this.eventHub.publishNotification("Initialization failed: " + e.getMessage(), NotificationType.ERROR);
		}
	}

	public void cancel() {
		Thread thread = this.currentThread.get();
		if (thread != null)
			thread.interrupt();
	}

	public boolean isBusy() {
		return this.currentThread.get() != null;
	}

	private void launchVirtualThread(ExperimentTask task, String name) {
		Thread thread = Thread.ofVirtual().unstarted(() -> {
			try {
				this.eventHub.publishExperimentState(true);
				task.run();
				this.eventHub.publishNotification("Experiment '" + name + "' finished.", NotificationType.SUCCESS);
			} catch (Exception e) {
				this.eventHub.publishNotification("Experiment failed: " + e.getMessage(), NotificationType.ERROR);
			}
		});

		this.currentThread.set(thread);
		thread.start();
	}

	private void handleExecutionCleanup() {
		this.currentThread.set(null);
		this.eventHub.publishExperimentState(false);
	}
}
