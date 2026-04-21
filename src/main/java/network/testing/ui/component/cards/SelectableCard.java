package network.testing.ui.component.cards;

import java.awt.BasicStroke;
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

public abstract class SelectableCard extends JPanel {
	protected boolean selected = false;
	private final int fixedHeight;

	public SelectableCard(int height) {
		this.fixedHeight = height;
		this.setLayout(new BorderLayout());
		this.setOpaque(false);
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.setAlignmentX(LEFT_ALIGNMENT);
		this.setBorder(new EmptyBorder(5, 15, 5, 15));

		this.setMinimumSize(new Dimension(100, this.fixedHeight));
		this.setPreferredSize(new Dimension(220, this.fixedHeight));
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, this.fixedHeight));
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		this.repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Color bg;
		Color outline;

		if (this.selected) {
			bg = new Color(33, 150, 243, 40);
			outline = new Color(33, 150, 243);
		} else {
			bg = new Color(70, 73, 75);
			outline = new Color(80, 84, 86);
		}

		g2.setColor(bg);
		g2.fill(new RoundRectangle2D.Float(0, 0, this.getWidth(), this.getHeight(), 10, 10));
		g2.setColor(outline);
		g2.setStroke(new BasicStroke(1.0f));

		g2.draw(new RoundRectangle2D.Float(1, 1, this.getWidth() - 2, this.getHeight() - 2, 10, 10));

		g2.dispose();
		super.paintComponent(g);
	}
}
