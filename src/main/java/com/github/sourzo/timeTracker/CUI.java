package com.github.sourzo.timeTracker;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.TreeSet;


public class CUI 
{
	//Menu options ---------------------------------------------------------
	private static String[] mainMenuOptions = {
			"Clock in", "Clock out", "Add activity for today", 
			"Check & amend clockings/activities", "Check & amend days without clockings/activity", 
			"Save & Quit"};
	private static String[] activityOptions = ActivityType.allLabels();
	private static String[] reviewOptions = ViewType.allLabels();
	private static String[] inOrOut = StampType.allLabels();
	private static String[] viewOptions = {"Amend", "Previous", "Next", "Go to date", "Go Back"};
	private static String[] changeWhat = {"Remove a time stamp", "Add a time stamp", "Remove an activity", "Add an activity", "Go Back"};
	
	
	//Settings -------------------------------------------------------------
	private static boolean warnings = true;
	
	//Methods: get & set ---------------------------------------------------
	public static boolean getWarnings() {return warnings;}
	
	//Methods: other -------------------------------------------------------	
	public static void main(String[] args) throws IOException
	{
		//Get diary
		Diary diary = Diary.load(null);
		mainMenu(diary);
		
	}
	public static void mainMenu(Diary diary) throws IOException {
		//Main menu
		boolean keepLoopingMM = true;
		while (keepLoopingMM) {
			
			int choice = Menu.getOptionNumber(mainMenuOptions);
			switch(choice) {
			case 0:
				//Clock in
				diary.stamp(StampType.IN);
				break;
			case 1:
				//Clock out
				diary.stamp(StampType.OUT);
				break;
			case 2:
				//Add activity
				int activityNum = Menu.getOptionNumber(activityOptions);
				ActivityType activity = ActivityType.fromLabel(activityOptions[activityNum]);
				Duration duration = Menu.getDurationFromUser();
				diary.addActivity(activity, duration);
				break;
			case 3:
				//Check & amend
				reviewClockings(diary);
				break;
			case 4:
				//Missing days
				TreeSet<LocalDate> missingDays = diary.getUnrecordedDays();
				//TODO: Amend clockings
				break;
			case 5:
				//Save & Quit
				keepLoopingMM = false;
				diary.save();
	        	System.out.println();
	        	System.out.println("Goodbye.");
	        	System.exit(0);
			}
		}
	}

	/**Asks the user what they'd like to view (by day/week/summary).
	 * <P> Then displays the day/week/summary.
	 * <P> Then asks the user what they would like to do: View 
	 * next/previous; goto date; back to main menu*/
	public static void reviewClockings(Diary diary) throws IOException {
		//Choose what to view
		int reviewChoice = Menu.getOptionNumber(reviewOptions);
		ViewType viewType = ViewType.fromLabel(reviewOptions[reviewChoice]);
		boolean keepReviewing = true;
		
		while (keepReviewing) {
			//View diary
			LocalDate date = LocalDate.now();
			switch (viewType) {
			case DAY:
				diary.viewDay(date);
			case WEEK:
				diary.viewWeek(date);
			case SUMMARY:
				diary.balanceToDate(date);
			}
			
			//Choose what to do next
			int action = Menu.getOptionNumber(viewOptions);
			switch (action) {
			case 0:
				//amend
				changeClockings(diary);
				break;
			case 1:
				//previous day/week
				switch (viewType) {
				case DAY:
				case SUMMARY:
					date = date.minusDays(1);
					break;
				case WEEK:
					date = date.minusDays(7);
					break;
				}
			case 2:
				//next day/week
				switch (viewType) {
				case DAY:
				case SUMMARY:
					date = date.plusDays(1);
					break;
				case WEEK:
					date = date.plusDays(7);
					break;
				}
			case 3:
				//goto date
				date = Menu.getDateFromUser();
				break;
				
			case 4:
				//back
				keepReviewing = false;
				break;
			}

		}
	}
	
	public static void changeClockings(Diary diary) throws IOException {
		System.out.println("Change clockings/activities.");
		LocalDate changeDate = Menu.getDateFromUser();
		int changeItem = Menu.getOptionNumber(changeWhat);
		switch (changeItem) {
		case 0:
			// Remove time stamp
			LocalTime removeTime = Menu.getTimeFromUser();
			diary.get(changeDate).removeTimeStamp(removeTime);
			break;
		case 1:
			// Add time stamp
			LocalTime addTime = Menu.getTimeFromUser();
			int ioNum = Menu.getOptionNumber(inOrOut);
			StampType inOut = StampType.fromLabel(inOrOut[ioNum]);
			diary.get(changeDate).addTimeStamp(addTime, inOut);
			break;
		case 2:
			// Remove activity
			int activityRemoveNum = Menu.getOptionNumber(activityOptions);
			ActivityType activityRemove = ActivityType.fromLabel(activityOptions[activityRemoveNum]);
			Duration durationAdd = Menu.getDurationFromUser();
			diary.get(changeDate).removeActivity(activityRemove, durationAdd);
			break;			
		case 3:
			// Add activity
			int activityAddNum = Menu.getOptionNumber(activityOptions);
			ActivityType activityAdd = ActivityType.fromLabel(activityOptions[activityAddNum]);
			Duration durationRemove = Menu.getDurationFromUser();
			diary.get(changeDate).addActivity(activityAdd, durationRemove);
			break;			
		case 4:
			// Go back
			break;
		}
	}
	
	public void suppressWarnings() {
		warnings = false;
	}
	
	public static void warn(String message) {
		if (warnings) {
			System.out.println("Warning: " + message);
		}
	}
}
