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




            if( !password.equals(user.getPassword()) ){
                result.setResult(-2);
                return result;
            }
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
    public JsonResult listOwnerByRoomNo(@RequestParam String buildingNo, @RequestParam String unitNo,
                                        @RequestParam String floorNo, @RequestParam String roomNo){
        JsonResult result = JsonResult.getInstance();
        List<UserInfo> users = userService.listOwnerByRoom(buildingNo, unitNo,floorNo, roomNo);
        result.setData(users);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST)
    public JsonResult save(@RequestBody UserInfo userInfo){
        JsonResult result = JsonResult.getInstance();
        if(userInfo.getUserId()!=null && userInfo.getUserId() == -1){
            userInfo.setUserId(null);
            checkNewUser(userInfo, result);
        }
        else{
            checkOldUser(userInfo, result);
        }
        if(result.getResult().equals(JsonResult.OK)) {
            userService.save(userInfo);
            result.setData(userInfo);
        }
        return result;
    }

    private boolean checkOldUser(UserInfo oldUser, JsonResult result){
        UserInfo user = userService.getByUserName(oldUser.getUserName(), oldUser.getUserId());
        if(user != null){
            result.setResult(-2);
            return false;
        }
        if(oldUser.getPhoneNo()!=null && oldUser.getPhoneNo().length()>0){
            user = userService.getByPhoneNo(oldUser.getPhoneNo(), oldUser.getUserId());
            if(user != null){
                result.setResult( -3);
                return false;
            }
        }

        checkOldCardNo(oldUser.getCardNo1(), oldUser.getUserId(), -11, result);
        checkOldCardNo(oldUser.getCardNo2(), oldUser.getUserId(), -12, result);
        checkOldCardNo(oldUser.getCardNo3(), oldUser.getUserId(), -13, result);
        checkOldCardNo(oldUser.getCardNo4(), oldUser.getUserId(), -14, result);
        checkOldCardNo(oldUser.getCardNo5(), oldUser.getUserId(), -15, result);
        checkOldCardNo(oldUser.getCardNo6(), oldUser.getUserId(), -16, result);
        checkOldCardNo(oldUser.getCardNo7(), oldUser.getUserId(), -17, result);
        checkOldCardNo(oldUser.getCardNo8(), oldUser.getUserId(), -18, result);
        checkOldCardNo(oldUser.getCardNo9(), oldUser.getUserId(), -19, result);
        checkOldCardNo(oldUser.getCardNo10(), oldUser.getUserId(), -20, result);

        return true;

    }

    private boolean checkNewUser(UserInfo newUser, JsonResult result){
        UserInfo user = userService.getByUserName(newUser.getUserName());
        if(user != null){
            result.setResult(-2);
            return false;
        }
        if(newUser.getPhoneNo()!=null && newUser.getPhoneNo().length()>0){
            user = userService.getByPhoneNo(newUser.getPhoneNo());
            if(user != null){
                result.setResult( -3);
                return false;
            }
        }

        checkCardNo(newUser.getCardNo1(), -11, result);
        checkCardNo(newUser.getCardNo2(), -12, result);
        checkCardNo(newUser.getCardNo3(), -13, result);
        checkCardNo(newUser.getCardNo4(), -14, result);
        checkCardNo(newUser.getCardNo5(), -15, result);
        checkCardNo(newUser.getCardNo6(), -16, result);
        checkCardNo(newUser.getCardNo7(), -17, result);
        checkCardNo(newUser.getCardNo8(), -18, result);
        checkCardNo(newUser.getCardNo9(), -19, result);
        checkCardNo(newUser.getCardNo10(), -20, result);


        return true;
    }

    private void checkOldCardNo(String cardNo,Integer userId,  Integer errorNo, JsonResult result){
        if(cardNo!=null && cardNo.length()>0){
            UserInfo user = userService.getByCardNo(cardNo, userId);
            if(user != null){
               result.setResult(errorNo);
            }
        }
    }

    private void checkCardNo(String cardNo, Integer errorNo, JsonResult result){
        if(cardNo!=null && cardNo.length()>0){
            UserInfo user = userService.getByCardNo(cardNo);
            if(user != null){
                result.setResult(errorNo);
            }
        }

    }

    @RequestMapping(value="/deleteByIds", method = RequestMethod.POST)
    public JsonResult deleteByIds(@RequestBody String ids){
        JsonResult result = JsonResult.getInstance();
        userService.deleteByIds(ids);
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

    @RequestMapping(value = "/findUserInfoByNoPickupMail")
    public JsonResult findUserInfoByNoPickupMail() {
        JsonResult result = JsonResult.getInstance();
        List<UserInfo> userList = userService.findUserInfoByNoPickupMail();
        result.setData(userList);
        return result;
    }

}
