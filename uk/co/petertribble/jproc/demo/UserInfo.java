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
import uk.co.petertribble.jproc.gui.UserInfoTable;
import uk.co.petertribble.jproc.gui.JProcResources;
import java.util.Set;
import java.util.HashSet;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * A process view rather like top, displaying usage aggregated by user.
 *
 * @author Peter Tribble
 */
public class UserInfo extends JPdemo implements ActionListener {

    private JProc jproc;
    private UserInfoTable jpip;

    private JCheckBoxMenuItem allZoneItem;
    private Set <JCheckBoxMenuItem> zoneSelectionItems;

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
	JProcessFilter jpf = new JProcessFilter(jps);

        jpip = new UserInfoTable(jproc, jpf, DEFAULT_INTERVAL);

	JPanel mainPanel = new JPanel(new BorderLayout());
	mainPanel.add(new JScrollPane(jpip));
	setContentPane(mainPanel);

	addInfoPanel(mainPanel, VERSION);

	zoneSelectionItems = new HashSet <JCheckBoxMenuItem> ();
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
	    zoneSelectionItems.add(jmi);
	}
	jm.add(zoneMenu);
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
	if (zoneSelectionItems.contains(e.getSource())) {
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
	    if (args[0].equals("-s")) {
		new UserInfo(
		    new PClientConfig(args[1], PClientConfig.CLIENT_XMLRPC));
	    } else if (args[0].equals("-S")) {
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
