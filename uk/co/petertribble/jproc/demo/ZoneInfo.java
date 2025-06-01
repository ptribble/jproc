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

package uk.co.petertribble.jproc.demo;

import uk.co.petertribble.jproc.api.*;
import uk.co.petertribble.jproc.client.PClientConfig;
import uk.co.petertribble.jproc.gui.ZoneInfoTable;
import uk.co.petertribble.jproc.gui.JProcResources;
import java.util.Set;
import java.util.HashSet;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A process view rather like top, displaying usage aggregated by zone.
 *
 * @author Peter Tribble
 */
public final class ZoneInfo extends JPdemo implements ActionListener {

    private static final long serialVersionUID = 1L;

    /**
     * The JProc to query for data.
     */
    private JProc jproc;
    /**
     * The table embedded in this demo.
     */
    private ZoneInfoTable jpip;

    /**
     * A menu item to select all users.
     */
    private JCheckBoxMenuItem allUserItem;
    /**
     * Menu items to select specific users.
     */
    private transient Set<JCheckBoxMenuItem> userSelectionItems;

    private static final String VERSION = "ZoneInfo version 1.0";

    /**
     * Create a new ZoneInfo application.
     */
    public ZoneInfo() {
	this(new JProc(), true);
    }

    /**
     * Run the ZoneInfo demo.
     *
     * @param pcc a PClientConfig containing client configuration
     */
    public ZoneInfo(PClientConfig pcc) {
	this(new JProc(pcc), true);
    }

    /**
     * Create a new ZoneInfo application.
     *
     * @param jproc a JProc object to query for process information
     * @param standalone  a boolean, true if the demo is a standalone
     */
    public ZoneInfo(JProc jproc, boolean standalone) {
	super("ZoneInfo", standalone);
	this.jproc = jproc;

        // create main display panel
	JProcessSet jps = new JProcessSet(jproc);

        jpip = new ZoneInfoTable(jproc, new JProcessFilter(jps),
				DEFAULT_INTERVAL);

	JPanel mainPanel = new JPanel(new BorderLayout());
	mainPanel.add(new JScrollPane(jpip));
	setContentPane(mainPanel);

	addInfoPanel(mainPanel, VERSION);

	userSelectionItems = new HashSet<>();
	if (jps.getUsers().size() > 1) {
	    addUserMenu(jps);
	}

	setSize(640, 480);
	validate();
	setVisible(true);
    }

    /*
     * Construct a menu containing all users.
     *
     * FIXME: dynamically update the menu as users come and go.
     */
    private void addUserMenu(JProcessSet jps) {
	JMenu userMenu = new JMenu(JProcResources.getString("USER.MENU"));
	userMenu.setMnemonic(KeyEvent.VK_Z);
	allUserItem = new JCheckBoxMenuItem(
				JProcResources.getString("USER.ALL"), true);
	allUserItem.addActionListener(this);
	userMenu.add(allUserItem);
	userMenu.addSeparator();
	for (String s : jps.getUsers()) {
	    JCheckBoxMenuItem jmi = new JCheckBoxMenuItem(s);
	    jmi.addActionListener(this);
	    userMenu.add(jmi);
	    userSelectionItems.add(jmi);
	}
	jm.add(userMenu);
    }

    private void handleUser(JCheckBoxMenuItem jmi) {
	boolean selected = jmi.isSelected();
	if (jmi == allUserItem) {
	    if (selected) {
		for (JCheckBoxMenuItem jcbmi : userSelectionItems) {
		    jcbmi.setSelected(false);
		}
		jpip.unSetUser();
	    } else {
		/*
		 * disable unselection of all users.
		 */
		allUserItem.setSelected(true);
	    }
	} else {
	    /*
	     * If we've selected a user, unselect everything else.
	     */
	    if (selected) {
		for (JCheckBoxMenuItem jcbmi : userSelectionItems) {
		    if (jcbmi != jmi) {
			jcbmi.setSelected(false);
		    }
		}
		allUserItem.setSelected(false);
		jpip.setUser(jproc.getUserId(jmi.getText()));
	    } else {
		allUserItem.setSelected(true);
		jpip.unSetUser();
	    }
	}
    }

    @Override
    public void stopLoop() {
	jpip.stopLoop();
    }

    @Override
    public void setDelay(int i) {
	jpip.setDelay(i);
	setLabelDelay(i);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	super.actionPerformed(e);
	if (e.getSource() == allUserItem) {
	    handleUser((JCheckBoxMenuItem) e.getSource());
	}
	if (userSelectionItems.contains(e.getSource())) {
	    handleUser((JCheckBoxMenuItem) e.getSource());
	}
    }

    /**
     * Create a new ZoneInfo application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
	if (args.length == 0) {
	    new ZoneInfo();
	} else if (args.length == 2) {
	    if ("-s".equals(args[0])) {
		new ZoneInfo(
		    new PClientConfig(args[1], PClientConfig.CLIENT_XMLRPC));
	    } else if ("-S".equals(args[0])) {
		new ZoneInfo(
		    new PClientConfig(args[1], PClientConfig.CLIENT_REST));
	    } else {
		System.err.println("Usage: zoneinfo [-s|-S server]");
	    }
	} else {
	    System.err.println("Usage: zoneinfo [-s|-S server]");
	}
    }
}
