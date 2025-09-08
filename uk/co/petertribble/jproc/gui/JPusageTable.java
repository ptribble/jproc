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

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.event.*;
import uk.co.petertribble.jproc.api.JProc;
import uk.co.petertribble.jproc.api.JProcess;
import uk.co.petertribble.jproc.api.JProcessFilter;
import uk.co.petertribble.jproc.util.PrettyFormat;
import javax.swing.table.*;
import java.util.List;

/**
 * A panel showing process information rather like top.
 *
 * @author Peter Tribble
 */
public final class JPusageTable extends JTable implements ActionListener {

    private static final long serialVersionUID = 1L;

    /**
     * The JProc to query for data.
     */
    JProc jproc;
    /**
     * The underlying data model.
     */
    PSusageTableModel ftm;
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
     * Create a new JPusageTable.
     *
     * @param jproc a JProc object to query for process information
     * @param jpf a filter defining the processes to be shown
     * @param interval the initial update interval, in seconds
     */
    public JPusageTable(final JProc jproc, final JProcessFilter jpf,
			final int interval) {
	this.jproc = jproc;
	this.interval = interval;
	setLayout(new BorderLayout());

	ftm = new PSusageTableModel(jproc, jpf);
	setModel(ftm);
	setAutoCreateRowSorter(true);

	/*
	 * Create modified cell renderers for the time columns
	 */
	timeColRenderer =
	    new DefaultTableCellRenderer() {
		private static final long serialVersionUID = 1L;
		@Override
		public void setValue(final Object value) {
		    // We know it's a Double, we wrote the model
		    setText(value instanceof Double
			    ? PrettyFormat.timescale((Double) value) : "");
	    }
	};
	timeColRenderer.setHorizontalAlignment(JLabel.RIGHT);
	setRenderers();

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
	    getColumn("STIME").setCellRenderer(timeColRenderer);
	} catch (IllegalArgumentException iae) { }
	try {
	    getColumn("UTIME").setCellRenderer(timeColRenderer);
	} catch (IllegalArgumentException iae) { }
	try {
	    getColumn("RTIME").setCellRenderer(timeColRenderer);
	} catch (IllegalArgumentException iae) { }
    }

    /**
     * Return the list of column names.
     *
     * @return a List of column names
     */
    public List<String> columns() {
	return ftm.columns();
    }

    /**
     * Remove a column from the list of items to be displayed.
     *
     * @param s the name of the column to be removed
     */
    public void removeColumn(final String s) {
	ftm.removeColumn(s);
	setRenderers();
    }

    /**
     * Add a column to the list of items to be displayed.
     *
     * @param s the name of the column to be added
     */
    public void addColumn(final String s) {
	ftm.addColumn(s);
	setRenderers();
    }

    /**
     * Only show processes belonging to this user.
     *
     * @param uid The user id to show.
     */
    public void setUser(final int uid) {
	ftm.setUser(uid);
    }

    /**
     * Unset user filtering, so that all users are shown.
     */
    public void unSetUser() {
	ftm.unSetUser();
    }

    /**
     * Only show processes belonging to this zone.
     *
     * @param uid The zone id to show.
     */
    public void setZone(final int uid) {
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
    public void setDelay(final int interval) {
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
    public void actionPerformed(final ActionEvent e) {
	ftm.updateJprocess();
    }

    /**
     * Inner class to handle mouse popups.
     */
    class PopupListener extends MouseAdapter {

	@Override
	public void mousePressed(final MouseEvent e) {
	    showPopup(e);
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	    showPopup(e);
	}

	private void showPopup(final MouseEvent e) {
	    if (e.isPopupTrigger()) {
		JProcess jp = ftm.getProcess(convertRowIndexToModel(
				rowAtPoint(e.getPoint())));
		createPopupMenu(jp).show(e.getComponent(), e.getX(), e.getY());
	    }
	}
    }

    /**
     * Create a popup menu allowing the user to show a thread table.
     *
     * @param jp the JProcess to display
     *
     * @return a JPopupMenu
     */
    public JPopupMenu createPopupMenu(final JProcess jp) {
	JPopupMenu jpm = new JPopupMenu();
	JMenuItem showChartItem = new JMenuItem(
		JProcResources.getString("THREAD.MENU") + " " + jp.getPid());
	showChartItem.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(final ActionEvent e) {
		new LWPusageFrame(jproc, jp, interval);
	    }
	});
	jpm.add(showChartItem);
	return jpm;
    }
}
