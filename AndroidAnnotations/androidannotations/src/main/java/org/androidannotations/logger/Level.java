package org.androidannotations.logger;

public enum Level {
	TRACE(1, "TRACE"), //
	DEBUG(2, "DEBUG"), //
	INFO(3, "INFO "), //
	WARN(4, "WARN "), //
	ERROR(5, "ERROR");

	public final int weight;
	public final String name;

	private Level(int weight, String name) {
		this.weight = weight;
		this.name = name;
	}

	public boolean isGreaterOrEqual(Level l) {
		return weight >= l.weight;
	}

}
