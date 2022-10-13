package com.css.cvds.cmu.service.impl;

import com.css.cvds.cmu.storager.dao.RecordInfoDao;
import com.css.cvds.cmu.storager.dao.dto.RecordInfo;
import com.css.cvds.cmu.service.IRecordInfoServer;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordInfoServerImpl implements IRecordInfoServer {

    @Autowired
    private RecordInfoDao recordInfoDao;

    @Override
    public PageInfo<RecordInfo> getRecordList(int page, int count) {
        PageHelper.startPage(page, count);
        List<RecordInfo> all = recordInfoDao.selectAll();
        return new PageInfo<>(all);
    }
}
