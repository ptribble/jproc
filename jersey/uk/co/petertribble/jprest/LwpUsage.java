package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getLwpUsage/{pid}/{lwpid}")

public class LwpUsage {

    static final JProc JPROC = new JProc();

    /**
     * Get the usage of the given lwp.
     *
     * @param pid the pid to report usage of, as a String
     * @param lwpid the lwpid to report usage of, as a String
     *
     * @return a JSON formatted JProcUsage for the given lwp
     */
    @GET
    @Produces("application/json")
    public String getLwpUsage(@PathParam("pid") String pid,
			    @PathParam("lwpid") String lwpid) {
	JProcUsage jpu = JPROC.getUsage(Integer.valueOf(pid),
					Integer.valueOf(lwpid));
	return (jpu == null) ? "" : jpu.toJSON();
    }
}
