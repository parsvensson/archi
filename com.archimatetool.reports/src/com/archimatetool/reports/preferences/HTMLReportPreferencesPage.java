/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.reports.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.ui.UIUtils;
import com.archimatetool.reports.ArchiReportsPlugin;


/**
 * HTML Report Preferences Page
 *
 * @author Phillip Beauvoir
 */
public class HTMLReportPreferencesPage
extends PreferencePage
implements IWorkbenchPreferencePage, IHTMLReportPreferenceConstants {

    private static String HELP_ID = "com.archimatetool.help.prefsHTMLReport"; //$NON-NLS-1$

    private Text fPostProcessCommandTextField;

    public HTMLReportPreferencesPage() {
        setPreferenceStore(ArchiReportsPlugin.getInstance().getPreferenceStore());
    }

    @Override
    protected Control createContents(Composite parent) {
        // Help
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HELP_ID);

        Composite client = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.marginWidth = layout.marginHeight = 0;
        client.setLayout(layout);

        Group settingsGroup = new Group(client, SWT.NULL);
        settingsGroup.setText(Messages.HTMLReportPreferencesPage_0);
        settingsGroup.setLayout(new GridLayout(1, false));
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = 500;
        settingsGroup.setLayoutData(gd);

        Label label = new Label(settingsGroup, SWT.NULL);
        label.setText(Messages.HTMLReportPreferencesPage_1);

        fPostProcessCommandTextField = UIUtils.createSingleTextControl(settingsGroup, SWT.BORDER, false);
        fPostProcessCommandTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label descriptionLabel = new Label(settingsGroup, SWT.WRAP);
        descriptionLabel.setText(Messages.HTMLReportPreferencesPage_2);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = 500;
        descriptionLabel.setLayoutData(gd);

        setValues();

        return client;
    }

    private void setValues() {
        fPostProcessCommandTextField.setText(getPreferenceStore().getString(HTML_REPORT_POST_PROCESS_COMMAND));
    }

    @Override
    public boolean performOk() {
        getPreferenceStore().setValue(HTML_REPORT_POST_PROCESS_COMMAND, fPostProcessCommandTextField.getText());
        return true;
    }

    @Override
    protected void performDefaults() {
        fPostProcessCommandTextField.setText(""); //$NON-NLS-1$
        super.performDefaults();
    }

    @Override
    public void init(IWorkbench workbench) {
    }
}
