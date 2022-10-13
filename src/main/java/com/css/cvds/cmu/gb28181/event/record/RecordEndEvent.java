package com.css.cvds.cmu.gb28181.event.record;

import com.css.cvds.cmu.gb28181.bean.RecordInfo;
import org.springframework.context.ApplicationEvent;

/**
 * @description: 录像查询结束时间
 * @author: pan
 * @data: 2022-02-23
 */

public class RecordEndEvent extends ApplicationEvent {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public RecordEndEvent(Object source) {
        super(source);
    }

    private RecordInfo recordInfo;

    public RecordInfo getRecordInfo() {
        return recordInfo;
    }

    public void setRecordInfo(RecordInfo recordInfo) {
        this.recordInfo = recordInfo;
    }
}
