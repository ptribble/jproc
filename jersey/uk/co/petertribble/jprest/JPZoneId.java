package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getZoneId/{zone}")

public class JPZoneId {

    static final JProc jproc = new JProc();

    @GET
    @Produces("application/json")
    public String getZoneId(@PathParam("zone") String zone) {
	return Integer.toString(jproc.getZoneId(zone));
    }
}
