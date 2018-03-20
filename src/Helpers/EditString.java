package Helpers;

public final class EditString {

	public static String Capitalize ( String str) {
		/**
		 * Capitalize first letter of a string
		 */
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
}
