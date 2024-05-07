package com.github.sourzo.timeTracker;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class WorkDay implements Serializable {
	//Static fields--------------------------------------------------------
	private static final long serialVersionUID = 1L;
	
	//Instance Fields----------------------------------------------------------------
	/**The date for this record.*/
	private final LocalDate date;
	/**Set of all in/out time stamps recorded by the user for the day.*/
	private TreeMap<LocalTime, StampType> timeStamps = new TreeMap<>();
	/**Set of all activities (added by the user) which count towards the working day's time total. 
	 * For example: 7h annual leave, or 3h training.*/
	private HashMap<ActivityType, Duration> activities = new HashMap<>();
	/**The number of hours worked for the day.*/
	private Duration hoursWorked = Duration.ofHours(0);
	/**Whether to print warnings to the console.*/
	private boolean warnings = true;
	//Constructors----------------------------------------------------------
	public WorkDay(LocalDate date)
	{
		this.date = date;
	}
	//Simple getters ----------------------------------------------------------
	public LocalDate getDate() {return date;}
	public TreeMap<LocalTime, StampType> getTimeStamps() {return timeStamps;}
	public HashMap<ActivityType, Duration> getActivities() {return activities;}
	public Duration getHoursWorked() {warnMissedStamps(); return hoursWorked;} //add optional boolean for warning?
	//Methods: descriptive -------------------------------------------------------
	//Note: Nothing in this section should change the fields of this instance.

	/**Calculates the total time worked based on time stamps (in & out).
	 * <P>If you have IN followed by IN or OUT followed by OUT, then you've missed a time stamp. 
	 * The second one will be ignored. If the last clocking is IN then you've missed a time stamp 
	 * (well, not clocked out yet). This will be ignored.*/
	Duration timeStampsTotal() {
		if (timeStamps.isEmpty()) {
			return Duration.ofHours(0);
		} else {
			StampType inOrOut = StampType.OUT;
			Duration tsTotal = Duration.ofHours(0);
			LocalTime intervalStart = null; 
			for (Map.Entry<LocalTime, StampType> stamp : timeStamps.entrySet()) {
				if (stamp.getValue() != inOrOut) {
					inOrOut = stamp.getValue();
					if (stamp.getValue() == StampType.IN) {
						intervalStart = stamp.getKey();
					} else if (stamp.getValue() == StampType.OUT){
						tsTotal = tsTotal.plus(Duration.between(intervalStart, stamp.getKey()));
					}
				} //ignore repeated StampType: IN-IN or OUT-OUT
			}
			//if last stamp-type is IN then it will be ignored.
			return tsTotal;
		}
	}

	Duration activitiesTotal() {
		if (activities.isEmpty()) {
			return Duration.ofHours(0);
		} else {
			Duration total = Duration.ofHours(0);
			for (Map.Entry<ActivityType, Duration> activity : activities.entrySet()) {
				total = total.plus(activity.getValue());
			}
			return total;
		}
	}
	
	/**Prints out the hours worked, broken down by activity type plus total from clockings.*/
	public void displaySummary() { //TODO Work out how to make this an @override toString()
		System.out.println(date.toString());
		for (LocalTime time : timeStamps.keySet()) {
			System.out.println("Hours clocked: " + timeStampsTotal());
		}
		for (Map.Entry<ActivityType, Duration> activity : activities.entrySet()) {
			System.out.println(activity.getKey() + " " + activity.getValue().toHours() + " hours");
		}
		System.out.println("Hours Worked: " + this.getHoursWorked().toHours() + "; ");
		//System.out.println("Balance at end of day: "); //TODO
	}
	
	/**Prints out the clockings and activities for the day, and the total number of hours worked.*/
	public void displayDetails() { //TODO Work out how to make this an @override toString()
		System.out.println(date.toString());
		for (LocalTime time : timeStamps.keySet()) {
			System.out.println(timeStamps.get(time).toString() + ": " + time.toString() +  "; ");
		}
		for (Map.Entry<ActivityType, Duration> activity : activities.entrySet()) {
			System.out.println(activity.getKey() + " " + activity.getValue().toHours() + " hours");
		}
		System.out.println("Hours Worked: " + this.getHoursWorked().toHours() + "; ");
		//System.out.println("Balance at end of day: "); //TODO
	}


	//Methods: changing things -----------------------------------------------------
	//Note everything here should apply recalculateHoursWorked()
	
	/**Calculates the number of hours which count towards the "working hours" total for this day, 
	 * and updates the {@link #hoursWorked} value. The calculation is the sum of the duration of 
	 * recorded activities (see {@link #activitiesTotal()}) and the sum of all valid clockings (see
	 * {@link #timeStampsTotal()})
	 * @return the updated value for {@link #hoursWorked}*/
	public Duration recalculateHoursWorked() {
		hoursWorked = this.activitiesTotal().plus(this.timeStampsTotal());
		return hoursWorked;
	}
	
	/**Adds a new time stamp to the set of time stamps.
	 * Note that this will replace an existing stamp if it is already in the dataset, 
	 * but only if it is exactly the same time. */
	public void addTimeStamp(LocalTime time, StampType inOrOut) {
		timeStamps.put(time, inOrOut);
		this.recalculateHoursWorked();
	}
	
	/**Decides if Stamp Type should be IN or OUT, then runs {@link #addTimeStamp(LocalTime, StampType)}.*/
	public void addTimeStamp(LocalTime time) {
		Map.Entry<LocalTime, StampType> lastStamp = timeStamps.lowerEntry(time);
		if (lastStamp == null || lastStamp.getValue() == StampType.OUT) {
			//First clocking or previous clocking was OUT: assume IN
			addTimeStamp(time, StampType.IN);
		} else {
			//previous clocking was IN: assume OUT
			addTimeStamp(time, StampType.OUT);
		}
	}
	
	public void removeTimeStamp(LocalTime time) {
		if (timeStamps.containsKey(time)) {
			timeStamps.remove(time);
		} else CUI.warn("Time stamp not recorded on this day. Nothing to remove.");
	}
	
	/**Adds a new activity to the set of activities for the day, or updates an 
	 * existing activity by increasing by the value {@code duration}*/
	public void addActivity(ActivityType activity, Duration duration) {
		if (activities.containsKey(activity)) {
			activities.put(activity, activities.get(activity).plus(duration));
		} else {
			activities.put(activity, duration);
		}
		this.recalculateHoursWorked();
	}
	
	public void removeActivity(ActivityType activity) {
		if (activities.containsKey(activity)) {
			activities.remove(activity);
			this.recalculateHoursWorked();
		} else CUI.warn(activity.getLabel() + " has not been recorded on this day. Nothing to remove.");
	}
	
	public void removeActivity(ActivityType activity, Duration duration) {
		if (activities.containsKey(activity)) {
			if(activities.get(activity).compareTo(duration) >= 0) {
				activities.put(activity, activities.get(activity).minus(duration));
				this.recalculateHoursWorked();			
			} else CUI.warn("Duration to remove is too long. No time deducted.");
		} else CUI.warn(activity.getLabel() + " has not been recorded on this day");
	}

	
	//Checking things ----------------------------------------------------

	public void suppressWarnings() {
		warnings = false;
	}
	
	public void warnMissedStamps() {//TODO test this
		if (CUI.getWarnings()) {
			if (timeStamps.lastEntry() != null){
				//Stamps should start with clocking IN
				if (timeStamps.firstEntry().getValue()==StampType.OUT) {
					System.out.println("Warning: Not yet clocked in before " + timeStamps.firstKey());
				}			
				//Compare each stamp to the previous one, making sure they alternate between in & out
				StampType inOrOut = StampType.OUT;
				LocalTime lagTime = TimeAndDate.timeNow();
				for (Map.Entry<LocalTime, StampType> stamp : timeStamps.entrySet()) {
					if (stamp.getValue() == inOrOut && !stamp.equals(timeStamps.firstEntry())) {
						System.out.println("Warning: Missed time stamp: " + lagTime + " = " + inOrOut + ", " + stamp.getKey() + " = " + stamp.getValue());
					}
					lagTime = stamp.getKey();
					inOrOut = stamp.getValue();
				}
				//Stamps should end with clocking OUT
				if (timeStamps.lastEntry().getValue()==StampType.IN) {
					System.out.println("Warning: Not yet clocked out after " + timeStamps.lastKey());
				}			
			}
		}
	}
}