package org.labs;

import org.labs.common.Config;
import org.labs.orderedlocks.DiningStudentsSimulation;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var config = Config.builder()
                .NUMBER_OF_STUDENTS(7)
                .NUMBER_OF_SOUP(10_000)
                .NUMBER_OF_WAITERS(2)
                .TIME_TO_EAT_SOUP_MS(1)
                .TIME_TO_SPEAK_MS(2)
                .FAIR_IF_POSSIBLE(false)
                .build();
        var diningStudents = new DiningStudentsSimulation(config);
        diningStudents.simulate();
    }
}
