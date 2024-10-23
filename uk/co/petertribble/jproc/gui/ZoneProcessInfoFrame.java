/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at usr/src/OPENSOLARIS.LICENSE
 * or http://www.opensolaris.org/os/licensing.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at usr/src/OPENSOLARIS.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

package uk.co.petertribble.jproc.gui;

import uk.co.petertribble.jproc.api.*;
import java.util.Map;
import java.util.HashMap;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * A process view rather like top.
 *
 * @author Peter Tribble
 */
public class ZoneProcessInfoFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    JPinfoTable jpip;

    /**
     * Create a new ZoneProcessInfoFrame.
     *
     * @param jproc a JProc object to query for process information
     * @param zoneid the zoneid to show processes for
     * @param interval the display update interval, in seconds
     */
    public ZoneProcessInfoFrame(JProc jproc, int zoneid, int interval) {
	setTitle(JProcResources.getString("ZONE.TITLE") + " "
		+ jproc.getZoneName(zoneid));
	setLayout(new BorderLayout());

	JProcessFilter jpf = new JProcessFilter(new JProcessSet(jproc));
	jpf.setZone(zoneid);

	JMenuBar jm = new JMenuBar();
	setJMenuBar(jm);

	addWindowListener(new WindowExit());
	setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        jpip = new JPinfoTable(jproc, jpf, interval);

	setContentPane(new JScrollPane(jpip));

	addColumnMenu(jm);

	setSize(640, 480);
	validate();
	setVisible(true);
    }

    /*
     * Hide the named column.
     */
    private void removeColumn(JCheckBoxMenuItem jmi) {
	jmi.setSelected(false);
	jpip.removeColumn(jmi.getText());
    }

    /*
     * Show the named column.
     */
    private void addColumn(JCheckBoxMenuItem jmi) {
	jmi.setSelected(true);
	jpip.addColumn(jmi.getText());
    }

    /*
     * Construct a menu allowing the user to select/unselect columns.
     */
    private void addColumnMenu(JMenuBar jm) {
	JMenu columnMenu = new JMenu(JProcResources.getString("COLUMN.MENU"));
	columnMenu.setMnemonic(KeyEvent.VK_C);
	Map <String, JCheckBoxMenuItem> columnMap =
	    new HashMap<>();
	for (String s : jpip.columns()) {
	    JCheckBoxMenuItem jmi = new JCheckBoxMenuItem(s);
	    jmi.setSelected(true);
	    jmi.addActionListener(this);
	    columnMenu.add(jmi);
	    columnMap.put(s, jmi);
	}
	removeColumn(columnMap.get("CT"));
	removeColumn(columnMap.get("TASK"));
	removeColumn(columnMap.get("PROJ"));
	removeColumn(columnMap.get("ZONE"));
	jm.add(columnMenu);
    }

    private void handleColumn(JCheckBoxMenuItem jmi) {
	if (jmi.isSelected()) {
	    addColumn(jmi);
	} else {
	    removeColumn(jmi);
	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	handleColumn((JCheckBoxMenuItem) e.getSource());
    }

    /**
     * On closure, stop the table updating.
     */
    class WindowExit extends WindowAdapter {
	@Override
	public void windowClosing(WindowEvent we) {
	    jpip.stopLoop();
	}
    }
}
