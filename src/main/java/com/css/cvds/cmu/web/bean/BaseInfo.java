package com.css.cvds.cmu.web.bean;

import com.css.cvds.cmu.conf.security.dto.LoginUser;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author chend
 */
@Schema(description = "基本信息参数")
public class BaseInfo {

    @Schema(description = "系統信息")
    private SystemInfo systemInfo;

    @Schema(description = "用户登录信息")
    private LoginUser userInfo;

    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    public LoginUser getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(LoginUser userInfo) {
        this.userInfo = userInfo;
    }
}
