package com.css.cvds.cmu.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.css.cvds.cmu.conf.DynamicTask;
import com.css.cvds.cmu.conf.SipConfig;
import com.css.cvds.cmu.conf.UserSetting;
import com.css.cvds.cmu.gb28181.session.SsrcConfig;
import com.css.cvds.cmu.gb28181.session.VideoStreamSessionManager;
import com.css.cvds.cmu.media.css.MediaServerItem;
import com.css.cvds.cmu.utils.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;

import com.css.cvds.cmu.common.VideoManagerConstants;
import com.css.cvds.cmu.gb28181.event.EventPublisher;
import com.css.cvds.cmu.service.IMediaServerService;

/**
 * 媒体服务器节点管理
 */
@Service
public class MediaServerServiceImpl implements IMediaServerService {

    private final static Logger logger = LoggerFactory.getLogger(MediaServerServiceImpl.class);

    @Autowired
    private UserSetting userSetting;

    @Autowired
    DataSourceTransactionManager dataSourceTransactionManager;

    @Autowired
    TransactionDefinition transactionDefinition;

    @Override
    public void releaseSsrc(String mediaServerItemId, String ssrc) {
        MediaServerItem mediaServerItem = getOne(mediaServerItemId);
        if (mediaServerItem == null || ssrc == null) {
            return;
        }
        SsrcConfig ssrcConfig = mediaServerItem.getSsrcConfig();
        ssrcConfig.releaseSsrc(ssrc);
        mediaServerItem.setSsrcConfig(ssrcConfig);
        String key = VideoManagerConstants.MEDIA_SERVER_PREFIX + userSetting.getServerId() + "_" + mediaServerItem.getId();
        RedisUtil.set(key, mediaServerItem);
    }

    @Override
    public List<MediaServerItem> getAllOnline() {
        String key = VideoManagerConstants.MEDIA_SERVERS_ONLINE_PREFIX + userSetting.getServerId();
        Set<String> mediaServerIdSet = RedisUtil.zRevRange(key, 0, -1);

        List<MediaServerItem> result = new ArrayList<>();
        if (mediaServerIdSet != null && mediaServerIdSet.size() > 0) {
            for (String mediaServerId : mediaServerIdSet) {
                String serverKey = VideoManagerConstants.MEDIA_SERVER_PREFIX + userSetting.getServerId() + "_" + mediaServerId;
                result.add((MediaServerItem) RedisUtil.get(serverKey));
            }
        }
        Collections.reverse(result);
        return result;
    }

    /**
     * 获取单个zlm服务器
     * @param mediaServerId 服务id
     * @return MediaServerItem
     */
    @Override
    public MediaServerItem getOne(String mediaServerId) {
        if (mediaServerId == null) {
            return null;
        }
        String key = VideoManagerConstants.MEDIA_SERVER_PREFIX + userSetting.getServerId() + "_" + mediaServerId;
        return (MediaServerItem)RedisUtil.get(key);
    }
    
    @Override
    public MediaServerItem getMediaServerForMinimumLoad() {
        String key = VideoManagerConstants.MEDIA_SERVERS_ONLINE_PREFIX + userSetting.getServerId();

        if (RedisUtil.zSize(key)  == null || RedisUtil.zSize(key) == 0) {
            if (RedisUtil.zSize(key)  == null || RedisUtil.zSize(key) == 0) {
                logger.info("获取负载最低的节点时无在线节点");
                return null;
            }
        }

        // 获取分数最低的，及并发最低的
        Set<Object> objects = RedisUtil.zRange(key, 0, -1);
        ArrayList<Object> mediaServerObjectS = new ArrayList<>(objects);

        String mediaServerId = (String)mediaServerObjectS.get(0);
        return getOne(mediaServerId);
    }

    @Override
    public void delete(String id) {
        RedisUtil.zRemove(VideoManagerConstants.MEDIA_SERVERS_ONLINE_PREFIX + userSetting.getServerId(), id);
        String key = VideoManagerConstants.MEDIA_SERVER_PREFIX + userSetting.getServerId() + "_" + id;
        RedisUtil.del(key);
    }
}
