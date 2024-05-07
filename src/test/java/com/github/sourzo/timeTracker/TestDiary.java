package com.github.sourzo.timeTracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import junit.framework.TestCase;

public class TestDiary extends TestCase {
	public void testDiary() throws IOException {
		Diary newDiary = new Diary("Test.txt");	
		assertNotNull(newDiary);							// Can create new diary
		
		Files.deleteIfExists(Paths.get("Test.txt"));
		newDiary.save();
		assertTrue(Files.exists(Paths.get("Test.txt")));	// Can save a diary
		
		Files.deleteIfExists(Paths.get("Test2.txt"));
		newDiary.saveAs("Test2.txt");
		assertTrue(Files.exists(Paths.get("Test2.txt")));	// Can save a diary with a new name
		assertEquals("Test2.txt", newDiary.getFileName()); 	// Saving a diary changes diary name
		Files.deleteIfExists(Paths.get("Test2.txt"));

		Diary loadedDiary = Diary.load("Test.txt");
		assertNotNull(loadedDiary);							// Can load a diary
		Files.deleteIfExists(Paths.get("Test.txt"));
		//serialization - make changes and save & load
		//load a diary that doesn't exist
		//day with no clockings?
	}

	public static void main(String[] args) throws IOException {
		//Test the WorkDay class
		//Test the Diary class
		TestDiary test = new TestDiary();
		test.testDiary();
	}
}
