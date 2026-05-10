package network.testing.app;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import network.testing.core.utils.PositionUtils;
import network.testing.domain.model.network.Network;
import network.testing.domain.model.network.Topology;
import network.testing.persistence.database.DatabaseManager;
import network.testing.persistence.database.ResultRepository;
import network.testing.persistence.database.SessionRepository;
import network.testing.persistence.file.NetworkLoader;
import network.testing.persistence.project.ProjectStorageManager;

public class ProjectManager {
	private final ProjectStorageManager storage;
	private DatabaseManager dbManager;
	private ProjectContext context;

	private ResultRepository experimentRepo;
	private SessionRepository sessionRepo;

	public ProjectManager() {
		this.storage = new ProjectStorageManager();
	}

	public void load(Path path) throws Exception {
		if (Files.exists(path)) {
			this.storage.open(path);
		}

		this.dbManager = new DatabaseManager(this.storage.getDbPath());
		this.dbManager.open();

		this.experimentRepo = new ResultRepository(this.dbManager.getConnection());
		this.sessionRepo = new SessionRepository(this.dbManager.getConnection());

		Network network = NetworkLoader.load(this.storage.getVerticesPath(), this.storage.getEdgesPath());
		Point2D[] positions = this.resolvePositions(network.getTopology());
		this.context = ProjectContext.create(network, positions);
	}

	public void create(Path zipPath, Path vert, Path edge, Path coord) throws Exception {
		this.storage.create(zipPath, vert, edge, coord);
		this.load(zipPath);
	}

	public void save() throws Exception {
		this.storage.save();
	}

	public void close() throws Exception {
		if (this.dbManager != null)
			this.dbManager.close();
		this.storage.close();
		this.context = null;
	}

	private Point2D[] resolvePositions(Topology topology) throws IOException {
		Path coords = this.storage.getCoordsPath();
		if (coords != null && Files.exists(coords))
			return PositionUtils.projectGeoCoordinates(NetworkLoader.loadCoordinates(coords, topology.getNumOfVerts()));

		return PositionUtils.computeForceLayout(topology);
	}

	public ProjectContext getContext() {
		return this.context;
	}

	public ResultRepository getExperimentRepo() {
		return this.experimentRepo;
	}

	public SessionRepository getSessionRepo() {
		return this.sessionRepo;
	}

	public String getProjectName() {
		return this.storage.getProjectFileName();
	}

	public boolean isLoaded() {
		return this.context != null;
	}
}
