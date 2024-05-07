package com.github.sourzo.timeTracker;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import junit.framework.TestCase;

public class TestWorkDay extends TestCase {
	private WorkDay wd;
	private LocalTime refTime;
	
	public void setUp() {
		wd = new WorkDay(LocalDate.now());		
		wd.suppressWarnings();
		refTime = TimeAndDate.timeNow();
	}
	
	public void testInitialFieldValues() {
		assertEquals(LocalDate.now(), wd.getDate());
		assertNotNull(wd.getTimeStamps());
		assertNotNull(wd.getActivities());
		assertEquals(0, wd.getTimeStamps().size());
		assertEquals(0, wd.getActivities().size());
		assertEquals(Duration.ofHours(0), wd.getHoursWorked());
	}
	
	public void testCalculationsWithEmptyFields() {
		Duration hrsWorked = wd.getHoursWorked();
		assertEquals(Duration.ofHours(0), wd.timeStampsTotal());						// No time stamps = duration 0
		assertEquals(Duration.ofHours(0), wd.activitiesTotal());						// No activities = duration 0
		assertEquals(Duration.ofHours(0), wd.recalculateHoursWorked());					// No time stamps or activities = duration 0
		assertEquals(hrsWorked, wd.getHoursWorked());									// recalculateHoursWorked doesn't change number of hours worked when nothing else has changed		
	}
	
	public void testTimeStamps() {
		wd.removeTimeStamp(refTime); 													//remove from empty hashmap
		
		wd.addTimeStamp(refTime);														//first stamp (auto-in)
		assertEquals(1, wd.getTimeStamps().size());										//
		assertEquals(StampType.IN, wd.getTimeStamps().get(refTime));					//
		assertEquals(Duration.ofHours(0), wd.getHoursWorked());							//
		
		wd.addTimeStamp(refTime.plusHours(1L));											//in-order stamp (auto out)
		assertEquals(2, wd.getTimeStamps().size());										//
		assertEquals(StampType.OUT, wd.getTimeStamps().get(refTime.plusHours(1L)));		//
		assertEquals(Duration.ofHours(1), wd.getHoursWorked());

		wd.addTimeStamp(refTime.plusMinutes(30L));										//out-of-order stamp (auto-out)
		assertEquals(3, wd.getTimeStamps().size());										//
		assertEquals(StampType.OUT, wd.getTimeStamps().get(refTime.plusMinutes(30L)));	//
		assertEquals(Duration.ofMinutes(30), wd.getHoursWorked());						//

		//Removing stamps
		wd.removeTimeStamp(refTime.plusHours(4L));										//remove a time stamp which doesn't exist
		wd.removeTimeStamp(refTime.plusHours(1L));										//remove a time stamp which exists
		assertEquals(Duration.ofMinutes(30), wd.getHoursWorked());
	}
	
	public void testActivities() {
		//Adding activities
		wd.addActivity(ActivityType.ANNUAL_LEAVE, Duration.ofHours(3));					//First activity
		assertEquals(Duration.ofHours(3), wd.getHoursWorked());
		assertEquals(1, wd.getActivities().size());
		
		wd.addActivity(ActivityType.SICK, Duration.ofHours(1));							//Second activity
		assertEquals(Duration.ofHours(4), wd.getHoursWorked());
		assertEquals(2, wd.getActivities().size());
		
		wd.addActivity(ActivityType.ANNUAL_LEAVE, Duration.ofHours(3));					//Adding to first activity
		assertEquals(2, wd.getActivities().size());
		assertEquals(Duration.ofHours(6), wd.getActivities().get(ActivityType.ANNUAL_LEAVE));
		assertEquals(Duration.ofHours(7), wd.getHoursWorked());
				
		//Removing activities
		wd.removeActivity(ActivityType.ANNUAL_LEAVE);									//Remove an activity - whole thing
		assertEquals(1, wd.getActivities().size());
		wd.removeActivity(ActivityType.SICK, Duration.ofMinutes(30));					//Remove an activity - part of it
		assertEquals(1, wd.getActivities().size());
		assertEquals(Duration.ofMinutes(30), wd.activitiesTotal());
		wd.removeActivity(ActivityType.GLOBAL_NON_WORKING_DAY); 						//remove an activity that doesn't exist
		wd.removeActivity(ActivityType.GLOBAL_NON_WORKING_DAY, Duration.ofMinutes(20));	//remove an activity that doesn't exist
		wd.removeActivity(ActivityType.SICK, Duration.ofHours(30));						//remove more time than is available
	}
	
	public void testCalculationsWithNonemptyFields() {
		wd.addTimeStamp(refTime);
		wd.addTimeStamp(refTime.plusMinutes(15));
		wd.addTimeStamp(refTime.plusMinutes(60));
		wd.addTimeStamp(refTime.plusMinutes(75));
		wd.addActivity(ActivityType.SICK, Duration.ofMinutes(30));
		wd.addActivity(ActivityType.ANNUAL_LEAVE, Duration.ofHours(1));
		Duration hrsWorked = wd.getHoursWorked();
		assertEquals(Duration.ofMinutes(30), wd.timeStampsTotal());						// No time stamps = duration 0
		assertEquals(Duration.ofMinutes(90), wd.activitiesTotal());						// No activities = duration 0
		assertEquals(Duration.ofHours(2), wd.recalculateHoursWorked());					// No time stamps or activities = duration 0
		assertEquals(hrsWorked, wd.getHoursWorked());									// recalculateHoursWorked doesn't change number of hours worked when nothing else has changed
	}
	
	public static void main(String[] args) {
		TestWorkDay test = new TestWorkDay();
		test.setUp();
		test.testInitialFieldValues();
		test.setUp();
		test.testCalculationsWithEmptyFields();
		test.setUp();
		test.testTimeStamps();
		test.setUp();
		test.testActivities();
		test.setUp();
		test.testCalculationsWithNonemptyFields();
		
	}
}
