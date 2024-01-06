/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.htmlunit.cyberneko;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.parsers.DOMParser;
import org.htmlunit.cyberneko.parsers.SAXParser;
import org.htmlunit.cyberneko.xerces.xni.XNIException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLInputSource;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLParserConfiguration;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class HtmlParser_v380_Benchmark {
    private static final String simpleFile = "src/test/resources/org/htmlunit/cyberneko/benchmark/simple.html";
    private static final String smallFile = "src/test/resources/org/htmlunit/cyberneko/benchmark/small-xc-homepage.html";
    private static final String mediumFile = "src/test/resources/org/htmlunit/cyberneko/benchmark/wikipedia-de-hp.html";
    private static final String largeFile = "src/test/resources/org/htmlunit/cyberneko/benchmark/puma-de-hp.html";

    @Param({simpleFile, smallFile, mediumFile, largeFile})
    String file = mediumFile;

    @Setup
    public void setup(BenchmarkParams params) throws IOException {
    }

    @Benchmark
    public XMLParserConfiguration simpleParser() throws XNIException, IOException {
        final XMLParserConfiguration parser = new HTMLConfiguration();
        parser.parse(new XMLInputSource(null, file, null));

        return parser;
    }

    @Benchmark
    public SAXParser saxParser() throws XNIException, IOException {
        final SAXParser parser = new SAXParser();

        ContentHandler myContentHandler = new MyContentHandler();
        parser.setContentHandler(myContentHandler);

        parser.parse(new XMLInputSource(null, file, null));

        return parser;
    }

    @Benchmark
    public DOMParser domParser() throws XNIException, IOException {
        final DOMParser parser = new DOMParser(HTMLDocumentImpl.class);
        XMLInputSource src = new XMLInputSource(null, file, null);
        src.setEncoding("UTF-8");
        parser.parse(src);

        return parser;
    }

    public static void main(String[] args) throws RunnerException
    {
        Options opt = new OptionsBuilder()
                // important, otherwise we will run all tests!
                .include(HtmlParser_v380_Benchmark.class.getSimpleName() + ".domParser")
                // 0 is needed for debugging, not for running
                .forks(0)
                .build();

        new Runner(opt).run();
    }

    private static class MyContentHandler implements ContentHandler {
        @Override
        public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        }

        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        }

        @Override
        public void startDocument() throws SAXException {
        }

        @Override
        public void skippedEntity(final String name) throws SAXException {
        }

        @Override
        public void setDocumentLocator(final Locator locator) {
        }

        @Override
        public void processingInstruction(final String target, final String data) throws SAXException {
        }

        @Override
        public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        }

        @Override
        public void endPrefixMapping(final String prefix) throws SAXException {
        }

        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        }

        @Override
        public void endDocument() throws SAXException {
        }

        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
        }
    }
}

