package network.testing.ui.component;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import network.testing.domain.model.result.SnapshotData;
import network.testing.ui.render.VisualizationService;

public class StatsPanel extends JPanel {
	private final DefaultTableModel model;
	private final JTable table;

	public StatsPanel(String title) {
		this.model = new DefaultTableModel(new String[] { "Metric", "Value" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		this.table = new JTable(this.model);
		this.table.setFillsViewportHeight(true);
		this.table.setRowHeight(25);
		this.table.getTableHeader().setReorderingAllowed(false);

		this.initComponents(title);
	}

	private void initComponents(String title) {
		this.setLayout(new BorderLayout());
		this.setBorder(new TitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), title));
		this.setMinimumSize(new Dimension(0, 0));

		JScrollPane scrollPane = new JScrollPane(this.table);
		this.add(scrollPane, BorderLayout.CENTER);
	}

	public void onStepSelected(SnapshotData snapshot) {
		if (snapshot == null) {
			this.clearData();
			return;
		}

		String[][] stats = VisualizationService.prepareStats(snapshot);

		this.updateTable(stats);
	}

	private void updateTable(String[][] stats) {
		this.model.setRowCount(0);
		for (String[] row : stats)
			this.model.addRow(row);
	}

	private void clearData() {
		this.model.setRowCount(0);
	}
}
