package com.css.cvds.cmu.storager.dao;

import com.css.cvds.cmu.gb28181.bean.Train;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * 用于存储日志
 * @author chend
 */
@Mapper
@Repository
public interface TrainMapper {

    @Select("SELECT * FROM train WHERE id = 1")
    Train getById(Integer id);
}
