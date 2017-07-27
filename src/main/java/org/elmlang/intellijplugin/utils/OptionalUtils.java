package org.elmlang.intellijplugin.utils;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class OptionalUtils {
    public static <T1, T2, T3> Optional<T3> map2(Optional<T1> o1, Optional<T2> o2, BiFunction<T1, T2, T3> f) {
        return o1.flatMap(v1 -> o2.map(v2 -> f.apply(v1, v2)));
    }

    public static <T> Optional<T> oneOf(Supplier<Optional<T>>...suppliers) {
        return Arrays.stream(suppliers)
                .reduce(Optional.empty(), OptionalUtils::oneOfTwo, OptionalUtils::oneOfTwo);
    }

    private static <T> Optional<T> oneOfTwo(Optional<T> optional, Supplier<Optional<T>> optionalSupplier) {
        return optional.map(Optional::of).orElseGet(optionalSupplier);
    }

    private static <T> Optional<T> oneOfTwo(Optional<T> optional1, Optional<T> optional2) {
        return oneOfTwo(optional1, () -> optional2);
    }
}
