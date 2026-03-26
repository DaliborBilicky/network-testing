package network.testing.ui.controller;

import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import network.testing.core.model.DistanceMatrix;
import network.testing.core.model.Network;
import network.testing.core.model.result.AllKResult;
import network.testing.core.model.result.FirstKResult;
import network.testing.io.file.NetworkLoader;
import network.testing.io.file.ResultIO;
import network.testing.logic.experiment.AllKExperiment;
import network.testing.logic.experiment.FirstKExperiment;
import network.testing.logic.pathfinding.Dijkstra;
import network.testing.logic.sensitivity.EdgeElongator;
import network.testing.ui.view.ExperimentView;

public class ExperimentController {
	private final ExperimentView view;
	private SwingWorker<Void, String> worker;

	public ExperimentController(ExperimentView view) {
		this.view = view;
	}

	public void startExperiment(String vertexPath, String edgePath, String outputPath,
			int pMin, int pMax, String type, double baseSpeed, double precision) {

		this.worker = new SwingWorker<>() {
			@Override
			protected Void doInBackground() throws Exception {
				this.publish("--- Starting Experiment Workflow ---");
				this.publish("Loading network data...");
				Network network = NetworkLoader.load(new File(vertexPath), new File(edgePath));

				this.publish("Calculating base distance matrix...");
				DistanceMatrix dm = Dijkstra.calculateDistanceMatrix(network);

				this.publish("Initializing Edge Elongator...");
				EdgeElongator elongator = new EdgeElongator(network, dm);

				File resultFile = new File(outputPath);

				for (int p = pMin; p <= pMax; p++) {
					if (this.isCancelled())
						break;

					this.publish("Processing p = " + p + "...");

					if ("First-K".equals(type)) {
						FirstKExperiment exp = new FirstKExperiment(network, p, dm, elongator, baseSpeed, precision);
						FirstKResult res = exp.run();
						if (res != null) {
							ResultIO.saveFirstK(resultFile, p, res);
							this.publish("  [OK] p=" + p + " result saved.");
						}
					} else {
						AllKExperiment exp = new AllKExperiment(network, p, dm, elongator, baseSpeed, precision);
						AllKResult res = exp.run();
						if (res != null) {
							ResultIO.saveAllK(resultFile, p, res);
							this.publish("  [OK] p=" + p + " result saved.");
						}
					}
				}
				return null;
			}

			@Override
			protected void process(List<String> chunks) {
				for (String msg : chunks)
					ExperimentController.this.view.appendStatus(msg);
			}

			@Override
			protected void done() {
				ExperimentController.this.view.setRunning(false);
				try {
					this.get();
					ExperimentController.this.view.appendStatus("--- All tasks finished successfully ---\n");
				} catch (Exception e) {
					if (!this.isCancelled())
						ExperimentController.this.view.appendStatus("[!] FATAL ERROR: " + e.getMessage());
				}
			}
		};

		this.view.setRunning(true);
		this.worker.execute();
	}

	public void stopExperiment() {
		if (this.worker != null && !this.worker.isDone()) {
			this.worker.cancel(true);
			this.view.appendStatus("!!! Experiment STOPPED by user !!!");
		}
	}
}
