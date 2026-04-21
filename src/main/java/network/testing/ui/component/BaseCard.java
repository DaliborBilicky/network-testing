package network.testing.ui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public abstract class BaseCard extends JPanel {
	protected static final Color COLOR_ACTIVE = new Color(33, 150, 243);
	protected static final Color COLOR_CARD = new Color(50, 50, 55);
	protected static final Color COLOR_BORDER = new Color(70, 70, 75);
	protected static final Color COLOR_SELECTED_BG = new Color(60, 65, 80);

	protected final int height;
	protected boolean selected = false;

	public BaseCard(int height) {
		this.height = height;
		this.setupBase();
	}

	private void setupBase() {
		this.setLayout(new BorderLayout());
		this.setOpaque(false);
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));

		this.setMinimumSize(new Dimension(0, this.height));
		this.setPreferredSize(new Dimension(200, this.height));
		this.setBorder(new EmptyBorder(0, 15, 0, 15));
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		this.repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Pozadie karty
		g2.setColor(this.selected ? COLOR_SELECTED_BG : COLOR_CARD);
		g2.fill(new RoundRectangle2D.Float(0, 0, this.getWidth(), this.getHeight(), 12, 12));

		// Okraj
		if (this.selected) {
			g2.setColor(COLOR_ACTIVE);
			g2.setStroke(new java.awt.BasicStroke(2f));
		} else {
			g2.setColor(COLOR_BORDER);
			g2.setStroke(new java.awt.BasicStroke(1f));
		}
		g2.draw(new RoundRectangle2D.Float(1, 1, this.getWidth() - 2, this.getHeight() - 2, 12, 12));

		g2.dispose();
	}
}
