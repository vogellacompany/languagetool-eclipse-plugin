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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.markup.AnnotatedTextBuilder;
import org.languagetool.rules.RuleMatch;

public class Engine implements ISpellingEngine {
	
	@Override
	public void check(IDocument document, IRegion[] regions, SpellingContext context,
			ISpellingProblemCollector collector, IProgressMonitor monitor) {
		JLanguageTool.setDataBroker(new EclipseRessourceDataBroker());

		Language language = null;
		JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
		language = langTool.getLanguage();

		if (language == null) {
			return;
		}

		for (IRegion region : regions) {
			AnnotatedTextBuilder textBuilder = new AnnotatedTextBuilder();
			List<RuleMatch> matches;
			try {
				populateBuilder(textBuilder, document.get(region.getOffset(), region.getLength()));
				matches = langTool.check(textBuilder.build());
				for (RuleMatch match : matches) {
					collector.accept(new LTSpellingProblem(match));
//					addMarker(match);
				}
			} catch (IOException | BadLocationException e) {
				e.printStackTrace(); 
			}
		}
	}

	private void addMarker(RuleMatch match) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
				IEditorInput currentEditorInput = activeWorkbenchWindow.getActivePage()
						.getActiveEditor().getEditorInput();
				IFile file = null;
				if (currentEditorInput instanceof IFileEditorInput) {
					file = ((IFileEditorInput) currentEditorInput).getFile();
					if (file != null) {
						IMarker marker;
						try {
							marker = file.createMarker(IMarker.PROBLEM);
							marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
							marker.setAttribute(IMarker.MESSAGE, match.getMessage());
							marker.setAttribute(IMarker.LINE_NUMBER, match.getLine() + 1);
						} catch (CoreException e) {
							e.printStackTrace();
						}
					}
				}
			}			
		});
	}

	private static void populateBuilder(AnnotatedTextBuilder builder, String lines) {
		String[] splittedList = lines.split("(?<=\\s|\\||\\s\\_|\\[)|(?=\\_\\s|\\])");
		for (String singleWord : splittedList) {
			if (isMarkup(singleWord)) {
				builder.addMarkup(singleWord);
				continue;
			}
			builder.addText(singleWord);
		}
	}

	private static boolean isMarkup(String line) {
		if (line.startsWith("image::")) {
			return true;
		}

		if (line.startsWith("menu:")) {
			return true;
		}

		if (line.contains("_")) {
			return true;
		}
		
		if(line.contains("`")) {
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

		return false;
	}

}
