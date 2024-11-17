package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getGroupName/{gid}")

public class JPGroupName {

    static final JProc JPROC = new JProc();

    /**
     * Get the group name for the given group id.
     *
     * @param gid the groupid to convert into a group name, as a String
     *
     * @return the group name, as a String
     */
    @GET
    @Produces("application/json")
    public String getGroupName(@PathParam("gid") String gid) {
	return JPROC.getGroupName(Integer.valueOf(gid));
    }
}
