package org.maziarz.jdt.utils.handlers;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;


public class PrepareTestCaseHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		Object selectedObject = selection.getFirstElement();
		
		if (selectedObject instanceof IFile) {
			IFile file = (IFile) selectedObject;
			
			IContainer fileContainer = file.getParent();
			if (fileContainer instanceof IFolder) {
				IFolder testSuiteFolder = (IFolder)fileContainer;
				
				String testFileName = file.getName();
				int lastDot = testFileName.lastIndexOf('.');
				String testFileExtension = testFileName.substring(lastDot);
				String testNameBase = testFileName.substring(0,lastDot);
					
				if (testFileName.indexOf(".in") == -1){
					String fixInFileTest = testNameBase+".in"+testFileExtension;
					try {
						file.move(fileContainer.getFullPath().append(fixInFileTest), IResource.NONE, null);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
				
				IFile outFileTest = testSuiteFolder.getFile(testNameBase+".out.csv");
				if (outFileTest.exists() == false) {
					
					byte[] bytes = "".getBytes();
					InputStream source = new ByteArrayInputStream(bytes);
					try {
						outFileTest.create(source, IResource.NONE, null);
					} catch (CoreException e) {
						e.printStackTrace();
					}
					
				}
				
			}
			
		}
		
		return null;
	}

}
