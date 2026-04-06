package network.testing.core.services.execution;

public enum ExperimentType {
	FIRST_K("First-K Strategy"),
	ALL_K("All-K Strategy");

	private final String label;

	ExperimentType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}
}
