package com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.response.cmd;

import com.css.cvds.cmu.conf.SipConfig;
import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.gb28181.bean.ParentPlatform;
import com.css.cvds.cmu.gb28181.event.EventPublisher;
import com.css.cvds.cmu.gb28181.transmit.callback.DeferredResultHolder;
import com.css.cvds.cmu.gb28181.transmit.callback.RequestMessage;
import com.css.cvds.cmu.gb28181.transmit.event.request.SIPRequestProcessorParent;
import com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.IMessageHandler;
import com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.response.ResponseMessageHandler;
import com.css.cvds.cmu.gb28181.utils.XmlUtil;
import com.css.cvds.cmu.service.IDeviceService;
import com.css.cvds.cmu.storager.IRedisCatchStorage;
import com.css.cvds.cmu.storager.IVideoManagerStorage;
import com.css.cvds.cmu.web.bean.WVPResult;
import org.apache.logging.log4j.util.Strings;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.message.Response;
import java.text.ParseException;

/**
 * @author lin
 */
@Component
public class DeviceInfoResponseMessageHandler extends SIPRequestProcessorParent implements InitializingBean, IMessageHandler {

    private Logger logger = LoggerFactory.getLogger(DeviceInfoResponseMessageHandler.class);
    private final String cmdType = "DeviceInfo";

    @Autowired
    private ResponseMessageHandler responseMessageHandler;

    @Autowired
    private IVideoManagerStorage storager;

    @Autowired
    private IRedisCatchStorage redisCatchStorage;

    @Autowired
    private DeferredResultHolder deferredResultHolder;

    @Autowired
    private SipConfig config;

    @Autowired
    private EventPublisher publisher;

    @Autowired
    private IDeviceService deviceService;

    @Override
    public void afterPropertiesSet() throws Exception {
        responseMessageHandler.addHandler(cmdType, this);
    }

    @Override
    public void handForDevice(RequestEvent evt, Device device, Element rootElement) {
        logger.debug("接收到DeviceInfo应答消息");
        // 检查设备是否存在， 不存在则不回复
        if (device == null || device.getOnline() == 0) {
            logger.warn("[接收到DeviceInfo应答消息,但是设备已经离线]：" + (device != null ? device.getDeviceId():"" ));
            return;
        }
        ServerTransaction serverTransaction = getServerTransaction(evt);
        try {
            rootElement = getRootElement(evt, device.getCharset());
            if (rootElement == null) {
                logger.warn("[ 接收到DeviceInfo应答消息 ] content cannot be null, {}", evt.getRequest());
                responseAck(serverTransaction, Response.BAD_REQUEST);
                return;
            }
            Element deviceIdElement = rootElement.element("DeviceID");
            String channelId = deviceIdElement.getTextTrim();
            String key = DeferredResultHolder.CALLBACK_CMD_DEVICEINFO + device.getDeviceId() + channelId;
            device.setName(XmlUtil.getText(rootElement, "DeviceName"));

            device.setManufacturer(XmlUtil.getText(rootElement, "Manufacturer"));
            device.setModel(XmlUtil.getText(rootElement, "Model"));
            device.setFirmware(XmlUtil.getText(rootElement, "Firmware"));
            if (Strings.isEmpty(device.getStreamMode())) {
                device.setStreamMode("TCP-PASSIVE");
            }
            deviceService.updateDevice(device);

            RequestMessage msg = new RequestMessage();
            msg.setKey(key);
            msg.setData(WVPResult.success(device));
            deferredResultHolder.invokeAllResult(msg);
            // 回复200 OK
            responseAck(serverTransaction, Response.OK);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SipException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatform parentPlatform, Element rootElement) {

    }
}
