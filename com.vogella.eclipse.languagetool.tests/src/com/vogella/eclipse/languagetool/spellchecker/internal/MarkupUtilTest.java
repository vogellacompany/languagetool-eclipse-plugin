package com.vogella.eclipse.languagetool.spellchecker.internal;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MarkupUtilTest {

	@Test
	@DisplayName("Ensure include:: is identified as markup")
	void IsIncludeIdentifiedAsMarkup() {
		assertTrue(MarkupUtil.getMatcher("include::lars[]").find(), "include:: should be indentified as markup");
	}

	@Test
	@DisplayName("Ensure that an image:: tag without description is identified as markup")
	void isImageTagWithoutDescriptionIdentifiedAsMarkup() {
		assertTrue(MarkupUtil.getMatcher("image::androidstudio_installation10.png[]").find(), "image:: should be indentified as markup");
	}
	
	@Test
	@DisplayName("Ensure that an image:: tag with description is identified as markup")
	void isImageTagWithDescriptionIdentifiedAsMarkup() {
		assertTrue(MarkupUtil.getMatcher("image::androidstudio_installation10.png[A description]").find(), "image:: tag with description should be indentified as markup");
	}

//	@Test
//	@DisplayName("Split line correctly for image tag with a description tag")
//	void isLineSplitCorrectlyWithImageTag() {
//		String[] splitLine = MarkupUtil.splitLine("image::androidstudio_installation10.png[A description]");
//		// split does not include the string which was used to split, currently the following test test this asssumption
//		// but needs to be adjusted once David changes the logic
//		assertTrue(splitLine.length == 0, "Split incorrect");
////		assertTrue(splitLine.length==1);
//		
//	}
//
//	@Test
//	@DisplayName("Split line correctly for image tag without a description tag")
//	void isLineSplitCorrectlyWithoutImageTag() {
//		String[] splitLine = MarkupUtil.splitLine("image::androidstudio_installation10.png[]");
//		// split does not include the string which was used to split, currently the following test test this asssumption
//		// but needs to be adjusted once David changes the logic
//		assertTrue(splitLine.length == 0, "Split incorrect");
////		assertTrue(splitLine.length==1);
//		
//	}

	
}
