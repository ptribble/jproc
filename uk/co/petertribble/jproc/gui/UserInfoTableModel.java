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

    private String[] columnNames = { "USER", "NPROC", "nlwp", "SZ", "RSS",
				"TIME"};

    private List <Integer> colMap;

    private JProcessFilter jpf;
    private List <JProcess> vp;
    private List <JProcInfo> vpi;
    private JProc jproc;
    private List <Integer> userids;

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
	Iterator <JProcess> ip = vp.iterator();
	int i = 0;
	Set <Integer> users = new TreeSet<>();
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
		users.add(psi_new.getuid());
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
	return columnNames[mapColumn(col)];
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
	switch (mapColumn(c)) {
	    case 0:
		return String.class;
	    case 5:
		return Double.class;
	}
	return Long.class;
    }
}
