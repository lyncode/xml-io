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

package com.lyncode.xml.matchers;

import com.lyncode.test.matchers.extractor.ExtractFunction;
import com.lyncode.test.matchers.extractor.MatcherExtractor;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import javax.xml.namespace.QName;
import javax.xml.stream.events.XMLEvent;

public class XmlEventMatchers {
    public static Matcher<XMLEvent> text () {
        return new TypeSafeMatcher<XMLEvent>() {
            @Override
            protected boolean matchesSafely(XMLEvent item) {
                return item.isCharacters();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is text");
            }
        };
    }

    public static Matcher<XMLEvent> aStartElement () {
        return new TypeSafeMatcher<XMLEvent>() {
            @Override
            protected boolean matchesSafely(XMLEvent item) {
                return item.isStartElement();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is start element");
            }
        };
    }

    public static Matcher<XMLEvent> anEndElement () {
        return new TypeSafeMatcher<XMLEvent>() {
            @Override
            protected boolean matchesSafely(XMLEvent item) {
                return item.isEndElement();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is start element");
            }
        };
    }

    public static Matcher<XMLEvent> theEndOfDocument () {
        return new TypeSafeMatcher<XMLEvent>() {
            @Override
            protected boolean matchesSafely(XMLEvent item) {
                return item.isEndDocument();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is start element");
            }
        };
    }

    public static Matcher<XMLEvent> theStartOfDocument () {
        return new TypeSafeMatcher<XMLEvent>() {
            @Override
            protected boolean matchesSafely(XMLEvent item) {
                return item.isStartDocument();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is start element");
            }
        };
    }

    public static Matcher<XMLEvent> hasAttributes () {
        return new TypeSafeMatcher<XMLEvent>() {
            @Override
            protected boolean matchesSafely(XMLEvent item) {
                return aStartElement().matches(item) && item.asStartElement().getAttributes().hasNext();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has attributes");
            }
        };
    }

    public static Matcher<XMLEvent> elementName (Matcher<QName> nameMatcher) {
        return new MatcherExtractor<XMLEvent, QName>(nameMatcher, extractName());
    }

    private static ExtractFunction<XMLEvent, QName> extractName() {
        return new ExtractFunction<XMLEvent, QName>() {
            @Override
            public QName apply(XMLEvent input) {
                if (input.isStartDocument()) return input.asStartElement().getName();
                else if (input.isEndElement()) return input.asEndElement().getName();
                else return null;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("name");
            }
        };
    }
}
