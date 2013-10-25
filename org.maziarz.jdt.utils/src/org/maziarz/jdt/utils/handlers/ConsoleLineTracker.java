package org.maziarz.jdt.utils.handlers;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.console.IOConsole;

public class ConsoleLineTracker implements IConsoleLineTracker {

	 private IConsole fConsole;
	
	@Override
	public void init(IConsole console) {
		fConsole = console;
	}

	@Override
	public void lineAppended(IRegion line) {
		try {
			String lineAdded = fConsole.getDocument().get(line.getOffset(), line.getLength());
			
			Pattern p = Pattern.compile("/{1}[^/]{1}[^\\s]*");
			Matcher m = p.matcher(lineAdded);
			while(m.find()){
				File file = new File(m.group());
//				System.out.print(">"+m.group()+"("+m.start()+","+m.end()+")");
				if (file.exists() && !file.isDirectory()) {
					// Can be also uses FileLink class to create hyperlink
					((IOConsole) fConsole).addHyperlink(new ExternalFileHyperlink(file, 0), line.getOffset()+m.start(), m.end()-m.start());
					
				} 
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void dispose() {
		fConsole = null;
	}

}
