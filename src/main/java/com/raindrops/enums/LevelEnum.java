package com.raindrops.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * LevelEnum
 *
 * @author raindrops
 */
public enum LevelEnum {

    /**
     * 第一关
     */
    Level1(1, 8, 1),
    /**
     * 第二关
     */
    Level2(2, 12, 0),
    /**
     * 第三关
     */
    Level3(3, 24, 0),

    /**
     * 第四关
     */
    Level4(4, 24, 1),

    /**
     * 第五关
     */
    Level5(5, 24, 2),

    /**
     * 第六关
     */
    Level6(6, 24, 3),

    /**
     * 第七关
     */
    Level7(7, 24, 4);

    private final int level;

    private final int puzzleNum;

    private final int direction;

    LevelEnum(int level, int puzzleNum, int direction) {
        this.level = level;
        this.puzzleNum = puzzleNum;
        this.direction = direction;
    }

    private static final Map<Integer, LevelEnum> map = new HashMap<>();

    static {
        for (LevelEnum item : EnumSet.allOf(LevelEnum.class)) {
            map.put(item.level, item);
        }
    }

    public static LevelEnum getByLevel(Integer value) {
        return map.getOrDefault(value, Level7);
    }

    public int getLevel() {
        return level;
    }

    public int getPuzzleNum() {
        return puzzleNum;
    }

    public int getDirection() {
        return direction;
    }
}
