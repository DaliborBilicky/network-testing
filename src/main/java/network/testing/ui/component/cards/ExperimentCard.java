package network.testing.ui.component.cards;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import network.testing.domain.model.dto.ExperimentSummary;

public class ExperimentCard extends SelectableCard {
	private final ExperimentSummary data;
	private final JPanel actionPanel;
	private final JButton btnExport;
	private final JButton btnDelete;

	public ExperimentCard(ExperimentSummary data) {
		super(70);
		this.data = data;

		this.btnExport = new JButton("📄");
		this.btnDelete = new JButton("🗑️");
		this.actionPanel = new JPanel();

		this.initComponents();
	}

	private void initComponents() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel info = new JPanel();
		info.setOpaque(false);
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

		JLabel title = new JLabel(this.data.name());
		title.setFont(new Font("SansSerif", Font.BOLD, 16));

		JLabel sub = new JLabel(this.data.type() + " • " + this.data.date());
		sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
		sub.setForeground(Color.LIGHT_GRAY);

		info.add(title);
		info.add(Box.createRigidArea(new Dimension(0, 2)));
		info.add(sub);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 5, 0, 10);
		this.add(info, gbc);

		this.actionPanel.setOpaque(false);
		this.actionPanel.setLayout(new BoxLayout(this.actionPanel, BoxLayout.X_AXIS));

		this.styleButton(this.btnExport, new Color(76, 175, 80));
		this.styleButton(this.btnDelete, new Color(229, 57, 53));

		this.actionPanel.add(this.btnExport);
		this.actionPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		this.actionPanel.add(this.btnDelete);
		this.actionPanel.setVisible(false);

		gbc.gridx = 1;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(0, 0, 0, 5);
		this.add(this.actionPanel, gbc);
	}

	private void styleButton(JButton b, Color color) {
		b.setFont(new Font("SansSerif", Font.PLAIN, 20));
		b.setMargin(new Insets(0, 0, 0, 0));
		b.setContentAreaFilled(false);
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		b.setForeground(color);
		b.setPreferredSize(new Dimension(40, 40));
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		this.actionPanel.setVisible(selected);
		this.revalidate();
		this.repaint();
	}

	public long getExperimentId() {
		return this.data.id();
	}

	public JButton getBtnExport() {
		return this.btnExport;
	}

	public JButton getBtnDelete() {
		return this.btnDelete;
	}

	public String getExperimentName() {
		return this.data.name();
	}
}
