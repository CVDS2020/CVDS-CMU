package com.css.cvds.cmu.service;

import com.css.cvds.cmu.media.css.MediaServerItem;

/**
 * 媒体服务节点
 */
public interface IMediaServerService {

    MediaServerItem getOne(String generalMediaServerId);

    void releaseSsrc(String mediaServerItemId, String ssrc);
}
