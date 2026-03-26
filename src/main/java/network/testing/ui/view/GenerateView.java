package network.testing.ui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import network.testing.ui.component.InteractiveCanvas;
import network.testing.ui.controller.GenerationController;

public final class GenerateView extends JPanel {
	private final JSpinner sideSpinner;
	private final JSpinner populationSpinner;
	private final JSpinner extraRatioSpinner;
	private final JSpinner cityChanceSpinner;
	private final JSpinner topNSpinner;
	private final JTextField seedField;
	private final JComboBox<String> topoCombo;
	private final JComboBox<String> weightCombo;
	private final InteractiveCanvas canvasArea;
	private final GenerationController controller;

	public GenerateView() {
		this.canvasArea = new InteractiveCanvas();
		this.controller = new GenerationController(this.canvasArea);

		this.sideSpinner = new JSpinner(new SpinnerNumberModel(20, 2, 100, 1));
		this.populationSpinner = new JSpinner(new SpinnerNumberModel(10000, 1, 1000000, 1000));
		this.extraRatioSpinner = new JSpinner(new SpinnerNumberModel(0.2, 0.0, 1.0, 0.05));
		this.cityChanceSpinner = new JSpinner(new SpinnerNumberModel(0.75, 0.0, 1.0, 0.01));
		this.topNSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
		this.seedField = new JTextField();

		this.topoCombo = new JComboBox<>(new String[] { "Full Grid", "Randomized Spanning" });
		this.weightCombo = new JComboBox<>(new String[] { "Realistic", "Uniformity Modifier", "SuperVertex Modifier" });

		this.setLayout(new BorderLayout());
		this.initComponents();
	}

	private void initComponents() {
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(this.createControlPanel());
		splitPane.setRightComponent(this.canvasArea);
		splitPane.setDividerLocation(260);
		splitPane.setOneTouchExpandable(true);

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

		panel.add(new JLabel("Random Seed (Optional):"), gbc);
		gbc.gridy++;
		panel.add(this.seedField, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(15, 0, 2, 0);
		panel.add(this.createSectionLabel("Topology Settings"), gbc);
		gbc.gridy++;
		gbc.insets = new Insets(5, 0, 2, 0);
		panel.add(new JLabel("Grid Side:"), gbc);
		gbc.gridy++;
		panel.add(this.sideSpinner, gbc);
		gbc.gridy++;
		panel.add(new JLabel("Topology Type:"), gbc);
		gbc.gridy++;
		panel.add(this.topoCombo, gbc);
		gbc.gridy++;
		panel.add(new JLabel("Extra Edge Ratio:"), gbc);
		gbc.gridy++;
		panel.add(this.extraRatioSpinner, gbc);
		gbc.gridy++;
		JButton btnGenT = new JButton("GENERATE EDGES");
		btnGenT.addActionListener(e -> this.controller.generateTopology(
				(int) this.sideSpinner.getValue(), (String) this.topoCombo.getSelectedItem(),
				(double) this.extraRatioSpinner.getValue(), this.seedField.getText()));
		panel.add(btnGenT, gbc);
		gbc.gridy++;
		JButton btnSaveE = new JButton("SAVE EDGES");
		btnSaveE.addActionListener(e -> this.handleSave("Edges", true));
		panel.add(btnSaveE, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(20, 0, 2, 0);
		panel.add(this.createSectionLabel("Weight Settings"), gbc);
		gbc.gridy++;
		gbc.insets = new Insets(5, 0, 2, 0);
		panel.add(new JLabel("Target Population:"), gbc);
		gbc.gridy++;
		panel.add(this.populationSpinner, gbc);
		gbc.gridy++;
		panel.add(new JLabel("City Chance:"), gbc);
		gbc.gridy++;
		panel.add(this.cityChanceSpinner, gbc);
		gbc.gridy++;
		panel.add(new JLabel("Strategy:"), gbc);
		gbc.gridy++;
		panel.add(this.weightCombo, gbc);
		gbc.gridy++;
		panel.add(new JLabel("Modifier Top N:"), gbc);
		gbc.gridy++;
		panel.add(this.topNSpinner, gbc);
		gbc.gridy++;
		JButton btnGenW = new JButton("GENERATE VERTICES");
		btnGenW.addActionListener(e -> this.controller.generateWeights(
				(int) this.populationSpinner.getValue(), (double) this.cityChanceSpinner.getValue(),
				(String) this.weightCombo.getSelectedItem(), (int) this.topNSpinner.getValue(),
				this.seedField.getText()));
		panel.add(btnGenW, gbc);
		gbc.gridy++;
		JButton btnSaveV = new JButton("SAVE VERTICES");
		btnSaveV.addActionListener(e -> this.handleSave("Vertices", false));
		panel.add(btnSaveV, gbc);

		gbc.weighty = 1.0;
		gbc.gridy++;
		panel.add(new JLabel(""), gbc);
		return panel;
	}

	private JLabel createSectionLabel(String text) {
		JLabel label = new JLabel(text.toUpperCase());
		label.setFont(new Font("SansSerif", Font.BOLD, 10));
		label.setForeground(new Color(120, 120, 120));
		return label;
	}

	private void handleSave(String type, boolean isEdge) {
		FileDialog fd = new FileDialog((Frame) null, "Save " + type, FileDialog.SAVE);
		fd.setFile(isEdge ? "edges.txt" : "vertices.txt");
		fd.setVisible(true);
		if (fd.getFile() != null) {
			try {
				String path = new File(fd.getDirectory(), fd.getFile()).getAbsolutePath();
				if (isEdge)
					this.controller.saveEdges(path);
				else
					this.controller.saveVertices(path);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Save error: " + ex.getMessage());
			}
		}
	}
}
