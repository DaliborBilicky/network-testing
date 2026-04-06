import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class NetworkRobustnessUI extends JFrame {

	private JButton btnRun, btnSettings;
	private JProgressBar progressBar;
	private JTextArea logArea;
	private boolean isRunning = false;

	public NetworkRobustnessUI() {
		// Nastavenie systémového vzhľadu (Standard Light Theme)
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
		}

		setTitle("Network Robustness Pro");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1400, 900);
		setLocationRelativeTo(null);

		// Menu bar (Štandardný svetlý)
		setJMenuBar(createMenuBar());

		// --- LAVA STRANA: Timeline (Standard Tree) ---
		JTree tree = new JTree();
		JPanel leftTimeline = createTitledPanel("Timeline (Results)", new JScrollPane(tree));
		leftTimeline.setPreferredSize(new Dimension(250, 0));

		// --- PRAVA STRANA: Stats + Execution ---
		JPanel statsContainer = new JPanel(new GridLayout(2, 1, 0, 5));
		statsContainer.add(createTitledPanel("Current Snapshot", createStatsTable()));
		statsContainer.add(createTitledPanel("Previous Snapshot", createStatsTable()));

		JPanel executionPanel = createExecutionPanel();

		// Vertikálny split vpravo (Stats hore, Experiment dole)
		JSplitPane rightSplit = createSplit(JSplitPane.VERTICAL_SPLIT, statsContainer, executionPanel, 0.6);
		rightSplit.setPreferredSize(new Dimension(350, 0));
		rightSplit.setMinimumSize(new Dimension(0, 0));

		// --- STRED: Biela Mapa (Zostáva biela) ---
		JPanel canvasMock = new JPanel();
		canvasMock.setMinimumSize(new Dimension(0, 0));

		// --- GLOBÁLNE POSPÁJANIE (SPLIT PANES) ---
		// Stred + Pravý panel
		JSplitPane centerRightSplit = createSplit(JSplitPane.HORIZONTAL_SPLIT, canvasMock, rightSplit, 0.75);

		// Lavý panel + (Stred + Pravý panel)
		JSplitPane globalSplit = createSplit(JSplitPane.HORIZONTAL_SPLIT, leftTimeline, centerRightSplit, 0.18);

		add(globalSplit, BorderLayout.CENTER);
	}

	private JPanel createExecutionPanel() {
		JPanel main = new JPanel(new BorderLayout(5, 5));
		main.setBorder(new TitledBorder("Experiment Execution"));
		main.setMinimumSize(new Dimension(0, 0));

		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);

		logArea = new JTextArea();
		logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
		logArea.setEditable(false);
		JScrollPane logScroll = new JScrollPane(logArea);

		JPanel btnPanel = new JPanel(new BorderLayout());
		btnSettings = new JButton("Settings");
		btnRun = new JButton("RUN");
		btnRun.setFont(new Font(btnRun.getFont().getName(), Font.BOLD, 11));

		btnSettings.addActionListener(e -> openSettingsDialog());
		btnRun.addActionListener(e -> toggleExperiment());

		btnPanel.add(btnSettings, BorderLayout.WEST);
		btnPanel.add(btnRun, BorderLayout.EAST);

		main.add(progressBar, BorderLayout.NORTH);
		main.add(logScroll, BorderLayout.CENTER);
		main.add(btnPanel, BorderLayout.SOUTH);

		return main;
	}

	private void toggleExperiment() {
		isRunning = !isRunning;
		if (isRunning) {
			btnRun.setText("STOP");
			btnRun.setForeground(Color.RED);
			btnSettings.setEnabled(false);
			logArea.append("[INFO] Calculating...\n");
			progressBar.setIndeterminate(true);
		} else {
			btnRun.setText("RUN");
			btnRun.setForeground(Color.BLACK);
			btnSettings.setEnabled(true);
			logArea.append("[INFO] Stopped.\n");
			progressBar.setIndeterminate(false);
			progressBar.setValue(0);
		}
	}

	private JMenuBar createMenuBar() {
		JMenuBar mb = new JMenuBar();

		JMenu file = new JMenu("File");
		file.add(new JMenuItem("New Project"));
		file.add(new JMenuItem("Open Project"));
		file.addSeparator();
		file.add(new JMenuItem("Exit"));

		JMenu tools = new JMenu("Tools");
		tools.add(new JMenuItem("Network Generator"));

		mb.add(file);
		mb.add(tools);
		return mb;
	}

	private JScrollPane createStatsTable() {
		String[] cols = { "Metric", "Value" };
		Object[][] data = { { "Total Weight", "1500" }, { "Medians", "5" }, { "Avg Dist", "12.4" } };
		JTable table = new JTable(new DefaultTableModel(data, cols));
		table.setFillsViewportHeight(true);
		return new JScrollPane(table);
	}

	private JPanel createTitledPanel(String title, JComponent content) {
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(new TitledBorder(title));
		p.add(content, BorderLayout.CENTER);
		p.setMinimumSize(new Dimension(0, 0));
		return p;
	}

	private JSplitPane createSplit(int orientation, Component first, Component second, double weight) {
		JSplitPane split = new JSplitPane(orientation, first, second);
		split.setContinuousLayout(true);
		split.setOneTouchExpandable(true);
		split.setResizeWeight(weight);
		split.setDividerSize(8);
		split.setBorder(null);
		return split;
	}

	private void openSettingsDialog() {
		JDialog d = new JDialog(this, "Settings", true);
		d.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);

		d.add(new JLabel("Experiment Parameters:"), gbc);
		gbc.gridy = 1;
		JButton close = new JButton("Close");
		close.addActionListener(e -> d.dispose());
		d.add(close, gbc);

		JButton save = new JButton("Save");
		save.addActionListener(e -> d.dispose());
		d.add(save, gbc);

		d.pack();
		d.setSize(300, 200);
		d.setLocationRelativeTo(this);
		d.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new NetworkRobustnessUI().setVisible(true);
		});
	}
}
