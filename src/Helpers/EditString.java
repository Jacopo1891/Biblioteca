package Helpers;

public final class EditString {

	public static String Capitalize ( String str) {
		/**
		 * Capitalize first letter of each word in a string
		 */
		String [] words = str.split(" ");
		String capitalized_string = "";
		for (int i = 0; i < words.length; i++ ) {
			capitalized_string += words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
			capitalized_string += (i != words.length-1 ) ? " " : "";
		}
		return capitalized_string;
	}
}
