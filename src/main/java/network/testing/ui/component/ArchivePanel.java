package network.testing.ui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import network.testing.app.AppCoordinator;
import network.testing.app.events.listeners.ExperimentListListener;
import network.testing.domain.model.dto.ExperimentSummary;
import network.testing.ui.component.cards.ExperimentCard;

public class ArchivePanel extends JPanel implements ExperimentListListener {
	private final AppCoordinator coordinator;
	private final JPanel container;
	private final List<ExperimentCard> cards;

	public ArchivePanel(AppCoordinator coordinator) {
		this.coordinator = coordinator;
		this.container = new JPanel();
		this.cards = new ArrayList<>();

		this.initComponents();
	}

	private void initComponents() {
		this.setLayout(new BorderLayout());
		this.setBorder(new TitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), "Experiment Archive"));

		this.setMinimumSize(new Dimension(0, 0));

		this.container.setLayout(new BoxLayout(this.container, BoxLayout.Y_AXIS));
		this.container.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.container.setBackground(new Color(60, 63, 65));

		JScrollPane scroll = new JScrollPane(this.container);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUnitIncrement(12);

		this.add(scroll, BorderLayout.CENTER);
	}

	@Override
	public void onExperimentListUpdated(List<ExperimentSummary> list) {
		SwingUtilities.invokeLater(() -> {
			this.container.removeAll();
			this.cards.clear();

			for (ExperimentSummary exp : list) {
				ExperimentCard card = new ExperimentCard(exp);
				this.setupCardActions(card);
				this.cards.add(card);
				this.container.add(card);
				this.container.add(Box.createRigidArea(new Dimension(0, 10)));
			}
			this.onExperimentSelected(this.coordinator.getActiveExperimentId());
			this.container.revalidate();
			this.container.repaint();
		});
	}

	@Override
	public void onExperimentSelected(long id) {
		SwingUtilities.invokeLater(() -> {
			for (ExperimentCard card : this.cards)
				card.setSelected(card.getExperimentId() == id);
		});
	}

	private void setupCardActions(ExperimentCard card) {
		card.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ArchivePanel.this.coordinator.selectExperiment(card.getExperimentId());
			}
		});

		card.getBtnDelete().addActionListener(e -> {
			int conf = JOptionPane.showConfirmDialog(
					SwingUtilities.getWindowAncestor(this),
					"Delete " + card.getExperimentName() + "?",
					"Confirm",
					JOptionPane.YES_NO_OPTION);
			if (conf == JOptionPane.YES_OPTION)
				this.coordinator.deleteExperiment(card.getExperimentId());
		});

		card.getBtnExport().addActionListener(e -> {
			FileDialog fd = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Export", FileDialog.SAVE);
			fd.setFile(card.getExperimentName() + ".xlsx");
			fd.setVisible(true);
			if (fd.getFile() != null)
				this.coordinator.exportToExcel(card.getExperimentId(), Paths.get(fd.getDirectory(), fd.getFile()));
		});
	}
}
