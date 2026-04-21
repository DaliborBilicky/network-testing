package network.testing.app.events.listeners;

import java.util.List;

import network.testing.domain.model.dto.ExperimentSummary;

public interface ExperimentListListener {
	void onExperimentListUpdated(List<ExperimentSummary> list);

	void onExperimentSelected(long id);
}
