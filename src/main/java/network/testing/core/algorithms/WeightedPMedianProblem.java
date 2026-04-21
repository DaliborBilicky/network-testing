package network.testing.core.algorithms;

import java.util.Arrays;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import network.testing.domain.model.network.DistanceMatrix;

public class WeightedPMedianProblem {
	static {
		Loader.loadNativeLibraries();
	}

	private static final double INFINITY_PENALTY = 1_000_000_000.0;

	private final int[] weights;
	private final DistanceMatrix distMatrix;
	private final int p;
	private final int n;

	private double objectiveValue;
	private int[] selectedMedians;
	private int[] assignments;

	private MPSolver solver;
	private MPVariable[] y;
	private MPVariable[][] x;

	public WeightedPMedianProblem(int[] weights, DistanceMatrix distMatrix, int p) {
		this.weights = weights.clone();
		this.distMatrix = distMatrix;
		this.p = p;
		this.n = weights.length;

		this.solver = MPSolver.createSolver("HiGHS");
		this.y = new MPVariable[this.n];
		this.x = new MPVariable[this.n][this.n];
	}

	public boolean solve() {
		if (this.solver == null)
			return false;

		this.createVariablesY();
		this.createVariablesX();

		this.addPLimitConstraint();
		this.addAssignmentAndLinkingConstraints();

		this.setupObjective();

		MPSolver.ResultStatus status = this.solver.solve();

		if (status == MPSolver.ResultStatus.OPTIMAL || status == MPSolver.ResultStatus.FEASIBLE) {
			this.objectiveValue = this.solver.objective().value();
			this.extractMedians();
			this.extractAssignments();
			return true;
		}
		return false;
	}

	private void createVariablesY() {
		for (int j = 0; j < this.n; j++) {
			this.y[j] = this.solver.makeBoolVar("y_" + j);
			if (this.weights[j] == 0)
				this.y[j].setUb(0);
		}

	}

	private void createVariablesX() {
		for (int i = 0; i < this.n; i++) {
			if (this.weights[i] > 0) {
				for (int j = 0; j < this.n; j++)
					this.x[i][j] = this.solver.makeBoolVar("x_" + i + "_" + j);
			}
		}
	}

	private void addPLimitConstraint() {
		MPConstraint pLimit = this.solver.makeConstraint(this.p, this.p, "p_limit");
		for (int j = 0; j < this.n; j++)
			pLimit.setCoefficient(this.y[j], 1);
	}

	private void addAssignmentAndLinkingConstraints() {
		for (int i = 0; i < this.n; i++) {
			if (this.weights[i] > 0) {
				MPConstraint assign = this.solver.makeConstraint(1, 1, "assign_" + i);
				for (int j = 0; j < this.n; j++) {
					assign.setCoefficient(this.x[i][j], 1);

					MPConstraint link = this.solver.makeConstraint(Double.NEGATIVE_INFINITY, 0, "link_" + i + "_" + j);
					link.setCoefficient(this.x[i][j], 1);
					link.setCoefficient(this.y[j], -1);
				}
			}
		}
	}

	private void setupObjective() {
		MPObjective objective = this.solver.objective();
		for (int i = 0; i < this.n; i++) {
			if (this.weights[i] > 0) {
				for (int j = 0; j < this.n; j++) {
					double distance = this.distMatrix.getDistance(i, j);
					double cost = (distance == Double.POSITIVE_INFINITY) ? INFINITY_PENALTY : distance;
					objective.setCoefficient(this.x[i][j], (double) this.weights[i] * cost);
				}
			}
		}
		objective.setMinimization();
	}

	private void extractMedians() {
		this.selectedMedians = new int[this.p];
		int mCount = 0;
		for (int id = 0; id < this.n; id++) {
			if (this.y[id].solutionValue() > 0.5 && mCount < this.p)
				this.selectedMedians[mCount++] = id;
		}
		Arrays.sort(this.selectedMedians);
	}

	private void extractAssignments() {
		this.assignments = new int[this.n];
		Arrays.fill(this.assignments, -1);

		for (int i = 0; i < this.n; i++) {
			if (this.weights[i] > 0) {
				for (int medianId : this.selectedMedians) {
					if (this.x[i][medianId] != null && this.x[i][medianId].solutionValue() > 0.5) {
						this.assignments[i] = medianId;
						break;
					}
				}
			}
		}
	}

	public double getObjectiveValue() {
		return this.objectiveValue;
	}

	public int[] getSelectedMedians() {
		return this.selectedMedians.clone();
	}

	public int[] getAssignments() {
		return this.assignments.clone();
	}
}
