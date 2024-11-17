package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getUserId/{username}")

public class JPUserId {

    static final JProc JPROC = new JProc();

    /**
     * Get the userid for the given username.
     *
     * @param username the username to convert into a userid
     *
     * @return the numerical userid, as a String
     */
    @GET
    @Produces("application/json")
    public String getUserId(@PathParam("username") String username) {
	return Integer.toString(JPROC.getUserId(username));
    }
}
