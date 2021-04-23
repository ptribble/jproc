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

package uk.co.petertribble.jproc.demo;

import uk.co.petertribble.jproc.api.*;
import uk.co.petertribble.jproc.client.*;
import uk.co.petertribble.jproc.gui.JPinfoTable;
import uk.co.petertribble.jproc.gui.JProcResources;
import java.util.Set;
import java.util.HashSet;
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
public class JPinfo extends JPdemo implements ActionListener {

    private JProc jproc;
    private JProcessSet jps;
    private JPinfoTable jpip;

    private JCheckBoxMenuItem allUserItem;
    private Set <JCheckBoxMenuItem> userSelectionItems;
    private JCheckBoxMenuItem allZoneItem;
    private Set <JCheckBoxMenuItem> zoneSelectionItems;
    private Set <JCheckBoxMenuItem> columnSelectionItems;

    private static final String VERSION = "JPinfo version 1.0";

    /**
     * Create a new JPinfo application.
     */
    public JPinfo() {
	this(new JProc(), true);
    }

    /**
     * Run the JPinfo demo.
     *
     * @param pcc a PClientConfig containing client configuration
     */
    public JPinfo(PClientConfig pcc) {
	this(new JProc(pcc), true);
    }

    /**
     * Create a new JPinfo application.
     *
     * @param jproc a JProc object to query for process information
     * @param standalone  a boolean, true if the demo is a standalone
     */
    public JPinfo(JProc jproc, boolean standalone) {
	super("JPinfo", standalone);
	this.jproc = jproc;

        // create main display panel
	jps = new JProcessSet(jproc);

        jpip = new JPinfoTable(jproc, new JProcessFilter(jps),
			DEFAULT_INTERVAL);

	JPanel mainPanel = new JPanel(new BorderLayout());
	mainPanel.add(new JScrollPane(jpip));
	setContentPane(mainPanel);

	addInfoPanel(mainPanel, VERSION);

	userSelectionItems = new HashSet <JCheckBoxMenuItem> ();
	addUserMenu();

	zoneSelectionItems = new HashSet <JCheckBoxMenuItem> ();
	if (jps.getZones().size() > 1) {
	    addZoneMenu();
	}

	columnSelectionItems = new HashSet <JCheckBoxMenuItem> ();

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
    private void addColumnMenu() {
	JMenu columnMenu = new JMenu(JProcResources.getString("COLUMN.MENU"));
	columnMenu.setMnemonic(KeyEvent.VK_C);
	Map <String, JCheckBoxMenuItem> columnMap =
	    new HashMap <String, JCheckBoxMenuItem> ();
	for (String s : jpip.columns()) {
	    JCheckBoxMenuItem jmi = new JCheckBoxMenuItem(s);
	    jmi.setSelected(true);
	    jmi.addActionListener(this);
	    columnMenu.add(jmi);
	    columnSelectionItems.add(jmi);
	    columnMap.put(s, jmi);
	}
	removeColumn(columnMap.get("CT"));
	removeColumn(columnMap.get("TASK"));
	removeColumn(columnMap.get("PROJ"));
	if (jps.getZones().size() == 1) {
	    removeColumn(columnMap.get("ZONE"));
	}
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

    private void handleZone(JCheckBoxMenuItem jmi) {
	boolean selected = jmi.isSelected();
	if (jmi == allZoneItem) {
	    if (selected) {
		for (JCheckBoxMenuItem jcbmi : zoneSelectionItems) {
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
		for (JCheckBoxMenuItem jcbmi : zoneSelectionItems) {
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

    private void handleColumn(JCheckBoxMenuItem jmi) {
	if (jmi.isSelected()) {
	    addColumn(jmi);
	} else {
	    removeColumn(jmi);
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
     * Create a new JPinfo application.
     *
     * @param args command line arguments, ignored
     */
    public static void main(String[] args) {
	if (args.length == 0) {
	    new JPinfo();
	} else if (args.length == 2) {
	    if ("-s".equals(args[0])) {
		new JPinfo(
		    new PClientConfig(args[1], PClientConfig.CLIENT_XMLRPC));
	    } else if ("-S".equals(args[0])) {
		new JPinfo(
		    new PClientConfig(args[1], PClientConfig.CLIENT_REST));
	    } else {
		System.err.println("Usage: info [-s|-S server]");
	    }
	} else {
	    System.err.println("Usage: info [-s|-S server]");
	}
    }
}
