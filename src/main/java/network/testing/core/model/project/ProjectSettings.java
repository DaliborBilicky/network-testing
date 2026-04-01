package network.testing.core.model.project;

public record ProjectSettings(
		String projectName,
		String vertexPath,
		String edgePath,
		String coordPath,
		double baseSpeed,
		String lastResultsPath) {
	public ProjectSettings withResults(String newResultsPath) {
		return new ProjectSettings(projectName, vertexPath, edgePath, coordPath, baseSpeed, newResultsPath);
	}
}
