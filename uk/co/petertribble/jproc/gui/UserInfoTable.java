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

package uk.co.petertribble.jproc.gui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import uk.co.petertribble.jproc.api.JProc;
import uk.co.petertribble.jproc.api.JProcessFilter;
import uk.co.petertribble.jingle.TableSorter;
import uk.co.petertribble.jproc.util.PrettyFormat;
import javax.swing.table.*;

/**
 * A panel showing process information rather like top, aggregated by user.
 *
 * @author Peter Tribble
 */
public final class UserInfoTable extends JTable implements ActionListener {

    private static final long serialVersionUID = 1L;

    /**
     * The JProc to query for data.
     */
    JProc jproc;
    /**
     * The underlying data model.
     */
    UserInfoTableModel ftm;
    /**
     * The sorted view of the data.
     */
    TableSorter sortedModel;
    /**
     * A custom renderer for sizes.
     */
    private DefaultTableCellRenderer sizeColRenderer;
    /**
     * A custom renderer for times.
     */
    private DefaultTableCellRenderer timeColRenderer;

    /**
     * A Timer to update the display in a loop.
     */
    private Timer timer;
    /**
     * The update interval for the table.
     */
    int interval;

    /**
     * Create a new UserInfoTable.
     *
     * @param jproc a JProc object to query for process information
     * @param jpf a filter defining the processes to be shown
     * @param interval the initial update interval, in seconds
     */
    public UserInfoTable(JProc jproc, JProcessFilter jpf, int interval) {
	this.jproc = jproc;
	this.interval = interval;
	setLayout(new BorderLayout());

	ftm = new UserInfoTableModel(jproc, jpf);
	sortedModel = new TableSorter(ftm);
	setModel(sortedModel);

	/*
	 * Create modified cell renderers for the memory, time, and start
	 * columns
	 */
	sizeColRenderer =
	    new DefaultTableCellRenderer() {
	    private static final long serialVersionUID = 1L;
		@Override
		public void setValue(Object value) {
		    // We know it's a Long, we wrote the model, but to be safe
		    setText(value instanceof Long
			    ? PrettyFormat.memscale((Long) value) : "");
	    }
	};
	sizeColRenderer.setHorizontalAlignment(JLabel.RIGHT);
	timeColRenderer =
	    new DefaultTableCellRenderer() {
		private static final long serialVersionUID = 1L;
		@Override
		public void setValue(Object value) {
		    // We know it's a Double, we wrote the model
		    setText(value instanceof Double
			    ? PrettyFormat.timescale((Double) value) : "");
	    }
	};
	timeColRenderer.setHorizontalAlignment(JLabel.RIGHT);

	setRenderers();

	sortedModel.setTableHeader(getTableHeader());

	addMouseListener((MouseListener) new PopupListener());

	// set up for regular updates
	ftm.updateJprocess();
	startLoop();
    }

    /*
     * This is also called from addColumn and removeColumn as the act of
     * messing with the columns destroys the custom renderers, so we
     * need to add them back.
     *
     * If a column is removed then it isn't shown and getColumn throws an
     * IllegalArgumentException. That doesn't matter as we don't need the
     * setCellRenderer call to succeed if the column isn't visible.
     */
    private void setRenderers() {
	try {
	    getColumn("TIME").setCellRenderer(timeColRenderer);
	} catch (IllegalArgumentException iae) { }
	try {
	    getColumn("SZ").setCellRenderer(sizeColRenderer);
	} catch (IllegalArgumentException iae) { }
	try {
	    getColumn("RSS").setCellRenderer(sizeColRenderer);
	} catch (IllegalArgumentException iae) { }
    }

    /**
     * Only show processes belonging to this zone.
     *
     * @param uid The zone id to show.
     */
    public void setZone(int uid) {
	ftm.setZone(uid);
    }

    /**
     * Unset zone filtering, so that all zones are shown.
     */
    public void unSetZone() {
	ftm.unSetZone();
    }

    /**
     * Start the loop that updates the Jprocesses regularly.
     */
    public void startLoop() {
	if (interval > 0) {
	    if (timer == null) {
		timer = new Timer(interval * 1000, this);
	    }
	    timer.start();
	}
    }

    /**
     * Stop the loop that updates the Jprocesses.
     */
    public void stopLoop() {
	if (timer != null) {
	    timer.stop();
	}
    }

    /**
     * Set the loop delay to be the specified number of seconds.
     * If a zero or negative delay is requested, stop the updates
     * and remember the previous delay.
     *
     * @param interval the desired delay, in seconds
     */
    public void setDelay(int interval) {
	if (interval <= 0) {
	    stopLoop();
	} else {
	    this.interval = interval;
	    if (timer != null) {
		timer.setDelay(interval * 1000);
	    }
	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	ftm.updateJprocess();
    }

    /**
     * Inner class to handle mouse popups.
     */
    class PopupListener extends MouseAdapter {

	@Override
	public void mousePressed(MouseEvent e) {
	    showPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	    showPopup(e);
	}

	// show processes for user in this row
	private void showPopup(MouseEvent e) {
	    if (e.isPopupTrigger()) {
		int uid = ftm.getUser(sortedModel.modelIndex(
				rowAtPoint(e.getPoint())));
		createPopupMenu(uid).show(e.getComponent(), e.getX(), e.getY());
	    }
	}
    }

    /**
     * Create a popup menu that can launch a window showing processes belonging
     * to the given user.
     *
     * @param uid the userid
     *
     * @return a JPopupMenu
     */
    public JPopupMenu createPopupMenu(final int uid) {
	JPopupMenu jpm = new JPopupMenu();
	JMenuItem showChartItem = new JMenuItem(
		JProcResources.getString("USER.SHOW") + " "
				+ jproc.getUserName(uid));
	showChartItem.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		new UserProcessInfoFrame(jproc, uid, interval);
	    }
	});
	jpm.add(showChartItem);
	return jpm;
    }
}
