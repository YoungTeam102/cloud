package com.igniubi.core.aysnc;

import com.igniubi.core.aysnc.annotation.AysncConsumer;
import com.igniubi.core.aysnc.annotation.AysncProvider;
import com.igniubi.core.aysnc.annotation.InvokeParameter;
import com.igniubi.core.aysnc.facade.AysncDataFacade;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

@SpringBootApplication
public class AysncApplication {

    public static void main(String[] args) throws InterruptedException, IllegalAccessException, InvocationTargetException {
        SpringApplication.run(AysncApplication.class, args);
        System.out.println(AysncDataFacade.get("user", Collections.singletonMap("userId",1),String.class));
        System.out.println(AysncDataFacade.get("user1", Collections.singletonMap("userId",1),String.class));
    }

    @AysncProvider("user1")
    public static String getUser1(@AysncConsumer("user") String user){
        return "users1 :"+ user;
    }


    @AysncProvider("user")
    public String getUser(@InvokeParameter("userId")Integer userId){
        return "user :" +userId;
    }



}
