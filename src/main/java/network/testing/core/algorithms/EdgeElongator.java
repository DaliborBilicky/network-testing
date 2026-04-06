package network.testing.core.algorithms;

import java.util.Arrays;

import network.testing.domain.model.DistanceMatrix;
import network.testing.domain.model.Network;
import network.testing.domain.model.Topology;

public class EdgeElongator {
	private final Network network;
	private final Topology topology;
	private final int numOfEdges;
	private final double[] fracList;
	private final double denominator;
	private double kLim;

	public EdgeElongator(Network network, DistanceMatrix distMatrix) {
		this.network = network;
		this.topology = this.network.getTopology();
		this.numOfEdges = network.getTopology().getNumOfEdges();
		this.fracList = new double[this.numOfEdges];

		this.calculateFracList(distMatrix);
		this.denominator = Arrays.stream(this.fracList).sum();
		this.calculateKUpperLimit();
	}

	public double[] getElongatedCosts(double k) {
		double[] elongatedCosts = new double[this.numOfEdges];

		for (int i = 0; i < this.numOfEdges; i++) {
			double factor = 1.0 - (this.fracList[i] * (k / this.denominator));

			elongatedCosts[i] = this.network.getEdgeCost(i) / factor;
		}
		return elongatedCosts;
	}

	public double getKLim() {
		return this.kLim;
	}

	private void calculateFracList(DistanceMatrix distMatrix) {
		int numOfVerts = this.topology.getNumOfVerts();

		for (int i = 0; i < this.numOfEdges; i++) {
			int u = this.topology.getEdgeU(i);
			int v = this.topology.getEdgeV(i);
			double cost = this.network.getEdgeCost(i);
			double fracSum = 0.0;

			for (int targetId = 0; targetId < numOfVerts; targetId++) {
				int weight = this.network.getVertexWeight(targetId);
				if (weight == 0)
					continue;

				double distU = distMatrix.getDistance(u, targetId);
				double distV = distMatrix.getDistance(v, targetId);

				double distEdgeVertex = Math.min(distU, distV) + (cost / 2.0);

				if (distEdgeVertex > 0)
					fracSum += (weight / distEdgeVertex);
			}
			this.fracList[i] = fracSum;
		}
	}

	private void calculateKUpperLimit() {
		double xMin = Double.POSITIVE_INFINITY;
		for (double fraction : this.fracList) {
			if (fraction > 0) {
				double x = this.denominator / fraction;
				xMin = Math.min(x, xMin);
			}
		}

		this.kLim = (xMin != Double.POSITIVE_INFINITY) ? (xMin - 1.0) : 0.0;
	}
}
