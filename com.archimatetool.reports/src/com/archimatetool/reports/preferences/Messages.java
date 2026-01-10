/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.reports.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.archimatetool.reports.preferences.messages"; //$NON-NLS-1$

    public static String HTMLReportPreferencesPage_0;

    public static String HTMLReportPreferencesPage_1;

    public static String HTMLReportPreferencesPage_2;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
