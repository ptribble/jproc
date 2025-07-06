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
 * A TableModel to implement a user summary like prstat.
 *
 * @author Peter Tribble
 */
public final class UserInfoTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLNAMES = {"USER", "NPROC", "nlwp", "SZ",
				"RSS", "TIME"};

    private transient List<Integer> colMap;

    /**
     * The filter to select the processes to be displayed.
     */
    private JProcessFilter jpf;
    private transient List<JProcess> vp;
    private transient List<JProcInfo> vpi;
    /**
     * The JProc to query for data.
     */
    private JProc jproc;
    private transient List<Integer> userids;

    /**
     * Create a Table Model from the given Set of JProcesses.
     *
     * @param jproc a JProc object
     * @param jpf a JProcessFilter describing the list of processes to show
     */
    public UserInfoTableModel(JProc jproc, JProcessFilter jpf) {
	this.jpf = jpf;
	this.jproc = jproc;

	// initialize the column mapping list
	colMap = new ArrayList<>();
	for (int i = 0; i < COLNAMES.length; i++) {
	    colMap.add(i);
	}

	// initialize the data objects
	vp = new ArrayList<>();
	vpi = new ArrayList<>();
	for (JProcess p : jpf.getProcesses()) {
	    JProcInfo njpi = jproc.getInfo(p);
	    if (njpi != null) {
		vp.add(p);
		vpi.add(njpi);
	    }
	}

	userids = new ArrayList<>();
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
	Set<Integer> users = new TreeSet<>();
	while (ip.hasNext()) {
	    JProcess jp = ip.next();
	    /*
	     * The Jprocesses are updated here, so we can detect if they
	     * disappear. Most process removals will be handled above, but
	     * there's still a brief window.
	     */
	    JProcInfo njpi = jproc.getInfo(jp);
	    if (njpi == null) {
		ip.remove();
		vpi.remove(i);
	    } else {
		vpi.set(i, njpi);
		users.add(njpi.getuid());
		++i;
	    }
	}
	userids.clear();
	userids.addAll(users);
	fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
	return colMap.size();
    }

    @Override
    public int getRowCount() {
	return userids.size();
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
     * Get the userid corresponding to the given row.
     *
     * @param row the row requested
     *
     * @return the userid of the given row
     */
    public int getUser(int row) {
	return userids.get(row);
    }

    @Override
    public String getColumnName(int col) {
	return COLNAMES[mapColumn(col)];
    }

    /*
     * The following aggregate the values for a given user.
     *
     * This is potentially expensive, as we run through the entire list for
     * each user, *and* we do so whenever the table asks for it, rather than
     * computing it once every update cycle and then saving it. So may need to
     * be revised if performance is inadequate.
     */

    private long aggrNPROC(int uid) {
	long np = 0;
	for (JProcInfo jpi : vpi) {
	    if (jpi.getuid() == uid) {
		np++;
	    }
	}
	return np;
    }

    private long aggrNLWP(int uid) {
	long nlwp = 0;
	for (JProcInfo jpi : vpi) {
	    if (jpi.getuid() == uid) {
		nlwp += jpi.getnlwp();
	    }
	}
	return nlwp;
    }

    private long aggrSIZE(int uid) {
	long size = 0;
	for (JProcInfo jpi : vpi) {
	    if (jpi.getuid() == uid) {
		size += jpi.getsize();
	    }
	}
	return size;
    }

    private long aggrRSS(int uid) {
	long rss = 0;
	for (JProcInfo jpi : vpi) {
	    if (jpi.getuid() == uid) {
		rss += jpi.getrssize();
	    }
	}
	return rss;
    }

    private double aggrTIME(int uid) {
	double time = 0;
	for (JProcInfo jpi : vpi) {
	    if (jpi.getuid() == uid) {
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
	int uid = userids.get(row);
	if (col == 0) {
	    return jproc.getUserName(uid);
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
	int mcol = mapColumn(c);
	if (mcol == 0) {
	    return String.class;
	} else if (mcol == 5) {
	    return Double.class;
	} else {
	    return Long.class;
	}
    }
}
