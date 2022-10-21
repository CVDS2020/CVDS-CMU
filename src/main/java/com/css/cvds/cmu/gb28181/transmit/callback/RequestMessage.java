package com.css.cvds.cmu.gb28181.transmit.callback;

import com.css.cvds.cmu.web.bean.WVPResult;

/**
 * @description: 请求信息定义   
 * @author: swwheihei
 * @date:   2020年5月8日 下午1:09:18     
 */
public class RequestMessage {
	
	private String id;

	private String key;

	private WVPResult<Object> data;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public Object getData() {
		return data;
	}

	public void setData(WVPResult<Object> data) {
		this.data = data;
	}

	public void setError(String msg) {
		this.data = WVPResult.fail(msg);
	}
}
