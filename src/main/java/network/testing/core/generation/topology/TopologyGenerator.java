package network.testing.core.generation.topology;

public interface TopologyGenerator {
	void generate();

	int getNumOfVerts();

	int getNumOfEdges();

	int[] getU();

	int[] getV();
}
