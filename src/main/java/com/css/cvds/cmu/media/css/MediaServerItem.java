package com.css.cvds.cmu.media.css;

import com.css.cvds.cmu.gb28181.session.SsrcConfig;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author chend
 */
@Schema(description = "流媒体服务信息")
public class MediaServerItem {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "IP")
    private String ip;

    @Schema(description = "IP")
    private Integer port;

    @Schema(description = "SDP IP")
    private String sdpIp;

    @Schema(description = "流ID")
    private String stream;

    @Schema(description = "ssrc")
    private String ssrc;

    @Schema(description = "是否使用多端口模式")
    private boolean rtpEnable;

    @Schema(description = "SSRC信息")
    private SsrcConfig ssrcConfig;

    public MediaServerItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() { return ip; }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSdpIp() {
        return sdpIp;
    }

    public void setSdpIp(String sdpIp) {
        this.sdpIp = sdpIp;
    }

    public boolean isRtpEnable() {
        return rtpEnable;
    }

    public void setRtpEnable(boolean rtpEnable) {
        this.rtpEnable = rtpEnable;
    }

    public SsrcConfig getSsrcConfig() {
        return ssrcConfig;
    }

    public void setSsrcConfig(SsrcConfig ssrcConfig) {
        this.ssrcConfig = ssrcConfig;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getSsrc() {
        return ssrc;
    }

    public void setSsrc(String ssrc) {
        this.ssrc = ssrc;
    }
}
