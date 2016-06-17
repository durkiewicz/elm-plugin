package org.elmlang.intellijplugin.utils;

import java.util.Optional;
import java.util.function.BiFunction;

public class OptionalUtils {
    public static <T1, T2, T3> Optional<T3> map2(Optional<T1> o1, Optional<T2> o2, BiFunction<T1, T2, T3> f) {
        return o1.flatMap(v1 -> o2.map(v2 -> f.apply(v1, v2)));
    }
}
