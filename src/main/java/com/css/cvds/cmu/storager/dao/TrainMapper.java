package com.css.cvds.cmu.storager.dao;

import com.css.cvds.cmu.storager.dao.dto.TrainDto;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * 用于存储日志
 * @author chend
 */
@Mapper
@Repository
public interface TrainMapper {

    @Insert("insert into train ( id, serial, trainNo, name, description) " +
            "values ('${id}', '${serial}', '${trainNo}', '${name}', '${description}')")
    int add(TrainDto trainDto);

    @Select("SELECT * FROM train WHERE id = ${id}}")
    TrainDto getById(Integer id);

    @Update(value = {" <script>" +
            "UPDATE train " +
            "SET serial='${serial}'" +
            "<if test=\"trainNo != null\">, trainNo='${trainNo}'</if>" +
            "<if test=\"name != null\">, name='${name}'</if>" +
            "<if test=\"description != null\">, description='${description}'</if>" +
            "WHERE id='${id}'"+
            " </script>"})
    int update(TrainDto train);
}
