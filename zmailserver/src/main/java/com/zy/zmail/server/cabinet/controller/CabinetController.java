package com.zy.zmail.server.cabinet.controller;

import com.zy.zmail.server.cabinet.entity.BoxInfo;
import com.zy.zmail.server.cabinet.entity.CabinetInfo;
import com.zy.zmail.server.cabinet.service.BoxService;
import com.zy.zmail.server.cabinet.service.CabinetService;
import com.zy.zmail.server.common.json.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by wenliz on 2017/2/8.
 */
@RestController
@CrossOrigin
@RequestMapping(value="/api/cabinet", produces = "application/json")
public class CabinetController {
    private static final Logger logger= LoggerFactory.getLogger(CabinetController.class);

    @Autowired
    CabinetService cabinetService;

    @Autowired
    BoxService boxService;

    @RequestMapping(method = RequestMethod.GET)
    public JsonResult listAll(){
        JsonResult result = JsonResult.getInstance();
        List<CabinetInfo> cabinets = cabinetService.listAll();
        result.setData(cabinets);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST)
    public JsonResult save(@RequestBody CabinetInfo cabinetInfo){
        JsonResult result = JsonResult.getInstance();

        if(cabinetInfo.getCabinetId()==-1){
            cabinetInfo.setCabinetId(null);
            int count = cabinetService.sameCabinetNo(cabinetInfo.getCabinetNo());
            if(count>=1){
                result.setResult(-2);
                result.setMessage("给定的箱柜号已经存在，请输入唯一的箱柜号。");
                return result;
            }
        }
        else{
            int count = cabinetService.sameCabinetNo(cabinetInfo.getCabinetNo());
            if(count>=2){
                result.setResult(-2);
                result.setMessage("给定的箱柜号已经存在，请输入唯一的箱柜号。");
                return result;
            }
        }
        cabinetService.save(cabinetInfo);
        boxService.deleteByCabinetId(cabinetInfo.getCabinetId());
        int sequenceIndex= 0;
        sequenceIndex = createControlBoxes(1, cabinetInfo.getController1BoxNumber(), sequenceIndex, cabinetInfo);
        sequenceIndex = createControlBoxes(2, cabinetInfo.getController2BoxNumber(), sequenceIndex, cabinetInfo);
        sequenceIndex = createControlBoxes(3, cabinetInfo.getController3BoxNumber(), sequenceIndex, cabinetInfo);
        sequenceIndex = createControlBoxes(4, cabinetInfo.getController4BoxNumber(), sequenceIndex, cabinetInfo);
        sequenceIndex = createControlBoxes(5, cabinetInfo.getController5BoxNumber(), sequenceIndex, cabinetInfo);
        sequenceIndex = createControlBoxes(6, cabinetInfo.getController6BoxNumber(), sequenceIndex, cabinetInfo);
        sequenceIndex = createControlBoxes(7, cabinetInfo.getController7BoxNumber(), sequenceIndex, cabinetInfo);
        sequenceIndex = createControlBoxes(8, cabinetInfo.getController8BoxNumber(), sequenceIndex, cabinetInfo);
        sequenceIndex = createControlBoxes(9, cabinetInfo.getController9BoxNumber(), sequenceIndex, cabinetInfo);
        createControlBoxes(10, cabinetInfo.getController10BoxNumber(), sequenceIndex, cabinetInfo);
        result.setData(cabinetInfo);
        return  result;
    }

    private int createControlBoxes(int controlId, int controlNumber, int sequenceIndex, CabinetInfo cabinetInfo){
        for(int i=0; i<controlNumber; i++){
            sequenceIndex++;
            BoxInfo box = new BoxInfo();
            box.setCabinetId(cabinetInfo.getCabinetId());
            box.setControlCardId(controlId);
            box.setControlSequence(i+1);
            box.setLocked(false);
            box.setOpened(false);
            box.setSequence(sequenceIndex);
            box.setBoxType(BoxInfo.BOX_TYPE_SMALL);
            box.setUsed(false);
            boxService.save(box);
        }
        return sequenceIndex;
    }



    @RequestMapping(value = "/{cabinetId}",method = RequestMethod.DELETE)
    public JsonResult deleteById(@PathVariable Integer cabinetId){
        JsonResult result = JsonResult.getInstance();
        cabinetService.delete(cabinetId);
        boxService.deleteByCabinetId(cabinetId);
        return result;
    }

    @RequestMapping(value="/byCabinetNo", method = RequestMethod.GET)
    public JsonResult getByCabinetNo(@RequestParam String cabinetNo){
        JsonResult result = JsonResult.getInstance();
        CabinetInfo cabinetInfo = cabinetService.getByCabinetNo(cabinetNo);
        if(cabinetInfo != null) {
            result.setData(cabinetInfo);
        }
        else{
            result.setResult(JsonResult.FAIL);
        }
        return result;
    }
}
