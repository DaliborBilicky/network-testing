package network.testing.persistence.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import network.testing.core.utils.ArrayUtils;
import network.testing.domain.model.dto.ExperimentSummary;
import network.testing.domain.model.dto.SnapshotHeader;
import network.testing.domain.model.result.SnapshotData;

public class ResultRepository {
	private final Connection connection;

	public ResultRepository(Connection connection) {
		this.connection = connection;
	}

	public long createExperiment(String name, String type, double precision, double speed) throws SQLException {
		String sql = "INSERT INTO experiments (name, type, precision, base_speed) VALUES (?, ?, ?, ?)";
		try (PreparedStatement prepStatement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			prepStatement.setString(1, name);
			prepStatement.setString(2, type);
			prepStatement.setDouble(3, precision);
			prepStatement.setDouble(4, speed);
			prepStatement.executeUpdate();

			try (ResultSet resultSet = prepStatement.getGeneratedKeys()) {
				if (resultSet.next())
					return resultSet.getLong(1);
			}
		}
		return -1;
	}

	public long createResult(long experimentId, int p) throws SQLException {
		String sql = "INSERT INTO results (experiment_id, p) VALUES (?, ?)";
		try (PreparedStatement prepStatement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			prepStatement.setLong(1, experimentId);
			prepStatement.setInt(2, p);
			prepStatement.executeUpdate();
			try (ResultSet resultSet = prepStatement.getGeneratedKeys()) {
				if (resultSet.next())
					return resultSet.getLong(1);
			}
		}
		return -1;
	}

	public List<ExperimentSummary> getAllExperiments() throws SQLException {
		List<ExperimentSummary> list = new ArrayList<>();
		String sql = "SELECT id, name, created_at, type, base_speed FROM experiments ORDER BY created_at DESC";
		try (ResultSet resultSet = this.connection.createStatement().executeQuery(sql)) {
			while (resultSet.next()) {
				list.add(new ExperimentSummary(
						resultSet.getLong("id"),
						resultSet.getString("name"),
						resultSet.getString("created_at"),
						resultSet.getString("type"),
						resultSet.getDouble("base_speed")));
			}
		}
		return list;
	}

	public SnapshotData getSnapshot(long snapshotId) throws SQLException {
		String sql = "SELECT * FROM snapshots WHERE id = ?";
		try (PreparedStatement prepStatement = this.connection.prepareStatement(sql)) {
			prepStatement.setLong(1, snapshotId);
			try (ResultSet resultSet = prepStatement.executeQuery()) {
				if (resultSet.next()) {
					return this.mapRowToSnapshot(resultSet);
				}
			}
		}
		return null;
	}

	public void insertSnapshots(long resultId, List<SnapshotData> snapshots) throws SQLException {
		String sql = """
				    INSERT INTO snapshots (result_id, sort_order, k, objective, median_sum,
				    median_irregularity, median_ids, decline_min, decline_max, decline_avg, decline_mode)
				    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""";

		boolean autoCommit = this.connection.getAutoCommit();
		this.connection.setAutoCommit(false);

		try (PreparedStatement prepStatement = this.connection.prepareStatement(sql)) {
			int order = 0;
			for (SnapshotData data : snapshots) {
				prepStatement.setLong(1, resultId);
				prepStatement.setInt(2, order++);
				prepStatement.setDouble(3, data.k());
				prepStatement.setDouble(4, data.objective());
				prepStatement.setInt(5, data.medianSum());
				prepStatement.setDouble(6, data.medianIrregularity());
				prepStatement.setString(7, ArrayUtils.intArrayToString(data.medianIds()));
				prepStatement.setDouble(8, data.declineMin());
				prepStatement.setDouble(9, data.declineMax());
				prepStatement.setDouble(10, data.declineAvg());
				prepStatement.setDouble(11, data.declineMode());
				prepStatement.addBatch();
			}
			prepStatement.executeBatch();
			this.connection.commit();
		} catch (SQLException e) {
			this.connection.rollback();
			throw e;
		} finally {
			this.connection.setAutoCommit(autoCommit);
		}
	}

	public Map<Integer, List<SnapshotHeader>> getExperimentTree(long experimentId) throws SQLException {
		Map<Integer, List<SnapshotHeader>> tree = new LinkedHashMap<>();
		String sql = """
				    SELECT r.p, s.id as snap_id, s.k
				    FROM results r
				    JOIN snapshots s ON r.id = s.result_id
				    WHERE r.experiment_id = ?
				    ORDER BY r.p, s.sort_order -- Radenie podľa poradia, nie podľa k
				""";
		try (PreparedStatement prepStatement = this.connection.prepareStatement(sql)) {
			prepStatement.setLong(1, experimentId);
			try (ResultSet resultSet = prepStatement.executeQuery()) {
				while (resultSet.next()) {
					int p = resultSet.getInt("p");
					tree.computeIfAbsent(p, k -> new ArrayList<>())
							.add(new SnapshotHeader(resultSet.getLong("snap_id"), resultSet.getDouble("k")));
				}
			}
		}
		return tree;
	}

	public SnapshotData getPreviousSnapshot(long currentId) throws SQLException {
		String sql = """
				    SELECT * FROM snapshots
				    WHERE result_id = (SELECT result_id FROM snapshots WHERE id = ?)
				      AND sort_order < (SELECT sort_order FROM snapshots WHERE id = ?)
				    ORDER BY sort_order DESC LIMIT 1
				""";
		try (PreparedStatement prepStatement = this.connection.prepareStatement(sql)) {
			prepStatement.setLong(1, currentId);
			prepStatement.setLong(2, currentId);
			try (ResultSet resultSet = prepStatement.executeQuery()) {
				if (resultSet.next())
					return this.mapRowToSnapshot(resultSet);
			}
		}
		return this.getSnapshot(currentId);
	}

	public void deleteExperiment(long experimentId) throws SQLException {
		String sql = "DELETE FROM experiments WHERE id = ?";
		try (PreparedStatement prepStatement = this.connection.prepareStatement(sql)) {
			prepStatement.setLong(1, experimentId);
			prepStatement.executeUpdate();
		}
	}

	public ExperimentSummary getExperimentSummary(long experimentId) throws SQLException {
		String sql = "SELECT id, name, created_at, type, base_speed FROM experiments WHERE id = ?";
		try (PreparedStatement prepStatement = this.connection.prepareStatement(sql)) {
			prepStatement.setLong(1, experimentId);
			try (ResultSet resultSet = prepStatement.executeQuery()) {
				if (resultSet.next()) {
					return new ExperimentSummary(
							resultSet.getLong("id"),
							resultSet.getString("name"),
							resultSet.getString("created_at"),
							resultSet.getString("type"),
							resultSet.getDouble("base_speed"));
				}
			}
		}
		return null;
	}

	public Map<Integer, List<SnapshotData>> getFullExperimentData(long experimentId) throws SQLException {
		Map<Integer, List<SnapshotData>> data = new LinkedHashMap<>();
		String sql = """
				    SELECT r.p, s.*
				    FROM results r
				    JOIN snapshots s ON r.id = s.result_id
				    WHERE r.experiment_id = ?
				    ORDER BY r.p, s.sort_order
				""";
		try (PreparedStatement prepStatement = this.connection.prepareStatement(sql)) {
			prepStatement.setLong(1, experimentId);
			try (ResultSet resultSet = prepStatement.executeQuery()) {
				while (resultSet.next()) {
					int p = resultSet.getInt("p");
					SnapshotData snap = this.mapRowToSnapshot(resultSet);
					data.computeIfAbsent(p, k -> new ArrayList<>()).add(snap);
				}
			}
		}
		return data;
	}

	private SnapshotData mapRowToSnapshot(ResultSet resultSet) throws SQLException {
		return new SnapshotData(
				resultSet.getDouble("k"),
				resultSet.getDouble("objective"),
				ArrayUtils.stringToIntArray(resultSet.getString("median_ids")),
				resultSet.getInt("median_sum"),
				resultSet.getDouble("median_irregularity"),
				resultSet.getDouble("decline_min"),
				resultSet.getDouble("decline_max"),
				resultSet.getDouble("decline_avg"),
				resultSet.getDouble("decline_mode"));
	}
}
