package network.testing.io.export;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import network.testing.core.model.Network;
import network.testing.ui.render.NetworkRenderer;

public class ImageExporter {
	private static final int PADDING = 50;

	public static void exportToPng(Network network, Point2D[] positions, int[] oldMedians,
			int[] newMedians, double[] declines, String path) throws IOException {
		Rectangle bounds = calculateBounds(positions);

		int width = bounds.width + (PADDING * 2);
		int height = bounds.height + (PADDING * 2);

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = img.createGraphics();

		prepareCanvas(g2, bounds, width, height);

		NetworkRenderer.render(g2, network, positions, oldMedians, newMedians, declines, 1.0);
		g2.dispose();

		writeImage(img, path);
	}

	private static void prepareCanvas(Graphics2D g2, Rectangle bounds, int width, int height) {
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, width, height);

		g2.translate(PADDING - bounds.x, PADDING - bounds.y);
	}

	private static void writeImage(BufferedImage img, String path) throws IOException {
		File file = new File(path);
		File parent = file.getParentFile();
		if (parent != null && !parent.exists())
			parent.mkdirs();

		ImageIO.write(img, "png", file);
	}

	private static Rectangle calculateBounds(Point2D[] positions) {
		if (positions == null || positions.length == 0)
			return new Rectangle(0, 0, 800, 600);

		double minX = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;

		for (Point2D p : positions) {
			minX = Math.min(minX, p.getX());
			maxX = Math.max(maxX, p.getX());
			minY = Math.min(minY, p.getY());
			maxY = Math.max(maxY, p.getY());
		}

		return new Rectangle(
				(int) minX,
				(int) minY,
				(int) (maxX - minX),
				(int) (maxY - minY));
	}
}
