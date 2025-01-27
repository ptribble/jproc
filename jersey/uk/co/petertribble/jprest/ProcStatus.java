package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getStatus/{pid}")

public class ProcStatus {

    static final JProc JPROC = new JProc();

    /**
     * Get a status report for the given process.
     *
     * @param pid the pid to report status of, as a String
     *
     * @return a JSON formatted JProcStatus for the given pid
     */
    @GET
    @Produces("application/json")
    public String getStatus(@PathParam("pid") String pid) {
	JProcStatus jps = JPROC.getStatus(Integer.valueOf(pid));
	return (jps == null) ? "" : jps.toJSON();
    }
}
