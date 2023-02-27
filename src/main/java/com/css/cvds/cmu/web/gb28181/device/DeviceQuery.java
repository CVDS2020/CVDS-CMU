package com.css.cvds.cmu.web.gb28181.device;

import com.alibaba.fastjson.JSONObject;
import com.css.cvds.cmu.conf.DynamicTask;
import com.css.cvds.cmu.conf.exception.ControllerException;
import com.css.cvds.cmu.conf.security.SecurityUtils;
import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.gb28181.bean.DeviceChannel;
import com.css.cvds.cmu.gb28181.bean.SyncStatus;
import com.css.cvds.cmu.gb28181.task.ISubscribeTask;
import com.css.cvds.cmu.gb28181.transmit.callback.DeferredResultHolder;
import com.css.cvds.cmu.gb28181.transmit.callback.RequestMessage;
import com.css.cvds.cmu.gb28181.transmit.cmd.impl.SIPCommander;
import com.css.cvds.cmu.service.IDeviceChannelService;
import com.css.cvds.cmu.service.IDeviceService;
import com.css.cvds.cmu.service.ILogService;
import com.css.cvds.cmu.storager.IRedisCatchStorage;
import com.css.cvds.cmu.storager.IVideoManagerStorage;
import com.css.cvds.cmu.utils.DateUtil;
import com.css.cvds.cmu.utils.UserLogEnum;
import com.css.cvds.cmu.web.bean.*;
import com.css.cvds.cmu.web.converter.DeviceConverter;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;
import java.util.*;

@Tag(name  = "设备查询", description = "摄像头查询")
@SuppressWarnings("rawtypes")
@CrossOrigin
@RestController
@RequestMapping("/api/device/query")
public class DeviceQuery {
	
	private final static Logger logger = LoggerFactory.getLogger(DeviceQuery.class);
	
	@Autowired
	private IVideoManagerStorage storager;

	@Autowired
	private IDeviceChannelService deviceChannelService;

	@Autowired
	private IRedisCatchStorage redisCatchStorage;
	
	@Autowired
	private SIPCommander cmder;
	
	@Autowired
	private DeferredResultHolder resultHolder;

	@Autowired
	private IDeviceService deviceService;

	@Autowired
	private DynamicTask dynamicTask;

	@Autowired
	private ILogService logService;

	/**
	 * 使用ID查询设备
	 * @param id 数据库ID
	 * @return 国标设备
	 */
	@Operation(summary = "查询设备")
	@Parameter(name = "id", description = "数据库ID", required = true)
	@GetMapping("/devices/{id}")
	public WVPResult<DeviceDetailsVO> devices(@PathVariable Long id){
		return WVPResult.success(storager.queryDeviceDetailsById(id));
	}

	/**
	 * 分页查询设备
	 * @param page 当前页
	 * @param count 每页查询数量
	 * @return 分页列表
	 */
	@Operation(summary = "分页查询摄像头")
	@Parameter(name = "page", description = "当前页", required = true)
	@Parameter(name = "count", description = "每页查询数量", required = true)
	@Parameter(name = "keyword", description = "名称/ip 过滤")
	@Parameter(name = "carriageNo", description = "车厢号 过滤")
	@Parameter(name = "online", description = "是否在线")
	@Parameter(name = "superviseTargetType", description = "监视物类型")
	@GetMapping("/devices")
	public WVPResult<PageInfo<DeviceVO>> devices(int page, int count,
												 @RequestParam(required = false) String keyword,
												 @RequestParam(required = false) Boolean online,
												 @RequestParam(required = false) Integer carriageNo,
												 @RequestParam(required = false) Integer superviseTargetType) {
		
		return WVPResult.success(storager.queryDeviceList(page, count, keyword, online, carriageNo, superviseTargetType));
	}

	/**
	 * 分页查询通道数
	 *
	 * @param deviceId 设备id
	 * @return 通道列表
	 */
	@GetMapping("/devices/{deviceId}/channels")
	@Operation(summary = "查询设备通道")
	@Parameter(name = "deviceId", description = "设备编号", required = true)
	public WVPResult<List<DeviceChannel>> channels(@PathVariable String deviceId) {
		return WVPResult.success(storager.queryChannelsByDeviceId(deviceId));
	}

	/**
	 * 同步设备通道
	 * @param deviceId 设备id
	 * @return
	 */
	@Operation(summary = "同步设备通道")
	@Parameter(name = "deviceId", description = "设备编号", required = true)
	@PostMapping("/devices/{deviceId}/sync")
	public WVPResult<SyncStatus> devicesSync(@PathVariable String deviceId){
		
		if (logger.isDebugEnabled()) {
			logger.debug("设备通道信息同步API调用，deviceId：" + deviceId);
		}
		Device device = storager.queryVideoDevice(deviceId);
		boolean status = deviceService.isSyncRunning(deviceId);
		// 已存在则返回进度
		if (status) {
			SyncStatus channelSyncStatus = deviceService.getChannelSyncStatus(deviceId);
			return WVPResult.success(channelSyncStatus);
		}
		deviceService.sync(device);

		logService.addUserLog(UserLogEnum.HARDWARE_CTRL, "同步摄像机通道信息" + deviceId);

		WVPResult<SyncStatus> wvpResult = new WVPResult<>();
		wvpResult.setCode(0);
		wvpResult.setMessage("开始同步");
		return wvpResult;
	}

	/**
	 * 添加设备
	 * @param addDeviceParam
	 * @return
	 */
	@Operation(summary = "添加设备")
	@PostMapping(value = "/devices/add")
	public WVPResult<?> add(@RequestBody @Validated DeviceDTO addDeviceParam) {
		if (!SecurityUtils.isAdmin()) {
			throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
		}
		Device device = deviceService.queryDevice(addDeviceParam.getDeviceId());
		if (Objects.nonNull(device)) {
			throw new ControllerException(ErrorCode.ERROR101.getCode(), "摄像头已存在！");
		}
		device = DeviceConverter.INSTANCE.toDevice(addDeviceParam);
		if (StringUtils.isEmpty(device.getDeviceId())) {
			device.setDeviceId(device.getIp());
		}
		String now = DateUtil.getNow();
		device.setDeviceId(addDeviceParam.getIp());
		device.setStreamMode("TCP-PASSIVE");
		device.setCharset("GB2312");
		device.setGeoCoordSys("WGS84");
		device.setTreeType("CivilCode");
		device.setSubscribeCycleForCatalog(0);
		device.setHostAddress("");
		device.setCreateTime(now);
		device.setUpdateTime(now);
		device.setExpires(0);

		device.setOnline(0);

		deviceService.add(device);

		logService.addUserLog(UserLogEnum.DATA_CONFIG, "添加摄像机：" + addDeviceParam.getIp());

		return WVPResult.success(null);
	}

	/**
	 * 添加设备
	 * @param addDeviceParam
	 * @return
	 */
	@Operation(summary = "编辑设备")
	@Parameter(name = "id", description = "数据库id", required = true)
	@PostMapping(value = "/devices/{id}/update")
	public WVPResult<?> update(@PathVariable Long id, @RequestBody DeviceDTO addDeviceParam) {
		if (!SecurityUtils.isAdmin()) {
			throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
		}
		Device device = deviceService.queryDeviceById(id);
		if (Objects.isNull(device)) {
			throw new ControllerException(ErrorCode.ERROR101.getCode(), "摄像头不存在！");
		}
		device = DeviceConverter.INSTANCE.toDevice(addDeviceParam);
		device.setId(id);
		// 更新时间
		device.setUpdateTime(DateUtil.getNow());
		deviceService.updateDeviceById(device);

		logService.addUserLog(UserLogEnum.DATA_CONFIG, "修改摄像机：" + device.getIp());

		return WVPResult.success(null);
	}

	/**
	 * 移除设备
	 * @param id 设备id
	 * @return
	 */
	@Operation(summary = "移除设备")
	@Parameter(name = "id", description = "数据库ID", required = true)
	@DeleteMapping("/devices/{id}/delete")
	public WVPResult<?> delete(@PathVariable Long id) {
		if (!SecurityUtils.isAdmin()) {
			throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
		}
		Device device = deviceService.queryDeviceById(id);
		if (Objects.isNull(device)) {
			throw new ControllerException(ErrorCode.ERROR101.getCode(), "摄像头不存在！");
		}
		String deviceId = device.getDeviceId();
		if (logger.isDebugEnabled()) {
			logger.debug("设备信息删除API调用，deviceId：" + deviceId);
		}
		// 清除redis记录
		boolean isSuccess = storager.delete(deviceId);
		if (isSuccess) {
			logService.addUserLog(UserLogEnum.DATA_CONFIG, "删除摄像机：" + device.getIp());

			redisCatchStorage.clearCatchByDeviceId(deviceId);
			// 停止此设备的订阅更新
			Set<String> allKeys = dynamicTask.getAllKeys();
			for (String key : allKeys) {
				if (key.startsWith(deviceId)) {
					Runnable runnable = dynamicTask.get(key);
					if (runnable instanceof ISubscribeTask) {
						ISubscribeTask subscribeTask = (ISubscribeTask) runnable;
						subscribeTask.stop();
					}
					dynamicTask.stop(key);
				}
			}
			JSONObject json = new JSONObject();
			json.put("deviceId", deviceId);
		} else {
			logger.warn("设备信息删除API调用失败！");
			throw new ControllerException(ErrorCode.ERROR100.getCode(), "设备信息删除API调用失败！");
		}
		return WVPResult.success(null);
	}

	/**
	 * 更新通道信息
	 * @param deviceId 设备id
	 * @param channel 通道
	 * @return
	 */
	@Operation(summary = "更新通道信息")
	@Parameter(name = "deviceId", description = "设备编号", required = true)
	@PostMapping("/channel/update/{deviceId}")
	public WVPResult<?> updateChannel(@PathVariable String deviceId, @RequestBody DeviceChannel channel) {
		if (!SecurityUtils.isAdmin()) {
			throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
		}
		logService.addUserLog(UserLogEnum.DATA_CONFIG, "更新摄像机通道信息：" + deviceId);

		deviceChannelService.updateChannel(deviceId, channel);
		return WVPResult.success(null);
	}

	@Operation(summary = "更新视频配置信息")
	@Parameter(name = "deviceId", description = "设备国标ID", required = true)
	@PostMapping("/videoConfig/{deviceId}")
	public WVPResult<?> updateDeviceVideoConfig(@PathVariable String deviceId,
												@RequestBody VideoConfig config) {
		if (!SecurityUtils.isAdmin()) {
			throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
		}
		Device device = deviceService.queryDevice(deviceId);
		if (device == null) {
			throw new ControllerException(ErrorCode.ERROR100.getCode(), "设备不存在");
		}
		deviceService.updateConfig(deviceId, config);

		logService.addUserLog(UserLogEnum.DATA_CONFIG, "更新视频配置信息：" + deviceId);

		return WVPResult.success(null);
	}

	@Operation(summary = "重置视频配置信息")
	@Parameter(name = "deviceId", description = "设备国标ID", required = true)
	@PutMapping("/videoConfig/{deviceId}/reset")
	public WVPResult<?> resetDeviceVideoConfig(@PathVariable String deviceId) {
		if (!SecurityUtils.isAdmin()) {
			throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
		}
		Device device = deviceService.queryDevice(deviceId);
		if (device == null) {
			throw new ControllerException(ErrorCode.ERROR100.getCode(), "设备不存在");
		}
		deviceService.resetConfig(deviceId);

		logService.addUserLog(UserLogEnum.DATA_CONFIG, "重置视频配置信息：" + deviceId);

		return WVPResult.success(null);
	}

	@Operation(summary = "获取视频配置信息")
	@Parameter(name = "deviceId", description = "设备国标ID", required = true)
	@GetMapping("/videoConfig/{deviceId}")
	public WVPResult<VideoConfig> getDeviceVideoConfig(@PathVariable String deviceId) {
		Device device = deviceService.queryDevice(deviceId);
		if (device == null) {
			throw new ControllerException(ErrorCode.ERROR100.getCode(), "设备不存在");
		}
		return WVPResult.success(new VideoConfig());
	}

	/**
	 * 修改数据流传输模式
	 * @param deviceId 设备id
	 * @param streamMode 数据流传输模式
	 * @return
	 */
	@Operation(summary = "修改数据流传输模式")
	@Parameter(name = "deviceId", description = "设备编号", required = true)
	@Parameter(name = "streamMode", description = "数据流传输模式, 取值：" +
			"UDP（udp传输），TCP-ACTIVE（tcp主动模式,暂不支持），TCP-PASSIVE（tcp被动模式）", required = true)
	@PostMapping("/transport/{deviceId}/{streamMode}")
	public WVPResult<?> updateTransport(@PathVariable String deviceId, @PathVariable String streamMode) {
		if (!SecurityUtils.isAdmin()) {
			throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
		}
		Device device = storager.queryVideoDevice(deviceId);
		if (Objects.equals(device.getStreamMode(), streamMode)) {
			device.setStreamMode(streamMode);
			deviceService.updateDevice(device);
			logService.addUserLog(UserLogEnum.DATA_CONFIG, "修改摄像机数据传输模式：" + deviceId);
		}
		return WVPResult.success(null);
	}

	/**
	 * 设备状态查询请求API接口
	 * 
	 * @param deviceId 设备id
	 */
	@Operation(summary = "设备状态查询")
	@Parameter(name = "deviceId", description = "设备编号", required = true)
	@GetMapping("/devices/{deviceId}/status")
	public DeferredResult<WVPResult<?>> deviceStatusApi(@PathVariable String deviceId) {
		if (logger.isDebugEnabled()) {
			logger.debug("设备状态查询API调用");
		}
		Device device = storager.queryVideoDevice(deviceId);
		String uuid = UUID.randomUUID().toString();
		String key = DeferredResultHolder.CALLBACK_CMD_DEVICESTATUS + deviceId;
		DeferredResult<WVPResult<?>> result = new DeferredResult<WVPResult<?>>(2*1000L);
		if(device == null) {
			result.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), String.format("设备%s不存在", deviceId)));
			return result;
		}
		try {
			cmder.deviceStatusQuery(device, event -> {
				RequestMessage msg = new RequestMessage();
				msg.setId(uuid);
				msg.setKey(key);
				msg.setData(WVPResult.fail(String.format("获取设备状态失败，错误码： %s, %s", event.statusCode, event.msg)));
				resultHolder.invokeResult(msg);
			});
		} catch (InvalidArgumentException | SipException | ParseException e) {
			logger.error("[命令发送失败] 获取设备状态: {}", e.getMessage());
			throw new ControllerException(ErrorCode.ERROR100.getCode(), "命令发送失败: " + e.getMessage());
		}
		result.onTimeout(()->{
			logger.warn(String.format("获取设备状态超时"));
			// 释放rtpserver
			RequestMessage msg = new RequestMessage();
			msg.setId(uuid);
			msg.setKey(key);
			msg.setData(WVPResult.fail("Timeout. Device did not response to this command."));
			resultHolder.invokeResult(msg);
		});
		resultHolder.put(DeferredResultHolder.CALLBACK_CMD_DEVICESTATUS + deviceId, uuid, result);
		return result;
	}

	@GetMapping("/{deviceId}/sync_status")
	@Operation(summary = "同步通道状态")
	@Parameter(name = "deviceId", description = "设备编号", required = true)
	public WVPResult<SyncStatus> getSyncStatus(@PathVariable String deviceId) {
		SyncStatus channelSyncStatus = deviceService.getChannelSyncStatus(deviceId);
		WVPResult<SyncStatus> wvpResult = new WVPResult<>();
		if (channelSyncStatus == null) {
			wvpResult.setCode(-1);
			wvpResult.setMessage("同步尚未开始");
		} else {
			logService.addUserLog(UserLogEnum.HARDWARE_CTRL, "同步摄像机状态：" + deviceId);

			wvpResult.setCode(ErrorCode.SUCCESS.getCode());
			wvpResult.setMessage(ErrorCode.SUCCESS.getMsg());
			wvpResult.setData(channelSyncStatus);
			if (channelSyncStatus.getErrorMsg() != null) {
				wvpResult.setMessage(channelSyncStatus.getErrorMsg());
			}
		}
		return wvpResult;
	}
}
