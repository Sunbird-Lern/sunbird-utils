package org.sunbird.auth.verifier;

import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.PropertiesCache;

import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class KeyManager {

    private static PropertiesCache propertiesCache = PropertiesCache.getInstance();

    private static Map<String, KeyData> keyMap = new HashMap<String, KeyData>();

    public static void init() {
        System.out.println("keymanager init called");
        String basePath = null;
        String keyPrefix = null;
        try {
            ProjectLogger.log("KeyManager:init: Start", LoggerEnum.INFO.name());
            basePath = propertiesCache.getProperty(JsonKey.ACCESS_TOKEN_PUBLICKEY_BASEPATH);
            keyPrefix = propertiesCache.getProperty(JsonKey.ACCESS_TOKEN_PUBLICKEY_KEYPREFIX);
            int keyCount = Integer.parseInt(propertiesCache.getProperty(JsonKey.ACCESS_TOKEN_PUBLICKEY_KEYCOUNT));
          //  int keyCount = 1;
            ProjectLogger.log("KeyManager:init: basePath: "+basePath+ " keyPrefix: "+keyPrefix+ " keys count: "+keyCount, LoggerEnum.INFO.name());
            System.out.println("KeyManager:init: basePath: "+basePath+ " keyPrefix: "+keyPrefix+ " keys count: "+keyCount);
            for(int i = 1; i <= keyCount; i++) {
                String keyId = keyPrefix + i;
             //   KeyData keyData = new KeyData("accessv1_key2", loadPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0U3RhBTXPIQ6lBXhvIrSB29/WBsPn1VGbTXUbTP/Yvlifol64g/mAxEsX4rVLIA1IGGji5QflKZ1kUScx4RZyL7Xsj6NlTkoLBevg8DsevfQfS2zWcLUxIokZptgkaAO1/5soNjj/5UUBFcHCvVGxH4/qdof/Prti8rKdK0I39QnGopdsQImSgKzA34wjXNX8B9vZYMicdSIpcp5SqH+UmAz7vS1Bu+TT/TtVRPiSxZZc/EHybCv1w2VV3Z50Yq4zW5rQr49PDA+/eara5Sh0dJybG79dXkO1ElEaiyFcApZrnL2utURpfzaSzZ+OVQa9fBfdyqQdVAnyCCG3TCj5wIDAQAB"));
              //  System.out.println("keyData : " + keyData);
              //  keyMap.put("accessv1_key2", keyData);
                keyMap.put(keyId, new KeyData(keyId, loadPublicKey(basePath + keyId)));
            }
            System.out.println("keyMap : " + keyMap);
        } catch (Exception e) {
            ProjectLogger.log("KeyManager:init: exception in loading publickeys ", LoggerEnum.ERROR.name());
            e.printStackTrace();
        }

    }

    public static KeyData getPublicKey(String keyId) {
        return keyMap.get(keyId);
    }

    private static PublicKey loadPublicKey(String path) throws Exception {
        FileInputStream in = new FileInputStream(path);
        byte[] keyBytes = new byte[in.available()];
        in.read(keyBytes);
        in.close();
        String publicKey = new String(keyBytes, "UTF-8");
        publicKey = publicKey
                .replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?)", "");
        keyBytes = Base64Util.decode(publicKey.getBytes("UTF-8"), Base64Util.DEFAULT);

        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(X509publicKey);
    }
//    private static PublicKey loadPublicKey(String publicKey) throws Exception {
//        byte[] keyBytes = Base64Util.decode(publicKey.getBytes("UTF-8"), Base64Util.DEFAULT);
//
//        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(keyBytes);
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        return kf.generatePublic(X509publicKey);
//
//    }

}
