package org.maziarz.jdt.utils;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class ModuleDefintionHyperlinkDetector extends AbstractHyperlinkDetector {

	public ModuleDefintionHyperlinkDetector() {
		System.out.println("ModuleDefintionHyperlinkDetector");
	}

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {

		System.out.println("detect hypoerlink");

//		ITextEditor textEditor = (ITextEditor) getAdapter(ITextEditor.class);

		// if (region == null || !(textEditor instanceof JavaEditor))
		// return null;

		//		IAction openAction= textEditor.getAction("OpenEditor"); //$NON-NLS-1$
		// if (!(openAction instanceof SelectionDispatchAction))
		// return null;

//		int offset = region.getOffset();

		return null;
	}

}
