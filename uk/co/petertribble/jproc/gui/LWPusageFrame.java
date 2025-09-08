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

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import uk.co.petertribble.jproc.api.JProc;
import uk.co.petertribble.jproc.api.JProcess;

/**
 * A frame showing thread information for a process.
 *
 * @author Peter Tribble
 */
public final class LWPusageFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    /**
     * The table embedded in this frame.
     */
    LWPusageTable lup;

    /**
     * Construct a new LWPusageFrame.
     *
     * @param jproc a JProc object to query for process information
     * @param jp The JProcess to display
     * @param interval the display update interval, in seconds
     */
    public LWPusageFrame(final JProc jproc, final JProcess jp,
			 final int interval) {

	setTitle(JProcResources.getString("THREAD.TITLE") + " " + jp.getPid());
	setLayout(new BorderLayout());

	addWindowListener(new WindowExit());
	setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	lup = new LWPusageTable(jproc, jp, interval);

	setContentPane(new JScrollPane(lup));

	setSize(620, 400);
	validate();
	setVisible(true);
    }

    /**
     * On closure, stop the table updating.
     */
    class WindowExit extends WindowAdapter {
	@Override
	public void windowClosing(final WindowEvent we) {
	    lup.stopLoop();
	}
    }
}
