package com.css.cvds.cmu.storager;

import com.alibaba.fastjson.JSONObject;
import com.css.cvds.cmu.gb28181.bean.AlarmChannelMessage;
import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.gb28181.bean.ParentPlatformCatch;
import com.css.cvds.cmu.gb28181.bean.SendRtpItem;
import com.css.cvds.cmu.storager.dao.dto.PlatformRegisterInfo;
import com.css.cvds.cmu.common.StreamInfo;
import com.css.cvds.cmu.service.bean.GPSMsgInfo;

import java.util.List;
import java.util.Map;

public interface IRedisCatchStorage {

    /**
     * 计数器。为cseq进行计数
     *
     * @return
     */
    Long getCSEQ();

    /**
     * 开始播放时将流存入
     *
     * @param stream 流信息
     * @return
     */
    boolean startPlay(StreamInfo stream);

    /**
     * 停止播放时删除
     *
     * @return
     */
    boolean stopPlay(StreamInfo streamInfo);

    StreamInfo queryPlayByDevice(String deviceId, String channelId);

    ParentPlatformCatch queryPlatformCatchInfo(String platformGbId);

    void updatePlatformRegisterInfo(String callId, PlatformRegisterInfo platformRegisterInfo);

    PlatformRegisterInfo queryPlatformRegisterInfo(String callId);

    void delPlatformRegisterInfo(String callId);

    /**
     * 清空某个设备的所有缓存
     * @param deviceId 设备ID
     */
    void clearCatchByDeviceId(String deviceId);

    /**
     * 发送报警消息
     * @param msg 消息内容
     */
    void sendAlarmMsg(AlarmChannelMessage msg);

    /**
     * 将device信息写入redis
     * @param device
     */
    void updateDevice(Device device);

    /**
     * 获取Device
     */
    Device getDevice(String deviceId);

    void resetAllCSEQ();

    void updateGpsMsgInfo(GPSMsgInfo gpsMsgInfo);

    List<GPSMsgInfo> getAllGpsMsgInfo();

    void sendMobilePositionMsg(JSONObject jsonObject);

    /**
     * 判断设备状态
     * @param deviceId 设备ID
     * @return
     */
    boolean deviceIsOnline(String deviceId);
}
