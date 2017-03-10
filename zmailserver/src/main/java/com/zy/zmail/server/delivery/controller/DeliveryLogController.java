package com.zy.zmail.server.delivery.controller;

import com.zy.zmail.server.cabinet.controller.CabinetController;
import com.zy.zmail.server.cabinet.entity.BoxInfo;
import com.zy.zmail.server.cabinet.entity.CabinetInfo;
import com.zy.zmail.server.cabinet.entity.CabinetNode;
import com.zy.zmail.server.cabinet.service.BoxService;
import com.zy.zmail.server.cabinet.service.CabinetService;
import com.zy.zmail.server.common.json.JsonResult;
import com.zy.zmail.server.delivery.entity.DeliveryLog;
import com.zy.zmail.server.delivery.entity.LogBrief;
import com.zy.zmail.server.delivery.service.DeliveryLogService;
import com.zy.zmail.server.north.DoorMessage;
import com.zy.zmail.server.north.DoorSystemRunner;
import com.zy.zmail.server.north.util.StringUtil;
import com.zy.zmail.server.north.zytcp.command.ZytcpCommand;
import com.zy.zmail.server.user.entity.UserInfo;
import com.zy.zmail.server.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by wenliz on 2017/2/14.
 */
@RestController
@CrossOrigin
@RequestMapping(value="/api/deliveryLog", produces = "application/json")
public class DeliveryLogController {
    private static final Logger logger= LoggerFactory.getLogger(DeliveryLogController.class);

    @Autowired
    DeliveryLogService deliveryLogService;
    @Autowired
    BoxService boxService;
    @Autowired
    UserService userService;

     @RequestMapping(value="/byCabinetId", method = RequestMethod.GET)
    public JsonResult listByCabinetId(@RequestParam Integer cabinetId, Integer periodType){
        JsonResult result = JsonResult.getInstance();
         List<DeliveryLog> logs;
        if(periodType==null || periodType>5 || periodType<=0){
            logs =deliveryLogService.listByCabinetId(cabinetId);
        }
        else{
            logs = deliveryLogService.listByCabinetId(cabinetId, periodType);
        }
        result.setData(logs);
        return result;
    }

    @RequestMapping(value="/byOwner", method = RequestMethod.GET)
    public JsonResult listByOwner(@RequestParam Integer cabinetId, Integer ownerId, Integer pickuped){
        JsonResult result = JsonResult.getInstance();
        if(pickuped == null || pickuped == 0){
            result.setData(deliveryLogService.listByOwner(cabinetId, ownerId, false));
        }
        else{
            result.setData(deliveryLogService.listByOwner(cabinetId, ownerId, true));
        }
        return result;
    }

    @RequestMapping(value="/allByOwner", method = RequestMethod.GET)
    public JsonResult listAllByOwner(Integer ownerId, Integer pickuped){
        JsonResult result = JsonResult.getInstance();
        result.setData(deliveryLogService.listAllByOwner(ownerId, pickuped));
        return result;
    }

    @RequestMapping(value="/byDelivery", method = RequestMethod.GET)
    public JsonResult listByDelivery(@RequestParam Integer cabinetId, Integer deliveryMan, Integer pickuped, Integer periodType){
        JsonResult result = JsonResult.getInstance();
        result.setData(deliveryLogService.listByDelivery(cabinetId, deliveryMan, pickuped, periodType));

        return result;
    }



    @RequestMapping(value="/putdown", method = RequestMethod.GET)
    public JsonResult putdown(@RequestParam Integer deliveryMan, @RequestParam Integer ownerId,
                              @RequestParam Integer boxId, @RequestParam Integer deliveryType){
        JsonResult result = JsonResult.getInstance();
        BoxInfo box = boxService.getById(boxId);
        box.setDeliveryMan(deliveryMan);
        box.setOwner( ownerId);
        box.setUsed(true);
        boxService.save(box);

        LogBrief brief = new LogBrief();
        brief.setDeliveryMan(deliveryMan);
        brief.setBoxId(boxId);
        brief.setDeliveryTime(new Timestamp(System.currentTimeMillis()));
        brief.setDeliveryType(deliveryType);
        brief.setPickupType(deliveryType);
        brief.setPickupUser(ownerId);
        deliveryLogService.save(brief);

        DoorMessage message = new DoorMessage();
        UserInfo user = userService.getByUserId(ownerId);
        message.setBuildingNo(StringUtil.getInteger(user.getBuildingNo()));
        message.setUnitNo(StringUtil.getInteger(user.getUnitNo()));
        message.setFloorNo(StringUtil.getInteger(user.getFloorNo()));
        message.setRoomNo(StringUtil.getInteger(user.getRoomNo()));
        message.setCommandNo(ZytcpCommand.DELIVERY);
        message.setDeliveryTime(new Date());
        message.setOperateType((byte)0);
        DoorSystemRunner.messages.add(message);

        return result;
    }

    @RequestMapping(value="/pickup", method = RequestMethod.GET)
    public JsonResult pickup(@RequestParam Integer logId, @RequestParam Integer pickupUser, @RequestParam Integer pickupType){

        JsonResult result = JsonResult.getInstance();
         LogBrief log = deliveryLogService.getBriefByLogId(logId);

        BoxInfo box = boxService.getById(log.getBoxId());
        box.setUsed(false);
        boxService.save(box);


        log.setPickupTime(new Timestamp(System.currentTimeMillis()));
        log.setPickupType(pickupType);
        log.setPickupUser(pickupUser);
        deliveryLogService.save(log);

        DoorMessage message = new DoorMessage();
        UserInfo user = userService.getByUserId(pickupUser);
        message.setBuildingNo(StringUtil.getInteger(user.getBuildingNo()));
        message.setUnitNo(StringUtil.getInteger(user.getUnitNo()));
        message.setFloorNo(StringUtil.getInteger(user.getFloorNo()));
        message.setRoomNo(StringUtil.getInteger(user.getRoomNo()));
        message.setCommandNo(ZytcpCommand.PICKUP);
        message.setDeliveryTime(new Date());
        message.setOperateType((byte)0);
        DoorSystemRunner.messages.add(message);


        return result;
    }
}
