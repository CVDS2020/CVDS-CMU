package com.css.cvds.cmu.gb28181.transmit.event.request.impl;

import com.alibaba.fastjson.JSONObject;
import com.css.cvds.cmu.conf.DynamicTask;
import com.css.cvds.cmu.gb28181.bean.ParentPlatform;
import com.css.cvds.cmu.gb28181.bean.SendRtpItem;
import com.css.cvds.cmu.gb28181.transmit.SIPProcessorObserver;
import com.css.cvds.cmu.gb28181.transmit.cmd.ISIPCommander;
import com.css.cvds.cmu.gb28181.transmit.cmd.ISIPCommanderForPlatform;
import com.css.cvds.cmu.gb28181.transmit.event.request.ISIPRequestProcessor;
import com.css.cvds.cmu.gb28181.transmit.event.request.SIPRequestProcessorParent;
import com.css.cvds.cmu.media.css.MediaServerItem;
import com.css.cvds.cmu.service.IMediaServerService;
import com.css.cvds.cmu.service.bean.RequestPushStreamMsg;
import com.css.cvds.cmu.storager.IRedisCatchStorage;
import com.css.cvds.cmu.storager.IVideoManagerStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.address.SipURI;
import javax.sip.header.CallIdHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderAddress;
import javax.sip.header.ToHeader;
import java.text.ParseException;
import java.util.*;

/**
 * SIP命令类型： ACK请求
 */
@Component
public class AckRequestProcessor extends SIPRequestProcessorParent implements InitializingBean, ISIPRequestProcessor {

	private Logger logger = LoggerFactory.getLogger(AckRequestProcessor.class);
	private final String method = "ACK";

	@Autowired
	private SIPProcessorObserver sipProcessorObserver;

	@Override
	public void afterPropertiesSet() throws Exception {
		// 添加消息处理的订阅
		sipProcessorObserver.addRequestProcessor(method, this);
	}

	@Autowired
    private IRedisCatchStorage redisCatchStorage;

	@Autowired
	private IVideoManagerStorage storager;

	@Autowired
	private DynamicTask dynamicTask;

	@Autowired
	private ISIPCommander cmder;

	/**   
	 * 处理  ACK请求
	 * 
	 * @param evt
	 */
	@Override
	public void process(RequestEvent evt) {
		CallIdHeader callIdHeader = (CallIdHeader)evt.getRequest().getHeader(CallIdHeader.NAME);

		String platformGbId = ((SipURI) ((HeaderAddress) evt.getRequest().getHeader(FromHeader.NAME)).getAddress().getURI()).getUser();
		logger.info("[收到ACK]： platformGbId->{}", platformGbId);
		// 取消设置的超时任务
		dynamicTask.stop(callIdHeader.getCallId());
	}
}
