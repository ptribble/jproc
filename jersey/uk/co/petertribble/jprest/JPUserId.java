package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getUserId/{username}")

public class JPUserId {

    static final JProc JPROC = new JProc();

    @GET
    @Produces("application/json")
    public String getUserId(@PathParam("username") String username) {
	return Integer.toString(JPROC.getUserId(username));
    }
}
