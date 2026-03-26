package network.testing.ui.controller;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Random;

import network.testing.core.model.Network;
import network.testing.core.model.Topology;
import network.testing.io.file.NetworkWriter;
import network.testing.logic.generator.topology.FullGridTopology;
import network.testing.logic.generator.topology.RandomizedSpanningModifier;
import network.testing.logic.generator.topology.TopologyGenerator;
import network.testing.logic.generator.weight.PopulationDistributor;
import network.testing.logic.generator.weight.SuperVertexModifier;
import network.testing.logic.generator.weight.UniformityModifier;
import network.testing.logic.generator.weight.WeightDistributor;
import network.testing.logic.layout.NetworkLayout;
import network.testing.ui.component.InteractiveCanvas;

public class GenerationController {
	private static final double K_REALISTIC = 1.5;
	private static final double K_UNIFORMITY = 4.0;

	private final InteractiveCanvas canvas;
	private Topology currentTopology;
	private int[] currentWeights;
	private Point2D[] positions;

	public GenerationController(InteractiveCanvas canvas) {
		this.canvas = canvas;
	}

	public void generateTopology(int side, String type, double extraRatio, String seedText) {
		Random random = this.createRandom(seedText);
		TopologyGenerator base = new FullGridTopology(side);
		TopologyGenerator finalGen;

		if ("Randomized Spanning".equals(type))
			finalGen = new RandomizedSpanningModifier(base, extraRatio, random);
		else
			finalGen = base;

		finalGen.generate();
		this.currentTopology = new Topology(
				finalGen.getNumOfVerts(),
				finalGen.getNumOfEdges(),
				finalGen.getU(),
				finalGen.getV());

		this.positions = NetworkLayout.generateGrid(this.currentTopology.getNumOfVerts());
		this.currentWeights = new int[this.currentTopology.getNumOfVerts()];
		this.updateCanvas();
	}

	public void generateWeights(int population, double cityChance, String distType, int topN, String seedText) {
		if (this.currentTopology == null)
			return;

		Random random = this.createRandom(seedText);

		WeightDistributor dist = new PopulationDistributor(population, cityChance, K_REALISTIC);

		if ("Uniformity Modifier".equals(distType))
			dist = new UniformityModifier(dist, topN, population, K_UNIFORMITY);
		else if ("SuperVertex Modifier".equals(distType))
			dist = new SuperVertexModifier(dist, topN);

		this.currentWeights = dist.distribute(this.currentTopology.getNumOfVerts(), random);
		this.updateCanvas();
	}

	private Random createRandom(String seedText) {
		if (seedText == null || seedText.trim().isEmpty())
			return new Random();

		try {
			long seed = Long.parseLong(seedText.trim());
			return new Random(seed);
		} catch (NumberFormatException e) {
			return new Random();
		}
	}

	private void updateCanvas() {
		if (this.currentTopology == null)
			return;

		Network tempNet = new Network(this.currentTopology, this.currentWeights);
		int[] empty = new int[0];
		double[] emptyDeclines = new double[this.currentTopology.getNumOfEdges()];
		this.canvas.setData(tempNet, empty, empty, emptyDeclines, this.positions);
	}

	public void saveEdges(String path) throws IOException {
		if (this.currentTopology == null)
			return;

		Network temp = new Network(this.currentTopology, new int[this.currentTopology.getNumOfVerts()]);
		NetworkWriter.writeEdges(path, temp);
	}

	public void saveVertices(String path) throws IOException {
		if (this.currentWeights == null)
			return;

		NetworkWriter.writeVertices(path, this.currentWeights);
	}
}
