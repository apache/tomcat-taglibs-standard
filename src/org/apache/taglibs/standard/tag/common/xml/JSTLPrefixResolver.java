/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.taglibs.standard.tag.common.xml;

import java.util.HashMap;

import org.apache.xml.utils.PrefixResolver;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * <meta name="usage" content="general"/>
 * This class implements a JSTL PrefixResolver that
 * can be used to perform prefix-to-namespace lookup
 * for the XPath object.
 */
public class JSTLPrefixResolver implements PrefixResolver
{

  /**
   * The context to resolve the prefix from, if the context
   * is not given. */

    HashMap namespaces;

  /**
   * The URI for the XML namespace.
   * (Duplicate of that found in org.apache.xpath.XPathContext). */
     
  public static final String S_XMLNAMESPACEURI =
    "http://www.w3.org/XML/1998/namespace";

  /**
   * No-arg constructor which would create empty HashMap of namespaces
   */
  public JSTLPrefixResolver()
  {
    namespaces = new HashMap();
  }

  public JSTLPrefixResolver( HashMap nses )
  {
    namespaces = nses;
  }

  /**
   * Given a namespace, get the corresponding prefix.  This assumes that
   * the PrevixResolver hold's it's own namespace context, or is a namespace
   * context itself.
   * @param prefix Prefix to resolve.
   * @return Namespace that prefix resolves to, or null if prefix
   * is not bound.
   */
  public String getNamespaceForPrefix(String prefix)
  {
    return (String)namespaces.get(prefix);
  }

  /**
   * Given a prefix and a Context Node, get the corresponding namespace.
   * Warning: This will not work correctly if namespaceContext
   * is an attribute node.
   * @param prefix Prefix to resolve.
   * @param namespaceContext Node from which to start searching for a
   * xmlns attribute that binds a prefix to a namespace.
   * @return Namespace that prefix resolves to, or null if prefix
   * is not bound.
   */
  public String getNamespaceForPrefix(String prefix,
                                      org.w3c.dom.Node namespaceContext)
  {

    Node parent = namespaceContext;
    String namespace = null;

    if (prefix.equals("xml"))
    {
      namespace = S_XMLNAMESPACEURI;
    }
    else
    {
      int type;

      while ((null != parent) && (null == namespace)
             && (((type = parent.getNodeType()) == Node.ELEMENT_NODE)
                 || (type == Node.ENTITY_REFERENCE_NODE)))
      {
        if (type == Node.ELEMENT_NODE)
        {
          NamedNodeMap nnm = parent.getAttributes();

          for (int i = 0; i < nnm.getLength(); i++)
          {
            Node attr = nnm.item(i);
            String aname = attr.getNodeName();
            boolean isPrefix = aname.startsWith("xmlns:");

            if (isPrefix || aname.equals("xmlns"))
            {
              int index = aname.indexOf(':');
              String p = isPrefix ? aname.substring(index + 1) : "";

              if (p.equals(prefix))
              {
                namespace = attr.getNodeValue();

                break;
              }
            }
          }
        }

        parent = parent.getParentNode();
      }
    }

    return namespace;
  }

  /**
   * Return the base identifier.
   *
   * @return null
   */
  public String getBaseIdentifier()
  {
    return null;
  }

  /**
   * @see PrefixResolver#handlesNullPrefixes() */
  public boolean handlesNullPrefixes() {
    return false;
  }

  public void addNamespace ( String prefix, String uri ) {
    namespaces.put( prefix, uri );
  }

}
