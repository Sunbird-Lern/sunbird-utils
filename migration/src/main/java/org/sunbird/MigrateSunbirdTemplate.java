package org.sunbird;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sunbird.util.HttpClientUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

public class MigrateSunbirdTemplate {
    private static final String sunbirdRecepientName = "${recipientName}";
    private static final String sunbirdQrCodeImage = "${qrCodeImage}";
    private static final String sunbirdCourseName = "${courseName}";
    private static final String sunbirdIssuedDate = "${issuedDate}";
    private static final String sunbirdMaxFontSize = "${maxFontSize}";
    private static final String sunbirdMinFontSize = "${minFontSize}";
    private static final String rcRecepientName = "{{credentialSubject.recipientName}}";
    private static final String rcQrCodeImage = "{{qrCode}}";
    private static final String rcCourseName = "{{credentialSubject.trainingName}}";
    private static final String rcIssuedDate = "{{dateFormat issuanceDate \"DD MMMM  YYYY\"}}";
    private static final String rcMaxFontSize = "{{maxFontSize}}";
    private static final String rcMinFontSize = "{{minFontSize}}";
    private static FileWriter fileWriter = null;
    private static BufferedWriter bufferedWriter = null;

    public static void main(String[] args) {
        String domain = args[0];
        String offset = args[1];
        String limit = args[2];

        String processName = "";
        String oldFontUrl = "";
        String cnameUrl = "";
        if (args.length > 3) {
            processName = args[3];
            oldFontUrl = args[4];
            cnameUrl = args[5];
        }

        try {
            fileWriter = new FileWriter("config", true);
            bufferedWriter = new BufferedWriter(fileWriter);
            new HashMap();
            String uri = "https://" + domain + "/api/content/v1/search";
            Map<String, Object> req = new HashMap();
            Map<String, Object> request = new HashMap();
            Map<String, Object> filters = new HashMap();
            List<String> certTypes = new ArrayList();
            certTypes.add("cert template layout");
            certTypes.add("cert template");
            filters.put("certType", certTypes);
            filters.put("mediaType", "image");
            request.put("filters", filters);
            String[] fields = new String[]{"artifactUrl", "identifier"};
            request.put("fields", fields);
            request.put("offset", Integer.parseInt(offset));
            request.put("limit", Integer.parseInt(limit));
            req.put("request", request);
            Map<String, String> headers = new HashMap();
            headers.put("Content-Type", "application/json");
            headers.put("Accept", "application/json");
            Map<String, Object> response = post(req, headers, uri);
            System.out.println("Response : " + response);
            if (MapUtils.isNotEmpty(response)) {
                Map<String, Object> result = (Map)response.get("result");
                if (MapUtils.isNotEmpty(result)) {
                    int count = (Integer)result.get("count");
                    List<Map<String, String>> list = (List)result.get("content");
                    if (count > 0 && CollectionUtils.isNotEmpty(list)) {
                        Iterator var15 = list.iterator();

                        while(var15.hasNext()) {
                            Map<String, String> map = (Map)var15.next();
                            String url = (String)map.get("artifactUrl");
                            String identifier = (String)map.get("identifier");
                            String[] strArray = url.split("/");
                            String fileName = strArray[strArray.length - 1];
                            migrate(identifier, fileName, url, processName, oldFontUrl, cnameUrl);
                            bufferedWriter.write(map.toString());
                            bufferedWriter.write("\n");
                        }
                    }
                }
            }
        } catch (Exception var29) {
            System.out.println("Exception while writing file");
            var29.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
            } catch (IOException var28) {
                System.out.println("Exception while closing the file.");
            }

        }

    }

    public static Map<String, Object> post(Map<String, Object> requestBody, Map<String, String> headers, String uri) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            HttpClientUtil client = HttpClientUtil.getInstance();
            String reqBody = mapper.writeValueAsString(requestBody);
            System.out.println("Composite search api called.");
            String response = client.post(uri, reqBody, headers);
            System.out.println("Composite search api response." + response);
            return (Map)mapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception var7) {
            System.out.println("Composite search api call: Exception occurred = ");
            var7.printStackTrace();
            return new HashMap();
        }
    }

    private static void migrate(String identifier, String fileName, String url, String processName, String oldFontUrl, String cnameUrl) {
        BufferedWriter bw = null;

        try {
            File file = new File(identifier);
            boolean isCreated = file.mkdir();
            if (isCreated) {
                String var10002 = file.getAbsolutePath();
                File newFile = new File(var10002 + File.separator + fileName);
                FileWriter myWriter = new FileWriter(newFile, true);
                bw = new BufferedWriter(myWriter);
                URL svg = new URL(url);
                BufferedReader br = new BufferedReader(new InputStreamReader(svg.openStream()));

                String st;
                while((st = br.readLine()) != null) {

                    if (processName.contains("font_migration")) {
                        st = st.replace(oldFontUrl, cnameUrl);
                    } else {

                        if (st.contains("${recipientName}")) {
                            st = st.replace("${recipientName}", "{{credentialSubject.recipientName}}");
                        }

                        if (st.contains("${qrCodeImage}")) {
                            st = st.replace("${qrCodeImage}", "{{qrCode}}");
                        }

                        if (st.contains("${courseName}")) {
                            st = st.replace("${courseName}", "{{credentialSubject.trainingName}}");
                        }

                        if (st.contains("${issuedDate}")) {
                            st = st.replace("${issuedDate}", "{{dateFormat issuanceDate \"DD MMMM  YYYY\"}}");
                        }

                        if (st.contains("${maxFontSize}")) {
                            st = st.replace("${maxFontSize}", "{{maxFontSize}}");
                        }

                        if (st.contains("${minFontSize}")) {
                            st = st.replace("${minFontSize}", "{{minFontSize}}");
                        }
                    }

                    bw.write(st);
                    bw.newLine();
                }
            }
        } catch (Exception var19) {
            var19.printStackTrace();
            System.out.println("Exception while writing svg file.");
        } finally {
            try {
                bw.close();
            } catch (IOException var18) {
                System.out.println("Exception while closing the file.");
            }

        }

    }
}