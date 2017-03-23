package com.zy.zmail.server.setting.controller;

import com.zy.zmail.server.common.json.JsonResult;
import com.zy.zmail.server.north.Abudp.AbudpProtocol;
import com.zy.zmail.server.north.Abudp.AbudpSender;
import com.zy.zmail.server.north.DoorMessage;
import com.zy.zmail.server.north.DoorSystemRunner;
import com.zy.zmail.server.north.zytcp.ZytcpGateway;
import com.zy.zmail.server.north.zytcp.ZytcpProtocol;
import com.zy.zmail.server.north.zytcp.command.ZytcpCommand;
import com.zy.zmail.server.north.zyudp.ZyudpGateway;
import com.zy.zmail.server.north.zyudp.ZyudpProtocol;
import com.zy.zmail.server.north.zyudp.command.ZyudpCommand;
import com.zy.zmail.server.setting.entity.SystemOption;
import com.zy.zmail.server.setting.service.OptionService;
import com.zy.zmail.server.starter.SystemStarter;
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
        else if(protocolType == 1){
            result.setResult(testTcpConnection(serverIp,serverPort));
        }
        else{
            result.setResult(testZyudpConnection(serverIp, serverPort));
        }
        return result;
    }

    private Integer testTcpConnection(String serverIp, Integer serverPort) {

        SystemOption building = optionService.getById(SystemOption.BUILDING_NO_ID);
        SystemOption unitNo = optionService.getById(SystemOption.UNIT_NO_ID);


        DoorMessage message = new DoorMessage();
        message.setCommandNo(ZytcpCommand.DELIVERY);
        message.setBuildingNo(building.getIntValue());
        message.setUnitNo(unitNo.getIntValue());
        DoorSystemRunner.messages.add(message);
        sleep(500);
        message = new DoorMessage();

        message.setBuildingNo(building.getIntValue());
        message.setUnitNo(unitNo.getIntValue());
        message.setCommandNo(ZytcpCommand.PICKUP);
        DoorSystemRunner.messages.add(message);
        sleep(500);
        message = new DoorMessage();

        message.setBuildingNo(building.getIntValue());
        message.setUnitNo(unitNo.getIntValue());
        message.setCommandNo(ZytcpCommand.QUERY_STATUS_RESPONSE);
        message.setData(Integer.valueOf(1));
        DoorSystemRunner.messages.add(message);
        sleep(500);
        return 0;

    }

    private Integer testZyudpConnection(String serverIp, Integer serverPort) {
        SystemOption building = optionService.getById(SystemOption.BUILDING_NO_ID);
        SystemOption unitNo = optionService.getById(SystemOption.UNIT_NO_ID);

        DoorMessage message = new DoorMessage();
        message.setCommandNo(ZytcpCommand.DELIVERY);
        message.setBuildingNo(building.getIntValue());
        message.setUnitNo(unitNo.getIntValue());
        message.setFloorNo(8);
        message.setRoomNo(9);
        message.setDeliveryTime(new Date());
        message.setCabinetNo(10);
        message.setBoxNo(11);
        message.setOperateType((byte) 1);
        DoorSystemRunner.messages.add(message);
        sleep(500);
        message = new DoorMessage();
        message.setCommandNo(ZytcpCommand.PICKUP);
        message.setBuildingNo(building.getIntValue());
        message.setUnitNo(unitNo.getIntValue());
        message.setFloorNo(8);
        message.setRoomNo(9);
        message.setDeliveryTime(new Date());
        message.setCabinetNo(10);
        message.setBoxNo(11);
        message.setOperateType((byte) 2);
        DoorSystemRunner.messages.add(message);
        sleep(500);
        message = new DoorMessage();
        message.setBuildingNo(building.getIntValue());
        message.setUnitNo(unitNo.getIntValue());
        message.setFloorNo(8);
        message.setRoomNo(9);
        message.setDeliveryTime(new Date());
        message.setCabinetNo(10);
        message.setBoxNo(11);
        message.setOperateType((byte) 2);
        message.setCommandNo(ZyudpCommand.QUERY_STATUS_RESPONSE);
        byte[] context = new byte[]{1, 0};
        message.setData(context);
        DoorSystemRunner.messages.add(message);
        sleep(500);
        return 0;

    }

    private Integer testUdpConnection(String serverIp, Integer serverPort) {
        DoorMessage message = new DoorMessage();
        message.setDeliveryTime(new Date());
        message.setOperateType((byte) 1);

        DoorSystemRunner.messages.add(message);
        sleep(500);
        message = new DoorMessage();
        message.setDeliveryTime(new Date());
        message.setOperateType((byte) 2);
        DoorSystemRunner.messages.add(message);
        return 0;
    }

    private void sleep(int seconds){
        try{
            Thread.sleep(seconds);
        }
        catch (Exception e){

        }
    }


}
