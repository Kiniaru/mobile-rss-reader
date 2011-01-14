//--Need to modify--#preprocess
/*
 * RssItunesFeed.java
 *
 * Copyright (C) 2007-2008 Tommi Laukkanen
 * Copyright (C) 2007-2008 Irving Bunton
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
 * IB 2010-04-17 1.11.5RC2 Change to put compatibility classes in compatibility packages.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 */
// Expand to define itunes define
@DITUNESDEF@
// Expand to define logging define
@DLOGDEF@
package com.substanceofcode.rssreader.businessentities.compatibility3;

import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import com.substanceofcode.rssreader.businessentities.RssFeedInfo;
import com.substanceofcode.utils.compatibility4.Base64;
import com.substanceofcode.utils.MiscUtil;
import java.io.UnsupportedEncodingException;
import java.util.*;
//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * RssItunesFeed class contains one RSS Itunes feed's properties.
 * Properties include name and subtitle and summary.
 *
 * @author Irving Bunton
 */
public class RssItunesFeed extends RssFeed
//#ifdef DTEST
implements RssItunesFeedInfo
//#endif
{
    
	// Make max summary same as max description (actual max is 50K)
    public static int MAX_SUMMARY = 500;
	// Beginning of data that has 0 itunes info.
	// Number of Itunes info
    private static int NBR_ITUNES_FEED_INFO = 8;
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("compatibility3.RssItunesFeed");
	//#endif
	//#ifdef DLOGGING
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif
    // Value that shows that the first item (and those following may
	// contain ITunes items (or all may not contain any, but they
	// can later be modified to contain them).
    private static int INT_ITUNES_INDICATOR = NBR_ITUNES_FEED_INFO;
    private boolean m_itunes = false;
    protected String m_title = "";
    protected String m_description = "";
    protected String m_language = "";   // The RSS feed language
    protected String m_author = "";   // The RSS feed author
    protected String m_subtitle = "";   // The RSS feed subtitle
    protected String m_summary = "";   // The RSS feed summary
    private byte m_explicit = RssItunesItem.BNO_EXPLICIT;   // The RSS feed explicit
    
    /** Creates a new instance of RSSBookmark */
    public RssItunesFeed(){
		super();
	}

    /** Creates a new instance of RSSBookmark */
    public RssItunesFeed(String name, String url, String username, String password){
        super(name, url, username, password);
    }
    
    /** Creates a new instance of RSSBookmark */
    public RssItunesFeed(String name, String url, String username,
						String password,
						Date upddate,
						String link,
						Date date,
						int category,
						boolean itunes,
						String title,
						String description,
						String language,
						String author,
						String subtitle,
						String summary,
						byte explicit) {
        super(name, url, username, password, upddate, link, date, category);
		if (itunes) {
			modifyItunes(itunes, title, description, language, author, subtitle,
					summary, explicit);
		}
	}
    
	/** Modify fields for Itunes. */
	public void modifyItunes(boolean itunes, String title, String description,
							String language,
							String author,
							String subtitle,
							String summary,
							byte explicit) {
		//#ifdef DITUNES
		this.m_itunes = itunes;
		this.m_title = title;
		this.m_description = description;
		this.m_language = language;
		this.m_author = author;
		this.m_subtitle = subtitle;
		this.m_summary = summary;
		this.m_explicit = explicit;
		//#endif
	}

	/** Create feed from an existing feed.  **/
	public RssItunesFeed(RssFeedInfo pfeed) {
		super(pfeed);
		try {
        
			if (pfeed instanceof RssItunesFeedInfo) {
				RssItunesFeedInfo feed = (RssItunesFeedInfo)pfeed;
				this.m_itunes = feed.isItunes();
				if (this.m_itunes) {
					this.m_title = feed.getTitle();
					this.m_description = feed.getDescription();
					this.m_language = feed.getLanguage();
					this.m_author = feed.getAuthor();
					this.m_subtitle = feed.getSubtitle();
					this.m_summary = feed.getSummary();
					this.m_explicit = RssItunesItem.convExplicit(
							feed.getExplicit());
				}
			}
        } catch(Throwable e) {
            System.err.println("RssItunesFeed contructor : " + e.toString());
			e.printStackTrace();
        }
	}
    
	/** Deserialize the object
        Creates a new instance of RssItunesFeed from store string 
		**/
	public static RssItunesFeed deserialize(boolean encoded,
			String storeString){

		try {
        
			boolean hasPipe = (storeString.indexOf('\n') >= 0);
			String[] nodes = MiscUtil.split( storeString, "|" );
			RssItunesFeed feed = new RssItunesFeed();
			feed.init(hasPipe, encoded, nodes);
			return feed;
        } catch(Exception e) {
            System.err.println("Error while RssItunesFeed deserialize : " + e.toString());
			e.printStackTrace();
			return null;
        }
	}
			
	public void init(boolean hasPipe, boolean encoded, String [] nodes) {

		try {
        
			/* Node count should be 6
			 * m_itunes | m_title | m_description | m_language | m_author |
			   m_subtitle | m_summary | m_explicit | rss feed fields
			 */
			// TODO itunes enabled

			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("nodes.length=" + nodes.length);}
			//#endif
			//#ifdef DITUNES
			int ITUNES = 0;
			m_itunes = nodes[ITUNES].equals("1");
			
			if (m_itunes) {
				int TITLE = 1;
				m_title = nodes[TITLE];
				if (hasPipe) {
					m_title = m_title.replace('\n', '|');
				}
				
				int DESCRIPTION = 2;
				m_description = nodes[DESCRIPTION];
				if (hasPipe) {
					m_description = m_description.replace('\n', '|');
				}
				
				int LANGUAGE = 3;
				m_language = nodes[LANGUAGE];
				
				int AUTHOR = 4;
				m_author = nodes[AUTHOR];
				if (hasPipe) {
					m_author = m_author.replace('\n', '|');
				}
				
				int SUBTITLE = 5;
				m_subtitle = nodes[SUBTITLE];
				if (hasPipe) {
					m_subtitle = m_subtitle.replace('\n', '|');
				}
				
				int SUMMARY = 6;
				m_summary = nodes[SUMMARY];
				if (hasPipe) {
					m_summary = m_summary.replace('\n', '|');
				}

				int EXPLICIT = 7;
				final String explicit = nodes[EXPLICIT];
				if (explicit.length() > 0) {
					m_explicit = (byte)Integer.parseInt(explicit);
				} else {
					m_explicit = RssItunesItem.BNO_EXPLICIT;
				}
			}
			//#endif

			super.init(false, NBR_ITUNES_FEED_INFO, true,
					   hasPipe, encoded, nodes);

        } catch(Exception e) {
            System.err.println("Error while RssItunesFeed initialization : " + e.toString());
			e.printStackTrace();
        }
    }
    
    /** Return record store string */
    public String getStoreString(boolean serializeItems, boolean encoded){
		String title = "";
		String description = "";
		String author = "";
		String subtitle = "";
		String summary = "";
		//#ifdef DITUNES
		if (m_itunes) {
			title = m_title.replace('|', '\n');
			description = m_description.replace('|', '\n');
			author = m_author.replace('|', '\n');
			subtitle = m_subtitle.replace('|', '\n');
			summary = m_summary.replace('|', '\n');
		}
		//#endif
        String storeString = (m_itunes ? "1" : "") + "|" + title + "|" +
			description + "|" + m_language + "|" +
                author + "|" + subtitle + "|" + summary + "|" +
                 ((m_explicit == RssItunesItem.BNO_EXPLICIT) ? "" :
						 Integer.toString((int)m_explicit)) + "|" +
			super.getStoreString(serializeItems, encoded);
        return storeString;
        
    }

	/** Compare feed to an existing feed.  **/
	public boolean equals(RssFeedInfo pfeed) {
		boolean rtn = true;
		if (!super.equals(pfeed)) {
			rtn = false;
		}
		if (!(pfeed instanceof RssItunesFeedInfo)) {
			return rtn;
		}
		RssItunesFeedInfo feed = (RssItunesFeedInfo)pfeed;
		if (feed.isItunes() != m_itunes) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("unequal feed.m_itunes,this=" + feed.isItunes() + "," + m_itunes);}
			//#endif
			rtn = false;
		}
		if (!feed.getTitle().equals(this.m_title)) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("unequal feed.m_title,this=" + feed.getTitle() + "," + m_title);}
			//#endif
			rtn = false;
		}
		if (!feed.getDescription().equals(this.m_description)) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("unequal feed.m_description,this=" + feed.getDescription() + "," + m_description);}
			//#endif
			rtn = false;
		}
		if (!feed.getLanguage().equals(this.m_language)) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("unequal feed.m_language,this=" + feed.getLanguage() + "," + m_language);}
			//#endif
			rtn = false;
		}
		if (!feed.getAuthor().equals(this.m_author)) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("unequal feed.m_author,this=" + feed.getAuthor() + "," + m_author);}
			//#endif
			rtn = false;
		}
		if (!feed.getSummary().equals(this.m_summary)) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("unequal feed.m_summary,this=" + feed.getSummary() + "," + m_summary);}
			//#endif
			rtn = false;
		}
		if (RssItunesItem.convExplicit(feed.getExplicit()) !=
				m_explicit) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("unequal feed.m_explicit,this=" + feed.getExplicit() + "," + m_explicit);}
			//#endif
			rtn = false;
		}
		return rtn;
	}
    
    public void setCategory(int category) {
        this.m_category = category;
    }

    public int getCategory() {
        return (m_category);
    }

    /** Write record as a string */
    public String toString(){
        String storeString = m_itunes + "|" + m_title + "|" +
			m_description + "|" + m_language + "|" +
			m_author + "|" + m_subtitle + "|" + m_summary + "|" +
                 ((m_explicit == RssItunesItem.BNO_EXPLICIT) ? "" :
						 Integer.toString((int)m_explicit)) + "|" +
				 super.toString();
        return storeString;
        
    }

    public void setDescription(String description) {
        this.m_description = description;
    }

    public String getDescription() {
        return (m_description);
    }

    public void setLanguage(String language) {
        this.m_language = language;
    }

    public String getLanguage() {
        return (m_language);
    }

    public void setAuthor(String author) {
        this.m_author = author;
    }

    public String getAuthor() {
        return (m_author);
    }

    public void setSubtitle(String subtitle) {
        this.m_subtitle = subtitle;
    }

    public String getSubtitle() {
        return (m_subtitle);
    }

    public void setSummary(String summary) {
        this.m_summary = summary;
    }

    public String getSummary() {
        return (m_summary);
    }

    public void setExplicit(int explicit) {
        this.m_explicit = (byte)explicit;
    }

    public void setExplicit(String explicit) {
        this.m_explicit = RssItunesItem.convExplicit(explicit);
    }

    public String getExplicit() {
		return RssItunesItem.convExplicit(m_explicit);
    }

    public void setItunes(boolean itunes) {
        this.m_itunes = itunes;
    }

    public boolean isItunes() {
		// If itunes, allow it.  If not itunes, make it seem that it is not.
		//#ifdef DITUNES
        return (m_itunes);
		//#else
        return (false);
		//#endif
    }

    public void setTitle(String title) {
        this.m_title = title;
    }

    public String getTitle() {
        return (m_title);
    }

}
