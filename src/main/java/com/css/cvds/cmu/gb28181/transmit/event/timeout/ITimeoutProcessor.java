package com.css.cvds.cmu.gb28181.transmit.event.timeout;

import javax.sip.TimeoutEvent;

public interface ITimeoutProcessor {
    void process(TimeoutEvent event);
}
