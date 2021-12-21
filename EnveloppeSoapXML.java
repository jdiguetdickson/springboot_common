package com.thermador.formation.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Enveloppe Soap pour GCE
 */
@Data
@Component
@NoArgsConstructor
public class EnveloppeSoapXML {

    private static final String ENCODAGE_UTF_8 = "UTF-8";
    public HttpURLConnection con;
    public InputStream is = null;
    StringBuilder req = new StringBuilder(1024);
    String bv;
    String cinematic;
    String entity;
    String soapEnvHeaderGCE201;
    String cinematicGCE201 = "<com:cinematic>%</com:cinematic>\n";
    String paramAndValue = " <com:param name=\"chp:%1%\" value=\"%2%\"/>\n";
    String frameAndValue = " <com:param name=\"frame\" value=\"%2%\"/>\n";
    String targetPevAndValue = " <com:param name=\"targetPev\" value=\"%2%\"/>\n";
    String selAndValue = " <com:param name=\"sel:%1%\" value=\"%2%\"/>\n";

    public EnveloppeSoapXML(final String pBV, final String pCinematic, final Map<String, String> mapParams, final String entity) {
        this.entity = entity;
        this.soapEnvHeaderGCE201 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:GCE\" xmlns:com=\"http://www.generix.fr/technicalframework/businesscomponent/applicationmodule/common\" xmlns:ser=\"http://www.generix.fr/technicalframework/business/webservice/server\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:execute>\n" +
                "         <com:BusinessViewServiceexecuteIn>\n" +
                "            <com:context>\n" +
                "               <com:entity>" + this.entity + "</com:entity>\n" +
                "               <!--Optional:-->\n" +
                "               <com:identification>\n" +
                "                  <com:code>SMX</com:code>\n" +//variabilisé code uti
                "               </com:identification>\n" +
                "            </com:context>\n" +
                "            <com:businessView>%</com:businessView>\n";
        this.req.append("<?xml version='1.0' encoding='");
        this.req.append(ENCODAGE_UTF_8);
        this.req.append("'?>");
        this.bv = pBV;
        this.req.append(this.soapEnvHeaderGCE201.replace("%", pBV));
        this.cinematic = pCinematic;
        this.req.append(this.cinematicGCE201.replace("%", pCinematic));

        for (final Map.Entry<String, String> entry : mapParams.entrySet()) {
            final String cle = entry.getKey();
            String valeur = entry.getValue();
            String temp = "";
            if (!cle.equals("frame") && !cle.equals("targetPev")) {
                temp = this.paramAndValue.replace("%1%", cle);
                if (null == valeur) {
                    valeur = "";
                }
                temp = temp.replace("%2%", valeur);
            } else {
                if (cle.equals("frame")) {
                    temp = this.frameAndValue.replace("%2%", valeur);
                } else if (cle.equals("targetPev")) {
                    temp = this.targetPevAndValue.replace("%2%", valeur);
                } else {
                    temp = valeur;
                }
            }
            this.req.append(temp);
        }
        this.req.append("</com:BusinessViewServiceexecuteIn>\n" +
                "      </urn:execute>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>\n");

    }

    public EnveloppeSoapXML(final String pBV, final String pCinematic, final Map<String, String> mapParams, final String entity, final Map<String, String> selParams) {
        this.entity = entity;
        this.soapEnvHeaderGCE201 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:GCE\" xmlns:com=\"http://www.generix.fr/technicalframework/businesscomponent/applicationmodule/common\" xmlns:ser=\"http://www.generix.fr/technicalframework/business/webservice/server\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:execute>\n" +
                "         <!--Optional:-->\n" +
                "         <!--com:ctx entity=\"1\" language=\"FRA\" target=\"MENU\" user=\"GNC\" password=\"INFOR1\"-->\n" +
                "         <!-- /com:ctx -->\n" +
                "         <com:BusinessViewServiceexecuteIn>\n" +
                "            <com:context>\n" +
                "               <com:entity>" + this.entity + "</com:entity>\n" +
                "               <!--Optional:-->\n" +
                "               <com:identification>\n" +
                "                  <com:code>SMX</com:code>\n" +//variabilisé code uti
                "               </com:identification>\n" +
                "            </com:context>\n" +
                "            <com:businessView>%</com:businessView>\n";
        this.req.append("<?xml version='1.0' encoding='");
        this.req.append(ENCODAGE_UTF_8);
        this.req.append("'?>");
        this.bv = pBV;
        this.req.append(this.soapEnvHeaderGCE201.replace("%", pBV));
        this.cinematic = pCinematic;
        this.req.append(this.cinematicGCE201.replace("%", pCinematic));

        for (final Map.Entry<String, String> entry : mapParams.entrySet()) {
            final String cle = entry.getKey();
            String valeur = entry.getValue();

            String temp = "";
            if (!cle.equals("frame")) {
                temp = this.paramAndValue.replace("%1%", cle);
                if (null == valeur) {
                    valeur = "";
                }
                temp = temp.replace("%2%", valeur);
            } else {
                temp = this.frameAndValue.replace("%2%", valeur);
            }
            this.req.append(temp);
        }
        for (final Map.Entry<String, String> entry : selParams.entrySet()) {
            final String cle = entry.getKey();
            String valeur = entry.getValue();
            String temp = this.selAndValue.replace("%1%", cle);
            if (null == valeur) {
                valeur = "";
            }
            temp = temp.replace("%2%", valeur);
            this.req.append(temp);
        }
        this.req.append("</com:BusinessViewServiceexecuteIn>\n" +
                "      </urn:execute>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>\n");

    }


}
