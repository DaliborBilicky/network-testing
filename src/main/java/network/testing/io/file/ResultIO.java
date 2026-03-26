package network.testing.io.file;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import network.testing.core.model.result.AllKResult;
import network.testing.core.model.result.FirstKResult;

public class ResultIO {
	private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	public static void saveFirstK(File file, int p, FirstKResult result) throws IOException {
		saveToMap(file, String.valueOf(p), result, new TypeReference<Map<String, FirstKResult>>() {
		});
	}

	public static void saveAllK(File file, int p, AllKResult result) throws IOException {
		saveToMap(file, String.valueOf(p), result, new TypeReference<Map<String, AllKResult>>() {
		});
	}

	public static FirstKResult loadFirstK(File file, int p) throws IOException {
		Map<String, FirstKResult> data = MAPPER.readValue(file, new TypeReference<>() {
		});
		return data.get(String.valueOf(p));
	}

	public static AllKResult loadAllK(File file, int p) throws IOException {
		Map<String, AllKResult> data = MAPPER.readValue(file, new TypeReference<>() {
		});
		return data.get(String.valueOf(p));
	}

	private static <T> void saveToMap(File file, String key, T result, TypeReference<Map<String, T>> typeRef)
			throws IOException {
		Map<String, T> allResults = new LinkedHashMap<>();

		if (file.exists() && file.length() > 0) {
			allResults = MAPPER.readValue(file, typeRef);
		} else {
			if (file.getParentFile() != null)
				file.getParentFile().mkdirs();
		}

		allResults.put(key, result);
		MAPPER.writeValue(file, allResults);
	}
}
