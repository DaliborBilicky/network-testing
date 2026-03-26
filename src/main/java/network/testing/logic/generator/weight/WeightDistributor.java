package network.testing.logic.generator.weight;

import java.util.Random;

public interface WeightDistributor {
	int[] distribute(int numOfVerts, Random random);
}
