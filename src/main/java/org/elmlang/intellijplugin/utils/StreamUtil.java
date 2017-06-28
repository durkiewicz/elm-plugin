package org.elmlang.intellijplugin.utils;

import java.util.function.Function;
import java.util.stream.Stream;

public class StreamUtil {

    @SafeVarargs
    public static <T> Stream<T> concatAll(Stream<T> ...streams) {
        return Stream.of(streams).flatMap(Function.identity());
    }
}
