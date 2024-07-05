package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getGroupName/{gid}")

public class JPGroupName {

    static final JProc jproc = new JProc();

    @GET
    @Produces("application/json")
    public String getGroupName(@PathParam("gid") String gid) {
	return jproc.getGroupName(Integer.valueOf(gid));
    }
}
