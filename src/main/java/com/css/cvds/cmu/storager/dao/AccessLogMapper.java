package com.css.cvds.cmu.storager.dao;

import com.css.cvds.cmu.storager.dao.dto.AccessLogDto;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用于存储设服务的日志
 */
@Mapper
@Repository
public interface AccessLogMapper {

    @Insert("insert into access_log ( name, type, uri, address, result, timing, username, createTime) " +
            "values ('${name}', '${type}', '${uri}', '${address}', '${result}', ${timing}, '${username}', '${createTime}')")
    int add(AccessLogDto accessLogDto);

    @Select(value = {"<script>" +
            " SELECT * FROM access_log " +
            " WHERE 1=1 " +
            " <if test=\"query != null\"> AND (name LIKE '%${query}%')</if> " +
            " <if test=\"type != null\" >  AND type = '${type}'</if>" +
            " <if test=\"startTime != null\" >  AND createTime &gt;= '${startTime}' </if>" +
            " <if test=\"endTime != null\" >  AND createTime &lt;= '${endTime}' </if>" +
            " ORDER BY createTime DESC " +
            " </script>"})
    List<AccessLogDto> query(String query, String type, String startTime, String endTime);

    @Delete("DELETE FROM access_log")
    int clear();
}
