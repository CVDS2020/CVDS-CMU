package com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.response.cmd;

import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.gb28181.bean.ParentPlatform;
import com.css.cvds.cmu.gb28181.bean.PresetQuerySipReq;
import com.css.cvds.cmu.gb28181.transmit.callback.DeferredResultHolder;
import com.css.cvds.cmu.gb28181.transmit.callback.RequestMessage;
import com.css.cvds.cmu.gb28181.transmit.event.request.SIPRequestProcessorParent;
import com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.IMessageHandler;
import com.css.cvds.cmu.gb28181.transmit.event.request.impl.message.response.ResponseMessageHandler;
import com.css.cvds.cmu.gb28181.utils.XmlUtil;
import com.css.cvds.cmu.web.bean.WVPResult;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 设备预置位查询应答
 */
@Component
public class PresetQueryResponseMessageHandler extends SIPRequestProcessorParent implements InitializingBean, IMessageHandler {

    private Logger logger = LoggerFactory.getLogger(PresetQueryResponseMessageHandler.class);
    private final String cmdType = "PresetQuery";

    @Autowired
    private ResponseMessageHandler responseMessageHandler;

    @Autowired
    private DeferredResultHolder deferredResultHolder;


    @Override
    public void afterPropertiesSet() throws Exception {
        responseMessageHandler.addHandler(cmdType, this);
    }

    @Override
    public void handForDevice(RequestEvent evt, Device device, Element element) {

        ServerTransaction serverTransaction = getServerTransaction(evt);

        try {
             Element rootElement = getRootElement(evt, device.getCharset());

            if (rootElement == null) {
                logger.warn("[ 设备预置位查询应答 ] content cannot be null, {}", evt.getRequest());
                responseAck(serverTransaction, Response.BAD_REQUEST);
                return;
            }
            Element presetListNumElement = rootElement.element("PresetList");
            Element snElement = rootElement.element("SN");
            //该字段可能为通道或则设备的id
            String deviceId = XmlUtil.getText(rootElement, "DeviceID");
            String key = DeferredResultHolder.CALLBACK_CMD_PRESETQUERY + deviceId;
            if (snElement == null || presetListNumElement == null) {
                responseAck(serverTransaction, Response.BAD_REQUEST, "xml error");
                return;
            }
            int sumNum = Integer.parseInt(presetListNumElement.attributeValue("Num"));
            List<PresetQuerySipReq> presetQuerySipReqList = new ArrayList<>();
            if (sumNum > 0) {
                for (Iterator<Element> presetIterator = presetListNumElement.elementIterator(); presetIterator.hasNext(); ) {
                    Element itemListElement = presetIterator.next();
                    PresetQuerySipReq presetQuerySipReq = new PresetQuerySipReq();
                    for (Iterator<Element> itemListIterator = itemListElement.elementIterator(); itemListIterator.hasNext(); ) {
                        // 遍历item
                        Element itemOne = itemListIterator.next();
                        String name = itemOne.getName();
                        String textTrim = itemOne.getTextTrim();
                        if ("PresetID".equalsIgnoreCase(name)) {
                            presetQuerySipReq.setPresetId(textTrim);
                        } else {
                            presetQuerySipReq.setPresetName(textTrim);
                        }
                    }
                    presetQuerySipReqList.add(presetQuerySipReq);
                }
            }
            RequestMessage requestMessage = new RequestMessage();
            requestMessage.setKey(key);
            requestMessage.setData(WVPResult.success(presetQuerySipReqList));
            deferredResultHolder.invokeAllResult(requestMessage);
            responseAck(serverTransaction, Response.OK);
        } catch (DocumentException e) {
            logger.error("[解析xml]失败: ", e);
        } catch (InvalidArgumentException | ParseException | SipException e) {
            logger.error("[命令发送失败] 设备预置位查询应答处理: {}", e.getMessage());
        }
    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatform parentPlatform, Element rootElement) {

    }

}
