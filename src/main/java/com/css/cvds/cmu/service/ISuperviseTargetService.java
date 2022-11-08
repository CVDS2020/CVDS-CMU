package com.css.cvds.cmu.service;

import com.css.cvds.cmu.service.bean.SuperviseTarget;
import com.css.cvds.cmu.service.bean.SuperviseTargetType;

import java.util.List;

/**
 * 监视物
 */
public interface ISuperviseTargetService {

    /**
     * 监视物类型列表
     * @return  SuperviseTargetType
     */
    List<SuperviseTargetType> getTypeList();

    /**
     * 监视物列表
     * @param type 监视物类型
     * @return  SuperviseTarget
     */
    List<SuperviseTarget> getList(Integer type);

    /**
     * 监视物
     * @param id id
     * @return  SuperviseTarget
     */
    SuperviseTarget getById(Integer id);
}
