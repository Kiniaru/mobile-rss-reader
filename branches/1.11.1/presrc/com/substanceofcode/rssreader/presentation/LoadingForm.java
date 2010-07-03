/*
 * LoadingForm.java
 *
 * Copyright (C) 2005-2006 Tommi Laukkanen
 * Copyright (C) 2007-2010 Irving Bunton, Jr
 * http://www.substanceofcode.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
/*
 * IB 2010-03-07 1.11.4RC1 Use observer pattern for feed parsing to prevent hangs from spotty networks and bad URLs.
 * IB 2010-06-09 1.11.5RC2 Add parameters and variables to make this class independent for easier testing.
 */

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define itunes define
@DITUNESDEF@
// Expand to define logging define
@DLOGDEF@
// Expand to define test ui define
@DTESTUIDEF@

package com.substanceofcode.rssreader.presentation;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
//#ifndef DTESTUI
import javax.microedition.lcdui.StringItem;
//#else
// If using the test UI define the Test UI's
import com.substanceofcode.testlcdui.StringItem;
//#endif


import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;
import com.substanceofcode.utils.MiscUtil;
import com.substanceofcode.utils.CauseException;

//#ifdef DMIDP20
import net.yinlight.j2me.observable.Observer;
import net.yinlight.j2me.observable.Observable;
//#endif

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/* Form to show data being loaded.  Save messages and exceptions to
   allow them to be viewed separately as well as diagnostics for
   reporting errors. */
final public class LoadingForm extends FeatureForm
	implements CommandListener {
	//#ifdef DMIDP10
	private String      m_title;         // Store title.
	//#endif
	private boolean     m_loadFinished = false;  // Store loading finished.
	private Command     m_loadMsgsCmd;   // The load form messages command
	private Command     m_loadDiagCmd;   // The load form diagnostic command
	private Command     m_loadErrCmd;    // The load form error command
	private Command     m_loadQuitCmd = null;   // The load form quit command
	private Vector m_msgs = new Vector(); // Original messages
	private Vector m_notes = new Vector(); // Notes
	private Vector m_excs = new Vector(); // Only errors
	//#ifdef DMIDP20
	private Observable m_observable;
	//#else
	private Object m_observable;
	//#endif
	private Displayable m_disp;
	private Displayable m_mainDisp;

	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("LoadingForm");
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif
    
	/* Constructor */
	LoadingForm(final String title,
				final Displayable disp,
				final Displayable mainDisp,
				//#ifdef DMIDP20
				final Observable observable,
				//#else
				final Object observable,
				//#endif
				RssReaderMIDlet midlet
				) {
		super(midlet, title);
		//#ifdef DMIDP10
		this.m_title = title;
		//#endif
		this.m_observable = observable;
		m_loadMsgsCmd       = new Command("Messages", Command.SCREEN, 2);
		m_loadErrCmd        = new Command("Errors", Command.SCREEN, 3);
		m_loadDiagCmd       = new Command("Diagnostics", Command.SCREEN, 4);
		if (RssReaderMIDlet.m_backCommand == null) {
			RssReaderMIDlet.m_backCommand   = new Command("Back", Command.BACK, 1);
		}
		super.addCommand( m_loadMsgsCmd );
		super.addCommand( m_loadErrCmd );
		super.addCommand( m_loadDiagCmd );
		m_mainDisp = mainDisp;
		m_disp = disp;
		if (disp != null) {
			super.addCommand( RssReaderMIDlet.m_backCommand );
		}
	}

	static public LoadingForm getLoadingForm(final String desc,
									   Displayable disp,
									   Displayable mainDisp,
									   //#ifdef DMIDP20
									   Observable observable,
									   //#else
									   Object observable,
									   //#endif
										RssReaderMIDlet midlet
			) {
		LoadingForm loadForm = new LoadingForm("Loading", disp, mainDisp,
				observable, midlet);
		loadForm.appendMsg( desc + "\n" );
		loadForm.setCommandListener( loadForm, false );
		return loadForm;
	}

	/* Add quit command used for errors during exit. */
	public void addQuit() {
		/* Quit */
		if (m_loadQuitCmd == null) {
			m_loadQuitCmd = new Command("Quit", Command.CANCEL, 200);
		}
		super.addPromptCommand( m_loadQuitCmd,
				"Are you sure you want to quit the program without saving?");
	}

	/** Respond to commands */
	public void commandAction(Command c, Displayable s) {

		if( c == RssReaderMIDlet.m_backCommand ){
			Displayable cdisp = null;
			synchronized(this) {
				cdisp = m_disp;
			}
			//#ifdef DMIDP20
			if (m_observable != null) {
				m_observable.getObservableHandler().setCanceled(m_observable,
						true);
			}
			//#endif
			super.getFeatureMgr().getMidlet().setCurrent( cdisp );
		}

		if( c == m_loadQuitCmd ){
			try {
				super.getFeatureMgr().getMidlet().destroyApp(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.getFeatureMgr().getMidlet().notifyDestroyed();
		}

		/** Give messages for loading */
		if( c == m_loadMsgsCmd ) {
			showMsgs();
		}

		/** Give errors for loading */
		if( c == m_loadErrCmd ) {
			showErrMsgs(true);
		}

		/** Give diagnostics for loading */
		if( c == m_loadDiagCmd ) {
			showErrMsgs(false);
		}

	}

	/** Set title and addmessage for loading form */
	public void setLoadingFinished(final String title, String msg) {
		if (title != null) {
			super.setTitle(title);
		}
		if (msg != null) {
			appendMsg(msg);
		}
		setLoadFinished(true);
	}
	
	/* Record the exception in the loading form, log it and give std error. */
	public void recordFin() {
		setTitle("Finished with errors or exceptions below");
		appendMsg("Finished with errors or exceptions above");
		setLoadFinished(true);
	}

	/* Record the exception in the loading form, log it and give std error. */
	public void recordExcForm(final String causeMsg, final Throwable e) {
		final CauseException ce = new CauseException(causeMsg, e);
		//#ifdef DLOGGING
		logger.severe(ce.getMessage(), e);
		//#endif
		/** Error while parsing RSS feed */
		System.out.println(e.getClass().getName() + " " + ce.getMessage());
		e.printStackTrace();
		setTitle("Finished with errors below");
		addExc(ce.getMessage(), ce);
		super.getFeatureMgr().getMidlet().setCurrent( this );
	}

	/* Record the exception in the loading form, log it and give std error. */
	public void recordExcFormFin(final String causeMsg, final Throwable e) {
		recordExcForm(causeMsg, e);
		recordFin();
	}

	/* Show errors and diagnostics. */
	private void showMsgs() {
		try {
			while(super.size()>0) {
				super.delete(0);
			}
			final int elen = m_msgs.size();
			for (int ic = 0; ic < elen; ic++) {
				super.append((String)m_msgs.elementAt(ic));
			}
		}catch(Throwable t) {
			//#ifdef DLOGGING
			logger.severe("showMsgs", t);
			//#endif
			/** Error while executing constructor */
			System.out.println("showMsgs " + t.getMessage());
			t.printStackTrace();
		}
	}

	/* Show errors and diagnostics. */
	private void showErrMsgs(final boolean showErrsOnly) {
		try {
			while(super.size()>0) {
				super.delete(0);
			}
			final int elen = m_excs.size();
			for (int ic = 0; ic < elen; ic++) {
				Throwable nexc = (Throwable)m_excs.elementAt(ic);
				while (nexc != null) {
					String msg = nexc.getMessage();
					if (msg != null) {
						super.append(nexc.getMessage());
						// If showing errs only, only show the first error found
						if (showErrsOnly) {
							break;
						}
					} else if (!showErrsOnly) {
						super.append("Error " + nexc.getClass().getName());
					}
					if (nexc instanceof CauseException) {
						nexc = ((CauseException)nexc).getCause();
					} else {
						break;
					}
				}
			}
			if (!showErrsOnly) {
				//#ifdef DMIDP20
				super.append("Current threads:");
				String[] threadInfo = MiscUtil.getDispThreads();
				for (int ic = 0; ic < threadInfo.length; ic++) {
					super.append(new StringItem(ic + ": ", threadInfo[ic]));
				}
				//#endif
				super.append(new StringItem("Active Threads:",
							Integer.toString(Thread.activeCount())));
			}
		}catch(Throwable t) {
			//#ifdef DLOGGING
			logger.severe("showErrMsgs", t);
			//#endif
			/** Error while executing constructor */
			System.out.println("showErrMsgs " + t.getMessage());
			t.printStackTrace();
		}
	}

	/* Append message to form and save in messages. */
	public void appendMsg(final String msg) {
		if (msg != null) {
			super.append(msg);
			m_msgs.addElement(msg);
		}
	}

	/* Append note to form and save in messages and notes. */
	public void appendNote(final String note) {
		if (note != null) {
			super.append(note);
			m_notes.addElement(note);
		}
	}

	/* Add exception. */
	public void addExc(final String msg, final Throwable exc) {
		appendMsg(msg);
		m_excs.addElement(exc);
	}

	/* Replace reference to displayable to free memory or
	   define where to return to.  Use null to go to m_mainDisp. */
	public void replaceRef(final Displayable disp,
			final Displayable newDisp) {
		//#ifdef DLOGGING
		boolean removed = false;
		//#endif
		synchronized (this) {
			Displayable odisp = m_disp;
			if (m_disp == disp) {
				m_disp = null;
			}
			m_disp = (newDisp == null) ? m_mainDisp : newDisp;
			if ((odisp == null) && (m_disp != null)) {
				super.addCommand( RssReaderMIDlet.m_backCommand);
			}
		}
		//#ifdef DLOGGING
		if (removed) {
			removed = true;
			if (finestLoggable) {logger.finest("Ref removed " + disp);}
		}
		//#endif
	}

	/* Check for exceptions. */
	public boolean hasExc() {
		return (m_excs.size() > 0);
	}

	/* Check for notes. */
	public boolean hasNotes() {
		return (m_notes.size() > 0);
	}

	/* Check for messages. */
	public boolean hasMsgs() {
		return (m_msgs.size() > 0);
	}

	//#ifdef DMIDP10
	public String getTitle() {
		return m_title;
	}
	//#endif

	public void setLoadFinished(boolean loadFinished) {
		this.m_loadFinished = loadFinished;
		Displayable cdisp = null;
		synchronized(this) {
			cdisp = m_disp;
		}
		if (cdisp == null) {
			addQuit();
		}
	}

	public boolean isLoadFinished() {
		return (m_loadFinished);
	}

	//#ifdef DMIDP20
	public void setObservable(Observable observable) {
		this.m_observable = observable;
	}

	public Observable getObservable() {
		return (m_observable);
	}
	//#endif

}