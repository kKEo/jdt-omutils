package org.maziarz.utils.ui.model;

public class AccessedResource {

	public enum AccessedResourceType {
		Opened, Modified
	}

	private final String name;
	private final AccessedResourceType type;
	private String mtime;

	public AccessedResource(String name, AccessedResourceType type) {
		this.name = name;
		this.type = type;
		this.mtime = Long.toString(System.currentTimeMillis());
	}

	@Override
	public String toString() {
		return name + "(" + type + ") - " + mtime + "\n";
	}

	public void updMTime() {
		this.mtime = Long.toString(System.currentTimeMillis());
	}

}
