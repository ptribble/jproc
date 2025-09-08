/*
 * SPDX-License-Identifier: CDDL-1.0
 *
 * CDDL HEADER START
 *
 * This file and its contents are supplied under the terms of the
 * Common Development and Distribution License ("CDDL"), version 1.0.
 * You may only use this file in accordance with the terms of version
 * 1.0 of the CDDL.
 *
 * A full copy of the text of the CDDL should have accompanied this
 * source. A copy of the CDDL is also available via the Internet at
 * http://www.illumos.org/license/CDDL.
 *
 * CDDL HEADER END
 *
 * Copyright 2025 Peter Tribble
 *
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
public final class UserProcessInfoFrame extends JFrame
    implements ActionListener {

    private static final long serialVersionUID = 1L;

    /**
     * The table embedded in this frame.
     */
    JPinfoTable jpip;

    /**
     * Create a new UserProcessInfoFrame.
     *
     * @param jproc a JProc object to query for process information
     * @param uid the userid to show processes for
     * @param interval the display update interval, in seconds
     */
    public UserProcessInfoFrame(final JProc jproc, final int uid,
				final int interval) {
	setTitle(JProcResources.getString("USER.TITLE") + " "
		+ jproc.getUserName(uid));
	setLayout(new BorderLayout());

	JProcessFilter jpf = new JProcessFilter(new JProcessSet(jproc));
	jpf.setUser(uid);

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
    private void removeColumn(final JCheckBoxMenuItem jmi) {
	jmi.setSelected(false);
	jpip.removeColumn(jmi.getText());
    }

    /*
     * Show the named column.
     */
    private void addColumn(final JCheckBoxMenuItem jmi) {
	jmi.setSelected(true);
	jpip.addColumn(jmi.getText());
    }

    /*
     * Construct a menu allowing the user to select/unselect columns.
     */
    private void addColumnMenu(final JMenuBar jm) {
	JMenu columnMenu = new JMenu(JProcResources.getString("COLUMN.MENU"));
	columnMenu.setMnemonic(KeyEvent.VK_C);
	Map<String, JCheckBoxMenuItem> columnMap = new HashMap<>();
	for (String s : jpip.columns()) {
	    JCheckBoxMenuItem jmi = new JCheckBoxMenuItem(s);
	    jmi.setSelected(true);
	    jmi.addActionListener(this);
	    columnMenu.add(jmi);
	    columnMap.put(s, jmi);
	}
	removeColumn(columnMap.get("CT"));
	removeColumn(columnMap.get("USER"));
	removeColumn(columnMap.get("TASK"));
	removeColumn(columnMap.get("PROJ"));
	jm.add(columnMenu);
    }

    private void handleColumn(final JCheckBoxMenuItem jmi) {
	if (jmi.isSelected()) {
	    addColumn(jmi);
	} else {
	    removeColumn(jmi);
	}
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
	handleColumn((JCheckBoxMenuItem) e.getSource());
    }

    /**
     * On closure, stop the table updating.
     */
    class WindowExit extends WindowAdapter {
	@Override
	public void windowClosing(final WindowEvent we) {
	    jpip.stopLoop();
	}
    }
}
