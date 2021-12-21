package com.thermador.formation.service;

import com.thermador.formation.util.EnveloppeSoapXML;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
@Service
@Data
public class WsgceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WsgceService.class);

    private static final String PROPERTY_CONTENT_LENGTH = "Content-Length";
    private static final String PROPERTY_CONTENT_TYPE = "Content-type";
    private static final String PROPERTY_CONTENT_TYPE_XML = "text/xml; charset=utf-8";
    private static final String SOAPURLService = "businessViewService";
    private static final String PROPERTY_SOAPAction = "SOAPAction";
    private static final String METHOD_POST = "POST";
    private static final String CINEMATIC_FORWARD_0 = "forward(0);";

    @Autowired
    private EnveloppeSoapXML enveloppeSoapXML;
    @Value("${wsgceUrl}")
    private String wsgceUrl;

    public String callWebserviceGCE(String viewName, final String entity, String sigtie) throws Exception  {

        final StringBuilder result = new StringBuilder();
        HttpURLConnection con = null;
        InputStream is = null;

        if (null == entity) {
            LOGGER.error("WS_ERROR - getOrders - entite doit etre definit");
            throw new Exception("WS_ERROR - getOrders - entite doit etre definit");
        }
        final Map<String, String> mapParamValeur = new HashMap<>();
        mapParamValeur.put("Codsoc", entity);
        mapParamValeur.put("Typeve", "CDE");
        mapParamValeur.put("Achvte", "V");
        mapParamValeur.put("Sigtie", sigtie);

        this.enveloppeSoapXML = new EnveloppeSoapXML(viewName, CINEMATIC_FORWARD_0, mapParamValeur, entity);

        final byte[] b = this.enveloppeSoapXML.getReq().toString().getBytes(StandardCharsets.UTF_8);

        con = this.getURLConnection(b);
        con.setRequestProperty("SMX", "SMX-MAG-CDE" );
        con.setRequestProperty("UUID", MDC.get("uuid"));
        try {

            final OutputStream out = con.getOutputStream();
            out.write(b);
            out.close();

            if (con.getResponseCode() == 500) {
                is = con.getErrorStream();
            } else {
                is = con.getInputStream();
            }
            final InputStreamReader isr = new InputStreamReader(is);
            final BufferedReader in = new BufferedReader(isr);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
            }
        } catch (final IOException e) {
            throw new Exception(e);
        } finally {
            this.closeConnectionAndInpuStream(con, is);
        }
        if (result.toString().trim().equals("")) {
            return "EMPTY RESPONSE";
        } else {
            return result.toString();
        }
    }
    private void closeConnectionAndInpuStream(final HttpURLConnection con, final InputStream is) throws IOException {
        if (con != null) {
            con.disconnect();
        }
        if (is != null) {
            is.close();
        }
    }

    private HttpURLConnection getURLConnection(final byte[] b) {
        HttpURLConnection con = null;
        try {
            final URL url = new URL(this.wsgceUrl + SOAPURLService);
            LOGGER.info("URL = " + this.wsgceUrl + SOAPURLService);

            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty(PROPERTY_CONTENT_LENGTH, String.valueOf(b.length));
            con.setRequestProperty(PROPERTY_CONTENT_TYPE, PROPERTY_CONTENT_TYPE_XML);
            con.setRequestProperty(PROPERTY_SOAPAction, SOAPURLService);
            con.setRequestMethod(METHOD_POST);
            con.setDoOutput(true);
            con.setDoInput(true);
        } catch (final Exception e) {
            LOGGER.error("getURLConnection", e);
        }
        return con;
    }


}
