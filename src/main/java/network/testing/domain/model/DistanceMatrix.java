package network.testing.domain.model;

public class DistanceMatrix {
	private final int n;
	private final double[] distances;

	public DistanceMatrix(int n, double[] rawDistances) {
		this.n = n;
		this.distances = rawDistances.clone();
	}

	public double getDistance(int u, int v) {
		return this.distances[u * this.n + v];
	}

	public int getSize() {
		return this.n;
	}
}
