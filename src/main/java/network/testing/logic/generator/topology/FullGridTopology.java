package network.testing.logic.generator.topology;

public class FullGridTopology implements TopologyGenerator {
	private final int side;
	private int[] u;
	private int[] v;
	private int m;

	public FullGridTopology(int side) {
		this.side = side;
	}

	@Override
	public void generate() {
		int n = this.getNumOfVerts();
		this.u = new int[2 * n];
		this.v = new int[2 * n];
		this.m = 0;

		for (int i = 0; i < this.side; i++) {
			for (int j = 0; j < this.side; j++) {
				int vertex = i * this.side + j;
				if (j + 1 < this.side)
					this.add(vertex, vertex + 1);
				if (i + 1 < this.side)
					this.add(vertex, vertex + this.side);
			}
		}
	}

	private void add(int u, int v) {
		this.u[this.m] = u;
		this.v[this.m] = v;
		this.m++;
	}

	@Override
	public int getNumOfVerts() {
		return this.side * this.side;
	}

	@Override
	public int getNumOfEdges() {
		return this.m;
	}

	@Override
	public int[] getU() {
		return this.u;
	}

	@Override
	public int[] getV() {
		return this.v;
	}
}
