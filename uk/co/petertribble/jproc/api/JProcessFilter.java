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

package uk.co.petertribble.jproc.api;

import java.util.Set;
import java.util.HashSet;

/**
 * A class for filtering Solaris processes. Allows filtering by zone, user, and
 * process contract.
 *
 * @author Peter Tribble
 */
public class JProcessFilter {

    private JProcessSet jps;

    private Set <JProcess> addedProcesses;
    private Set <JProcess> deletedProcesses;
    private Set <JProcess> newProcesses;
    private Set <JProcess> currentProcesses;

    private int szone = -1;
    private int suid = -1;
    private int spid = -1;
    private int sctid = -1;
    private int staskid = -1;
    private int sprojid = -1;

    /**
     * Creates a JProcessFilter.
     */
    public JProcessFilter() {
	this(new JProcessSet(new JProc()));
    }

    /**
     * Creates a JProcessFilter.
     *
     * @param jps A JProcessSet.
     */
    public JProcessFilter(JProcessSet jps) {
	this.jps = jps;
	currentProcesses = jps.getProcesses();
	addedProcesses = new HashSet <JProcess> ();
	deletedProcesses = new HashSet <JProcess> ();
    }

    /**
     * Creates a JProcessFilter representing a single process. This allows
     * other classes to treat a single process the same way as they would
     * a filtered set of processes.
     *
     * @param jp A JProcess.
     */
    public JProcessFilter(JProcess jp) {
	this.jps = jps;
	currentProcesses = new HashSet <JProcess> ();
	currentProcesses.add(jp);
	spid = 1;
	addedProcesses = new HashSet <JProcess> ();
	deletedProcesses = new HashSet <JProcess> ();
    }

    /**
     * Filter by zone. If set, only return processes in this zone.
     *
     * @param zoneid The zoneid of the zone.
     */
    public void setZone(int zoneid) {
	szone = zoneid;
    }

    /**
     * Stop filtering by zone.
     */
    public void unSetZone() {
	szone = -1;
    }

    /**
     * Filter by contract. If set, only return processes belonging to this
     * contract.
     *
     * @param ctid The desired contract id.
     */
    public void setContract(int ctid) {
	sctid = ctid;
    }

    /**
     * Stop filtering by contract.
     */
    public void unSetContract() {
	sctid = -1;
    }

    /**
     * Filter by project. If set, only return processes belonging to this
     * project.
     *
     * @param projid The desired project id.
     */
    public void setProject(int projid) {
	sprojid = projid;
    }

    /**
     * Stop filtering by project.
     */
    public void unSetProject() {
	sprojid = -1;
    }

    /**
     * Filter by task. If set, only return processes belonging to this
     * task.
     *
     * @param taskid The desired task id.
     */
    public void setTask(int taskid) {
	staskid = taskid;
    }

    /**
     * Stop filtering by task.
     */
    public void unSetTask() {
	staskid = -1;
    }

    /**
     * Filter by user. If set, only return processes owned by this userid.
     *
     * @param userid The userid.
     */
    public void setUser(int userid) {
	suid = userid;
    }

    /**
     * Stop filtering by user.
     */
    public void unSetUser() {
	suid = -1;
    }

    /**
     * Returns the processes added in the last update.
     *
     * @return The Set of JProcesses added in the last update.
     */
    public Set <JProcess> getAddedProcesses() {
	return addedProcesses;
    }

    /**
     * Returns the processes deleted in the last update.
     *
     * @return The Set of JProcesses deleted in the last update.
     */
    public Set <JProcess> getDeletedProcesses() {
	return deletedProcesses;
    }

    /**
     * Returns the current processes.
     *
     * @return The current Set of JProcesses managed by this JProcessSet.
     */
    public Set <JProcess> getProcesses() {
	return currentProcesses;
    }

    /**
     * Update the list of processes. Creates lists of added and deleted
     * processes since the previous update.
     *
     * @return true if the process list has changed, otherwise false
     */
    public boolean update() {
	if (spid < 0) {
	    Set <JProcess> matchProcesses = new HashSet <JProcess> ();
	    jps.update();
	    newProcesses = jps.getProcesses();
	    for (JProcess jp : newProcesses) {
		if (matchFilter(jp)) {
		    matchProcesses.add(jp);
		}
	    }
	    addedProcesses = new HashSet <JProcess> (matchProcesses);
	    addedProcesses.removeAll(currentProcesses);
	    deletedProcesses = new HashSet <JProcess> (currentProcesses);
	    deletedProcesses.removeAll(matchProcesses);
	    currentProcesses = matchProcesses;
	}
	return !(addedProcesses.isEmpty() && deletedProcesses.isEmpty());
    }

    /*
     * Match a JProcess against the filter. Uses the cached JProcInfo
     * to avoid unnecessary excursions into native code.
     */
    private boolean matchFilter(JProcess jp) {
	JProcInfo jpi = jp.getCachedInfo();
	// if null, process doesn't exist, so we definitely don't want it
	if (jpi == null) {
	    return false;
	}
	if (suid > -1 && jpi.getuid() != suid) {
	    return false;
	}
	if (szone > -1 && jpi.getzoneid() != szone) {
	    return false;
	}
	if (sctid > -1 && jpi.getcontract() != sctid) {
	    return false;
	}
	if (staskid > -1 && jpi.gettaskid() != staskid) {
	    return false;
	}
	if (sprojid > -1 && jpi.getprojid() != sprojid) {
	    return false;
	}
	return true;
    }
}
