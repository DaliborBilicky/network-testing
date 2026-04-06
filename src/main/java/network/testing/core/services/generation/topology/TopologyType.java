package network.testing.core.services.generation.topology;

public enum TopologyType {
	FULL_GRID("Full Grid"),
	RANDOMIZED_SPANNING("Randomized Spanning");

	private final String label;

	TopologyType(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return this.label;
	}
}
