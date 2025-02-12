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

@Path("getProjectName/{projid}")

public class JPProjName {

    static final JProc JPROC = new JProc();

    /**
     * Get the project name for the given project id.
     *
     * @param projid the projectid to convert into a project name, as a String
     *
     * @return the project name, as a String
     */
    @GET
    @Produces("application/json")
    public String getProjectName(@PathParam("projid") String projid) {
	return JPROC.getProjectName(Integer.valueOf(projid));
    }
}
