package com.css.cvds.cmu.service;

import com.css.cvds.cmu.gb28181.bean.SuperviseTarget;
import com.css.cvds.cmu.gb28181.bean.SuperviseTargetType;
import com.css.cvds.cmu.gb28181.bean.Train;

import java.util.List;

/**
 * 监视物
 */
public interface ISuperviseTarget {

    /**
     * 监视物类型列表
     * @return  SuperviseTargetType
     */
    List<SuperviseTargetType> getTypeList();

    /**
     * 监视物列表
     * @return  SuperviseTarget
     */
    List<SuperviseTarget> getList(Integer type);
}
