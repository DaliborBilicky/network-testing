package network.testing.domain.model.result;

public record MedianResult(
		int[] ids,
		int[] weights,
		int sum,
		double irregularity) {
	public MedianResult {
		ids = (ids != null) ? ids.clone() : new int[0];
		weights = (weights != null) ? weights.clone() : new int[0];
	}
}
