package com.zy.zmail.server.delivery.controller;

import com.zy.zmail.server.cabinet.entity.BoxInfo;
import com.zy.zmail.server.cabinet.service.BoxService;
import com.zy.zmail.server.common.json.JsonResult;
import com.zy.zmail.server.delivery.entity.DeliveryLog;
import com.zy.zmail.server.delivery.entity.LogBrief;
import com.zy.zmail.server.delivery.entity.OpeningBrief;
import com.zy.zmail.server.delivery.entity.OpeningLog;
import com.zy.zmail.server.delivery.service.DeliveryLogService;
import com.zy.zmail.server.delivery.service.OpeningLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by wenliz on 2017/3/2.
 */
@RestController
@CrossOrigin
@RequestMapping(value="/api/openingLog", produces = "application/json")
public class OpeningLogController{
        private static final Logger logger= LoggerFactory.getLogger(OpeningLogController.class);

        @Autowired
        OpeningLogService openingLogService;
        @Autowired
        BoxService boxService;

        @RequestMapping(value="/byCabinetId", method = RequestMethod.GET)
        public JsonResult listByCabinetId(@RequestParam Integer cabinetId, Integer periodType){
            JsonResult result = JsonResult.getInstance();
            List<OpeningLog> logs;
            if(periodType==null || periodType>5 || periodType<=0){
                logs =openingLogService.listByCabinetId(cabinetId);
            }
            else{
                logs = openingLogService.listByCabinetId(cabinetId, periodType);
            }
            result.setData(logs);
            return result;
        }

    @RequestMapping(value="/save", method = RequestMethod.GET)
    public JsonResult putdown(@RequestParam Integer openingUser,
                              @RequestParam Integer boxId, @RequestParam String openingResult){
        JsonResult result = JsonResult.getInstance();


        OpeningBrief brief = new OpeningBrief();
        brief.setOpeningUser(openingUser);
        brief.setOpeningTime(new Timestamp(System.currentTimeMillis()));
        brief.setOpeningResult(openingResult);
        brief.setBoxId(boxId);

        openingLogService.save(brief);

        return result;
    }

}
