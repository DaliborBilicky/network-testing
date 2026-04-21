package network.testing.core.generation.weight;

import java.util.Random;

public class PopulationDistributor implements WeightDistributor {
	private final int population;
	private final double cityChance;
	private final double k;

	private int[] weights;

	public PopulationDistributor(int population, double cityChance, double k) {
		this.population = population;
		this.cityChance = cityChance;
		this.k = k;
		this.weights = null;
	}

	@Override
	public int[] distribute(int numOfVerts, Random random) {
		this.weights = new int[numOfVerts];

		this.pickCities(random);

		int sum = this.sumWeights();

		int currentSum = 0;
		while (currentSum < this.population) {
			currentSum = sum;
			for (int i = 0; i < numOfVerts; i++) {
				if (sum >= this.population)
					break;
				double x = (double) this.weights[i] / currentSum;
				double p = 1.0 - Math.exp(-this.k * x);
				if (random.nextDouble() < p) {
					this.weights[i]++;
					sum++;
				}
			}
		}

		return this.weights;
	}

	private void pickCities(Random random) {
		for (int i = 0; i < this.weights.length; i++) {
			if (random.nextDouble() < this.cityChance)
				this.weights[i]++;
		}
	}

	private int sumWeights() {
		int sum = 0;
		for (int w : this.weights)
			sum += w;

		return sum;
	}
}
