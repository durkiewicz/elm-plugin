package org.elmlang.intellijplugin.utils;

import com.intellij.openapi.util.Pair;

import java.util.Set;

public interface TypeFilter {
    boolean testType(String name);

    boolean testTypeMember(String typeName, String memberName);

    String ALL_MEMBERS = "..";

    static TypeFilter always(boolean value) {
        return new TypeFilter() {
            @Override
            public boolean testType(String name) {
                return value;
            }

            @Override
            public boolean testTypeMember(String typeName, String memberName) {
                return value;
            }
        };
    }

    static TypeFilter fromSets(Set<String> types, Set<Pair<String, String>> typeMembers) {
        return new TypeFilter() {
            @Override
            public boolean testType(String name) {
                return types.contains(name);
            }

            @Override
            public boolean testTypeMember(String typeName, String memberName) {
                return typeMembers.contains(Pair.create(typeName, memberName))
                        || typeMembers.contains(Pair.create(typeName, ALL_MEMBERS));
            }
        };
    }

    static TypeFilter and(TypeFilter a, TypeFilter b) {
        return new TypeFilter() {
            @Override
            public boolean testType(String name) {
                return a.testType(name) && b.testType((name));
            }

            @Override
            public boolean testTypeMember(String typeName, String memberName) {
                return a.testTypeMember(typeName, memberName) && b.testTypeMember(typeName, memberName);
            }
        };
    }
}
