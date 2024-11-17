package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getGroupId/{group}")

public class JPGroupId {

    static final JProc JPROC = new JProc();

    /**
     * Get the group id for the given group name.
     *
     * @param group the group name to convert into a group id
     *
     * @return the numerical group id, as a String
     */
    @GET
    @Produces("application/json")
    public String getGroupId(@PathParam("group") String group) {
	return Integer.toString(JPROC.getGroupId(group));
    }
}
