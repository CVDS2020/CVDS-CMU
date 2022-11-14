package com.css.cvds.cmu.utils;

/**
 * @author chend
 */
public enum BoardCardEnum {
    /**
     * 板卡
     */
    CARD_POWER(1, "电源板"),
    CARD_CHANGE(2, "交换板"),
    CARD_VIDEO(3, "视频核心板"),
    CARD_VI(4, "AI分析板"),
    ;

    private final Integer type;
    private final String name;

    BoardCardEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
