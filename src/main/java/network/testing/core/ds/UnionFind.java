package network.testing.core.ds;

public class UnionFind {
	private final int[] parent;
	private final int[] rank;

	public UnionFind(int n) {
		this.parent = new int[n];
		this.rank = new int[n];
		for (int i = 0; i < n; i++) {
			this.parent[i] = i;
			this.rank[i] = 0;
		}
	}

	public int find(int i) {
		int root = i;
		while (this.parent[root] != root)
			root = this.parent[root];

		while (this.parent[i] != root) {
			int nextNode = this.parent[i];
			this.parent[i] = root;
			i = nextNode;
		}
		return root;
	}

	public boolean union(int i, int j) {
		int rootI = find(i);
		int rootJ = find(j);

		if (rootI != rootJ) {
			if (this.rank[rootI] < this.rank[rootJ]) {
				this.parent[rootI] = rootJ;
			} else if (this.rank[rootI] > this.rank[rootJ]) {
				this.parent[rootJ] = rootI;
			} else {
				this.parent[rootI] = rootJ;
				this.rank[rootJ]++;
			}
			return true;
		}
		return false;
	}
}
