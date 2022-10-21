package com.css.cvds.cmu.service;

import com.css.cvds.cmu.storager.dao.dto.AccessLogDto;
import com.css.cvds.cmu.storager.dao.dto.LogDto;
import com.css.cvds.cmu.storager.dao.dto.TrainDto;
import com.github.pagehelper.PageInfo;

/**
 * 列车
 */
public interface ITrainService {

    /**
     * 列车
     * @param trainDto 列车
     *
     */
    void update(TrainDto trainDto);

    /**
     * 列车
     * @return  trainDto 列车
     */
    TrainDto get();

}
