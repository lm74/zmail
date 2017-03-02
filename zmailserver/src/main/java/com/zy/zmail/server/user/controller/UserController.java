package com.zy.zmail.server.user.controller;

import com.zy.zmail.server.common.json.JsonResult;
import com.zy.zmail.server.user.entity.UserInfo;
import com.zy.zmail.server.user.service.KeySecurity;
import com.zy.zmail.server.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;
import java.util.List;

/**
 * Created by wenliz on 2017/1/22.
 */
@RestController
@CrossOrigin
@RequestMapping(value="/api/user", produces = "application/json")
public class UserController {
    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value="/login", method = RequestMethod.GET)
    public JsonResult login(@RequestParam String userName, @RequestParam String password){
        JsonResult result = JsonResult.getInstance();
        try {
            UserInfo user = null;
            try {
                user = userService.getByUserName(userName);
            }
            catch (NoResultException e){
                try {
                    user = userService.getByPhoneNo(userName);
                }
                catch (NoResultException e1){

                        user = userService.getByCardNo(userName);
                        if(user == null) {
                            result.setResult(JsonResult.FAIL);
                            return result;
                        }

                }
            }


            String sEnc = KeySecurity.encrypt(password);

            /*if( !sEnc.equals(user.getPassword()) ){
                result.setResult(-2);
                return result;
            }*/
            result.setData(user);
        }
        catch(Exception e){
            result.setResult(JsonResult.FAIL);
        }
        return result;
    }

    @RequestMapping(value="/byCardNo", method = RequestMethod.GET)
    public JsonResult listByCardNo(@RequestParam String cardNo){
        JsonResult result = JsonResult.getInstance();
        UserInfo user = userService.getByCardNo(cardNo);
        if(user == null){
            result.setResult(JsonResult.FAIL);
        }
        else {
            result.setData(user);
        }
        return result;
    }

    @RequestMapping(value="/byUserTypes", method = RequestMethod.GET)
    public JsonResult listByUserTypes(@RequestParam String userTypes){
        JsonResult result = JsonResult.getInstance();
        List<UserInfo> users = userService.listByUserTypes(userTypes);
        result.setData(users);
        return result;
    }

    @RequestMapping(value="/changePassword", method = RequestMethod.GET)
    public JsonResult changePassword(@RequestParam Integer userId, @RequestParam String oldPassword, @RequestParam String newPassword){
        JsonResult result = JsonResult.getInstance();
        result.setResult(userService.changePassword(userId, oldPassword, newPassword));
        return result;
    }

    @RequestMapping(method = RequestMethod.GET)
    public JsonResult listAll(){
        JsonResult result = JsonResult.getInstance();
        List<UserInfo> users = userService.listAll();
        result.setData(users);
        return result;
    }

    @RequestMapping(value="/owner", method = RequestMethod.GET)
    public JsonResult listOwner(@RequestParam String filter){
        JsonResult result = JsonResult.getInstance();
        List<UserInfo> users = userService.listOwner(filter);
        result.setData(users);
        return result;
    }

    @RequestMapping(value="/owner/byRoom", method = RequestMethod.GET)
    public JsonResult listOwnerByRoomNo(@RequestParam String buildingNo, @RequestParam String unitNo, @RequestParam String roomNo){
        JsonResult result = JsonResult.getInstance();
        List<UserInfo> users = userService.listOwnerByRoom(buildingNo, unitNo, roomNo);
        result.setData(users);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST)
    public JsonResult save(@RequestBody UserInfo userInfo){
        JsonResult result = JsonResult.getInstance();
        if(userInfo.getUserId()!=null && userInfo.getUserId() == -1){
            userInfo.setUserId(null);
        }
        userService.save(userInfo);
        result.setData(userInfo);
        return result;
    }

    @RequestMapping(value="/test")
    public JsonResult test(){
        JsonResult result = JsonResult.getInstance();
        result.setResult(JsonResult.OK);
        return result;
    }

    @RequestMapping(value = "/{userId}",method = RequestMethod.DELETE)
    public JsonResult deleteById(@PathVariable Integer userId){
        JsonResult result = JsonResult.getInstance();
        userService.delete(userId);
        return result;
    }

}
