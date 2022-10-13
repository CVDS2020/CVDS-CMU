package com.css.cvds.cmu.gb28181.transmit.event.request.impl;

import com.css.cvds.cmu.gb28181.bean.*;
import com.css.cvds.cmu.media.css.MediaServerItem;
import com.css.cvds.cmu.conf.DynamicTask;
import com.css.cvds.cmu.conf.UserSetting;
import com.css.cvds.cmu.gb28181.session.VideoStreamSessionManager;
import com.css.cvds.cmu.gb28181.transmit.SIPProcessorObserver;
import com.css.cvds.cmu.gb28181.transmit.cmd.ISIPCommander;
import com.css.cvds.cmu.gb28181.transmit.cmd.impl.SIPCommanderFroPlatform;
import com.css.cvds.cmu.gb28181.transmit.event.request.ISIPRequestProcessor;
import com.css.cvds.cmu.gb28181.transmit.event.request.SIPRequestProcessorParent;
import com.css.cvds.cmu.gb28181.utils.SipUtils;
import com.css.cvds.cmu.service.IMediaServerService;
import com.css.cvds.cmu.service.IPlayService;
import com.css.cvds.cmu.service.redisMsg.RedisPushStreamResponseListener;
import com.css.cvds.cmu.storager.IRedisCatchStorage;
import com.css.cvds.cmu.storager.IVideoManagerStorage;
import gov.nist.javax.sdp.TimeDescriptionImpl;
import gov.nist.javax.sdp.fields.TimeField;
import gov.nist.javax.sip.message.SIPResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sdp.*;
import javax.sip.*;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.time.Instant;
import java.util.Vector;

/**
 * SIP命令类型： INVITE请求
 */
@SuppressWarnings("rawtypes")
@Component
public class InviteRequestProcessor extends SIPRequestProcessorParent implements InitializingBean, ISIPRequestProcessor {

    private final static Logger logger = LoggerFactory.getLogger(InviteRequestProcessor.class);

    private final String method = "INVITE";

    @Autowired
    private SIPCommanderFroPlatform cmderFroPlatform;

    @Autowired
    private IVideoManagerStorage storager;

    @Autowired
    private IRedisCatchStorage redisCatchStorage;

    @Autowired
    private DynamicTask dynamicTask;

    @Autowired
    private RedisPushStreamResponseListener redisPushStreamResponseListener;

    @Autowired
    private IPlayService playService;

    @Autowired
    private ISIPCommander commander;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private SIPProcessorObserver sipProcessorObserver;

    @Autowired
    private VideoStreamSessionManager sessionManager;

    @Autowired
    private UserSetting userSetting;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 添加消息处理的订阅
        sipProcessorObserver.addRequestProcessor(method, this);
    }

    /**
     * 处理invite请求
     *
     * @param evt 请求消息
     */
    @Override
    public void process(RequestEvent evt) {
        //  Invite Request消息实现，此消息一般为级联消息，上级给下级发送请求视频指令
        try {
            Request request = evt.getRequest();
            String channelId = SipUtils.getChannelIdFromRequest(request);
            String requesterId = SipUtils.getUserIdFromFromHeader(request);
            CallIdHeader callIdHeader = (CallIdHeader) request.getHeader(CallIdHeader.NAME);
            ServerTransaction serverTransaction = getServerTransaction(evt);
            if (requesterId == null || channelId == null) {
                logger.info("无法从FromHeader的Address中获取到平台id，返回400");
                // 参数不全， 发400，请求错误
                responseAck(serverTransaction, Response.BAD_REQUEST);
                return;
            }
            inviteFromDeviceHandle(serverTransaction, requesterId);

        } catch (SipException | InvalidArgumentException | ParseException e) {
            e.printStackTrace();
            logger.warn("sdp解析错误");
            e.printStackTrace();
        } catch (SdpParseException e) {
            e.printStackTrace();
        } catch (SdpException e) {
            e.printStackTrace();
        }
    }

    public SIPResponse sendStreamAck(MediaServerItem mediaServerItem, ServerTransaction serverTransaction, SendRtpItem sendRtpItem, ParentPlatform platform, RequestEvent evt) {

        StringBuffer content = new StringBuffer(200);
        content.append("v=0\r\n");
        content.append("o=" + sendRtpItem.getChannelId() + " 0 0 IN IP4 " + mediaServerItem.getSdpIp() + "\r\n");
        content.append("s=Play\r\n");
        content.append("c=IN IP4 " + mediaServerItem.getSdpIp() + "\r\n");
        content.append("t=0 0\r\n");
        content.append("m=video " + sendRtpItem.getLocalPort() + " RTP/AVP 96\r\n");
        content.append("a=sendonly\r\n");
        content.append("a=rtpmap:96 PS/90000\r\n");
        if (sendRtpItem.isTcp()) {
            content.append("a=connection:new\r\n");
            if (!sendRtpItem.isTcpActive()) {
                content.append("a=setup:active\r\n");
            } else {
                content.append("a=setup:passive\r\n");
            }
        }
        content.append("y=" + sendRtpItem.getSsrc() + "\r\n");
        content.append("f=\r\n");

        try {
            return responseSdpAck(serverTransaction, content.toString(), platform);
        } catch (SipException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void inviteFromDeviceHandle(ServerTransaction serverTransaction, String requesterId) throws InvalidArgumentException, ParseException, SipException, SdpException {

        // 非上级平台请求，查询是否设备请求（通常为接收语音广播的设备）
        Device device = redisCatchStorage.getDevice(requesterId);
        if (device != null) {
            logger.info("收到设备" + requesterId + "的语音广播Invite请求");
            responseAck(serverTransaction, Response.TRYING);

            String contentString = new String(serverTransaction.getRequest().getRawContent());
            // jainSip不支持y=字段， 移除移除以解析。
            String substring = contentString;
            String ssrc = "0000000404";
            int ssrcIndex = contentString.indexOf("y=");
            if (ssrcIndex > 0) {
                substring = contentString.substring(0, ssrcIndex);
                ssrc = contentString.substring(ssrcIndex + 2, ssrcIndex + 12);
            }
            ssrcIndex = substring.indexOf("f=");
            if (ssrcIndex > 0) {
                substring = contentString.substring(0, ssrcIndex);
            }
            SessionDescription sdp = SdpFactory.getInstance().createSessionDescription(substring);

            //  获取支持的格式
            Vector mediaDescriptions = sdp.getMediaDescriptions(true);
            // 查看是否支持PS 负载96
            int port = -1;
            //boolean recvonly = false;
            boolean mediaTransmissionTCP = false;
            Boolean tcpActive = null;
            for (int i = 0; i < mediaDescriptions.size(); i++) {
                MediaDescription mediaDescription = (MediaDescription) mediaDescriptions.get(i);
                Media media = mediaDescription.getMedia();

                Vector mediaFormats = media.getMediaFormats(false);
                if (mediaFormats.contains("8")) {
                    port = media.getMediaPort();
                    String protocol = media.getProtocol();
                    // 区分TCP发流还是udp， 当前默认udp
                    if ("TCP/RTP/AVP".equals(protocol)) {
                        String setup = mediaDescription.getAttribute("setup");
                        if (setup != null) {
                            mediaTransmissionTCP = true;
                            if ("active".equals(setup)) {
                                tcpActive = true;
                            } else if ("passive".equals(setup)) {
                                tcpActive = false;
                            }
                        }
                    }
                    break;
                }
            }
            if (port == -1) {
                logger.info("不支持的媒体格式，返回415");
                // 回复不支持的格式
                responseAck(serverTransaction, Response.UNSUPPORTED_MEDIA_TYPE); // 不支持的格式，发415
                return;
            }
            String username = sdp.getOrigin().getUsername();
            String addressStr = sdp.getOrigin().getAddress();
            logger.info("设备{}请求语音流，地址：{}:{}，ssrc：{}", username, addressStr, port, ssrc);

        } else {
            logger.warn("来自无效设备/平台的请求");
            responseAck(serverTransaction, Response.BAD_REQUEST);
        }
    }
}
