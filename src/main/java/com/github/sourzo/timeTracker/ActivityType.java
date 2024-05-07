package com.github.sourzo.timeTracker;

public enum ActivityType {
	WORK("Work"),
	ANNUAL_LEAVE("Annual leave"),
	SPECIAL_LEAVE("Special leave"),
	MEDICAL_APPOINTMENT("Medical appointment"),
	SICK("Sick"),
	TRAINING("Training"),
	GLOBAL_NON_WORKING_DAY("All-staff non-working day"),
	PERSONAL_NON_WORKING_DAY("Personal non-working day"),
	PRE_EMPLOYMENT("Before employment start date");
	
	private final String label;
	
	ActivityType(String label)
	{
		this.label = label;
	}
	
	public String getLabel() {return label;}
	
	public static String[] allLabels() {
		ActivityType[] values = ActivityType.values();
		String[] labels = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			labels[i] = (values[i].getLabel());
		}
		return labels;
	}
	
	public static ActivityType fromLabel(String label) {
		for (ActivityType activity : ActivityType.values()) {
			if (activity.getLabel().equals(label)) {
				return activity;
			}
		}
		return null;
	}
}
