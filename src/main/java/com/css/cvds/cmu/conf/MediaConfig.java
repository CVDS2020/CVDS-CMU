package com.css.cvds.cmu.conf;

import com.css.cvds.cmu.media.css.MediaServerItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;


@Configuration("mediaConfig")
public class MediaConfig{

    private final static Logger logger = LoggerFactory.getLogger(MediaConfig.class);

    // 修改必须配置，不再支持自动获取
    @Value("${media.id}")
    private String id;

    @Value("${media.ip}")
    private String ip;

    @Value("${sip.ip}")
    private String sipIp;

    @Value("${sip.domain}")
    private String sipDomain;

    @Value("${media.sdp-ip:${media.ip}}")
    private String sdpIp;

    @Value("${media.rtp.enable}")
    private boolean rtpEnable;

    public String getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public String getSipIp() {
        if (sipIp == null) {
            return this.ip;
        }else {
            return sipIp;
        }
    }

    public boolean isRtpEnable() {
        return rtpEnable;
    }

    public String getSdpIp() {
        if (ObjectUtils.isEmpty(sdpIp)){
            return ip;
        }else {
            if (isValidIPAddress(sdpIp)) {
                return sdpIp;
            }else {
                // 按照域名解析
                String hostAddress = null;
                try {
                    hostAddress = InetAddress.getByName(sdpIp).getHostAddress();
                } catch (UnknownHostException e) {
                    logger.error("[获取SDP IP]: 域名解析失败");
                }
                return hostAddress;
            }
        }
    }

    private boolean isValidIPAddress(String ipAddress) {
        if ((ipAddress != null) && (!ipAddress.isEmpty())) {
            return Pattern.matches("^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$", ipAddress);
        }
        return false;
    }

    public MediaServerItem getMediaSerItem(){
        MediaServerItem mediaServerItem = new MediaServerItem();
        mediaServerItem.setId(id);
        mediaServerItem.setIp(ip);
        mediaServerItem.setSdpIp(getSdpIp());
        mediaServerItem.setRtpEnable(rtpEnable);

        return mediaServerItem;
    }
}
