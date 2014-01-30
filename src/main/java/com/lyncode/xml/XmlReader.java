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

import com.lyncode.test.matchers.extractor.ExtractFunction;
import com.lyncode.xml.exceptions.XmlReaderException;
import org.codehaus.stax2.XMLInputFactory2;
import org.hamcrest.Matcher;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.lyncode.xml.matchers.XmlEventMatchers.text;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class XmlReader {
    private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory2.newFactory();
    private final XMLEventReader xmlEventParser;

    public XmlReader(InputStream stream) throws XmlReaderException {
        try {
            this.xmlEventParser = XML_INPUT_FACTORY.createXMLEventReader(stream);
        } catch (XMLStreamException e) {
            throw new XmlReaderException(e);
        }
    }

    public boolean current (Matcher<XMLEvent> matcher) throws XmlReaderException {
        return matcher.matches(getPeek());
    }

    public void close () throws XmlReaderException {
        try {
            xmlEventParser.close();
        } catch (XMLStreamException e) {
            throw new XmlReaderException(e);
        }
    }


    public String getText() throws XmlReaderException {
        if (current(text()))
            return getPeek().asCharacters().getData();
        else
            throw new XmlReaderException("Current element is not text");
    }

    public boolean hasName (Matcher<QName> matcher) throws XmlReaderException {
        if (getPeek().isStartElement())
            return matcher.matches(getPeek().asStartElement().getName());
        else
            return matcher.matches(getPeek().asEndElement().getName());
    }

    public String getAttributeValue(Matcher<QName> nameMatcher) throws XmlReaderException {
        if (getPeek().isStartElement()) {
            Iterator attributes = getPeek().asStartElement().getAttributes();
            while (attributes.hasNext()) {
                Attribute attribute = (Attribute) attributes.next();
                if (nameMatcher.matches(attribute.getName()))
                    return attribute.getValue();
            }

        }
        return null;
    }

    public <T> Map<T, String> getAttributes(ExtractFunction<QName, T> extractFunction) throws XmlReaderException {
        HashMap<T, String> map = new HashMap<T, String>();
        if (getPeek().isStartElement()) {
            Iterator attributes = getPeek().asStartElement().getAttributes();
            while (attributes.hasNext()) {
                Attribute attribute = (Attribute) attributes.next();
                map.put(extractFunction.apply(attribute.getName()), attribute.getValue());
            }
        }
        return map;
    }

    public boolean hasAttribute (Matcher<Attribute> matcher) throws XmlReaderException {
        return hasItem(matcher).matches(getPeek().asStartElement().getAttributes());
    }

    public boolean untilNext (Matcher<XMLEvent> eventMatcher, Matcher<XMLEvent> foundIsMatcher) throws XmlReaderException {
        try {
            xmlEventParser.nextEvent();
            while (!anyOf(eventMatcher).matches(getPeek()))
                xmlEventParser.nextEvent();

            return foundIsMatcher.matches(foundIsMatcher);
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
