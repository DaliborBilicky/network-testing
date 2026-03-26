package network.testing.ui.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import network.testing.ui.controller.ExperimentController;

public final class ExperimentView extends JPanel {
	private String vertexPath;
	private String edgePath;
	private final JTextArea statusArea;
	private final JSpinner pMin;
	private final JSpinner pMax;
	private final JSpinner speedSpinner;
	private final JSpinner precisionSpinner;
	private final JComboBox<String> typeCombo;
	private final JButton btnRun;
	private final JButton btnStop;
	private final ExperimentController controller;

	public ExperimentView() {
		this.statusArea = new JTextArea();
		this.pMin = new JSpinner(new SpinnerNumberModel(5, 1, 1000, 1));
		this.pMax = new JSpinner(new SpinnerNumberModel(15, 1, 1000, 1));

		this.speedSpinner = new JSpinner(new SpinnerNumberModel(110.0, 1.0, 1000.0, 10.0));
		this.precisionSpinner = new JSpinner(new SpinnerNumberModel(0.01, 0.00001, 1.0, 0.01));

		this.typeCombo = new JComboBox<>(new String[] { "First-K", "All-K" });
		this.btnRun = new JButton("RUN");
		this.btnStop = new JButton("STOP");

		this.controller = new ExperimentController(this);

		this.setLayout(new BorderLayout());
		this.initComponents();
	}

	private void initComponents() {
		this.statusArea.setEditable(false);
		this.statusArea.setFont(new Font("Monospaced", Font.PLAIN, 20));

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(this.createControlPanel());
		splitPane.setRightComponent(new JScrollPane(this.statusArea));
		splitPane.setDividerLocation(260);

		this.add(splitPane, BorderLayout.CENTER);
	}

	private JPanel createControlPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		panel.setMinimumSize(new Dimension(0, 0));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 0, 2, 0);

		panel.add(new JLabel("Edges File:"), gbc);
		gbc.gridy++;
		JTextField fEdge = new JTextField("Select edges...");
		this.setupFilePicker(fEdge, true);
		panel.add(fEdge, gbc);

		gbc.gridy++;
		panel.add(new JLabel("Vertices File:"), gbc);
		gbc.gridy++;
		JTextField fVert = new JTextField("Select vertices...");
		this.setupFilePicker(fVert, false);
		panel.add(fVert, gbc);

		gbc.gridy++;
		panel.add(new JLabel("P Range (Min / Max):"), gbc);
		gbc.gridy++;
		JPanel pPanel = new JPanel(new GridLayout(1, 2, 5, 0));
		pPanel.add(this.pMin);
		pPanel.add(this.pMax);
		panel.add(pPanel, gbc);

		gbc.gridy++;
		panel.add(new JLabel("Base Speed:"), gbc);
		gbc.gridy++;
		panel.add(this.speedSpinner, gbc);

		gbc.gridy++;
		panel.add(new JLabel("K Precision:"), gbc);
		gbc.gridy++;
		panel.add(this.precisionSpinner, gbc);

		gbc.gridy++;
		panel.add(new JLabel("Experiment Type:"), gbc);
		gbc.gridy++;
		panel.add(this.typeCombo, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(20, 0, 5, 0);
		this.btnRun.addActionListener(e -> this.handleRunAction());
		panel.add(this.btnRun, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 5, 0);
		this.btnStop.setEnabled(false);
		this.btnStop.addActionListener(e -> this.controller.stopExperiment());
		panel.add(this.btnStop, gbc);

		gbc.weighty = 1.0;
		gbc.gridy++;
		panel.add(new JLabel(""), gbc);

		return panel;
	}

	private void handleRunAction() {
		if (this.edgePath == null || this.vertexPath == null) {
			this.appendStatus("[!] Error: Select input files.");
			return;
		}

		FileDialog fd = new FileDialog((Frame) null, "Save Results", FileDialog.SAVE);
		fd.setFile("results.json");
		fd.setVisible(true);

		if (fd.getFile() != null) {
			String outputPath = new File(fd.getDirectory(), fd.getFile()).getAbsolutePath();

			this.controller.startExperiment(
					this.vertexPath, this.edgePath, outputPath,
					(int) this.pMin.getValue(), (int) this.pMax.getValue(),
					(String) this.typeCombo.getSelectedItem(),
					(double) this.speedSpinner.getValue(),
					(double) this.precisionSpinner.getValue());
		}
	}

	private void setupFilePicker(JTextField field, boolean isEdge) {
		field.setEditable(false);
		field.setCursor(new Cursor(Cursor.HAND_CURSOR));
		field.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				FileDialog fd = new FileDialog((Frame) null, "Open", FileDialog.LOAD);
				fd.setVisible(true);
				if (fd.getFile() != null) {
					String path = new File(fd.getDirectory(), fd.getFile()).getAbsolutePath();
					if (isEdge)
						ExperimentView.this.edgePath = path;
					else
						ExperimentView.this.vertexPath = path;
					field.setText(fd.getFile());
				}
			}
		});
	}

	public void appendStatus(String message) {
		this.statusArea.append(message + "\n");
		this.statusArea.setCaretPosition(this.statusArea.getDocument().getLength());
	}

	public void setRunning(boolean running) {
		this.btnRun.setEnabled(!running);
		this.btnStop.setEnabled(running);
	}
}
