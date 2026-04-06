package network.testing.domain.model.result;

import java.util.List;

public record AllKResult(
		int p,
		double kLim,
		double graphIrregularity,
		List<SolutionSnapshot> snapshots) implements KResult {
}
