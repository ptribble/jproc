package uk.co.petertribble.jprest;
import javax.ws.rs.*;
import uk.co.petertribble.jproc.api.*;

@Path("getProcesses")

public class ProcessList {

    static final JProc JPROC = new JProc();

    @GET
    @Produces("application/json")
    public String getProcesses() {
	StringBuilder sb = new StringBuilder();
	sb.append('[');
	for (JProcess jp : JPROC.getProcesses()) {
	    sb.append(JPROC.getInfo(jp).toJSON()).append(",\n");
	}
	sb.append(']');
	return sb.toString();
    }
}
