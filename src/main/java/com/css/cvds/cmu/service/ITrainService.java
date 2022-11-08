package com.css.cvds.cmu.service;

import com.css.cvds.cmu.service.bean.GPSMsgInfo;

/**
 * 列车
 */
public interface ITrainService {

    /**
     * 列车
     * @return  train 列车
     */
    GPSMsgInfo.Train get();

}
