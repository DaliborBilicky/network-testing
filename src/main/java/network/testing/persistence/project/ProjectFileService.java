package network.testing.persistence.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ProjectFileService {
	public static void extractProject(Path zipPath, Path targetDir) throws IOException {
		if (!Files.exists(targetDir))
			Files.createDirectories(targetDir);

		try (ZipInputStream zipInStream = new ZipInputStream(new FileInputStream(zipPath.toFile()))) {
			ZipEntry entry;
			while ((entry = zipInStream.getNextEntry()) != null) {
				Path newPath = targetDir.resolve(entry.getName());
				if (entry.isDirectory()) {
					Files.createDirectories(newPath);
				} else {
					Files.createDirectories(newPath.getParent());
					Files.copy(zipInStream, newPath, StandardCopyOption.REPLACE_EXISTING);
				}
				zipInStream.closeEntry();
			}
		}
	}

	public static void archiveProject(Path sourceDir, Path targetZip) throws IOException {
		try (ZipOutputStream zipOutStream = new ZipOutputStream(new FileOutputStream(targetZip.toFile()))) {
			Files.walk(sourceDir).forEach(path -> {
				if (Files.isDirectory(path))
					return;
				ZipEntry entry = new ZipEntry(sourceDir.relativize(path).toString());
				try {
					zipOutStream.putNextEntry(entry);
					Files.copy(path, zipOutStream);
					zipOutStream.closeEntry();
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
		}
	}

	public static void deleteDirectory(Path path) throws IOException {
		if (!Files.exists(path))
			return;
		Files.walk(path)
				.sorted(Comparator.reverseOrder())
				.map(Path::toFile)
				.forEach(File::delete);
	}

	public static void createProject(Path srcVert, Path srcEdge, Path srcCoord, Path targetDir) throws IOException {
		Files.createDirectories(targetDir);
		Files.copy(srcVert, targetDir.resolve("vertices.txt"), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(srcEdge, targetDir.resolve("edges.txt"), StandardCopyOption.REPLACE_EXISTING);

		if (srcCoord != null && Files.exists(srcCoord))
			Files.copy(srcCoord, targetDir.resolve("coords.txt"), StandardCopyOption.REPLACE_EXISTING);
	}

	public static Path prepareWorkspace() {
		String tempName = "ntp_new_" + UUID.randomUUID().toString().substring(0, 8);
		return Paths.get(System.getProperty("java.io.tmpdir"), tempName);
	}
}
