package network.testing.core.services.generation.weight;

import java.util.Random;

public interface WeightDistributor {
	int[] distribute(int numOfVerts, Random random);
}
