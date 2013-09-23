package de.wak_sh.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	public static String match(String pattern, String subject) {
		Matcher matcher = Pattern.compile(pattern).matcher(subject);
		matcher.find();
		return matcher.group(1);
	}
}
