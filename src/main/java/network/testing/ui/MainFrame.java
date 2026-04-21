package network.testing.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import network.testing.app.AppCoordinator;
import network.testing.app.events.NotificationType;
import network.testing.app.events.listeners.NotificationListener;
import network.testing.app.events.listeners.ProjectStateListener;
import network.testing.ui.component.ArchivePanel;
import network.testing.ui.component.ExecutionPanel;
import network.testing.ui.component.InteractiveCanvas;
import network.testing.ui.component.StatsPanel;
import network.testing.ui.component.TimelinePanel;
import network.testing.ui.component.Toast;
import network.testing.ui.dialog.NewProjectDialog;

public class MainFrame extends JFrame implements NotificationListener, ProjectStateListener {
	private final AppCoordinator coordinator;

	private final InteractiveCanvas canvas;
	private final TimelinePanel timeline;
	private final ExecutionPanel execution;
	private final StatsPanel currentStats;
	private final StatsPanel previousStats;
	private final ArchivePanel archive;

	public MainFrame(AppCoordinator coordinator) {
		this.coordinator = coordinator;

		this.canvas = new InteractiveCanvas(this.coordinator);
		this.timeline = new TimelinePanel(this.coordinator);
		this.execution = new ExecutionPanel(this.coordinator);
		this.currentStats = new StatsPanel("Current Snapshot");
		this.previousStats = new StatsPanel("Previous Snapshot");
		this.archive = new ArchivePanel(this.coordinator);

		this.initComponents();
		this.setupListeners();
	}

	private void initComponents() {
		this.setTitle("Network Robustness Analyzer");
		this.setJMenuBar(this.createMenuBar());

		this.setupLayout();
		this.setupWindow();
	}

	private void setupLayout() {
		this.setLayout(new BorderLayout());
		JPanel statsContainer = new JPanel(new GridLayout(2, 1, 0, 5));
		statsContainer.add(this.currentStats);
		statsContainer.add(this.previousStats);

		JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, statsContainer, this.execution);
		rightSplit.setResizeWeight(0.6);
		rightSplit.setBorder(null);
		rightSplit.setPreferredSize(new Dimension(400, 0));

		JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.timeline, this.archive);
		leftSplit.setResizeWeight(0.66);
		leftSplit.setBorder(null);
		leftSplit.setPreferredSize(new Dimension(270, 0));

		JSplitPane centerRightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.canvas, rightSplit);
		centerRightSplit.setResizeWeight(1.0);
		centerRightSplit.setBorder(null);

		JSplitPane globalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplit, centerRightSplit);
		globalSplit.setResizeWeight(0.0);
		globalSplit.setBorder(null);

		this.add(globalSplit, BorderLayout.CENTER);
	}

	private void setupListeners() {
		this.coordinator.getEventHub().addProjectListener(this);
		this.coordinator.getEventHub().addNotificationListener(this);

		this.coordinator.getEventHub().addDataListener(this.timeline);
		this.coordinator.getEventHub().addExperimentListener(this.execution);
		this.coordinator.getEventHub().addExperimentListListener(this.archive);
		this.coordinator.getEventHub().addProjectListener(this.execution);
		this.coordinator.getEventHub().addSelectionListener(this.canvas);
		this.coordinator.getEventHub().addSelectionListener(this.timeline);

		this.coordinator.getEventHub().addSelectionListener((id, current, previous) -> {
			this.currentStats.onStepSelected(current);
			this.previousStats.onStepSelected(previous);
		});
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");

		JMenuItem newItem = new JMenuItem("New Project");
		JMenuItem openItem = new JMenuItem("Open Project");
		JMenuItem exitItem = new JMenuItem("Exit");

		newItem.addActionListener(e -> this.handleNewProject());
		openItem.addActionListener(e -> this.handleOpenProject());
		exitItem.addActionListener(e -> this.handleExit());

		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);

		menuBar.add(fileMenu);
		return menuBar;
	}

	private void handleNewProject() {
		NewProjectDialog dialog = new NewProjectDialog(this);
		dialog.setVisible(true);

		if (dialog.isApproved()) {
			this.coordinator.createProject(
					dialog.getZipPath(),
					dialog.getVertPath(),
					dialog.getEdgePath(),
					dialog.getCoordPath());
		}
	}

	private void handleOpenProject() {
		FileDialog fd = new FileDialog(this, "Open Project", FileDialog.LOAD);
		fd.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".ntp"));
		fd.setFile("*.ntp");
		fd.setVisible(true);

		if (fd.getFile() != null) {
			File file = new File(fd.getDirectory(), fd.getFile());
			this.coordinator.openProject(file.toPath());
		}
	}

	private void handleExit() {
		if (this.coordinator.isExperimentRunning()) {
			int confirm = JOptionPane.showConfirmDialog(this,
					"An experiment is currently running. Stop it and exit?",
					"Experiment in Progress", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			if (confirm != JOptionPane.YES_OPTION) {
				return;
			}
			this.coordinator.stopExperiment();
		}

		this.dispose();
		this.coordinator.closeProject();
		System.exit(0);
	}

	private void setupWindow() {
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				handleExit();
			}
		});

		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setMinimumSize(new Dimension(1024, 768));
		this.setLocationRelativeTo(null);
	}

	@Override
	public void onNotify(String message, NotificationType type) {
		new Toast(message, type, this);
	}

	@Override
	public void onProjectReady(String projectName) {
		this.setTitle(projectName + " - Network Robustness Analyzer");
	}
}
