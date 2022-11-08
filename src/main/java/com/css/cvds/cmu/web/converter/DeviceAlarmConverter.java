package com.css.cvds.cmu.web.converter;

import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.gb28181.bean.DeviceAlarm;
import com.css.cvds.cmu.web.bean.DeviceAlarmVO;
import com.css.cvds.cmu.web.bean.DeviceDTO;
import com.css.cvds.cmu.web.bean.DeviceDetailsVO;
import com.css.cvds.cmu.web.bean.DeviceVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author chendj
 * @date 2022年6月9日18:26:50
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface DeviceAlarmConverter {
    DeviceAlarmConverter INSTANCE = Mappers.getMapper(DeviceAlarmConverter.class);

    /**
     * to VO
     *
     * @param entity DeviceAlarm
     * @return DeviceAlarmVO
     */
    DeviceAlarmVO toVo(DeviceAlarm entity);

}
