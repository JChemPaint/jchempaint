package org.openscience.jchempaint.dialog;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * This class is used to display the Wait Dialog. This Dialog is displayed
 * when the system is busy processing. All the GUI controls in this dialog
 * are initialized once and the static methods showDialog/hideDialog, uses
 * this instance to show/hide.

 *
 * @since 1.0
 * @version 1.0
 */
public class WaitDialog extends JDialog  {
    /**
     * 
     */

    private JLabel jLabel1 = new JLabel();

    // single instance of this class, used through out the scope of the application
    private static WaitDialog dlg = new  WaitDialog();

    /**
     * The constructor intialies all the GUI controls
     */
    private WaitDialog() {
        try {
            jbInit();
        } catch(Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * This method intializes all the GUI controls and adds it to the Panel
     *
     * @exception Exception if any exception, while creating GUI controls
     */
    private void jbInit() throws Exception {
        this.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER));
        this.setResizable(false);
        
        this.setTitle("Please wait - JChemPaint busy");
        jLabel1.setText("Processing in background or loading library...");
        jLabel1.setFont(new Font("Tahoma", 1, 13));
        this.getContentPane().add(jLabel1, null);
    }

    /**
     * This static method uses pre-created dialog, positions it in the center
     * and displays it to the user.
     */
    public static void showDialog() {
        dlg.setSize(300, 70);
        dlg.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width/2-dlg.getSize().width/2, Toolkit.getDefaultToolkit().getScreenSize().height/2-dlg.getSize().height/2);

        if (!dlg.isVisible())
            dlg.setVisible(true);
        dlg.paint(dlg.getGraphics());
    }

    /**

     * This static method hides the wait dialog.
     */
    public static void hideDialog()   {
        if (dlg.isVisible())
            dlg.setVisible(false);
    }
}