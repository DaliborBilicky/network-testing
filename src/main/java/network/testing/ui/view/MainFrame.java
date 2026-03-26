package network.testing.ui.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

public final class MainFrame extends JFrame {
	private final CardLayout cardLayout;
	private final JPanel contentPanel;
	private final ButtonGroup navGroup;

	public MainFrame() {
		this.setTitle("Network Robustness Testing");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);

		this.cardLayout = new CardLayout();
		this.contentPanel = new JPanel(this.cardLayout);
		this.navGroup = new ButtonGroup();

		this.initComponents();
		this.setLocationRelativeTo(null);
	}

	private void initComponents() {
		JPanel sidebar = this.createSidebar();

		this.contentPanel.add(new ExperimentView(), "EXP");
		this.contentPanel.add(new VisualizeView(), "VIZ");
		this.contentPanel.add(new GenerateView(), "GEN");

		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, this.contentPanel);
		mainSplit.setDividerLocation(160);
		mainSplit.setDividerSize(8);
		mainSplit.setOneTouchExpandable(true);
		mainSplit.setBorder(null);

		this.add(mainSplit, BorderLayout.CENTER);
		this.showPage("EXP");
	}

	private JPanel createSidebar() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(new Color(240, 240, 240));
		panel.setBorder(new MatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
		panel.setMinimumSize(new Dimension(0, 0));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4, 4, 4, 4);

		panel.add(this.createNavButton("Experiment", "EXP"), gbc);
		gbc.gridy++;
		panel.add(this.createNavButton("Visualize", "VIZ"), gbc);
		gbc.gridy++;
		panel.add(this.createNavButton("Generate", "GEN"), gbc);

		gbc.weighty = 1.0;
		gbc.gridy++;
		panel.add(new JPanel() {
			{
				setOpaque(false);
			}
		}, gbc);

		return panel;
	}

	private JToggleButton createNavButton(String text, String cardName) {
		JToggleButton btn = new JToggleButton(text);
		btn.setPreferredSize(new Dimension(0, 45));
		btn.setHorizontalAlignment(SwingConstants.LEFT);
		btn.setFont(new Font("SansSerif", Font.PLAIN, 20));
		btn.addActionListener(e -> this.showPage(cardName));
		this.navGroup.add(btn);
		if (cardName.equals("EXP"))
			btn.setSelected(true);
		return btn;
	}

	private void showPage(String cardName) {
		this.cardLayout.show(this.contentPanel, cardName);
	}
}
