package org.wildfly.swarm.examples.keycloak;
//
//import java.io.IOException;
//
//import javax.ws.rs.container.ContainerRequestContext;
//import javax.ws.rs.container.ContainerResponseContext;
//import javax.ws.rs.container.ContainerResponseFilter;
//import javax.ws.rs.ext.Provider;
//
//
///**
// * @author Yoshimasa Tanabe
// */
//@Provider
//public class CORSFilter implements ContainerResponseFilter {
//
//    @Override
//    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
//        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
//        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//        responseContext.getHeaders().add("Access-Control-Max-Age", "-1");
//        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
//    }
//
//}
//

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;


@Provider
public class CORSFilter implements ContainerResponseFilter {
    
    @Context
    private HttpServletRequest request;
    
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String requestOrigin = request.getHeader("origin");
        String allowedOrigins = System.getenv("CORS_ALLOWED_ORIGINS"); // ACC: This could come form keycloak jwt?
        System.out.println("RequestOrigin:"+requestOrigin+" AllowedOrigins=["+allowedOrigins+"]");
        System.out.println("sysReq="+request.getHeader("Authorization"));
        if ((allowedOrigins != null)&&(requestOrigin != null)){
//            String[] originList = allowedOrigins.split(",");
//            for(String origin: originList){
                if (allowedOrigins.contains(requestOrigin)){
                	System.out.println("requestOrigin Match"+requestOrigin);
                    /* Origins match let's allow it to access the API */
                    responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", requestOrigin);
                    responseContext.getHeaders().putSingle("Access-Control-Allow-Methods", "OPTIONS, GET, POST, PUT, DELETE");
                    responseContext.getHeaders().putSingle("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept"); 
                    responseContext.getHeaders().putSingle("Access-Control-Max-Age", "-1");
                }
            }               
 //       }
               
    }
 
}