package de.wak_sh.client.backend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {

	public static String match(String regex, String input) {
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(input);

		if (matcher.find()) {
			MatchResult result = matcher.toMatchResult();
			return result.group(1);
		}

		return null;
	}

	public static List<MatchResult> matchAll(String regex, String input) {
		List<MatchResult> results = new ArrayList<MatchResult>();

		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(input);

		while (matcher.find()) {
			MatchResult result = matcher.toMatchResult();
			results.add(result);
		}

		return results;
	}

	public static String getMd5(String value) {
		StringBuilder builder = new StringBuilder();
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.update(value.getBytes());
			for (byte b : digest.digest()) {
				builder.append(String.format("%02x", b));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		return builder.toString();
	}
}
