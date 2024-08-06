package com.carrot.carrotmarketclonecoding.board.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;

public enum Method {
    SELL, SHARE;

    @JsonCreator
    public static Method methodValidate(String val) {
        return Arrays.stream(values())
                .filter(type -> type.name().equals(val))
                .findAny()
                .orElse(null);
    }
}
