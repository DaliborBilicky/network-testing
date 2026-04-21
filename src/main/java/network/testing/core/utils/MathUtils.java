package network.testing.core.utils;

public class MathUtils {
	public static boolean isClose(double a, double b, double tolerance) {
		if (tolerance < 0.0)
			throw new IllegalArgumentException("Tolerance must be non-negative");
		if (a == b)
			return true;
		if (Double.isInfinite(a) || Double.isInfinite(b))
			return false;

		double diff = Math.abs(b - a);
		return diff <= Math.max(tolerance * Math.max(Math.abs(a), Math.abs(b)), 0.0);
	}
}
