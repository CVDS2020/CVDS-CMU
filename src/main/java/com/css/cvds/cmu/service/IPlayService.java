package com.css.cvds.cmu.service;

import com.alibaba.fastjson.JSONObject;
import com.css.cvds.cmu.conf.exception.ServiceException;
import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.gb28181.bean.InviteStreamCallback;
import com.css.cvds.cmu.gb28181.bean.InviteStreamInfo;
import com.css.cvds.cmu.gb28181.event.SipSubscribe;
import com.css.cvds.cmu.media.css.MediaServerItem;
import com.css.cvds.cmu.service.bean.InviteTimeOutCallback;
import com.css.cvds.cmu.service.bean.PlayBackCallback;
import com.css.cvds.cmu.service.bean.PlayResult;
import com.css.cvds.cmu.service.bean.SSRCInfo;
import com.css.cvds.cmu.vmanager.bean.WVPResult;
import com.css.cvds.cmu.common.StreamInfo;
import org.springframework.web.context.request.async.DeferredResult;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;

/**
 * 点播处理
 */
public interface IPlayService {

    void play(MediaServerItem mediaServerItem, Device device, String channelId,
                SipSubscribe.Event okEvent, SipSubscribe.Event errorEvent);
    PlayResult play(MediaServerItem mediaServerItem, String deviceId, String channelId, SipSubscribe.Event errorEvent, Runnable timeoutCallback);

    MediaServerItem getMediaServerItem(Device device);

    MediaServerItem newMediaServerItem(Integer port);
}
