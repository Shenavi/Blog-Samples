package org.wso2.mi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

/**
 * This class decodes the JWT token which is extracted and stored as a property named "JWT_HEADER"
 */
public class JwtDecodeMediator extends AbstractMediator {

    private static final Log log = LogFactory.getLog(JwtDecodeMediator.class);
    final String END_USER_CLAIM = "http://wso2.org/claims/enduser";

    @Override
    public boolean mediate(MessageContext context) {
        if(context.getProperty("JWT_HEADER")!=null) {
            String temp_auth = ((String) context.getProperty("JWT_HEADER")).trim();
            String[] val= temp_auth.split("\\.");
            String auth=val[1];

            byte[] base64decodedBytes = Base64.decodeBase64(auth.getBytes(Charset.forName("UTF-8")));

            try {
                String decodedBody=new String(base64decodedBytes, "utf-8");

                JsonObject jwtToken = new JsonParser().parse(decodedBody).getAsJsonObject();
                String enduser = jwtToken.get(END_USER_CLAIM).getAsString();
                context.setProperty("apim-enduser",enduser);
                log.info("Printing the enduser coming from the APIM JWT token " + enduser);

            } catch (UnsupportedEncodingException e) {
                log.error("Unsupported EncodingException : " + e.getMessage());
            }
        }
        return true;
    }
}