package network.testing.ui.component;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import network.testing.app.AppCoordinator;
import network.testing.app.events.listeners.SelectionListener;
import network.testing.domain.model.result.SnapshotData;
import network.testing.ui.render.NetworkRenderer;
import network.testing.ui.render.RenderData;
import network.testing.ui.render.VisualizationService;

public class InteractiveCanvas extends JPanel implements SelectionListener {
	private final AppCoordinator coordinator;

	private RenderData renderData;
	private double zoom;
	private double offX;
	private double offY;
	private Point lastMouse;

	public InteractiveCanvas(AppCoordinator coordinator) {
		this.coordinator = coordinator;
		this.zoom = 1.0;
		this.offX = 0.0;
		this.offY = 0.0;

		this.initComponents();
		this.setupInteraction();
	}

	private void initComponents() {
		this.setBackground(Color.WHITE);
		this.setDoubleBuffered(true);
		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
	}

	@Override
	public void onStepSelected(long id, SnapshotData current, SnapshotData previous) {
		this.renderData = VisualizationService.prepareRenderData(
				this.coordinator.getContext(),
				current,
				previous,
				this.coordinator.getActiveBaseSpeed());

		this.repaint();
	}

	public void resetView() {
		this.zoom = 1.0;
		this.offX = 0.0;
		this.offY = 0.0;
		this.repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (this.renderData == null || this.renderData.network() == null) {
			this.drawPlaceholder(g);
			return;
		}

		Graphics2D g2 = (Graphics2D) g;
		AffineTransform oldTransform = g2.getTransform();

		g2.translate(this.getWidth() / 2.0, this.getHeight() / 2.0);
		g2.scale(this.zoom, this.zoom);
		g2.translate(this.offX, this.offY);

		NetworkRenderer.render(g2, this.renderData, this.zoom);

		g2.setTransform(oldTransform);
	}

	private void setupInteraction() {
		MouseAdapter adapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				this.handleMousePressed(e);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				this.handleMouseDragged(e);
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				this.handleMouseWheel(e);
			}

			private void handleMousePressed(MouseEvent e) {
				InteractiveCanvas.this.lastMouse = e.getPoint();
			}

			private void handleMouseDragged(MouseEvent e) {
				if (InteractiveCanvas.this.lastMouse != null) {
					InteractiveCanvas.this.offX += (e.getX() - InteractiveCanvas.this.lastMouse.x)
							/ InteractiveCanvas.this.zoom;
					InteractiveCanvas.this.offY += (e.getY() - InteractiveCanvas.this.lastMouse.y)
							/ InteractiveCanvas.this.zoom;
					InteractiveCanvas.this.lastMouse = e.getPoint();
					InteractiveCanvas.this.repaint();
				}
			}

			private void handleMouseWheel(MouseWheelEvent e) {
				if (e.getWheelRotation() < 0)
					InteractiveCanvas.this.zoom *= 1.1;
				else
					InteractiveCanvas.this.zoom *= 0.9;

				InteractiveCanvas.this.zoom = Math.max(0.01, Math.min(InteractiveCanvas.this.zoom, 100.0));
				InteractiveCanvas.this.repaint();
			}
		};

		this.addMouseListener(adapter);
		this.addMouseMotionListener(adapter);
		this.addMouseWheelListener(adapter);
	}

	private void drawPlaceholder(Graphics g) {
		g.setColor(Color.GRAY);
		String msg = "No project data loaded. Select a snapshot from the timeline.";

		FontMetrics fm = g.getFontMetrics();
		int x = (this.getWidth() - fm.stringWidth(msg)) / 2;
		int y = this.getHeight() / 2;

		g.drawString(msg, x, y);
	}
}
