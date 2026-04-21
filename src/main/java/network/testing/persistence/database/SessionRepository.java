package network.testing.persistence.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import network.testing.app.ProjectSession;

public class SessionRepository {
	private final Connection connection;

	public SessionRepository(Connection connection) {
		this.connection = connection;
	}

	public void saveSession(Long experimentId, Long snapshotId) throws SQLException {
		String sql = "UPDATE session SET last_experiment_id = ?, last_snapshot_id = ? WHERE id = 1";
		try (PreparedStatement prepStatement = this.connection.prepareStatement(sql)) {
			if (experimentId != null)
				prepStatement.setLong(1, experimentId);
			else
				prepStatement.setNull(1, Types.INTEGER);
			if (snapshotId != null)
				prepStatement.setLong(2, snapshotId);
			else
				prepStatement.setNull(2, Types.INTEGER);
			prepStatement.executeUpdate();
		}
	}

	public ProjectSession loadSession() throws SQLException {
		String sql = "SELECT last_experiment_id, last_snapshot_id FROM session WHERE id = 1";
		try (ResultSet resultSet = this.connection.createStatement().executeQuery(sql)) {
			if (resultSet.next()) {
				long experimentId = resultSet.getLong("last_experiment_id");
				long snapId = resultSet.getLong("last_snapshot_id");
				return new ProjectSession(
						resultSet.wasNull() ? null : experimentId,
						resultSet.wasNull() ? null : snapId);
			}
		}
		return new ProjectSession(null, null);
	}
}
