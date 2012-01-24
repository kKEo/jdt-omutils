package org.maziarz.jdt.utils.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.IParameterValues;

public class WindowManagersCommandValues implements IParameterValues {

	@Override
	public Map<String, String> getParameterValues() {
		Map<String, String> commands = new HashMap<String, String>();
		commands.put("gtk", "nautilus");
		commands.put("win32", "explorer.exe");
		return commands;
	}

}
