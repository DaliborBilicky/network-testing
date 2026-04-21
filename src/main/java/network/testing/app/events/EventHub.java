package network.testing.app.events;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import network.testing.app.events.listeners.DataUpdateListener;
import network.testing.app.events.listeners.ExperimentListListener;
import network.testing.app.events.listeners.ExperimentProgressListener;
import network.testing.app.events.listeners.NotificationListener;
import network.testing.app.events.listeners.ProjectStateListener;
import network.testing.app.events.listeners.SelectionListener;
import network.testing.domain.model.dto.ExperimentSummary;
import network.testing.domain.model.dto.SnapshotHeader;
import network.testing.domain.model.result.SnapshotData;

public class EventHub {
	private final List<ProjectStateListener> projectListeners;
	private final List<DataUpdateListener> dataListeners;
	private final List<SelectionListener> selectionListeners;
	private final List<ExperimentProgressListener> experimentListeners;
	private final List<NotificationListener> notifyListeners;
	private final List<ExperimentListListener> experimentListListeners;

	public EventHub() {
		this.projectListeners = new CopyOnWriteArrayList<>();
		this.dataListeners = new CopyOnWriteArrayList<>();
		this.selectionListeners = new CopyOnWriteArrayList<>();
		this.experimentListeners = new CopyOnWriteArrayList<>();
		this.notifyListeners = new CopyOnWriteArrayList<>();
		this.experimentListListeners = new CopyOnWriteArrayList<>();
	}

	public void addProjectListener(ProjectStateListener l) {
		this.projectListeners.add(l);
	}

	public void addDataListener(DataUpdateListener l) {
		this.dataListeners.add(l);
	}

	public void addSelectionListener(SelectionListener l) {
		this.selectionListeners.add(l);
	}

	public void addExperimentListener(ExperimentProgressListener l) {
		this.experimentListeners.add(l);
	}

	public void addNotificationListener(NotificationListener l) {
		this.notifyListeners.add(l);
	}

	public void addExperimentListListener(ExperimentListListener l) {
		this.experimentListListeners.add(l);
	}

	public void publishExperimentSelected(long id) {
		this.experimentListListeners.forEach(l -> l.onExperimentSelected(id));
	}

	public void publishExperimentList(List<ExperimentSummary> list) {
		this.experimentListListeners.forEach(l -> l.onExperimentListUpdated(list));
	}

	public void publishProjectReady(String name) {
		this.projectListeners.forEach(l -> l.onProjectReady(name));
	}

	public void publishDataUpdate(Map<Integer, List<SnapshotHeader>> tree) {
		this.dataListeners.forEach(l -> l.onDataUpdated(tree));
	}

	public void publishSelection(long id, SnapshotData current, SnapshotData previous) {
		this.selectionListeners.forEach(l -> l.onStepSelected(id, current, previous));
	}

	public void publishNotification(String msg, NotificationType type) {
		this.notifyListeners.forEach(l -> l.onNotify(msg, type));
	}

	public void publishExperimentState(boolean running) {
		this.experimentListeners.forEach(l -> l.onExperimentStateChanged(running));
	}

	public void publishLog(String msg) {
		this.experimentListeners.forEach(l -> l.onLog(msg));
	}

	public void publishProgress(int currentP, int totalP) {
		this.experimentListeners.forEach(l -> l.onProgress(currentP, totalP));
	}
}
