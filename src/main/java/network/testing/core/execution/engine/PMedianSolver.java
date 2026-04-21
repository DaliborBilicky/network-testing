package network.testing.core.execution.engine;

import network.testing.app.ProjectContext;
import network.testing.core.algorithms.Dijkstra;
import network.testing.core.algorithms.WeightedPMedianProblem;
import network.testing.domain.model.network.DistanceMatrix;
import network.testing.domain.model.network.Network;

public class PMedianSolver {
	private final ProjectContext context;
	private final int p;

	public PMedianSolver(ProjectContext context, int p) {
		this.context = context;
		this.p = p;
	}

	public SolverResult solve(double k) throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();

		double[] elongatedCosts = this.context.elongator().getElongatedCosts(k);
		Network networkK = new Network(this.context.network().getTopology(), elongatedCosts);

		DistanceMatrix distMatrix = Dijkstra.calculateDistanceMatrix(networkK);

		WeightedPMedianProblem problem = new WeightedPMedianProblem(
				this.context.network().copyVertexWeights(), distMatrix, this.p);

		if (problem.solve())
			return new SolverResult(k, problem, distMatrix, elongatedCosts);
		return null;
	}

	public double getKLim() {
		return this.context.elongator().getKLim();
	}
}
