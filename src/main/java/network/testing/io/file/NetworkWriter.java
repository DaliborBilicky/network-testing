package network.testing.io.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import network.testing.core.model.Network;

public class NetworkWriter {
	public static void writeVertices(String path, int[] weights) throws IOException {
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(path)))) {
			writer.println(weights.length);
			for (int i = 0; i < weights.length; i++)
				writer.printf(Locale.US, "%d %d%n", i + 1, weights[i]);
		}
	}

	public static void writeEdges(String path, Network network) throws IOException {
		int m = network.getTopology().getNumOfEdges();
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(path)))) {
			writer.println(m);
			for (int i = 0; i < m; i++) {
				writer.printf(Locale.US, "%d %d %d%n",
						network.getTopology().getEdgeU(i) + 1,
						network.getTopology().getEdgeV(i) + 1,
						(int) network.getEdgeCost(i));
			}
		}
	}
}
