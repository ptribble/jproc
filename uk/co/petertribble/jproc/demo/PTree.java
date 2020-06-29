/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at usr/src/OPENSOLARIS.LICENSE
 * or http://www.opensolaris.org/os/licensing.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at usr/src/OPENSOLARIS.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

package uk.co.petertribble.jproc.demo;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Enumeration;
import uk.co.petertribble.jproc.api.*;
import uk.co.petertribble.jproc.client.*;
import uk.co.petertribble.jproc.gui.ProcessTreeNode;
import javax.swing.tree.TreeNode;

/**
 * An example of the use of the JProc api to print information in the mannner
 * of ptree.
 *
 * @author Peter Tribble
 */
public class PTree {

    private SortedMap <Integer, ProcessTreeNode> nodemap;
    private SortedMap <Integer, JProcess> pmap;

    /**
     * Run the ptree demo, printing a tree of all processes.
     */
    public PTree() {
	this(new JProc());
    }

    /**
     * Run the ptree demo, printing a tree of all processes.
     *
     * @param pcc a PClientConfig containing client configuration
     */
    public PTree(PClientConfig pcc) {
	this(new JProc(pcc));
    }

    /**
     * Run the ptree demo, printing a tree of the given process.
     *
     * @param pid the process id to display
     */
    public PTree(int pid) {
	this(new JProc(), pid);
    }

    /**
     * Run the ptree demo, printing a tree of the given process.
     *
     * @param pcc a PClientConfig containing client configuration
     * @param pid the process id to display
     */
    public PTree(PClientConfig pcc, int pid) {
	this(new JProc(pcc), pid);
    }

    /**
     * Run the ptree demo, printing a tree of all processes.
     *
     * @param jp a JProc to query for process information
     */
    public PTree(JProc jp) {
	buildtree(jp);
	printit((ProcessTreeNode) nodemap.get(nodemap.firstKey()).getRoot());
    }

    /**
     * Run the ptree demo, printing a tree of the given process.
     *
     * @param jp a JProc to query for process information
     * @param pid the process id to display
     */
    public PTree(JProc jp, int pid) {
	buildtree(jp);
	printit(pmap.get(pid));
    }

    private void buildtree(JProc jp) {
	pmap = new TreeMap <Integer, JProcess> ();
	nodemap = new TreeMap <Integer, ProcessTreeNode> ();
	// create a node for each process
	for (JProcess p : jp.getProcesses()) {
	    pmap.put(p.getPid(), p);
	    nodemap.put(p.getPid(), new ProcessTreeNode(p));
	}
	// add each process to its parent
	// FIXME need a way to skip init/sched/zsched
	for (JProcess p : jp.getProcesses()) {
	    int ppid = p.getCachedInfo().getppid();
	    ProcessTreeNode ptn = nodemap.get(ppid);
	    if (ptn != null && ppid != p.getPid()) {
		ptn.add(nodemap.get(p.getPid()));
	    }
	}
    }

    /*
     * This prints the entire tree below this process, but just the direct
     * ancestors. Fortunately the TreeNode api gives us that information.
     */
    private void printit(JProcess p) {
	if (p != null) {
	    ProcessTreeNode ptn = nodemap.get(p.getPid());
	    TreeNode[] ppath = ptn.getPath();
	    String indent = "";
	    for (int i = 0; i < ppath.length -1; i++) {
		ProcessTreeNode ptn2 = (ProcessTreeNode) ppath[i];
		printone(ptn2.getProcess(), indent);
		indent = indent + "  ";
	    }
	    printit(ptn, indent);
	}
    }

    private void printit(ProcessTreeNode ptn) {
	printit(ptn, "");
    }

    private void printit(ProcessTreeNode ptn, String indent) {
	printone(ptn.getProcess(), indent);
	for (Enumeration e = ptn.children(); e.hasMoreElements(); ) {
	    printit((ProcessTreeNode) e.nextElement(), indent + "  ");
	}
    }

    private void printone(JProcess p, String indent) {
	System.out.println(indent + p.getPid() + " " +
			p.getCachedInfo().getfname());
    }

    /**
     * Invoke the PTree demo from the command line.
     *
     * @param args  Command line arguments
     */
    public static void main(String[] args) {
	if (args.length == 0) {
	    new PTree();
	} else if (args.length == 1) {
	    new PTree(Integer.parseInt(args[0]));
	} else if (args.length == 2) {
	    if (args[0].equals("-s")) {
		new PTree(
		    new PClientConfig(args[1], PClientConfig.CLIENT_XMLRPC));
	    } else if (args[0].equals("-S")) {
		new PTree(
		    new PClientConfig(args[1], PClientConfig.CLIENT_REST));
	    } else {
		System.err.println("Usage: ptree [-s|-S server] [pid]");
	    }
	} else if (args.length == 3) {
	    if (args[0].equals("-s")) {
		new PTree(
		    new PClientConfig(args[1], PClientConfig.CLIENT_XMLRPC),
		    Integer.parseInt(args[2]));
	    } else if (args[0].equals("-S")) {
		new PTree(
		    new PClientConfig(args[1], PClientConfig.CLIENT_REST),
		    Integer.parseInt(args[2]));
	    } else {
		System.err.println("Usage: ptree [-s|-S server] [pid]");
	    }
	} else {
	    System.err.println("Usage: ptree [-s|-S server] [pid]");
	}
    }
}
