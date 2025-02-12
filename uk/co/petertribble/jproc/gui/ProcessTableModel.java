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
import uk.co.petertribble.jproc.api.*;

/**
 * Display details of a process in a table.
 *
 * @author Peter Tribble
 */
public final class ProcessTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private String[] columnNames = {"Property", "Value", "Change"};

    private JProcess process;
    private JProc jproc;

    private String[] rowNames = {"Threads", "Size", "RSS", "Time", "CTime"};
    private int nlwp;
    private long size;
    private long rssize;
    private double time;
    private double ctime;
    private int dnlwp;
    private long dsize;
    private long drssize;
    private double dtime;
    private double dctime;

    /**
     * Create a new ProcessTableModel.
     *
     * @param jproc  a JProc object to query for process information
     * @param process the JProcess to display
     */
    public ProcessTableModel(JProc jproc, JProcess process) {
	this.jproc = jproc;
	this.process = process;
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
	return columnNames.length;
    }

    @Override
    public int getRowCount() {
	return rowNames.length;
    }

    @Override
    public String getColumnName(int col) {
	return columnNames[col];
    }

    /**
     * Return the appropriate data.
     */
    @Override
    public Object getValueAt(int row, int col) {
	if (col == 0) {
	    return rowNames[row];
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
    public Class<?> getColumnClass(int c) {
	return getValueAt(0, c).getClass();
    }
}
