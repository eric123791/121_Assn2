// Hua Hsin 49399981
package ir.assignments.three;

import ir.assignments.three.Frequency;

import java.io.File;
import java.util.*;


/**
 * Counts the total number of words and their frequencies in a text file.
 */
public final class WordFrequencyCounter {
	/**
	 * This class should not be instantiated.
	 */
	private WordFrequencyCounter() {}

	/**
	 * Takes the input list of words and processes it, returning a list
	 * of {@link Frequency}s.
	 * 
	 * This method expects a list of lowercase alphanumeric strings.
	 * If the input list is null, an empty list is returned.
	 * 
	 * There is one frequency in the output list for every 
	 * unique word in the original list. The frequency of each word
	 * is equal to the number of times that word occurs in the original list. 
	 * 
	 * The returned list is ordered by decreasing frequency, with tied words sorted
	 * alphabetically.
	 * 
	 * The original list is not modified.
	 * 
	 * Example:
	 * 
	 * Given the input list of strings 
	 * ["this", "sentence", "repeats", "the", "word", "sentence"]
	 * 
	 * The output list of frequencies should be 
	 * ["sentence:2", "the:1", "this:1", "repeats:1",  "word:1"]
	 *  
	 * @param words A list of words.
	 * @return A list of word frequencies, ordered by decreasing frequency.
	 */

	static Comparator<Frequency> comparator = new Comparator<Frequency>() {
		public int compare(Frequency o1, Frequency o2) {
			if( o1.getFrequency() > o2.getFrequency())
				return -1;
			else if ( o1.getFrequency() < o2.getFrequency())
				return 1 ;
			else
			{
				return o1.getText().compareTo(o2.getText());
			}
		}
	};
	public static List<Frequency> computeWordFrequencies(List<String> words) {
		// TODO Write body!
		List<Frequency> result = new ArrayList<Frequency> ();

		if (words.isEmpty())
			return result;
		else
		{			
			Map<String, Integer> map = new HashMap<>();
			for(String w: words)
			{
				Integer n = map.get(w);
				n = (n == null) ? 1: ++n;
				map.put(w, n);
			}

			Iterator it = map.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry pairs = (Map.Entry) it.next();
				Frequency frequency = new Frequency ((String) pairs.getKey(), (Integer) pairs.getValue());
				result.add(frequency);
			}
			Collections.sort(result, comparator);	


		}
		return result;

	}

	/**
	 * Runs the word frequency counter. The input should be the path to a text file.
	 * 
	 * @param args The first element should contain the path to a text file.
	 * @throws Exception 
	 */
	/*public static void main(String[] args) throws Exception {
		File file = new File(args[0]);
		List<String> words;
		words = Utilities.tokenizeFile(file);
		List<Frequency> frequencies = computeWordFrequencies(words);
		Utilities.printFrequencies(frequencies);
	}*/
}
