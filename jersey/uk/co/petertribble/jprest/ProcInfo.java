package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getInfo/{pid}")

public class ProcInfo {

    static final JProc JPROC = new JProc();

    /**
     * Get info about the given process.
     *
     * @param pid the pid to report usage of, as a String
     *
     * @return a JSON formatted JProcInfo for the given process
     */
    @GET
    @Produces("application/json")
    public String getInfo(@PathParam("pid") String pid) {
	JProcInfo jpi = JPROC.getInfo(Integer.valueOf(pid));
	return (jpi == null) ? "" : jpi.toJSON();
    }
}
