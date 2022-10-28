package com.css.cvds.cmu.web.bean;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author chend
 */
@Schema(description = "文件下载信息")
public class DownloadInfo {

    @Schema(description = "下载Url")
    private String url;

}
