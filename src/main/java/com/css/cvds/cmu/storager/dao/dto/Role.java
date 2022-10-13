package com.css.cvds.cmu.storager.dao.dto;

/**
 * @author chend
 */
public class Role {

    /**
     * 超级管理员角色ID
     */
    public static final Integer SUPER_ADMIN_ID = 1;
    /**
     * 管理员角色ID
     */
    public static final Integer ADMIN_ID = 2;
    /**
     * 操作员角色ID
     */
    public static final Integer OPERATOR_ID = 3;

    private int id;
    private String name;
    private String authority;
    private String createTime;
    private String updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
