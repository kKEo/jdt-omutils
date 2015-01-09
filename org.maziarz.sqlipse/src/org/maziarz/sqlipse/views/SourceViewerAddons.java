package org.maziarz.sqlipse.views;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

public class SourceViewerAddons {

	private AbstractHandler fContentAssistHandler;
	private AbstractHandler fUndoHandler;
	private AbstractHandler fRedoHandler;
	private IHandlerService fHandlerService;
	private IHandlerActivation fContentAssistActivation;
	private IHandlerActivation fUndoActivation;
	private IHandlerActivation fRedoActivation;

	public static SourceViewerAddons configure(final SourceViewer sourceViewer, ProposalProvider proposalProvider) {
		return new SourceViewerAddons(sourceViewer, proposalProvider);
	}

	public SourceViewerAddons(final SourceViewer sourceViewer, ProposalProvider proposalProvider) {

		addHandlers(sourceViewer);

		sourceViewer.configure(new SqlSourceViewerConfiguration(proposalProvider));

		sourceViewer.addTextListener(new ITextListener() {

			@Override
			public void textChanged(TextEvent event) {
				System.out.println("Changed: " + sourceViewer.getDocument().get());
			}
		});

	}

	private void addHandlers(final SourceViewer sourceViewer) {
		fContentAssistHandler = new AbstractHandler() {
			public Object execute(ExecutionEvent event) throws org.eclipse.core.commands.ExecutionException {
				sourceViewer.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
				return null;
			}
		};
		fUndoHandler = new AbstractHandler() {
			public Object execute(ExecutionEvent event) throws org.eclipse.core.commands.ExecutionException {
				sourceViewer.doOperation(ITextOperationTarget.UNDO);
				return null;
			}
		};
		fRedoHandler = new AbstractHandler() {
			public Object execute(ExecutionEvent event) throws org.eclipse.core.commands.ExecutionException {
				sourceViewer.doOperation(ITextOperationTarget.REDO);
				return null;
			}
		};
		fHandlerService = (IHandlerService) PlatformUI.getWorkbench().getAdapter(IHandlerService.class);

		sourceViewer.getTextWidget().addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				activateHandlers();
			}

			@Override
			public void focusLost(FocusEvent e) {
				deactivateHandlers();
			}

		});

		sourceViewer.getTextWidget().getParent().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				deactivateHandlers();
			}
		});
	}

	private void deactivateHandlers() {
		if (fContentAssistActivation != null) {
			fHandlerService.deactivateHandler(fContentAssistActivation);
			fContentAssistActivation = null;
		}
		if (fUndoActivation != null) {
			fHandlerService.deactivateHandler(fUndoActivation);
			fUndoActivation = null;
		}
		if (fRedoActivation != null) {
			fHandlerService.deactivateHandler(fRedoActivation);
			fRedoActivation = null;
		}
	}

	private void activateHandlers() {
		fContentAssistActivation = fHandlerService.activateHandler(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS,
				fContentAssistHandler);
		fUndoActivation = fHandlerService.activateHandler(IWorkbenchCommandConstants.EDIT_UNDO, fUndoHandler);
		fRedoActivation = fHandlerService.activateHandler(IWorkbenchCommandConstants.EDIT_REDO, fRedoHandler);
	}

}
