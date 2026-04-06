package network.testing.io.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import network.testing.domain.model.Topology;

public class NetworkWriter {
	public static void writeVertices(String path, int[] weights) throws IOException {
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(path)))) {
			writer.println(weights.length);
			for (int i = 0; i < weights.length; i++)
				writer.printf(Locale.US, "%d %d%n", i + 1, weights[i]);
		}
	}

	public static void writeEdges(String path, Topology topology) throws IOException {
		int numOfEdges = topology.getNumOfEdges();
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(path)))) {
			writer.println(numOfEdges);
			for (int i = 0; i < numOfEdges; i++) {
				writer.printf(Locale.US, "%d %d %d%n", topology.getEdgeU(i) + 1, topology.getEdgeV(i) + 1, 1);
			}
		}
	}
}
