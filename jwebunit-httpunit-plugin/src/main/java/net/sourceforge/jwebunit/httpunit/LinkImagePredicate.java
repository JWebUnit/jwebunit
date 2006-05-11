/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
**********************************/
package net.sourceforge.jwebunit.httpunit;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.HTMLElementPredicate;

/**
 * Matches to a Link element containing an image with a specified filename string as a suffix.
 *
 * @author Justin Sampson
 */
public class LinkImagePredicate implements HTMLElementPredicate
    {

        public
        boolean matchesCriteria(Object webLink, Object criteria)
        {
            Element a = (Element) ((WebLink) webLink).getDOMSubtree();
            NodeList imgs = getChildImageElement(a);

            if (imgs == null)
            {
                return false;
            }

            for(int i=0; i< imgs.getLength(); i++) 
			{
                Element img = (Element) imgs.item(i);
                String source = img.getAttribute("src");
                
				if(source.endsWith((String) criteria)) 
					return true;
            }
            return false;
        }

        private
        NodeList getChildImageElement(Element htmlElement)
        {
            NodeList nodes = htmlElement.getElementsByTagName("img");
            return nodes.getLength() == 0 ? null : nodes;
        }
    }
