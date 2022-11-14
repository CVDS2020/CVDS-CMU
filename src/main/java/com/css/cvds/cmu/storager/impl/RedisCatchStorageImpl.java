package com.css.cvds.cmu.storager.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.css.cvds.cmu.gb28181.bean.*;
import com.css.cvds.cmu.utils.redis.RedisUtil;
import com.css.cvds.cmu.common.StreamInfo;
import com.css.cvds.cmu.common.VideoManagerConstants;
import com.css.cvds.cmu.conf.UserSetting;
import com.css.cvds.cmu.service.bean.GPSMsgInfo;
import com.css.cvds.cmu.storager.IRedisCatchStorage;
import com.css.cvds.cmu.storager.dao.DeviceChannelMapper;
import com.css.cvds.cmu.storager.dao.dto.PlatformRegisterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@SuppressWarnings("rawtypes")
@Component
public class RedisCatchStorageImpl implements IRedisCatchStorage {

    private final Logger logger = LoggerFactory.getLogger(RedisCatchStorageImpl.class);

    @Autowired
    private DeviceChannelMapper deviceChannelMapper;

    @Autowired
    private UserSetting userSetting;

    @Override
    public Long getCSEQ() {
        String key = VideoManagerConstants.SIP_CSEQ_PREFIX  + userSetting.getServerId();

        long result =  RedisUtil.incr(key, 1L);
        if (result > Integer.MAX_VALUE) {
            RedisUtil.set(key, 1);
            result = 1;
        }
        return result;
    }

    @Override
    public void resetAllCSEQ() {
        String scanKey = VideoManagerConstants.SIP_CSEQ_PREFIX  + userSetting.getServerId() + "_*";
        List<Object> keys = RedisUtil.scan(scanKey);
        for (Object o : keys) {
            String key = (String) o;
            RedisUtil.set(key, 1);
        }
    }

    /**
     * 开始播放时将流存入redis
     *
     * @return
     */
    @Override
    public boolean startPlay(StreamInfo stream) {
        return RedisUtil.set(String.format("%S_%S_%s_%s_%s", VideoManagerConstants.PLAYER_PREFIX, userSetting.getServerId(),
                        stream.getStream(), stream.getDeviceId(), stream.getChannelId()),
                stream);
    }

    /**
     * 停止播放时从redis删除
     *
     * @return
     */
    @Override
    public boolean stopPlay(StreamInfo streamInfo) {
        if (streamInfo == null) {
            return false;
        }
        return RedisUtil.del(String.format("%S_%s_%s_%s_%s", VideoManagerConstants.PLAYER_PREFIX,
                userSetting.getServerId(),
                streamInfo.getStream(),
                streamInfo.getDeviceId(),
                streamInfo.getChannelId()));
    }

    @Override
    public StreamInfo queryPlayByDevice(String deviceId, String channelId) {
        List<Object> playLeys = RedisUtil.scan(String.format("%S_%s_*_%s_%s", VideoManagerConstants.PLAYER_PREFIX,
                userSetting.getServerId(),
                deviceId,
                channelId));
        if (playLeys == null || playLeys.size() == 0) {
            return null;
        }
        return (StreamInfo)RedisUtil.get(playLeys.get(0).toString());
    }

    @Override
    public ParentPlatformCatch queryPlatformCatchInfo(String platformGbId) {
        return (ParentPlatformCatch)RedisUtil.get(VideoManagerConstants.PLATFORM_CATCH_PREFIX + userSetting.getServerId() + "_" + platformGbId);
    }

    @Override
    public void updatePlatformRegisterInfo(String callId, PlatformRegisterInfo platformRegisterInfo) {
        String key = VideoManagerConstants.PLATFORM_REGISTER_INFO_PREFIX + userSetting.getServerId() + "_" + callId;
        RedisUtil.set(key, platformRegisterInfo, 30);
    }

    @Override
    public PlatformRegisterInfo queryPlatformRegisterInfo(String callId) {
        return (PlatformRegisterInfo)RedisUtil.get(VideoManagerConstants.PLATFORM_REGISTER_INFO_PREFIX + userSetting.getServerId() + "_" + callId);
    }

    @Override
    public void delPlatformRegisterInfo(String callId) {
        RedisUtil.del(VideoManagerConstants.PLATFORM_REGISTER_INFO_PREFIX + userSetting.getServerId() + "_" + callId);
    }

    @Override
    public void clearCatchByDeviceId(String deviceId) {
        List<Object> playLeys = RedisUtil.scan(String.format("%S_%s_*_%s_*", VideoManagerConstants.PLAYER_PREFIX,
                userSetting.getServerId(),
                deviceId));
        if (playLeys.size() > 0) {
            for (Object key : playLeys) {
                RedisUtil.del(key.toString());
            }
        }

        List<Object> playBackers = RedisUtil.scan(String.format("%S_%s_%s_*_*_*", VideoManagerConstants.PLAY_BLACK_PREFIX,
                userSetting.getServerId(),
                deviceId));
        if (playBackers.size() > 0) {
            for (Object key : playBackers) {
                RedisUtil.del(key.toString());
            }
        }

        List<Object> deviceCache = RedisUtil.scan(String.format("%S%s_%s", VideoManagerConstants.DEVICE_PREFIX,
                userSetting.getServerId(),
                deviceId));
        if (deviceCache.size() > 0) {
            for (Object key : deviceCache) {
                RedisUtil.del(key.toString());
            }
        }
    }

    @Override
    public void updateDevice(Device device) {
        String key = VideoManagerConstants.DEVICE_PREFIX + userSetting.getServerId() + "_" + device.getDeviceId();
        RedisUtil.set(key, device);
    }

    @Override
    public Device getDevice(String deviceId) {
        String key = VideoManagerConstants.DEVICE_PREFIX + userSetting.getServerId() + "_" + deviceId;
        return (Device)RedisUtil.get(key);
    }

    @Override
    public void updateGpsMsgInfo(GPSMsgInfo gpsMsgInfo) {
        String key = VideoManagerConstants.WVP_STREAM_GPS_MSG_PREFIX + userSetting.getServerId() + "_" + gpsMsgInfo.getId();
        RedisUtil.set(key, gpsMsgInfo, 60); // 默认GPS消息保存1分钟
    }

    @Override
    public List<GPSMsgInfo> getAllGpsMsgInfo() {
        String scanKey = VideoManagerConstants.WVP_STREAM_GPS_MSG_PREFIX + userSetting.getServerId() + "_*";
        List<GPSMsgInfo> result = new ArrayList<>();
        List<Object> keys = RedisUtil.scan(scanKey);
        for (Object o : keys) {
            String key = (String) o;
            GPSMsgInfo gpsMsgInfo = (GPSMsgInfo) RedisUtil.get(key);
            if (!gpsMsgInfo.isStored()) { // 只取没有存过得
                result.add((GPSMsgInfo) RedisUtil.get(key));
            }
        }

        return result;
    }

    @Override
    public void sendMobilePositionMsg(JSONObject jsonObject) {
        String key = VideoManagerConstants.VM_MSG_SUBSCRIBE_MOBILE_POSITION;
        logger.info("[redis发送通知] 移动位置 {}: {}", key, jsonObject.toString());
        RedisUtil.convertAndSend(key, jsonObject);
    }

    @Override
    public void sendAlarmMsg(AlarmChannelMessage msg) {
        String key = VideoManagerConstants.VM_MSG_SUBSCRIBE_ALARM;
        logger.info("[redis发送通知] 报警{}: {}", key, JSON.toJSON(msg));
        RedisUtil.convertAndSend(key, (JSONObject)JSON.toJSON(msg));
    }

    @Override
    public boolean deviceIsOnline(String deviceId) {
        return getDevice(deviceId).getOnline() == 1;
    }
}
