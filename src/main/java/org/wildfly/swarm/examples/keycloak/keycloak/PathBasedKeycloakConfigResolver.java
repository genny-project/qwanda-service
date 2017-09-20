package org.wildfly.swarm.examples.keycloak.keycloak;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.OIDCHttpFacade;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 *
 * @author Juraci Paixão Kröhling <juraci at kroehling.de>
 */
public class PathBasedKeycloakConfigResolver implements KeycloakConfigResolver {

    private final Map<String, KeycloakDeployment> cache = new ConcurrentHashMap<String, KeycloakDeployment>();

    @Override
    public KeycloakDeployment resolve(final OIDCHttpFacade.Request request) {
        final String path = request.getURI();
        System.out.println("Keycloak Deployment Path incoming request:"+path);
        final int multitenantIndex = path.indexOf("multitenant/");
        if (multitenantIndex == -1) {
            throw new IllegalStateException("Not able to resolve realm from the request path!");
        }

        String realm = path.substring(path.indexOf("multitenant/")).split("/")[1];
        if (realm.contains("?")) {
            realm = realm.split("\\?")[0];
        }

        KeycloakDeployment deployment = cache.get(realm);
        if (null == deployment) {
            // not found on the simple cache, try to load it from the file system
            final InputStream is = getClass().getResourceAsStream("/" + realm + "-keycloak.json");
            if (is == null) {
                throw new IllegalStateException("Not able to find the file /" + realm + "-keycloak.json");
            }
            deployment = KeycloakDeploymentBuilder.build(is);
            cache.put(realm, deployment);
        }

        return deployment;
    }

}

//import org.keycloak.adapters.KeycloakConfigResolver;
//import org.keycloak.adapters.KeycloakDeployment;
//import org.keycloak.adapters.KeycloakDeploymentBuilder;
//import java.io.InputStream;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class PathBasedKeycloakConfigResolver implements KeycloakConfigResolver {
//
//  private final Map<String, KeycloakDeployment> cache = new ConcurrentHashMap<String, KeycloakDeployment>();
//
//    @Override
//    public KeycloakDeployment resolve(OIDCHttpFacade.Request request) {
//        if (path.startsWith("alternative")) {
//            KeycloakDeployment deployment = cache.get(realm);
//            if (null == deployment) {
//                InputStream is = getClass().getResourceAsStream("/tenant1-keycloak.json");
//                return KeycloakDeploymentBuilder.build(is);
//            }
//        } else {
//            InputStream is = getClass().getResourceAsStream("/default-keycloak.json");
//            return KeycloakDeploymentBuilder.build(is);
//        }
//    }
//
//}