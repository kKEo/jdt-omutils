package org.maziarz.sqlipse.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

public class JarListViewer {

	private TableViewer jars;

	public JarListViewer(FormToolkit tk, final Composite parent) {

		Composite c = tk.createComposite(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = layout.marginRight = 10;
		c.setLayout(layout);
		tk.createLabel(c, "Jars");

		Hyperlink browse = tk.createHyperlink(c, "browse", SWT.NONE);

		Table tbl = tk.createTable(c, SWT.BORDER | SWT.READ_ONLY);
		jars = new TableViewer(tbl);
		jars.setContentProvider(ArrayContentProvider.getInstance());
		jars.setLabelProvider(new LabelProvider());

		browse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				FileDialog fileDialog = new FileDialog(parent.getShell(), SWT.MULTI);
				
				if (fileDialog.open() != null) {
					List<String> jarPaths = new ArrayList<String>();
					for (String selected : fileDialog.getFileNames()) {
						jarPaths.add(fileDialog.getFilterPath() + "/" + selected);
					}
					jars.setInput(jarPaths);
					jars.getTable().setFocus();
				}
			}
		});

		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(tbl);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(c);

	}

	public void setInput(String sJars) {
		if (sJars.length() > 0) {
			jars.setInput(Arrays.asList(sJars.split("\n")));
		} else {
			jars.setInput(Collections.emptyList());
		}
	}
	
	public String getJars(){
		StringBuilder sb = new StringBuilder();
		for (Object el : (List<?>)jars.getInput()) {
			sb.append(""+el).append("\n");
		}
		return sb.toString();
	}

	public void addModifyListener(final ModifyListener modifyListener) {

		jars.addPostSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				modifyListener.modifyText(null);
			}
		});
	}

}
