package com.css.cvds.cmu.service.impl;

import com.css.cvds.cmu.gb28181.bean.SuperviseTarget;
import com.css.cvds.cmu.gb28181.bean.SuperviseTargetType;
import com.css.cvds.cmu.service.ISuperviseTarget;
import com.css.cvds.cmu.storager.dao.SuperviseTargetMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chend
 */
@Service
public class SuperviseTargetImpl implements ISuperviseTarget {

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
}
