/**
 * 
 */
package org.sunbird.common.models.util.azure;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.microsoft.azure.storage.blob.CloudBlobContainer;

/**
 * @author Manzarul
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AzureServiceFactoryTest {
  
  private static Object obj = null;
  private static CloudBlobContainer container = null;
  private static CloudBlobContainer container1 = null;
  private static String containerName ="testcontainer"; 
  @Test
  public void getObject() {
   obj = CloudServiceFactory.get("Azure");
   Assert.assertTrue(obj instanceof CloudService);
   Assert.assertNotNull(obj); 
  }
  
  @Test
  public void getObjectWithWrongType() {
    Object obj = CloudServiceFactory.get("Azure12");
   Assert.assertNull(obj); 
  } 
  
  @Test
  public void pcheckObjectForSingleton() {
    Object obj1 = CloudServiceFactory.get("Azure");
   Assert.assertTrue(obj.equals(obj1) );
   Assert.assertNotNull(obj1); 
  }
  
  @Test
  public void checkContainerWithAccessPublic () {
    container = AzureConnectionManager.getContainer(containerName, true);
    Assert.assertNull(container);
  }
 
  @Test(expected = NullPointerException.class)
  public void checkContainerWithOutPublicAccess () {
    container1 = AzureConnectionManager.getContainerReference(containerName);
    Assert.assertNull(container1);
  }
  
  @Test(expected = NullPointerException.class)
  public void deleteContainer () {
    boolean response = AzureConnectionManager.deleteContainer(containerName);
    Assert.assertFalse(response);
  }
 
  @Test(expected = NullPointerException.class)
  public void uploadFileTest () {
    CloudService service = (CloudService) obj;
   String url = service.uploadFile(containerName, new File("test.txt"));
   Assert.assertEquals(null, url);
  }
  
  @Test(expected = NullPointerException.class)
  public void uploadFileWithOutContainerNameTest () {
    CloudService service = (CloudService) obj;
   String url = service.uploadFile("",new File("test.txt"));
   Assert.assertEquals(null, url);
  } 
  
  @Test(expected = NullPointerException.class)
  public void uploadFileWithMultiplePathTest () {
    CloudService service = (CloudService) obj;
   String url = service.uploadFile("/tez/po/"+containerName,new File("test.txt"));
   Assert.assertEquals(null, url);
  } 
  
  
  
  @AfterClass
  public static void teardown() {
    container1 = null;
    container = null;
  }
}
