package network.testing;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;

import network.testing.app.AppCoordinator;
import network.testing.ui.MainFrame;

public class Main {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch (Exception ex) {
			System.err.println("Failed to initialize LaF");
		}

		SwingUtilities.invokeLater(() -> {
			AppCoordinator coordinator = new AppCoordinator();
			MainFrame mainFrame = new MainFrame(coordinator);
			mainFrame.setVisible(true);
		});
	}
}
