package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getUserName/{uid}")

public class JPUserName {

    static final JProc jproc = new JProc();

    @GET
    @Produces("application/json")
    public String getUserName(@PathParam("uid") String uid) {
	return jproc.getUserName(Integer.valueOf(uid));
    }
}
