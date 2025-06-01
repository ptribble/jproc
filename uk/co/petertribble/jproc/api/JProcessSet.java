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

package uk.co.petertribble.jproc.api;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;

/**
 * A class for representing a Set of Solaris processes and obtaining
 * information on them.
 *
 * @author Peter Tribble
 */
public class JProcessSet implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The JProc to query for data.
     */
    private JProc jproc;

    private transient Set<JProcess> addedProcesses;
    private transient Set<JProcess> deletedProcesses;
    private transient Set<JProcess> currentProcesses;

    /**
     * Creates a JProcessSet that includes all processes.
     */
    public JProcessSet() {
	this(new JProc());
    }

    /**
     * Creates a JProcessSet that includes all processes.
     *
     * @param jproc a JProc object.
     */
    public JProcessSet(JProc jproc) {
	this.jproc = jproc;
	currentProcesses = jproc.getProcesses();
	addedProcesses = new HashSet<>();
	deletedProcesses = new HashSet<>();
    }

    /**
     * Update the list of processes. Creates lists of added and deleted
     * processes since the previous update.
     *
     * @return true if the process list has changed, otherwise false
     */
    public boolean update() {
	Set<JProcess> newProcesses = jproc.getProcesses();
	addedProcesses = new HashSet<>(newProcesses);
	addedProcesses.removeAll(currentProcesses);
	deletedProcesses = new HashSet<>(currentProcesses);
	deletedProcesses.removeAll(newProcesses);
	currentProcesses.addAll(addedProcesses);
	currentProcesses.removeAll(deletedProcesses);
	return !(addedProcesses.isEmpty() && deletedProcesses.isEmpty());
    }

    /**
     * Returns the processes added in the last update.
     *
     * @return The Set of JProcesses added in the last update.
     */
    public Set<JProcess> getAddedProcesses() {
	return addedProcesses;
    }

    /**
     * Returns the processes deleted in the last update.
     *
     * @return The Set of JProcesses deleted in the last update.
     */
    public Set<JProcess> getDeletedProcesses() {
	return deletedProcesses;
    }

    /**
     * Returns the current processes.
     *
     * @return The current Set of JProcesses managed by this JProcessSet.
     */
    public Set<JProcess> getProcesses() {
	return currentProcesses;
    }

    /**
     * Returns the current users.
     *
     * @return The Set of current users.
     */
    public Set<String> getUsers() {
	return getUsers(getProcesses());
    }

    /**
     * Returns the current users.
     *
     * @param processes  A Set of processes.
     *
     * @return The Set of current users.
     */
    public Set<String> getUsers(Set<JProcess> processes) {
	Set<String> users = new HashSet<>();
	for (JProcess jp : processes) {
	    users.add(jproc.getUserName(jp.getCachedInfo().getuid()));
	}
	return users;
    }

    /**
     * Returns the current zones.
     *
     * @return The Set of current zones.
     */
    public Set<String> getZones() {
	return getZones(getProcesses());
    }

    /**
     * Returns the current zones.
     *
     * @param processes  A Set of processes.
     *
     * @return The Set of current zones.
     */
    public Set<String> getZones(Set<JProcess> processes) {
	Set<String> zones = new HashSet<>();
	for (JProcess jp : processes) {
	    zones.add(jproc.getZoneName(jp.getCachedInfo().getzoneid()));
	}
	return zones;
    }

    /**
     * Returns the current tasks.
     *
     * @return The Set of current tasks.
     */
    public Set<Integer> getTasks() {
	return getTasks(getProcesses());
    }

    /**
     * Returns the current tasks.
     *
     * @param processes  A Set of processes.
     *
     * @return The Set of current task ids.
     */
    public Set<Integer> getTasks(Set<JProcess> processes) {
	Set<Integer> tasks = new HashSet<>();
	for (JProcess jp : processes) {
	    tasks.add(jp.getCachedInfo().gettaskid());
	}
	return tasks;
    }

    /**
     * Returns the current projects.
     *
     * @return The Set of current tasks.
     */
    public Set<Integer> getProjects() {
	return getProjects(getProcesses());
    }

    /**
     * Returns the current projects.
     *
     * @param processes  A Set of processes.
     *
     * @return The Set of current project ids.
     */
    public Set<Integer> getProjects(Set<JProcess> processes) {
	Set<Integer> projects = new HashSet<>();
	for (JProcess jp : processes) {
	    projects.add(jp.getCachedInfo().getprojid());
	}
	return projects;
    }
}
