package com.css.cvds.cmu.web.gb28181;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.css.cvds.cmu.storager.IVideoManagerStorage;
import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.gb28181.bean.DeviceChannel;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API兼容：设备信息
 */
@SuppressWarnings("unchecked")
@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/device")
public class ApiDeviceController {

    private final static Logger logger = LoggerFactory.getLogger(ApiDeviceController.class);

    @Autowired
    private IVideoManagerStorage storager;

    /**
     * 分页获取设备列表 TODO 现在直接返回，尚未实现分页
     * @param start
     * @param limit
     * @param q
     * @param online
     * @return
     */
    @RequestMapping(value = "/list")
    public JSONObject list( @RequestParam(required = false)Integer start,
                            @RequestParam(required = false)Integer limit,
                            @RequestParam(required = false)String q,
                            @RequestParam(required = false)Boolean online ){
        JSONObject result = new JSONObject();
        List<Device> devices;
        if (start == null || limit ==null) {
            devices = storager.queryVideoDeviceList();
            result.put("DeviceCount", devices.size());
        }else {
            PageInfo<Device> deviceList = storager.queryVideoDeviceList(start/limit, limit, null, null, null);
            result.put("DeviceCount", deviceList.getTotal());
            devices = deviceList.getList();
        }

        JSONArray deviceJSONList = new JSONArray();
        for (Device device : devices) {
            JSONObject deviceJsonObject = new JSONObject();
            deviceJsonObject.put("ID", device.getDeviceId());
            deviceJsonObject.put("Name", device.getName());
            deviceJsonObject.put("Type", "GB");
            deviceJsonObject.put("ChannelCount", device.getChannelCount());
            deviceJsonObject.put("RecvStreamIP", "");
            // 通道目录抓取周期
            deviceJsonObject.put("CatalogInterval", 3600);
            // 订阅周期(秒), 0 表示后台不周期订阅
            deviceJsonObject.put("SubscribeInterval", device.getSubscribeCycleForCatalog());
            deviceJsonObject.put("Online", device.getOnline() == 1);
            deviceJsonObject.put("Password", "");
            deviceJsonObject.put("MediaTransport", device.getTransport());
            deviceJsonObject.put("RemoteIP", device.getIp());
            deviceJsonObject.put("RemotePort", device.getPort());
            deviceJsonObject.put("LastRegisterAt", "");
            deviceJsonObject.put("LastKeepaliveAt", "");
            deviceJsonObject.put("UpdatedAt", "");
            deviceJsonObject.put("CreatedAt", "");
            deviceJSONList.add(deviceJsonObject);
        }
        result.put("DeviceList",deviceJSONList);
        return result;
    }

    @RequestMapping(value = "/channellist")
    public JSONObject channellist( String serial,
                                   @RequestParam(required = false)String channel_type,
                                   @RequestParam(required = false)String dir_serial ,
                                   @RequestParam(required = false)Integer start,
                                   @RequestParam(required = false)Integer limit,
                                   @RequestParam(required = false)String q,
                                   @RequestParam(required = false)Boolean online ){

        JSONObject result = new JSONObject();
        // 查询设备是否存在
        Device device = storager.queryVideoDevice(serial);
        if (device == null) {
            result.put("ChannelCount", 0);
            result.put("ChannelList", "[]");
            return result;
        }
        List<DeviceChannel> deviceChannels;
        List<DeviceChannel> allDeviceChannelList = storager.queryChannelsByDeviceId(serial);
        if (start == null || limit ==null) {
            deviceChannels = allDeviceChannelList;
            result.put("ChannelCount", deviceChannels.size());
        }else {
            deviceChannels = storager.queryChannelsByDeviceIdWithStartAndLimit(serial, null, null, null,start, limit);
            int total = allDeviceChannelList.size();
            result.put("ChannelCount", total);
        }

        JSONArray channleJSONList = new JSONArray();
        for (DeviceChannel deviceChannel : deviceChannels) {
            JSONObject deviceJOSNChannel = new JSONObject();
            deviceJOSNChannel.put("ID", deviceChannel.getChannelId());
            deviceJOSNChannel.put("DeviceID", device.getDeviceId());
            deviceJOSNChannel.put("DeviceName", device.getName());
            deviceJOSNChannel.put("DeviceOnline", device.getOnline() == 1);
            // TODO 自定义序号
            deviceJOSNChannel.put("Channel", 0);
            deviceJOSNChannel.put("Name", deviceChannel.getName());
            deviceJOSNChannel.put("Custom", false);
            deviceJOSNChannel.put("CustomName", "");
            // TODO ? 子节点数, SubCount > 0 表示该通道为子目录
            deviceJOSNChannel.put("SubCount", deviceChannel.getSubCount());
            deviceJOSNChannel.put("SnapURL", "");
            deviceJOSNChannel.put("Manufacturer ", deviceChannel.getManufacture());
            deviceJOSNChannel.put("Model", deviceChannel.getModel());
            deviceJOSNChannel.put("Owner", deviceChannel.getOwner());
            deviceJOSNChannel.put("CivilCode", deviceChannel.getCivilCode());
            deviceJOSNChannel.put("Address", deviceChannel.getAddress());
            // 当为通道设备时, 是否有通道子设备, 1-有,0-没有
            deviceJOSNChannel.put("Parental", deviceChannel.getParental());
            // 直接上级编号
            deviceJOSNChannel.put("ParentID", deviceChannel.getParentId());
            deviceJOSNChannel.put("Secrecy", deviceChannel.getSecrecy());
            // 注册方式, 缺省为1, 允许值: 1, 2, 3
            // 1-IETF RFC3261,
            // 2-基于口令的双向认证,
            // 3-基于数字证书的双向认证
            deviceJOSNChannel.put("RegisterWay", 1);
            deviceJOSNChannel.put("Status", deviceChannel.getStatus());
            deviceJOSNChannel.put("Longitude", deviceChannel.getLongitudeWgs84());
            deviceJOSNChannel.put("Latitude", deviceChannel.getLatitudeWgs84());
            // 云台类型, 0 - 未知, 1 - 球机, 2 - 半球,
            // 3 - 固定枪机, 4 - 遥控枪机
            deviceJOSNChannel.put("PTZType ", deviceChannel.getPTZType());
            deviceJOSNChannel.put("CustomPTZType", "");
            // StreamID 直播流ID, 有值表示正在直播
            deviceJOSNChannel.put("StreamID", deviceChannel.getStreamId());
            // 直播在线人数
            deviceJOSNChannel.put("NumOutputs ", -1);
            channleJSONList.add(deviceJOSNChannel);
        }
        result.put("ChannelList", channleJSONList);
        return result;
    }
}
