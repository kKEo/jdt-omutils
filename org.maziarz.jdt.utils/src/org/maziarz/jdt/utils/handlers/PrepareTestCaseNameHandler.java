package org.maziarz.jdt.utils.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class PrepareTestCaseNameHandler extends AbstractHandler{

	private static Clipboard myClipboard;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		
		Object selectedObject = selection.getFirstElement();
		
		if (selectedObject instanceof IFile) {
			IFile file = (IFile) selectedObject;
			
			IContainer fileContainer = file.getParent();
			
			if (fileContainer instanceof IFolder) {
				IFolder testSuiteFolder = (IFolder) fileContainer;
				
				String testSuiteName = testSuiteFolder.getName();
				String testName = file.getName();
				
				//get rid of input file indicator (.in.*)
				testName = testSuiteName+":"+testName.substring(0,testName.lastIndexOf(".in"));
				
				if (myClipboard == null) {
					myClipboard = new Clipboard(window.getShell().getDisplay());
				}
				
				TextTransfer textTransfer = TextTransfer.getInstance();
				myClipboard.setContents(new Object[] {testName}, new Transfer[]{textTransfer});
				
			}
			
		}
		
		
		
		
		
		return null;
	}

}
