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

package uk.co.petertribble.jproc.client;

import java.io.IOException;
import java.util.Set;
import uk.co.petertribble.jproc.api.*;
import uk.co.petertribble.jproc.parse.JSONParser;

/**
 * An access class for Solaris /proc. Allows details on individual processes
 * to be queried.
 *
 * @author Peter Tribble
 */
public class JsonJProc extends ProcessInterface {

    private JPhttpClient client;

    /**
     * Creates a new NativeJProc object.
     *
     * @param pcc a PClientConfig containing client configuration
     */
    public JsonJProc(PClientConfig pcc) {
	super();
	client = new JPhttpClient(pcc);
    }

    /**
     * Return a Set of all processes in the system.
     *
     * @return A Set of all the processes running on the system.
     */
    public Set <JProcess> getProcesses() {
	try {
	    String m = client.execute("getProcesses");
	    return JSONParser.getProcesses(m);
	} catch (IOException e) {
	    throw new JProcException("JsonJProc getProcesses failed", e);
	}
    }

    /**
     * Return a Set of JLwp objects representing the lwps in this process.
     * If the process no longer exists, returns null
     *
     * @param pid the pid of the process to query
     *
     * @return A Set of JLwp objects representing the lwps in this process.
     */
    public Set <JLwp> getLwps(int pid) {
	try {
	    String m = client.execute("getLwps",
					new String[] {Integer.toString(pid)});
	    return JSONParser.getLwps(m);
	} catch (IOException e) {
	    throw new JProcException("JsonJProc getLwps failed", e);
	}
    }

    /**
     * Retrieves status of a process.
     *
     * @param pid The process pid to query
     *
     * @return A new JProcStatus object populated with current data, or null
     * if the process does not exist
     */
    public JProcStatus getStatus(int pid) {
	try {
	    String m = client.execute("getStatus",
					new String[] {Integer.toString(pid)});
	    return JSONParser.getStatus(m);
	} catch (IOException e) {
	    throw new JProcException("JsonJProc getStatus failed", e);
	}
    }

    /**
     * Retrieves status of an lwp in a process.
     *
     * @param pid The process pid to query
     * @param lwpid The id of the lwp to query
     *
     * @return A new JProcLwpStatus object populated with current data, or null
     * if the process or lwp does not exist
     */
    public JProcLwpStatus getLwpStatus(int pid, int lwpid) {
	try {
	    String m = client.execute("getLwpStatus",
					new String[] {Integer.toString(pid),
						Integer.toString(lwpid)});
	    return JSONParser.getLwpStatus(m);
	} catch (IOException e) {
	    throw new JProcException("JsonJProc getLwpStatus failed", e);
	}
    }

    /**
     * Retrieves information about a process.
     *
     * @param pid The process pid to query
     *
     * @return A new JProcInfo object populated with current data, or null
     * if the process does not exist
     */
    public JProcInfo getInfo(int pid) {
	try {
	    String m = client.execute("getInfo",
					new String[] {Integer.toString(pid)});
	    return JSONParser.getInfo(m);
	} catch (IOException e) {
	    throw new JProcException("JsonJProc getInfo failed", e);
	}
    }

    /**
     * Retrieves information about an lwp in a process.
     *
     * @param pid The process pid to query
     * @param lwpid The id of the lwp to query
     *
     * @return A new JProcLwpInfo object populated with current data, or null
     * if the process or lwp does not exist
     */
    public JProcLwpInfo getLwpInfo(int pid, int lwpid) {
	try {
	    String m = client.execute("getLwpInfo",
					new String[] {Integer.toString(pid),
						Integer.toString(lwpid)});
	    return JSONParser.getLwpInfo(m);
	} catch (IOException e) {
	    throw new JProcException("JsonJProc getLwpInfo failed", e);
	}
    }

    /**
     * Retrieves usage information about a process.
     *
     * @param pid The process pid to query
     *
     * @return A new JProcUsage object populated with current data, or null
     * if the process does not exist
     */
    public JProcUsage getUsage(int pid) {
	try {
	    String m = client.execute("getUsage",
					new String[] {Integer.toString(pid)});
	    return JSONParser.getUsage(m);
	} catch (IOException e) {
	    throw new JProcException("JsonJProc getUsage failed", e);
	}
    }

    /**
     * Retrieves usage information about an lwp in a process.
     *
     * @param pid The process pid to query
     * @param lwpid The id of the lwp to query
     *
     * @return A new JProcUsage object populated with current data, or null
     * if the process or lwp does not exist
     */
    public JProcUsage getLwpUsage(int pid, int lwpid) {
	try {
	    String m = client.execute("getLwpUsage",
					new String[] {Integer.toString(pid),
						Integer.toString(lwpid)});
	    return JSONParser.getUsage(m);
	} catch (IOException e) {
	    throw new JProcException("JsonJProc getLwpUsage failed", e);
	}
    }

    /**
     * Retrieves the user name corresponding to a given numeric uid.
     *
     * @param uid The numeric userid.
     *
     * @return The user name, or null if no user matches.
     */
    public String getUserName(int uid) {
	try {
	    return client.execute("getUserName",
					new String[] {Integer.toString(uid)});
	} catch (IOException e) {
	    throw new JProcException("JsonJProc getUserName failed", e);
	}
    }

    /**
     * Retrieves the user id corresponding to a given username.
     *
     * @param username The username.
     *
     * @return The userid, or -1 if no user matches.
     */
    public int getUserId(String username) {
	try {
	    return Integer.parseInt(client.execute("getUserId",
					new String[] {username}));
	} catch (IOException e) {
	    throw new JProcException("JsonJProc getUserId failed", e);
	}
    }

    /**
     * Retrieves the group name corresponding to a given numeric gid.
     *
     * @param gid The numeric groupid.
     *
     * @return The group name, or null if no group matches.
     */
    public String getGroupName(int gid) {
	try {
	    return client.execute("getGroupName",
					new String[] {Integer.toString(gid)});
	} catch (IOException e) {
	    throw new JProcException("JsonJProc getGroupName failed", e);
	}
    }

    /**
     * Retrieves the group id corresponding to a given group name.
     *
     * @param group The group name.
     *
     * @return The groupid, or -1 if no group matches.
     */
    public int getGroupId(String group) {
	try {
	    return Integer.parseInt(client.execute("getGroupId",
					new String[] {group}));
	} catch (IOException e) {
	    throw new JProcException("JsonJProc getGroupId failed", e);
	}
    }

    /**
     * Retrieves the project name corresponding to a given numeric project id.
     *
     * @param projid The numeric project id.
     *
     * @return The project name, or null if no project matches.
     */
    public String getProjectName(int projid) {
	try {
	    return client.execute("getProjectName",
				new String[] {Integer.toString(projid)});
	} catch (IOException e) {
	    throw new JProcException("JsonJProc getProjectName failed", e);
	}
    }

    /**
     * Retrieves the project id corresponding to a given project name.
     *
     * @param project The project name.
     *
     * @return The project id, or -1 if no project matches.
     */
    public int getProjectId(String project) {
	try {
	    return Integer.parseInt(client.execute("getProjectId",
					new String[] {project}));
	} catch (IOException e) {
	    throw new JProcException("JsonJProc getProjectId failed", e);
	}
    }

    /**
     * Retrieves the zone name corresponding to a given numeric zone id.
     *
     * @param zoneid The numeric zone id.
     *
     * @return The zone name, or null if no zone matches.
     */
    public String getZoneName(int zoneid) {
	try {
	    return client.execute("getZoneName",
				new String[] {Integer.toString(zoneid)});
	} catch (IOException e) {
	    throw new JProcException("JsonJProc getZoneName failed", e);
	}
    }

    /**
     * Retrieves the zone id corresponding to a given zone name.
     *
     * @param zone The zone name.
     *
     * @return The zone id, or -1 if no zone matches.
     */
    public int getZoneId(String zone) {
	try {
	    return Integer.parseInt(client.execute("getZoneId",
						new String[] {zone}));
	} catch (IOException e) {
	    throw new JProcException("JsonJProc getZoneId failed", e);
	}
    }
}
