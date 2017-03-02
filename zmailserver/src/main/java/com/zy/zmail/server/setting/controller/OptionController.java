package com.zy.zmail.server.setting.controller;

import com.zy.zmail.server.common.json.JsonResult;
import com.zy.zmail.server.setting.entity.SystemOption;
import com.zy.zmail.server.setting.service.OptionService;
import com.zy.zmail.server.user.controller.UserController;
import com.zy.zmail.server.user.entity.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        result.setData(option);
        return result;
    }
}
