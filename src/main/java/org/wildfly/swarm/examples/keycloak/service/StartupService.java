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
import life.genny.qwanda.exception.BadDataException;

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
		  
		  // create a group attribute (This is because of a json/hibernate lazy issue)
		  
            final Attribute attributeImageUrl = new AttributeText(AttributeText.getDefaultCodePrefix() + "IMAGE_URL",
              "Image Url");
            service.insert(attributeImageUrl);

		  
			final Group root = new Group("ROOT", "Root");
			root.addAttribute(attributeImageUrl, 1.0,"dir-ico");
			service.insert(root);

			final Group contacts = new Group("CONTACTS", "Contacts");
            contacts.addAttribute(attributeImageUrl, 1.0,"dir-ico");
			service.insert(contacts);

			final Group companys = new Group("COMPANYS", "Companies");
            companys.addAttribute(attributeImageUrl, 1.0,"dir-ico");
			service.insert(companys);

			final Group users = new Group("USERS", "Users");
            users.addAttribute(attributeImageUrl, 1.0,"dir-ico");
			service.insert(users);

			final AttributeLink linkAttribute = new AttributeLink("LNK_CORE", "Parent");
			service.insert(linkAttribute);

			root.addTarget(contacts, linkAttribute, 1.0);
			root.addTarget(companys, linkAttribute, 0.8);
			root.addTarget(users, linkAttribute, 0.2);
			service.update(root);

			// check if groups exist
			final List<Group> parentGroupList = new ArrayList<Group>();
			parentGroupList.add(contacts);
			parentGroupList.add(users);

			// try {
			// service.importKeycloakUsers(parentGroupList, linkAttribute);
			// } catch (Exception e) {
			// System.out.println("Unable to load in Keycloak Users");
			// }

			final Attribute attributeFirstname = new AttributeText(AttributeText.getDefaultCodePrefix() + "FIRSTNAME",
					"Firstname");
			final Attribute attributeLastname = new AttributeText(AttributeText.getDefaultCodePrefix() + "LASTNAME",
					"Lastname");

			// Attribute attributeFirstname = service.findAttributeByCode("PRI_FIRSTNAME");
			// Attribute attributeLastname = service.findAttributeByCode("PRI_LASTNAME");
			final Attribute attributeBirthdate = new AttributeDateTime(AttributeText.getDefaultCodePrefix() + "BIRTHDATE",
					"Date of Birth");
			final Attribute attributeKeycloakId = new AttributeText(AttributeText.getDefaultCodePrefix() + "KEYCLOAK_ID",
					"Keycloak ID");

			service.insert(attributeFirstname);
			service.insert(attributeLastname);
			service.insert(attributeBirthdate);
			service.insert(attributeKeycloakId);

			final Person person = new Person(Person.getDefaultCodePrefix()+"USER1", "James Bond");

			person.addAttribute(attributeFirstname, 1.0,"Sean");
			person.addAttribute(attributeLastname, 0.8,"Connery");
			person.addAttribute(attributeBirthdate, 0.6, LocalDateTime.of(1989, 1, 7, 16, 0));
			person.addAttribute(attributeKeycloakId, 0.0, "6ea705a3-f523-45a4-aca3-dc22e6c24f4f");

			service.insert(person);

			// create test questions
			final Question questionFirstname = new Question(Question.getDefaultCodePrefix() + "FIRSTNAME", "Firstname",
					attributeFirstname);
			final Question questionLastname = new Question(Question.getDefaultCodePrefix() + "LASTNAME", "Lastname",
					attributeLastname);
			final Question questionBirthdate = new Question(Question.getDefaultCodePrefix() + "BIRTHDATE", "Birthdate",
					attributeBirthdate);

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

			final Question question = service.findQuestionByCode(Question.getDefaultCodePrefix() + "FIRSTNAME");
			System.out.println(question);

			final List<Ask> asks = service.findAsksByTargetBaseEntityId(person.getId());
			System.out.println(asks);

		} catch (final BadDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}