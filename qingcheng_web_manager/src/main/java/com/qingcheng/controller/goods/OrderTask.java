package com.qingcheng.controller.goods;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class OrderTask {

    @Scheduled(cron = "5/3 * * * * ?")
    public void orderTimeLogic() {
        System.out.println(new Date());
    }
}
