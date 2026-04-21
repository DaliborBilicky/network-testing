package network.testing.ui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import com.formdev.flatlaf.FlatClientProperties;

import network.testing.app.AppCoordinator;
import network.testing.app.events.listeners.ExperimentProgressListener;
import network.testing.app.events.listeners.ProjectStateListener;
import network.testing.core.execution.ExperimentParameters;
import network.testing.core.execution.ExperimentType;
import network.testing.ui.dialog.ExperimentSettingsDialog;

public class ExecutionPanel extends JPanel implements ExperimentProgressListener, ProjectStateListener {
	private final AppCoordinator coordinator;

	private final JProgressBar progressBar;
	private final JTextArea logArea;
	private final JButton btnSettings;
	private final JButton btnRun;

	private ExperimentParameters lastParams;

	public ExecutionPanel(AppCoordinator coordinator) {
		this.coordinator = coordinator;
		this.lastParams = new ExperimentParameters("Default Experiment", 5, 10, ExperimentType.FIRST_K, 0.01, 110.0);

		this.progressBar = new JProgressBar(0, 100);
		this.logArea = new JTextArea();
		this.btnSettings = new JButton("Settings");
		this.btnRun = new JButton("RUN");

		this.initComponents();
		this.setupActions();
	}

	@Override
	public void onLog(String message) {
		SwingUtilities.invokeLater(() -> {
			this.logArea.append(message + "\n");
			this.logArea.setCaretPosition(this.logArea.getDocument().getLength());
		});
	}

	@Override
	public void onProgress(int current, int total) {
		SwingUtilities.invokeLater(() -> {
			this.progressBar.setIndeterminate(false);
			this.progressBar.setMaximum(total);
			this.progressBar.setValue(current);
		});
	}

	@Override
	public void onExperimentStateChanged(boolean running) {
		SwingUtilities.invokeLater(() -> {
			this.updateButtonStyle(running);
			this.btnSettings.setEnabled(!running);
			this.progressBar.setIndeterminate(running && this.progressBar.getValue() == 0);
			if (!running)
				this.progressBar.setValue(0);
		});
	}

	@Override
	public void onProjectReady(String projectName) {
		this.lastParams = new ExperimentParameters(
				"Exp - " + projectName,
				this.lastParams.pMin(),
				this.lastParams.pMax(),
				this.lastParams.type(),
				this.lastParams.precision(),
				this.lastParams.baseSpeed());
	}

	private void initComponents() {
		this.setLayout(new BorderLayout(5, 5));
		this.setBorder(new TitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), "Experiment Execution"));
		this.setMinimumSize(new Dimension(0, 0));

		this.add(this.createProgressSection(), BorderLayout.NORTH);
		this.add(this.createLogSection(), BorderLayout.CENTER);
		this.add(this.createControlsSection(), BorderLayout.SOUTH);
	}

	private JPanel createProgressSection() {
		this.progressBar.setStringPainted(true);
		this.progressBar.putClientProperty(FlatClientProperties.PROGRESS_BAR_SQUARE, true);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.add(this.progressBar, BorderLayout.CENTER);
		return panel;
	}

	private JScrollPane createLogSection() {
		this.logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
		this.logArea.setEditable(false);
		this.logArea.setBackground(new Color(48, 50, 52));

		JScrollPane scroll = new JScrollPane(this.logArea);
		scroll.setBorder(BorderFactory.createLineBorder(new Color(48, 50, 52)));
		return scroll;
	}

	private JPanel createControlsSection() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		this.btnRun.setFont(new Font(this.btnRun.getFont().getName(), Font.BOLD, 12));
		this.updateButtonStyle(false);

		panel.add(this.btnSettings, BorderLayout.WEST);
		panel.add(this.btnRun, BorderLayout.EAST);

		return panel;
	}

	private void setupActions() {
		this.btnSettings.addActionListener(e -> this.handleOpenSettings());
		this.btnRun.addActionListener(e -> this.handleRunStopToggle());
	}

	private void handleOpenSettings() {
		Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
		ExperimentSettingsDialog dialog = new ExperimentSettingsDialog(owner, this.lastParams);
		dialog.setVisible(true);

		if (dialog.getResult() != null)
			this.lastParams = dialog.getResult();
	}

	private void handleRunStopToggle() {
		if (this.btnRun.getText().equals("RUN")) {
			this.logArea.setText("");
			this.coordinator.runExperiment(this.lastParams);
		} else {
			this.handleStopRequest();
		}
	}

	private void handleStopRequest() {
		int confirm = JOptionPane.showConfirmDialog(
				SwingUtilities.getWindowAncestor(this),
				"Stop experiment?",
				"Interrupt",
				JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION)
			this.coordinator.stopExperiment();
	}

	private void updateButtonStyle(boolean running) {
		this.btnRun.setText(running ? "STOP" : "RUN");
		String color = running ? "#C62828" : "#2E7D32";
		this.btnRun.putClientProperty(FlatClientProperties.STYLE, "background: " + color + "; foreground: #FFFFFF");
	}
}
