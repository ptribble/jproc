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

@Path("getZoneId/{zone}")

public class JPZoneId {

    static final JProc JPROC = new JProc();

    /**
     * Look up a zone id by name.
     *
     * @param zone the name of the zone
     *
     * @return the numerical id of the zone, as a String
     */
    @GET
    @Produces("application/json")
    public String getZoneId(@PathParam("zone") final String zone) {
	return Integer.toString(JPROC.getZoneId(zone));
    }
}
