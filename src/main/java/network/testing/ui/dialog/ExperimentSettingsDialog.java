package network.testing.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;

import network.testing.core.execution.ExperimentParameters;
import network.testing.core.execution.ExperimentType;

public class ExperimentSettingsDialog extends JDialog {
	private ExperimentParameters result;

	private final JTextField txtName;
	private final JSpinner spinMinP;
	private final JSpinner spinMaxP;
	private final JSpinner spinPrecision;
	private final JSpinner spinSpeed;
	private final JComboBox<ExperimentType> comboType;

	public ExperimentSettingsDialog(Frame owner, ExperimentParameters current) {
		super(owner, "Experiment Settings", true);

		this.txtName = new JTextField();
		this.txtName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g. Robustness Test 01");
		if (current != null && current.name() != null)
			this.txtName.setText(current.name());

		this.spinMinP = new JSpinner(new SpinnerNumberModel(current != null ? current.pMin() : 5, 1, 500, 1));
		this.spinMaxP = new JSpinner(new SpinnerNumberModel(current != null ? current.pMax() : 10, 1, 500, 1));
		this.spinPrecision = new JSpinner(
				new SpinnerNumberModel(current != null ? current.precision() : 0.01, 0.0001, 1.0, 0.001));
		this.spinSpeed = new JSpinner(
				new SpinnerNumberModel(current != null ? current.baseSpeed() : 110.0, 10.0, 300.0, 5.0));
		this.comboType = new JComboBox<>(ExperimentType.values());

		if (current != null)
			this.comboType.setSelectedItem(current.type());

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

		JLabel title = new JLabel("Configuration");
		title.setFont(new Font("SansSerif", Font.BOLD, 18));

		JLabel desc = new JLabel("Set parameters for the robustness analysis.");
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

		JPanel pPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		pPanel.add(this.spinMinP);
		JLabel toLabel = new JLabel("  to  ");
		toLabel.setForeground(Color.GRAY);
		pPanel.add(toLabel);
		pPanel.add(this.spinMaxP);

		this.addFormRow(form, gbc, 0, "Experiment Name:", this.txtName);
		this.addFormRow(form, gbc, 1, "P-Median Range:", pPanel);
		this.addFormRow(form, gbc, 2, "Search Strategy:", this.comboType);
		this.addFormRow(form, gbc, 3, "Precision:", this.spinPrecision);
		this.addFormRow(form, gbc, 4, "Base Speed (km/h):", this.spinSpeed);

		return form;
	}

	private void addFormRow(JPanel panel, GridBagConstraints gbc, int y, String label, JComponent comp) {
		gbc.gridy = y;
		gbc.gridx = 0;
		gbc.weightx = 0;

		JLabel lbl = new JLabel(label);
		lbl.setBorder(new EmptyBorder(0, 0, 0, 20));
		panel.add(lbl, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		panel.add(comp, gbc);
	}

	private JPanel createFooterPanel() {
		JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(e -> this.dispose());

		JButton btnSave = new JButton("Save Settings");
		btnSave.putClientProperty(FlatClientProperties.STYLE, "background: #2196F3; foreground: #FFFFFF");
		btnSave.addActionListener(e -> this.handleSave());

		footer.add(btnCancel);
		footer.add(btnSave);
		return footer;
	}

	private void handleSave() {
		String name = this.txtName.getText().trim();
		int min = (int) this.spinMinP.getValue();
		int max = (int) this.spinMaxP.getValue();

		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(
					SwingUtilities.getWindowAncestor(this),
					"Please enter an experiment name.",
					"Validation Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (min > max) {
			JOptionPane.showMessageDialog(
					SwingUtilities.getWindowAncestor(this),
					"Min P cannot be greater than Max P.",
					"Validation Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		this.result = new ExperimentParameters(
				name,
				min,
				max,
				(ExperimentType) this.comboType.getSelectedItem(),
				(double) this.spinPrecision.getValue(),
				(double) this.spinSpeed.getValue());

		this.dispose();
	}

	public ExperimentParameters getResult() {
		return this.result;
	}
}
