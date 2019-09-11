package Project1_NaiiveBayes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Elliott Pryor
 */
public class ReadWrite {

	/**
	 * Reads the file at the given filepath
	 * @param filePath the path to the file
	 * @return a String representation of the file
	 */
	public  String readEntireFile(String filePath) { // Reads the file
		File file = new File(filePath);
		return readEntireFile(file);
	}

    /**
     * Reads the entire file. If the file doesn't exist returns "File not found"
     * @param file the file to be read
     * @return a string representation of the contents of a file
     */
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


    /**
     * Stores the contents of a file in an array split along a given delimeter
     * @param filePath file path
     * @param delimeter delimeter to split the file
     * @return a string[]
     */
	public String[] readNstoreArray(String filePath, String delimeter){
		return readEntireFile(filePath).split(delimeter);
	}

    /**
     * Stores the contents of a file split on the comma
     * @param filePath file path
     * @return a string array
     */
	public String[] readNstoreArray(String filePath){
		return readNstoreArray(filePath, ",");
	}

    /**
     * Makes a new file at the given path
     * @param filePath file path
     * @return the file created
     */
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

    /**
     * Creates a file if there doesn't exist one already. Then returns the file at the filepath
     * @param filePath file path
     * @return the file (either old or newly created)
     */
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

    /**
     * Adds the string to the end of a file
     * @param line string to be added
     * @param file the file to be added to
     */
	public void appendToFile(String line, File file) { // adds on to file
		try {
			FileWriter writer = new FileWriter(file, true);
			writer.append(line);
			writer.close();
		} catch (IOException ignored) {
		}
	}

    /**
     * Erases the old content of a file and replaces it with the new string
     * @param contents the string with which to fill the file
     * @param file the file to be overwritten
     */
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
