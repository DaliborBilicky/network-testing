package network.testing.core.utils;

import java.awt.geom.Point2D;
import java.util.Random;
import network.testing.domain.model.network.Topology;

public class PositionUtils {
	private static final double WIDTH = 1000.0;
	private static final double HEIGHT = 1000.0;

	public static Point2D[] projectGeoCoordinates(double[][] latLonCoords) {
		if (latLonCoords.length == 0)
			return new Point2D[0];

		double[] bounds = findBounds(latLonCoords);
		double centerLat = (bounds[0] + bounds[1]) / 2.0;
		double centerLon = (bounds[2] + bounds[3]) / 2.0;

		Point2D[] points = new Point2D[latLonCoords.length];
		for (int i = 0; i < latLonCoords.length; i++) {
			double x = latLonCoords[i][1] - centerLon;
			double y = centerLat - latLonCoords[i][0];
			points[i] = new Point2D.Double(x, y);
		}

		return normalizeAndScale(points, WIDTH, HEIGHT);
	}

	public static Point2D[] computeForceLayout(Topology topology) {
		int n = topology.getNumOfVerts();
		Point2D[] positions = initializeRandomPositions(n);

		double k = Math.sqrt((WIDTH * HEIGHT) / n) * 0.75;
		double temperature = WIDTH / 10.0;
		int iterations = Math.max(300, n * 2);

		for (int iter = 0; iter < iterations; iter++) {
			Point2D[] forces = new Point2D[n];
			for (int i = 0; i < n; i++)
				forces[i] = new Point2D.Double(0, 0);

			applyRepulsiveForces(positions, forces, k);
			applyAttractiveForces(topology, positions, forces, k);
			updatePositions(positions, forces, temperature);

			temperature *= 0.992;
		}

		return normalizeAndScale(positions, WIDTH, HEIGHT);
	}

	private static double[] findBounds(double[][] coords) {
		double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
		double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;

		for (double[] c : coords) {
			minLat = Math.min(c[0], minLat);
			maxLat = Math.max(c[0], maxLat);
			minLon = Math.min(c[1], minLon);
			maxLon = Math.max(c[1], maxLon);
		}
		return new double[] { minLat, maxLat, minLon, maxLon };
	}

	private static Point2D[] initializeRandomPositions(int n) {
		Random rnd = new Random(45);
		Point2D[] pos = new Point2D[n];
		for (int i = 0; i < n; i++)
			pos[i] = new Point2D.Double(rnd.nextDouble() * WIDTH, rnd.nextDouble() * HEIGHT);
		return pos;
	}

	private static void applyRepulsiveForces(Point2D[] pos, Point2D[] forces, double k) {
		double c_rep = 3.0;
		for (int i = 0; i < pos.length; i++) {
			for (int j = 0; j < pos.length; j++) {
				if (i == j)
					continue;

				double dx = pos[i].getX() - pos[j].getX();
				double dy = pos[i].getY() - pos[j].getY();
				double dist = Math.max(0.1, Math.sqrt(dx * dx + dy * dy));

				double forceMag = c_rep * (k * k) / dist;
				forces[i].setLocation(
						forces[i].getX() + (dx / dist) * forceMag,
						forces[i].getY() + (dy / dist) * forceMag);
			}
		}
	}

	private static void applyAttractiveForces(Topology topo, Point2D[] pos, Point2D[] forces, double k) {
		double c_attr = 0.5;
		for (int i = 0; i < topo.getNumOfEdges(); i++) {
			int u = topo.getEdgeU(i);
			int v = topo.getEdgeV(i);

			double dx = pos[u].getX() - pos[v].getX();
			double dy = pos[u].getY() - pos[v].getY();
			double dist = Math.max(0.1, Math.sqrt(dx * dx + dy * dy));

			double forceMag = c_attr * (dist * dist) / k;
			double fx = (dx / dist) * forceMag;
			double fy = (dy / dist) * forceMag;

			forces[u].setLocation(forces[u].getX() - fx, forces[u].getY() - fy);
			forces[v].setLocation(forces[v].getX() + fx, forces[v].getY() + fy);
		}
	}

	private static void updatePositions(Point2D[] pos, Point2D[] forces, double temp) {
		for (int i = 0; i < pos.length; i++) {
			double fx = forces[i].getX();
			double fy = forces[i].getY();
			double fDist = Math.sqrt(fx * fx + fy * fy);

			if (fDist > 0) {
				double displacement = Math.min(fDist, temp);
				pos[i].setLocation(
						pos[i].getX() + (fx / fDist) * displacement,
						pos[i].getY() + (fy / fDist) * displacement);
			}
		}
	}

	private static void scaleToDimensions(double maxBound, double targetW, double targetH, Point2D[] centered) {
		if (maxBound > 0) {
			double scale = Math.min(targetW, targetH) / maxBound;
			for (Point2D p : centered)
				p.setLocation(p.getX() * scale, p.getY() * scale);
		}
	}

	private static double centerCoords(double avgX, double avgY, Point2D[] pos, Point2D[] centered) {
		double maxBound = 0;
		for (int i = 0; i < pos.length; i++) {
			double cx = pos[i].getX() - avgX;
			double cy = pos[i].getY() - avgY;
			centered[i] = new Point2D.Double(cx, cy);
			maxBound = Math.max(maxBound, Math.max(Math.abs(cx), Math.abs(cy)));
		}
		return maxBound;
	}

	private static Point2D[] normalizeAndScale(Point2D[] pos, double targetW, double targetH) {
		if (pos.length == 0)
			return pos;

		double avgX = 0, avgY = 0;
		for (Point2D p : pos) {
			avgX += p.getX();
			avgY += p.getY();
		}
		avgX /= pos.length;
		avgY /= pos.length;

		Point2D[] centered = new Point2D[pos.length];
		double maxBound = centerCoords(avgX, avgY, pos, centered);

		scaleToDimensions(maxBound, targetW, targetH, centered);

		return centered;
	}
}
