package network.testing.core.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatisticsUtils {
	public static int sum(int[] data) {
		int sum = 0;
		for (int val : data)
			sum += val;
		return sum;
	}

	public static double avg(double[] data) {
		return Arrays.stream(data).average().orElse(0.0);
	}

	public static double min(double[] data) {
		return Arrays.stream(data).min().orElse(0.0);
	}

	public static double max(double[] data) {
		return Arrays.stream(data).max().orElse(0.0);
	}

	public static int max(int[] data) {
		return Arrays.stream(data).max().orElse(0);
	}

	public static double mode(double[] data) {
		if (data == null || data.length == 0)
			return 0.0;
		Map<Double, Integer> frequencies = new LinkedHashMap<>();
		for (double val : data)
			frequencies.put(val, frequencies.getOrDefault(val, 0) + 1);

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

	public static double variance(int[] data) {
		if (data.length == 0)
			return 0.0;
		double average = (double) sum(data) / data.length;
		double sumSqDiff = 0;
		for (int val : data)
			sumSqDiff += Math.pow(val - average, 2);
		return sumSqDiff / data.length;
	}
}
