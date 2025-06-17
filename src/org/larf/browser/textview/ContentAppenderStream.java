/*
 * $Id: ContentAppenderStream.java,v 1.1 2005-04-04 01:10:15 mch Exp $
 *
 * @author Created by Omnicore CodeGuide
 */

package org.larf.browser.textview;

import java.io.OutputStream;

/**
 * Implementations 'write' out the data to a Component.
 */

public abstract class ContentAppenderStream extends OutputStream
{
   public abstract void setError(Throwable th);
}

