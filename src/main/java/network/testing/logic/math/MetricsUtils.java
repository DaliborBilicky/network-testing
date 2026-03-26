package network.testing.logic.math;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class MetricsUtils {
	public static boolean isClose(double a, double b, double tolerance) {
		if (tolerance < 0.0)
			throw new IllegalArgumentException("Tolerances must be non-negative");

		if (a == b)
			return true;

		if (Double.isInfinite(a) || Double.isInfinite(b))
			return false;

		double diff = Math.abs(b - a);
		return diff <= Math.max(tolerance * Math.max(Math.abs(a), Math.abs(b)), 0.0);
	}

	public static double calculateIrregularity(int[] weights) {
		if (weights == null || weights.length == 0)
			return 0.0;

		double average = calculateAverage(weights);
		return calculateVariance(weights, average);
	}

	public static int calculateSum(int[] weights) {
		int sum = 0;
		for (int weight : weights)
			sum += weight;

		return sum;
	}

	public static double[] calculateDeclines(double[] baseCosts, double[] elongCosts, double speed) {
		double[] declines = new double[baseCosts.length];
		for (int i = 0; i < baseCosts.length; i++)
			declines[i] = calculateSingleDecline(baseCosts[i], elongCosts[i], speed);

		return declines;
	}

	public static int max(int[] data) {
		if (data == null || data.length == 0)
			return 0;

		return Arrays.stream(data).max().getAsInt();
	}

	public static double max(double[] data) {
		if (data == null || data.length == 0)
			return 0.0;

		return Arrays.stream(data).max().orElse(0.0);
	}

	public static boolean contains(int[] data, int value) {
		if (data == null)
			return false;

		for (int item : data) {
			if (item == value)
				return true;
		}
		return false;
	}

	public static double min(double[] data) {
		return Arrays.stream(data).min().orElse(0.0);
	}

	public static double avg(double[] data) {
		return Arrays.stream(data).average().orElse(0.0);
	}

	public static double mode(double[] data) {
		if (data == null || data.length == 0)
			return 0.0;
		Map<Double, Integer> frequencies = buildFrequencyMap(data);
		return findModeInMap(frequencies);
	}

	private static double calculateAverage(int[] weights) {
		return (double) calculateSum(weights) / weights.length;
	}

	private static double calculateVariance(int[] weights, double average) {
		double sumSqDiff = 0;
		for (int weight : weights)
			sumSqDiff += Math.pow((double) weight - average, 2);

		return sumSqDiff / weights.length;
	}

	private static double calculateSingleDecline(double baseCost, double elongCost, double speed) {
		if (elongCost == 0)
			return speed;
		return speed - ((baseCost / elongCost) * speed);
	}

	private static Map<Double, Integer> buildFrequencyMap(double[] data) {
		Map<Double, Integer> frequencies = new LinkedHashMap<>();
		for (double value : data)
			frequencies.put(value, frequencies.getOrDefault(value, 0) + 1);

		return frequencies;
	}

	private static double findModeInMap(Map<Double, Integer> frequencies) {
		double modeValue = 0.0;
		int maxCount = -1;

		for (Map.Entry<Double, Integer> entry : frequencies.entrySet()) {
			if (entry.getValue() > maxCount) {
				maxCount = entry.getValue();
				modeValue = entry.getKey();
			}
		}
		return modeValue;
	}
}
