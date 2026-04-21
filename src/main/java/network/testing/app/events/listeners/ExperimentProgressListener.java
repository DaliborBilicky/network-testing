package network.testing.app.events.listeners;

public interface ExperimentProgressListener {
	void onExperimentStateChanged(boolean running);

	void onLog(String message);

	void onProgress(int currentP, int totalP);
}
