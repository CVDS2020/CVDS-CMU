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
public class DeviceControlResponseMessageHandler extends SIPRequestProcessorParent implements InitializingBean, IMessageHandler {

    private Logger logger = LoggerFactory.getLogger(DeviceControlResponseMessageHandler.class);
    private final String cmdType = "DeviceControl";

    @Autowired
    private ResponseMessageHandler responseMessageHandler;

    @Autowired
    private DeferredResultHolder deferredResultHolder;

    @Override
    public void afterPropertiesSet() throws Exception {
        responseMessageHandler.addHandler(cmdType, this);
    }

    @Override
    public void handForDevice(RequestEvent evt, Device device, Element element) {
        // 此处是对本平台发出DeviceControl指令的应答
        try {
            responseAck(getServerTransaction(evt), Response.OK);
            JSONObject json = new JSONObject();
            String channelId = XmlUtil.getText(element, "DeviceID");
            XmlUtil.node2Json(element, json);
            if (logger.isDebugEnabled()) {
                logger.debug(json.toJSONString());
            }
            RequestMessage msg = new RequestMessage();
            String key = DeferredResultHolder.CALLBACK_CMD_DEVICECONTROL +  device.getDeviceId() + channelId;
            msg.setKey(key);
            msg.setData(json);
            deferredResultHolder.invokeAllResult(msg);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            logger.error("[命令发送失败] 国标级联 设备控制: {}", e.getMessage());
        }
    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatform parentPlatform, Element rootElement) {
    }
}
