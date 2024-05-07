package com.github.sourzo.timeTracker;

public enum ViewType {
	DAY("View by day"),
	WEEK("View by week"),
	SUMMARY("Summary");
	
	private final String label;
	
	ViewType(String label)
	{
		this.label = label;
	}
	
	public String getLabel() {return label;}
	
	public static String[] allLabels() {
		ViewType[] values = ViewType.values();
		String[] labels = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			labels[i] = (values[i].getLabel());
		}
		return labels;
	}
	
	public static ViewType fromLabel(String label) {
		for (ViewType viewType : ViewType.values()) {
			if (viewType.getLabel().equals(label)) {
				return viewType;
			}
		}
		return null;
	}

}
