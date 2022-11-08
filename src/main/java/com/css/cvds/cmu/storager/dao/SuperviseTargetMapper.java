package com.css.cvds.cmu.storager.dao;

import com.css.cvds.cmu.service.bean.SuperviseTarget;
import com.css.cvds.cmu.service.bean.SuperviseTargetType;
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
public interface SuperviseTargetMapper {

    @Select(value = {" <script>" +
            "SELECT " +
            "s.*, t.name as typeName " +
            "from " +
            "supervise_target s, supervise_target_type t " +
            "WHERE " +
            "s.type = t.type " +
            " <if test='type != null'> AND s.type=#{type} </if> " +
            "ORDER BY s.type " +
            " </script>"})
    List<SuperviseTarget> getList(Integer type);

    @Select(value = {" <script>" +
            "SELECT " +
            "s.*, t.name as typeName " +
            "from " +
            "supervise_target s, supervise_target_type t " +
            "WHERE " +
            "s.id=#{id} AND s.type = t.type"+
            " </script>"})
    SuperviseTarget getById(Integer id);

    @Select("SELECT * FROM supervise_target_type")
    List<SuperviseTargetType> getTypeList();
}
