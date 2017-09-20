package org.wildfly.swarm.examples.keycloak.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.keycloak.KeycloakSecurityContext;
import org.wildfly.swarm.examples.keycloak.models.Setup;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import life.genny.qwanda.Answer;
import life.genny.qwanda.AnswerLink;
import life.genny.qwanda.Ask;
import life.genny.qwanda.Context;
import life.genny.qwanda.GPS;
import life.genny.qwanda.Question;
import life.genny.qwanda.attribute.Attribute;
import life.genny.qwanda.attribute.AttributeLink;
import life.genny.qwanda.attribute.AttributeText;
import life.genny.qwanda.attribute.EntityAttribute;
import life.genny.qwanda.datatype.DataType;
import life.genny.qwanda.entity.BaseEntity;
import life.genny.qwanda.entity.Company;
import life.genny.qwanda.entity.Group;
import life.genny.qwanda.entity.Person;
import life.genny.qwanda.entity.Product;
import life.genny.qwanda.exception.BadDataException;
import life.genny.qwanda.rule.Rule;


/**
 * This Service bean demonstrate various JPA manipulations of {@link BaseEntity}
 *
 * @author Adam Crow
 */
@ApplicationScoped
@Startup
public class BaseEntityService {

  @Inject
  private Event<BaseEntity> baseEntityEventSrc;

  @Inject
  private Event<Attribute> attributeEventSrc;

  @Inject
  private Event<DataType> dataTypeEventSrc;

  @Inject
  Event<BaseEntity> baseEntityRemoveEvent;

  @Inject
  Event<Attribute> attributeRemoveEvent;

  @Inject
  Event<DataType> dataTypeRemoveEvent;


  @PersistenceContext(unitName = "MyPU")
  private EntityManager em;

  public Long insert(final Question question) {
    // always check if question exists through check for unique code
    try {
      em.persist(question);
      // baseEntityEventSrc.fire(entity);

    } catch (final EntityExistsException e) {
      // so update otherwise // TODO merge?
      Question existing = findQuestionByCode(question.getCode());
      existing = em.merge(existing);
      return existing.getId();

    }
    return question.getId();
  }

  public Long insert(final Ask ask) {
    // always check if question exists through check for unique code
    try {
      em.persist(ask);
      // baseEntityEventSrc.fire(entity);

    } catch (final EntityExistsException e) {
      // so update otherwise // TODO merge?
      Ask existing = findAskById(ask.getId());
      existing = em.merge(existing);
      return existing.getId();

    }
    return ask.getId();
  }

  public Long insert(final Rule rule) {
    // always check if rule exists through check for unique code
    try {
      em.persist(rule);
      // baseEntityEventSrc.fire(entity);

    } catch (final EntityExistsException e) {
      // so update otherwise // TODO merge?
      Rule existing = findRuleById(rule.getId());
      existing = em.merge(existing);
      return existing.getId();

    }
    return rule.getId();
  }

  public Long insert(final Answer answer) {
    // always check if answer exists through check for unique code
    try {
      BaseEntity beSource = null;
      BaseEntity beTarget = null;
      Ask ask = null;

      if (answer.getAskId() != null) {
        ask = findAskById(answer.getAskId());
        beTarget = ask.getTarget();
        beSource = ask.getSource();
        answer.setSourceCode(beSource.getCode());
        answer.setTargetCode(beTarget.getCode());
      } else {
        // Need to find source and target by their codes
        beSource = findBaseEntityByCode(answer.getSourceCode());
        beTarget = findBaseEntityByCode(answer.getTargetCode());
      }

      // now look for existing answerlink
      answer.setAsk(ask);

      em.persist(answer);
      // update answerlink

      // check if answerlink already there
      AnswerLink answerLink =
          findAnswerLinkByCodes(beTarget.getCode(), beSource.getCode(), answer.getAttributeCode());

      if (answerLink == null) {
        answerLink = beSource.addAnswer(beTarget, answer, answer.getWeight());
        beSource = em.merge(beSource);
      } else {
        answerLink.setAnswer(answer);
        answerLink = em.merge(answerLink);
      }

      if (!ask.getAnswerList().getAnswerList().contains(answerLink)) {
        ask.getAnswerList().getAnswerList().add(answerLink);
        ask = em.merge(ask);
      }
      // baseEntityEventSrc.fire(entity);


    } catch (final BadDataException e) {

    } catch (final EntityExistsException e) {
      // so update otherwise // TODO merge?
      Answer existing = findAnswerById(answer.getId());
      existing = em.merge(existing);
      if (answer.getAskId() != null) {
        final Ask ask = findAskById(answer.getAsk().getId());
        BaseEntity be = ask.getTarget();
        final Set<AnswerLink> answerLinks = be.getAnswers();
        // dumbly check if existing answerLink there
        for (final AnswerLink al : answerLinks) { // watch for duplicates
          if (al.getAsk().getId().equals(ask.getId())) {
            if (al.getCreated().equals(answer.getCreated())) {
              // this is the same answer
              al.setExpired(answer.getExpired());
            }
          }
        }
        be = em.merge(be);
      }
      return existing.getId();

    }
    return answer.getId();
  }



  public Long insert(final BaseEntity entity) {
    // always check if baseentity exists through check for unique code
    try {
      em.persist(entity);
      baseEntityEventSrc.fire(entity);

    } catch (final EntityExistsException e) {
      // so update otherwise // TODO merge?
      BaseEntity existing = findBaseEntityByCode(entity.getCode());
      final List<EntityAttribute> changes = existing.merge(entity);
      System.out.println("Updated " + existing + ":" + changes);
      existing = em.merge(existing);
      return existing.getId();

    }
    return entity.getId();
  }

  // public Long insert(Ask ask)
  // {
  // // always check if ask exists through check for source, target, and question, and created
  // datetime
  // try {
  // em.persist(ask);
  // // baseEntityEventSrc.fire(entity);
  //
  // } catch (EntityExistsException e) {
  // // so update otherwise // TODO merge?
  // BaseEntity existing = findBaseEntityByCode(entity.getCode());
  // List<EntityAttribute> changes = existing.merge(entity);
  // System.out.println("Updated "+existing+ ":"+ changes);
  // existing = em.merge(existing);
  // return existing.getId();
  //
  // }
  // return entity.getId();
  // }

  public Long update(Ask ask) {
    // always check if ask exists through check for unique code
    try {
      ask = em.merge(ask);
    } catch (final IllegalArgumentException e) {
      // so persist otherwise
      em.persist(ask);
    }
    return ask.getId();
  }


  public Long update(BaseEntity entity) {
    // always check if baseentity exists through check for unique code
    try {
      entity = em.merge(entity);
      baseEntityEventSrc.fire(entity);
    } catch (final IllegalArgumentException e) {
      // so persist otherwise
      em.persist(entity);
    }
    return entity.getId();
  }

  public Long update(Attribute attribute) {
    // always check if attribute exists through check for unique code
    try {
      attribute = em.merge(attribute);
      attributeEventSrc.fire(attribute);
    } catch (final IllegalArgumentException e) {
      // so persist otherwise
      em.persist(attribute);
    }
    return attribute.getId();
  }

  public Ask findAskById(final Long id) {
    return em.find(Ask.class, id);
  }

  public GPS findGPSById(final Long id) {
    return em.find(GPS.class, id);
  }

  public Question findQuestionById(final Long id) {
    return em.find(Question.class, id);
  }

  public Answer findAnswerById(final Long id) {
    return em.find(Answer.class, id);
  }

  public Context findContextById(final Long id) {
    return em.find(Context.class, id);
  }

  public BaseEntity findBaseEntityById(final Long id) {
    return em.find(BaseEntity.class, id);
  }

  public Attribute findAttributeById(final Long id) {
    return em.find(Attribute.class, id);
  }

  public Rule findRuleById(final Long id) {
    return em.find(Rule.class, id);
  }

  public DataType findDataTypeById(final Long id) {
    return em.find(DataType.class, id);
  }

  public BaseEntity findBaseEntityByCode(@NotNull final String baseEntityCode)
      throws NoResultException {

    final BaseEntity result =
        (BaseEntity) em.createQuery("SELECT a FROM BaseEntity a where a.code=:baseEntityCode")
            .setParameter("baseEntityCode", baseEntityCode.toUpperCase()).getSingleResult();

    // Ugly, add field filtering through header field list

    final List<EntityAttribute> attributes = em
        .createQuery(
            "SELECT ea FROM EntityAttribute ea where ea.pk.baseEntity.code=:baseEntityCode")
        .setParameter("baseEntityCode", baseEntityCode).getResultList();
    result.setBaseEntityAttributes(new HashSet<EntityAttribute>(attributes));
    return result;

  }

  public Rule findRuleByCode(@NotNull final String ruleCode) throws NoResultException {

    final Rule result = (Rule) em.createQuery("SELECT a FROM Rule a where a.code=:ruleCode")
        .setParameter("ruleCode", ruleCode.toUpperCase()).getSingleResult();

    return result;
  }

  public Question findQuestionByCode(@NotNull final String code) throws NoResultException {

    final Question result = (Question) em.createQuery("SELECT a FROM Question a where a.code=:code")
        .setParameter("code", code.toUpperCase()).getSingleResult();

    return result;
  }

  public DataType findDataTypeByCode(@NotNull final String code) throws NoResultException {

    final DataType result = (DataType) em.createQuery("SELECT a FROM DataType a where a.code=:code")
        .setParameter("code", code.toUpperCase()).getSingleResult();

    return result;
  }

  public Attribute findAttributeByCode(@NotNull final String code) throws NoResultException {

    final Attribute result =
        (Attribute) em.createQuery("SELECT a FROM Attribute a where a.code=:code")
            .setParameter("code", code.toUpperCase()).getSingleResult();

    return result;
  }

  public AnswerLink findAnswerLinkByCodes(@NotNull final String targetCode,
      @NotNull final String sourceCode, @NotNull final String attributeCode) {

    final AnswerLink result = (AnswerLink) em.createQuery(
        "SELECT a FROM AnswerLink a where a.targetCode=:targetCode and a.sourceCode=:sourceCode and  attributeCode=:attributeCode")
        .setParameter("targetCode", targetCode).setParameter("sourceCode", sourceCode)
        .setParameter("attributeCode", attributeCode).getSingleResult();

    return result;

  }



  public BaseEntity findUserByAttributeValue(@NotNull final String attributeCode,
      final Integer value) {

    final List<EntityAttribute> results = em.createQuery(
        "SELECT ea FROM EntityAttribute ea where ea.pk.attribute.code=:attributeCode and ea.valueInteger=:valueInteger")
        .setParameter("attributeCode", attributeCode).setParameter("valueInteger", value)
        .setMaxResults(1).getResultList();
    if ((results == null) || (results.size() == 0))
      return null;

    final BaseEntity ret = results.get(0).getBaseEntity();

    return ret;
  }

  public BaseEntity findUserByAttributeValue(@NotNull final String attributeCode,
      final String value) {

    final List<EntityAttribute> results = em.createQuery(
        "SELECT ea FROM EntityAttribute ea where ea.pk.attribute.code=:attributeCode and ea.valueString=:value")
        .setParameter("attributeCode", attributeCode).setParameter("value", value).setMaxResults(1)
        .getResultList();
    if ((results == null) || (results.size() == 0))
      return null;

    final BaseEntity ret = results.get(0).getBaseEntity();
    return ret;
  }

  public List<BaseEntity> findChildrenByAttributeLink(@NotNull final String sourceCode,
      final String linkCode) {

    final List<BaseEntity> eeResults = em.createQuery(
        "SELECT be FROM BaseEntity be,EntityEntity ee where ee.pk.target.code=be.code and ee.pk.linkAttribute.code=:linkAttributeCode and ee.pk.source.code=:sourceCode")
        .setParameter("sourceCode", sourceCode).setParameter("linkAttributeCode", linkCode)
        .getResultList();


    // TODO: improve
    // final List<BaseEntity> resultList = eeResults.stream()
    // .map(x -> x.getTarget())
    // .collect(Collectors.toList());
    return eeResults;
  }

  public Setup setup(final KeycloakSecurityContext kContext) {
    final Setup setup = new Setup();
    String bearerToken = null;
    String decodedJson = null;
    JSONObject jsonObj;
    try {
      bearerToken = kContext.getTokenString();
      System.out.println("bearerToken:" + bearerToken);
      final String[] jwtToken = bearerToken.split("\\.");
      System.out.println("jwtToken:" + jwtToken);
      final Decoder decoder = Base64.getDecoder();
      final byte[] decodedClaims = decoder.decode(jwtToken[1]);
      decodedJson = new String(decodedClaims);
      System.out.println("decodedJson:" + decodedJson);
      jsonObj = new JSONObject(decodedJson);
      final String userUUID = jsonObj.getString("sub");
      System.out.println("UserId=" + userUUID);
      final JSONObject realm_access = (JSONObject) jsonObj.get("realm_access");
      final JSONArray realm_roles = (JSONArray) realm_access.get("roles");
      final JSONObject resource_access = (JSONObject) jsonObj.get("resource_access");
      final JSONObject qwandaService = (JSONObject) resource_access.get("qwanda-service");
      final JSONArray resource_roles = (JSONArray) qwandaService.get("roles");

      System.out.println("Roles:" + resource_roles + "," + realm_roles + "!");

      final BaseEntity be =
          findUserByAttributeValue(AttributeText.getDefaultCodePrefix() + "UUID", userUUID);
      be.getBaseEntityAttributes();
      setup.setUser(be);



      // {"jti":"1ae163f0-5495-4466-b224-de35a1f5794b","exp":1494376724,"nbf":0,"iat":1494376424,"iss":"http://bouncer.outcome-hub.com/auth/realms/genny","aud":"qwanda-service","sub":"81ef02bd-9976-4ce4-9fb4-17f30b416e06","typ":"Bearer","azp":"qwanda-service","auth_time":1494376102,"session_state":"4153d350-e9e9-4a00-85de-2def89427f4e","acr":"0","client_session":"307f1c32-c7ea-4408-816c-12a189c09081","allowed-origins":["*"],"realm_access":{"roles":["uma_authorization","user"]},"resource_access":{"qwanda-service":{"roles":["admin"]},"account":{"roles":["manage-account","manage-account-links","view-profile"]}},"name":"Bob
      // Console","preferred_username":"adamcrow63+bobconsole@gmail.com","given_name":"Bob","family_name":"Console","email":"adamcrow63+bobconsole@gmail.com"}
    } catch (final JSONException e1) {
      // log.error("bearerToken=" + bearerToken + " decodedJson=" + decodedJson + ":" +
      // e1.getMessage());
    }

    setup.setLayout("layout1");

    return setup;
  }


  public void importKeycloakUsers(final List<Group> parentGroupList,
      final AttributeLink linkAttribute) throws IOException, BadDataException {


    final Map<String, String> envParams = System.getenv();
    String keycloakUrl = envParams.get("KEYCLOAKURL");
    System.out.println("Keycloak URL=[" + keycloakUrl + "]");
    keycloakUrl = keycloakUrl.replaceAll("'", "");
    final String realm = envParams.get("KEYCLOAK_REALM");
    final String username = envParams.get("KEYCLOAK_USERNAME");
    final String password = envParams.get("KEYCLOAK_PASSWORD");
    final String clientid = envParams.get("KEYCLOAK_CLIENTID");
    final String secret = envParams.get("KEYCLOAK_SECRET");

    System.out.println("Realm is :[" + realm + "]");

    final KeycloakService kcs =
        new KeycloakService(keycloakUrl, realm, username, password, clientid, secret);
    final List<LinkedHashMap> users = kcs.fetchKeycloakUsers();
    for (final LinkedHashMap user : users) {
      final String name = user.get("firstName") + " " + user.get("lastName");
      final Person newUser = new Person(name);
      final String keycloakUUID = (String) user.get("id");
      newUser.setCode(Person.getDefaultCodePrefix() + keycloakUUID.toUpperCase());
      newUser.setName(name);
      newUser.addAttribute(createAttributeText("NAME"), 1.0, name);
      newUser.addAttribute(createAttributeText("FIRSTNAME"), 1.0, user.get("firstName"));
      newUser.addAttribute(createAttributeText("LASTNAME"), 1.0, user.get("lastName"));
      newUser.addAttribute(createAttributeText("UUID"), 1.0, user.get("id"));
      newUser.addAttribute(createAttributeText("EMAIL"), 1.0, user.get("email"));
      newUser.addAttribute(createAttributeText("USERNAME"), 1.0, user.get("username"));
      System.out.println("Code=" + newUser.getCode());;
      insert(newUser);
      // Now link to groups
      for (final Group parent : parentGroupList) {
        if (!parent.containsTarget(newUser.getCode(), linkAttribute.getCode())) {
          parent.addTarget(newUser, linkAttribute, 1.0);


        }
      }
    }
    // now save the parents
    for (Group parent : parentGroupList) {
      parent = em.merge(parent);
    }
    System.out.println(users);
  }

  private Attribute createAttributeText(final String attributeName) {
    Attribute attribute = null;
    try {
      attribute = findAttributeByCode(AttributeText.getDefaultCodePrefix() + attributeName);
    } catch (final NoResultException e) {

      attribute = new AttributeText(AttributeText.getDefaultCodePrefix() + attributeName,
          StringUtils.capitalize(attributeName));

      em.persist(attribute);
    }
    return attribute;
  }


  /**
   * init
   */
  public void init(final KeycloakSecurityContext kContext) {
    // Entities
    if (kContext == null) {
      System.out.println("Null Keycloak Context");
      return;
    }

    final BaseEntity be = new BaseEntity("Test BaseEntity");
    be.setCode(BaseEntity.getDefaultCodePrefix() + "TEST");
    em.persist(be);

    Person edison = new Person("Thomas Edison");
    edison.setCode(Person.getDefaultCodePrefix() + "EDISON");
    em.persist(edison);

    final Person tesla = new Person("Nikola Tesla");
    tesla.setCode(Person.getDefaultCodePrefix() + "TESLA");
    em.persist(tesla);

    final Company crowtech = new Company("crowtech", "Crowtech Pty Ltd");
    crowtech.setCode(Company.getDefaultCodePrefix() + "CROWTECH");
    em.persist(crowtech);

    final Company spacex = new Company("spacex", "SpaceX");
    spacex.setCode(Company.getDefaultCodePrefix() + "SPACEX");
    em.persist(spacex);


    final Product bmw316i = new Product("bmw316i", "BMW 316i");
    bmw316i.setCode(Product.getDefaultCodePrefix() + "BMW316I");
    em.persist(bmw316i);

    final Product mazdaCX5 = new Product("maxdacx5", "Mazda CX-5");
    mazdaCX5.setCode(Product.getDefaultCodePrefix() + "MAXDACX5");
    em.persist(mazdaCX5);

    final AttributeText attributeText1 =
        new AttributeText(AttributeText.getDefaultCodePrefix() + "TEST1", "Test 1");
    em.persist(attributeText1);
    final AttributeText attributeText2 =
        new AttributeText(AttributeText.getDefaultCodePrefix() + "TEST2", "Test 2");
    em.persist(attributeText2);
    final AttributeText attributeText3 =
        new AttributeText(AttributeText.getDefaultCodePrefix() + "TEST3", "Test 3");
    em.persist(attributeText3);

    Person person = new Person("Barry Allen");
    person.setCode(Person.getDefaultCodePrefix() + "FLASH");
    em.persist(person);

    try {
      person.addAttribute(attributeText1, 1.0);
      person.addAttribute(attributeText2, 0.8);
      person.addAttribute(attributeText3, 0.6, 3147);

      // Link some BaseEntities
      final AttributeText link1 =
          new AttributeText(AttributeText.getDefaultCodePrefix() + "LINK1", "Link1");
      em.persist(link1);
      person.addTarget(bmw316i, link1, 1.0);
      person.addTarget(mazdaCX5, link1, 0.9);
      person.addTarget(edison, link1, 0.8);
      person.addTarget(tesla, link1, 0.7);
      edison.addTarget(spacex, link1, 0.5);
      edison.addTarget(crowtech, link1, 0.4);

      person = em.merge(person);
      edison = em.merge(edison);



    } catch (final BadDataException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * @return all the {@link BaseEntity} in the db
   */
  public List<BaseEntity> getAll() {
    final Query query = em.createQuery("SELECT e FROM BaseEntity e");
    return query.getResultList();

  }

  /**
   * Remove {@link BaseEntity} one by one and throws an exception at a given point to simulate a
   * real error and test Transaction bahaviour
   *
   * @throws IllegalStateException when removing {@link BaseEntity} at given index
   */
  public void remove(final BaseEntity entity) {
    resetMsgLists();
    final BaseEntity baseEntity = findBaseEntityById(entity.getId());
    baseEntityRemoveEvent.fire(baseEntity);
    em.remove(baseEntity);
  }

  /**
   * Remove {@link BaseEntity} one by one and throws an exception at a given point to simulate a
   * real error and test Transaction bahaviour
   *
   * @throws IllegalStateException when removing {@link BaseEntity} at given index
   */
  public void removeBaseEntity(final String code) {
    final BaseEntity baseEntity = findBaseEntityByCode(code);
    baseEntityRemoveEvent.fire(baseEntity);
    em.remove(baseEntity);
  }

  /**
   * Remove {@link Attribute} one by one and throws an exception at a given point to simulate a real
   * error and test Transaction bahaviour
   *
   * @throws IllegalStateException when removing {@link Attribute} at given index
   */
  public void removeAttribute(final String code) {
    final Attribute attribute = findAttributeByCode(code);
    attributeRemoveEvent.fire(attribute);
    em.remove(attribute);
  }


  public void resetMsgLists() {
    commitMsg.clear();
    rollbackMsg.clear();
  }

  /**
   * Add a message to the commit messages list
   *
   * @param msg to add
   */
  public void addCommitMsg(final String msg) {
    commitMsg.add(msg);
  }

  /**
   * Add a message to the roll back messages list
   *
   * @param msg to add
   */
  public void addRollbackMsg(final String msg) {
    rollbackMsg.add(msg);
  }

  /**
   * @return commit messages
   */
  public List<String> getCommitMsg() {
    return commitMsg;
  }

  /**
   * @return rollback messages
   */
  public List<String> getRollbackMsg() {
    return rollbackMsg;
  }


  private List<String> commitMsg = new ArrayList<>();

  private List<String> rollbackMsg = new ArrayList<>();

  public static String set(final Object item) {

    final ObjectMapper mapper = new ObjectMapper();
    // mapper.registerModule(new JavaTimeModule());

    String json = null;

    try {
      json = mapper.writeValueAsString(item);
    } catch (final JsonProcessingException e) {

    }
    return json;
  }


  public List<EntityAttribute> findAttributesByBaseEntityId(final Long id) {
    final List<EntityAttribute> results =
        em.createQuery("SELECT ea FROM EntityAttribute ea where ea.pk.baseEntity.id=:baseEntityId")
            .setParameter("baseEntityId", id).getResultList();

    return results;
  }


  public void importBaseEntitys(final InputStream in, final String filename) {
    // import csv
    String line = "";
    final String cvsSplitBy = ",";
    boolean headerLine = true;
    final Map<Integer, Attribute> attributes = new HashMap<Integer, Attribute>();


    try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
      int rowNumber = 0;
      while ((line = br.readLine()) != null) {

        // use comma as separator
        final String[] columns = line.split(cvsSplitBy);
        if (headerLine) {
          headerLine = false;
          for (int i = 0; i < columns.length; i++) {
            final String[] code_name = columns[i].split(":");

            Attribute attribute = findAttributeByCode(code_name[0]);
            if (attribute == null) {
              attribute = new AttributeText(code_name[0], code_name[1]);
              em.persist(attribute);
            }

            attributes.put(i, attribute);
          }
        } else {
          BaseEntity entity = null;
          final String code = filename + "-" + rowNumber;
          if (filename.toUpperCase().contains("PERSON")) {
            entity = new Person(code);
          } else if (filename.toUpperCase().contains("COMPANY")) {
            entity = new Company(code, "Import");
          } else if (filename.toUpperCase().contains("PRODUCT")) {
            entity = new Product(code, "Import");
          } else {
            entity = new BaseEntity(code);
          }

          for (int i = 0; i < columns.length; i++) {
            // determine if it is a person, company or product else baseentity

            final Attribute attribute = attributes.get(i);
            if (attribute.getCode().equalsIgnoreCase("NAME")) {
              entity.setName(columns[i]);
            }
            if (attribute.getCode().equalsIgnoreCase("CODE")) {
              entity.setCode(columns[i]);
            }
            try {
              entity.addAttribute(attribute, 1.0, columns[i]);
            } catch (final BadDataException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }


          }
          if (entity instanceof Person) {
            if (!entity.containsEntityAttribute("NAME")) {
              // get first
              final Optional<EntityAttribute> firstname =
                  entity.findEntityAttribute("PRI_FIRSTNAME");
              final Optional<EntityAttribute> lastname = entity.findEntityAttribute("PRI_LASTNAME");

              String name = "";
              if (firstname.isPresent()) {
                name += firstname.get().getValueString() + " ";
              }
              if (lastname.isPresent()) {
                name += lastname.get().getValueString() + " ";
              }
              Attribute nameAttribute = findAttributeByCode("PRI_NAME");
              if (nameAttribute == null) {
                nameAttribute = new AttributeText("PRI_NAME", "Name");
                em.persist(nameAttribute);

              }
              try {
                entity.addAttribute(nameAttribute, 1.0, name);

              } catch (final BadDataException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }

            }
          }
          // Now check if not already there by comparing specific fields
          em.persist(entity);
        }
        rowNumber++;
      }

    } catch (final IOException e) {
      e.printStackTrace();
    }

  }

  public Long insert(final Attribute attribute) {
    // always check if baseentity exists through check for unique code
    try {
      em.persist(attribute);
      attributeEventSrc.fire(attribute);

    } catch (final EntityExistsException e) {
      // so update otherwise // TODO merge?
      Attribute existing = findAttributeByCode(attribute.getCode());
      existing = em.merge(existing);
      return existing.getId();

    }
    return attribute.getId();
  }



  public List<Ask> findAsksBySourceBaseEntityId(final Long id) {
    final List<Ask> results =
        em.createQuery("SELECT ea FROM Ask ea where ea.source.id=:baseEntityId")
            .setParameter("baseEntityId", id).getResultList();

    return results;

  }

  public List<Ask> findAsksByTargetBaseEntityId(final Long id) {
    final List<Ask> results =
        em.createQuery("SELECT ea FROM Ask ea where ea.target.id=:baseEntityId")
            .setParameter("baseEntityId", id).getResultList();

    return results;

  }
 
  public List<GPS> findGPSByTargetBaseEntityId(final Long id) {
    final List<GPS> results =
        em.createQuery("SELECT ea FROM GPS ea where ea.targetId=:baseEntityId")
            .setParameter("baseEntityId", id).getResultList();

    return results;

  }
  
  public List<GPS> findGPSByTargetBaseEntityCode(final String targetCode) {
    final List<GPS> results =
        em.createQuery("SELECT ea FROM GPS ea where ea.targetCode=:baseEntityCode")
            .setParameter("baseEntityCode", targetCode).getResultList();

    return results;

  }

  public List<AnswerLink> findAnswersByTargetBaseEntityId(final Long id) {
    final List<AnswerLink> results =
        em.createQuery("SELECT ea FROM AnswerLink ea where ea.pk.target.id=:baseEntityId")
            .setParameter("baseEntityId", id).getResultList();

    return results;

  }



  public List<Question> findQuestions() throws NoResultException {

    final List<Question> results = em.createQuery("SELECT a FROM Question a").getResultList();

    return results;
  }

  public List<Ask> findAsks() throws NoResultException {

    final List<Ask> results = em.createQuery("SELECT a FROM Ask a").getResultList();

    return results;
  }

  public List<Rule> findRules() throws NoResultException {

    final List<Rule> results = em.createQuery("SELECT a FROM Rule a").getResultList();

    return results;
  }

  public List<AnswerLink> findAnswerLinks() throws NoResultException {

    final List<AnswerLink> results = em.createQuery("SELECT a FROM AnswerLink a").getResultList();

    return results;
  }

  public List<EntityAttribute> findAttributesByBaseEntityCode(final String code)
      throws NoResultException {

    final List<EntityAttribute> results = em
        .createQuery(
            "SELECT ea FROM EntityAttribute ea where ea.pk.baseEntity.code=:baseEntityCode")
        .setParameter("baseEntityCode", code).getResultList();

    return results;
  }

  public Long insert(final GPS entity) {
    try {
      em.persist(entity);

    } catch (final EntityExistsException e) {
      // so update otherwise // TODO merge?
      GPS existing = findGPSById(entity.getId());
      existing = em.merge(existing);
      return existing.getId();

    }
    return entity.getId();

  }
}
