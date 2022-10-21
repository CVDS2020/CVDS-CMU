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
import com.css.cvds.cmu.storager.IRedisCatchStorage;
import com.css.cvds.cmu.storager.IVideoManagerStorage;
import com.css.cvds.cmu.web.bean.ErrorCode;
import com.css.cvds.cmu.web.bean.WVPResult;
import com.css.cvds.cmu.web.gb28181.device.bean.AddDeviceParam;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

	/**
	 * 使用ID查询设备
	 * @param deviceId 国标ID
	 * @return 国标设备
	 */
	@Operation(summary = "查询设备")
	@Parameter(name = "deviceId", description = "设备编号", required = true)
	@GetMapping("/devices/{deviceId}")
	public WVPResult<Device> devices(@PathVariable String deviceId){
		
		return WVPResult.success(storager.queryVideoDevice(deviceId));
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
	@Parameter(name = "keyword", description = "关键字（名称、设备id、设备ip）过滤")
	@Parameter(name = "online", description = "是否在线")
	@Parameter(name = "superviseTargetId", description = "监视物ID")
	@GetMapping("/devices")
	public WVPResult<PageInfo<Device>> devices(int page, int count,
									@RequestParam(required = false) String keyword,
									@RequestParam(required = false) Boolean online,
									@RequestParam(required = false) Integer superviseTargetId) {
		
		return WVPResult.success(storager.queryVideoDeviceList(page, count, keyword, online, superviseTargetId));
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
	@PostMapping(value = "/add")
	@ResponseBody
	public WVPResult<?> add(@RequestBody AddDeviceParam addDeviceParam) {
		if (!SecurityUtils.isAdmin()) {
			throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
		}
		Device device = deviceService.queryDevice(addDeviceParam.getDeviceId());
		if (Objects.nonNull(device)) {
			throw new ControllerException(ErrorCode.ERROR101.getCode(), "摄像头已存在！");
		}
		device = new Device();
		device.setDeviceId(addDeviceParam.getDeviceId());
		device.setIp(addDeviceParam.getIp());
		device.setPort(addDeviceParam.getPort());
		device.setName(addDeviceParam.getName());
		device.setGateway(addDeviceParam.getGateway());
		device.setNetmask(addDeviceParam.getNetmask());
		device.setSuperviseTargetId(addDeviceParam.getSuperviseTargetId());

		deviceService.add(device);

		return WVPResult.success(null);
	}

	/**
	 * 移除设备
	 * @param deviceId 设备id
	 * @return
	 */
	@Operation(summary = "移除设备")
	@Parameter(name = "deviceId", description = "设备编号", required = true)
	@DeleteMapping("/devices/{deviceId}/delete")
	public WVPResult<?> delete(@PathVariable String deviceId){
		if (logger.isDebugEnabled()) {
			logger.debug("设备信息删除API调用，deviceId：" + deviceId);
		}
		if (!SecurityUtils.isAdmin()) {
			throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
		}
		// 清除redis记录
		boolean isSuccess = storager.delete(deviceId);
		if (isSuccess) {
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
	@Parameter(name = "channel", description = "通道信息", required = true)
	@PostMapping("/channel/update/{deviceId}")
	public WVPResult<?> updateChannel(@PathVariable String deviceId,DeviceChannel channel) {
		if (!SecurityUtils.isAdmin()) {
			throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
		}
		deviceChannelService.updateChannel(deviceId, channel);
		return WVPResult.success(null);
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
		device.setStreamMode(streamMode);
		deviceService.updateDevice(device);
		return WVPResult.success(null);
	}

	/**
	 * 更新设备信息
	 * @param device 设备信息
	 * @return
	 */
	@Operation(summary = "更新设备信息")
	@Parameter(name = "device", description = "设备", required = true)
	@PostMapping("/device/update/")
	public WVPResult<?> updateDevice(Device device){
		if (!SecurityUtils.isAdmin()) {
			throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
		}
		if (device != null && device.getDeviceId() != null) {
			deviceService.updateDevice(device);
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

	/**
	 * 设备报警查询请求API接口
	 * @param deviceId 设备id
	 * @param startPriority	报警起始级别（可选）
	 * @param endPriority	报警终止级别（可选）
	 * @param alarmMethod	报警方式条件（可选）
	 * @param alarmType		报警类型
	 * @param startTime		报警发生起始时间（可选）
	 * @param endTime		报警发生终止时间（可选）
	 * @return				true = 命令发送成功
	 */
	@Operation(summary = "设备告警查询")
	@Parameter(name = "deviceId", description = "设备编号", required = true)
	@Parameter(name = "startPriority", description = "报警起始级别")
	@Parameter(name = "endPriority", description = "报警终止级别")
	@Parameter(name = "alarmMethod", description = "报警方式条件")
	@Parameter(name = "alarmType", description = "报警类型")
	@Parameter(name = "startTime", description = "报警发生起始时间")
	@Parameter(name = "endTime", description = "报警发生终止时间")
	@GetMapping("/alarm/{deviceId}")
	public DeferredResult<ResponseEntity<String>> alarmApi(@PathVariable String deviceId,
														@RequestParam(required = false) String startPriority, 
														@RequestParam(required = false) String endPriority, 
														@RequestParam(required = false) String alarmMethod,
														@RequestParam(required = false) String alarmType,
														@RequestParam(required = false) String startTime,
														@RequestParam(required = false) String endTime) {
		if (logger.isDebugEnabled()) {
			logger.debug("设备报警查询API调用");
		}
		Device device = storager.queryVideoDevice(deviceId);
		String key = DeferredResultHolder.CALLBACK_CMD_ALARM + deviceId;
		String uuid = UUID.randomUUID().toString();
		try {
			cmder.alarmInfoQuery(device, startPriority, endPriority, alarmMethod, alarmType, startTime, endTime, event -> {
				RequestMessage msg = new RequestMessage();
				msg.setId(uuid);
				msg.setKey(key);
				msg.setData(WVPResult.fail(String.format("设备报警查询失败，错误码： %s, %s",event.statusCode, event.msg)));
				resultHolder.invokeResult(msg);
			});
		} catch (InvalidArgumentException | SipException | ParseException e) {
			logger.error("[命令发送失败] 设备报警查询: {}", e.getMessage());
			throw new ControllerException(ErrorCode.ERROR100.getCode(), "命令发送失败: " + e.getMessage());
		}
		DeferredResult<ResponseEntity<String>> result = new DeferredResult<ResponseEntity<String >> (3 * 1000L);
		result.onTimeout(()->{
			logger.warn(String.format("设备报警查询超时"));
			// 释放rtpserver
			RequestMessage msg = new RequestMessage();
			msg.setId(uuid);
			msg.setKey(key);
			msg.setData(WVPResult.fail("设备报警查询超时"));
			resultHolder.invokeResult(msg);
		});
		resultHolder.put(DeferredResultHolder.CALLBACK_CMD_ALARM + deviceId, uuid, result);
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
		}else {
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
