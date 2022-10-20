package com.css.cvds.cmu.service;

import com.alibaba.fastjson.JSONObject;
import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.gb28181.event.SipSubscribe;
import com.css.cvds.cmu.media.css.MediaServerItem;
import com.css.cvds.cmu.vmanager.bean.WVPResult;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * 点播处理
 */
public interface IPlayService {

    void play(MediaServerItem mediaServerItem, Device device, String channelId,
                SipSubscribe.Event okEvent, SipSubscribe.Event errorEvent);

    DeferredResult<WVPResult<JSONObject>> play(String deviceId, String channelId, String ip, Integer port);

    WVPResult<String> stop(String deviceId, String channelId);
}
