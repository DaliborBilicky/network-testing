package network.testing.core.utils;

public class ArrayUtils {
	public static boolean contains(int[] data, int value) {
		if (data == null)
			return false;
		for (int item : data)
			if (item == value)
				return true;
		return false;
	}

	public static String intArrayToString(int[] arr) {
		if (arr == null)
			return "";
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			strBuilder.append(arr[i]);
			if (i < arr.length - 1)
				strBuilder.append(",");
		}
		return strBuilder.toString();
	}

	public static int[] stringToIntArray(String str) {
		if (str == null || str.isEmpty())
			return new int[0];
		String[] parts = str.split(",");
		int[] result = new int[parts.length];
		for (int i = 0; i < parts.length; i++)
			result[i] = Integer.parseInt(parts[i]);
		return result;
	}
}
