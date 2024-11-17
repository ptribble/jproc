package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getZoneId/{zone}")

public class JPZoneId {

    static final JProc JPROC = new JProc();

    /**
     * Look up a zone id by name.
     *
     * @param zone the name of the zone
     *
     * @return the numerical id of the zone, as a String
     */
    @GET
    @Produces("application/json")
    public String getZoneId(@PathParam("zone") String zone) {
	return Integer.toString(JPROC.getZoneId(zone));
    }
}
