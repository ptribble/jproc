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
import uk.co.petertribble.jproc.api.*;

/**
 * Display details of a process in a table.
 *
 * @author Peter Tribble
 */
public final class ProcessTableModel extends AbstractTableModel {

    private String[] columnNames = { "Property", "Value", "Change" };

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
