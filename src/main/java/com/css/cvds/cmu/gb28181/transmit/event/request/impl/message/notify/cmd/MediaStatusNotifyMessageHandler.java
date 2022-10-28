package com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.notify.cmd;

import com.css.cvds.cmu.common.StreamInfo;
import com.css.cvds.cmu.conf.exception.SsrcTransactionNotFoundException;
import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.gb28181.bean.ParentPlatform;
import com.css.cvds.cmu.gb28181.bean.SendRtpItem;
import com.css.cvds.cmu.gb28181.bean.SsrcTransaction;
import com.css.cvds.cmu.gb28181.session.VideoStreamSessionManager;
import com.css.cvds.cmu.gb28181.transmit.cmd.impl.SIPCommander;
import com.css.cvds.cmu.gb28181.transmit.cmd.impl.SIPCommanderFroPlatform;
import com.css.cvds.cmu.gb28181.transmit.event.request.SIPRequestProcessorParent;
import com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.IMessageHandler;
import com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.notify.NotifyMessageHandler;
import com.css.cvds.cmu.storager.IRedisCatchStorage;
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
import javax.sip.header.CallIdHeader;
import javax.sip.message.Response;
import java.text.ParseException;

import static com.css.cvds.cmu.gb28181.utils.XmlUtil.getText;

/**
 * 媒体通知
 */
@Component
public class MediaStatusNotifyMessageHandler extends SIPRequestProcessorParent implements InitializingBean, IMessageHandler {

    private Logger logger = LoggerFactory.getLogger(MediaStatusNotifyMessageHandler.class);
    private final String cmdType = "MediaStatus";

    @Autowired
    private NotifyMessageHandler notifyMessageHandler;

    @Autowired
    private SIPCommander cmder;

    @Autowired
    private SIPCommanderFroPlatform sipCommanderFroPlatform;

    @Autowired
    private IRedisCatchStorage redisCatchStorage;

    @Autowired
    private IVideoManagerStorage storage;

    @Autowired
    private VideoStreamSessionManager sessionManager;

    @Override
    public void afterPropertiesSet() throws Exception {
        notifyMessageHandler.addHandler(cmdType, this);
    }

    @Override
    public void handForDevice(RequestEvent evt, Device device, Element rootElement) {
        // 回复200 OK
        try {
            responseAck(getServerTransaction(evt), Response.OK);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            logger.error("[命令发送失败] 国标级联 录像流推送完毕，回复200OK: {}", e.getMessage());
        }
        CallIdHeader callIdHeader = (CallIdHeader)evt.getRequest().getHeader(CallIdHeader.NAME);
        String NotifyType =getText(rootElement, "NotifyType");
        if ("121".equals(NotifyType)){
            logger.info("[录像流]推送完毕，收到关流通知");
            // 先从会话内查找
            SsrcTransaction ssrcTransaction = sessionManager.getSsrcTransaction(null, null, callIdHeader.getCallId(), null);
            // 兼容海康 媒体通知 消息from字段不是设备ID的问题
            if (ssrcTransaction != null) {
                try {
                    cmder.streamByeCmd(device, ssrcTransaction.getChannelId(), null, callIdHeader.getCallId());
                } catch (InvalidArgumentException | ParseException | SsrcTransactionNotFoundException | SipException e) {
                    logger.error("[录像流]推送完毕，收到关流通知， 发送BYE失败 {}", e.getMessage());
                }
            }
        }
    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatform parentPlatform, Element element) {

    }
}
