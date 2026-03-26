package network.testing.logic.generator.topology;

public interface TopologyGenerator {
	void generate();

	int getNumOfVerts();

	int getNumOfEdges();

	int[] getU();

	int[] getV();
}
