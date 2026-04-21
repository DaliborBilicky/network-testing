package network.testing.app;

import java.awt.geom.Point2D;

import network.testing.core.algorithms.Dijkstra;
import network.testing.core.algorithms.EdgeElongator;
import network.testing.domain.model.network.DistanceMatrix;
import network.testing.domain.model.network.Network;

public record ProjectContext(
		Network network,
		DistanceMatrix baseDistMatrix,
		EdgeElongator elongator,
		Point2D[] positions) {

	public static ProjectContext create(Network network, Point2D[] positions) {
		DistanceMatrix baseMatrix = Dijkstra.calculateDistanceMatrix(network);
		EdgeElongator elongator = new EdgeElongator(network, baseMatrix);

		return new ProjectContext(network, baseMatrix, elongator, positions);
	}
}
