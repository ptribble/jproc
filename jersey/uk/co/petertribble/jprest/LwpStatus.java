package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getLwpStatus/{pid}/{lwpid}")

public class LwpStatus {

    static final JProc jproc = new JProc();

    @GET
    @Produces("application/json")
    public String getLwpStatus(@PathParam("pid") String pid,
			    @PathParam("lwpid") String lwpid) {
	JProcLwpStatus jps = jproc.getStatus(Integer.valueOf(pid),
					  Integer.valueOf(lwpid));
	return (jps == null) ? "" : jps.toJSON();
    }
}
