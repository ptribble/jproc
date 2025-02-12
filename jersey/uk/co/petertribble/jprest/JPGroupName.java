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

@Path("getGroupName/{gid}")

public class JPGroupName {

    static final JProc JPROC = new JProc();

    /**
     * Get the group name for the given group id.
     *
     * @param gid the groupid to convert into a group name, as a String
     *
     * @return the group name, as a String
     */
    @GET
    @Produces("application/json")
    public String getGroupName(@PathParam("gid") String gid) {
	return JPROC.getGroupName(Integer.valueOf(gid));
    }
}
