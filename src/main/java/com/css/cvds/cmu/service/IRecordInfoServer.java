package com.css.cvds.cmu.service;

import com.css.cvds.cmu.storager.dao.dto.RecordInfo;
import com.github.pagehelper.PageInfo;

public interface IRecordInfoServer {
    PageInfo<RecordInfo> getRecordList(int page, int count);
}
