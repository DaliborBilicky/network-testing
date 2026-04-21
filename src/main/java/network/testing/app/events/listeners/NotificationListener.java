package network.testing.app.events.listeners;

import network.testing.app.events.NotificationType;

public interface NotificationListener {
	void onNotify(String message, NotificationType type);
}
