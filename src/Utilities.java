// Hua Hsin 49399981

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * A collection of utility methods for text processing.
 */
public class Utilities {
	/**
	 * Reads the input text file and splits it into alphanumeric tokens.
	 * Returns an ArrayList of these tokens, ordered according to their
	 * occurrence in the original text file.
	 *
	 * Non-alphanumeric characters delineate tokens, and are discarded.
	 *
	 * Words are also normalized to lower case.
	 *
	 * Example:
	 *
	 * Given this input string
	 * "An input string, this is! (or is it?)"
	 *
	 * The output list of strings should be
	 * ["an", "input", "string", "this", "is", "or", "is", "it"]
	 *
	 * @param input The file to read in and tokenize.
	 * @return The list of tokens (words) from the input file, ordered by occurrence.
	 * @throws Expection
	 */
	private static ArrayList<String> stopWordsList = stopWords();
	
	public static ArrayList<String> tokenizeString(String text) throws Exception {
		// TODO Write body!
		ArrayList<String> result = tokenizeWithStopWords(text, stopWordsList);

		return result;

	}

	private static ArrayList<String> tokenizeWithStopWords(String text, ArrayList<String> stopWordsList ) throws Exception {
		// TODO Write body!
		Scanner  sc = new Scanner (text);
		ArrayList<String> result = new ArrayList<String> ();

		while(sc.hasNext())
		{
			String word = sc.next();
			word = word.toLowerCase().replaceAll("[^a-z1-9']+", "");
			if(!stopWordsList.contains(word))
				result.add(word);
		}
		sc.close();
		return result;

	}

	private static ArrayList<String> tokenize(File input) throws Exception {
		// TODO Write body!
		String path = input.getName();
		Scanner  sc = new Scanner (new FileReader(path));
		ArrayList<String> result = new ArrayList<String> ();

		while(sc.hasNext())
		{
			String line = sc.next();
			line = line.toLowerCase().replaceAll("[^a-z1-9']+", "");
			result.add(line);
		}
		sc.close();
		return result;
	}

	private static ArrayList<String> stopWords()
	{
		try{
		File file = new File("stopwordlist.txt");
		ArrayList <String> result =  tokenize(file);
		return result;
		}catch(Exception e){
			System.out.println("STOPWORDS ERROR");
		}
		return null;
	}

	/**
	 * Takes a list of {@link Frequency}s and prints it to standard out. It also
	 * prints out the total number of items, and the total number of unique items.
	 *
	 * Example one:
	 *
	 * Given the input list of word frequencies
	 * ["sentence:2", "the:1", "this:1", "repeats:1",  "word:1"]
	 *
	 * The following should be printed to standard out
	 *
	 * Total item count: 6
	 * Unique item count: 5
	 *
	 * sentence	2
	 * the		1
	 * this		1
	 * repeats	1
	 * word		1
	 *
	 *
	 * Example two:
	 *
	 * Given the input list of 2-gram frequencies
	 * ["you think:2", "how you:1", "know how:1", "think you:1", "you know:1"]
	 *
	 * The following should be printed to standard out
	 *
	 * Total 2-gram count: 6
	 * Unique 2-gram count: 5
	 *
	 * you think	2
	 * how you		1
	 * know how		1
	 * think you	1
	 * you know		1
	 *
	 * @param frequencies A list of frequencies.
	 */
	public static void printFrequencies(List<Frequency> frequencies) throws Exception {
		// TODO Write body!
		int total_count = 0;
		int uniq_count = frequencies.size();
		if(uniq_count == 0)
		{
			System.out.println("NO ITEM!");
			return;
		}

		for(int i = 0; i < uniq_count; ++i)
			total_count += frequencies.get(i).getFrequency();

		System.out.println("Total item count: " + total_count);
		System.out.println("Unique item count: " + uniq_count);

		for(int i = 0; i < uniq_count; ++i)
			System.out.println(frequencies.get(i).getText() + "	" + frequencies.get(i).getFrequency());

	}

}
