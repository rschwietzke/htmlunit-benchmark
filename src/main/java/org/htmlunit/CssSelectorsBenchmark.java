package org.htmlunit;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlPage;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class CssSelectorsBenchmark {
    private static final String simpleFile = "src/test/resources/org/htmlunit/cyberneko/benchmark/simple.html";
    private static final String smallFile = "src/test/resources/org/htmlunit/cyberneko/benchmark/small-xc-homepage.html";
    private static final String mediumFile = "src/test/resources/org/htmlunit/cyberneko/benchmark/wikipedia-de-hp.html";
    private static final String largeFile = "src/test/resources/org/htmlunit/cyberneko/benchmark/puma-de-hp.html";

    WebClient webClient;
    String content;
    URL url;
    HtmlPage page;
    final static String sUrl = "";

    //@Param({simpleFile, smallFile, mediumFile})
    String file = largeFile;

    @Setup
    public void setup() throws IOException {
        webClient = new WebClient(BrowserVersion.FIREFOX_ESR, false, null, 0);

        content = Files.readAllLines(Paths.get(file)).stream().collect(Collectors.joining());
        url = new URL("http://localhost/");

        final MockWebConnection webConnection = new MockWebConnection();
        webConnection.setResponse(url, content);
        webClient.setWebConnection(webConnection);

        page = webClient.getPage("http://localhost/");
    }

    @TearDown
    public void tearDown() throws IOException {
        webClient.close();
    }

    @Benchmark
    public List<DomNode> cssSelectClass() {
        List<DomNode> l = page.querySelectorAll(".p-mobile-nav-links");
        if (l.size() != 32) {
            throw new RuntimeException("Was " + l.size());
        }
        return l;
    }

    @Benchmark
    public List<DomNode> cssSelectID() {
        List<DomNode> l = page.querySelectorAll("#mobileSearch");
        if (l.size() != 1) {
            throw new RuntimeException("Was " + l.size());
        }
        return l;
    }

    @Benchmark
    public List<DomNode> cssSelectClassIndirectChild() {
        List<DomNode> l = page.querySelectorAll(".p-nav-subnav-item-group .p-sub-nav-tier2 .p-nav-subnav-link");
        if (l.size() != 227) {
            throw new RuntimeException("Was " + l.size());
        }
        return l;
    }

    @Benchmark
    public List<DomNode> cssSelectTag() {
        List<DomNode> l = page.querySelectorAll("footer");
        if (l.size() != 1) {
            throw new RuntimeException("Was " + l.size());
        }
        return l;
    }

    @Benchmark
    public List<DomNode> cssSelectTagAndChildTag() {
        List<DomNode> l = page.querySelectorAll("footer button");
        if (l.size() != 1) {
            throw new RuntimeException("Was " + l.size());
        }
        return l;
    }

    @Benchmark
    public List<DomNode> cssSelectAttribute() {
        List<DomNode> l = page.querySelectorAll("footer button[type='button']");
        if (l.size() != 1) {
            throw new RuntimeException("Was " + l.size());
        }
        return l;
    }

    public static void main(String[] args) throws RunnerException
    {
        Options opt = new OptionsBuilder()
                        // important, otherwise we will run all tests!
                        .include(CssSelectorsBenchmark.class.getSimpleName() + ".cssSelect")
                        // 0 is needed for debugging, not for running
                        .forks(0)
                        .build();

        new Runner(opt).run();
    }

}

