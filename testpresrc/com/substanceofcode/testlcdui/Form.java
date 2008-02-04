/*
 * Form.java
 *
 * Copyright (C) 2007 Irving Bunton Jr
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

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define test ui define
@DTESTUIDEF@

//#ifdef DTESTUI

package com.substanceofcode.testlcdui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.StringItem;

import com.substanceofcode.testutil.TestOutput;

/**
 * Form.
 *
 * Test GUI class to log actions on the Form class.
 *
 * @author  Irving Bunton
 * @version 1.0
 */
public class Form extends javax.microedition.lcdui.Form
implements CommandListener {

	private CommandListener m_cmdListener;

	public Form(String title) {
		super(title);
		TestOutput.println("Test UI Form Title: " + title);
	}

	// TODO log items
	public Form(String title, Item[] items) {
		super(title, items);
		TestOutput.println("Test UI Form Title: " + title);
	}

	public int append(Item item) {
		int rtn = super.append(item);
		TestOutput.println("Test UI Form append: [" + super.getTitle() + "]," + item.getLabel());
		String strValue;
		if (item instanceof TextField) {
			strValue = ((TextField)item).getString();
		} else if (item instanceof StringItem) {
			strValue = ((StringItem)item).getText();
		} else if (item instanceof ImageItem) {
			strValue = ((ImageItem)item).getAltText();
		} else {
			return rtn;
		}
		TestOutput.println("Test UI Form append string: [" + super.getTitle() + "]," + strValue);
		TestOutput.println("Test UI Form append int: [" + super.getTitle() + "]," + rtn);
		return rtn;
	}

	public int append(String stringPart) {
		int rtn = super.append(stringPart);
		TestOutput.println("Test UI Form append: [" + super.getTitle() + "]," + stringPart);
		TestOutput.println("Test UI Form append int: [" + super.getTitle() + "]," + rtn);
		return rtn;
	}

	public void insert(int elementnum, Item item) {
		super.insert(elementnum, item);
		TestOutput.println("Test UI Form insert: [" + super.getTitle() + "]," + item.getLabel());
		String strValue;
		if (item instanceof TextField) {
			strValue = ((TextField)item).getString();
		} else if (item instanceof StringItem) {
			strValue = ((StringItem)item).getText();
		} else if (item instanceof ImageItem) {
			strValue = ((ImageItem)item).getAltText();
		} else {
			return;
		}
		TestOutput.println("Test UI Form insert string: [" + super.getTitle() + "]," + strValue);
		TestOutput.println("Test UI Form insert elementnum: [" + super.getTitle() + "]," + elementnum);
	}

	public void set(int elementnum, Item item) {
		try {
			super.set(elementnum, item);
			String strValue;
			if (item instanceof TextField) {
				strValue = ((TextField)item).getString();
			} else if (item instanceof StringItem) {
				strValue = ((StringItem)item).getText();
			} else {
				return;
			}
			TestOutput.println("Test UI Form set string: [" + super.getTitle() + "]," + strValue);
			TestOutput.println("Test UI Form set elementnum: [" + super.getTitle() + "]," + elementnum);
		} catch (Throwable t) {
			System.err.println("Test UI Form set elementnum [" +
					super.getTitle() + "]," + t.getMessage());
			t.printStackTrace();
		}
		return;
	}

	public void delete(int elementnum) {
		try {
			super.delete(elementnum);
			TestOutput.println("Test UI Form delete elementnum: [" + super.getTitle() + "]," + elementnum);
		} catch (Throwable t) {
			System.err.println("Test UI Form delete elementnum: [" +
					super.getTitle() + "]," + t.getMessage());
			t.printStackTrace();
		}
	}

	//#ifdef DMIDP20
	public void deleteAll() {
		super.deleteAll();
		TestOutput.println("Test UI Form delete all [" + super.getTitle() + "]");
	}
	//#endif

	public void outputCmdAct(Command cmd, Displayable disp) {
		//#ifdef DMIDP20
		TestOutput.println("Test UI Form command,displayable=[" + super.getTitle() + "]," + cmd.getLabel() + "," + disp.getTitle());
		//#else
		TestOutput.println("Test UI Form command,displayable=[" + super.getTitle() + "]," + cmd.getLabel() + "," + super.getClass().getName());
		//#endif
	}

	public void commandAction(Command cmd, Displayable disp) {
		outputCmdAct(cmd, disp);
		m_cmdListener.commandAction(cmd, disp);
	}

    public void setCommandListener(CommandListener cmdListener) {
		super.setCommandListener(this);
        this.m_cmdListener = cmdListener;
    }

}

//#endif
