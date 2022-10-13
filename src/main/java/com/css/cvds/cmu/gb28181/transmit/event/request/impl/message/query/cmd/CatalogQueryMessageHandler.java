package com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.query.cmd;

import com.css.cvds.cmu.conf.SipConfig;
import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.gb28181.bean.DeviceChannel;
import com.css.cvds.cmu.gb28181.bean.ParentPlatform;
import com.css.cvds.cmu.gb28181.bean.*;
import com.css.cvds.cmu.gb28181.event.EventPublisher;
import com.css.cvds.cmu.gb28181.transmit.callback.DeferredResultHolder;
import com.css.cvds.cmu.gb28181.transmit.cmd.impl.SIPCommanderFroPlatform;
import com.css.cvds.cmu.gb28181.transmit.event.request.SIPRequestProcessorParent;
import com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.IMessageHandler;
import com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.query.QueryMessageHandler;
import com.css.cvds.cmu.storager.IVideoManagerStorage;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.header.FromHeader;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CatalogQueryMessageHandler extends SIPRequestProcessorParent implements InitializingBean, IMessageHandler {

    private Logger logger = LoggerFactory.getLogger(CatalogQueryMessageHandler.class);
    private final String cmdType = "Catalog";

    @Autowired
    private QueryMessageHandler queryMessageHandler;

    @Autowired
    private IVideoManagerStorage storager;

    @Autowired
    private SIPCommanderFroPlatform cmderFroPlatform;

    @Autowired
    private SipConfig config;

    @Autowired
    private EventPublisher publisher;

    @Autowired
    private IVideoManagerStorage storage;

    @Override
    public void afterPropertiesSet() throws Exception {
        queryMessageHandler.addHandler(cmdType, this);
    }

    @Override
    public void handForDevice(RequestEvent evt, Device device, Element element) {

    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatform parentPlatform, Element rootElement) {

        String key = DeferredResultHolder.CALLBACK_CMD_CATALOG + parentPlatform.getServerGBId();
        FromHeader fromHeader = (FromHeader) evt.getRequest().getHeader(FromHeader.NAME);
        try {
            // 回复200 OK
            responseAck(getServerTransaction(evt), Response.OK);
            Element snElement = rootElement.element("SN");
            String sn = snElement.getText();
            // 回复无通道
            cmderFroPlatform.catalogQuery(null, parentPlatform, sn, fromHeader.getTag(), 0);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            logger.error("[命令发送失败] 国标级联 目录查询: {}", e.getMessage());
        }

    }

    private DeviceChannel getChannelForPlatform(ParentPlatform platform) {
        DeviceChannel deviceChannel = new DeviceChannel();

        deviceChannel.setChannelId(platform.getDeviceGBId());
        deviceChannel.setName(platform.getName());
        deviceChannel.setManufacture("wvp-pro");
        deviceChannel.setOwner("wvp-pro");
        deviceChannel.setCivilCode(platform.getAdministrativeDivision());
        deviceChannel.setAddress("wvp-pro");
        deviceChannel.setRegisterWay(0);
        deviceChannel.setSecrecy("0");

        return deviceChannel;
    }
}
