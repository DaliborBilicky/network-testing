package network.testing.core.generation.weight;

import java.util.Arrays;
import java.util.Random;

public class UniformityModifier implements WeightDistributor {
	private final WeightDistributor base;
	private final int topN;
	private final int population;
	private final double k;

	private int[] weights;

	public UniformityModifier(WeightDistributor base, int topN, int population, double k) {
		this.base = base;
		this.topN = topN;
		this.population = population;
		this.weights = null;
		this.k = k;
	}

	@Override
	public int[] distribute(int numOfVerts, Random random) {
		this.weights = this.base.distribute(numOfVerts, random);

		int weightPool = this.extractWeights();

		int sum = this.population - weightPool;
		while (weightPool > 0) {
			int currentSum = sum;
			for (int i = 0; i < numOfVerts; i++) {
				if (weightPool <= 0)
					break;
				double p = Math.exp(-this.k * (1 - (this.weights[i] / currentSum)));
				if (random.nextDouble() < p) {
					this.weights[i]++;
					sum++;
					weightPool--;
				}
			}
		}

		return this.weights;
	}

	private Integer[] sortVertsByWeight() {
		Integer[] verts = new Integer[this.weights.length];
		for (int i = 0; i < this.weights.length; i++)
			verts[i] = i;

		Arrays.sort(verts, (a, b) -> Integer.compare(this.weights[b], this.weights[a]));

		return verts;
	}

	private int extractWeights() {
		Integer[] verts = this.sortVertsByWeight();
		int avg = this.population / verts.length;
		int weightPool = 0;
		for (int i = 0; i < Math.min(this.topN, verts.length); i++) {
			int id = verts[i];
			if (this.weights[id] > avg) {
				weightPool += (this.weights[id] - avg);
				this.weights[id] = avg;
			}
		}

		return weightPool;
	}
}
