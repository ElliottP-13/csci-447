package Project1_NaiiveBayes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadWrite {
	public  String readEntireFile(String filePath) { // Reads the file
		File file = new File(filePath);
		return readEntireFile(file);
	}
	public  String readEntireFile(File file) { // Reads the file
		String retString = "";
		if (file.exists()) {
			try {
				Scanner scan = new Scanner(file);
				scan.useDelimiter("\\Z");
				if (scan.hasNext()) {
					retString = scan.next();
				}
				scan.close();
			} catch (FileNotFoundException ignored) {
				return "File not found for path: " + file;
			}
		}else {
			System.out.println("File doesn't exist");
		}

		return retString;
	}
	public String[] readNstoreArray(String filePath, String delimeter){
		return readEntireFile(filePath).split(delimeter);
	}

	public String[] readNstoreArray(String filePath){
		return readEntireFile(filePath).split(" ");
	}

	public ArrayList readNstoreArrayList(String filePath) { // Reads the
		File file = new File(filePath);
		String retString = "";
		ArrayList<String> words = new ArrayList<>(50);

		if (file.exists()) {
			try {
				Scanner scan = new Scanner(file);
				scan.useDelimiter(" "); // this causes it to stop after each
				// space
				while (scan.hasNext()) {
					retString = scan.next(); // This records everything between
												// the spaces
					words.add(retString); // this adds the string into
													// the ArrayList
				}
				scan.close();
			} catch (FileNotFoundException ignored) {
				return null;
			}
		}
		return words; // returns the element at specified
											// location
	}
	public File createFile(String filePath) { // makes a file
		File file = new File(filePath);
		if(file.exists()){
			System.out.println("File already exists");
			return null;
		}
		else{
			try {
				file.createNewFile();
			} catch (IOException ignored) {
				ignored.printStackTrace();
			}
		return file;
		}
	}

	public File createFileIfNotExists(String filePath) { // makes a file
		File file = new File(filePath);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ignored) {
				ignored.printStackTrace();
			}
		}
		return file;
	}

	public void appendToFile(String line, File file) { // adds on to file
		try {
			FileWriter writer = new FileWriter(file, true);
			writer.append(line);
			writer.close();
		} catch (IOException ignored) {
		}
	}
	public void overwriteFileWithString(String contents, File file) {
		//erases all the text in the file and writes new text
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(contents);
			writer.close();
		} catch (IOException ignored) {
		}
	}
}
