package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getProjectName/{projid}")

public class JPProjName {

    static final JProc JPROC = new JProc();

    /**
     * Get the project name for the given project id.
     *
     * @param projid the projectid to convert into a project name, as a String
     *
     * @return the project name, as a String
     */
    @GET
    @Produces("application/json")
    public String getProjectName(@PathParam("projid") String projid) {
	return JPROC.getProjectName(Integer.valueOf(projid));
    }
}
