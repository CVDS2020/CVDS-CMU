package com.css.cvds.cmu.service.impl;

import com.css.cvds.cmu.service.ITrainService;
import com.css.cvds.cmu.storager.dao.TrainMapper;
import com.css.cvds.cmu.storager.dao.dto.TrainDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author chend
 */
@Service
public class TrainServiceImpl implements ITrainService {

    /**
     * 机车默认ID
     */
    static final Integer DEFAULT_ID = 1;

    static TrainDto defaultTrain = null;

    @Autowired
    private TrainMapper trainMapper;

    /**
     * 列车
     * @param trainDto 列车
     */
    @Override
    public void update(TrainDto trainDto) {
        if (Objects.nonNull(trainDto.getSerial()) ||
                Objects.nonNull(trainDto.getTrainNo()) ||
                Objects.nonNull(trainDto.getName()) ||
                Objects.nonNull(trainDto.getDescription())) {

            trainDto.setId(DEFAULT_ID);
            trainMapper.update(trainDto);
            // 重新获取
            defaultTrain = trainMapper.getById(DEFAULT_ID);
        }
    }

    /**
     * 列车
     * @return  trainDto 列车
     */
    @Override
    public TrainDto get() {
        if (Objects.nonNull(defaultTrain)) {
            return defaultTrain;
        }
        defaultTrain = trainMapper.getById(DEFAULT_ID);
        if (Objects.isNull(defaultTrain)) {
            defaultTrain = new TrainDto();
            defaultTrain.setId(DEFAULT_ID);
            defaultTrain.setSerial("未定义");
            defaultTrain.setTrainNo("未定义");
            defaultTrain.setName("未定义");
            defaultTrain.setDescription("");

            trainMapper.add(defaultTrain);
        }
        return defaultTrain;
    }
}
