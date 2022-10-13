package com.css.cvds.cmu.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置文件 user-settings 映射的配置信息
 */
@Component
@ConfigurationProperties(prefix = "user-settings", ignoreInvalidFields = true)
public class UserSetting {

    private Boolean savePositionHistory = Boolean.FALSE;

    private Boolean seniorSdp = Boolean.FALSE;

    private Integer playTimeout = 18000;

    private Boolean interfaceAuthentication = Boolean.TRUE;

    private Boolean logInDatabase = Boolean.TRUE;

    private String serverId = "000000";

    private List<String> interfaceAuthenticationExcludes = new ArrayList<>();

    public Boolean getSavePositionHistory() {
        return savePositionHistory;
    }

    public Boolean isSeniorSdp() {
        return seniorSdp;
    }

    public Integer getPlayTimeout() {
        return playTimeout;
    }

    public Boolean isInterfaceAuthentication() {
        return interfaceAuthentication;
    }

    public List<String> getInterfaceAuthenticationExcludes() {
        return interfaceAuthenticationExcludes;
    }

    public void setSavePositionHistory(Boolean savePositionHistory) {
        this.savePositionHistory = savePositionHistory;
    }

    public void setSeniorSdp(Boolean seniorSdp) {
        this.seniorSdp = seniorSdp;
    }

    public void setPlayTimeout(Integer playTimeout) {
        this.playTimeout = playTimeout;
    }

    public void setInterfaceAuthentication(boolean interfaceAuthentication) {
        this.interfaceAuthentication = interfaceAuthentication;
    }

    public void setInterfaceAuthenticationExcludes(List<String> interfaceAuthenticationExcludes) {
        this.interfaceAuthenticationExcludes = interfaceAuthenticationExcludes;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public Boolean getLogInDatabase() {
        return logInDatabase;
    }

    public void setLogInDatabase(Boolean logInDatabase) {
        this.logInDatabase = logInDatabase;
    }
}
