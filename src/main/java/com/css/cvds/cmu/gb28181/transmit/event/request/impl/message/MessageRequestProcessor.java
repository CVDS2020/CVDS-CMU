package com.css.cvds.cmu.gb28181.transmit.event.request.impl.message;

import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.gb28181.bean.DeviceNotFoundEvent;
import com.css.cvds.cmu.gb28181.bean.ParentPlatform;
import com.css.cvds.cmu.gb28181.bean.SsrcTransaction;
import com.css.cvds.cmu.gb28181.event.SipSubscribe;
import com.css.cvds.cmu.gb28181.session.VideoStreamSessionManager;
import com.css.cvds.cmu.gb28181.transmit.SIPProcessorObserver;
import com.css.cvds.cmu.gb28181.transmit.event.request.ISIPRequestProcessor;
import com.css.cvds.cmu.gb28181.transmit.event.request.SIPRequestProcessorParent;
import com.css.cvds.cmu.gb28181.utils.SipUtils;
import com.css.cvds.cmu.storager.IRedisCatchStorage;
import com.css.cvds.cmu.storager.IVideoManagerStorage;
import gov.nist.javax.sip.message.SIPRequest;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageRequestProcessor extends SIPRequestProcessorParent implements InitializingBean, ISIPRequestProcessor {

    private final static Logger logger = LoggerFactory.getLogger(MessageRequestProcessor.class);

    private final String method = "MESSAGE";

    private static Map<String, IMessageHandler> messageHandlerMap = new ConcurrentHashMap<>();

    @Autowired
    private SIPProcessorObserver sipProcessorObserver;

    @Autowired
    private IVideoManagerStorage storage;

    @Autowired
    private SipSubscribe sipSubscribe;

    @Autowired
    private IRedisCatchStorage redisCatchStorage;

    @Autowired
    private VideoStreamSessionManager sessionManager;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 添加消息处理的订阅
        sipProcessorObserver.addRequestProcessor(method, this);
    }

    public void addHandler(String name, IMessageHandler handler) {
        messageHandlerMap.put(name, handler);
    }

    @Override
    public void process(RequestEvent evt) {
        SIPRequest sipRequest = (SIPRequest)evt.getRequest();
        logger.debug("接收到消息：" + evt.getRequest());
        String deviceId = SipUtils.getUserIdFromFromHeader(evt.getRequest());
        CallIdHeader callIdHeader = sipRequest.getCallIdHeader();
        // 先从会话内查找
        SsrcTransaction ssrcTransaction = sessionManager.getSsrcTransaction(null, null, callIdHeader.getCallId(), null);
        // 兼容海康 媒体通知 消息from字段不是设备ID的问题
        if (ssrcTransaction != null) {
            deviceId = ssrcTransaction.getDeviceId();
        }
        ServerTransaction serverTransaction = getServerTransaction(evt);
        try {
            responseAck(serverTransaction, Response.UNSUPPORTED_MEDIA_TYPE, "Unsupported message type, must Control/Notify/Query/Response");
        } catch (SipException e) {
            logger.warn("SIP 回复错误", e);
        } catch (InvalidArgumentException e) {
            logger.warn("参数无效", e);
        } catch (ParseException e) {
            logger.warn("SIP回复时解析异常", e);
        }
    }
}
