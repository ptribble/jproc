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

import uk.co.petertribble.jproc.api.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Represents a process in a tree.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public class ProcessTreeNode extends DefaultMutableTreeNode {

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
