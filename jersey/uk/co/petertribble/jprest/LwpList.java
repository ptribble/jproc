package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;
import java.util.Set;

@Path("getLwps/{pid}")

public class LwpList {

    static final JProc JPROC = new JProc();

    /**
     * Get a list of lwps for the given process.
     *
     * @param pid the pid to list the lwps of, as a String
     *
     * @return a JSON formatted list of lwps for the given process
     */
    @GET
    @Produces("application/json")
    public String getLwps(@PathParam("pid") String pid) {
	Set<JLwp> lwps = JPROC.getLwps(Integer.valueOf(pid));
	if (lwps == null) {
	    return "";
	}
	StringBuilder sb = new StringBuilder();
	sb.append('[');
	for (JLwp jlwp : lwps) {
	    sb.append(jlwp.toJSON()).append(',');
	}
	sb.append(']');
	return sb.toString();
    }
}
