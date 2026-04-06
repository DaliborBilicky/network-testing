package network.testing.io.file;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import network.testing.domain.model.project.ProjectSettings;
import network.testing.domain.model.result.AllKResult;
import network.testing.domain.model.result.FirstKResult;

public class ProjectIO {
	private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	public static void saveProject(File file, ProjectSettings settings) throws IOException {
		MAPPER.writeValue(file, settings);
	}

	public static ProjectSettings loadProject(File file) throws IOException {
		return MAPPER.readValue(file, ProjectSettings.class);
	}

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

		if (file.exists() && file.length() > 0)
			allResults = MAPPER.readValue(file, typeRef);

		allResults.put(key, result);
		MAPPER.writeValue(file, allResults);
	}
}
