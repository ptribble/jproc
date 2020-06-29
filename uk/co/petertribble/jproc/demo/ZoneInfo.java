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
import uk.co.petertribble.jproc.gui.ZoneInfoTable;
import uk.co.petertribble.jproc.gui.JProcResources;
import java.util.Set;
import java.util.HashSet;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * A process view rather like top, displaying usage aggregated by zone.
 *
 * @author Peter Tribble
 */
public class ZoneInfo extends JPdemo implements ActionListener {

    private JProc jproc;
    private ZoneInfoTable jpip;

    private JCheckBoxMenuItem allUserItem;
    private Set <JCheckBoxMenuItem> userSelectionItems;

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

	userSelectionItems = new HashSet <JCheckBoxMenuItem> ();
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
	    if (args[0].equals("-s")) {
		new ZoneInfo(
		    new PClientConfig(args[1], PClientConfig.CLIENT_XMLRPC));
	    } else if (args[0].equals("-S")) {
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
