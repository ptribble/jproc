package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getLwpUsage/{pid}/{lwpid}")

public class LwpUsage {

    static final JProc jproc = new JProc();

    @GET
    @Produces("application/json")
    public String getLwpUsage(@PathParam("pid") String pid,
			    @PathParam("lwpid") String lwpid) {
	JProcUsage jpu = jproc.getUsage(Integer.valueOf(pid),
					Integer.valueOf(lwpid));
	return (jpu == null) ? "" : jpu.toJSON();
    }
}
