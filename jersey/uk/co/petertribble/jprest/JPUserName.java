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

@Path("getUserName/{uid}")

public class JPUserName {

    static final JProc JPROC = new JProc();

    /**
     * Get the username for the given userid.
     *
     * @param uid the userid to convert into a username, as a String
     *
     * @return the username, as a String
     */
    @GET
    @Produces("application/json")
    public String getUserName(@PathParam("uid") final String uid) {
	return JPROC.getUserName(Integer.valueOf(uid));
    }
}
