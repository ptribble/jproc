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
public class LWPusageFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    LWPusageTable lup;

    /**
     * Construct a new LWPusageFrame.
     *
     * @param jproc a JProc object to query for process information
     * @param jp The JProcess to display
     * @param interval the display update interval, in seconds
     */
    public LWPusageFrame(JProc jproc, JProcess jp, int interval) {

	setTitle(JProcResources.getString("THREAD.TITLE") + " " + jp.getPid());
	setLayout(new BorderLayout());

	addWindowListener(new winExit());
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
    class winExit extends WindowAdapter {
	@Override
	public void windowClosing(WindowEvent we) {
	    lup.stopLoop();
	}
    }
}
