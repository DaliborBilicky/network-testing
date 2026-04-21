package network.testing.ui.component.cards;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import network.testing.domain.model.dto.SnapshotHeader;

public class SnapshotCard extends SelectableCard {
	private final SnapshotHeader header;

	public SnapshotCard(SnapshotHeader header) {
		super(55);
		this.header = header;
		this.initComponents();
	}

	private void initComponents() {
		JLabel lblK = new JLabel(String.format(Locale.US, "k = %.6f", this.header.k()));
		lblK.setFont(new Font("SansSerif", Font.BOLD, 16));
		lblK.setHorizontalAlignment(SwingConstants.LEFT);

		this.add(lblK, BorderLayout.CENTER);
	}

	public long getSnapshotId() {
		return this.header.snapshotId();
	}
}
