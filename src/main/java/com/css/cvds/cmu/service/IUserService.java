package com.css.cvds.cmu.service;

import com.css.cvds.cmu.storager.dao.dto.User;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface IUserService {

    User getUser(String username, String password);

    boolean changePassword(int id, String password);

    User getUserByUsername(String username);

    int addUser(User user);

    User getUser(int id);

    int deleteUser(int id);

    List<User> getAllUsers();

    int updateUsers(User user);

    PageInfo<User> getUsers(int page, int count);
}
