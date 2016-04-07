package org.elmlang.intellijplugin.utils;

/**
 * Created by kamil on 06.04.16.
 */
public interface Function3<Param1, Param2, Param3, Result> {
    Result apply(Param1 var1, Param2 var2, Param3 var3);
}
