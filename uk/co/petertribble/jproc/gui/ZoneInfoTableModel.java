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
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;
import javax.swing.table.*;
import uk.co.petertribble.jproc.api.*;

/**
 * A TableModel to implement a zone summary like prstat.
 *
 * @author Peter Tribble
 */
public final class ZoneInfoTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private String[] columnNames = {"ZONE", "NPROC", "nlwp", "SZ", "RSS",
				"TIME"};

    private List<Integer> colMap;

    private JProcessFilter jpf;
    private List<JProcess> vp;
    private List<JProcInfo> vpi;
    private JProc jproc;
    private List<Integer> zoneids;

    /**
     * Create a Table Model from the given Set of JProcesses.
     *
     * @param jproc a JProc object
     * @param jpf a JProcessFilter describing the list of processes to show
     */
    public ZoneInfoTableModel(JProc jproc, JProcessFilter jpf) {
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
	    JProcInfo jpi = jproc.getInfo(p);
	    if (jpi != null) {
		vp.add(p);
		vpi.add(jpi);
	    }
	}

	zoneids = new ArrayList<>();
    }

    /**
     * Only show processes belonging to this user.
     *
     * @param uid The userid to show.
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
	Set<Integer> zones = new TreeSet<>();
	while (ip.hasNext()) {
	    JProcess jp = ip.next();
	    /*
	     * The Jprocesses are updated here, so we can detect if they
	     * disappear. Most process removals will be handled above, but
	     * there's still a brief window.
	     */
	    JProcInfo jpi = jproc.getInfo(jp);
	    if (jpi == null) {
		ip.remove();
		vpi.remove(i);
	    } else {
		vpi.set(i, jpi);
		zones.add(jpi.getzoneid());
		++i;
	    }
	}
	zoneids.clear();
	zoneids.addAll(zones);
	fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
	return colMap.size();
    }

    @Override
    public int getRowCount() {
	return zoneids.size();
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
     * Get the zoneid corresponding to the given row.
     *
     * @param row the row requested
     *
     * @return the zoneid of the given row
     */
    public int getZone(int row) {
	return zoneids.get(row);
    }

    @Override
    public String getColumnName(int col) {
	return columnNames[mapColumn(col)];
    }

    /*
     * The following aggregate the values for a given zone.
     *
     * This is potentially expensive, as we run through the entire list for
     * each zone, *and* we do so whenever the table asks for it, rather than
     * computing it once every update cycle and then saving it. So may need to
     * be revised if performance is inadequate.
     */

    private long aggrNPROC(int uid) {
	long np = 0;
	for (JProcInfo jpi : vpi) {
	    if (jpi.getzoneid() == uid) {
		np++;
	    }
	}
	return np;
    }

    private long aggrNLWP(int uid) {
	long nlwp = 0;
	for (JProcInfo jpi : vpi) {
	    if (jpi.getzoneid() == uid) {
		nlwp += jpi.getnlwp();
	    }
	}
	return nlwp;
    }

    private long aggrSIZE(int uid) {
	long size = 0;
	for (JProcInfo jpi : vpi) {
	    if (jpi.getzoneid() == uid) {
		size += jpi.getsize();
	    }
	}
	return size;
    }

    private long aggrRSS(int uid) {
	long rss = 0;
	for (JProcInfo jpi : vpi) {
	    if (jpi.getzoneid() == uid) {
		rss += jpi.getrssize();
	    }
	}
	return rss;
    }

    private double aggrTIME(int uid) {
	double time = 0;
	for (JProcInfo jpi : vpi) {
	    if (jpi.getzoneid() == uid) {
		time += jpi.gettime();
	    }
	}
	return time;
    }

    /**
     * Return the appropriate data.
     */
    @Override
    public Object getValueAt(int row, int icol) {
	int col = mapColumn(icol);
	int uid = zoneids.get(row);
	if (col == 0) {
	    return jproc.getZoneName(uid);
	} else if (col == 5) {
	    return Double.valueOf(aggrTIME(uid));
	}
	long l = 0;
	switch (col) {
	    case 1:
		l = aggrNPROC(uid);
		break;
	    case 2:
		l = aggrNLWP(uid);
		break;
	    case 3:
		l = aggrSIZE(uid);
		break;
	    case 4:
		l = aggrRSS(uid);
		break;
	}
	return Long.valueOf(l);
    }

    @Override
    public Class<?> getColumnClass(int c) {
	switch (mapColumn(c)) {
	    case 0:
		return String.class;
	    case 5:
		return Double.class;
	}
	return Long.class;
    }
}
