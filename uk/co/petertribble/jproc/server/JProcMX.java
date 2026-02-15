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
 * Copyright 2026 Peter Tribble
 *
 */

package uk.co.petertribble.jproc.server;

import uk.co.petertribble.jproc.api.JProc;
import uk.co.petertribble.jproc.api.JProcess;
import java.util.Set;

/**
 * The actual implementation of the MBean exposing JProc data over JMX.
 *
 * @author Peter Tribble
 */
public final class JProcMX implements JProcMXMBean {

    private static final JProc JPROC = new JProc();

    /**
     * Return a Set of all the processes.
     *
     * @return a Set of all the processes
     */
    @Override
    public Set<JProcess> getProcesses() {
	return JPROC.getProcesses();
    }

    @Override
    public JProcess getProcess(final int pid) {
	return new JProcess(pid, JPROC.getInfo(pid));
    }
}
