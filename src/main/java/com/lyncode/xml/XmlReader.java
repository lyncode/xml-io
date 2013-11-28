/**
 * Copyright 2012 Lyncode
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lyncode.xml;

import com.lyncode.xml.exceptions.XmlReaderException;
import org.codehaus.stax2.XMLInputFactory2;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class XmlReader {
    private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory2.newFactory();
    private final XMLEventReader xmlEventParser;

    public XmlReader(InputStream stream) throws XMLStreamException {
        this.xmlEventParser = XML_INPUT_FACTORY.createXMLEventReader(stream);
    }


    public boolean isStart() throws XmlReaderException {
        return getPeek().isStartElement();
    }

    public boolean isEnd() throws XmlReaderException {
        return getPeek().isEndElement();
    }

    public boolean isText() throws XmlReaderException {
        return getPeek().isCharacters();
    }


    public String getText() throws XmlReaderException {
        if (isText())
            return getPeek().asCharacters().getData();
        else
            return null;
    }

    public boolean hasName(String name) throws XmlReaderException {
        if (getPeek().isStartElement())
            return getPeek().asStartElement().getName().getLocalPart().equals(name);
        else
            return getPeek().asEndElement().getName().getLocalPart().equals(name);
    }

    public boolean hasAttribute(String name) throws XmlReaderException {
        if (getPeek().isStartElement()) {
            Iterator<Attribute> attributes = getPeek().asStartElement().getAttributes();
            while (attributes.hasNext()) {
                if (attributes.next().getName().getLocalPart().equals(name))
                    return true;
            }

        }
        return false;
    }

    public String getAttribute(String name) throws XmlReaderException {
        if (getPeek().isStartElement()) {
            Iterator<Attribute> attributes = getPeek().asStartElement().getAttributes();
            while (attributes.hasNext()) {
                Attribute attribute = attributes.next();
                if (attribute.getName().getLocalPart().equals(name))
                    return attribute.getValue();
            }

        }
        return null;
    }

    public Map<String, String> getAttributes() throws XmlReaderException {
        HashMap<String, String> map = new HashMap<String, String>();
        if (getPeek().isStartElement()) {
            Iterator<Attribute> attributes = getPeek().asStartElement().getAttributes();
            while (attributes.hasNext()) {
                Attribute attribute = attributes.next();
                map.put(attribute.getName().getLocalPart(), attribute.getValue());
            }
        }
        return map;
    }

    public boolean nextStartElement() throws XmlReaderException {
        try {
            xmlEventParser.nextEvent();
            while (!getPeek().isStartElement() && !getPeek().isEndDocument())
                xmlEventParser.nextEvent();
            return getPeek().isStartElement();
        } catch (XMLStreamException e) {
            throw new XmlReaderException(e);
        }
    }

    public boolean nextElement() throws XmlReaderException {
        try {
            xmlEventParser.nextEvent();
            while (!getPeek().isStartElement() && !getPeek().isEndElement() && !getPeek().isEndDocument())
                xmlEventParser.nextEvent();
            return getPeek().isStartElement() || getPeek().isEndElement();
        } catch (XMLStreamException e) {
            throw new XmlReaderException(e);
        }
    }

    private XMLEvent getPeek() throws XmlReaderException {
        try {
            return this.xmlEventParser.peek();
        } catch (XMLStreamException e) {
            throw new XmlReaderException(e);
        }
    }
}
