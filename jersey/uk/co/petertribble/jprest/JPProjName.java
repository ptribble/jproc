package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getProjectName/{projid}")

public class JPProjName {

    static final JProc jproc = new JProc();

    @GET
    @Produces("application/json")
    public String getProjectName(@PathParam("projid") String projid) {
	return jproc.getProjectName(Integer.valueOf(projid));
    }
}
