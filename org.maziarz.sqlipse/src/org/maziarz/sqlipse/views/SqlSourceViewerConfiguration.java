package org.maziarz.sqlipse.views;

import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class SqlSourceViewerConfiguration extends SourceViewerConfiguration {

	@Override
	public IUndoManager getUndoManager(ISourceViewer sourceViewer) {
		return super.getUndoManager(sourceViewer);
	}
	
	@Override
	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
		return super.getInformationPresenter(sourceViewer);
	}
	
	@Override
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		return super.getInformationControlCreator(sourceViewer);
	}
	
}
