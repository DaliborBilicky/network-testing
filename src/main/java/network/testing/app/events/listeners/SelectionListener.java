package network.testing.app.events.listeners;

import network.testing.domain.model.result.SnapshotData;

public interface SelectionListener {
	void onStepSelected(long snapshotId, SnapshotData current, SnapshotData previous);
}
