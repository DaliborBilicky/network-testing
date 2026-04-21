package network.testing.app.events;

import java.awt.Color;

public enum NotificationType {
	SUCCESS(new Color(60, 140, 60, 240)),
	INFO(new Color(50, 80, 140, 240)),
	ERROR(new Color(180, 40, 40, 240));

	private final Color color;

	NotificationType(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return this.color;
	}
}
