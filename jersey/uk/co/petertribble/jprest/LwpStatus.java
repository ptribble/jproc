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

package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getLwpStatus/{pid}/{lwpid}")

public class LwpStatus {

    static final JProc JPROC = new JProc();

    /**
     * Get a status report for the given lwp.
     *
     * @param pid the pid to report usage of, as a String
     * @param lwpid the lwpid to report usage of, as a String
     *
     * @return a JSON formatted JProcLwpStatus for the given lwp
     */
    @GET
    @Produces("application/json")
    public String getLwpStatus(@PathParam("pid") String pid,
			    @PathParam("lwpid") String lwpid) {
	JProcLwpStatus jps = JPROC.getStatus(Integer.valueOf(pid),
					  Integer.valueOf(lwpid));
	return (jps == null) ? "" : jps.toJSON();
    }
}
