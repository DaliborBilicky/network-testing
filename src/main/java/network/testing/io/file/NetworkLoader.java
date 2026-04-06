package network.testing.io.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import network.testing.domain.model.Network;
import network.testing.domain.model.Topology;

public class NetworkLoader {
	private static record EdgeData(int m, int[] u, int[] v, double[] costs) {
		EdgeData(int m) {
			this(m, new int[m], new int[m], new double[m]);
		}
	}

	public static Network load(File vertexFile, File edgeFile) throws IOException {
		int[] weights = parseVertices(vertexFile);

		EdgeData edges = parseEdges(edgeFile);

		Topology topology = new Topology(weights.length, edges.m(), edges.u(), edges.v());
		return new Network(topology, edges.costs(), weights);
	}

	public static double[][] loadCoordinates(File posFile, int nodeCount) throws IOException {
		double[][] coords = new double[nodeCount][2];
		try (BufferedReader reader = new BufferedReader(new FileReader(posFile))) {
			String line;
			int i = 0;
			while (i < nodeCount && (line = reader.readLine()) != null) {
				String[] parts = line.trim().split("\\s+");
				if (parts.length >= 2) {
					coords[i][0] = Double.parseDouble(parts[0]);
					coords[i][1] = Double.parseDouble(parts[1]);
					i++;
				}
			}
		}
		return coords;
	}

	private static int[] parseVertices(File file) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			int n = readHeader(reader, "Vertex");
			int[] weights = new int[n];

			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.trim().split("\\s+");
				if (parts.length < 1)
					continue;

				int id = Integer.parseInt(parts[0]) - 1;
				if (id >= 0 && id < n && parts.length >= 2)
					weights[id] = Integer.parseInt(parts[1]);
				else
					throw new IOException("Vertex ID " + (id + 1) + " is out of bounds (1-" + n + ")");
			}
			return weights;
		}
	}

	private static EdgeData parseEdges(File file) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			int m = readHeader(reader, "Edge");
			EdgeData data = new EdgeData(m);

			String line;
			int i = 0;
			while ((line = reader.readLine()) != null && i < m) {
				String[] parts = line.trim().split("\\s+");
				if (parts.length < 3)
					continue;

				data.u()[i] = Integer.parseInt(parts[0]) - 1;
				data.v()[i] = Integer.parseInt(parts[1]) - 1;
				data.costs()[i] = Double.parseDouble(parts[2]);
				i++;
			}
			return data;
		}
	}

	private static int readHeader(BufferedReader reader, String context) throws IOException {
		String header = reader.readLine();
		if (header == null || header.trim().isEmpty())
			throw new IOException(context + " file is empty or missing header.");
		try {
			return Integer.parseInt(header.trim());
		} catch (NumberFormatException e) {
			throw new IOException("Invalid " + context + " header: expected a number, got '" + header + "'");
		}
	}
}
