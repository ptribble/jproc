package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getUserName/{uid}")

public class JPUserName {

    static final JProc JPROC = new JProc();

    /**
     * Get the username for the given userid.
     *
     * @param uid the userid to convert into a username, as a String
     *
     * @return the username, as a String
     */
    @GET
    @Produces("application/json")
    public String getUserName(@PathParam("uid") String uid) {
	return JPROC.getUserName(Integer.valueOf(uid));
    }
}
