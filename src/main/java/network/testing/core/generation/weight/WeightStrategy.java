package network.testing.core.generation.weight;

public enum WeightStrategy {
	REALISTIC("Realistic Population"),
	UNIFORMITY("Uniformity Modifier"),
	SUPER_VERTEX("SuperVertex Modifier");

	private final String label;

	WeightStrategy(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return this.label;
	}
}
