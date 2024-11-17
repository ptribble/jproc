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
