package network.testing.ui.component;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import network.testing.core.model.Network;
import network.testing.ui.render.NetworkRenderer;

public class InteractiveCanvas extends JPanel {
	private Network network;
	private int[] oldMedians;
	private int[] newMedians;
	private double[] declines;
	private Point2D[] positions;

	private double zoom = 1.0;
	private double offX = 0.0;
	private double offY = 0.0;
	private Point lastMouse;

	public InteractiveCanvas() {
		this.setBackground(Color.WHITE);
		this.setupInteraction();
	}

	public void setData(Network network, int[] oldMedians, int[] newMedians, double[] declines, Point2D[] positions) {
		this.network = network;
		this.oldMedians = oldMedians;
		this.newMedians = newMedians;
		this.declines = declines;
		this.positions = positions;
		this.repaint();
	}

	private void setupInteraction() {
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				InteractiveCanvas.this.lastMouse = e.getPoint();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (InteractiveCanvas.this.lastMouse != null) {
					InteractiveCanvas.this.offX += (e.getX() - InteractiveCanvas.this.lastMouse.x)
							/ InteractiveCanvas.this.zoom;
					InteractiveCanvas.this.offY += (e.getY() - InteractiveCanvas.this.lastMouse.y)
							/ InteractiveCanvas.this.zoom;
					InteractiveCanvas.this.lastMouse = e.getPoint();
					InteractiveCanvas.this.repaint();
				}
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() < 0)
					InteractiveCanvas.this.zoom *= 1.1;
				else
					InteractiveCanvas.this.zoom *= 0.9;

				InteractiveCanvas.this.zoom = Math.max(0.01, Math.min(InteractiveCanvas.this.zoom, 100.0));
				InteractiveCanvas.this.repaint();
			}
		};

		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);
		this.addMouseWheelListener(mouseAdapter);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (this.network == null || this.positions == null) {
			this.drawPlaceholder(g);
			return;
		}

		Graphics2D g2 = (Graphics2D) g;
		AffineTransform oldTransform = g2.getTransform();

		g2.translate(this.getWidth() / 2.0, this.getHeight() / 2.0);
		g2.scale(this.zoom, this.zoom);
		g2.translate(this.offX, this.offY);

		NetworkRenderer.render(
				g2,
				this.network,
				this.positions,
				this.oldMedians,
				this.newMedians,
				this.declines,
				this.zoom);

		g2.setTransform(oldTransform);
	}

	private void drawPlaceholder(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		String message = "No data loaded.";
		FontMetrics fm = g.getFontMetrics();
		int x = (this.getWidth() - fm.stringWidth(message)) / 2;
		int y = this.getHeight() / 2;
		g.drawString(message, x, y);
	}

	public void resetView() {
		this.zoom = 1.0;
		this.offX = 0.0;
		this.offY = 0.0;
		this.repaint();
	}
}
