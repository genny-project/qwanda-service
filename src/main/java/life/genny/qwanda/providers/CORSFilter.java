package life.genny.qwanda.providers;
//
// import java.io.IOException;
//
// import javax.ws.rs.container.ContainerRequestContext;
// import javax.ws.rs.container.ContainerResponseContext;
// import javax.ws.rs.container.ContainerResponseFilter;
// import javax.ws.rs.ext.Provider;
//
//
/// **
// * @author Yoshimasa Tanabe
// */
// @Provider
// public class CORSFilter implements ContainerResponseFilter {
//
// @Override
// public void filter(ContainerRequestContext requestContext, ContainerResponseContext
// responseContext) throws IOException {
// responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
// responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE,
// OPTIONS");
// responseContext.getHeaders().add("Access-Control-Max-Age", "-1");
// responseContext.getHeaders().add("Access-Control-Allow-Headers", "Origin, X-Requested-With,
// Content-Type, Accept");
// }
//
// }
//

import org.apache.logging.log4j.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.invoke.MethodHandles;


@Provider
public class CORSFilter implements ContainerResponseFilter {
  /**
   * Stores logger object.
   */
  protected static final Logger log = org.apache.logging.log4j.LogManager
      .getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());

  @Context
  private HttpServletRequest request;

  @Override
  public void filter(final ContainerRequestContext requestContext,
      final ContainerResponseContext responseContext) throws IOException {
    final String requestOrigin = request.getHeader("origin");
    final String allowedOrigins = System.getenv("CORS_ALLOWED_ORIGINS"); // ACC: This could come
                                                                         // form keycloak jwt?
    log.debug("RequestOrigin:" + requestOrigin + " AllowedOrigins=[" + allowedOrigins + "]");
    log.debug("sysReq=" + request.getHeader("Authorization"));
    if ((allowedOrigins != null) && (requestOrigin != null)) {
      // String[] originList = allowedOrigins.split(",");
      // for(String origin: originList){
      if (allowedOrigins.contains(requestOrigin)) {
        log.debug("requestOrigin Match" + requestOrigin);
        /* Origins match let's allow it to access the API */
        responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", requestOrigin);
        responseContext.getHeaders().putSingle("Access-Control-Allow-Methods",
            "OPTIONS, GET, POST, PUT, DELETE");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Headers",
            "Content-Type, Authorization, X-Requested-With, Accept");
        responseContext.getHeaders().putSingle("Access-Control-Max-Age", "-1");
      }
    }
    // }

  }

}
