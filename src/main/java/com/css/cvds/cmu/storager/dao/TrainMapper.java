package com.css.cvds.cmu.storager.dao;

import com.css.cvds.cmu.service.bean.GPSMsgInfo;
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
    GPSMsgInfo.Train getById(Integer id);
}
