package com.css.cvds.cmu.storager.dao;

import com.css.cvds.cmu.gb28181.bean.SuperviseTarget;
import com.css.cvds.cmu.gb28181.bean.SuperviseTargetType;
import com.css.cvds.cmu.gb28181.bean.Train;
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
            "s.* " +
            "from " +
            "supervise_target s " +
            "WHERE " +
            " <if test='type != null'> AND s.type=#{type} </if> " +
            "1=1 " +
            "ORDER BY s.type " +
            " </script>"})
    List<SuperviseTarget> getList(Integer type);

    @Select("SELECT * FROM supervise_target_type")
    List<SuperviseTargetType> getTypeList();
}
