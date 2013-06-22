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
package ch.ngiger.elexis.text.utilities.handlers;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.data.Brief;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.util.SWTHelper;
import ch.ngiger.elexis.text.utilities.Messages;
import ch.elexis.views.TextView;
import ch.ngiger.elexis.text.utilities.Preferences;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class StressTestAllDocuments extends AbstractHandler {
	
	private static Logger log = LoggerFactory
		.getLogger(ch.ngiger.elexis.text.utilities.Activator.PLUGIN_ID);
	private Action stressTestAction; // added stress test feature.
	
	private TextView tv = null;
	IWorkbenchWindow window = null;
	ISelection selection = null;
	
	/**
	 * The constructor.
	 */
	public StressTestAllDocuments(){
		stressTestAction = new Action("StressTestAllDocuments") { //$NON-NLS-1$
				@Override
				public void run(){
					log.debug("****************************************************************"); //$NON-NLS-1$
					log.debug("js ch.elexis.views/BriefAuswahl.java: Initiating stress test 2."); //$NON-NLS-1$
					log.debug("****************************************************************"); //$NON-NLS-1$
					log.debug("This stress test will open all Briefe of the selected patient one after another, repeatedly, until you close the program or an error occurs."); //$NON-NLS-1$
					
					Integer stressTestPasses = 0;
					Boolean continueStressTest = true;
					
					// obtain a list of all documents for the current patient
					Patient actPat = (Patient) ElexisEventDispatcher.getSelected(Patient.class);
					if (actPat != null) {
						Query<Brief> qbe = new Query<Brief>(Brief.class);
						qbe.add(Brief.FLD_PATIENT_ID, Query.EQUALS, actPat.getId());
						qbe.add(Brief.FLD_TYPE, Query.NOT_EQUAL, Brief.TEMPLATE);
						
						List<Brief> list = qbe.execute();
						log.debug("Liste der Briefe des Patienten: " + list); //$NON-NLS-1$
						
						while (continueStressTest) {
							
							// open one document after another; each adds another pass to the
							// stress test pass count
							for (Brief brief : list) {
								
								if (brief != null) {
									
									stressTestPasses = stressTestPasses + 1;
									log.debug("stress test pass: " + stressTestPasses //$NON-NLS-1$
										+ " - about to load document..."); //$NON-NLS-1$
									
									log.debug("stress test pass: " + stressTestPasses //$NON-NLS-1$
										+ " - o !!= null; (Brief) brief[0.getLabel()]=<" //$NON-NLS-1$
										+ brief.getLabel().toString() + ">"); //$NON-NLS-1$
									log.debug("stress test pass: " //$NON-NLS-1$
										+ stressTestPasses
										+ " - try {} section o != null; about to tv.openDocument(brief)...."); //$NON-NLS-1$
									
									if (tv.openDocument(brief) == false) {
										log.debug("stress test pass: " //$NON-NLS-1$
											+ stressTestPasses
											+ " - try {} section tv.openDocument(brief) returned false. Setting continueStressTest=false."); //$NON-NLS-1$
										SWTHelper.alert(
											ch.elexis.views.Messages.getString("BriefAuswahlErrorHeading"), //$NON-NLS-1$
											ch.elexis.views.Messages.getString("BriefAuswahlCouldNotLoadText")); //$NON-NLS-1$
										continueStressTest = false;
										break;
									} else {
										
										log.debug("stress test pass: " //$NON-NLS-1$
											+ stressTestPasses
											+ " - try {} section tv.openDocument(brief) worked; document should have been loaded."); //$NON-NLS-1$
									}
									log.debug("stress test pass: " + stressTestPasses //$NON-NLS-1$
										+ " - try/catch completed."); //$NON-NLS-1$
									
									if (stressTestPasses > Preferences.getNrLoops()) {
										log.debug("stress test pass: " + stressTestPasses //$NON-NLS-1$
											+ " - Setting continueStressTest=false after " //$NON-NLS-1$
											+ stressTestPasses + " passes have completed."); //$NON-NLS-1$
										continueStressTest = false;
										break;
									}
									
									try {
										log.debug("stress test pass: " //$NON-NLS-1$
											+ stressTestPasses
											+ " - about to Thread.sleep()...(Otherwise the Briefe view content would not be visibly updated.)"); //$NON-NLS-1$
										/*
										 * Nichts von den folgenden hilft tatsächlich gut gegen das
										 * mangelnde Updaten im LibreOffice Frame nach dem ca. 4.
										 * Dokument Thread.sleep(10000); Thread.sleep(1000);
										 * Thread.yield();
										 */
									} catch (Throwable throwable) {
										// handle the interrupt that will happen after the sleep
										log.debug("stress test pass: " //$NON-NLS-1$
											+ stressTestPasses
											+ " - caught throwable; most probably the Thread.sleep() wakeup interrupt signal."); //$NON-NLS-1$
									}
									
									log.debug("****************************************************************"); //$NON-NLS-1$
									
								} // if ( brief != null)
							} // for ( brief : list )
						} // while (continueStressTest)
					} // if (actPat != null )
					log.debug("stress test pass: " + stressTestPasses + " - stress test ends."); //$NON-NLS-1$ //$NON-NLS-2$
				}
			};
	}
	
	/**
	 * the command has been executed, so extract extract the needed information from the application
	 * context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException{
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (!Preferences.getMustRunStressTest()) {
			MessageDialog.openInformation(window.getShell(), 
				Messages.Stresstest,
				Messages.StresstestInPreferencesDisallowed);
			return null;
		}
		selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		
		if (selection != null & selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			for (Iterator<Object> iterator = strucSelection.iterator(); iterator.hasNext();) {
				Object element = iterator.next();
			}
		}
		try {
			tv =
				(TextView) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
					.showView(TextView.ID);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stressTestAction.run();
		return null;
	}
}
