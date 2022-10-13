package com.css.cvds.cmu.service;

import com.alibaba.fastjson.JSONObject;
import com.css.cvds.cmu.media.css.MediaServerItem;

import java.util.List;

/**
 * 媒体服务节点
 */
public interface IMediaServerService {

    List<MediaServerItem> getAllOnline();

    MediaServerItem getOne(String generalMediaServerId);

    MediaServerItem getMediaServerForMinimumLoad();

    void releaseSsrc(String mediaServerItemId, String ssrc);

    void delete(String id);
}
