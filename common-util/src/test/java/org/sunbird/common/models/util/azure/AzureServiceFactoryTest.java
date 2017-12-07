/**
 * 
 */
package org.sunbird.common.models.util.azure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
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
  private static String containerName = "testcontainerxyz";

  @BeforeClass
  public static void getObject() {
    obj = CloudServiceFactory.get("Azure");
    Assert.assertTrue(obj instanceof CloudService);
    Assert.assertNotNull(obj);
  }

  @Test
  public void testGetObjectWithWrongType() {
    Object obj = CloudServiceFactory.get("Azure12");
    Assert.assertNull(obj);
  }

  @Test
  public void testCheckObjectForSingleton() {
    Object obj1 = CloudServiceFactory.get("Azure");
    Assert.assertNotNull(obj1);
    Assert.assertTrue(obj.equals(obj1));
  }

  @Test
  public void testCheckContainerWithAccessPublic() {
    container = AzureConnectionManager.getContainer(containerName, true);
    Assert.assertNotNull(container);
  }

  @Test
  public void testCheckContainerWithOutPublicAccess() {
    container1 = AzureConnectionManager.getContainerReference(containerName);
    Assert.assertNotNull(container1);
  }

  @Test
  public void testUploadFile() {
    CloudService service = (CloudService) obj;
    String url = service.uploadFile(containerName, new File("test.txt"));
    Assert.assertEquals(null, url);
  }

  @Test
  public void testUploadFileWithOutContainerName() {
    CloudService service = (CloudService) obj;
    String url = service.uploadFile("", new File("test.txt"));
    Assert.assertEquals(null, url);
  }
  

  @Test
  public void testUploadFileWithMultiplePath() {
    CloudService service = (CloudService) obj;
    String url = service.uploadFile("/tez/po/" + containerName, new File("test.txt"));
    Assert.assertEquals(null, url);
  }

  @Test
  public void testUploadFileObject() {
    CloudService service = (CloudService) obj;
    String url = service.uploadFile(containerName, "test.txt", "");
    Assert.assertEquals(null, url);
  }
  
  @Test
  public void testListAllFiles() {
    CloudService service = (CloudService) obj;
    List<String> filesList = service.listAllFiles(containerName);
    Assert.assertEquals(new ArrayList<>(), filesList);
  }
  
  @Test
  public void testDownloadFile() {
    CloudService service = (CloudService) obj;
    Boolean isFileDeleted = service.downLoadFile(containerName, "test1.txt", "");
    Assert.assertFalse(isFileDeleted);
  }

  @Test
  public void testDeleteFile() {
    CloudService service = (CloudService) obj;
    Boolean isFileDeleted = service.deleteFile(containerName, "test1.txt");
    Assert.assertFalse(isFileDeleted);
  }
  
  @Test
  public void testDeleteFileWithoutContainerName() {
    CloudService service = (CloudService) obj;
    Boolean isFileDeleted = service.deleteFile("", "test.abc");
    Assert.assertFalse(isFileDeleted);
  }
  
  @Test
  public void testDeleteContainer() {
    CloudService service = (CloudService) obj;
    boolean response = service.deleteContainer(containerName);
    Assert.assertTrue(response);
  }

  @AfterClass
  public static void teardown() {
    container1 = null;
    container = null;
    obj = null;
  }
}
