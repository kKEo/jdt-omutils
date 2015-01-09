package org.maziarz.sqlipse.views;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class SqlSourceViewerConfiguration extends SourceViewerConfiguration {

	private static final String[] keywords = new String[] { "SELECT", "FROM", "WHERE", "JOIN", "GROUP" };

	private final class FieldNameDetector implements IWordDetector {
		@Override
		public boolean isWordStart(char c) {
			return Character.isJavaIdentifierStart(c);
		}

		@Override
		public boolean isWordPart(char c) {
			return Character.isJavaIdentifierPart(c);
		}
	}

	private ProposalProvider proposalProvider;

	public SqlSourceViewerConfiguration(ProposalProvider proposalProvider) {
		this.proposalProvider = proposalProvider;
	}


	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

		PresentationReconciler reconciler = new PresentationReconciler();
		BufferedRuleBasedScanner scanner = new BufferedRuleBasedScanner(4096);

		Color darkBlueColor = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
		Color blackColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		Color numbersColor = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA);
		Color stringsColor = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);

		Token defaultToken = new Token(new TextAttribute(darkBlueColor, null, SWT.NORMAL));
		WordRule wordRule = new WordRule(new FieldNameDetector(), defaultToken, true);
		Token inputFieldToken = new Token(new TextAttribute(blackColor, null, SWT.NORMAL));
		for (String fieldName : keywords) {
			wordRule.addWord(fieldName, inputFieldToken);
		}

		Token stringToken = new Token(new TextAttribute(stringsColor, null, SWT.NORMAL));

		scanner.setRules(new IRule[] { //
		new NumberRule(new Token(new TextAttribute(numbersColor))),//
				wordRule, //
				new PatternRule("'", "'", stringToken, '\\', false) });

		DefaultDamagerRepairer damagerRepairer = new DefaultDamagerRepairer(scanner);

		reconciler.setDamager(damagerRepairer, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(damagerRepairer, IDocument.DEFAULT_CONTENT_TYPE);

		return reconciler;
	}


	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

		ContentAssistant assistant = new ContentAssistant();

		ProposalProvidedContentAssistProcessor processor = new ProposalProvidedContentAssistProcessor();
		processor.setProposalProvider(proposalProvider);
		assistant.setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);

		assistant.enableAutoActivation(true);
		assistant.enablePrefixCompletion(true);
		assistant.setAutoActivationDelay(0);
		assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);

		return assistant;
	}


}
