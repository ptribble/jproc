package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getLwpInfo/{pid}/{lwpid}")

public class LwpInfo {

    static final JProc JPROC = new JProc();

    /**
     * Get info about the given lwp.
     *
     * @param pid the pid to report usage of, as a String
     * @param lwpid the lwpid to report usage of, as a String
     *
     * @return a JSON formatted JProcLwpInfo for the given lwp
     */
    @GET
    @Produces("application/json")
    public String getLwpInfo(@PathParam("pid") String pid,
			    @PathParam("lwpid") String lwpid) {
	JProcLwpInfo jpi = JPROC.getInfo(Integer.valueOf(pid),
					 Integer.valueOf(lwpid));
	return (jpi == null) ? "" : jpi.toJSON();
    }
}
