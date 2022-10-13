package com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.query.cmd;

import com.css.cvds.cmu.conf.SipConfig;
import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.gb28181.bean.DeviceChannel;
import com.css.cvds.cmu.gb28181.bean.ParentPlatform;
import com.css.cvds.cmu.gb28181.bean.*;
import com.css.cvds.cmu.gb28181.event.EventPublisher;
import com.css.cvds.cmu.gb28181.event.record.RecordEndEventListener;
import com.css.cvds.cmu.gb28181.transmit.cmd.impl.SIPCommander;
import com.css.cvds.cmu.gb28181.transmit.cmd.impl.SIPCommanderFroPlatform;
import com.css.cvds.cmu.gb28181.transmit.event.request.SIPRequestProcessorParent;
import com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.IMessageHandler;
import com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.query.QueryMessageHandler;
import com.css.cvds.cmu.utils.DateUtil;
import com.css.cvds.cmu.storager.IVideoManagerStorage;
import com.css.cvds.cmu.storager.dao.dto.ChannelSourceInfo;
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
import javax.sip.header.FromHeader;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.List;

@Component
public class RecordInfoQueryMessageHandler extends SIPRequestProcessorParent implements InitializingBean, IMessageHandler {

    private Logger logger = LoggerFactory.getLogger(RecordInfoQueryMessageHandler.class);
    private final String cmdType = "RecordInfo";

    @Autowired
    private QueryMessageHandler queryMessageHandler;

    @Autowired
    private IVideoManagerStorage storager;

    @Autowired
    private SIPCommanderFroPlatform cmderFroPlatform;

    @Autowired
    private SIPCommander commander;

    @Autowired
    private RecordEndEventListener recordEndEventListener;

    @Autowired
    private SipConfig config;

    @Autowired
    private EventPublisher publisher;

    @Override
    public void afterPropertiesSet() throws Exception {
        queryMessageHandler.addHandler(cmdType, this);
    }

    @Override
    public void handForDevice(RequestEvent evt, Device device, Element element) {

    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatform parentPlatform, Element rootElement) {
        ServerTransaction serverTransaction = getServerTransaction(evt);
        // 错误的请求
        try {
            responseAck(serverTransaction, Response.BAD_REQUEST);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            logger.error("[命令发送失败] 录像查询: {}", e.getMessage());
        }
    }
}
