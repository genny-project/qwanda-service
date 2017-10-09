package org.wildfly.swarm.examples.keycloak.service;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import life.genny.qwanda.Answer;
import life.genny.qwanda.Ask;
import life.genny.qwanda.Question;
import life.genny.qwanda.attribute.Attribute;
import life.genny.qwanda.attribute.AttributeDateTime;
import life.genny.qwanda.attribute.AttributeLink;
import life.genny.qwanda.attribute.AttributeText;
import life.genny.qwanda.entity.BaseEntity;
import life.genny.qwanda.entity.Group;
import life.genny.qwanda.entity.Person;
import life.genny.qwanda.exception.BadDataException;;

/**
 * This Service bean demonstrate various JPA manipulations of {@link BaseEntity}
 *
 * @author Adam Crow
 */
@Singleton
@Startup
public class StartupService {

  @Inject
  private BaseEntityService service;


  @PostConstruct
  public void init() {
    try {
      // create a users directory and a contacts directory
      // and link these users to each


      final Attribute attributeImageUrl =
          new AttributeText(AttributeText.getDefaultCodePrefix() + "IMAGE_URL", "Image Url");
      service.insert(attributeImageUrl);

      final Group root = new Group("ROOT", "Root");
      root.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(root);

      final Group dashboard = new Group("DASHBOARD", "Dashboard");
      dashboard.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(dashboard);

      final Group driverView = new Group("DRIVER_VIEW", "Driver View");
      driverView.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(driverView);

      final Group ownerView = new Group("OWNER_VIEW", "Owner View");
      ownerView.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(ownerView);

      final Group loads = new Group("LOADS", "Loads");
      loads.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(loads);

      final Group contacts = new Group("CONTACTS", "Contacts");
      contacts.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(contacts);

      final Group people = new Group("PEOPLE", "People");
      people.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(people);

      final Group companys = new Group("COMPANYS", "Companies");
      companys.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(companys);

      final Group users = new Group("USERS", "Users");
      users.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(users);

      final Group settings = new Group("SETTINGS", "Settings");
      settings.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(settings);

      final AttributeLink linkAttribute = new AttributeLink("LNK_CORE", "Parent");
      service.insert(linkAttribute);

      root.addTarget(dashboard, linkAttribute, 1.0);
      dashboard.addTarget(driverView, linkAttribute, 1.0);
      dashboard.addTarget(ownerView, linkAttribute, 0.8);
      root.addTarget(loads, linkAttribute, 0.2);
      root.addTarget(contacts, linkAttribute, 1.0);
      contacts.addTarget(people, linkAttribute, 0.8);
      contacts.addTarget(companys, linkAttribute, 0.8);
      contacts.addTarget(users, linkAttribute, 0.2);
      root.addTarget(settings, linkAttribute, 0.2);
      service.update(dashboard);
      service.update(root);

      // Adding Live View Child items
      final Group available = new Group("AVAILABLE", "Available");
      available.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(available);

      // Adding Live View Child items
      final Group pending = new Group("PENDING", "Pending");
      pending.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(pending);

      final Group quote = new Group("QUOTE", "Quote");
      quote.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(quote);

      final Group accepted = new Group("ACCEPTED", "Accepted");
      accepted.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(accepted);

      final Group dispatched = new Group("DISPATCHED", "Dispatched");
      dispatched.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(dispatched);

      final Group intransit = new Group("IN-TRANSIT", "In-Transit");
      intransit.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(intransit);

      final Group atdestination = new Group("AT-DESTINATION", "At-Destination");
      atdestination.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(atdestination);

      final Group delivered = new Group("DELIVERED", "Delivered");
      delivered.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(delivered);

      driverView.addTarget(available, linkAttribute, 1.0);
      driverView.addTarget(quote, linkAttribute, 1.0);
      driverView.addTarget(accepted, linkAttribute, 1.0);
      driverView.addTarget(dispatched, linkAttribute, 1.0);
      driverView.addTarget(intransit, linkAttribute, 1.0);
      driverView.addTarget(atdestination, linkAttribute, 1.0);
      driverView.addTarget(delivered, linkAttribute, 1.0);
      service.update(driverView);

      ownerView.addTarget(pending, linkAttribute, 1.0);
      ownerView.addTarget(quote, linkAttribute, 1.0);
      ownerView.addTarget(accepted, linkAttribute, 1.0);
      ownerView.addTarget(dispatched, linkAttribute, 1.0);
      ownerView.addTarget(intransit, linkAttribute, 1.0);
      ownerView.addTarget(atdestination, linkAttribute, 1.0);
      ownerView.addTarget(delivered, linkAttribute, 1.0);
      service.update(ownerView);

      // contacts.addTarget(, linkAttribute, weight);

      // Adding Settings items
      final Group yourDetils = new Group("YOUR_DETAILS", "Your-Details");
      yourDetils.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(yourDetils);

      final Group loadTypes = new Group("LOAD_TYPES", "Load-Types");
      loadTypes.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(loadTypes);

      final Group truckSpec = new Group("TRUCK_SPECIFICATION", "Truck-Specification");
      truckSpec.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(truckSpec);

      final Group documents = new Group("DOCUMENTS", "Documents");
      documents.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(documents);

      final Group updatePwd = new Group("UPDATE_PASSWORD", "Update-Password");
      updatePwd.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(updatePwd);

      final Group paymentDetails = new Group("PAYMENT_DETAILS", "Payment-Details");
      paymentDetails.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(paymentDetails);

      settings.addTarget(yourDetils, linkAttribute, 1.0);
      settings.addTarget(loadTypes, linkAttribute, 1.0);
      settings.addTarget(truckSpec, linkAttribute, 1.0);
      settings.addTarget(documents, linkAttribute, 1.0);
      settings.addTarget(updatePwd, linkAttribute, 1.0);
      settings.addTarget(paymentDetails, linkAttribute, 1.0);
      service.update(settings);

      // Adding Loads child item
      final Group viewLoads = new Group("VIEW_LOADS", "View-Loads");
      viewLoads.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(viewLoads);

      final Group postLoads = new Group("POST_LOADS", "Post-Loads");
      postLoads.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(postLoads);

      loads.addTarget(viewLoads, linkAttribute, 1.0);
      loads.addTarget(postLoads, linkAttribute, 1.0);
      service.update(loads);

      // Adding Users child item
      final Group admin = new Group("ADMIN", "Admin");
      admin.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(admin);

      final Group driver = new Group("DRIVER", "Pacific-Driver");
      driver.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(driver);

      final Group loadOwner = new Group("LOAD_OWNER", "Load-Owner");
      loadOwner.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(loadOwner);

      users.addTarget(admin, linkAttribute, 1.0);
      users.addTarget(driver, linkAttribute, 1.0);
      users.addTarget(loadOwner, linkAttribute, 1.0);
      service.update(users);

      // Adding Transport Company child item
      final Group aurizon = new Group("AURIZON", "Aurizon");
      aurizon.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(aurizon);

      final Group pacificNational = new Group("PACIFIC_NATIONAL", "Pacific-National");
      pacificNational.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(pacificNational);

      final Group linFox = new Group("LINFOX", "linfox");
      linFox.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(linFox);

      final Group sctLogistics = new Group("SCT_LOGISTICS", "SCT-Logistics");
      sctLogistics.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(sctLogistics);

      final Group glenGroup = new Group("GLEN_CAMERON_GROUP", "Glen-Cameron-Group");
      glenGroup.addAttribute(attributeImageUrl, 1.0, "dir-ico");
      service.insert(glenGroup);

      companys.addTarget(aurizon, linkAttribute, 1.0);
      companys.addTarget(pacificNational, linkAttribute, 1.0);
      companys.addTarget(linFox, linkAttribute, 1.0);
      companys.addTarget(sctLogistics, linkAttribute, 1.0);
      companys.addTarget(glenGroup, linkAttribute, 1.0);
      service.update(companys);

      // check if groups exist
      final List<Group> parentGroupList = new ArrayList<Group>();
      parentGroupList.add(contacts);
      parentGroupList.add(users);

      // try {
      // service.importKeycloakUsers(parentGroupList, linkAttribute);
      // } catch (Exception e) {
      // System.out.println("Unable to load in Keycloak Users");
      // }

      final Attribute attributeFirstname =
          new AttributeText(AttributeText.getDefaultCodePrefix() + "FIRSTNAME", "Firstname");
      final Attribute attributeLastname =
          new AttributeText(AttributeText.getDefaultCodePrefix() + "LASTNAME", "Lastname");

      // Attribute attributeFirstname = service.findAttributeByCode("PRI_FIRSTNAME");
      // Attribute attributeLastname = service.findAttributeByCode("PRI_LASTNAME");
      final Attribute attributeBirthdate = new AttributeDateTime(
          AttributeText.getDefaultCodePrefix() + "BIRTHDATE", "Date of Birth");
      final Attribute attributeKeycloakId =
          new AttributeText(AttributeText.getDefaultCodePrefix() + "KEYCLOAK_ID", "Keycloak ID");
      
      final Attribute attributeEmail =
              new AttributeText(AttributeText.getDefaultCodePrefix() + "EMAIL", "Email");
      final Attribute attributeMobileNo =
              new AttributeText(AttributeText.getDefaultCodePrefix() + "MOBILE_NO", "Mobile-No");
      
      final Attribute attributeRole =  new AttributeText(AttributeText.getDefaultCodePrefix() + "ROLE", "Role");

      service.insert(attributeFirstname);
      service.insert(attributeLastname);
      service.insert(attributeBirthdate);
      service.insert(attributeKeycloakId);
      service.insert(attributeEmail);
      service.insert(attributeMobileNo);
      service.insert(attributeRole);

      final Person person = new Person(Person.getDefaultCodePrefix() + "USER1", "James Bond");

      person.addAttribute(attributeFirstname, 1.0, "Sean");
      person.addAttribute(attributeLastname, 0.8, "Connery");
      person.addAttribute(attributeBirthdate, 0.6, LocalDateTime.of(1989, 1, 7, 16, 0));
      person.addAttribute(attributeEmail, 0.5, "sean@gmail.com");
      person.addAttribute(attributeMobileNo, 0.8, "0412345678");
      person.addAttribute(attributeRole, 0.8, "Driver");
      person.addAttribute(attributeKeycloakId, 0.0, "6ea705a3-f523-45a4-aca3-dc22e6c24f4f");
      service.insert(person);
      
      final Person person2 = new Person(Person.getDefaultCodePrefix() + "USER2", "Connor");

      person2.addAttribute(attributeFirstname, 1.0, "Connor");
      person2.addAttribute(attributeLastname, 0.8, "Pirie");
      person2.addAttribute(attributeBirthdate, 0.6, LocalDateTime.of(1989, 1, 7, 16, 0));
      person2.addAttribute(attributeEmail, 0.5, "conor@gmail.com");
      person2.addAttribute(attributeMobileNo, 0.8, "0412345678");
      person2.addAttribute(attributeRole, 0.8, "Load-Owner");
      person2.addAttribute(attributeKeycloakId, 0.0, "6ea705a3-f523-45a4-aca3-dc22e6c24f4f");

      service.insert(person2);
      
      final Person person3 = new Person(Person.getDefaultCodePrefix() + "USER3", "Adam");

      person3.addAttribute(attributeFirstname, 1.0, "Adam");
      person3.addAttribute(attributeLastname, 0.8, "Crow");
      person3.addAttribute(attributeBirthdate, 0.6, LocalDateTime.of(1989, 1, 7, 16, 0));
      person3.addAttribute(attributeEmail, 0.5, "adam.crow@gmail.com");
      person3.addAttribute(attributeMobileNo, 0.8, "0412345678");
      person3.addAttribute(attributeRole, 0.8, "Driver-Manager");
      person3.addAttribute(attributeKeycloakId, 0.0, "6ea705a3-f523-45a4-aca3-dc22e6c24f4f");

      service.insert(person3);
      
      

      // create test questions
      final Question questionFirstname = new Question(Question.getDefaultCodePrefix() + "FIRSTNAME",
          "Firstname", attributeFirstname);
      final Question questionLastname =
          new Question(Question.getDefaultCodePrefix() + "LASTNAME", "Lastname", attributeLastname);
      final Question questionBirthdate = new Question(Question.getDefaultCodePrefix() + "BIRTHDATE",
          "Birthdate", attributeBirthdate);

      service.insert(questionFirstname);
      service.insert(questionLastname);
      service.insert(questionBirthdate);

      // Now ask the question!

      final Ask askFirstname = new Ask(questionFirstname, person, person);
      final Ask askLastname = new Ask(questionLastname, person, person);
      final Ask askBirthdate = new Ask(questionBirthdate, person, person);

      service.insert(askFirstname);
      service.insert(askLastname);
      service.insert(askBirthdate);

      final Answer answerFirstname = new Answer(askFirstname, "Bob");
      final Answer answerLastname = new Answer(askLastname, "Console");
      final Answer answerBirthdate = new Answer(askBirthdate, "1989-01-07T16:00:00");

      person.addAnswer(answerFirstname, 1.0);
      person.addAnswer(answerLastname, 1.0);
      person.addAnswer(answerBirthdate, 1.0);

      askFirstname.add(answerFirstname);
      askLastname.add(answerLastname);
      askBirthdate.add(answerBirthdate);

      service.update(askFirstname);
      service.update(askLastname);
      service.update(askBirthdate);

      service.update(person);

      final Question question =
          service.findQuestionByCode(Question.getDefaultCodePrefix() + "FIRSTNAME");
      System.out.println(question);

      final List<Ask> asks = service.findAsksByTargetBaseEntityId(person.getId());
      System.out.println(asks);

    } catch (final BadDataException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
