package com.css.cvds.cmu.conf;

import com.css.cvds.cmu.utils.GitUtil;
import com.css.cvds.cmu.common.VersionPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VersionInfo {

    @Autowired
    GitUtil gitUtil;

    public VersionPo getVersion() {
        VersionPo versionPo = new VersionPo();
        versionPo.setGIT_Revision(gitUtil.getGitCommitId());
        versionPo.setGIT_BRANCH(gitUtil.getBranch());
        versionPo.setGIT_URL(gitUtil.getGitUrl());
        versionPo.setBUILD_DATE(gitUtil.getBuildDate());
        versionPo.setGIT_Revision_SHORT(gitUtil.getCommitIdShort());
        versionPo.setVersion(gitUtil.getBuildVersion());

        return versionPo;
    }
}
