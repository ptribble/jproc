package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getLwpInfo/{pid}/{lwpid}")

public class LwpInfo {

    static final JProc jproc = new JProc();

    @GET
    @Produces("application/json")
    public String getLwpInfo(@PathParam("pid") String pid,
			    @PathParam("lwpid") String lwpid) {
	JProcLwpInfo jpi = jproc.getInfo(Integer.valueOf(pid),
					 Integer.valueOf(lwpid));
	return (jpi == null) ? "" : jpi.toJSON();
    }
}
