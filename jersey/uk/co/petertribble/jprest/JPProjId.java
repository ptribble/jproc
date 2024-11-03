package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getProjectId/{project}")

public class JPProjId {

    static final JProc JPROC = new JProc();

    @GET
    @Produces("application/json")
    public String getProjectName(@PathParam("username") String project) {
	return Integer.toString(JPROC.getProjectId(project));
    }
}
