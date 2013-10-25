package org.maziarz.jdt.utils;

import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.part.IPageBookViewPage;

public class ConsolePageParticipant implements IConsolePageParticipant {

	private IOConsole console;
	private IPatternMatchListener listener;
	
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public void init(IPageBookViewPage page, IConsole console) {
		this.console = (IOConsole) console;
		
		if (listener==null){
			listener = new IPatternMatchListener() {
				
				@Override
				public void matchFound(PatternMatchEvent event) {
					int offset = event.getOffset();
					int length = event.getLength();
					System.out.println("Found: "+offset+"("+length+")");
				}
				
				@Override
				public void disconnect() {
				}
				
				@Override
				public void connect(TextConsole console) {
				}
				
				@Override
				public String getPattern() {
					return "/[^/*]";
				}
				
				@Override
				public String getLineQualifier() {
					
					return null;
				}
				
				@Override
				public int getCompilerFlags() {
					return 0;
				}
			};
			
			
			this.console.addPatternMatchListener(listener);
		}
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void activated() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivated() {
		// TODO Auto-generated method stub

	}

}
