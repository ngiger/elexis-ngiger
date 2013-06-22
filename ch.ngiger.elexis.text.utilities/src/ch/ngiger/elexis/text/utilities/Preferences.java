/*******************************************************************************
 * Copyright (c) 2013 Niklaus Giger <niklaus.giger@member.fsf.org>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Niklaus Giger - initial API and implementation, adapted code from
 *     Jörg Sigle       initial idea
 ******************************************************************************/
package ch.ngiger.elexis.text.utilities;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.Hub;
import ch.elexis.preferences.SettingsPreferenceStore;

/**
 * Einstelllungsseite fuer Text-Utilities-Plugin.
 * 
 * @author Niklaus Giger
 */
public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private static SettingsPreferenceStore store;
	public static final String CFG_SHOW_STRESSTEST =
		ch.elexis.preferences.Messages.Texterstellung_TextProcessor + "/show_stresstest"; //$NON-NLS-1$
	public static final String CFG_NR_LOOPS =
		ch.elexis.preferences.Messages.Texterstellung_TextProcessor + "/nr_loops"; //$NON-NLS-1$
	
	static {
		store = new SettingsPreferenceStore(Hub.mandantCfg);
		store.setDefault(CFG_SHOW_STRESSTEST, false);
		store.setDefault(CFG_NR_LOOPS, 100);
	}
	
	public Preferences(){
		super(GRID);
		setPreferenceStore(store);
	}
	
	@Override
	public void init(IWorkbench workbench){}
	
	@Override
	protected void createFieldEditors(){
		addField(new BooleanFieldEditor(CFG_SHOW_STRESSTEST, Messages.Preferences_Allow_Stresstest,
			getFieldEditorParent()));
		addField(new IntegerFieldEditor(CFG_NR_LOOPS, Messages.Preferences_NrLoopsInStresstest,
			getFieldEditorParent()));
	}
	
	/**
	 * @return whether to run stresstest
	 */
	public static boolean getMustRunStressTest(){
		boolean val = Hub.mandantCfg.get(CFG_SHOW_STRESSTEST, false);
		return val;
	}
	
	/**
	 * @return nr of loops in stresstest.
	 */
	public static int getNrLoops(){
		int n = store.getInt(CFG_NR_LOOPS);
		if (n < 1)
			n = 1;
		return n;
	}
	
}
