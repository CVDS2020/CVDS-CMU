package com.css.cvds.cmu.storager.dao;

import com.css.cvds.cmu.storager.dao.dto.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {

    @Insert("INSERT INTO user (username, password, roleId, department, phone, description, createTime, updateTime) VALUES" +
            "('${username}', '${password}', '${role.id}', '${department}', '${phone}', '${description}', '${createTime}', '${updateTime}')")
    int add(User user);

    @Update(value = {" <script>" +
            "UPDATE user " +
            "SET updateTime='${updateTime}' " +
            "<if test=\"department != null\">, department='${department}'</if>" +
            "<if test=\"phone != null\">, phone='${phone}'</if>" +
            "<if test=\"description != null\">, description='${description}'</if>" +
            "<if test=\"role != null\">, roleId='${role.id}'</if>" +
            "<if test=\"password != null\">, password='${password}'</if>" +
            "<if test=\"username != null\">, username='${username}'</if>" +
            "WHERE id=#{id}" +
            " </script>"})
    int update(User user);

    @Delete("DELETE FROM user WHERE id != 1 and id=#{id}")
    int delete(int id);

    @Select("select u.*, r.id as roleID, r.name as roleName, r.authority as roleAuthority , r.createTime as roleCreateTime , r.updateTime as roleUpdateTime FROM user u, user_role r WHERE u.roleId=r.id and u.username=#{username} AND u.password=#{password}")
    @Results(id = "roleMap", value = {
            @Result(column = "roleID", property = "role.id"),
            @Result(column = "roleName", property = "role.name"),
            @Result(column = "roleAuthority", property = "role.authority"),
            @Result(column = "roleCreateTime", property = "role.createTime"),
            @Result(column = "roleUpdateTime", property = "role.updateTime")
    })
    User select(String username, String password);

    @Select("select u.*, r.id as roleID, r.name as roleName, r.authority as roleAuthority , r.createTime as roleCreateTime , r.updateTime as roleUpdateTime FROM user u, user_role r WHERE u.roleId=r.id and u.id=#{id}")
    @ResultMap(value="roleMap")
    User selectById(int id);

    @Select("select u.*, r.id as roleID, r.name as roleName, r.authority as roleAuthority , r.createTime as roleCreateTime , r.updateTime as roleUpdateTime FROM user u, user_role r WHERE u.roleId=r.id and u.username=#{username}")
    @ResultMap(value="roleMap")
    User getUserByUsername(String username);

    @Select("select u.*, r.id as roleID, r.name as roleName, r.authority as roleAuthority , r.createTime as roleCreateTime , r.updateTime as roleUpdateTime FROM user u, user_role r WHERE u.roleId=r.id")
    @ResultMap(value="roleMap")
    List<User> selectAll();

    @Select("select u.id, u.username,u.roleId, r.id as roleID, r.name as roleName, r.authority as roleAuthority , r.createTime as roleCreateTime , r.updateTime as roleUpdateTime FROM user u join user_role r on u.roleId=r.id")
    @ResultMap(value="roleMap")
    List<User> getUsers();
}
