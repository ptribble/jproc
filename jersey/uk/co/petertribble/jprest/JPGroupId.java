package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getGroupId/{group}")

public class JPGroupId {

    static final JProc JPROC = new JProc();

    @GET
    @Produces("application/json")
    public String getGroupId(@PathParam("group") String group) {
	return Integer.toString(JPROC.getGroupId(group));
    }
}
