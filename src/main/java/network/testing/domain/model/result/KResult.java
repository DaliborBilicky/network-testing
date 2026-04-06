package network.testing.domain.model.result;

import java.util.List;

public interface KResult {
	int p();

	List<SolutionSnapshot> snapshots();

	default SolutionSnapshot baseline() {
		return snapshots().isEmpty() ? null : snapshots().get(0);
	}
}
