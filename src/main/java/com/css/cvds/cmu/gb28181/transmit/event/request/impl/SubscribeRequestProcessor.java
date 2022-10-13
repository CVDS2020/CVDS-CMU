package com.css.cvds.cmu.gb28181.transmit.event.request.impl;

import com.css.cvds.cmu.gb28181.bean.CmdType;
import com.css.cvds.cmu.gb28181.bean.ParentPlatform;
import com.css.cvds.cmu.gb28181.bean.SubscribeHolder;
import com.css.cvds.cmu.gb28181.bean.SubscribeInfo;
import com.css.cvds.cmu.gb28181.transmit.SIPProcessorObserver;
import com.css.cvds.cmu.gb28181.transmit.event.request.ISIPRequestProcessor;
import com.css.cvds.cmu.gb28181.transmit.event.request.SIPRequestProcessorParent;
import com.css.cvds.cmu.gb28181.utils.SipUtils;
import com.css.cvds.cmu.gb28181.utils.XmlUtil;
import com.css.cvds.cmu.storager.IVideoManagerStorage;
import gov.nist.javax.sip.message.SIPResponse;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.*;
import javax.sip.header.ExpiresHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;

/**
 * SIP命令类型： SUBSCRIBE请求
 * @author lin
 */
@Component
public class SubscribeRequestProcessor extends SIPRequestProcessorParent implements InitializingBean, ISIPRequestProcessor {

	private final Logger logger = LoggerFactory.getLogger(SubscribeRequestProcessor.class);
	private final String method = "SUBSCRIBE";

	@Autowired
	private SIPProcessorObserver sipProcessorObserver;

	@Autowired
	private IVideoManagerStorage storager;

	@Autowired
	private SubscribeHolder subscribeHolder;

	@Override
	public void afterPropertiesSet() throws Exception {
		// 添加消息处理的订阅
		sipProcessorObserver.addRequestProcessor(method, this);
	}

	/**   
	 * 处理SUBSCRIBE请求  
	 * 
	 * @param evt 事件
	 */
	@Override
	public void process(RequestEvent evt) {
		ServerTransaction serverTransaction = getServerTransaction(evt);
		Request request = evt.getRequest();
		try {
			Element rootElement = getRootElement(evt);
			if (rootElement == null) {
				logger.error("处理SUBSCRIBE请求  未获取到消息体{}", evt.getRequest());
				return;
			}
			String cmd = XmlUtil.getText(rootElement, "CmdType");
			if (CmdType.MOBILE_POSITION.equals(cmd)) {
				processNotifyMobilePosition(serverTransaction, rootElement);
//			} else if (CmdType.ALARM.equals(cmd)) {
//				logger.info("接收到Alarm订阅");
//				processNotifyAlarm(serverTransaction, rootElement);
			} else if (CmdType.CATALOG.equals(cmd)) {
				processNotifyCatalogList(serverTransaction, rootElement);
			} else {
				logger.info("接收到消息：" + cmd);

				Response response = getMessageFactory().createResponse(200, request);
				if (response != null) {
					ExpiresHeader expireHeader = getHeaderFactory().createExpiresHeader(30);
					response.setExpires(expireHeader);
				}
				logger.info("response : " + response);
				ServerTransaction transaction = getServerTransaction(evt);
				if (transaction != null) {
					transaction.sendResponse(response);
					transaction.terminate();
				} else {
					logger.info("processRequest serverTransactionId is null.");
				}
			}
		} catch (ParseException | SipException | InvalidArgumentException | DocumentException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 处理移动位置订阅消息
	 */
	private void processNotifyMobilePosition(ServerTransaction serverTransaction, Element rootElement) throws SipException {

	}

	private void processNotifyAlarm(RequestEvent evt, Element rootElement) {

	}

	private void processNotifyCatalogList(ServerTransaction serverTransaction, Element rootElement) throws SipException {

	}
}
