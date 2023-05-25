package pt.unl.fct.di.apdc.geo5.util;

public class Search {

	public static boolean containsWords(String inputString, String[] items) {
		boolean found = true;
		for (String item : items) {
			if (!inputString.contains(item.toLowerCase())) {
				found = false;
				break;
			}
		}
		return found;
	}
}
