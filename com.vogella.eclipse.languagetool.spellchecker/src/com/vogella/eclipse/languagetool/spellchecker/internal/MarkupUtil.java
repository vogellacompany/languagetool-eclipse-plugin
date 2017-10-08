package com.vogella.eclipse.languagetool.spellchecker.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.languagetool.markup.AnnotatedTextBuilder;

public class MarkupUtil {

	// TODO:sentence can not end with markup
	private static Pattern markupPattern = Pattern.compile("(" + "image::.*\\[.*\\]\\s*|" // images
			+ "btn:.*\\[.*\\]\\s*|" // buttons
			+ "menu:.*\\[.*\\]\\s*|" // menus
			+ "_[a-zA-Z0-9\\s]*_|" // italic
			+ "include::.*\\[.*\\]\\s*|" // include
			+ "\\[\\[[\\w\\s]*\\]\\]"// [[androidstudio_installation]]
			+ ") {0,1}" // catch preceeding whitespace if any
	);

	public static Matcher getMatcher(String input) {
		return markupPattern.matcher(input);
	}

	public static void populateBuilder(AnnotatedTextBuilder builder, String text) {
		Matcher matcher = getMatcher(text);
		for (int currentIndex = 0; currentIndex < text.length(); ++currentIndex) {
			if (matcher.find(currentIndex)) {
				if (currentIndex >= matcher.start() && currentIndex < matcher.end()) {
					String markup = matcher.group();
					builder.addMarkup(markup);
					currentIndex += (markup.length() - 1);
					continue;
				}
			}
			char c = text.charAt(currentIndex);
			builder.addText(String.valueOf(c));
		}
	}
}
