/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
**********************************/
package net.sourceforge.jwebunit;

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
            Element img = getChildImageElement(a);

            if (img == null)
            {
                return false;
            }

            String src = img.getAttribute("src");
            return src.endsWith((String) criteria);
        }

        private
        Element getChildImageElement(Element htmlElement)
        {
            NodeList nodes = htmlElement.getElementsByTagName("img");
            return nodes.getLength() == 0 ? null : (Element) nodes.item(0);
        }

    }
