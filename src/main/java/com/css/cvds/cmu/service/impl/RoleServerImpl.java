package com.css.cvds.cmu.service.impl;

import com.css.cvds.cmu.storager.dao.RoleMapper;
import com.css.cvds.cmu.storager.dao.dto.Role;
import com.css.cvds.cmu.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServerImpl implements IRoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Role getRoleById(int id) {
        return roleMapper.selectById(id);
    }

    @Override
    public int add(Role role) {
        return roleMapper.add(role);
    }

    @Override
    public int delete(int id) {
        return roleMapper.delete(id);
    }

    @Override
    public List<Role> getAll() {
        return roleMapper.selectAll();
    }

    @Override
    public int update(Role role) {
        return roleMapper.update(role);
    }
}
