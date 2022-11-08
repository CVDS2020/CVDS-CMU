package com.css.cvds.cmu.storager;

import com.css.cvds.cmu.gb28181.bean.*;
import com.css.cvds.cmu.web.bean.DeviceDetailsVO;
import com.css.cvds.cmu.web.bean.DeviceVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**    
 * @description:视频设备数据存储接口
 * @author: swwheihei
 * @date:   2020年5月6日 下午2:14:31     
 */
@SuppressWarnings("rawtypes")
public interface IVideoManagerStorage {

	/**   
	 * 根据设备ID判断设备是否存在
	 * 
	 * @param deviceId 设备ID
	 * @return true:存在  false：不存在
	 */
	boolean exists(String deviceId);

	/**
	 * 开始播放
	 * @param deviceId 设备id
	 * @param channelId 通道ID
	 * @param streamId 流地址
	 */
	void startPlay(String deviceId, String channelId, String streamId);

	/**
	 * 停止播放
	 * @param deviceId 设备id
	 * @param channelId 通道ID
	 */
	void stopPlay(String deviceId, String channelId);
	
	/**   
	 * 获取设备
	 * 
	 * @param deviceId 设备ID
	 * @return DShadow 设备对象
	 */
	Device queryVideoDevice(String deviceId);

	/**
	 * 获取某个设备的通道列表
	 *
	 * @param deviceId 设备ID
	 * @return
	 */
	List<DeviceChannel> queryChannelsByDeviceId(String deviceId);

	/**
	 * 获取某个设备的通道
	 * @param deviceId 设备ID
	 * @param channelId 通道ID
	 */
	DeviceChannel queryChannel(String deviceId, String channelId);

	/**
	 * 删除通道
	 * @param deviceId 设备ID
	 * @param channelId 通道ID
	 */
	int delChannel(String deviceId, String channelId);

	/**
	 * 获取多个设备
	 * @param page 当前页数
	 * @param count 每页数量
	 * @param keyword 关键字（名称，设备ip）过滤
	 * @param online 是否在线
	 * @param superviseTargetType 监控物
	 * @return List<Device> 设备对象数组
	 */
	PageInfo<Device> queryVideoDeviceList(int page, int count,
										  String keyword, Boolean online, Integer carriageNo, Integer superviseTargetType);

	/**
	 * 获取多个设备
	 * @param page 当前页数
	 * @param count 每页数量
	 * @param keyword 关键字（名称，设备ip）过滤
	 * @param online 是否在线
	 * @param superviseTargetType 监控物
	 * @return List<Device> 设备对象数组
	 */
	PageInfo<DeviceVO> queryDeviceList(int page, int count,
											String keyword, Boolean online, Integer carriageNo, Integer superviseTargetType);

	/**
	 * 查询设备信息
	 * @param id 数据库id
	 * @return 设备信息
	 */
	DeviceDetailsVO queryDeviceDetailsById(Long id);

	/**
	 * 删除设备
	 * 
	 * @param deviceId 设备ID
	 * @return true：删除成功  false：删除失败
	 */
	boolean delete(String deviceId);
	
	/**   
	 * 更新设备在线
	 * 
	 * @param deviceId 设备ID
	 * @return true：更新成功  false：更新失败
	 */
	boolean online(String deviceId);
	
	/**   
	 * 更新设备离线
	 * 
	 * @param deviceId 设备ID
	 * @return true：更新成功  false：更新失败
	 */
	boolean outline(String deviceId);

	/**
	 * 清空通道
	 * @param deviceId
	 */
	void cleanChannelsForDevice(String deviceId);

	/**
	 * 添加Mobile Position设备移动位置
	 * @param mobilePosition
	 * @return
	 */
	boolean insertMobilePosition(MobilePosition mobilePosition);

	/**
	 * 查询移动位置轨迹
	 * @param deviceId
	 * @param startTime
	 * @param endTime
	 */
	List<MobilePosition> queryMobilePositions(String deviceId, String channelId, String startTime, String endTime);

	/**
	 * 查询最新移动位置
	 * @param deviceId
	 */
	MobilePosition queryLatestPosition(String deviceId);

	/**
	 * 根据通道ID获取其所在设备
	 * @param channelId  通道ID
	 * @return
	 */
    Device queryVideoDeviceByChannelId(String channelId);

	/**
	 * 通道上线
	 * @param channelId 通道ID
	 */
	void deviceChannelOnline(String deviceId, String channelId);

	/**
	 * 通道离线
	 * @param channelId 通道ID
	 */
	void deviceChannelOffline(String deviceId, String channelId);

	/**
	 * catlog查询结束后完全重写通道信息
	 * @param deviceId
	 * @param deviceChannelList
	 */
	boolean resetChannels(String deviceId, List<DeviceChannel> deviceChannelList);

    void updateChannelPosition(DeviceChannel deviceChannel);
}
