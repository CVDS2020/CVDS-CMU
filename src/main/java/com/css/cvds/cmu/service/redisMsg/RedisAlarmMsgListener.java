package com.css.cvds.cmu.service.redisMsg;

import com.alibaba.fastjson.JSON;
import com.css.cvds.cmu.gb28181.bean.AlarmChannelMessage;
import com.css.cvds.cmu.gb28181.transmit.cmd.ISIPCommander;
import com.css.cvds.cmu.gb28181.transmit.cmd.ISIPCommanderForPlatform;
import com.css.cvds.cmu.storager.IVideoManagerStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ConcurrentLinkedQueue;


@Component
public class RedisAlarmMsgListener implements MessageListener {

    private final static Logger logger = LoggerFactory.getLogger(RedisAlarmMsgListener.class);

    @Autowired
    private ISIPCommander commander;

    @Autowired
    private ISIPCommanderForPlatform commanderForPlatform;

    @Autowired
    private IVideoManagerStorage storage;

    private boolean taskQueueHandlerRun = false;

    private ConcurrentLinkedQueue<Message> taskQueue = new ConcurrentLinkedQueue<>();

    @Qualifier("taskExecutor")
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void onMessage(@NotNull Message message, byte[] bytes) {
        logger.info("收到来自REDIS的ALARM通知： {}", new String(message.getBody()));

        taskQueue.offer(message);
        if (!taskQueueHandlerRun) {
            taskQueueHandlerRun = true;
            logger.info("[线程池信息]活动线程数：{}, 最大线程数： {}", taskExecutor.getActiveCount(), taskExecutor.getMaxPoolSize());
            taskExecutor.execute(() -> {
                while (!taskQueue.isEmpty()) {
                    Message msg = taskQueue.poll();

                    AlarmChannelMessage alarmChannelMessage = JSON.parseObject(msg.getBody(), AlarmChannelMessage.class);
                    if (alarmChannelMessage == null) {
                        logger.warn("[REDIS的ALARM通知]消息解析失败");
                    }
                    // TODO 将告警推送到上级平台
                }
                taskQueueHandlerRun = false;
            });
        }


    }
}
