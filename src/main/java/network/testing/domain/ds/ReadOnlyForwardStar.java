package network.testing.domain.ds;

public interface ReadOnlyForwardStar {
	int getHead(int u);

	int getNext(int arcId);

	int getTarget(int arcId);

	int getGlobalEdgeId(int arcId);
}
