package network.testing.ui.controller;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import network.testing.core.model.DistanceMatrix;
import network.testing.core.model.Network;
import network.testing.core.model.result.AllKResult;
import network.testing.core.model.result.FirstKResult;
import network.testing.core.model.result.SolutionSnapshot;
import network.testing.io.file.NetworkLoader;
import network.testing.io.file.ResultIO;
import network.testing.logic.layout.NetworkLayout;
import network.testing.logic.math.MetricsUtils;
import network.testing.logic.pathfinding.Dijkstra;
import network.testing.logic.sensitivity.EdgeElongator;
import network.testing.ui.component.InteractiveCanvas;

public class VisualizationController {
	private final InteractiveCanvas canvas;
	private Network network;
	private Point2D[] positions;
	private EdgeElongator elongator;
	private double baseSpeed = 110.0;

	private List<SolutionSnapshot> currentSnapshots;

	public VisualizationController(InteractiveCanvas canvas) {
		this.canvas = canvas;
		this.currentSnapshots = new ArrayList<>();
	}

	public void loadNetwork(File vertexFile, File edgeFile, File coordFile) throws IOException {
		this.network = NetworkLoader.load(vertexFile, edgeFile);

		if (coordFile != null && coordFile.exists()) {
			double[][] raw = NetworkLoader.loadCoordinates(coordFile, this.network.getTopology().getNumOfVerts());
			this.positions = NetworkLayout.fromCoordinates(raw);
		} else {
			this.positions = NetworkLayout.generateGrid(this.network.getTopology().getNumOfVerts());
		}

		DistanceMatrix dm = Dijkstra.calculateDistanceMatrix(this.network);
		this.elongator = new EdgeElongator(this.network, dm);
	}

	public Double[] prepareResults(File resultFile, int p) throws IOException {
		this.currentSnapshots.clear();

		try {
			FirstKResult res = ResultIO.loadFirstK(resultFile, p);
			if (res != null) {
				this.currentSnapshots.add(res.original());
				if (res.modified() != null)
					this.currentSnapshots.add(res.modified());
			}
		} catch (Exception e) {
			AllKResult res = ResultIO.loadAllK(resultFile, p);
			if (res != null)
				this.currentSnapshots.addAll(res.snapshots());
		}

		return this.currentSnapshots.stream()
				.map(SolutionSnapshot::k)
				.toArray(Double[]::new);
	}

	public void updateView(int selectedIndex) {
		if (this.currentSnapshots.isEmpty() || selectedIndex < 0 || selectedIndex >= this.currentSnapshots.size())
			return;

		SolutionSnapshot current = this.currentSnapshots.get(selectedIndex);
		SolutionSnapshot previous = (selectedIndex > 0) ? this.currentSnapshots.get(selectedIndex - 1) : null;

		this.display(previous, current);
	}

	private void display(SolutionSnapshot original, SolutionSnapshot modified) {
		int[] oldIds = (original != null) ? original.medians().ids()
				: (modified != null ? modified.medians().ids() : new int[0]);
		int[] newIds = (modified != null) ? modified.medians().ids() : new int[0];

		if (original == null)
			oldIds = newIds;

		double[] declines = this.calculateDeclines(modified);
		this.canvas.setData(this.network, oldIds, newIds, declines, this.positions);
	}

	private double[] calculateDeclines(SolutionSnapshot snapshot) {
		if (snapshot == null || this.elongator == null)
			return new double[this.network.getTopology().getNumOfEdges()];

		double[] baseCosts = this.network.copyEdgeCosts();
		double[] elongCosts = this.elongator.getElongatedCosts(snapshot.k());
		return MetricsUtils.calculateDeclines(baseCosts, elongCosts, this.baseSpeed);
	}

	public void setBaseSpeed(double speed) {
		this.baseSpeed = speed;
	}
}
