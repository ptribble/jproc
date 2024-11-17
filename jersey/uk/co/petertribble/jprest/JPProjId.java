package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getProjectId/{project}")

public class JPProjId {

    static final JProc JPROC = new JProc();

    /**
     * Get the project id for the given project name.
     *
     * @param project the project name to convert into a project id
     *
     * @return the numerical project id, as a String
     */
    @GET
    @Produces("application/json")
    public String getProjectId(@PathParam("username") String project) {
	return Integer.toString(JPROC.getProjectId(project));
    }
}
