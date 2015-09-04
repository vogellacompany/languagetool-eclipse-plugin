/*
Copyright (c) 2014 Rémi AUGUSTE <remi.auguste@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package com.vogella.eclipse.languagetool.spellchecker;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

public class Engine implements ISpellingEngine {

	@Override
	public void check(IDocument document, IRegion[] regions, SpellingContext context,
			ISpellingProblemCollector collector, IProgressMonitor monitor) {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		// Replaces JLanguageTool default databrocker by one that works in
		// Eclipse Plugin context
		
		JLanguageTool.setDataBroker(new EclipseRessourceDataBroker());

		Language language = null;
		JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
//		for (Language lang : Language.getAllLanguages();
//			if (lang.getName().equals(store.getString(PreferenceConstants.P_LANGUAGE))) {
//				language = lang;
//				break;
//			}
//		}
		language = langTool.getLanguage();

		if (language == null)
			return;

		langTool.disableRule("COMMA_PARENTHESIS_WHITESPACE");
		langTool.disableRule("EN_QUOTES");

		if (langTool != null) {
			for (IRegion region : regions) {
				List<RuleMatch> matches;
				try {
					matches = langTool.check(document.get(region.getOffset(), region.getLength()));
					for (RuleMatch match : matches) {
						collector.accept(new LTSpellingProblem(match));
					}
				} catch (IOException | BadLocationException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
