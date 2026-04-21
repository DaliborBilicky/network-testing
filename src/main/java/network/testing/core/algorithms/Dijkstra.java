package network.testing.core.algorithms;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import network.testing.domain.ds.ReadOnlyForwardStar;
import network.testing.domain.model.network.DistanceMatrix;
import network.testing.domain.model.network.Network;
import network.testing.domain.model.network.Topology;

public class Dijkstra {
	private record Vertex(int id, double dist) {
	}

	public static DistanceMatrix calculateDistanceMatrix(Network network) {
		int n = network.getTopology().getNumOfVerts();
		double[] fullData = new double[n * n];

		for (int i = 0; i < n; i++) {
			double[] row = findDistances(network, i);
			System.arraycopy(row, 0, fullData, i * n, n);
		}

		return new DistanceMatrix(n, fullData);
	}

	public static double[] findDistances(Network network, int start) {
		return run(network, start, -1);
	}

	public static double findDistanceBetween(Network network, int start, int end) {
		if (start == end)
			return 0.0;
		double[] dists = run(network, start, end);
		return dists[end];
	}

	private static double[] run(Network network, int start, int end) {
		Topology topology = network.getTopology();
		int n = topology.getNumOfVerts();

		double[] distances = new double[n];
		Arrays.fill(distances, Double.POSITIVE_INFINITY);
		distances[start] = 0;

		PriorityQueue<Vertex> queue = new PriorityQueue<>(Comparator.comparingDouble(Vertex::dist));
		queue.add(new Vertex(start, 0));

		ReadOnlyForwardStar star = topology.getStar();

		while (!queue.isEmpty()) {
			Vertex current = queue.poll();
			int u = current.id();
			double d = current.dist();

			if (end != -1 && u == end)
				break;

			if (d > distances[u])
				continue;

			for (int arc = star.getHead(u); arc != -1; arc = star.getNext(arc)) {
				int v = star.getTarget(arc);
				double cost = network.getEdgeCost(star.getGlobalEdgeId(arc));

				if (distances[u] + cost < distances[v]) {
					distances[v] = distances[u] + cost;
					queue.add(new Vertex(v, distances[v]));
				}
			}
		}
		return distances;
	}
}
