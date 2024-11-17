package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getZoneName/{zoneid}")

public class JPZoneName {

    static final JProc JPROC = new JProc();

    /**
     * Look up a zone name by id.
     *
     * @param zoneid the id of the zone, as a String
     *
     * @return the name of the zone, as a String
     */
    @GET
    @Produces("application/json")
    public String getZoneName(@PathParam("zoneid") String zoneid) {
	return JPROC.getZoneName(Integer.valueOf(zoneid));
    }
}
