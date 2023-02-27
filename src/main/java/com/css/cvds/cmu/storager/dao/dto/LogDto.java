package com.css.cvds.cmu.storager.dao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * @author chend
 */
@Schema(description = "Log信息")
public class LogDto {

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "类型 0-系统日志，1-操作日志")
    private Integer type;
    @Schema(description = "标题/事件/操作类型")
    private String title;
    @Schema(description = "内容")
    private String content;
    @Schema(description = "用户ID")
    private Integer userId;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "操作终端")
    private String terminal;
    @Schema(description = "日志时间")
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }
}
