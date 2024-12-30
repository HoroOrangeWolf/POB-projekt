package org.example;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    DIRECTIONAL_0_TO_1("0 na 1"),
    DIRECTIONAL_1_TO_0("1 na 0"),
    BIDIRECTIONAL("1 na 0 i 0 na 1");
    private final String name;

    public static ErrorType getByName(String name) {
        return Arrays.stream(ErrorType.values())
                .filter(errorType -> errorType.name.equals(name))
                .findFirst()
                .orElseThrow();
    }

    public static List<ErrorType> getAll() {
        return Arrays.asList(ErrorType.values());
    }

    public static List<String> getAllNames() {
        return getAll()
                .stream()
                .map(ErrorType::getName)
                .toList();
    }
}
