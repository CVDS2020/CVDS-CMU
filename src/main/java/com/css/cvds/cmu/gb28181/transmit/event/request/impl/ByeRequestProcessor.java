package com.css.cvds.cmu.gb28181.transmit.event.request.impl;

import com.css.cvds.cmu.common.StreamInfo;
import com.css.cvds.cmu.conf.exception.SsrcTransactionNotFoundException;
import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.gb28181.bean.InviteStreamType;
import com.css.cvds.cmu.gb28181.bean.SendRtpItem;
import com.css.cvds.cmu.gb28181.bean.SsrcTransaction;
import com.css.cvds.cmu.gb28181.session.VideoStreamSessionManager;
import com.css.cvds.cmu.gb28181.transmit.SIPProcessorObserver;
import com.css.cvds.cmu.gb28181.transmit.cmd.ISIPCommander;
import com.css.cvds.cmu.gb28181.transmit.event.request.ISIPRequestProcessor;
import com.css.cvds.cmu.gb28181.transmit.event.request.SIPRequestProcessorParent;
import com.css.cvds.cmu.media.css.MediaServerItem;
import com.css.cvds.cmu.service.IDeviceService;
import com.css.cvds.cmu.service.IMediaServerService;
import com.css.cvds.cmu.service.bean.MessageForPushChannel;
import com.css.cvds.cmu.storager.IRedisCatchStorage;
import com.css.cvds.cmu.storager.IVideoManagerStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.*;
import javax.sip.address.SipURI;
import javax.sip.header.CallIdHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderAddress;
import javax.sip.header.ToHeader;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * SIP命令类型： BYE请求
 */
@Component
public class ByeRequestProcessor extends SIPRequestProcessorParent implements InitializingBean, ISIPRequestProcessor {

	private final Logger logger = LoggerFactory.getLogger(ByeRequestProcessor.class);
	private final String method = "BYE";

	@Autowired
	private ISIPCommander cmder;

	@Autowired
	private IRedisCatchStorage redisCatchStorage;

	@Autowired
	private IDeviceService deviceService;

	@Autowired
	private IVideoManagerStorage storager;

	@Autowired
	private IMediaServerService mediaServerService;

	@Autowired
	private SIPProcessorObserver sipProcessorObserver;

	@Autowired
	private VideoStreamSessionManager streamSession;

	@Override
	public void afterPropertiesSet() throws Exception {
		// 添加消息处理的订阅
		sipProcessorObserver.addRequestProcessor(method, this);
	}

	/**
	 * 处理BYE请求
	 * @param evt
	 */
	@Override
	public void process(RequestEvent evt) {

		try {
			responseAck(getServerTransaction(evt), Response.OK);
		} catch (SipException | InvalidArgumentException | ParseException e) {
			logger.error("[回复BYE信息失败]，{}", e.getMessage());
		}
		CallIdHeader callIdHeader = (CallIdHeader)evt.getRequest().getHeader(CallIdHeader.NAME);
		String platformGbId = ((SipURI) ((HeaderAddress) evt.getRequest().getHeader(FromHeader.NAME)).getAddress().getURI()).getUser();
		String channelId = ((SipURI) ((HeaderAddress) evt.getRequest().getHeader(ToHeader.NAME)).getAddress().getURI()).getUser();
		// 可能是设备主动停止
		Device device = storager.queryVideoDeviceByChannelId(platformGbId);
		if (device != null) {
			storager.stopPlay(device.getDeviceId(), channelId);
			StreamInfo streamInfo = redisCatchStorage.queryPlayByDevice(device.getDeviceId(), channelId);
			if (streamInfo != null) {
				redisCatchStorage.stopPlay(streamInfo);
			}
			SsrcTransaction ssrcTransactionForPlay = streamSession.getSsrcTransaction(device.getDeviceId(), channelId, "play", null);
			if (ssrcTransactionForPlay != null){
				if (ssrcTransactionForPlay.getCallId().equals(callIdHeader.getCallId())){
					// 释放ssrc
					MediaServerItem mediaServerItem = mediaServerService.getOne(ssrcTransactionForPlay.getMediaServerId());
					if (mediaServerItem != null) {
						mediaServerService.releaseSsrc(mediaServerItem.getId(), ssrcTransactionForPlay.getSsrc());
					}
					streamSession.remove(device.getDeviceId(), channelId, ssrcTransactionForPlay.getStream());
				}
			}
			SsrcTransaction ssrcTransactionForPlayBack = streamSession.getSsrcTransaction(device.getDeviceId(), channelId, callIdHeader.getCallId(), null);
			if (ssrcTransactionForPlayBack != null) {
				// 释放ssrc
				MediaServerItem mediaServerItem = mediaServerService.getOne(ssrcTransactionForPlayBack.getMediaServerId());
				if (mediaServerItem != null) {
					mediaServerService.releaseSsrc(mediaServerItem.getId(), ssrcTransactionForPlayBack.getSsrc());
				}
				streamSession.remove(device.getDeviceId(), channelId, ssrcTransactionForPlayBack.getStream());
			}
		}
	}
}
