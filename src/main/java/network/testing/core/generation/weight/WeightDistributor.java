package network.testing.core.generation.weight;

import java.util.Random;

public interface WeightDistributor {
	int[] distribute(int numOfVerts, Random random);
}
