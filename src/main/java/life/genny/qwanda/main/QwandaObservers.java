package life.genny.qwanda.main;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import life.genny.qwanda.entity.BaseEntity;
import life.genny.qwanda.service.BaseEntityService;

/**
 * This CDI bean observes transactional events.
 * Same event type is observed when transaction succeed or fail and keep track of these successes or failures
 *
 * @author Adam Crow
 */
@ApplicationScoped
public class QwandaObservers {

    /**
     * Observes {@link BaseEntity} event type in case of transaction failure.
     * Log a failure message and use it to invoke {@link BaseEntityService#addRollbackMsg(String)}
     *
     * @param emp payload
     */
    void processTxFailure(@Observes(during = TransactionPhase.AFTER_FAILURE) BaseEntity emp) {
        String msg = "*** An error occurred and deletion of emp # " + emp.getId() + " was roll-backed";
        LOG.info(msg);
        service.addRollbackMsg(msg);
    }

    /**
     * Observes {@link BaseEntity} event type in case of transaction success.
     * Log a success message and use it to invoke {@link BaseEntityService#addCommitMsg(String)}
     *
     * @param emp payload
     */
    void processTxSuccess(@Observes(during = TransactionPhase.AFTER_SUCCESS) BaseEntity emp) {
        String msg = "*** baseEntity # " + emp.getId() + " was committed";
        LOG.info(msg);
        service.addCommitMsg(msg);
    }

    final static Logger LOG = Logger.getLogger(QwandaObservers.class);

    @Inject
    BaseEntityService service;


}