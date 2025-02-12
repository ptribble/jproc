/*
 * SPDX-License-Identifier: CDDL-1.0
 *
 * CDDL HEADER START
 *
 * This file and its contents are supplied under the terms of the
 * Common Development and Distribution License ("CDDL"), version 1.0.
 * You may only use this file in accordance with the terms of version
 * 1.0 of the CDDL.
 *
 * A full copy of the text of the CDDL should have accompanied this
 * source. A copy of the CDDL is also available via the Internet at
 * http://www.illumos.org/license/CDDL.
 *
 * CDDL HEADER END
 *
 * Copyright 2025 Peter Tribble
 *
 */

package uk.co.petertribble.jproc.demo;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Enumeration;
import uk.co.petertribble.jproc.api.*;
import uk.co.petertribble.jproc.client.PClientConfig;
import uk.co.petertribble.jproc.gui.ProcessTreeNode;
import javax.swing.tree.TreeNode;

/**
 * An example of the use of the JProc api to print information in the mannner
 * of ptree.
 *
 * @author Peter Tribble
 */
public class PTree {

    private SortedMap<Integer, ProcessTreeNode> nodemap;
    private SortedMap<Integer, JProcess> pmap; //NOPMD

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
	pmap = new TreeMap<>();
	nodemap = new TreeMap<>();
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
	    for (int i = 0; i < ppath.length - 1; i++) {
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
	for (Enumeration e = ptn.children(); e.hasMoreElements();) {
	    printit((ProcessTreeNode) e.nextElement(), indent + "  ");
	}
    }

    private void printone(JProcess p, String indent) {
	System.out.println(indent + p.getPid() + " "
			+ p.getCachedInfo().getfname());
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
	    if ("-s".equals(args[0])) {
		new PTree(
		    new PClientConfig(args[1], PClientConfig.CLIENT_XMLRPC));
	    } else if ("-S".equals(args[0])) {
		new PTree(
		    new PClientConfig(args[1], PClientConfig.CLIENT_REST));
	    } else {
		System.err.println("Usage: ptree [-s|-S server] [pid]");
	    }
	} else if (args.length == 3) {
	    if ("-s".equals(args[0])) {
		new PTree(
		    new PClientConfig(args[1], PClientConfig.CLIENT_XMLRPC),
		    Integer.parseInt(args[2]));
	    } else if ("-S".equals(args[0])) {
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
