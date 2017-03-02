package com.zy.zmail.server.cabinet.controller;

import com.zy.zmail.server.cabinet.entity.BoxInfo;
import com.zy.zmail.server.cabinet.entity.CabinetInfo;
import com.zy.zmail.server.cabinet.entity.CabinetNode;
import com.zy.zmail.server.cabinet.service.BoxService;
import com.zy.zmail.server.common.json.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by wenliz on 2017/2/10.
 */
@RestController
@CrossOrigin
@RequestMapping(value="/api/box", produces = "application/json")
public class BoxController {
    private static final Logger logger= LoggerFactory.getLogger(BoxController.class);

    @Autowired
    private BoxService boxService;

    @RequestMapping(value="/byCabinetId", method= RequestMethod.GET)
    public JsonResult listByCabinetId(@RequestParam("cabinetId")  Integer cabinetId){
        JsonResult result = JsonResult.getInstance();
        List<BoxInfo> boxes = boxService.listByCabinetId(cabinetId);
        result.setData(boxes);
        return result;
    }
    @RequestMapping(value="/applyMail", method= RequestMethod.GET)
    public JsonResult listApplyMail(@RequestParam("cabinetId")  Integer cabinetId){
        JsonResult result = JsonResult.getInstance();
        List<BoxInfo> boxes = boxService.listApplyMail(cabinetId);
        result.setData(boxes);
        return result;
    }

    @RequestMapping(value="/anotherMaxAvailableCabinet", method = RequestMethod.GET)
    public JsonResult getAnotherMaxAvailableCabinet(Integer currentCabinetId, String boxTypes){
        JsonResult result = JsonResult.getInstance();
        List<CabinetNode> nodes = boxService.cabinetAvailableCount(boxTypes);

        Integer cabinetNo = 0;
        Integer maxCount = 0;
        for(int i=0; i<nodes.size(); i++){
            CabinetNode node = nodes.get(i);
            if(node.getCabinetId() == currentCabinetId) continue;

            if(node.getCount()>maxCount){
                cabinetNo = node.getCabinetNo();
                maxCount = node.getCount();
            }
        }
        result.setResult(cabinetNo);
        result.setData(maxCount);
        return result;
    }

    @RequestMapping(value="/available", method= RequestMethod.GET)
    public JsonResult listAvailable(@RequestParam("cabinetId")  Integer cabinetId){
        JsonResult result = JsonResult.getInstance();
        List<BoxInfo> boxes = boxService.listAvailabe(cabinetId);
        result.setData(boxes);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST)
    public JsonResult save(@RequestBody BoxInfo boxInfo) {
        JsonResult result = JsonResult.getInstance();
        boxService.save(boxInfo);
        return result;
    }
}
