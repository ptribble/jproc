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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;
import javax.swing.table.*;
import uk.co.petertribble.jproc.api.*;

/**
 * A TableModel to implement prstat.
 *
 * @author Peter Tribble
 */
public final class PSusageTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private String[] columnNames = {"COMMAND", "pid", "USER", "nlwp",
		"RTIME", "UTIME", "STIME", "MINF", "MAJF", "NSWAP", "INBLK",
		"OUBLK", "MSND", "MRCV", "SIGS", "VCTX", "ICTX", "SYSC",
		"IOCH"};

    private List<Integer> colMap;

    private JProcessFilter jpf;
    private List<JProcess> vp;
    private List<JProcUsage> vpu;
    private List<JProcInfo> vpi;
    private JProc jproc;

    /**
     * Create a Table Model from the given Set of JProcesses.
     *
     * @param jproc a JProc object to query for process information
     * @param jpf a JProcessFilter describing the list of processes to show
     */
    public PSusageTableModel(JProc jproc, JProcessFilter jpf) {
	this.jpf = jpf;
	this.jproc = jproc;

	// initialize the column mapping list
	colMap = new ArrayList<>();
	for (int i = 0; i < columnNames.length; i++) {
	    colMap.add(i);
	}

	// initialize the data objects
	vp = new ArrayList<>();
	vpu = new ArrayList<>();
	vpi = new ArrayList<>();
	for (JProcess p : jpf.getProcesses()) {
	    JProcUsage njpu = jproc.getUsage(p);
	    JProcInfo njpi = jproc.getInfo(p);
	    if (njpu != null && njpi != null) {
		vp.add(p);
		vpu.add(njpu);
		vpi.add(njpi);
	    }
	}
    }

    /**
     * Only show processes belonging to this user.
     *
     * @param uid The user id to show.
     */
    public void setUser(int uid) {
	jpf.setUser(uid);
	updateJprocess();
    }

    /**
     * Unset user filtering, so that all users are shown.
     */
    public void unSetUser() {
	jpf.unSetUser();
	updateJprocess();
    }

    /**
     * Only show processes belonging to this zone.
     *
     * @param uid The zone id to show.
     */
    public void setZone(int uid) {
	jpf.setZone(uid);
	updateJprocess();
    }

    /**
     * Unset zone filtering, so that all zones are shown.
     */
    public void unSetZone() {
	jpf.unSetZone();
	updateJprocess();
    }

    /**
     * Update the statistics. Iterates through the current list
     * updating each one. If a JProcess disappears, it is removed.
     */
    public void updateJprocess() {
	// update the underlying process list
	if (jpf.update()) {
	    for (JProcess jp : jpf.getAddedProcesses()) {
		vp.add(jp);
		// just populate the slot; data actually read below
		vpu.add(new JProcUsage());
		vpi.add(new JProcInfo());
	    }
	    for (JProcess jp : jpf.getDeletedProcesses()) {
		int i = vp.indexOf(jp);
		if (i > -1) {
		    vp.remove(i);
		    vpu.remove(i);
		    vpi.remove(i);
		}
	    }
	}
	Iterator<JProcess> ip = vp.iterator();
	int i = 0;
	while (ip.hasNext()) {
	    JProcess jp = ip.next();
	    /*
	     * The Jprocesses are updated here, so we can detect if they
	     * disappear. Most process removals will be handled above, but
	     * there's still a brief window.
	     */
	    JProcUsage njpu = jproc.getUsage(jp);
	    JProcInfo njpi = jproc.getInfo(jp);
	    if (njpu == null || njpi == null) {
		ip.remove();
		vpu.remove(i);
		vpi.remove(i);
	    } else {
		vpu.set(i, njpu);
		vpi.set(i, njpi);
		++i;
	    }
	}
	fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
	return colMap.size();
    }

    @Override
    public int getRowCount() {
	return vp.size();
    }

    /*
     * Map the visible column into the underlying data column. This allows us
     * to display a subset of the columns, or change the order, without
     * changing the subsequent methods.
     */
    private int mapColumn(int col) {
	return colMap.get(col);
    }

    /**
     * Return the list of column names.
     *
     * @return a List of column names
     */
    public List<String> columns() {
	return Arrays.asList(columnNames);
    }

    /**
     * Remove a column from the list of items to be displayed.
     *
     * @param s the name of the column to be removed
     */
    public void removeColumn(String s) {
	Iterator<Integer> iter = colMap.iterator();
	while (iter.hasNext()) {
	    if (columnNames[iter.next()].equals(s)) {
		iter.remove();
	    }
	}
	fireTableStructureChanged();
    }

    /**
     * Add a column to the list of items to be displayed.
     *
     * @param s the name of the column to be added
     */
    public void addColumn(String s) {
	int icol = 0;
	for (int i = 0; i < columnNames.length; i++) {
	    if (columnNames[i].equals(s)) {
		icol = i;
	    }
	}
	int imap = 0;
	Iterator<Integer> iter = colMap.iterator();
	while (iter.hasNext()) {
	    if (iter.next() < icol) {
		imap++;
	    }
	}
	colMap.add(imap, icol);
	fireTableStructureChanged();
    }

    /**
     * Get the process corresponding to the given row.
     *
     * @param row the row requested
     *
     * @return the JProcess at the given row
     */
    public JProcess getProcess(int row) {
	return vp.get(row);
    }

    @Override
    public String getColumnName(int col) {
	return columnNames[mapColumn(col)];
    }

    /**
     * Return the appropriate data.
     */
    @Override
    public Object getValueAt(int row, int icol) {
	int col = mapColumn(icol);
	JProcUsage jpu = vpu.get(row);
	JProcInfo jpi = vpi.get(row);
	if (col == 0) {
	    return jpi.getfname();
	} else if (col == 2) {
	    return jproc.getUserName(jpi.getuid());
	} else if (col == 4) {
	    return Double.valueOf(jpu.getrtime());
	} else if (col == 5) {
	    return Double.valueOf(jpu.getutime());
	} else if (col == 6) {
	    return Double.valueOf(jpu.getstime());
	}
	long l = 0;
	switch (col) {
	    case 1:
		l = vp.get(row).getPid();
		break;
	    case 3:
		l = jpi.getnlwp();
		break;
	    case 7:
		l = jpu.getminf();
		break;
	    case 8:
		l = jpu.getmajf();
		break;
	    case 9:
		l = jpu.getnswap();
		break;
	    case 10:
		l = jpu.getinblk();
		break;
	    case 11:
		l = jpu.getoublk();
		break;
	    case 12:
		l = jpu.getmsnd();
		break;
	    case 13:
		l = jpu.getmrcv();
		break;
	    case 14:
		l = jpu.getsigs();
		break;
	    case 15:
		l = jpu.getvctx();
		break;
	    case 16:
		l = jpu.getictx();
		break;
	    case 17:
		l = jpu.getsysc();
		break;
	    case 18:
		l = jpu.getioch();
		break;
	}
	return Long.valueOf(l);
    }

    @Override
    public Class<?> getColumnClass(int c) {
	switch (mapColumn(c)) {
	    case 0:
	    case 2:
		return String.class;
	    case 4:
	    case 5:
	    case 6:
		return Double.class;
	}
	return Long.class;
    }
}
