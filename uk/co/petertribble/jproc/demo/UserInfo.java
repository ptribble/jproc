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
import uk.co.petertribble.jproc.gui.UserInfoTable;
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
 * A process view rather like top, displaying usage aggregated by user.
 *
 * @author Peter Tribble
 */
public final class UserInfo extends JPdemo implements ActionListener {

    private static final long serialVersionUID = 1L;

    /**
     * The JProc to query for data.
     */
    private JProc jproc;
    /**
     * The table embedded in this demo.
     */
    private UserInfoTable jpip;

    /**
     * A menu item to select all zones.
     */
    private JCheckBoxMenuItem allZoneItem;
    /**
     * Menu items to select specific zones.
     */
    private transient Set<JCheckBoxMenuItem> zoneMenuItems;

    private static final String VERSION = "UserInfo version 1.0";

    /**
     * Create a new UserInfo application.
     */
    public UserInfo() {
	this(new JProc(), true);
    }

    /**
     * Run the UserInfo demo.
     *
     * @param pcc a PClientConfig containing client configuration
     */
    public UserInfo(PClientConfig pcc) {
	this(new JProc(pcc), true);
    }

    /**
     * Create a new UserInfo application.
     *
     * @param jproc a JProc object to query for process information
     * @param standalone  a boolean, true if the demo is a standalone
     */
    public UserInfo(JProc jproc, boolean standalone) {
	super("UserInfo", standalone);
	this.jproc = jproc;

        // create main display panel
	JProcessSet jps = new JProcessSet(jproc);

        jpip = new UserInfoTable(jproc, new JProcessFilter(jps),
				DEFAULT_INTERVAL);

	JPanel mainPanel = new JPanel(new BorderLayout());
	mainPanel.add(new JScrollPane(jpip));
	setContentPane(mainPanel);

	addInfoPanel(mainPanel, VERSION);

	zoneMenuItems = new HashSet<>();
	if (jps.getZones().size() > 1) {
	    addZoneMenu(jps);
	}

	setSize(640, 480);
	validate();
	setVisible(true);
    }

    /*
     * Construct a menu containing all zones.
     *
     * FIXME: dynamically update the menu as zones come and go.
     */
    private void addZoneMenu(JProcessSet jps) {
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
	    zoneMenuItems.add(jmi);
	}
	jm.add(zoneMenu);
    }

    private void handleZone(JCheckBoxMenuItem jmi) {
	boolean selected = jmi.isSelected();
	if (jmi == allZoneItem) {
	    if (selected) {
		for (JCheckBoxMenuItem jcbmi : zoneMenuItems) {
		    jcbmi.setSelected(false);
		}
		jpip.unSetZone();
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
		for (JCheckBoxMenuItem jcbmi : zoneMenuItems) {
		    if (jcbmi != jmi) {
			jcbmi.setSelected(false);
		    }
		}
		allZoneItem.setSelected(false);
		jpip.setZone(jproc.getZoneId(jmi.getText()));
	    } else {
		allZoneItem.setSelected(true);
		jpip.unSetZone();
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
	if (e.getSource() == allZoneItem) {
	    handleZone((JCheckBoxMenuItem) e.getSource());
	}
	if (zoneMenuItems.contains(e.getSource())) {
	    handleZone((JCheckBoxMenuItem) e.getSource());
	}
    }

    /**
     * Create a new UserInfo application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
	if (args.length == 0) {
	    new UserInfo();
	} else if (args.length == 2) {
	    if ("-s".equals(args[0])) {
		new UserInfo(
		    new PClientConfig(args[1], PClientConfig.CLIENT_XMLRPC));
	    } else if ("-S".equals(args[0])) {
		new UserInfo(
		    new PClientConfig(args[1], PClientConfig.CLIENT_REST));
	    } else {
		System.err.println("Usage: userinfo [-s|-S server]");
	    }
	} else {
	    System.err.println("Usage: userinfo [-s|-S server]");
	}
    }
}
