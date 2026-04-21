package network.testing.persistence.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Path;

public class DatabaseManager implements AutoCloseable {
	private Connection connection;
	private final Path dbPath;

	public DatabaseManager(Path dbPath) {
		this.dbPath = dbPath;
	}

	public void open() throws SQLException {
		if (this.connection != null && !this.connection.isClosed())
			return;

		String url = "jdbc:sqlite:" + this.dbPath.toAbsolutePath();
		this.connection = DriverManager.getConnection(url);

		try (Statement statement = connection.createStatement()) {
			statement.execute("PRAGMA foreign_keys = ON;");
			statement.execute("PRAGMA journal_mode = WAL;");
		}
		this.initSchema();
	}

	private void initSchema() throws SQLException {
		String experimentsTable = """
				    CREATE TABLE IF NOT EXISTS experiments (
				        id INTEGER PRIMARY KEY AUTOINCREMENT,
				        name TEXT NOT NULL,
				        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
				        type TEXT CHECK(type IN ('FIRST_K', 'ALL_K')),
				        precision REAL,
				        base_speed REAL
				    );
				""";

		String resultsTable = """
				    CREATE TABLE IF NOT EXISTS results (
				        id INTEGER PRIMARY KEY AUTOINCREMENT,
				        experiment_id INTEGER NOT NULL,
				        p INTEGER NOT NULL,
				        obj_new_on_orig REAL,
				        obj_orig_on_new REAL,
				        FOREIGN KEY (experiment_id) REFERENCES experiments(id) ON DELETE CASCADE
				    );
				""";

		String snapshotsTable = """
				    CREATE TABLE IF NOT EXISTS snapshots (
				        id INTEGER PRIMARY KEY AUTOINCREMENT,
				        result_id INTEGER NOT NULL,
				        sort_order INTEGER,
				        k REAL,
				        objective REAL,
				        median_sum INTEGER,
				        median_irregularity REAL,
				        median_ids TEXT,
				        decline_min REAL,
				        decline_max REAL,
				        decline_avg REAL,
				        decline_mode REAL,
				        FOREIGN KEY (result_id) REFERENCES results(id) ON DELETE CASCADE
				    );
				""";

		String sessionTable = """
				    CREATE TABLE IF NOT EXISTS session (
				        id INTEGER PRIMARY KEY CHECK (id = 1),
				        last_experiment_id INTEGER,
				        last_snapshot_id INTEGER,
				        -- PRIDANÉ: ON DELETE SET NULL
				        FOREIGN KEY (last_experiment_id) REFERENCES experiments(id) ON DELETE SET NULL,
				        FOREIGN KEY (last_snapshot_id) REFERENCES snapshots(id) ON DELETE SET NULL
				    );
				""";

		try (Statement statement = this.connection.createStatement()) {
			statement.execute(experimentsTable);
			statement.execute(resultsTable);
			statement.execute(snapshotsTable);
			statement.execute(sessionTable);

			statement.execute("INSERT OR IGNORE INTO session (id) VALUES (1);");
		}
	}

	public Connection getConnection() {
		if (this.connection == null)
			throw new IllegalStateException("Database not opened.");
		return this.connection;
	}

	@Override
	public void close() throws SQLException {
		if (this.connection != null)
			this.connection.close();
	}
}
