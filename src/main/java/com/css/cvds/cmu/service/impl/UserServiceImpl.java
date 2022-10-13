package com.css.cvds.cmu.service.impl;

import com.css.cvds.cmu.storager.dao.UserMapper;
import com.css.cvds.cmu.storager.dao.dto.User;
import com.css.cvds.cmu.service.IUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUser(String username, String password) {
        return userMapper.select(username, password);
    }

    @Override
    public boolean changePassword(int id, String password) {
        User user = userMapper.selectById(id);
        user.setPassword(password);
        return userMapper.update(user) > 0;
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    @Override
    public int addUser(User user) {
        User userByUsername = userMapper.getUserByUsername(user.getUsername());
        if (userByUsername != null) {
            return 0;
        }
        return userMapper.add(user);
    }

    @Override
    public User getUser(int id) {
        return userMapper.selectById(id);
    }

    @Override
    public int deleteUser(int id) {
        return userMapper.delete(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    public int updateUsers(User user) {
        return userMapper.update(user);
    }

    @Override
    public PageInfo<User> getUsers(int page, int count) {
        PageHelper.startPage(page, count);
        List<User> users = userMapper.getUsers();
        return new PageInfo<>(users);
    }
}
