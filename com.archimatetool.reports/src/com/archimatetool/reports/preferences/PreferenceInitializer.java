/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.reports.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.archimatetool.reports.ArchiReportsPlugin;



/**
 * Class used to initialize default preference values
 *
 * @author Phillip Beauvoir
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
implements IHTMLReportPreferenceConstants {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = ArchiReportsPlugin.getInstance().getPreferenceStore();

        store.setDefault(HTML_REPORT_POST_PROCESS_COMMAND, ""); //$NON-NLS-1$
    }
}
