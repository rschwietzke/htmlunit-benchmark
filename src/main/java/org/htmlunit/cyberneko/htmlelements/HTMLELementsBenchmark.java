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

package org.htmlunit.cyberneko.htmlelements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class HTMLELementsBenchmark {
    private static final String simpleFile = "src/test/resources/org/htmlunit/cyberneko/benchmark/simple.html";
    private static final String smallFile = "src/test/resources/org/htmlunit/cyberneko/benchmark/small-xc-homepage.html";
    private static final String mediumFile = "src/test/resources/org/htmlunit/cyberneko/benchmark/wikipedia-de-hp.html";
    private static final String largeFile = "src/test/resources/org/htmlunit/cyberneko/benchmark/puma-de-hp.html";

    private List<String> tagNames = new ArrayList<>(1000);

    @Param({simpleFile, smallFile, mediumFile, largeFile})
    String file;

    // our HTMLElements for testing
    org.htmlunit.cyberneko.htmlelements.old416.HTMLElements htmlElementsOld416 = new org.htmlunit.cyberneko.htmlelements.old416.HTMLElements();
    org.htmlunit.cyberneko.htmlelements.old.HTMLElements htmlElementsOld = new org.htmlunit.cyberneko.htmlelements.old.HTMLElements();
    org.htmlunit.cyberneko.htmlelements.rbrill.HTMLElements htmlElementsNew = new org.htmlunit.cyberneko.htmlelements.rbrill.HTMLElements();
    org.htmlunit.cyberneko.htmlelements.uncached.HTMLElements htmlElementsUncached = new org.htmlunit.cyberneko.htmlelements.uncached.HTMLElements();
    org.htmlunit.cyberneko.htmlelements.new417.HTMLElements.HTMLElementsWithCache htmlElementsNew417 = 
            new org.htmlunit.cyberneko.htmlelements.new417.HTMLElements.HTMLElementsWithCache(
                    new org.htmlunit.cyberneko.htmlelements.new417.HTMLElements());
    org.htmlunit.cyberneko.htmlelements.new417_2.HTMLElements.HTMLElementsWithCache htmlElementsNew417_2 = 
            new org.htmlunit.cyberneko.htmlelements.new417_2.HTMLElements.HTMLElementsWithCache(
                    new org.htmlunit.cyberneko.htmlelements.new417_2.HTMLElements());
    @Setup
    public void setup(BenchmarkParams params) throws IOException {
        // extract all tag names in a cheap way, good enough for our purpose
        // AI generated code, might be not perfect
        Files.readAllLines(Path.of(file)).forEach(line -> {
            int idx = 0;
            while (true) {
                final int idx1 = line.indexOf('<', idx);
                if (idx1 < 0) {
                    break;
                }
                final int idx2 = line.indexOf(' ', idx1 + 1);
                final int idx3 = line.indexOf('>', idx1 + 1);
                if (idx3 < 0) {
                    break;
                }
                final int idx4 = line.indexOf('/', idx1 + 1);
                int endIdx = idx3;
                if (idx2 > 0 && idx2 < endIdx) {
                    endIdx = idx2;
                }
                if (idx4 > 0 && idx4 < endIdx) {
                    endIdx = idx4;
                }
                if (endIdx > idx1 + 1) {
                    // take the tag as found
                    final String tagName = line.substring(idx1 + 1, endIdx);
                    tagNames.add(tagName);
                }
                idx = idx3 + 1;
            }
        });
//        tagNames.clear();
//        tagNames.add("foo");
//        tagNames.add("foo");
//        tagNames.add("akjsdf sa");

        // remove !DOCTYPE and empty tags
        tagNames.removeIf(t -> t.length() == 0 || t.charAt(0) == '!');
        //        System.out.println("Found " + tagNames.size() + " tags in " + file);
        //        tagNames.stream().forEach(System.out::println);

    }

    @Benchmark
    public org.htmlunit.cyberneko.htmlelements.old416.HTMLElements.Element old416HTMLElements() {
        org.htmlunit.cyberneko.htmlelements.old416.HTMLElements.Element last = null;

        for (final String tagName : tagNames) {
            last = htmlElementsOld416.getElement(tagName);
        }

        return last;
    }

    @Benchmark
    public org.htmlunit.cyberneko.htmlelements.old.HTMLElements.Element oldHTMLElements() {
        org.htmlunit.cyberneko.htmlelements.old.HTMLElements.Element last = null;

        for (final String tagName : tagNames) {
            last = htmlElementsOld.getElement(tagName);
        }

        return last;
    }

    @Benchmark
    public org.htmlunit.cyberneko.htmlelements.rbrill.HTMLElements.Element newHTMLElements() {
        org.htmlunit.cyberneko.htmlelements.rbrill.HTMLElements.Element last = null;

        for (final String tagName : tagNames) {
            last = htmlElementsNew.getElement(tagName);
        }

        return last;
    }

    @Benchmark
    public org.htmlunit.cyberneko.htmlelements.uncached.HTMLElements.Element uncachedHTMLElements() {
        org.htmlunit.cyberneko.htmlelements.uncached.HTMLElements.Element last = null;

        for (final String tagName : tagNames) {
            last = htmlElementsUncached.getElement(tagName);
        }

        return last;
    }

    @Benchmark
    public org.htmlunit.cyberneko.htmlelements.new417.HTMLElements.Element new417HTMLElements() {
        org.htmlunit.cyberneko.htmlelements.new417.HTMLElements.Element last = null;

        for (final String tagName : tagNames) {
            last = htmlElementsNew417.getElement(tagName);
        }

        return last;
    }

    @Benchmark
    public org.htmlunit.cyberneko.htmlelements.new417_2.HTMLElements.Element new417_2HTMLElements() {
        org.htmlunit.cyberneko.htmlelements.new417_2.HTMLElements.Element last = null;

        for (final String tagName : tagNames) {
            last = htmlElementsNew417_2.getElement(tagName);
        }

        return last;
    }

    public static void main(String[] args) throws RunnerException
    {
        Options opt = new OptionsBuilder()
                // important, otherwise we will run all tests!
                .include(HTMLELementsBenchmark.class.getSimpleName() + ".new417_2HTMLElements")
                // 0 is needed for debugging, not for running
                .forks(0)
                .build();

        new Runner(opt).run();
    }
}

