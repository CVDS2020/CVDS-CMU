package com.css.cvds.cmu.utils;

/**
 * 用户日志类型
 *
 * @author sumu
 * @date 2019/4/19
 */
public enum SysLogEnum {
    /**
     * 系统日志
     */
    POWER_UP(0, "电源板开机"),
    SELF_CHECK(0, "系统自检"),
    STREAM(0, "视频流"),
    ;

    private final Integer type;
    private final String name;

    SysLogEnum(Integer type, String name) {
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
