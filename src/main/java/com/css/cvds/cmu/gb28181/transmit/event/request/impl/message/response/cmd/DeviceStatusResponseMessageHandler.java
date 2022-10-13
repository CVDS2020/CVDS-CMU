package com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.response.cmd;

import com.alibaba.fastjson.JSONObject;
import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.gb28181.bean.ParentPlatform;
import com.css.cvds.cmu.gb28181.transmit.callback.DeferredResultHolder;
import com.css.cvds.cmu.gb28181.transmit.callback.RequestMessage;
import com.css.cvds.cmu.gb28181.transmit.event.request.SIPRequestProcessorParent;
import com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.IMessageHandler;
import com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.response.ResponseMessageHandler;
import com.css.cvds.cmu.gb28181.utils.XmlUtil;
import com.css.cvds.cmu.service.IDeviceService;
import com.css.cvds.cmu.storager.IRedisCatchStorage;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Response;
import java.text.ParseException;

@Component
public class DeviceStatusResponseMessageHandler extends SIPRequestProcessorParent implements InitializingBean, IMessageHandler {

    private Logger logger = LoggerFactory.getLogger(DeviceStatusResponseMessageHandler.class);
    private final String cmdType = "DeviceStatus";

    @Autowired
    private ResponseMessageHandler responseMessageHandler;

    @Autowired
    private DeferredResultHolder deferredResultHolder;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IRedisCatchStorage redisCatchStorage;

    @Override
    public void afterPropertiesSet() throws Exception {
        responseMessageHandler.addHandler(cmdType, this);
    }

    @Override
    public void handForDevice(RequestEvent evt, Device device, Element element) {
        logger.info("接收到DeviceStatus应答消息");
        // 检查设备是否存在， 不存在则不回复
        if (device == null) {
            return;
        }
        // 回复200 OK
        try {
            responseAck(getServerTransaction(evt), Response.OK);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            logger.error("[命令发送失败] 国标级联 设备状态应答回复200OK: {}", e.getMessage());
        }
        Element deviceIdElement = element.element("DeviceID");
        Element onlineElement = element.element("Online");
        String channelId = deviceIdElement.getText();
        JSONObject json = new JSONObject();
        XmlUtil.node2Json(element, json);
        if (logger.isDebugEnabled()) {
            logger.debug(json.toJSONString());
        }
        String text = onlineElement.getText();
        if ("ONLINE".equalsIgnoreCase(text.trim())) {
            deviceService.online(device);
        }else {
            deviceService.offline(device.getDeviceId());
        }
        RequestMessage msg = new RequestMessage();
        msg.setKey(DeferredResultHolder.CALLBACK_CMD_DEVICESTATUS + device.getDeviceId());
        msg.setData(json);
        deferredResultHolder.invokeAllResult(msg);
    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatform parentPlatform, Element rootElement) {


    }
}
