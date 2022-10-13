package com.css.cvds.cmu.gb28181.transmit.event.response.impl;

import com.css.cvds.cmu.conf.SipConfig;
import com.css.cvds.cmu.gb28181.transmit.SIPProcessorObserver;
import com.css.cvds.cmu.gb28181.transmit.event.response.SIPResponseProcessorAbstract;
import com.css.cvds.cmu.gb28181.SipLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.ResponseEvent;

/**    
 * @description: CANCEL响应处理器
 * @author: panlinlin
 * @date:   2021年11月5日 16:35
 */
@Component
public class CancelResponseProcessor extends SIPResponseProcessorAbstract {

	private final String method = "CANCEL";

	@Autowired
	private SipLayer sipLayer;

	@Autowired
	private SipConfig config;

	@Autowired
	private SIPProcessorObserver sipProcessorObserver;

	@Override
	public void afterPropertiesSet() throws Exception {
		// 添加消息处理的订阅
		sipProcessorObserver.addResponseProcessor(method, this);
	}
	/**   
	 * 处理CANCEL响应
	 *  
	 * @param evt
	 */
	@Override
	public void process(ResponseEvent evt) {
		// TODO Auto-generated method stub
		
	}

}
