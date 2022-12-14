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
        // ???????????????????????????
        sipProcessorObserver.addRequestProcessor(method, this);
    }

    public void addHandler(String name, IMessageHandler handler) {
        messageHandlerMap.put(name, handler);
    }

    @Override
    public void process(RequestEvent evt) {
        SIPRequest sipRequest = (SIPRequest)evt.getRequest();
        logger.debug("??????????????????" + evt.getRequest());
        String deviceId = SipUtils.getUserIdFromFromHeader(evt.getRequest());
        CallIdHeader callIdHeader = sipRequest.getCallIdHeader();
        // ?????????????????????
        SsrcTransaction ssrcTransaction = sessionManager.getSsrcTransaction(null, null, callIdHeader.getCallId(), null);
        // ???????????? ???????????? ??????from??????????????????ID?????????
        if (ssrcTransaction != null) {
            deviceId = ssrcTransaction.getDeviceId();
        }
        // ????????????????????????
        Device device = redisCatchStorage.getDevice(deviceId);
        ServerTransaction serverTransaction = getServerTransaction(evt);
        try {
            Element rootElement = null;
            try {
                rootElement = getRootElement(evt);
                if (rootElement == null) {
                    logger.error("??????MESSAGE??????  ?????????????????????{}", evt.getRequest());
                    responseAck(serverTransaction, Response.BAD_REQUEST, "content is null");
                    return;
                }
            } catch (DocumentException e) {
                logger.warn("??????XML??????????????????", e);
                // ??????????????????404
                responseAck(serverTransaction, Response.BAD_REQUEST, e.getMessage());
            }
            String name = rootElement.getName();
            IMessageHandler messageHandler = messageHandlerMap.get(name);
            if (messageHandler != null) {
                if (device != null) {
                    messageHandler.handForDevice(evt, device, rootElement);
                } else {
                    // ??????????????????????????????null??????????????????????????????device???parentPlatform??????????????????null
                    // messageHandler.handForPlatform(evt, parentPlatform, rootElement);
                }
            } else {
                // ????????????message
                // ??????????????????415
                responseAck(serverTransaction, Response.UNSUPPORTED_MEDIA_TYPE, "Unsupported message type, must Control/Notify/Query/Response");
            }
        } catch (SipException e) {
            logger.warn("SIP ????????????", e);
        } catch (InvalidArgumentException e) {
            logger.warn("????????????", e);
        } catch (ParseException e) {
            logger.warn("SIP?????????????????????", e);
        }
    }
}
