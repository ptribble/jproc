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

package uk.co.petertribble.jprest;

import java.util.Set;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getLwps/{pid}")

public class LwpList {

    static final JProc JPROC = new JProc();

    /**
     * Get a list of lwps for the given process.
     *
     * @param pid the pid to list the lwps of, as a String
     *
     * @return a JSON formatted list of lwps for the given process
     */
    @GET
    @Produces("application/json")
    public String getLwps(@PathParam("pid") final String pid) {
	Set<JLwp> lwps = JPROC.getLwps(Integer.valueOf(pid));
	if (lwps == null) {
	    return "";
	}
	StringBuilder sb = new StringBuilder();
	sb.append('[');
	for (JLwp jlwp : lwps) {
	    sb.append(jlwp.toJSON()).append(',');
	}
	sb.append(']');
	return sb.toString();
    }
}
