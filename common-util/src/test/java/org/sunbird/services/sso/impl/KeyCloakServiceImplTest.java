package org.sunbird.services.sso.impl;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.sunbird.common.models.util.JsonKey;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author arvind
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KeyCloakServiceImplTest {

    KeyCloakServiceImpl keyCloakService = new KeyCloakServiceImpl();

    private static String userId ;
    final String userName = UUID.randomUUID().toString().replaceAll("-", "");

    @BeforeClass
    public static void setup(){

    }

    @Test
    public void createUserTest(){

        System.out.println("UN : "+userName);

        Map<String , Object> request = new HashMap<String , Object>();
        request.put(JsonKey.USERNAME , userName);
        request.put(JsonKey.PASSWORD , "password");
        request.put(JsonKey.FIRST_NAME , "A");
        request.put(JsonKey.LAST_NAME , "B");

        userId = keyCloakService.createUser(request);
        System.out.println("USER ID "+userId);
        Assert.assertNotNull(userId);
    }

    @Test
    public void updateUserTest(){

        Map<String , Object> request = new HashMap<String , Object>();
        request.put(JsonKey.USER_ID , userId);
        request.put(JsonKey.FIRST_NAME , userName);
        String result = keyCloakService.updateUser(request);
        Assert.assertNotNull(result);
    }

    @Test
    public void vremoveUserTest(){

        Map<String , Object> request = new HashMap<String , Object>();
        request.put(JsonKey.USER_ID , userId);
        String result = keyCloakService.removeUser(request);
        Assert.assertNotNull(result);
    }
}
