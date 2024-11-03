package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getZoneName/{zoneid}")

public class JPZoneName {

    static final JProc JPROC = new JProc();

    @GET
    @Produces("application/json")
    public String getUserName(@PathParam("zoneid") String zoneid) {
	return JPROC.getZoneName(Integer.valueOf(zoneid));
    }
}
