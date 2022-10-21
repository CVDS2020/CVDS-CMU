package com.css.cvds.cmu.web.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.css.cvds.cmu.conf.exception.ControllerException;
import com.css.cvds.cmu.utils.SpringBeanFactory;
import com.css.cvds.cmu.VManageBootstrap;
import com.css.cvds.cmu.common.VersionPo;
import com.css.cvds.cmu.conf.SipConfig;
import com.css.cvds.cmu.conf.UserSetting;
import com.css.cvds.cmu.conf.VersionInfo;
import com.css.cvds.cmu.web.bean.ErrorCode;
import gov.nist.javax.sip.SipStackImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.SipProvider;
import java.util.Iterator;

@SuppressWarnings("rawtypes")
@Tag(name = "服务控制")
@CrossOrigin
@RestController
@RequestMapping("/api/server")
public class ServerController {

    @Autowired
    private VersionInfo versionInfo;

    @Autowired
    private SipConfig sipConfig;

    @Autowired
    private UserSetting userSetting;

    @Value("${server.port}")
    private int serverPort;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Operation(summary = "重启服务")
    @GetMapping(value = "/restart")
    @ResponseBody
    public void restart() {
        taskExecutor.execute(()-> {
            try {
                Thread.sleep(3000);
                SipProvider up = (SipProvider) SpringBeanFactory.getBean("udpSipProvider");
                SipStackImpl stack = (SipStackImpl) up.getSipStack();
                stack.stop();
                Iterator listener = stack.getListeningPoints();
                while (listener.hasNext()) {
                    stack.deleteListeningPoint((ListeningPoint) listener.next());
                }
                Iterator providers = stack.getSipProviders();
                while (providers.hasNext()) {
                    stack.deleteSipProvider((SipProvider) providers.next());
                }
                VManageBootstrap.restart();
            } catch (InterruptedException | ObjectInUseException e) {
                throw new ControllerException(ErrorCode.ERROR100.getCode(), e.getMessage());
            }
        });
    };

    @Operation(summary = "获取版本信息")
    @GetMapping(value = "/version")
    @ResponseBody
    public VersionPo VersionPogetVersion() {
        return versionInfo.getVersion();
    }

    @GetMapping(value = "/config")
    @Operation(summary = "获取版本信息")
    @Parameter(name = "type", description = "配置类型（sip, base）", required = true)
    @ResponseBody
    public JSONObject getVersion(String type) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("server.port", serverPort);
        if (ObjectUtils.isEmpty(type)) {
            jsonObject.put("sip", JSON.toJSON(sipConfig));
            jsonObject.put("base", JSON.toJSON(userSetting));
        } else {
            switch (type) {
                case "sip":
                    jsonObject.put("sip", sipConfig);
                    break;
                case "base":
                    jsonObject.put("base", userSetting);
                    break;
                default:
                    break;
            }
        }
        return jsonObject;
    }
}
