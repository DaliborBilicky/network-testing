package network.testing.core.ds;

public interface ReadOnlyForwardStar {
	int getHead(int u);

	int getNext(int arcId);

	int getTarget(int arcId);

	int getGlobalEdgeId(int arcId);
}
