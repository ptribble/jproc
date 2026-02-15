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

package uk.co.petertribble.jproc.parse;

import uk.co.petertribble.jproc.api.JLwp;
import uk.co.petertribble.jproc.api.JProcess;
import uk.co.petertribble.jproc.api.JProcInfo;
import uk.co.petertribble.jproc.api.JProcLwpInfo;
import uk.co.petertribble.jproc.api.JProcLwpStatus;
import uk.co.petertribble.jproc.api.JProcStatus;
import uk.co.petertribble.jproc.api.JProcUsage;
import java.util.HashSet;
import java.util.Set;
import com.github.openjson.JSONArray;
import com.github.openjson.JSONException;
import com.github.openjson.JSONObject;

/**
 * Read in JSON serialized proc output and parse it.
 *
 * @author Peter Tribble
 */
public final class JSONParser {

    private JSONParser() {
    }

    /**
    /**
     * Parse the supplied String (in JSON format) and return the encoded
     * Set of processes.
     *
     * @param s A String in JSON format representing a Set of JProcesses.
     *
     * @return The Set of JProcesses encoded by the supplied String.
     */
    public static Set<JProcess> getProcesses(final String s) {
	try {
	    return getProcesses(new JSONArray(s));
	} catch (JSONException jse) {
	    return null;
	}
    }

    private static Set<JProcess> getProcesses(final JSONArray ja) {
	Set<JProcess> nprocesses = new HashSet<>();
	try {
	    for (int i = 0; i < ja.length(); i++) {
		JSONObject jo = ja.getJSONObject(i);
		JProcInfo jpi = getInfo(jo);
		nprocesses.add(new JProcess(jpi.getpid(), jpi));
	    }
	} catch (JSONException jse) {
	    // on error, return whatever we have
	}
	return nprocesses;
    }

    /**
     * Parse the supplied String (in JSON format) and return the encoded
     * Set of lwps.
     *
     * @param s A String in JSON format representing a Set of lwps.
     *
     * @return The Set of JLwp encoded by the supplied String.
     */
    public static Set<JLwp> getLwps(final String s) {
	try {
	    return getLwps(new JSONArray(s));
	} catch (JSONException jse) {
	    return null;
	}
    }

    private static Set<JLwp> getLwps(final JSONArray ja) {
	Set<JLwp> nlwps = new HashSet<>();
	try {
	    for (int i = 0; i < ja.length(); i++) {
		JSONObject jo = ja.getJSONObject(i);
		nlwps.add(new JLwp(jo.getInt("pid"), jo.getInt("lwpid")));
	    }
	} catch (JSONException jse) {
	    // on error, return whatever we have
	}
	return nlwps;
    }

    /**
     * Parse the supplied String (in JSON format) and return the encoded
     * JProcStatus.
     *
     * @param s A String in JSON format representing a JProcStatus.
     *
     * @return The JProcStatus encoded by the supplied String.
     */
    public static JProcStatus getStatus(final String s) {
	try {
	    return getStatus(new JSONObject(s));
	} catch (JSONException jse) {
	    try {
		/*
		 * It may be wrapped in a single-element array. For example,
		 * node returns it this way.
		 */
		JSONArray ja = new JSONArray(s);
		return getStatus(ja.getJSONObject(0));
	    } catch (JSONException jse2) {
		return null;
	    }
	}
    }

    private static JProcStatus getStatus(final JSONObject jo) {
	JProcStatus jps = new JProcStatus();
	try {
	    jps.insert(jo.getInt("lwpid"),
			jo.getLong("utime"), jo.getLong("nutime"),
			jo.getLong("stime"), jo.getLong("nstime"),
			jo.getLong("cutime"), jo.getLong("ncutime"),
			jo.getLong("cstime"), jo.getLong("ncstime"));
	} catch (JSONException jse) {
	    return null;
	}
	return jps;
    }

    /**
     * Parse the supplied String (in JSON format) and return the encoded
     * JProcUsage.
     *
     * @param s A String in JSON format representing a JProcUsage.
     *
     * @return The JProcUsage encoded by the supplied String.
     */
    public static JProcUsage getUsage(final String s) {
	try {
	    return getUsage(new JSONObject(s));
	} catch (JSONException jse) {
	    try {
		/*
		 * It may be wrapped in a single-element array. For example,
		 * node returns it this way.
		 */
		JSONArray ja = new JSONArray(s);
		return getUsage(ja.getJSONObject(0));
	    } catch (JSONException jse2) {
		return null;
	    }
	}
    }

    private static JProcUsage getUsage(final JSONObject jo) {
	JProcUsage jpu = new JProcUsage();
	try {
	    jpu.insert(jo.getInt("lwpid"), jo.getInt("count"),
			jo.getLong("rtime"), jo.getLong("nrtime"),
			jo.getLong("utime"), jo.getLong("nutime"),
			jo.getLong("stime"), jo.getLong("nstime"),
			jo.getLong("minf"), jo.getLong("majf"),
			jo.getLong("nswap"), jo.getLong("inblk"),
			jo.getLong("oublk"),
			jo.getLong("msnd"), jo.getLong("mrcv"),
			jo.getLong("sigs"),
			jo.getLong("vctx"), jo.getLong("ictx"),
			jo.getLong("sysc"), jo.getLong("ioch"));
	} catch (JSONException jse) {
	    return null;
	}
	return jpu;
    }

    /**
     * Parse the supplied String (in JSON format) and return the encoded
     * JProcInfo.
     *
     * @param s A String in JSON format representing a JProcInfo.
     *
     * @return The JProcInfo encoded by the supplied String.
     */
    public static JProcInfo getInfo(final String s) {
	try {
	    return getInfo(new JSONObject(s));
	} catch (JSONException jse) {
	    try {
		/*
		 * It may be wrapped in a single-element array. For example,
		 * node returns it this way.
		 */
		JSONArray ja = new JSONArray(s);
		return getInfo(ja.getJSONObject(0));
	    } catch (JSONException jse2) {
		return null;
	    }
	}
    }

    private static JProcInfo getInfo(final JSONObject jo) {
	JProcInfo jpi = new JProcInfo();
	try {
	    jpi.insert(jo.getInt("pid"), jo.getInt("ppid"),
			jo.getInt("uid"), jo.getInt("euid"),
			jo.getInt("gid"), jo.getInt("egid"),
			jo.getInt("nlwp"),
			jo.getLong("size"), jo.getLong("rssize"),
			jo.getLong("stime"),
			jo.getLong("etime"), jo.getLong("ntime"),
			jo.getLong("ectime"), jo.getLong("nctime"),
			jo.getInt("taskid"), jo.getInt("projid"),
			jo.getInt("zoneid"), jo.getInt("contract"),
			jo.getString("fname"));
	} catch (JSONException jse) {
	    return null;
	}
	return jpi;
    }

    /**
     * Parse the supplied String (in JSON format) and return the encoded
     * JProcLwpStatus.
     *
     * @param s A String in JSON format representing a JProcLwpStatus.
     *
     * @return The JProcLwpStatus encoded by the supplied String.
     */
    public static JProcLwpStatus getLwpStatus(final String s) {
	try {
	    return getLwpStatus(new JSONObject(s));
	} catch (JSONException jse) {
	    try {
		/*
		 * It may be wrapped in a single-element array. For example,
		 * node returns it this way.
		 */
		JSONArray ja = new JSONArray(s);
		return getLwpStatus(ja.getJSONObject(0));
	    } catch (JSONException jse2) {
		return null;
	    }
	}
    }

    private static JProcLwpStatus getLwpStatus(final JSONObject jo) {
	JProcLwpStatus jpls = new JProcLwpStatus();
	try {
	    jpls.insert(jo.getInt("pid"), jo.getInt("lwpid"),
			jo.getLong("utime"), jo.getLong("nutime"),
			jo.getLong("stime"), jo.getLong("nstime"));
	} catch (JSONException jse) {
	    return null;
	}
	return jpls;
    }

    /**
     * Parse the supplied String (in JSON format) and return the encoded
     * JProcLwpInfo.
     *
     * @param s A String in JSON format representing a JProcLwpInfo.
     *
     * @return The JProcLwpInfo encoded by the supplied String.
     */
    public static JProcLwpInfo getLwpInfo(final String s) {
	try {
	    return getLwpInfo(new JSONObject(s));
	} catch (JSONException jse) {
	    try {
		/*
		 * It may be wrapped in a single-element array. For example,
		 * node returns it this way.
		 */
		JSONArray ja = new JSONArray(s);
		return getLwpInfo(ja.getJSONObject(0));
	    } catch (JSONException jse2) {
		return null;
	    }
	}
    }

    private static JProcLwpInfo getLwpInfo(final JSONObject jo) {
	JProcLwpInfo jpli = new JProcLwpInfo();
	try {
	    jpli.insert(jo.getInt("pid"), jo.getInt("lwpid"),
			jo.getLong("stime"), jo.getLong("etime"),
			jo.getLong("ntime"));
	} catch (JSONException jse) {
	    return null;
	}
	return jpli;
    }
}
