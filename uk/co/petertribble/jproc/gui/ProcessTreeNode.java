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
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Represents a process in a tree.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public class ProcessTreeNode extends DefaultMutableTreeNode {

    private static final long serialVersionUID = 1L;

    /**
     * Construct a new ProcessTreeNode.
     *
     * @param process the JProcess to be represented by this node
     */
    public ProcessTreeNode(JProcess process) {
	super(process);
    }

    /**
     * Return the JProcess object underlying this ProcessTreeNode.
     *
     * @return the underlying JProcess
     */
    public JProcess getProcess() {
	return (JProcess) getUserObject();
    }

    @Override
    public String toString() {
	JProcess p = (JProcess) getUserObject();
	return Integer.toString(p.getPid()) + " "
		+ p.getCachedInfo().getfname();
    }
}
