package org.maziarz.jdt.utils.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.maziarz.jdt.utils.OmutilsActivator;

public class OmutilsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public OmutilsPreferencePage() {
		super(GRID);
		setPreferenceStore(OmutilsActivator.getDefault().getPreferenceStore());
		setDescription("Default application used for opening locations");
	}

	@Override
	public void createFieldEditors() {
		addField(new DirectoryFieldEditor(PreferenceConstants.P_PATH, "&Directory preference:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_BOOLEAN, "&An example of a boolean preference",
				getFieldEditorParent()));

		addField(new RadioGroupFieldEditor(PreferenceConstants.P_CHOICE, "An example of a multiple-choice preference", 1,
				new String[][] { { "&Choice 1", "choice1" }, { "C&hoice 2", "choice2" } }, getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_STRING, "A &text preference:", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
	}

}