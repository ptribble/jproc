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

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;
import javax.swing.table.*;
import uk.co.petertribble.jproc.api.*;

/**
 * A TableModel to show usage data for the threads in a process.
 *
 * @author Peter Tribble
 */
public final class LWPusageTableModel extends AbstractTableModel {

    /*
     * The columns we can show.
     */
    private String[] columnNames = { "LWP", "RTIME", "UTIME", "STIME", "MINF",
		"MAJF", "NSWAP", "INBLK", "OUBLK", "MSND", "MRCV", "SIGS",
		"VCTX", "ICTX", "SYSC", "IOCH"};

    private List <Integer> colMap;

    private JProc jproc;
    private JProcess jp;
    private List <JLwp> vp;
    private List <JProcUsage> vpu;

    /**
     * Create a Table Model for the given process.
     *
     * @param jproc a JProc object to query for process information
     * @param jp a JProcess to display
     */
    public LWPusageTableModel(JProc jproc, JProcess jp) {
	this.jproc = jproc;
	this.jp = jp;

	// initialize the column mapping list
	colMap = new ArrayList<>();
	for (int i = 0; i < columnNames.length; i++) {
	    colMap.add(i);
	}

	// initialize the data objects
	vp = new ArrayList<>();
	vpu = new ArrayList<>();
	for (JLwp p : jproc.getLwps(jp)) {
	    JProcUsage jpu = jproc.getUsage(p);
	    if (jpu != null) {
		vp.add(p);
		vpu.add(jpu);
	    }
	}
    }

    /**
     * Update the statistics. Add new threads. Iterates through the current
     * list updating each one. If a JLwp disappears, it is removed.
     *
     * @throws NoSuchProcessException if the process exits.
     */
    public void updateJprocess() throws NoSuchProcessException {
	// update the thread list
	Set <JLwp> nlwp = jproc.getLwps(jp);
	/*
	 * If the process exits, nlwp will be null.
	 */
	if (nlwp == null) {
	    throw new NoSuchProcessException();
	}
	List <JLwp> nvp = new ArrayList<>(nlwp);
	// ignore existing threads
	nvp.removeAll(vp);
	// add any new threads - removes handled below
	for (JLwp jlwp : nvp) {
	    vp.add(jlwp);
	    vpu.add(new JProcUsage());
	}

	Iterator <JLwp> ip = vp.iterator();
	int i = 0;
	while (ip.hasNext()) {
	    JLwp jlwp = ip.next();
	    /*
	     * The JLwps are updated here, so we can detect if they exit.
	     */
	    JProcUsage psu_new = jproc.getUsage(jlwp);
	    if (psu_new == null) {
		ip.remove();
		vpu.remove(i);
	    } else {
		vpu.set(i, psu_new);
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
    public List <String> columns() {
	return Arrays.asList(columnNames);
    }

    /**
     * Remove a title from the list of items to be displayed.
     *
     * @param s the name of the column to be removed
     */
    public void removeColumn(String s) {
	Iterator <Integer> iter = colMap.iterator();
	while (iter.hasNext()) {
	    if (columnNames[iter.next()].equals(s)) {
		iter.remove();
	    }
	}
	fireTableStructureChanged();
    }

    /**
     * Add a title to the list of items to be displayed.
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
	Iterator <Integer> iter = colMap.iterator();
	while (iter.hasNext()) {
	    if (iter.next() < icol) {
		imap++;
	    }
	}
	colMap.add(imap, icol);
	fireTableStructureChanged();
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
	if (col == 0) {
	    return Integer.valueOf(vp.get(row).getLWPid());
	} else if (col == 1) {
	    return Double.valueOf(jpu.getrtime());
	} else if (col == 2) {
	    return Double.valueOf(jpu.getutime());
	} else if (col == 3) {
	    return Double.valueOf(jpu.getstime());
	}
	long l = 0;
	switch (col) {
	    case 4:
		l = jpu.getminf();
		break;
	    case 5:
		l = jpu.getmajf();
		break;
	    case 6:
		l = jpu.getnswap();
		break;
	    case 7:
		l = jpu.getinblk();
		break;
	    case 8:
		l = jpu.getoublk();
		break;
	    case 9:
		l = jpu.getmsnd();
		break;
	    case 10:
		l = jpu.getmrcv();
		break;
	    case 11:
		l = jpu.getsigs();
		break;
	    case 12:
		l = jpu.getvctx();
		break;
	    case 13:
		l = jpu.getictx();
		break;
	    case 14:
		l = jpu.getsysc();
		break;
	    case 15:
		l = jpu.getioch();
		break;
	}
	return Long.valueOf(l);
    }

    @Override
    public Class<?> getColumnClass(int c) {
	switch (mapColumn(c)) {
	    case 0:
		return Integer.class;
	    case 1:
	    case 2:
	    case 3:
		return Double.class;
	}
	return Long.class;
    }
}
