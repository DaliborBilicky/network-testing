package network.testing.ui.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import network.testing.ui.controller.VisualizationController;

public final class VisualizeView extends JPanel {
	private String resultPath;
	private String edgePath;
	private String vertexPath;
	private String coordPath;

	private final JSpinner pSpinner;
	private final JSpinner speedSpinner;
	private final JComboBox<Double> kCombo;
	private final InteractiveCanvas canvasArea;
	private final VisualizationController controller;

	public VisualizeView() {
		this.canvasArea = new InteractiveCanvas();
		this.controller = new VisualizationController(this.canvasArea);

		this.pSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 1000, 1));
		this.speedSpinner = new JSpinner(new SpinnerNumberModel(110.0, 1.0, 1000.0, 10.0));
		this.kCombo = new JComboBox<>();

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

		panel.add(new JLabel("Result JSON:"), gbc);
		gbc.gridy++;
		JTextField fRes = new JTextField("Select JSON...");
		this.setupFilePicker(fRes, "JSON");
		panel.add(fRes, gbc);

		gbc.gridy++;
		panel.add(new JLabel("Edges File:"), gbc);
		gbc.gridy++;
		JTextField fEdge = new JTextField("Select edges...");
		this.setupFilePicker(fEdge, "Edges");
		panel.add(fEdge, gbc);

		gbc.gridy++;
		panel.add(new JLabel("Vertices File:"), gbc);
		gbc.gridy++;
		JTextField fVert = new JTextField("Select vertices...");
		this.setupFilePicker(fVert, "Vertices");
		panel.add(fVert, gbc);

		gbc.gridy++;
		panel.add(new JLabel("Coordinates (Optional):"), gbc);
		gbc.gridy++;
		JTextField fCoord = new JTextField("Grid Layout (Default)");
		this.setupFilePicker(fCoord, "Coordinates");
		panel.add(fCoord, gbc);

		gbc.gridy++;
		panel.add(new JLabel("P Value:"), gbc);
		gbc.gridy++;
		panel.add(this.pSpinner, gbc);

		gbc.gridy++;
		panel.add(new JLabel("Base Speed:"), gbc);
		gbc.gridy++;
		panel.add(this.speedSpinner, gbc);

		gbc.gridy++;
		panel.add(new JLabel("Select K (Timeline):"), gbc);
		gbc.gridy++;
		this.kCombo.addActionListener(e -> this.handleKSelection());
		panel.add(this.kCombo, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(20, 0, 5, 0);
		JButton btnLoad = new JButton("LOAD RESULTS");
		btnLoad.addActionListener(e -> this.handleLoadAction());
		panel.add(btnLoad, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 5, 0);
		JButton btnSave = new JButton("SAVE IMAGE");
		btnSave.addActionListener(e -> this.handleSaveImageAction());
		panel.add(btnSave, gbc);

		gbc.weighty = 1.0;
		gbc.gridy++;
		panel.add(new JLabel(""), gbc);

		return panel;
	}

	private void setupFilePicker(JTextField field, String type) {
		field.setEditable(false);
		field.setCursor(new Cursor(Cursor.HAND_CURSOR));
		field.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				FileDialog fd = new FileDialog((Frame) null, "Select " + type, FileDialog.LOAD);
				fd.setVisible(true);
				if (fd.getFile() != null) {
					File file = new File(fd.getDirectory(), fd.getFile());
					field.setText(file.getName());

					if (type.equals("JSON"))
						VisualizeView.this.resultPath = file.getAbsolutePath();
					else if (type.equals("Edges"))
						VisualizeView.this.edgePath = file.getAbsolutePath();
					else if (type.equals("Vertices"))
						VisualizeView.this.vertexPath = file.getAbsolutePath();
					else if (type.equals("Coordinates"))
						VisualizeView.this.coordPath = file.getAbsolutePath();
				}
			}
		});
	}

	private void handleLoadAction() {
		if (this.resultPath == null || this.edgePath == null || this.vertexPath == null) {
			JOptionPane.showMessageDialog(this, "Please select JSON, Edges, and Vertices first.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			this.controller.setBaseSpeed((double) this.speedSpinner.getValue());
			this.controller.loadNetwork(new File(this.vertexPath), new File(this.edgePath),
					(this.coordPath != null) ? new File(this.coordPath) : null);

			Double[] kValues = this.controller.prepareResults(new File(this.resultPath),
					(int) this.pSpinner.getValue());

			this.kCombo.removeAllItems();
			for (Double k : kValues)
				this.kCombo.addItem(k);

			if (this.kCombo.getItemCount() > 0)
				this.kCombo.setSelectedIndex(0);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error loading results: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	private void handleKSelection() {
		int idx = this.kCombo.getSelectedIndex();
		if (idx >= 0)
			this.controller.updateView(idx);
	}

	private void handleSaveImageAction() {
		if (this.kCombo.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(this, "No data loaded to save.", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}

		FileDialog fd = new FileDialog((Frame) null, "Save Visualization as PNG", FileDialog.SAVE);
		fd.setFile("network_view.png");
		fd.setVisible(true);

		if (fd.getFile() != null) {
			String savePath = fd.getDirectory() + fd.getFile();
			if (!savePath.toLowerCase().endsWith(".png"))
				savePath += ".png";

			try {
				this.controller.saveImage(savePath, this.kCombo.getSelectedIndex());
				JOptionPane.showMessageDialog(this, "Image saved successfully!", "Success",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}
}
