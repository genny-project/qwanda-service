package org.wildfly.swarm.examples.keycloak;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Ken Finnigan
 */
@ApplicationScoped
public class PersistenceHelper {

    @PersistenceContext(unitName = "MyPU")
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }
}
