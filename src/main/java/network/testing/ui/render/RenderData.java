package network.testing.ui.render;

import java.awt.geom.Point2D;

import network.testing.domain.model.network.Network;

public record RenderData(
		Network network,
		Point2D[] positions,
		int[] originalMedians,
		int[] modifiedMedians,
		double[] declines) {
}
