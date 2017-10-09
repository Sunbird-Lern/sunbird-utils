package org.sunbird.common.models.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.velocity.VelocityContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;

/**
 * Created by arvind on 6/10/17.
 */
public class ProjectUtilTest {

  private  PropertiesCache propertiesCache = ProjectUtil.propertiesCache;

  @BeforeClass
  public static void setUp(){

  }

  @Test
  public void testMailTemplateContextNameAsent(){

    Map<String , Object> templateMap = new HashMap<>();
    templateMap.put(JsonKey.ACTION_URL, "googli.com");

    VelocityContext context = ProjectUtil.getContext(templateMap);
    Assert.assertEquals(false ,context.internalContainsKey(JsonKey.NAME));

  }

  @Test
  public void testMailTemplateContextActionUrlAbsent(){

    Map<String , Object> templateMap = new HashMap<>();
    templateMap.put(JsonKey.NAME, "userName");

    VelocityContext context = ProjectUtil.getContext(templateMap);
    Assert.assertEquals(false ,context.internalContainsKey(JsonKey.ACTION_URL));

  }

  @Test
  public void testMailTemplateContextCheckFromMail(){

    Map<String , Object> templateMap = new HashMap<>();
    templateMap.put(JsonKey.ACTION_URL, "googli.com");
    templateMap.put(JsonKey.NAME, "userName");


    boolean envVal = !ProjectUtil.isStringNullOREmpty(System.getenv(JsonKey.EMAIL_SERVER_FROM));
    boolean cacheVal = propertiesCache.getProperty(JsonKey.EMAIL_SERVER_FROM)!=null;

    VelocityContext context = ProjectUtil.getContext(templateMap);
    if(envVal){
      Assert.assertEquals(System.getenv(JsonKey.EMAIL_SERVER_FROM) , (String)context.internalGet(JsonKey.FROM_EMAIL));
    }else if(cacheVal){
      Assert.assertEquals(propertiesCache.getProperty(JsonKey.EMAIL_SERVER_FROM) , (String)context.internalGet(JsonKey.FROM_EMAIL));
    }

  }

  @Test
  public void testMailTemplateContextCheckOrgImageUrl(){

    Map<String , Object> templateMap = new HashMap<>();
    templateMap.put(JsonKey.ACTION_URL, "googli.com");
    templateMap.put(JsonKey.NAME, "userName");


    boolean envVal = !ProjectUtil.isStringNullOREmpty(System.getenv(JsonKey.SUNBIRD_ENV_LOGO_URL));
    boolean cacheVal = propertiesCache.getProperty(JsonKey.SUNBIRD_ENV_LOGO_URL)!=null;

    VelocityContext context = ProjectUtil.getContext(templateMap);
    if(envVal){
      Assert.assertEquals(System.getenv(JsonKey.SUNBIRD_ENV_LOGO_URL) , (String)context.internalGet(JsonKey.ORG_IMAGE_URL));
    }else if(cacheVal){
      Assert.assertEquals(propertiesCache.getProperty(JsonKey.SUNBIRD_ENV_LOGO_URL) , (String)context.internalGet(JsonKey.ORG_IMAGE_URL));
    }

  }

}
