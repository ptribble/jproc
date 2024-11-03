package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getInfo/{pid}")

public class ProcInfo {

    static final JProc JPROC = new JProc();

    @GET
    @Produces("application/json")
    public String getInfo(@PathParam("pid") String pid) {
	JProcInfo jpi = JPROC.getInfo(Integer.valueOf(pid));
	return (jpi == null) ? "" : jpi.toJSON();
    }
}
