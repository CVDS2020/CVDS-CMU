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
import com.css.cvds.cmu.web.bean.WVPResult;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.RequestEvent;

@Component
public class DeviceConfigResponseMessageHandler extends SIPRequestProcessorParent implements InitializingBean, IMessageHandler {

    private Logger logger = LoggerFactory.getLogger(DeviceConfigResponseMessageHandler.class);
    private final String cmdType = "DeviceConfig";

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
        JSONObject json = new JSONObject();
        XmlUtil.node2Json(element, json);
        String channelId = XmlUtil.getText(element, "DeviceID");
        if (logger.isDebugEnabled()) {
            logger.debug(json.toJSONString());
        }
        String key = DeferredResultHolder.CALLBACK_CMD_DEVICECONFIG + device.getDeviceId() + channelId;
        RequestMessage msg = new RequestMessage();
        msg.setKey(key);
        msg.setData(WVPResult.success(json));
        deferredResultHolder.invokeAllResult(msg);
    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatform parentPlatform, Element rootElement) {
    }
}
