package network.testing.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;

public class NewProjectDialog extends JDialog {
	private Path vertPath;
	private Path edgePath;
	private Path coordPath;
	private Path zipPath;
	private boolean approved = false;

	private final JTextField txtVert;
	private final JTextField txtEdge;
	private final JTextField txtCoord;
	private final JTextField txtZip;

	public NewProjectDialog(Frame owner) {
		super(owner, "Create New Project", true);

		this.txtVert = this.createField("Select nodes file (vertices.txt)...");
		this.txtEdge = this.createField("Select links file (edges.txt)...");
		this.txtCoord = this.createField("Optional: Select node positions...");
		this.txtZip = this.createField("Where to save the .ntp project...");

		this.initComponents(owner);
	}

	private void initComponents(Frame owner) {
		this.setLayout(new BorderLayout());

		this.add(this.createHeaderPanel(), BorderLayout.NORTH);
		this.add(this.createFormPanel(), BorderLayout.CENTER);
		this.add(this.createFooterPanel(), BorderLayout.SOUTH);

		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(owner);
	}

	private JPanel createHeaderPanel() {
		JPanel header = new JPanel(new GridLayout(2, 1));
		header.setBorder(new EmptyBorder(15, 20, 10, 20));

		JLabel title = new JLabel("Project Setup");
		title.setFont(new Font("SansSerif", Font.BOLD, 18));

		JLabel desc = new JLabel("Import network data files to initialize a new testing project.");
		desc.setForeground(Color.LIGHT_GRAY);

		header.add(title);
		header.add(desc);
		return header;
	}

	private JPanel createFormPanel() {
		JPanel form = new JPanel(new GridBagLayout());
		form.setBorder(new EmptyBorder(10, 20, 10, 20));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(6, 0, 6, 0);

		this.addFileRow(form, gbc, 0, "Nodes File*:", this.txtVert, "txt", false);
		this.addFileRow(form, gbc, 1, "Edges File*:", this.txtEdge, "txt", false);
		this.addFileRow(form, gbc, 2, "Coordinates:", this.txtCoord, "txt", false);
		this.addFileRow(form, gbc, 3, "Save As*:", this.txtZip, "ntp", true);

		return form;
	}

	private void addFileRow(JPanel panel, GridBagConstraints gbc, int y, String label, JTextField field, String ext,
			boolean isSave) {
		gbc.gridy = y;
		gbc.gridx = 0;
		gbc.weightx = 0;
		panel.add(new JLabel(label), gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(6, 10, 6, 10);
		panel.add(field, gbc);

		gbc.gridx = 2;
		gbc.weightx = 0;
		gbc.insets = new Insets(6, 0, 6, 0);
		JButton btn = new JButton("...");
		btn.addActionListener(e -> this.handleFilePick(label, field, ext, isSave));
		panel.add(btn, gbc);
	}

	private JPanel createFooterPanel() {
		JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(e -> this.dispose());

		JButton btnCreate = new JButton("Create Project");
		btnCreate.putClientProperty(FlatClientProperties.STYLE, "background: #2196F3; foreground: #FFFFFF");
		btnCreate.addActionListener(e -> this.handleCreate());

		footer.add(btnCancel);
		footer.add(btnCreate);
		return footer;
	}

	private void handleFilePick(String label, JTextField field, String ext, boolean isSave) {
		FileDialog fd = new FileDialog((Frame) this.getOwner(), label, isSave ? FileDialog.SAVE : FileDialog.LOAD);
		if (isSave) {
			fd.setFile("new_project.ntp");
		}
		fd.setVisible(true);

		if (fd.getFile() != null) {
			Path path = new File(fd.getDirectory(), fd.getFile()).toPath();
			field.setText(path.toString());
			this.updateInternalPath(label, path);
		}
	}

	private void updateInternalPath(String label, Path path) {
		if (label.contains("Nodes")) {
			this.vertPath = path;
		} else if (label.contains("Edges")) {
			this.edgePath = path;
		} else if (label.contains("Coordinates")) {
			this.coordPath = path;
		} else {
			this.zipPath = path;
		}
	}

	private void handleCreate() {
		if (this.vertPath == null || this.edgePath == null || this.zipPath == null) {
			JOptionPane.showMessageDialog(this, "Please select all required files (*).", "Missing Information",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		this.approved = true;
		this.dispose();
	}

	private JTextField createField(String hint) {
		JTextField field = new JTextField(25);
		field.setEditable(false);
		field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, hint);
		return field;
	}

	public boolean isApproved() {
		return this.approved;
	}

	public Path getVertPath() {
		return this.vertPath;
	}

	public Path getEdgePath() {
		return this.edgePath;
	}

	public Path getCoordPath() {
		return this.coordPath;
	}

	public Path getZipPath() {
		return this.zipPath;
	}
}
