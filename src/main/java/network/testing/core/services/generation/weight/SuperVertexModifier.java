package network.testing.core.services.generation.weight;

import java.util.Arrays;
import java.util.Random;

public class SuperVertexModifier implements WeightDistributor {
	private final WeightDistributor base;
	private final int topN;

	private int[] weights;

	public SuperVertexModifier(WeightDistributor base, int topN) {
		this.base = base;
		this.topN = topN;
		this.weights = null;
	}

	@Override
	public int[] distribute(int numOfVerts, Random random) {
		this.weights = this.base.distribute(numOfVerts, random);

		Integer[] verts = this.sortVertsByWeight();
		int maxId = this.getBiggestCity();

		double stolenWeight = 0;
		for (int i = 0; i < Math.min(this.topN, numOfVerts); i++) {
			int id = verts[i];
			if (id == maxId)
				continue;
			stolenWeight += this.weights[id];
			this.weights[id] = 0;
		}

		this.weights[maxId] += stolenWeight;
		return this.weights;
	}

	private Integer[] sortVertsByWeight() {
		Integer[] verts = new Integer[this.weights.length];
		for (int i = 0; i < this.weights.length; i++)
			verts[i] = i;

		Arrays.sort(verts, (a, b) -> Integer.compare(this.weights[a], this.weights[b]));

		return verts;
	}

	private int getBiggestCity() {
		int maxId = 0;
		for (int i = 0; i < this.weights.length; i++) {
			if (this.weights[i] > this.weights[maxId])
				maxId = i;
		}
		return maxId;
	}
}
