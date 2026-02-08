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
 * Copyright 2026 Peter Tribble
 *
 */

package uk.co.petertribble.jproc.gui;

import javax.swing.table.AbstractTableModel;
import uk.co.petertribble.jproc.api.JProc;
import uk.co.petertribble.jproc.api.JProcess;
import uk.co.petertribble.jproc.api.JProcInfo;
import uk.co.petertribble.jproc.api.NoSuchProcessException;

/**
 * Display details of a process in a table.
 *
 * @author Peter Tribble
 */
public final class ProcessTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLNAMES
	= {"Property", "Value", "Change"};
    private static final String[] ROWNAMES
	= {"Threads", "Size", "RSS", "Time", "CTime"};

    /**
     * The JProcess to display.
     */
    private JProcess process;
    /**
     * The JProc to query for data.
     */
    private JProc jproc;

    /**
     * The number of lwps in a process.
     */
    private int nlwp;
    /**
     * The size of a process.
     */
    private long size;
    /**
     * The RSS of a process.
     */
    private long rssize;
    /**
     * The cpu time used by a process.
     */
    private double time;
    /**
     * The cpu time used by a process and reaped children.
     */
    private double ctime;
    /**
     * The change in number of lwps in a process.
     */
    private int dnlwp;
    /**
     * The change in size of a process.
     */
    private long dsize;
    /**
     * The change in RSS of a process.
     */
    private long drssize;
    /**
     * The change in cpu time used by a process.
     */
    private double dtime;
    /**
     * The change in cpu time used by a process and reaped children.
     */
    private double dctime;

    /**
     * Create a new ProcessTableModel.
     *
     * @param njproc a JProc object to query for process information
     * @param nprocess the JProcess to display
     */
    public ProcessTableModel(final JProc njproc, final JProcess nprocess) {
	jproc = njproc;
	process = nprocess;
    }

    /**
     * Update the details of the displayed process.
     *
     * @throws NoSuchProcessException if the process has exited
     */
    public void updateProcess() throws NoSuchProcessException {
	JProcInfo procinfo = jproc.getInfo(process);
	if (procinfo == null) {
	    throw new NoSuchProcessException();
	} else {
	    dnlwp = procinfo.getnlwp() - nlwp;
	    nlwp = procinfo.getnlwp();
	    dsize = procinfo.getsize() - size;
	    size = procinfo.getsize();
	    drssize = procinfo.getrssize() - rssize;
	    rssize = procinfo.getrssize();
	    dtime = procinfo.gettime() - time;
	    time = procinfo.gettime();
	    dctime = procinfo.getctime() - ctime;
	    ctime = procinfo.getctime();
	    fireTableDataChanged();
	}
    }

    @Override
    public int getColumnCount() {
	return COLNAMES.length;
    }

    @Override
    public int getRowCount() {
	return ROWNAMES.length;
    }

    @Override
    public String getColumnName(final int col) {
	return COLNAMES[col];
    }

    /**
     * Return the appropriate data.
     */
    @Override
    public Object getValueAt(final int row, final int col) {
	if (col == 0) {
	    return ROWNAMES[row];
	} else if (col == 2) {
	    if (row == 0) {
		return dnlwp;
	    } else if (row == 1) {
		return dsize;
	    } else if (row == 2) {
		return drssize;
	    } else if (row == 3) {
		return dtime;
	    } else if (row == 4) {
		return dctime;
	    }
	} else {
	    if (row == 0) {
		return nlwp;
	    } else if (row == 1) {
		return size;
	    } else if (row == 2) {
		return rssize;
	    } else if (row == 3) {
		return time;
	    } else if (row == 4) {
		return ctime;
	    }
	}
	return null;
    }

    @Override
    public Class<?> getColumnClass(final int c) {
	return getValueAt(0, c).getClass();
    }
}
