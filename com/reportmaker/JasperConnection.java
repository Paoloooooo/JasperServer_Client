package com.reportmaker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.reportmaker.ResourceLookup.SearchResult;

import org.json.JSONObject;
import org.json.JSONException;

public class JasperConnection {

    private final String baseUrl = "http://192.168.181.40:8080/jasperserver/";

    /**
     *
     * Manages to login to the JasperReports server
     *
     * @param username the username to use to login
     * @param password the password to use to login
     * @return String the sessionId to use in all future requests
     * @throws IOException
     */
    public String login(String username, String password) throws IOException {
        String jsessionId = "";
        String urlLink = baseUrl + "rest_v2/login?j_username=" +
                username + "&j_password=" + password;

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        URL url = new URL(urlLink);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.connect();

        System.out.println("Login response code :" + con.getResponseCode());
        if(con.getResponseCode()!=200)
            return null;

        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
        for (HttpCookie cookie : cookies) {
            if (cookie.getName().equals("JSESSIONID"))
                return cookie.getValue();
        }
        return null;
    }

    /**
     *
     * Creates a directory on the JasperReports server
     *
     * @param dirName   the directory name
     * @param sessionId the sessionId
     * @return boolean  the result of the operation  true: directory successfully created
     *                                               false: could not create the directory
     */
    public boolean createDirectory(String dirName, String sessionId) {
        int responseCode = 0;

        String urlLink = baseUrl + "rest_v2/resources/";
        try {
            URL url = new URL(urlLink);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Cookie", "JSESSIONID=" + sessionId);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/repository.folder+json");
            con.setRequestProperty("Accept", "application/repository.folder+json");
            con.setRequestMethod("POST");
            JSONObject jsonBodyInfo = new JSONObject();
            try {
                jsonBodyInfo.put("label", dirName);
                jsonBodyInfo.put("description", "Sample Test");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(jsonBodyInfo.toString());
            wr.flush();
            con.connect();
            responseCode = con.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("creation Response Code : " + responseCode);

        if (responseCode == 201) {
            return true;
        }
        if (responseCode == 401) {
            System.out.println("Un-Authorised Access");
            return false;
        }
        return false;
    }

    /**
     *
     * Generates a report
     *
     * @param reportPath    the path of the report to generate
     * @param sessionId     the sessionId
     * @return boolean      the result of the operation true: directory successfully created
     *                                                  false: could not create the directory
     */
    public boolean generateReport(String reportPath, String reportName, String sessionId) {
        int responseCode = 0;

        try {                                    //reports/
            URL url = new URL(baseUrl + "rest_v2/reports/" + reportPath + ".pdf");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Cookie", "JSESSIONID=" + sessionId);
            con.setDoInput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/repository.folder+json");
            con.setRequestProperty("Accept", "*/*");
            con.setRequestProperty("Connection", "keep-alive");
            con.connect();

            responseCode = con.getResponseCode();
            System.out.println("res: "+responseCode);

            byte[] fileAsBytes = getArrayFromInputStream((InputStream) con.getInputStream());
            writeContent(fileAsBytes, "/storage/emulated/0/Download/"+reportName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (responseCode == 200) {
            return true;
        }
        if (responseCode == 404) {
            System.out.println("File non trovato"); //da mettere in inglese
        }
        return false;
    }

    /**
     *
     * Returns an array of bytes from an input stream
     *
     * @param inputStream the input stream from wich to get the bytes
     * @return byte[] the array made from the input stream
     * @throws IOException
     */
    private byte[] getArrayFromInputStream(InputStream inputStream) throws IOException {
        byte[] bytes;
        byte[] buffer = new byte[1024];

        try (BufferedInputStream is = new BufferedInputStream(inputStream)) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int length;
            while ((length = is.read(buffer)) > -1) {
                bos.write(buffer, 0, length);
            }
            bos.flush();
            bytes = bos.toByteArray();
        }
        return bytes;
    }

    /**
     *
     * Writes content on a file
     *
     * @param content the content to write
     * @param fileToWriteTo the file to write to
     * @throws IOException
     */
    private void writeContent(byte[] content, String fileToWriteTo) throws IOException {
        File file = new File(fileToWriteTo);

        try (BufferedOutputStream salida = new BufferedOutputStream(new FileOutputStream(file))) {
            salida.write(content);
            salida.flush();
        }
    }

    /**
     *
     * Gets a lookup of the resources on the server
     *
     * @param parameters    a map containing all the parameters needed to refine the search
     * @param sessionId     the session identifier
     * @return SearchResult the result of the search
     */
    public SearchResult getResources(Map<String, String> parameters, String sessionId) throws IOException {

        StringBuilder params = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = parameters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            params.append(entry.getKey()).append("=").append(entry.getValue());
            if (iterator.hasNext()) {
                params.append("&");
            }
        }
        URL url = new URL(baseUrl + "rest_v2/resources?" + params);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Cookie", "JSESSIONID=" + sessionId);
        con.setDoInput(true);
        con.setRequestMethod("GET");
        con.connect();
        int responseCode = con.getResponseCode();
        System.out.println(responseCode + "  url:  " + con.getURL() +
                "\nmess:  " + con.getResponseMessage());
        if (responseCode >= 300) {
            return new SearchResult(responseCode);
        }
        List<ResourceLookup> list = ResourceLookup.parse(con.getInputStream());
        return new SearchResult(list, responseCode);
    }

    private final String content_1="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<remoteXmlDataAdapter class=\"net.sf.jasperreports.data.xml.RemoteXmlDataAdapterImpl\">\n" +
            "<name>contenzioso</name>\n<fileName>repo:";

    private final String content_2="</fileName>\n<useConnection>true</useConnection>\n" +
            "<datePattern>yyyy-MM-dd</datePattern>\n<numberPattern>#,##0.##</numberPattern>\n" +
            "<locale xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"java:java.lang.String\">en</locale>\n" +
            "</remoteXmlDataAdapter>";

    private final String fileDescr_1="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<file>\n" +
            "<uri>/reports/Prova/Data_adapter</uri>\n<label>Data adapter</label>\n" +
            "<description>Sample Data Adapter</description>\n<permissionMask>0</permissionMask>\n" +
            "<creationDate>2013-07-04T12:18:47</creationDate>\n<updateDate>2013-07-04T12:18:47</updateDate>\n" +
            "<version>0</version>\n<type>xml</type>\n<content>";

    private final String fileDescr_2="</content>\n</file>";

    /**
     *
     * Updates the data adapter for the report, referencing a new xml source
     *
     * @param xmlPath   the path of the new xml
     * @param sessionId the session identifier
     * @return Integer  the result of the update
     */
    public Integer updateXml(String xmlPath,String sessionId){
        int response=-1;

        try{
            URL url = new URL(baseUrl + "rest_v2/resources" + "/reports/Prova/Data_adapter"
                    +"?overwrite=true");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Cookie", "JSESSIONID=" + sessionId);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type","application/repository.file+xml");
            con.setRequestProperty("Accept","application/json");

            con.setRequestMethod("PUT");

            String content=new String(Base64.getEncoder().encode(
                    (content_1+xmlPath+content_2).getBytes()));
            try(PrintWriter p=new PrintWriter(con.getOutputStream())){
                p.write(fileDescr_1+content+fileDescr_2);
            }

            con.connect();
            response=con.getResponseCode();
            System.out.println("Response: "+response);
        }catch(Exception ex){
            System.out.println(ex);
        }
        return response;
    }

    /**
     *
     * Logs out from the server and ends the connection
     *
     * @param sessionId the session identifier
     */
    public boolean logout(String sessionId){
        try{
            URL url = new URL(baseUrl + "logout.html");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Cookie", "JSESSIONID=" + sessionId);

            con.setRequestMethod("GET");
            System.out.println(con.getResponseMessage());
            con.disconnect();
        }catch(Exception ex){
            System.out.println(ex);
            return false;
        }
        return true;
    };

    /**
     *
     * Report - esperimenti
     *
     */
    private void report() throws IOException {

        String username = "jasperadmin";

        String password = "jasperadmin";

        String sessionId = login(username, password);

        //boolean sessionID=createDirectory("Funzioni",sessionId);
        //Map<String, String> params = new HashMap<>();
        //params.put("net.sf.jasperreports.xml.source","/reports/Prova/dati.xml");

        //updateXml("/reports/Prova/xml_nuovo",sessionId);

        //boolean report=generateReport("reports/Prova/main",sessionId);

        //System.out.println("ID:\t"+report);
        //params.put("folderUri", "/reports");
        //getResources(params, sessionId);

    }

}
