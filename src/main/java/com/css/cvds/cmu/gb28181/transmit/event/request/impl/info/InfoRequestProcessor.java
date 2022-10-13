package com.css.cvds.cmu.gb28181.transmit.event.request.impl.info;

import com.css.cvds.cmu.common.StreamInfo;
import com.css.cvds.cmu.gb28181.bean.*;
import com.css.cvds.cmu.gb28181.bean.*;
import com.css.cvds.cmu.gb28181.event.SipSubscribe;
import com.css.cvds.cmu.gb28181.session.VideoStreamSessionManager;
import com.css.cvds.cmu.gb28181.transmit.SIPProcessorObserver;
import com.css.cvds.cmu.gb28181.transmit.cmd.impl.SIPCommander;
import com.css.cvds.cmu.gb28181.transmit.event.request.ISIPRequestProcessor;
import com.css.cvds.cmu.gb28181.transmit.event.request.SIPRequestProcessorParent;
import com.css.cvds.cmu.gb28181.utils.SipUtils;
import com.css.cvds.cmu.storager.IRedisCatchStorage;
import com.css.cvds.cmu.storager.IVideoManagerStorage;
import gov.nist.javax.sip.message.SIPRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.header.*;
import javax.sip.message.Response;
import java.text.ParseException;

@Component
public class InfoRequestProcessor extends SIPRequestProcessorParent implements InitializingBean, ISIPRequestProcessor {

    private final static Logger logger = LoggerFactory.getLogger(InfoRequestProcessor.class);

    private final String method = "INFO";

    @Autowired
    private SIPProcessorObserver sipProcessorObserver;

    @Autowired
    private IVideoManagerStorage storage;

    @Autowired
    private SipSubscribe sipSubscribe;

    @Autowired
    private IRedisCatchStorage redisCatchStorage;

    @Autowired
    private IVideoManagerStorage storager;

    @Autowired
    private SIPCommander cmder;

    @Autowired
    private VideoStreamSessionManager sessionManager;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 添加消息处理的订阅
        sipProcessorObserver.addRequestProcessor(method, this);
    }

    @Override
    public void process(RequestEvent evt) {
        logger.debug("接收到消息：" + evt.getRequest());
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
