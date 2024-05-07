package com.github.sourzo.timeTracker;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Diary implements Serializable {
	//Static fields--------------------------------------------------------
	private static final long serialVersionUID = 1L;
	static String defaultFileName = "workDiary.txt";

	//Instance fields------------------------------------------------------
	private TreeMap<LocalDate, WorkDay> clockCard;
	private Duration balanceOfRecordedDays = Duration.ofHours(0); 
	private String fileName = defaultFileName;
	private LocalDate diaryStart = LocalDate.now(); //TODO: today or first recorded day?
	/**The target number of hours to work each day. If you don't know, 
	 * divide your weekly contractual hours by 5.*/ 
	private Duration dayTargetHours = Duration.ofHours(7);
	private TreeSet<DayOfWeek> regularNonWorkingDays = new TreeSet<>();
	
	//Constructors----------------------------------------------------------
	public Diary() {
		regularNonWorkingDays.add(DayOfWeek.SATURDAY);
		regularNonWorkingDays.add(DayOfWeek.SUNDAY);
	}
	public Diary(String fileName) {
		regularNonWorkingDays.add(DayOfWeek.SATURDAY);
		regularNonWorkingDays.add(DayOfWeek.SUNDAY);
		this.fileName = fileName;
	}

	//Methods: load & save --------------------------------------------
	/**Saves the current state of the Diary object (using serialization).*/
	public void save()
	{
		try 
		{
			FileOutputStream fOut = new FileOutputStream(fileName);
			ObjectOutputStream out = new ObjectOutputStream(fOut);
			out.writeObject(this);
			out.flush();
			out.close();
		}
		catch (Exception e) 
		{
			System.out.println(e);
		}
	}
	
	public void saveAs(String filename) {
		this.fileName = filename;
		this.save();
	}
	
	/**Loads the diary from the saved state.
	 * @param fileName the filename, e.g. "clockingDiary.txt". If left null, {@link #defaultFileName} will be used.
	 * @return The loaded diary.*/
	public static Diary load(String fileName)
	{
		Diary loadedDiary;
		if (fileName == null) { //TODO Optional<>?
			fileName = defaultFileName;
		}
		
		//If there is not file of that name, create a new Diary and save it.
		Path path = Paths.get(fileName);
		if(!Files.exists(path)) {
			loadedDiary = new Diary();
			loadedDiary.saveAs(fileName);
		}
		
		//Load the saved diary.
		try
		{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
			loadedDiary = (Diary)in.readObject();
			in.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
			loadedDiary = new Diary();
		}
		return loadedDiary;
	}
	
	
	//Methods: Changing things ---------------------------------------------
	/**For each day which has been recorded in the diary, sum up the number of hours worked 
	 * (which is updated every time an entry is modified) and subtract the target number of
	 * hours to work each day*/
	public Duration recalculateBalance() {
		Duration balance = Duration.ofHours(0);
		for (Map.Entry<LocalDate, WorkDay> diaryEntry : clockCard.entrySet()) {
			balance.plus(diaryEntry.getValue().getHoursWorked());
			balance.minus(dayTargetHours);
		}
		if (!getUnrecordedDays().isEmpty()) {
			System.out.println("Warning: There are missed clockings in the diary");
		}
		return balance;
	}
	
	public void stamp()
	{
		LocalDate today = LocalDate.now();
		LocalTime rightNow = TimeAndDate.timeNow();
		if (!clockCard.containsKey(today))
		{
			clockCard.put(today, new WorkDay(today));
		}
		clockCard.get(today).addTimeStamp(rightNow);
		recalculateBalance();
	}
	
	//TODO look into Optional<> types?
	public void stamp(StampType inOrOut)
	{
		LocalDate today = LocalDate.now();
		LocalTime rightNow = TimeAndDate.timeNow();
		if (!clockCard.containsKey(today))
		{
			clockCard.put(today, new WorkDay(today));
		}
		clockCard.get(today).addTimeStamp(rightNow, inOrOut);
		recalculateBalance();
		System.out.println("Success! " + inOrOut.getLabel() + " at " + rightNow + " on " + today);
	}
	
	public void addActivity(ActivityType activity, Duration duration) {
		LocalDate today = LocalDate.now();
		if (!clockCard.containsKey(today))
		{
			clockCard.put(today, new WorkDay(today));
		}
		clockCard.get(today).addActivity(activity, duration);
		recalculateBalance();
		System.out.println("Success! Added " + activity.getLabel() + " of duration  " + duration + " on " + today);
	}
	
	public Diary setStartDate(LocalDate newStartDate) {
		this.diaryStart = newStartDate;
		recalculateBalance();
		return this;
	}

	//Methods: descriptive  -------------------------------------------------------
	
	public WorkDay get(LocalDate date) {
		if (clockCard.containsKey(date)) {
			return clockCard.get(date);
		} else {
			clockCard.put(date, new WorkDay(date));
			return clockCard.get(date);
		}
	}
	public Duration balanceToDate(LocalDate startDate, LocalDate endDate) {
		Duration balance = Duration.ofHours(0);
		for (Map.Entry<LocalDate, WorkDay> diaryEntry : clockCard.entrySet()) {
			if (diaryEntry.getKey().isAfter(startDate.minusDays(1))) {
				if (diaryEntry.getKey().isAfter(endDate)){
					break;
				} else {
					balance.plus(diaryEntry.getValue().getHoursWorked());
					balance.minus(dayTargetHours);
				}
			}
		}
		if (!getUnrecordedDays().isEmpty()) {
			System.out.println("Warning: There are missed clockings in the diary");
		}
		return balance;
	}
	
	public Duration balanceToDate(LocalDate endDate) {
		return balanceToDate(diaryStart, endDate);
	}
	
	public Duration balanceToDate() {
		return balanceToDate(diaryStart, LocalDate.now());
	}
	
	
	public TreeSet<LocalDate> getUnrecordedDays() {
		TreeSet<LocalDate> missingDays = new TreeSet<>();
		for (LocalDate date = diaryStart; date.isBefore(LocalDate.now().plusDays(1)); date = date.plusDays(1)) {
			if (!clockCard.containsKey(date) && !regularNonWorkingDays.contains(date.getDayOfWeek())) {
				missingDays.add(date);
			}
		}
		return missingDays;
	} 
	
	public void viewDay() {
		viewDay(LocalDate.now());
	}
	
	public void viewDay(LocalDate date) {
		if (date==null) {
			viewDay();
		}
		if (clockCard.containsKey(date)) {
			clockCard.get(date).displayDetails();
		} else {
			CUI.warn("No record for day " + date.toString());
		}
	}
	
	public void viewWeek() {
		viewWeek(LocalDate.now());
	}

	public void viewWeek(LocalDate date) {
		int dayNum = date.getDayOfWeek().getValue();
		LocalDate weekStart = date.minusDays(dayNum-1);
		for (int i = 0; i<5; i++) {
			LocalDate day = weekStart.plusDays(i);
			System.out.print(day + ": ");
			if (clockCard.containsKey(day)) {
				System.out.println(TimeAndDate.parseDuration(clockCard.get(day).getHoursWorked()) + " total");
			} else {
				System.out.println("nothing recorded");
			}
			//TODO add activities/stamp totals?
		}
	}
	
	public String getFileName() {
		return fileName;
	}

}
