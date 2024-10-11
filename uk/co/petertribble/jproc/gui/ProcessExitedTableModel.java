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

import javax.swing.table.*;

/**
 * This is a special Model that informs the user that the process they're
 * interested in no longer exists. The expected usage is that a Table
 * detects that the process being displayed exits, stops updating its custom
 * Model, and sets its Model to be a new instance of this static Model.
 *
 * @author Peter Tribble
 */
public final class ProcessExitedTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    @Override
    public int getColumnCount() {
	return 1;
    }

    @Override
    public int getRowCount() {
	return 1;
    }

    @Override
    public String getColumnName(int col) {
	return "Status";
    }

    /**
     * Return the appropriate data.
     */
    @Override
    public Object getValueAt(int row, int col) {
	return "Process has exited";
    }

    @Override
    public Class<?> getColumnClass(int c) {
	return String.class;
    }
}
