package org.labs.common;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class Config {

    public final int NUMBER_OF_STUDENTS;
    public final int NUMBER_OF_SOUP;
    public final int NUMBER_OF_WAITERS;

    public final long TIME_TO_EAT_SOUP_MS;
    public final long TIME_TO_SPEAK_MS;

    public final boolean FAIR_IF_POSSIBLE;
}
