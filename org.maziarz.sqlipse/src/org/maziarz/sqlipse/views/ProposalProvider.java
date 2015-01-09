package org.maziarz.sqlipse.views;

import java.util.List;

import org.eclipse.jface.text.source.SourceViewer;
import org.maziarz.sqlipse.views.SourceViewerUtils.SourceStatement;

public class ProposalProvider {

	private SourceViewer sourceViewer;
	private List<String> proposals;

	public ProposalProvider(List<String> proposals) {
		this.proposals = proposals;
	}

	List<String> getProposals() {

		SourceStatement stmt = SourceViewerUtils.getScript(sourceViewer);
		
		return proposals;
	}

}
