package com.css.cvds.cmu.utils;

/**
 * 用户日志类型
 *
 * @author sumu
 * @date 2019/4/19
 */
public enum UserLogEnum {
    /**
     * 用户操作日志
     */
    ONLINE(1, "用户登录"),
    OFFLINE(1, "用户登出"),
    DATA_CONFIG(1, "配置数据"),
    HARDWARE_CTRL(1, "硬件控制"),
    ALARM_ALREADY(1, "告警确认"),
    RESET(1, "重启系统"),
    ;

    private final Integer type;
    private final String name;

    UserLogEnum(Integer type, String name) {
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
