package network.testing.domain.model.project;

public record ProjectSettings(
		String projectName,
		String vertexPath,
		String edgePath,
		String coordPath,
		double baseSpeed,
		String lastResultsPath) {
	public ProjectSettings withResults(String newResultsPath) {
		return new ProjectSettings(
				this.projectName,
				this.vertexPath,
				this.edgePath,
				this.coordPath,
				this.baseSpeed,
				newResultsPath);
	}
}
