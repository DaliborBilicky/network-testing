package network.testing;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import network.testing.ui.view.MainFrame;

public class Main {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		SwingUtilities.invokeLater(() -> {
			MainFrame mainFrame = new MainFrame();
			mainFrame.setVisible(true);
		});
	}
}
