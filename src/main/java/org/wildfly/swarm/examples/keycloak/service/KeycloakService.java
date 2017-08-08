package org.wildfly.swarm.examples.keycloak.service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
////import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.util.JsonSerialization;




public class KeycloakService {

	String keycloakUrl = null;
	String realm = null;
	String username = null;
	String password = null;

	String clientid = null;
	String secret = null;
	
	AccessTokenResponse accessToken = null;
	Keycloak keycloak = null;
	
	public  KeycloakService(String keycloakUrl, String realm, String username, String password, String clientid, String secret) throws IOException {

		
		this.keycloakUrl = keycloakUrl;
		this.realm = realm;
		this.username  = username;
		this.password = password;
		this.clientid = clientid;
		this.secret = secret;
	
		
		accessToken = getToken();
		
	}
//	
	private  AccessTokenResponse getToken() throws IOException {

	    HttpClient httpClient = new DefaultHttpClient();

	    try {
	        HttpPost post = new HttpPost(KeycloakUriBuilder.fromUri(keycloakUrl+"/auth")
	                .path(ServiceUrlConstants.TOKEN_PATH).build(realm));
	        
	        post.addHeader("Content-Type", "application/x-www-form-urlencoded");
	        
	        List <NameValuePair> formParams = new ArrayList <NameValuePair>();
	        formParams.add(new BasicNameValuePair("username", username));
	        formParams.add(new BasicNameValuePair("password", password));
	        formParams.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, "password"));
	        formParams.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ID, "security-admin-console"));
	        formParams.add(new BasicNameValuePair(OAuth2Constants.CLIENT_SECRET, secret));
	        UrlEncodedFormEntity form = new UrlEncodedFormEntity(formParams, "UTF-8");
	        
	        post.setEntity(form);

	        HttpResponse response = httpClient.execute(post);
	        
	        int statusCode = response.getStatusLine().getStatusCode();
	        HttpEntity entity = response.getEntity();
	        String content = null;
	        if (statusCode != 200) {
	            content = getContent(entity);
	            throw new IOException(""+statusCode);
	        }
	        if (entity == null) {
	            throw new IOException("Null Entity");
	        } else {
	        		content = getContent(entity);
	        }
	        return JsonSerialization.readValue(content, AccessTokenResponse.class);
	    } finally {
	        httpClient.getConnectionManager().shutdown();
	    }
	}

	public static String getContent(HttpEntity httpEntity) throws IOException {
	    if (httpEntity == null) return null;
	    InputStream is = httpEntity.getContent();
	    try {
	        ByteArrayOutputStream os = new ByteArrayOutputStream();
	        int c;
	        while ((c = is.read()) != -1) {
	            os.write(c);
	        }
	        byte[] bytes = os.toByteArray();
	        String data = new String(bytes);
	        return data;
	    } finally {
	        try {
	            is.close();
	        } catch (IOException ignored) {

	        }
	    }

	}
	
	public List<LinkedHashMap> fetchKeycloakUsers()
	{
	    HttpClient client = new DefaultHttpClient();
	    try {
	        HttpGet get = new HttpGet(this.keycloakUrl+"/auth/admin/realms/"+this.realm+"/users");
	        get.addHeader("Authorization", "Bearer " + this.accessToken.getToken());
	        try {
	            HttpResponse response = client.execute(get);
	            if (response.getStatusLine().getStatusCode() != 200) {
	                throw new IOException();
	            }
	            HttpEntity entity = response.getEntity();
	            InputStream is = entity.getContent();
	            try {
	            	return JsonSerialization.readValue(is,(new ArrayList<UserRepresentation>()).getClass() );
	            } finally {
	                is.close();
	            }
	        } catch (IOException e) {
	            throw new RuntimeException(e);
	        }
	    } finally {
	        client.getConnectionManager().shutdown();
	    }
	}
}
