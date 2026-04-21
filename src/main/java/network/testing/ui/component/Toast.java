package network.testing.ui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import network.testing.app.events.NotificationType;

public class Toast extends JWindow {
	private static final CopyOnWriteArrayList<Toast> TOAST_STACK = new CopyOnWriteArrayList<>();
	private static final int FIXED_HEIGHT = 65;
	private static final int GAP = 10;
	private static final int MARGIN = 25;

	public Toast(String message, NotificationType type, Window owner) {
		super(owner);
		this.initComponents(message, type);

		TOAST_STACK.add(this);
		this.repositionAll();

		this.setVisible(true);

		if (type != NotificationType.ERROR) {
			Timer timer = new Timer(4000, e -> this.dispose());
			timer.setRepeats(false);
			timer.start();
		}
	}

	private void initComponents(String message, NotificationType type) {
		this.setAlwaysOnTop(true);
		this.setBackground(new Color(0, 0, 0, 0));

		JPanel content = new JPanel(new BorderLayout(12, 0)) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(type.getColor());
				g2.fill(new RoundRectangle2D.Float(0, 0, this.getWidth(), this.getHeight(), 12, 12));
				g2.dispose();
			}
		};

		content.setOpaque(false);
		content.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

		JLabel lblIcon = new JLabel(this.getEmoji(type));
		lblIcon.setFont(new Font("SansSerif", Font.PLAIN, 22));
		lblIcon.setVerticalAlignment(SwingConstants.CENTER);

		JLabel lblMsg = new JLabel(message);
		lblMsg.setForeground(Color.WHITE);
		lblMsg.setFont(new Font("SansSerif", Font.BOLD, 16));
		lblMsg.setVerticalAlignment(SwingConstants.CENTER);

		content.add(lblIcon, BorderLayout.WEST);
		content.add(lblMsg, BorderLayout.CENTER);

		int prefWidth = content.getPreferredSize().width + 20;
		int width = Math.max(220, prefWidth);

		content.setPreferredSize(new Dimension(width, FIXED_HEIGHT));

		this.add(content);
		this.setupMouseListener();

		this.pack();
		this.setShape(new RoundRectangle2D.Double(0, 0, this.getWidth(), this.getHeight(), 12, 12));
	}

	private void setupMouseListener() {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Toast.this.dispose();
			}
		});
	}

	private String getEmoji(NotificationType type) {
		return switch (type) {
			case SUCCESS -> "✅";
			case INFO -> "ℹ️";
			case ERROR -> "❎";
		};
	}

	private void repositionAll() {
		int index = 0;
		for (Toast t : TOAST_STACK) {
			Window owner = t.getOwner();
			if (owner != null && owner.isShowing()) {
				int x = owner.getX() + owner.getWidth() - t.getWidth() - MARGIN;
				int targetY = owner.getY() + MARGIN + (index * (FIXED_HEIGHT + GAP));
				t.setLocation(x, targetY);
				index++;
			}
		}
	}

	@Override
	public void dispose() {
		TOAST_STACK.remove(this);
		this.repositionAll();
		super.dispose();
	}
}
