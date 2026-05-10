package network.testing.app;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import network.testing.app.events.EventHub;
import network.testing.app.events.NotificationType;
import network.testing.app.events.listeners.ExperimentListListener;
import network.testing.core.execution.ExecutionManager;
import network.testing.core.execution.ExperimentParameters;
import network.testing.domain.model.dto.ExperimentSummary;
import network.testing.domain.model.dto.SnapshotHeader;
import network.testing.domain.model.result.SnapshotData;
import network.testing.persistence.export.ExcelExportService;

public class AppCoordinator {
	private final EventHub eventHub;
	private final ProjectManager projectManager;
	private final ExecutionManager executionManager;

	private long activeExperimentId;
	private double activeBaseSpeed;

	public AppCoordinator() {
		this.eventHub = new EventHub();
		this.projectManager = new ProjectManager();
		this.executionManager = new ExecutionManager(this.eventHub);
		this.activeExperimentId = -1;
		this.activeBaseSpeed = 110.0;
	}

	public EventHub getEventHub() {
		return this.eventHub;
	}

	public void addExperimentListListener(ExperimentListListener l) {
		this.eventHub.addExperimentListListener(l);
		this.refreshArchive();
	}

	public void createProject(Path zipPath, Path vert, Path edge, Path coord) {
		try {
			this.closeProject();
			this.projectManager.create(zipPath, vert, edge, coord);
			this.loadProjectInternal(zipPath);
			this.eventHub.publishNotification("New project created.", NotificationType.SUCCESS);
		} catch (Exception e) {
			this.eventHub.publishNotification("Error creating project: " + e.getMessage(), NotificationType.ERROR);
			e.printStackTrace();
		}
	}

	public void openProject(Path path) {
		try {
			this.loadProjectInternal(path);
			this.eventHub.publishNotification("Project loaded.", NotificationType.SUCCESS);
		} catch (Exception e) {
			this.eventHub.publishNotification("Failed to open project: " + e.getMessage(), NotificationType.ERROR);
		}
	}

	public void runExperiment(ExperimentParameters params) {
		if (!this.projectManager.isLoaded())
			return;
		this.executionManager.start(
				this.projectManager.getContext(), params, params.name(), this.projectManager.getExperimentRepo());
		this.refreshArchive();
	}

	public boolean isExperimentRunning() {
		return this.executionManager.isBusy();
	}

	public void selectExperiment(long id) {
		this.activeExperimentId = id;
		try {
			ExperimentSummary summary = this.projectManager.getExperimentRepo().getExperimentSummary(id);
			if (summary != null)
				this.activeBaseSpeed = summary.baseSpeed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.eventHub.publishExperimentSelected(id);
		this.refreshTimeline();
	}

	public void selectSnapshot(long id) {
		try {
			SnapshotData current = this.projectManager.getExperimentRepo().getSnapshot(id);
			SnapshotData previous = this.projectManager.getExperimentRepo().getPreviousSnapshot(id);

			this.eventHub.publishSelection(id, current, previous);
			this.projectManager.getSessionRepo().saveSession(this.activeExperimentId, id);
		} catch (Exception e) {
			this.eventHub.publishNotification("Error loading data", NotificationType.ERROR);
		}
	}

	public void saveProject() {
		try {
			this.projectManager.save();
			this.eventHub.publishNotification("Archived to NTP.", NotificationType.SUCCESS);
		} catch (Exception e) {
			this.eventHub.publishNotification("Save failed", NotificationType.ERROR);
		}
	}

	public void closeProject() {
		try {
			if (this.projectManager.isLoaded()) {
				this.projectManager.save();
				this.projectManager.close();

				this.activeExperimentId = -1;
				this.eventHub.publishExperimentList(new ArrayList<>());
				this.eventHub.publishDataUpdate(new LinkedHashMap<>());
				this.eventHub.publishSelection(-1, null, null);
				this.eventHub.publishNotification("Project closed.", NotificationType.SUCCESS);
			}
		} catch (Exception e) {
			System.err.println("Critical error during close: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void deleteExperiment(long id) {
		try {
			if (this.activeExperimentId == id) {
				this.activeExperimentId = -1;
				this.eventHub.publishDataUpdate(new java.util.LinkedHashMap<>());
				this.projectManager.getSessionRepo().saveSession(null, null);
			}

			this.projectManager.getExperimentRepo().deleteExperiment(id);

			this.eventHub.publishNotification("Experiment deleted.", NotificationType.SUCCESS);
			this.refreshArchive();

		} catch (Exception e) {
			this.eventHub.publishNotification("Error during deletion: " + e.getMessage(), NotificationType.ERROR);
			e.printStackTrace();
		}
	}

	public void exportToExcel(long id, Path path) {
		try {
			ExperimentSummary info = this.projectManager.getExperimentRepo().getExperimentSummary(id);
			Map<Integer, List<SnapshotData>> data = this.projectManager.getExperimentRepo().getFullExperimentData(id);

			if (info != null) {
				ExcelExportService.exportExperiment(info, data, this.getContext(), path);
				this.eventHub.publishNotification("Export successful.", NotificationType.SUCCESS);
			}
		} catch (Exception e) {
			this.eventHub.publishNotification("Excel export failed: " + e.getMessage(), NotificationType.ERROR);
		}
	}

	public void refreshArchive() {
		try {
			if (this.projectManager.isLoaded()) {
				List<ExperimentSummary> list = this.projectManager.getExperimentRepo().getAllExperiments();
				this.eventHub.publishExperimentList(list);
			}
		} catch (Exception e) {
			this.eventHub.publishNotification("Failed to load archive.", NotificationType.ERROR);
		}
	}

	public ProjectContext getContext() {
		return this.projectManager.getContext();
	}

	public double getActiveBaseSpeed() {
		return this.activeBaseSpeed;
	}

	public void stopExperiment() {
		this.executionManager.cancel();
	}

	public long getActiveExperimentId() {
		return this.activeExperimentId;
	}

	private void loadProjectInternal(Path path) throws Exception {
		this.projectManager.load(path);
		this.eventHub.publishProjectReady(this.projectManager.getProjectName());
		this.refreshArchive();
		this.loadLastSession();
	}

	private void refreshTimeline() {
		try {
			Map<Integer, List<SnapshotHeader>> tree = this.projectManager.getExperimentRepo()
					.getExperimentTree(this.activeExperimentId);
			this.eventHub.publishDataUpdate(tree);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadLastSession() throws Exception {
		ProjectSession session = this.projectManager.getSessionRepo().loadSession();
		if (session.experimentId() != null) {
			this.selectExperiment(session.experimentId());
			if (session.snapshotId() != null)
				this.selectSnapshot(session.snapshotId());
		}
	}
}
