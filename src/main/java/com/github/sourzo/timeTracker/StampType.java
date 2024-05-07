package com.github.sourzo.timeTracker;

import java.io.Serializable;

public enum StampType implements Serializable {
	IN("Clock in"),
	OUT("Clock out");
	
	private final String label;
	
	StampType(String label)
	{
		this.label = label;
	}
	
	public String getLabel() {return label;}
	
	public static String[] allLabels() {
		StampType[] values = StampType.values();
		String[] labels = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			labels[i] = (values[i].getLabel());
		}
		return labels;
	}
	
	public static StampType fromLabel(String label) {
		for (StampType st : StampType.values()) {
			if (st.getLabel().equals(label)) {
				return st;
			}
		}
		return null;
	}

	
	
}
