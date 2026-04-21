package network.testing.app.events.listeners;

import java.util.List;
import java.util.Map;

import network.testing.domain.model.dto.SnapshotHeader;

public interface DataUpdateListener {
	void onDataUpdated(Map<Integer, List<SnapshotHeader>> tree);
}
