package com.css.cvds.cmu.service.bean;

import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.web.bean.WVPResult;
import org.springframework.web.context.request.async.DeferredResult;

public class PlayResult {

    private DeferredResult<WVPResult<String>> result;
    private String uuid;

    private Device device;

    public DeferredResult<WVPResult<String>> getResult() {
        return result;
    }

    public void setResult(DeferredResult<WVPResult<String>> result) {
        this.result = result;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
