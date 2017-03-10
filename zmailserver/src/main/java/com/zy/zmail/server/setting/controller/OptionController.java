package com.zy.zmail.server.setting.controller;

import com.zy.zmail.server.common.json.JsonResult;
import com.zy.zmail.server.north.DoorMessage;
import com.zy.zmail.server.north.zytcp.ZytcpGateway;
import com.zy.zmail.server.north.zytcp.ZytcpProtocol;
import com.zy.zmail.server.north.zytcp.command.ZytcpCommand;
import com.zy.zmail.server.north.zyudp.ZyudpProtocol;
import com.zy.zmail.server.north.zyudp.ZyudpSender;
import com.zy.zmail.server.setting.entity.SystemOption;
import com.zy.zmail.server.setting.service.OptionService;
import com.zy.zmail.server.starter.SystemStarter;
import com.zy.zmail.server.user.controller.UserController;
import com.zy.zmail.server.user.entity.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created by wenliz on 2017/1/23.
 */
@RestController
@RequestMapping(value="/api/setting/option", produces = "application/json")
public class OptionController {
    private static final Logger logger= LoggerFactory.getLogger(OptionController.class);

    @Autowired
    OptionService optionService;



    @RequestMapping(value="byId", method = RequestMethod.GET)
    public JsonResult getById(@RequestParam Integer optionId){
        JsonResult result = JsonResult.getInstance();
        result.setData(optionService.getById(optionId));
        return result;
    }

    @RequestMapping(method = RequestMethod.GET)
    public JsonResult listAll(){
        JsonResult result = JsonResult.getInstance();
        result.setData(optionService.listAll());
        return result;
    }

    @RequestMapping(method = RequestMethod.POST)
    public JsonResult save(@RequestBody SystemOption option){
        JsonResult result = JsonResult.getInstance();

        optionService.save(option);
        if(option.getOptionId().equals(SystemOption.BUILDING_NO_ID)){
            SystemStarter.doorSystemRunner.setBuildingNo(option.getIntValue());
        }
        else if(option.getOptionId().equals(SystemOption.UNIT_NO_ID)){
            SystemStarter.doorSystemRunner.setUnitNo(option.getIntValue());
        }
        else if(option.getOptionId().equals(SystemOption.DOOR_PROTOCOL_ID)){
            SystemStarter.doorSystemRunner.setProtocolType(option.getIntValue());
        }
        else if(option.getOptionId().equals(SystemOption.DOOR_SERVER_IP_ID)){
            SystemStarter.doorSystemRunner.setServerIp((option.getCharValue()));
        }
        else if(option.getOptionId().equals(SystemOption.DOOR_SERVER_PORT_ID)){
            SystemStarter.doorSystemRunner.setServerPort(option.getIntValue());
        }

        result.setData(option);
        return result;
    }

    @RequestMapping(value="testDoorSystem", method = RequestMethod.GET)
    public JsonResult testDoorSystem(@RequestParam Integer protocolType, @RequestParam String serverIp, @RequestParam Integer serverPort){
        JsonResult result = JsonResult.getInstance();
        if(protocolType == 0){
            result.setResult(testUdpConnection(serverIp,serverPort));
        }
        else{
            result.setResult(testTcpConnection(serverIp,serverPort));
        }
        return result;
    }

    private Integer testTcpConnection(String serverIp, Integer serverPort) {
        ZytcpGateway zytcpGateway = new ZytcpGateway(serverIp, serverPort);
        if (!zytcpGateway.open()) {
            return 2;
        }
        try {
            SystemOption building = optionService.getById(SystemOption.BUILDING_NO_ID);
            SystemOption unitNo = optionService.getById(SystemOption.UNIT_NO_ID);
            if (!zytcpGateway.login(building.getIntValue(), unitNo.getIntValue())) {
                return 3;
            }
            sleep(500);
            zytcpGateway.heartbeat(building.getIntValue(), unitNo.getIntValue());
            sleep(500);
            DoorMessage message = new DoorMessage();
            message.setCommandNo(ZytcpCommand.DELIVERY);
            message.setBuildingNo(building.getIntValue());
            message.setUnitNo(unitNo.getIntValue());
            ZytcpProtocol protocol = ZytcpProtocol.getInstance();
            byte[] data = protocol.pack(message);
            zytcpGateway.sendMessage(data);
            sleep(500);
            message.setCommandNo(ZytcpCommand.PICKUP);
            data = protocol.pack(message);
            zytcpGateway.sendMessage(data);
            sleep(500);
            message.setCommandNo(ZytcpCommand.QUERY_STATUS_RESPONSE);
            message.setData(Integer.valueOf(1));
            data = protocol.pack(message);
            zytcpGateway.sendMessage(data);
            sleep(500);
            return 0;
        }
        finally {
            zytcpGateway.close();
        }
    }

    private Integer testUdpConnection(String serverIp, Integer serverPort) {
        DoorMessage message = new DoorMessage();
        message.setDeliveryTime(new Date());
        message.setOperateType((byte) 0);

        ZyudpProtocol protocol = ZyudpProtocol.getInstance();
        byte[] data = protocol.pack(message);
        ZyudpSender udpSender = new ZyudpSender(serverPort);
        try {
            if (!udpSender.send(data, serverIp, serverPort)) {
                return 1;
            }
            sleep(500);
            message.setOperateType((byte) 1);
            data = protocol.pack(message);
            if (!udpSender.send(data, serverIp, serverPort)) {
                return 1;
            }

            return 0;
        }
        finally {
            udpSender.close();
        }
    }

    private void sleep(int seconds){
        try{
            Thread.sleep(seconds);
        }
        catch (Exception e){

        }
    }


}
