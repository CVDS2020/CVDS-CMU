package com.css.cvds.cmu.web.server;

import com.css.cvds.cmu.VManageBootstrap;
import com.css.cvds.cmu.conf.exception.ControllerException;
import com.css.cvds.cmu.conf.security.SecurityUtils;
import com.css.cvds.cmu.conf.security.dto.LoginUser;
import com.css.cvds.cmu.service.ILogService;
import com.css.cvds.cmu.utils.BoardCardEnum;
import com.css.cvds.cmu.utils.CollectUtils;
import com.css.cvds.cmu.utils.SpringBeanFactory;
import com.css.cvds.cmu.utils.UserLogEnum;
import com.css.cvds.cmu.web.bean.*;
import gov.nist.javax.sip.SipStackImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.SipProvider;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("rawtypes")
@Tag(name = "系统管理")
@CrossOrigin
@RestController
@RequestMapping("/api/system")
public class ServerController {

    @Autowired
    private ILogService logService;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Operation(summary = "重启服务")
    @GetMapping(value = "/restart")
    @ResponseBody
    public void restart() {
        if (!SecurityUtils.isAdmin()) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
        }

        logService.addUserLog(UserLogEnum.RESET, "重启CVDS-CMU服务");
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

    @GetMapping("/info")
    @Operation(summary = "获取基本信息")
    public WVPResult<BaseInfo> info() {

        BaseInfo baseInfo = new BaseInfo();
        baseInfo.setUserInfo(SecurityUtils.getUserInfo());

        SystemInfo systemInfo = new SystemInfo();
        systemInfo.setTypeCode("cvds-cmu");
        systemInfo.setManufacturerCode("CSS-CVDS-CMU");
        systemInfo.setSoftwareVersion("1.0");
        systemInfo.setFirmwareVersion("1.0");
        baseInfo.setSystemInfo(systemInfo);

        return WVPResult.success(baseInfo);
    }

    @PutMapping("/correction")
    @Operation(summary = "校时")
    @Parameter(name = "type", description = "类型（0:自动，1:手动）", required = true)
    @Parameter(name = "time", description = "时间（格式：yyyy-mm-dd HH:MM:SS）", required = false)
    public WVPResult<?> correction(Integer type, String time) {
        return WVPResult.success();
    }

    @GetMapping("/board/list")
    @Operation(summary = "获取板卡列表")
    public DeferredResult<WVPResult<List<BoardCard>>> boardList() {

        DeferredResult<WVPResult<List<BoardCard>>> resultDeferredResult =
                new DeferredResult<>(30 * 1000L);

        resultDeferredResult.onTimeout(() -> {
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), "超时"));
        });

        List<BoardCard> boardCardList = CollectUtils.toList(Arrays.asList(BoardCardEnum.values()), e -> {
            BoardCard boardCard = new BoardCard();
            boardCard.setType(e.getType());
            boardCard.setName(e.getName());
            boardCard.setStatus(0);
            return boardCard;
        });

        resultDeferredResult.setResult(WVPResult.success(boardCardList));

        return resultDeferredResult;
    }

    @PutMapping("/board/ctrl")
    @Operation(summary = "板卡控制")
    @Parameter(name = "action", description = "类型（0:关机，1:开机，2:重启）", required = true)
    @Parameter(name = "type", description = "板卡类型：1-电源板，2-交换板，3-视频核心板，4-AI分析板", required = true)
    public DeferredResult<WVPResult<?>> boardCtrl(Integer type, Integer action) {

        DeferredResult<WVPResult<?>> resultDeferredResult =
                new DeferredResult<>(30 * 1000L);

        resultDeferredResult.onTimeout(() -> {
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), "超时"));
        });

        resultDeferredResult.setResult(WVPResult.success());

        return resultDeferredResult;
    }
}
