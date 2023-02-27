package com.css.cvds.cmu.service.impl;

import com.css.cvds.cmu.conf.security.SecurityUtils;
import com.css.cvds.cmu.conf.security.dto.LoginUser;
import com.css.cvds.cmu.storager.dao.AccessLogMapper;
import com.css.cvds.cmu.storager.dao.LogMapper;
import com.css.cvds.cmu.storager.dao.dto.AccessLogDto;
import com.css.cvds.cmu.service.ILogService;
import com.css.cvds.cmu.storager.dao.dto.LogDto;
import com.css.cvds.cmu.utils.SysLogEnum;
import com.css.cvds.cmu.utils.UserLogEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class LogServiceImpl implements ILogService {

    @Autowired
    private AccessLogMapper accessLogMapper;

    @Autowired
    private LogMapper logMapper;

    @Override
    public PageInfo<LogDto> getList(int page, int count, String query,
                                    Integer type, String startTime, String endTime,
                                    Integer userId, String terminal) {
        PageHelper.startPage(page, count);
        List<LogDto> all = logMapper.query(query, type, startTime, endTime, userId, terminal);
        return new PageInfo<>(all);
    }

    @Override
    public void addUserLog(UserLogEnum type, String content) {
        LoginUser userInfo = SecurityUtils.getUserInfo();
        LogDto logDto = new LogDto();
        logDto.setType(type.getType());
        logDto.setTitle(type.getName());
        logDto.setCreateTime(new Date());
        logDto.setContent(content);
        if (Objects.nonNull(userInfo)) {
            logDto.setTerminal(userInfo.getTerminal());
            logDto.setUserId(userInfo.getId());
        } else {
            logDto.setUserId(0);
        }
        logMapper.add(logDto);
    }

    @Override
    public void addSysLog(SysLogEnum type, String content) {
        LogDto logDto = new LogDto();
        logDto.setType(type.getType());
        logDto.setTitle(type.getName());
        logDto.setCreateTime(new Date());
        logDto.setUserId(0);
        logDto.setContent(content);

        logMapper.add(logDto);
    }

    @Override
    public PageInfo<AccessLogDto> getAccessList(int page, int count, String query, String type, String startTime, String endTime) {
        PageHelper.startPage(page, count);
        List<AccessLogDto> all = accessLogMapper.query(query, type, startTime, endTime);
        return new PageInfo<>(all);
    }

    @Override
    public void addAccessLog(AccessLogDto accessLogDto) {
        accessLogMapper.add(accessLogDto);
    }

    @Override
    public int clearAccessLog() {
        return accessLogMapper.clear();
    }
}
