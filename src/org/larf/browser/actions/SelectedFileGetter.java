/*
 * $Id: SelectedFileGetter.java,v 1.1 2005-04-04 01:10:15 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf.browser.actions;

import org.larf.FileNode;

/**
 * Implementations can be used by the Actions to find out which is the currently
 * selected file
 *
 */

public interface SelectedFileGetter {

   
   /** Returns the currently selected StoreFile  */
   public FileNode getSelectedFile();
}


