package com.raindrops.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum LevelEnum {

    Level1(1),
    Level2(2),
    Level3(3);
    private final int level;

    LevelEnum(int level) {
        this.level = level;
    }

    private static final Map<Integer, LevelEnum> map = new HashMap<>();

    static {
        for (LevelEnum item : EnumSet.allOf(LevelEnum.class)) {
            map.put(item.level, item);
        }
    }

    public static LevelEnum getByLevel(Integer value) {
        return map.get(value);
    }
}
