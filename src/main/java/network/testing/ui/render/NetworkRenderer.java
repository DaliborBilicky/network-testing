package network.testing.ui.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import network.testing.core.model.Network;
import network.testing.core.model.Topology;
import network.testing.logic.math.MetricsUtils;

public class NetworkRenderer {
	private static final Color COLOR_OLD = Color.RED;
	private static final Color COLOR_NEW = Color.GREEN;
	private static final Color COLOR_BOTH = Color.ORANGE;
	private static final int RING_SIZE = 35;

	public static void render(Graphics2D g2, Network network, Point2D[] positions,
			int[] originalMedians, int[] modifiedMedians, double[] declines, double zoom) {

		setupGraphics(g2);

		renderEdges(g2, network, positions, declines, zoom);

		renderVertices(g2, network, positions, originalMedians, modifiedMedians, zoom);
	}

	private static void setupGraphics(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	private static void renderEdges(Graphics2D g2, Network network, Point2D[] positions,
			double[] declines, double zoom) {
		Topology topo = network.getTopology();
		double maxDecline = MetricsUtils.max(declines);

		for (int i = 0; i < topo.getNumOfEdges(); i++) {
			Point2D p1 = positions[topo.getEdgeU(i)];
			Point2D p2 = positions[topo.getEdgeV(i)];
			double decline = (declines != null && i < declines.length) ? declines[i] : 0.0;

			drawSingleEdge(g2, p1, p2, decline, maxDecline, zoom);
		}
	}

	private static void drawSingleEdge(Graphics2D g2, Point2D p1, Point2D p2,
			double decline, double maxDecline, double zoom) {
		double safeMax = Math.max(maxDecline, 0.0001);
		float intensity = (float) Math.sqrt(decline / safeMax);

		float thickness = 1.0f + (intensity * 4.0f);
		int grayValue = (int) (210 - (intensity * 160));

		g2.setColor(new Color(grayValue, grayValue, grayValue));
		g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());

		if (zoom > 2.5) {
			g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
			g2.setColor(new Color(100, 100, 100));
			String label = String.format("%.1f", decline);

			int midX = (int) (p1.getX() + p2.getX()) / 2;
			int midY = (int) (p1.getY() + p2.getY()) / 2;

			g2.drawString(label, midX, midY);
		}
	}

	private static void renderVertices(Graphics2D g2, Network network, Point2D[] positions,
			int[] originalMedians, int[] modifiedMedians, double zoom) {
		int n = network.getTopology().getNumOfVerts();
		int maxWeight = MetricsUtils.max(network.copyVertexWeights());

		for (int i = 0; i < n; i++) {
			Point2D p = positions[i];
			int weight = network.getVertexWeight(i);

			renderMedianRings(g2, i, p, originalMedians, modifiedMedians);

			if (weight > 0)
				drawNodeWithWeight(g2, p, i, weight, maxWeight, zoom);
			else
				drawEmptyNode(g2, p);
		}
	}

	private static void renderMedianRings(Graphics2D g2, int id, Point2D p,
			int[] originalMedians, int[] modifiedMedians) {
		boolean inOld = MetricsUtils.contains(originalMedians, id);
		boolean inNew = MetricsUtils.contains(modifiedMedians, id);

		if (inOld || inNew) {
			g2.setStroke(new BasicStroke(4.0f));

			if (inOld && inNew)
				g2.setColor(COLOR_BOTH);
			else if (inOld)
				g2.setColor(COLOR_OLD);
			else
				g2.setColor(COLOR_NEW);

			g2.drawOval((int) p.getX() - RING_SIZE / 2, (int) p.getY() - RING_SIZE / 2,
					RING_SIZE, RING_SIZE);
		}
	}

	private static void drawNodeWithWeight(Graphics2D g2, Point2D p, int id,
			int weight, int maxWeight, double zoom) {
		float weightRatio = (float) weight / Math.max(maxWeight, 1);
		int radius = 5 + (int) (10 * weightRatio);

		int gray = (int) (180 - (weightRatio * 150));
		g2.setColor(new Color(gray, gray, gray));
		g2.fillOval((int) p.getX() - radius, (int) p.getY() - radius, radius * 2, radius * 2);

		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(1.0f));
		g2.drawOval((int) p.getX() - radius, (int) p.getY() - radius, radius * 2, radius * 2);

		if (zoom > 1.2)
			drawNodeLabel(g2, p, id, weight, radius);
	}

	private static void drawNodeLabel(Graphics2D g2, Point2D p, int id, int weight, int radius) {
		g2.setFont(new Font("SansSerif", Font.BOLD, 11));
		g2.setColor(Color.DARK_GRAY);
		String label = id + " (" + weight + ")";
		g2.drawString(label, (int) p.getX() + radius + 2, (int) p.getY() - radius);
	}

	private static void drawEmptyNode(Graphics2D g2, Point2D p) {
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillOval((int) p.getX() - 3, (int) p.getY() - 3, 6, 6);
	}
}
