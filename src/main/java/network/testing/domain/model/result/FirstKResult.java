package network.testing.domain.model.result;

import java.util.List;

public record FirstKResult(
		int p,
		double kAtChange,
		double kLim,
		double graphIrregularity,
		SolutionSnapshot original,
		SolutionSnapshot modified,
		double objNewOnOrig,
		double objOrigOnNew) implements KResult {

	@Override
	public List<SolutionSnapshot> snapshots() {
		if (this.modified == null)
			return List.of(this.original);
		return List.of(this.original, this.modified);
	}
}
