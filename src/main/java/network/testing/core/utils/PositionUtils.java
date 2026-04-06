package network.testing.core.utils;

import java.awt.geom.Point2D;

public class PositionUtils {
	private static final double SCALE = 3000.0;
	private static final int GRID_SPACING = 80;

	public static Point2D[] fromCoordinates(double[][] rawCoords) {
		int n = rawCoords.length;
		Point2D[] pos = new Point2D[n];

		double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;
		double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;

		for (double[] c : rawCoords) {
			minLat = Math.min(c[0], minLat);
			maxLat = Math.max(c[0], maxLat);
			minLon = Math.min(c[1], minLon);
			maxLon = Math.max(c[1], maxLon);
		}

		double centerLon = (minLon + maxLon) / 2.0;
		double centerLat = (minLat + maxLat) / 2.0;

		for (int i = 0; i < n; i++) {
			double x = (rawCoords[i][1] - centerLon) * SCALE;
			double y = (centerLat - rawCoords[i][0]) * SCALE;
			pos[i] = new Point2D.Double(x, y);
		}
		return pos;
	}

	public static Point2D[] generateGrid(int n) {
		Point2D[] pos = new Point2D[n];
		int side = (int) Math.ceil(Math.sqrt(n));
		double offset = (side * GRID_SPACING) / 2.0;

		for (int i = 0; i < n; i++) {
			double x = (i % side) * GRID_SPACING - offset;
			double y = (i / side) * GRID_SPACING - offset;
			pos[i] = new Point2D.Double(x, y);
		}
		return pos;
	}
}
