/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Stefan Kuhn
 *
 *  Contact: cdk-jchempaint@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jchempaint;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 *  Helper class for dealing with String objects.
 *
 */
public class StringHelper
{

	/**
	 *  Partitions a given String into separate words and writes them into an
	 *  array.
	 *
	 *@param  input  String The String to be cutted into pieces
	 *@return        String[] The array containing the separate words
	 */
	public static String[] tokenize(String input)
	{
		Vector vector = new Vector();
		StringTokenizer tokenizer = new StringTokenizer(input);
		String seperateWords[];
		while (tokenizer.hasMoreTokens())
		{
			vector.addElement(tokenizer.nextToken());
		}
		seperateWords = new String[vector.size()];
		for (int i = 0; i < seperateWords.length; i++)
		{
			seperateWords[i] = (String) vector.elementAt(i);
		}
		return seperateWords;
	}
}

