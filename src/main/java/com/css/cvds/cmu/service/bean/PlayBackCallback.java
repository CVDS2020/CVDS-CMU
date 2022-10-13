package com.css.cvds.cmu.service.bean;

import com.css.cvds.cmu.gb28181.transmit.callback.RequestMessage;

public interface PlayBackCallback {

    void call(PlayBackResult<RequestMessage> msg);

}
