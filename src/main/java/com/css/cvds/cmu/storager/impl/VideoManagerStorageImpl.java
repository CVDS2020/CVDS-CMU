package com.css.cvds.cmu.storager.impl;

import com.css.cvds.cmu.gb28181.bean.*;
import com.css.cvds.cmu.storager.dao.*;
import com.css.cvds.cmu.utils.DateUtil;
import com.css.cvds.cmu.vmanager.gb28181.platform.bean.ChannelReduce;
import com.css.cvds.cmu.conf.SipConfig;
import com.css.cvds.cmu.conf.UserSetting;
import com.css.cvds.cmu.gb28181.event.EventPublisher;
import com.css.cvds.cmu.gb28181.event.subscribe.catalog.CatalogEvent;
import com.css.cvds.cmu.service.bean.GPSMsgInfo;
import com.css.cvds.cmu.storager.IRedisCatchStorage;
import com.css.cvds.cmu.storager.IVideoManagerStorage;
import com.css.cvds.cmu.storager.dao.dto.ChannelSourceInfo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 视频设备数据存储-jdbc实现
 * swwheihei
 * 2020年5月6日 下午2:31:42
 */
@SuppressWarnings("rawtypes")
@Component
public class VideoManagerStorageImpl implements IVideoManagerStorage {

	private final Logger logger = LoggerFactory.getLogger(VideoManagerStorageImpl.class);

	@Autowired
	EventPublisher eventPublisher;

	@Autowired
	SipConfig sipConfig;


	@Autowired
	TransactionDefinition transactionDefinition;

	@Autowired
	DataSourceTransactionManager dataSourceTransactionManager;

	@Autowired
    private DeviceMapper deviceMapper;

	@Autowired
	private DeviceChannelMapper deviceChannelMapper;

	@Autowired
	private DeviceMobilePositionMapper deviceMobilePositionMapper;

	@Autowired
    private IRedisCatchStorage redisCatchStorage;

	/**
	 * 根据设备ID判断设备是否存在
	 *
	 * @param deviceId 设备ID
	 * @return true:存在  false：不存在
	 */
	@Override
	public boolean exists(String deviceId) {
		return deviceMapper.getDeviceByDeviceId(deviceId) != null;
	}

	@Override
	public boolean resetChannels(String deviceId, List<DeviceChannel> deviceChannelList) {
		if (CollectionUtils.isEmpty(deviceChannelList)) {
			return false;
		}
		List<DeviceChannel> allChannels = deviceChannelMapper.queryAllChannels(deviceId);
		Map<String,DeviceChannel> allChannelMap = new ConcurrentHashMap<>();
		if (allChannels.size() > 0) {
			for (DeviceChannel deviceChannel : allChannels) {
				allChannelMap.put(deviceChannel.getChannelId(), deviceChannel);
			}
		}
		TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
		// 数据去重
		List<DeviceChannel> channels = new ArrayList<>();
		StringBuilder stringBuilder = new StringBuilder();
		Map<String, Integer> subContMap = new HashMap<>();
		if (deviceChannelList.size() > 0) {
			// 数据去重
			Set<String> gbIdSet = new HashSet<>();
			for (DeviceChannel deviceChannel : deviceChannelList) {
				if (!gbIdSet.contains(deviceChannel.getChannelId())) {
					gbIdSet.add(deviceChannel.getChannelId());
					if (allChannelMap.containsKey(deviceChannel.getChannelId())) {
						deviceChannel.setStreamId(allChannelMap.get(deviceChannel.getChannelId()).getStreamId());
						deviceChannel.setHasAudio(allChannelMap.get(deviceChannel.getChannelId()).isHasAudio());
					}
					channels.add(deviceChannel);
					if (!ObjectUtils.isEmpty(deviceChannel.getParentId())) {
						if (subContMap.get(deviceChannel.getParentId()) == null) {
							subContMap.put(deviceChannel.getParentId(), 1);
						}else {
							Integer count = subContMap.get(deviceChannel.getParentId());
							subContMap.put(deviceChannel.getParentId(), count++);
						}
					}
				}else {
					stringBuilder.append(deviceChannel.getChannelId()).append(",");
				}
			}
			if (channels.size() > 0) {
				for (DeviceChannel channel : channels) {
					if (subContMap.get(channel.getChannelId()) != null){
						channel.setSubCount(subContMap.get(channel.getChannelId()));
					}
				}
			}

		}
		if (stringBuilder.length() > 0) {
			logger.info("[目录查询]收到的数据存在重复： {}" , stringBuilder);
		}
		if(CollectionUtils.isEmpty(channels)){
			logger.info("通道重设，数据为空={}" , deviceChannelList);
			return false;
		}
		try {
			int cleanChannelsResult = deviceChannelMapper.cleanChannelsNotInList(deviceId, channels);
			int limitCount = 300;
			boolean result = cleanChannelsResult < 0;
			if (!result && channels.size() > 0) {
				if (channels.size() > limitCount) {
					for (int i = 0; i < channels.size(); i += limitCount) {
						int toIndex = i + limitCount;
						if (i + limitCount > channels.size()) {
							toIndex = channels.size();
						}
						result = result || deviceChannelMapper.batchAdd(channels.subList(i, toIndex)) < 0;
					}
				}else {
					result = result || deviceChannelMapper.batchAdd(channels) < 0;
				}
			}
			if (result) {
				//事务回滚
				dataSourceTransactionManager.rollback(transactionStatus);
			}
			dataSourceTransactionManager.commit(transactionStatus);     //手动提交
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			dataSourceTransactionManager.rollback(transactionStatus);
			return false;
		}

	}

	@Override
	public void deviceChannelOnline(String deviceId, String channelId) {
		deviceChannelMapper.online(deviceId, channelId);
	}

	@Override
	public void deviceChannelOffline(String deviceId, String channelId) {
		deviceChannelMapper.offline(deviceId, channelId);
	}

	@Override
	public void startPlay(String deviceId, String channelId, String streamId) {
		deviceChannelMapper.startPlay(deviceId, channelId, streamId);
	}

	@Override
	public void stopPlay(String deviceId, String channelId) {
		deviceChannelMapper.stopPlay(deviceId, channelId);
	}

	/**
	 * 获取设备
	 *
	 * @param deviceId 设备ID
	 * @return Device 设备对象
	 */
	@Override
	public Device queryVideoDevice(String deviceId) {
		return deviceMapper.getDeviceByDeviceId(deviceId);
	}

	@Override
	public PageInfo queryChannelsByDeviceId(String deviceId, String query, Boolean hasSubChannel, Boolean online, Boolean catalogUnderDevice, int page, int count) {
		// 获取到所有正在播放的流
		PageHelper.startPage(page, count);
		List<DeviceChannel> all;
		if (catalogUnderDevice != null && catalogUnderDevice) {
			all = deviceChannelMapper.queryChannels(deviceId, deviceId, query, hasSubChannel, online);
			// 海康设备的parentId是SIP id
			List<DeviceChannel> deviceChannels = deviceChannelMapper.queryChannels(deviceId, sipConfig.getId(), query, hasSubChannel, online);
			all.addAll(deviceChannels);
		}else {
			all = deviceChannelMapper.queryChannels(deviceId, null, query, hasSubChannel, online);
		}
		return new PageInfo<>(all);
	}

	@Override
	public List<DeviceChannel> queryChannelsByDeviceIdWithStartAndLimit(String deviceId, String query, Boolean hasSubChannel, Boolean online, int start, int limit) {
		return deviceChannelMapper.queryChannelsByDeviceIdWithStartAndLimit(deviceId, null, query, hasSubChannel, online, start, limit);
	}

	@Override
	public List<DeviceChannel> queryChannelsByDeviceId(String deviceId) {
		return deviceChannelMapper.queryChannels(deviceId, null,null, null, null);
	}

	@Override
	public PageInfo<DeviceChannel> querySubChannels(String deviceId, String parentChannelId, String query, Boolean hasSubChannel, Boolean online, int page, int count) {
		PageHelper.startPage(page, count);
		List<DeviceChannel> all = deviceChannelMapper.queryChannels(deviceId, parentChannelId, query, hasSubChannel, online);
		return new PageInfo<>(all);
	}

	@Override
	public DeviceChannel queryChannel(String deviceId, String channelId) {
		return deviceChannelMapper.queryChannel(deviceId, channelId);
	}


	@Override
	public int delChannel(String deviceId, String channelId) {
		return deviceChannelMapper.del(deviceId, channelId);
	}

	/**
	 * 获取多个设备
	 *
	 * @param page 当前页数
	 * @param count 每页数量
	 * @return PageInfo<Device> 分页设备对象数组
	 */
	@Override
	public PageInfo<Device> queryVideoDeviceList(int page, int count,
												 String keyword, Boolean online, Integer superviseTargetId) {
		PageHelper.startPage(page, count);
		List<Device> all = deviceMapper.getDevices(keyword, online, superviseTargetId);
		return new PageInfo<>(all);
	}

	/**
	 * 获取多个设备
	 *
	 * @return List<Device> 设备对象数组
	 */
	@Override
	public List<Device> queryVideoDeviceList() {

		List<Device> deviceList =  deviceMapper.getDevices(null, null, null);
		return deviceList;
	}

	/**
	 * 删除设备
	 *
	 * @param deviceId 设备ID
	 * @return true：删除成功  false：删除失败
	 */
	@Override
	public boolean delete(String deviceId) {
		TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
		boolean result = false;
		try {
			deviceChannelMapper.cleanChannelsByDeviceId(deviceId);
			if ( deviceMapper.del(deviceId) < 0 ) {
				//事务回滚
				dataSourceTransactionManager.rollback(transactionStatus);
			}
			result = true;
			dataSourceTransactionManager.commit(transactionStatus);     //手动提交
		}catch (Exception e) {
			dataSourceTransactionManager.rollback(transactionStatus);
		}
		return result;
	}

	/**
	 * 更新设备在线
	 *
	 * @param deviceId 设备ID
	 * @return true：更新成功  false：更新失败
	 */
	@Override
	public synchronized boolean online(String deviceId) {
		Device device = deviceMapper.getDeviceByDeviceId(deviceId);
		if (device == null) {
			return false;
		}
		device.setOnline(1);
		logger.info("更新设备在线: " + deviceId);
		redisCatchStorage.updateDevice(device);
		return deviceMapper.update(device) > 0;
	}

	/**
	 * 更新设备离线
	 *
	 * @param deviceId 设备ID
	 * @return true：更新成功  false：更新失败
	 */
	@Override
	public synchronized boolean outline(String deviceId) {
		logger.info("更新设备离线: " + deviceId);
		Device device = deviceMapper.getDeviceByDeviceId(deviceId);
		if (device == null) {
			return false;
		}
		device.setOnline(0);
		redisCatchStorage.updateDevice(device);
		return deviceMapper.update(device) > 0;
	}

	/**
	 * 清空通道
	 * @param deviceId
	 */
	@Override
	public void cleanChannelsForDevice(String deviceId) {
		deviceChannelMapper.cleanChannelsByDeviceId(deviceId);
	}

	/**
	 * 添加Mobile Position设备移动位置
	 * @param mobilePosition
	 */
	@Override
	public synchronized boolean insertMobilePosition(MobilePosition mobilePosition) {
		if (mobilePosition.getDeviceId().equals(mobilePosition.getChannelId())) {
			mobilePosition.setChannelId(null);
		}
		return deviceMobilePositionMapper.insertNewPosition(mobilePosition) > 0;
	}

	/**
	 * 查询移动位置轨迹
	 * @param deviceId
	 * @param startTime
	 * @param endTime
	 */
	@Override
	public synchronized List<MobilePosition> queryMobilePositions(String deviceId, String channelId, String startTime, String endTime) {
		return deviceMobilePositionMapper.queryPositionByDeviceIdAndTime(deviceId, channelId, startTime, endTime);
	}

	/**
	 * 查询最新移动位置
	 * @param deviceId
	 */
	@Override
	public MobilePosition queryLatestPosition(String deviceId) {
		return deviceMobilePositionMapper.queryLatestPositionByDevice(deviceId);
	}

	@Override
	public Device queryVideoDeviceByChannelId( String channelId) {
		Device result = null;
		List<DeviceChannel> channelList = deviceChannelMapper.queryChannelByChannelId(channelId);
		if (channelList.size() == 1) {
			result = deviceMapper.getDeviceByDeviceId(channelList.get(0).getDeviceId());
		}
		return result;
	}

	@Override
	public void updateChannelPosition(DeviceChannel deviceChannel) {
		if (deviceChannel.getChannelId().equals(deviceChannel.getDeviceId())) {
			deviceChannel.setChannelId(null);
		}
		if (deviceChannel.getGpsTime() == null) {
			deviceChannel.setGpsTime(DateUtil.getNow());
		}

		deviceChannelMapper.updatePosition(deviceChannel);
	}
}
