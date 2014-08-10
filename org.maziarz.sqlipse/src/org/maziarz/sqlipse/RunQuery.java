package org.maziarz.sqlipse;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class RunQuery extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
		
		if (currentSelection instanceof TextSelection ) {
			TextSelection currentSelection2 = (TextSelection) currentSelection;
			
			if (currentSelection2.isEmpty()) {
				
				System.out.println("Startline: "+currentSelection2.getStartLine());
				
			}
			
			
			String text = currentSelection2.getText();
			System.out.println("Run Query: "+text);
		}
		
		System.out.println(currentSelection);
		
		return null;
	}

}
