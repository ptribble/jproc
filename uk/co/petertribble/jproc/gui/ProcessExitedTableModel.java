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
    public String getColumnName(final int col) {
	return "Status";
    }

    /**
     * Return the appropriate data.
     */
    @Override
    public Object getValueAt(final int row, final int col) {
	return "Process has exited";
    }

    @Override
    public Class<?> getColumnClass(final int c) {
	return String.class;
    }
}
