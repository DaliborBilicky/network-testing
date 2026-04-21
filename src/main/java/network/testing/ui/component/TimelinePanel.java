package network.testing.ui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import network.testing.app.AppCoordinator;
import network.testing.app.events.listeners.DataUpdateListener;
import network.testing.app.events.listeners.SelectionListener;
import network.testing.domain.model.dto.SnapshotHeader;
import network.testing.domain.model.result.SnapshotData;
import network.testing.ui.component.cards.SnapshotCard;

public class TimelinePanel extends JPanel implements DataUpdateListener, SelectionListener {
	private final AppCoordinator coordinator;
	private final JPanel container;
	private final List<SnapshotCard> cards;

	public TimelinePanel(AppCoordinator coordinator) {
		this.coordinator = coordinator;
		this.container = new JPanel();
		this.cards = new ArrayList<>();

		this.initComponents();
	}

	private void initComponents() {
		this.setLayout(new BorderLayout());
		this.setBorder(new TitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), "Results Timeline"));

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
	public void onDataUpdated(Map<Integer, List<SnapshotHeader>> tree) {
		SwingUtilities.invokeLater(() -> {
			this.container.removeAll();
			this.cards.clear();

			for (Map.Entry<Integer, List<SnapshotHeader>> entry : tree.entrySet()) {
				this.addPLabel(entry.getKey());
				for (SnapshotHeader header : entry.getValue()) {
					SnapshotCard card = new SnapshotCard(header);
					card.addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {
							TimelinePanel.this.coordinator.selectSnapshot(header.snapshotId());
						}
					});
					this.cards.add(card);
					this.container.add(card);
					this.container.add(Box.createRigidArea(new Dimension(0, 8)));
				}
			}
			this.container.revalidate();
			this.container.repaint();
		});
	}

	@Override
	public void onStepSelected(long snapshotId, SnapshotData current, SnapshotData previous) {
		SwingUtilities.invokeLater(() -> {
			for (SnapshotCard card : this.cards) {
				card.setSelected(card.getSnapshotId() == snapshotId);
			}
		});
	}

	private void addPLabel(int p) {
		JLabel lblP = new JLabel("Parameter p = " + p);
		lblP.setFont(new Font("SansSerif", Font.BOLD, 14));
		lblP.setForeground(Color.LIGHT_GRAY);
		lblP.setBorder(new EmptyBorder(5, 5, 5, 0));
		lblP.setAlignmentX(LEFT_ALIGNMENT);

		this.container.add(lblP);
		this.container.add(Box.createRigidArea(new Dimension(0, 5)));
	}
}
