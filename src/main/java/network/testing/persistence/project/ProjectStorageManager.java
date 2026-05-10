package network.testing.persistence.project;

import java.io.IOException;
import java.nio.file.Path;

public class ProjectStorageManager {
	private Path activeWorkspace;
	private Path originalZipPath;

	public ProjectStorageManager() {
		this.activeWorkspace = ProjectFileService.prepareWorkspace();
	}

	private void ensureWorkspace() {
		if (this.activeWorkspace == null)
			this.activeWorkspace = ProjectFileService.prepareWorkspace();
	}

	public void create(Path zipPath, Path srcVert, Path srcEdge, Path srcCoord) throws IOException {
		this.ensureWorkspace();
		this.originalZipPath = zipPath;
		ProjectFileService.createProject(srcVert, srcEdge, srcCoord, this.activeWorkspace);
	}

	public void open(Path zipPath) throws IOException {
		this.ensureWorkspace();
		this.originalZipPath = zipPath;
		ProjectFileService.extractProject(this.originalZipPath, this.activeWorkspace);
	}

	public void save() throws IOException {
		if (this.activeWorkspace != null && this.originalZipPath != null)
			ProjectFileService.archiveProject(this.activeWorkspace, this.originalZipPath);
	}

	public void close() throws IOException {
		if (this.activeWorkspace != null) {
			ProjectFileService.deleteDirectory(this.activeWorkspace);
			this.activeWorkspace = null;
			this.originalZipPath = null;
		}
	}

	public String getProjectFileName() {
		return this.originalZipPath != null ? this.originalZipPath.getFileName().toString() : "No Project";
	}

	public Path getDbPath() {
		return this.resolve("project.db");
	}

	public Path getVerticesPath() {
		return this.resolve("vertices.txt");
	}

	public Path getEdgesPath() {
		return this.resolve("edges.txt");
	}

	public Path getCoordsPath() {
		return this.resolve("coords.txt");
	}

	private Path resolve(String fileName) {
		return this.activeWorkspace != null ? this.activeWorkspace.resolve(fileName) : null;
	}
}
