package org.htmlunit;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.htmlunit.cyberneko.xerces.xni.XNIException;
import org.htmlunit.html.HtmlPage;
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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 30, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class HtmlUnitBenchmark {
    private static final String simpleFile = "src/test/resources/org/htmlunit/cyberneko/benchmark/simple.html";
    private static final String smallFile = "src/test/resources/org/htmlunit/cyberneko/benchmark/small-xc-homepage.html";
    private static final String mediumFile = "src/test/resources/org/htmlunit/cyberneko/benchmark/wikipedia-de-hp.html";
    private static final String largeFile = "src/test/resources/org/htmlunit/cyberneko/benchmark/puma-de-hp.html";

    WebClient webClient;
    String content;
    URL url;
    final static String sUrl = "";

    //@Param({simpleFile, smallFile, mediumFile})
    String file = mediumFile;

    @Setup
    public void setup() throws IOException {
        webClient = new WebClient(BrowserVersion.FIREFOX_ESR, false, null, 0);

        content = Files.readAllLines(Paths.get(file)).stream().collect(Collectors.joining());
        url = new URL("http://localhost/");

        final MockWebConnection webConnection = new MockWebConnection();
        webConnection.setResponse(url, content);
        webClient.setWebConnection(webConnection);
    }

    @TearDown
    public void tearDown() throws IOException {
        webClient.close();
    }
    @Benchmark
    public HtmlPage simple() throws XNIException, IOException {
//        final MockWebConnection webConnection = new MockWebConnection();
//        webConnection.setResponse(url, content);
//        webClient.setWebConnection(webConnection);

        final HtmlPage page = webClient.getPage("http://localhost/");

        return page;
    }

    public static void main(String[] args) throws RunnerException
    {
        Options opt = new OptionsBuilder()
                        // important, otherwise we will run all tests!
                        .include(HtmlUnitBenchmark.class.getSimpleName() + ".simple")
                        // 0 is needed for debugging, not for running
                        .forks(0)
                        .build();

        new Runner(opt).run();
    }

}

