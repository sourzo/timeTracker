package com.github.sourzo.timeTracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Menu {

	//Fields: Other --------------------------------------------------------

	//Methods: other -------------------------------------------------------
	/**Asks the user to input a duration, in the format HH:MM, into the console 
	 * (also accepts H:MM). Returns the duration.*/
	public static Duration getDurationFromUser() throws IOException {
		String userInput = "";
        Pattern numeric = Pattern.compile("(\\d?\\d):(\\d\\d)");
		while (true) {
			System.out.println("Enter the amount of time, in the format HH:MM.");
			System.out.print("> ");
	        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	        userInput = reader.readLine().strip().toUpperCase();
	        Matcher m = numeric.matcher(userInput);
	        
	        if (m.matches() && 
	        		Integer.valueOf(m.group(1)) < 24 &&
	        		Integer.valueOf(m.group(2)) < 60) {
	        	return Duration.ofHours(Integer.valueOf(m.group(1))).plusMinutes(Integer.valueOf(m.group(2)));
	        }
	        else {
	    		System.out.println("Invalid answer.");
	    		System.out.println();	        	
	        }
		}
	}
	
	/**Asks the user to input a duration, in the format HH:MM, into the console 
	 * (also accepts H:MM). Returns the duration.*/
	public static LocalTime getTimeFromUser() throws IOException {
		String userInput = "";
        Pattern numeric = Pattern.compile("(\\d?\\d):(\\d\\d)");
		while (true) {
			System.out.println("Enter the time, in the 24-hour format HH:MM.");
			System.out.print("> ");
	        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	        userInput = reader.readLine().strip().toUpperCase();
	        Matcher m = numeric.matcher(userInput);
	        
	        if (m.matches() && 
	        		Integer.valueOf(m.group(1)) < 24 &&
	        		Integer.valueOf(m.group(2)) < 60) {
	        	return LocalTime.of(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)));
	        }
	        else {
	    		System.out.println("Invalid answer.");
	    		System.out.println();	        	
	        }
		}
	}

	/**Asks the user to input a date, in the format DD/MM/YY, into the console 
	 * (also accepts single-digit day/month). Returns the date.*/
	public static LocalDate getDateFromUser() throws IOException {
		String userInput = "";
        Pattern date = Pattern.compile("(\\d?\\d)/(\\d?\\d)/(\\d\\d\\d\\d)");
		while (true) {
			System.out.println("Enter the date, in the format DD/MM/YYYY.");
			System.out.print("> ");
	        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	        userInput = reader.readLine().strip().toUpperCase();
	        Matcher m = date.matcher(userInput);
	        
	        if (m.matches()) {
	        	try {
		        	return LocalDate.of(Integer.valueOf(m.group(3)), Integer.valueOf(m.group(2)), Integer.valueOf(m.group(1)));
	        	}
	        	catch(DateTimeException e){
		    		System.out.println("Invalid date.");
		    		System.out.println();	        		        		
	        	}
	        }
	        else {
	    		System.out.println("Invalid date.");
	    		System.out.println();	        	
	        }
		}
	}
	
	/**Displays the input string array as a numeric list of options, and asks the user to pick one of the options.
	 * @param displayText {@code String[]} The options to be displayed to the user
	 * @return {@code int} The index of the selected option*/
	public static int getOptionNumber(String[] displayText) throws IOException {
		//Get selection numbers as a string
		ArrayList<String> selectionValues = new ArrayList<String>();
		for (int n = 0; n < displayText.length; n++) 
		{
			selectionValues.add(String.valueOf(n));
		}

		//User input loop:
		//Display the menu options and request valid input
		String userInput = "";
		System.out.println("Please select one of the following options:");
		System.out.println();
		while (selectionValues.contains(userInput) == false) 
		{
			for (int i = 1; i <= displayText.length; i++)
			{
				System.out.print(i + ": ");
				System.out.println(displayText[i]);
			}
			System.out.println();
			
			//Request user input - choose a menu item
			System.out.print("> ");
	        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	        userInput = reader.readLine().strip().toUpperCase();
	        	        
	        if (selectionValues.contains(userInput) == false) {
	        	System.out.println();
	        	System.out.println("That is not one of the options.");
	        	System.out.println("Type in the number of your choice from the list below:");
	        	System.out.println();
	        }
		}	

		return Integer.valueOf(userInput)-1;
	}
}
