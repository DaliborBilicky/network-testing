package network.testing.core.ds;

import java.util.Arrays;

public class ForwardStar implements ReadOnlyForwardStar {
	private final int[] head;
	private final int[] next;
	private final int[] target;
	private final int[] edgeIdLookup;
	private int arcCount;

	public ForwardStar(int n, int m) {
		this.head = new int[n];
		Arrays.fill(this.head, -1);

		this.next = new int[m * 2];
		this.target = new int[m * 2];
		this.edgeIdLookup = new int[m * 2];
		this.arcCount = 0;
	}

	public void addEdge(int u, int v, int globalEdgeId) {
		this.addDirectedEdge(u, v, globalEdgeId);
		this.addDirectedEdge(v, u, globalEdgeId);
	}

	private void addDirectedEdge(int u, int v, int globalEdgeId) {
		this.target[this.arcCount] = v;
		this.edgeIdLookup[this.arcCount] = globalEdgeId;
		this.next[this.arcCount] = this.head[u];
		this.head[u] = this.arcCount++;
	}

	@Override
	public int getHead(int u) {
		return this.head[u];
	}

	@Override
	public int getNext(int arcId) {
		return this.next[arcId];
	}

	@Override
	public int getTarget(int arcId) {
		return this.target[arcId];
	}

	@Override
	public int getGlobalEdgeId(int arcId) {
		return this.edgeIdLookup[arcId];
	}
}
