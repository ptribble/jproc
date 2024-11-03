package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getProjectName/{projid}")

public class JPProjName {

    static final JProc JPROC = new JProc();

    @GET
    @Produces("application/json")
    public String getProjectName(@PathParam("projid") String projid) {
	return JPROC.getProjectName(Integer.valueOf(projid));
    }
}
