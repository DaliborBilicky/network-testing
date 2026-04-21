package network.testing.core.utils;

public class NetworkMetrics {
	public static double calculateIrregularity(int[] weights) {
		return StatisticsUtils.variance(weights);
	}

	public static double[] calculateDeclines(double[] baseCosts, double[] elongCosts, double speed) {
		double[] declines = new double[baseCosts.length];
		for (int i = 0; i < baseCosts.length; i++) {
			double elongCost = elongCosts[i];
			declines[i] = (elongCost == 0) ? speed : speed - ((baseCosts[i] / elongCost) * speed);
		}
		return declines;
	}
}
