package network.testing.ui.render;

import java.util.Locale;
import network.testing.app.ProjectContext;
import network.testing.core.utils.NetworkMetrics;
import network.testing.domain.model.result.SnapshotData;

public class VisualizationService {
	public static RenderData prepareRenderData(
			ProjectContext context, SnapshotData current, SnapshotData previous, double baseSpeed) {
		if (context == null || current == null)
			return null;

		double[] elongatedCosts = context.elongator().getElongatedCosts(current.k());

		double[] declines = NetworkMetrics.calculateDeclines(
				context.network().copyEdgeCosts(),
				elongatedCosts,
				baseSpeed);

		return new RenderData(
				context.network(),
				context.positions(),
				previous != null ? previous.medianIds() : current.medianIds(),
				current.medianIds(),
				declines);
	}

	public static String[][] prepareStats(SnapshotData snap) {
		if (snap == null)
			return new String[0][0];

		return new String[][] {
				{ "Parameter k", String.format(Locale.US, "%.6f", snap.k()) },
				{ "Objective Value", String.format(Locale.US, "%.2f", snap.objective()) },
				{ "Sum of Weights", String.valueOf(snap.medianSum()) },
				{ "Irregularity", String.format(Locale.US, "%.2f", snap.medianIrregularity()) },
				{ "Min Decline", String.format(Locale.US, "%.2f km/h", snap.declineMin()) },
				{ "Max Decline", String.format(Locale.US, "%.2f km/h", snap.declineMax()) },
				{ "Avg Decline", String.format(Locale.US, "%.2f km/h", snap.declineAvg()) },
				{ "Mode Decline", String.format(Locale.US, "%.2f km/h", snap.declineMode()) },
		};
	}
}
