package com.css.cvds.cmu.service.impl;

import com.css.cvds.cmu.service.bean.SuperviseTarget;
import com.css.cvds.cmu.service.bean.SuperviseTargetType;
import com.css.cvds.cmu.service.ISuperviseTargetService;
import com.css.cvds.cmu.storager.dao.SuperviseTargetMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chend
 */
@Service
public class SuperviseTargetImplService implements ISuperviseTargetService {

    @Resource
    private SuperviseTargetMapper superviseTargetMapper;

    @Override
    public List<SuperviseTargetType> getTypeList() {
        return superviseTargetMapper.getTypeList();
    }

    @Override
    public List<SuperviseTarget> getList(Integer type) {
        return superviseTargetMapper.getList(type);
    }

    @Override
    public SuperviseTarget getById(Integer id) {
        return superviseTargetMapper.getById(id);
    }

}
