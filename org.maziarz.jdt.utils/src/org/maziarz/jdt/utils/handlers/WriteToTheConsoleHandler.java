package org.maziarz.jdt.utils.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;

public class WriteToTheConsoleHandler extends AbstractHandler implements IHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		MessageConsole console = findConsole("TestConsole");
		
		MessageConsoleStream out = console.newMessageStream();
		out.println("Hello from Generic console sample Ge123ric action");
		out.println("Hello from console sample Ge123ric action");
		
		try {
			console.addHyperlink(new ExternalFileHyperlink(new File("/home/krma/examples.desktop"), 0), 10, 10 );
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		
		IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
		
		try {
			String consoleViewId = IConsoleConstants.ID_CONSOLE_VIEW;
			IConsoleView view = (IConsoleView) activePage.showView(consoleViewId);
			view.display(console);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	
	  private MessageConsole findConsole(String name) {
	      ConsolePlugin plugin = ConsolePlugin.getDefault();
	      IConsoleManager conMan = plugin.getConsoleManager();
	      IConsole[] existing = conMan.getConsoles();
	      for (int i = 0; i < existing.length; i++)
	         if (name.equals(existing[i].getName()))
	            return (MessageConsole) existing[i];
	      //no console found, so create a new one
	      MessageConsole myConsole = new MessageConsole(name, null);
	      conMan.addConsoles(new IConsole[]{myConsole});
	      return myConsole;
	   }

}
