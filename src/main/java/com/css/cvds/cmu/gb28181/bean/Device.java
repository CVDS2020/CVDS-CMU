package com.css.cvds.cmu.gb28181.bean;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 国标设备/平台
 * @author lin
 */
@Schema(description = "国标设备/平台")
public class Device {

	@Schema(description = "ID")
	private Long id;

	@Schema(description = "设备国标编号")
	private String deviceId;

	@Schema(description = "名称")
	private String name;

	@Schema(description = "生产厂商")
	private String manufacturer;

	@Schema(description = "型号")
	private String model;

	@Schema(description = "固件版本")
	private String firmware;

	@Schema(description = "传输协议（UDP/TCP）")
	private String transport;

	@Schema(description = "数据流传输模式,  UDP:udp传输, TCP-ACTIVE:tcp主动模式, TCP-PASSIVE：tcp被动模式")
	private String streamMode;

	@Schema(description = "IP")
	private String  ip;

	@Schema(description = "端口")
	private Integer port;

	@Schema(description = "wan地址")
	private String  hostAddress;

	@Schema(description = "子网掩码")
	private String netmask;

	@Schema(description = "网关")
	private String gateway;

	@Schema(description = "是否在线，1为在线，0为离线")
	private Integer online;

	@Schema(description = "注册时间")
	private String registerTime;

	@Schema(description = "心跳时间")
	private String keepaliveTime;

	@Schema(description = "通道个数")
	private Integer channelCount;

	@Schema(description = "注册有效期")
	private Integer expires;

	@Schema(description = "创建时间")
	private String createTime;

	@Schema(description = "更新时间")
	private String updateTime;

	@Schema(description = "设备使用的媒体id, 默认为null")
	private String mediaServerId;

	@Schema(description = "符集, 支持 UTF-8 与 GB2312")
	private String charset ;

	@Schema(description = "目录订阅周期，0为不订阅")
	private Integer subscribeCycleForCatalog;

	@Schema(description = "移动设备位置订阅周期，0为不订阅")
	private Integer subscribeCycleForMobilePosition;

	@Schema(description = "移动设备位置信息上报时间间隔,单位:秒,默认值5")
	private Integer mobilePositionSubmissionInterval = 5;

	@Schema(description = "报警心跳时间订阅周期，0为不订阅")
	private Integer subscribeCycleForAlarm;

	@Schema(description = "是否开启ssrc校验，默认关闭，开启可以防止串流")
	private Boolean ssrcCheck = true;

	@Schema(description = "地理坐标系， 目前支持 WGS84,GCJ02")
	private String geoCoordSys;

	@Schema(description = "监视物ID")
	private Integer superviseTargetId;

	@Schema(description = "监视物类型")
	private Integer superviseTargetType;

	@Schema(description = "树类型 国标规定了两种树的展现方式 行政区划：CivilCode 和业务分组:BusinessGroup")
	private String treeType;

	@Schema(description = "摄像机安装位置")
	private String position;

	@Schema(description = "摄像机所在车厢号")
	private Integer carriageNo;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getFirmware() {
		return firmware;
	}

	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public String getStreamMode() {
		return streamMode;
	}

	public void setStreamMode(String streamMode) {
		this.streamMode = streamMode;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}

	public Integer getOnline() {
		return online;
	}

	public void setOnline(Integer online) {
		this.online = online;
	}

	public Integer getChannelCount() {
		return channelCount;
	}

	public void setChannelCount(Integer channelCount) {
		this.channelCount = channelCount;
	}

	public String getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}

	public String getKeepaliveTime() {
		return keepaliveTime;
	}

	public void setKeepaliveTime(String keepaliveTime) {
		this.keepaliveTime = keepaliveTime;
	}

	public Integer getExpires() {
		return expires;
	}

	public void setExpires(Integer expires) {
		this.expires = expires;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getMediaServerId() {
		return mediaServerId;
	}

	public void setMediaServerId(String mediaServerId) {
		this.mediaServerId = mediaServerId;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public Integer getSubscribeCycleForCatalog() {
		return subscribeCycleForCatalog;
	}

	public void setSubscribeCycleForCatalog(Integer subscribeCycleForCatalog) {
		this.subscribeCycleForCatalog = subscribeCycleForCatalog;
	}

	public Integer getSubscribeCycleForMobilePosition() {
		return subscribeCycleForMobilePosition;
	}

	public void setSubscribeCycleForMobilePosition(Integer subscribeCycleForMobilePosition) {
		this.subscribeCycleForMobilePosition = subscribeCycleForMobilePosition;
	}

	public Integer getMobilePositionSubmissionInterval() {
		return mobilePositionSubmissionInterval;
	}

	public void setMobilePositionSubmissionInterval(Integer mobilePositionSubmissionInterval) {
		this.mobilePositionSubmissionInterval = mobilePositionSubmissionInterval;
	}

	public Integer getSubscribeCycleForAlarm() {
		return subscribeCycleForAlarm;
	}

	public void setSubscribeCycleForAlarm(Integer subscribeCycleForAlarm) {
		this.subscribeCycleForAlarm = subscribeCycleForAlarm;
	}

	public Boolean isSsrcCheck() {
		return ssrcCheck;
	}

	public void setSsrcCheck(Boolean ssrcCheck) {
		this.ssrcCheck = ssrcCheck;
	}

	public String getGeoCoordSys() {
		return geoCoordSys;
	}

	public void setGeoCoordSys(String geoCoordSys) {
		this.geoCoordSys = geoCoordSys;
	}

	public String getTreeType() {
		return treeType;
	}

	public void setTreeType(String treeType) {
		this.treeType = treeType;
	}

	public Integer getSuperviseTargetId() {
		return superviseTargetId;
	}

	public void setSuperviseTargetId(Integer superviseTargetId) {
		this.superviseTargetId = superviseTargetId;
	}

	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Integer getCarriageNo() {
		return carriageNo;
	}

	public void setCarriageNo(Integer carriageNo) {
		this.carriageNo = carriageNo;
	}

	public Integer getSuperviseTargetType() {
		return superviseTargetType;
	}

	public void setSuperviseTargetType(Integer superviseTargetType) {
		this.superviseTargetType = superviseTargetType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
