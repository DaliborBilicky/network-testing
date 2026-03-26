package network.testing.logic.generator.topology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import network.testing.core.ds.UnionFind;

public class RandomizedSpanningModifier implements TopologyGenerator {
	private final TopologyGenerator base;
	private final double extraEdgeRatio;
	private final Random random;

	private int[] u;
	private int[] v;
	private int m;

	public RandomizedSpanningModifier(TopologyGenerator base, double extraEdgeRatio, Random random) {
		this.base = base;
		this.extraEdgeRatio = extraEdgeRatio;
		this.random = random;

	}

	@Override
	public void generate() {
		this.base.generate();

		int numOfVerts = this.base.getNumOfVerts();
		int[] baseU = this.base.getU();
		int[] baseV = this.base.getV();

		int numOfEdges = this.base.getNumOfEdges();
		this.u = new int[numOfEdges];
		this.v = new int[numOfEdges];
		this.m = 0;

		List<Integer> allPossible = this.shuffleBase(numOfEdges);

		UnionFind unionFind = new UnionFind(numOfVerts);
		List<Integer> remaining = new ArrayList<>();

		for (int id : allPossible) {
			int u = baseU[id];
			int v = baseV[id];

			if (unionFind.union(u, v)) {
				this.u[this.m] = u;
				this.v[this.m] = v;
				this.m++;
			} else {
				remaining.add(id);
			}
		}
		this.addExtraEdges(remaining, baseU, baseV);
	}

	@Override
	public int getNumOfVerts() {
		return this.base.getNumOfVerts();
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

	private List<Integer> shuffleBase(int numOfEdges) {
		List<Integer> verts = new ArrayList<>(numOfEdges);
		for (int i = 0; i < numOfEdges; i++)
			verts.add(i);
		Collections.shuffle(verts);

		return verts;
	}

	private void addExtraEdges(List<Integer> remaining, int[] baseU, int[] baseV) {
		for (int id : remaining) {
			if (this.random.nextDouble() < this.extraEdgeRatio) {
				this.u[this.m] = baseU[id];
				this.v[this.m] = baseV[id];
				this.m++;
			}
		}
	}
}
