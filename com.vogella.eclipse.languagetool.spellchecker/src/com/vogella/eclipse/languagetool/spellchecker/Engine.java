/*
Copyright (c) 2017 vogella GmbH and others.

 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     remi.auguste@gmail.com - initial API and implementation
 *     vogella GmbH - rewritten the initial code
*******************************************************************************/

package com.vogella.eclipse.languagetool.spellchecker;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.markup.AnnotatedTextBuilder;
import org.languagetool.rules.RuleMatch;

import com.vogella.eclipse.languagetool.spellchecker.internal.MarkupUtil;

public class Engine implements ISpellingEngine {

	private static final String MY_MARKER_TYPE = "com.vogella.eclipse.languagetool.spellchecker.spellingproblem";

	@Override
	public void check(IDocument document, IRegion[] regions, SpellingContext context,
			ISpellingProblemCollector collector, IProgressMonitor monitor) {
		JLanguageTool.setDataBroker(new EclipseRessourceDataBroker());
		JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
		Language language = langTool.getLanguage();
		if (language == null) {
			return;
		}

		for (IRegion region : regions) {
			AnnotatedTextBuilder textBuilder = new AnnotatedTextBuilder();
			List<RuleMatch> matches;
			try {
				MarkupUtil.populateBuilder(textBuilder, document.get(region.getOffset(), region.getLength()));
				matches = langTool.check(textBuilder.build());
				processMatches(collector, matches);
			} catch (IOException | BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Tdestings
	 * 
	 * @param collector
	 * @param matches
	 */

	private void processMatches(ISpellingProblemCollector collector, List<RuleMatch> matches) {
		WorkspaceModifyOperation workspaceRunnable = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor)
					throws CoreException, InvocationTargetException, InterruptedException {
				// TODO
				deleteMarkers();
				matches.forEach(match -> {
					collector.accept(new LTSpellingProblem(match));
					addMarker(match);
				});
			}

		};

		try {
			workspaceRunnable.run(null);
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void deleteMarkers() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
				IEditorInput currentEditorInput = activeWorkbenchWindow.getActivePage().getActiveEditor()
						.getEditorInput();
				if (currentEditorInput instanceof IFileEditorInput) {
					IFile file = ((IFileEditorInput) currentEditorInput).getFile();
					if (file != null) {
						try {
							// delete all markers of current resource and type
							file.deleteMarkers(MY_MARKER_TYPE, true, IResource.DEPTH_ZERO);
						} catch (CoreException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	private void addMarker(RuleMatch match) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
				IEditorInput currentEditorInput = activeWorkbenchWindow.getActivePage().getActiveEditor()
						.getEditorInput();
				if (currentEditorInput instanceof IFileEditorInput) {
					IFile file = ((IFileEditorInput) currentEditorInput).getFile();
					if (file != null) {
						IMarker marker = null;
						try {
							marker = file.createMarker(MY_MARKER_TYPE);
							marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
							marker.setAttribute(IMarker.MESSAGE, match.getMessage());
							marker.setAttribute(IMarker.LOCATION, match.getLine() + 1);
							marker.setAttribute(IMarker.CHAR_START, match.getFromPos());
							marker.setAttribute(IMarker.CHAR_END, match.getToPos());
						} catch (CoreException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

}
