package com.css.cvds.cmu.storager.dao;

import com.css.cvds.cmu.storager.dao.dto.LogDto;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用于存储日志
 * @author chend
 */
@Mapper
@Repository
public interface LogMapper {

    @Insert("insert into log ( type, userId, terminal, title, content) " +
            "values ('${type}', '${userId}', '${terminal}', '${title}', '${content}')")
    int add(LogDto logDto);

    @Select(value = {"<script>" +
            " SELECT * FROM log " +
            " WHERE 1=1 " +
            " <if test=\"query != null\"> AND (title LIKE '%${query}%' OR content LIKE '%${query}%')</if> " +
            " <if test=\"type != null\" >  AND type = '${type}'</if>" +
            " <if test=\"userId != null\" >  AND userId = '${userId}'</if>" +
            " <if test=\"terminal != null\" >  AND terminal = '${terminal}'</if>" +
            " <if test=\"startTime != null\" >  AND createTime &gt;= '${startTime}' </if>" +
            " <if test=\"endTime != null\" >  AND createTime &lt;= '${endTime}' </if>" +
            " ORDER BY createTime DESC " +
            " </script>"})
    List<LogDto> query(String query, Integer type, String startTime, String endTime, Integer userId, String terminal);

    @Delete("DELETE FROM log")
    int clear();
}
