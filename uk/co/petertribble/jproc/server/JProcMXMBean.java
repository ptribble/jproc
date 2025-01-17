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

package uk.co.petertribble.jproc.server;

import uk.co.petertribble.jproc.api.JProcess;
import java.util.Set;

/**
 * An MBean, exposing some JProc data via JMX.
 *
 * @author Peter Tribble
 */
public interface JProcMXMBean {

    /**
     * Return a Set of all the processes.
     *
     * @return a Set of all the processes
     */
    Set<JProcess> getProcesses();

    /**
     * Return a given process.
     *
     * @param pid the desired process id
     *
     * @return the desired process
     */
    JProcess getProcess(int pid);
}
