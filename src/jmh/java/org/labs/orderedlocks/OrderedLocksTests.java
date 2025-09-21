package org.labs.orderedlocks;

import java.util.concurrent.TimeUnit;
import org.labs.common.Config;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@Fork(1)
@State(Scope.Benchmark)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class OrderedLocksTests {

    private final Config zeroDelayNonFairTest = Config.builder()
            .NUMBER_OF_STUDENTS(7)
            .NUMBER_OF_SOUP(1_000_0)
            .NUMBER_OF_WAITERS(2)
            .TIME_TO_EAT_SOUP_MS(0)
            .TIME_TO_SPEAK_MS(0)
            .FAIR_IF_POSSIBLE(false)
            .build();

    private final Config zeroDelayFairTest = Config.builder()
            .NUMBER_OF_STUDENTS(7)
            .NUMBER_OF_SOUP(1_000_0)
            .NUMBER_OF_WAITERS(2)
            .TIME_TO_EAT_SOUP_MS(0)
            .TIME_TO_SPEAK_MS(0)
            .FAIR_IF_POSSIBLE(true)
            .build();

    @Benchmark
    public void zeroDelayNonFairTest() throws InterruptedException {
        var diningStudents = new DiningStudentsSimulation(zeroDelayNonFairTest);
        diningStudents.simulate();
    }

    @Benchmark
    public void zeroDelayFairTest() throws InterruptedException {
        var diningStudents = new DiningStudentsSimulation(zeroDelayFairTest);
        diningStudents.simulate();
    }
}
