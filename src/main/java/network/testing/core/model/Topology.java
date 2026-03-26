package network.testing.core.model;

import network.testing.core.ds.ForwardStar;
import network.testing.core.ds.ReadOnlyForwardStar;

public class Topology {
	private final int numOfVerts;
	private final int numOfEdges;
	private final int[] edgeU;
	private final int[] edgeV;
	private final ForwardStar star;

	public Topology(int n, int m, int[] u, int[] v) {
		this.numOfVerts = n;
		this.numOfEdges = m;

		this.edgeU = u.clone();
		this.edgeV = v.clone();

		this.star = new ForwardStar(n, m);
		for (int i = 0; i < m; i++)
			this.star.addEdge(this.edgeU[i], this.edgeV[i], i);
	}

	public int getNumOfVerts() {
		return this.numOfVerts;
	}

	public int getNumOfEdges() {
		return this.numOfEdges;
	}

	public int getEdgeU(int i) {
		return this.edgeU[i];
	}

	public int getEdgeV(int i) {
		return this.edgeV[i];
	}

	public ReadOnlyForwardStar getStar() {
		return this.star;
	}
}
