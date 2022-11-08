package com.css.cvds.cmu.service.impl;

import com.css.cvds.cmu.service.ITrainService;
import com.css.cvds.cmu.service.bean.GPSMsgInfo;
import com.css.cvds.cmu.storager.dao.TrainMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chend
 */
@Service
public class TrainServiceImpl implements ITrainService {

    /**
     * 机车默认ID
     */
    static final Integer DEFAULT_ID = 1;

    @Autowired
    private TrainMapper trainMapper;

    /**
     * 列车
     * @return  trainDto 列车
     */
    @Override
    public GPSMsgInfo.Train get() {
        return trainMapper.getById(DEFAULT_ID);
    }
}
