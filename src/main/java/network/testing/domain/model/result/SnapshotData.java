package network.testing.domain.model.result;

public record SnapshotData(
		double k,
		double objective,

		int[] medianIds,
		int medianSum,
		double medianIrregularity,

		double declineMin,
		double declineMax,
		double declineAvg,
		double declineMode) {
}
