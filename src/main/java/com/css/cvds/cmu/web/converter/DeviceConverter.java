package com.css.cvds.cmu.web.converter;

import com.css.cvds.cmu.gb28181.bean.Device;
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
public interface DeviceConverter {
    DeviceConverter INSTANCE = Mappers.getMapper(DeviceConverter.class);

    /**
     * to AddEncoderDTO
     *
     * @param dto DeviceDTO
     * @return Device
     */
    Device toDevice(DeviceDTO dto);

    /**
     * to VO
     *
     * @param entity Device
     * @return DeviceVO
     */
    DeviceVO toVo(Device entity);

    /**
     * to VO
     *
     * @param entity Device
     * @return DeviceDetailsVO
     */
    DeviceDetailsVO toDetailsVo(Device entity);

    /**
     * to VO
     *
     * @param entity Device
     * @return DeviceDetailsVO
     */
    DeviceDetailsVO toDetailsVo(DeviceVO entity);
}
