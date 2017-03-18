package com.zy.zmail.server.starter;

import com.zy.zmail.server.delivery.service.DeliveryLogService;
import com.zy.zmail.server.north.DoorConnection;
import com.zy.zmail.server.north.DoorSystemRunner;
import com.zy.zmail.server.setting.entity.SystemOption;
import com.zy.zmail.server.setting.service.OptionService;
import com.zy.zmail.server.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Created by wenliz on 2017/3/8.
 */
@Component
public class SystemStarter implements ApplicationRunner{
    public static DoorSystemRunner doorSystemRunner;

    @Autowired
    OptionService optionService;
    @Autowired
    UserService userService;
    @Autowired
    DeliveryLogService deliveryLogService;

    @Override
    public void run(ApplicationArguments args) throws Exception{
        SystemOption doorServer = optionService.getById(SystemOption.DOOR_SERVER_IP_ID);
        SystemOption doorServerPort = optionService.getById(SystemOption.DOOR_SERVER_PORT_ID);
        SystemOption doorProtocol = optionService.getById(SystemOption.DOOR_PROTOCOL_ID);
        SystemOption buildingNo = optionService.getById(SystemOption.BUILDING_NO_ID);
        SystemOption unitNo = optionService.getById(SystemOption.UNIT_NO_ID);

        DoorConnection connection = new DoorConnection();
        connection.setBuildingNo(buildingNo.getIntValue());
        connection.setUnitNo(unitNo.getIntValue());
        connection.setProtocolType(doorProtocol.getIntValue());
        connection.setServerIp(doorServer.getCharValue());
        connection.setServerPort(doorServerPort.getIntValue());
        doorSystemRunner = new DoorSystemRunner(connection, userService, deliveryLogService);

        (new Thread(doorSystemRunner)).start();
    }
}
