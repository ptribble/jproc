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
public final class PSinfoTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private String[] columnNames = { "COMMAND", "pid", "ppid", "USER", "GROUP",
		"nlwp", "SZ", "RSS", "START", "TIME", "TASK", "PROJ", "ZONE",
		"CT"};

    private List<Integer> colMap;

    private JProcessFilter jpf;
    private List<JProcess> vp;
    private List<JProcInfo> vpi;
    private JProc jproc;

    /**
     * Create a Table Model from the given Set of JProcesses.
     *
     * @param jproc  a JProc object
     * @param jpf  A JProcessFilter describing the list of processes to show
     */
    public PSinfoTableModel(JProc jproc, JProcessFilter jpf) {
	this.jpf = jpf;
	this.jproc = jproc;

	// initialize the column mapping list
	colMap = new ArrayList<>();
	for (int i = 0; i < columnNames.length; i++) {
	    colMap.add(i);
	}

	// initialize the data objects
	vp = new ArrayList<>();
	vpi = new ArrayList<>();
	for (JProcess p : jpf.getProcesses()) {
	    JProcInfo psi_new = jproc.getInfo(p);
	    if (psi_new != null) {
		vp.add(p);
		vpi.add(psi_new);
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
     * Only show processes belonging to this contract.
     *
     * @param ctid The desired contract id.
     */
    public void setContract(int ctid) {
	jpf.setContract(ctid);
	updateJprocess();
    }

    /**
     * Stop filtering by contract.
     */
    public void unSetContract() {
	jpf.unSetContract();
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
		vpi.add(new JProcInfo());
	    }
	    for (JProcess jp : jpf.getDeletedProcesses()) {
		int i = vp.indexOf(jp);
		if (i > -1) {
		    vp.remove(i);
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
	    JProcInfo psi_new = jproc.getInfo(jp);
	    if (psi_new == null) {
		ip.remove();
		vpi.remove(i);
	    } else {
		vpi.set(i, psi_new);
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
     * Get the JProcess corresponding to the given row.
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
	JProcInfo jpi = vpi.get(row);
	if (col == 0) {
	    return jpi.getfname();
	} else if (col == 3) {
	    return jproc.getUserName(jpi.getuid());
	} else if (col == 4) {
	    return jproc.getGroupName(jpi.getgid());
	} else if (col == 12) {
	    return jproc.getZoneName(jpi.getzoneid());
	} else if (col == 9) {
	    return Double.valueOf(jpi.gettime());
	}
	long l = 0;
	switch (col) {
	    case 1:
		l = jpi.getpid();
		break;
	    case 2:
		l = jpi.getppid();
		break;
	    case 5:
		l = jpi.getnlwp();
		break;
	    case 6:
		l = jpi.getsize();
		break;
	    case 7:
		l = jpi.getrssize();
		break;
	    case 8:
		l = jpi.getstime();
		break;
	    case 10:
		l = jpi.gettaskid();
		break;
	    case 11:
		l = jpi.getprojid();
		break;
	    case 13:
		l = jpi.getcontract();
		break;
	}
	return Long.valueOf(l);
    }

    @Override
    public Class<?> getColumnClass(int c) {
	switch (mapColumn(c)) {
	    case 0:
	    case 3:
	    case 4:
	    case 12:
		return String.class;
	    case 9:
		return Double.class;
	}
	return Long.class;
    }
}
