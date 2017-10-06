package com.vogella.eclipse.languagetool.spellchecker.internal;

import org.languagetool.markup.AnnotatedTextBuilder;

public class MarkupUtil {

	public static boolean isMarkup(String line) {
		if (line.startsWith("image::")) {
			return true;
		}

		if (line.startsWith("menu:")) {
			return true;
		}

		if (line.contains("_")) {
			return true;
		}

		if (line.contains("`")) {
			return true;
		}

		if (line.startsWith("|")) {
			return true;
		}

		if (line.startsWith("btn:")) {
			return true;
		}

		if (line.startsWith("include::")) {
			return true;
		}

		if (line.startsWith("----")) {
			return true;
		}

		if (line.contains("\\") || line.contains("/")) {
			return true;
		}

		return false;
	}

	public static String[] splitLine(String lines) {
		return lines.split("(image::.*\\[.*\\]|btn:|menu:|include::|[\\s\\[\\`])" + "|" + "(?=[\\]\\[\\`])");
	}

	public static void populateBuilder(AnnotatedTextBuilder builder, String lines) {
		String[] splittedList = splitLine(lines);
		for (String token : splittedList) {
			if (MarkupUtil.isMarkup(token)) {
				builder.addMarkup(token);
				continue;
			}
			builder.addText(token);
		}
	}

}
