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
import uk.co.petertribble.jproc.gui.JPusageTable;
import uk.co.petertribble.jproc.gui.JProcResources;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A process view rather like top.
 *
 * @author Peter Tribble
 */
public final class JPusage extends JPdemo implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JProc jproc;
    private JProcessSet jps;
    private JPusageTable jpup;

    private JCheckBoxMenuItem allUserItem;
    private transient Set<JCheckBoxMenuItem> userSelectionItems;
    private JCheckBoxMenuItem allZoneItem;
    private transient Set<JCheckBoxMenuItem> zoneSelectionItems;
    private transient Set<JCheckBoxMenuItem> columnSelectionItems;

    private static final String VERSION = "JPusage version 1.0";

    /**
     * Create a new JPusage application.
     */
    public JPusage() {
	this(new JProc(), true);
    }

    /**
     * Create a new JPusage application.
     *
     * @param pcc a PClientConfig containing client configuration
     */
    public JPusage(PClientConfig pcc) {
	this(new JProc(pcc), true);
    }

    /**
     * Create a new JPusage application.
     *
     * @param jproc a JProc object to query for process information
     * @param standalone  a boolean, true if the demo is a standalone
     */
    public JPusage(JProc jproc, boolean standalone) {
	super("JPusage", standalone);
	this.jproc = jproc;

        // create main display panel
	jps = new JProcessSet(jproc);

        jpup = new JPusageTable(jproc, new JProcessFilter(jps),
				DEFAULT_INTERVAL);

	JPanel mainPanel = new JPanel(new BorderLayout());
	mainPanel.add(new JScrollPane(jpup));
	setContentPane(mainPanel);

	addInfoPanel(mainPanel, VERSION);

	userSelectionItems = new HashSet<>();
	addUserMenu();

	zoneSelectionItems = new HashSet<>();
	if (jps.getZones().size() > 1) {
	    addZoneMenu();
	}

	columnSelectionItems = new HashSet<>();

	addColumnMenu();

	setSize(640, 480);
	validate();
	setVisible(true);
    }

    /*
     * Construct a menu containing all users.
     *
     * FIXME: dynamically update the menu as users come and go.
     */
    private void addUserMenu() {
	JMenu userMenu = new JMenu(JProcResources.getString("USER.MENU"));
	userMenu.setMnemonic(KeyEvent.VK_U);
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


    /*
     * Hide the named column.
     */
    private void removeColumn(JCheckBoxMenuItem jmi) {
	jmi.setSelected(false);
	jpup.removeColumn(jmi.getText());
    }

    /*
     * Show the named column.
     */
    private void addColumn(JCheckBoxMenuItem jmi) {
	jmi.setSelected(true);
	jpup.addColumn(jmi.getText());
    }

    /*
     * Construct a menu allowing the user to select/unselect columns.
     */
    private void addColumnMenu() {
	JMenu columnMenu = new JMenu(JProcResources.getString("COLUMN.MENU"));
	columnMenu.setMnemonic(KeyEvent.VK_C);
	Map<String, JCheckBoxMenuItem> columnMap = new HashMap<>();
	for (String s : jpup.columns()) {
	    JCheckBoxMenuItem jmi = new JCheckBoxMenuItem(s);
	    jmi.setSelected(true);
	    jmi.addActionListener(this);
	    columnMenu.add(jmi);
	    columnSelectionItems.add(jmi);
	    columnMap.put(s, jmi);
	}
	removeColumn(columnMap.get("RTIME"));
	removeColumn(columnMap.get("NSWAP"));
	removeColumn(columnMap.get("MINF"));
	removeColumn(columnMap.get("MSND"));
	removeColumn(columnMap.get("MRCV"));
	jm.add(columnMenu);
    }

    /*
     * Construct a menu containing all zones.
     *
     * FIXME: dynamically update the menu as zones come and go.
     */
    private void addZoneMenu() {
	JMenu zoneMenu = new JMenu(JProcResources.getString("ZONE.MENU"));
	zoneMenu.setMnemonic(KeyEvent.VK_Z);
	allZoneItem = new JCheckBoxMenuItem(
				JProcResources.getString("ZONE.ALL"), true);
	allZoneItem.addActionListener(this);
	zoneMenu.add(allZoneItem);
	zoneMenu.addSeparator();
	for (String s : jps.getZones()) {
	    JCheckBoxMenuItem jmi = new JCheckBoxMenuItem(s);
	    jmi.addActionListener(this);
	    zoneMenu.add(jmi);
	    zoneSelectionItems.add(jmi);
	}
	jm.add(zoneMenu);
    }

    private void handleUser(JCheckBoxMenuItem jmi) {
	boolean selected = jmi.isSelected();
	if (jmi == allUserItem) {
	    if (selected) {
		for (JCheckBoxMenuItem jcbmi : userSelectionItems) {
		    jcbmi.setSelected(false);
		}
		jpup.unSetUser();
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
		jpup.setUser(jproc.getUserId(jmi.getText()));
	    } else {
		allUserItem.setSelected(true);
		jpup.unSetUser();
	    }
	}
    }

    private void handleZone(JCheckBoxMenuItem jmi) {
	boolean selected = jmi.isSelected();
	if (jmi == allZoneItem) {
	    if (selected) {
		for (JCheckBoxMenuItem jcbmi : zoneSelectionItems) {
		    jcbmi.setSelected(false);
		}
		jpup.unSetZone();
	    } else {
		/*
		 * disable unselection of all zones.
		 */
		allZoneItem.setSelected(true);
	    }
	} else {
	    /*
	     * If we've selected a zone, unselect everything else.
	     */
	    if (selected) {
		for (JCheckBoxMenuItem jcbmi : zoneSelectionItems) {
		    if (jcbmi != jmi) {
			jcbmi.setSelected(false);
		    }
		}
		allZoneItem.setSelected(false);
		jpup.setZone(jproc.getZoneId(jmi.getText()));
	    } else {
		allZoneItem.setSelected(true);
		jpup.unSetZone();
	    }
	}
    }

    private void handleColumn(JCheckBoxMenuItem jmi) {
	if (jmi.isSelected()) {
	    addColumn(jmi);
	} else {
	    removeColumn(jmi);
	}
    }

    @Override
    public void stopLoop() {
	jpup.stopLoop();
    }

    @Override
    public void setDelay(int i) {
	jpup.setDelay(i);
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
	if (e.getSource() == allZoneItem) {
	    handleZone((JCheckBoxMenuItem) e.getSource());
	}
	if (zoneSelectionItems.contains(e.getSource())) {
	    handleZone((JCheckBoxMenuItem) e.getSource());
	}
	if (columnSelectionItems.contains(e.getSource())) {
	    handleColumn((JCheckBoxMenuItem) e.getSource());
	}
    }

    /**
     * Create a new JPusage application.
     *
     * @param args command line arguments, ignored
     */
    public static void main(String[] args) {
	if (args.length == 0) {
	    new JPusage();
	} else if (args.length == 2) {
	    if ("-s".equals(args[0])) {
		new JPusage(
		    new PClientConfig(args[1], PClientConfig.CLIENT_XMLRPC));
	    } else if ("-S".equals(args[0])) {
		new JPusage(
		    new PClientConfig(args[1], PClientConfig.CLIENT_REST));
	    } else {
		System.err.println("Usage: usage [-s|-S server]");
	    }
	} else {
	    System.err.println("Usage: usage [-s|-S server]");
	}
    }
}
