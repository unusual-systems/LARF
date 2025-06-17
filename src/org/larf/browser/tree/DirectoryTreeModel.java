/**
 * DirectoryTreeModel.java
 *
 * @author Created by Omnicore CodeGuide
 */

package org.larf.browser.tree;

import javax.swing.tree.DefaultTreeModel;

public class DirectoryTreeModel extends DefaultTreeModel
{
   public DirectoryTreeModel(StoresList stores) {
      super(stores);
   }
   
}

