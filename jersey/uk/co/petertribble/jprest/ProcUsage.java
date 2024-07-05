package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getUsage/{pid}")

public class ProcUsage {

    static final JProc jproc = new JProc();

    @GET
    @Produces("application/json")
    public String getUsage(@PathParam("pid") String pid) {
	JProcUsage jpu = jproc.getUsage(Integer.valueOf(pid));
	return (jpu == null) ? "" : jpu.toJSON();
    }
}
