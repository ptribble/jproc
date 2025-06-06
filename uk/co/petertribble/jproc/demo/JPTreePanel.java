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
import java.util.Set;
import java.util.HashSet;
import java.util.Enumeration;
import uk.co.petertribble.jproc.api.*;
import uk.co.petertribble.jproc.gui.*;
import javax.swing.tree.*;
import javax.swing.*;
import java.awt.BorderLayout;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * An example of the use of the JProc api to give a graphical display
 * of the process tree.
 *
 * @author Peter Tribble
 */
public final class JPTreePanel extends JPanel implements TreeSelectionListener,
	ActionListener {

    private static final long serialVersionUID = 1L;

    /**
     * The JProc to query for data.
     */
    private JProc jproc;
    /**
     * The filter to select the processes to be displayed.
     */
    private JProcessFilter jpf;
    /**
     * A table to display process information.
     */
    private JPinfoTable jup;
    /**
     * A table to display lwp information.
     */
    private LWPusageTable lup;
    /**
     * The update interval for the table.
     */
    private int interval;
    /**
     * A Timer to update the display in a loop.
     */
    private Timer timer;
    /**
     * The update interval for the table.
     */
    private int delay = 1000;
    /**
     * A holder panel.
     */
    private JPanel ppanel;
    /**
     * A model containing the process tree.
     */
    private DefaultTreeModel dtm;

    private transient SortedMap<Integer, ProcessTreeNode> nodemap;

    /**
     * Create a new JPTreePanel.
     *
     * @param jproc a JProc object to query for process information
     * @param jpf a filter defining the processes to be shown
     * @param interval the initial update interval, in seconds
     */
    public JPTreePanel(JProc jproc, JProcessFilter jpf, int interval) {
	this.jproc = jproc;
	this.jpf = jpf;
	this.interval = interval;

	setLayout(new BorderLayout());
	buildtree();
	dtm = new DefaultTreeModel(getRoot());
	JTree jt = new JTree(dtm);
	jt.addTreeSelectionListener(this);
	ppanel = new JPanel(new BorderLayout());
	JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					new JScrollPane(jt), ppanel);
	jsp.setOneTouchExpandable(true);
	jsp.setDividerLocation(150);
	add(jsp);
	startLoop();
    }

    private void buildtree() {
	nodemap = new TreeMap<>();
	// create a node for each process
	for (JProcess p : jpf.getProcesses()) {
	    nodemap.put(p.getPid(), new ProcessTreeNode(p));
	}
	// add each process to its parent
	for (JProcess p : jpf.getProcesses()) {
	    int ppid = p.getCachedInfo().getppid();
	    ProcessTreeNode ptn = nodemap.get(ppid);
	    if (ptn != null && ppid != p.getPid()) {
		ptn.add(nodemap.get(p.getPid()));
	    }
	}
    }

    private ProcessTreeNode getRoot() {
	return (ProcessTreeNode) nodemap.get(nodemap.firstKey()).getRoot();
    }

    /**
     * Start regular updates of the panel.
     */
    public void startLoop() {
	if (delay > 0) {
	    if (timer == null) {
		timer = new Timer(delay, this);
	    }
	    timer.start();
	}
	if (jup != null) {
	    jup.startLoop();
	}
	if (lup != null) {
	    lup.startLoop();
	}
    }

    /**
     * Stop the panel updating.
     */
    public void stopLoop() {
	// just stop the child panels, leave the main loop running so we keep
	// updating the process list
	if (jup != null) {
	    jup.stopLoop();
	}
	if (lup != null) {
	    lup.stopLoop();
	}
    }

    /**
     * Set a new update interval.
     *
     * @param i The new update delay in seconds
     */
    public void setDelay(int i) {
	if (interval <= 0) {
	    stopLoop();
	} else {
	    delay = interval * 1000;
	    if (timer != null) {
		timer.setDelay(delay);
	    }
	}
	if (jup != null) {
	    jup.setDelay(i);
	}
	if (lup != null) {
	    lup.setDelay(i);
	}
    }

    private void showProcess(ProcessTreeNode ptn) {
	stopLoop();
	ppanel.removeAll();
	JPanel up = new JPanel(new BorderLayout());
	JProcess jp = ptn.getProcess();
	JProcInfo jpi = jp.getCachedInfo();
	jup = new JPinfoTable(jproc, new JProcessFilter(jp), interval);
	jup.removeColumn("pid");
	jup.removeColumn("ppid");
	jup.removeColumn("nlwp");
	jup.removeColumn("COMMAND");
	up.add(jup.getTableHeader(), BorderLayout.NORTH);
	up.setBorder(BorderFactory.createTitledBorder(
		"process " + jp.getPid()
		+ " " + jpi.getfname()));
	up.add(jup);
	ppanel.add(up, BorderLayout.NORTH);
	lup = new LWPusageTable(jproc, jp, interval);
	lup.removeColumn("NSWAP");
	lup.removeColumn("MINF");
	lup.removeColumn("MSND");
	lup.removeColumn("MRCV");
	JPanel tp = new JPanel(new BorderLayout());
	tp.add(new JScrollPane(lup));
	tp.setBorder(BorderFactory.createTitledBorder("threads"));
	ppanel.add(tp);
	ppanel.validate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	updateJprocess();
    }

    private void updateJprocess() {
	// update the underlying process list
	if (jpf.update()) {
	    /*
	     * A Set of processes that are orphaned and need reparenting.
	     */
	    Set<JProcess> orphans = new HashSet<>();
	    /*
	     * First add new nodes to the map.
	     */
	    for (JProcess jp : jpf.getAddedProcesses()) {
		nodemap.put(jp.getPid(), new ProcessTreeNode(jp));
	    }
	    /*
	     * Now add the new nodes to the tree.
	     */
	    for (JProcess jp : jpf.getAddedProcesses()) {
		int ppid = jp.getCachedInfo().getppid();
		ProcessTreeNode ptn = nodemap.get(ppid);
		if (ptn != null && ppid != jp.getPid()) {
		    ptn.add(nodemap.get(jp.getPid()));
		    dtm.nodeStructureChanged(ptn);
		}
	    }
	    for (JProcess jp : jpf.getDeletedProcesses()) {
		/*
		 * Get the node and remove it from the map. In that order.
		 */
		ProcessTreeNode tn = nodemap.get(jp.getPid());
		nodemap.remove(jp.getPid());
		/*
		 * Record this node's child processes so we can reparent them.
		 */
		for (Enumeration e1 = tn.children(); e1.hasMoreElements();) {
		    orphans.add(((ProcessTreeNode) e1.nextElement())
				.getProcess());
		}
		/*
		 * Find the node's parent.
		 */
		int ppid = jp.getCachedInfo().getppid();
		ProcessTreeNode ptn = nodemap.get(ppid);
		/*
		 * Remove the node from the parent.
		 */
		if (ptn != null) {
		    ptn.remove(tn);
		    dtm.nodeStructureChanged(ptn);
		}
	    }
	    /*
	     * Remove any deleted processes from the orphan list.
	     */
	    orphans.removeAll(jpf.getDeletedProcesses());
	    /*
	     * Reparent all remaining orphans. Note that we cannot use
	     * getCachedInfo() to get the ppid, because it has changed.
	     *
	     * It is irritating that we are often reparented by init, which is
	     * obviously the root of the tree, and this then resets the whole
	     * tree.
	     */
	    for (JProcess jp : orphans) {
		// FIXME what do we do if this process has exited?
		// currently, just wait until next time around
		JProcInfo jpi = jproc.getInfo(jp);
		if (jpi != null) {
		    // ok, we're still here
		    int ppid = jpi.getppid();
		    ProcessTreeNode ptn = nodemap.get(ppid);
		    if (ptn != null && ppid != jp.getPid()) {
			// FIXME if we haven't got a valid parent, we ought to
			// save the process and get a new parent next time
			// around
			ptn.add(nodemap.get(jp.getPid()));
			dtm.nodeStructureChanged(ptn);
		    }
		}
	    }
	}

    }

    // handle TreeSelectionListener events
    @Override
    public void valueChanged(TreeSelectionEvent e) {
	TreePath tpth = e.getNewLeadSelectionPath();
	if (tpth != null) {
	    showProcess((ProcessTreeNode) tpth.getLastPathComponent());
	}
    }
}
