package com.css.cvds.cmu.service;

import com.css.cvds.cmu.storager.dao.dto.AccessLogDto;
import com.css.cvds.cmu.storager.dao.dto.LogDto;
import com.css.cvds.cmu.utils.SysLogEnum;
import com.css.cvds.cmu.utils.UserLogEnum;
import com.github.pagehelper.PageInfo;

/**
 * 系统日志
 */
public interface ILogService {

    /**
     * 查询访问日志
     * @param page 当前页
     * @param count 每页数量
     * @param query 搜索内容
     * @param type 类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param userId 用户ID
     * @param terminal 操作终端
     * @return 日志列表
     */
    PageInfo<LogDto> getList(int page, int count, String query, Integer type, String startTime, String endTime, Integer userId, String terminal);

    /**
     * 添加访问日志
     * @param type 日志类型
     * @param content 日志内容
     */
    void addUserLog(UserLogEnum type, String content);

    /**
     * 添加访问日志
     * @param type 日志类型
     * @param content 日志内容
     */
    void addSysLog(SysLogEnum type, String content);

    /**
     * 查询访问日志
     * @param page 当前页
     * @param count 每页数量
     * @param query 搜索内容
     * @param type 类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    PageInfo<AccessLogDto> getAccessList(int page, int count, String query, String type, String startTime, String endTime);

    /**
     * 添加访问日志
     * @param accessLogDto 日志
     */
    void addAccessLog(AccessLogDto accessLogDto);

    /**
     * 清空访问日志
     */
    int clearAccessLog();

}
