package org.maziarz.sqlipse.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;


final class ProposalProvidedContentAssistProcessor implements IContentAssistProcessor {

	private static final ICompletionProposal[] NO_PROPOSALS = new ICompletionProposal[0];
	private static final IContextInformation[] NO_CONTEXTS = new IContextInformation[0];

	private ProposalProvider proposalProvider;
	private boolean allowEmptyPrefix = true;

	private static final class Proposal implements ICompletionProposal {

		private final String text;
		private final String prefix;
		private final int offset;

		public Proposal(String text, String prefix, int offset) {
			this.text = text;
			this.prefix = prefix;
			this.offset = offset;
		}

		@Override
		public void apply(IDocument document) {
			try {
				document.replace(offset, 0, text);
			} catch (Exception e) {
				throw new RuntimeException(String.format(" %s, %s", text, offset), e);
			}
		}

		@Override
		public Point getSelection(IDocument document) {
			return new Point(offset + text.length(), 0);
		}

		@Override
		public String getAdditionalProposalInfo() {
			return null;
		}

		@Override
		public String getDisplayString() {
			return prefix + text;
		}

		@Override
		public Image getImage() {
			return null;
		}

		@Override
		public IContextInformation getContextInformation() {
			return null;
		}

		@Override
		public String toString() {
			return String.format("%s$%s:%s", prefix, text, offset);
		}

	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return NO_CONTEXTS;
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int docOffset) {

		String prefix = SourceViewerUtils.getPrefix(viewer, docOffset);
		if (!allowEmptyPrefix || (prefix == null || prefix.isEmpty())) {
			return NO_PROPOSALS;
		}
		
		List<ICompletionProposal> suggestions = new ArrayList<ICompletionProposal>();

		for (String s : this.proposalProvider.getProposals()) {
			if (s.startsWith(prefix)) {
				suggestions.add(new Proposal(s.substring(prefix.length()), prefix, docOffset));
			}
		}
		
		return suggestions.toArray(new ICompletionProposal[0]);
	}

	public void setProposalProvider(ProposalProvider proposalProvider) {
		this.proposalProvider = proposalProvider;
	}

	public void setAllowEmptyPrefix(boolean allowEmptyPrefix) {
		this.allowEmptyPrefix = allowEmptyPrefix;
	}

}