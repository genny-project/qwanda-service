package org.wildfly.swarm.examples.keycloak.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

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
			Group root = new Group("ROOT", "Root");
			service.insert(root);

			Group contacts = new Group("CONTACTS", "Contacts");
			service.insert(contacts);

			Group companys = new Group("COMPANYS", "Companies");
			service.insert(companys);

			Group users = new Group("USERS", "Users");
			service.insert(users);

			AttributeLink linkAttribute = new AttributeLink("LNK_CORE", "Parent");
			service.insert(linkAttribute);

			root.addTarget(contacts, linkAttribute, 1.0);
			root.addTarget(companys, linkAttribute, 0.8);
			root.addTarget(users, linkAttribute, 0.2);
			service.update(root);

			// check if groups exist
			List<Group> parentGroupList = new ArrayList<Group>();
			parentGroupList.add(contacts);
			parentGroupList.add(users);

			// try {
			// service.importKeycloakUsers(parentGroupList, linkAttribute);
			// } catch (Exception e) {
			// System.out.println("Unable to load in Keycloak Users");
			// }

			Attribute attributeFirstname = new AttributeText(AttributeText.getDefaultCodePrefix() + "FIRSTNAME",
					"Firstname");
			Attribute attributeLastname = new AttributeText(AttributeText.getDefaultCodePrefix() + "LASTNAME",
					"Lastname");

			// Attribute attributeFirstname = service.findAttributeByCode("PRI_FIRSTNAME");
			// Attribute attributeLastname = service.findAttributeByCode("PRI_LASTNAME");
			Attribute attributeBirthdate = new AttributeDateTime(AttributeText.getDefaultCodePrefix() + "BIRTHDATE",
					"Date of Birth");
			Attribute attributeKeycloakId = new AttributeText(AttributeText.getDefaultCodePrefix() + "KEYCLOAK_ID",
					"Keycloak ID");

			service.insert(attributeFirstname);
			service.insert(attributeLastname);
			service.insert(attributeBirthdate);
			service.insert(attributeKeycloakId);

			Person person = new Person(Person.getDefaultCodePrefix()+"USER1", "James Bond");

			person.addAttribute(attributeFirstname, 1.0,"Sean");
			person.addAttribute(attributeLastname, 0.8,"Connery");
			person.addAttribute(attributeBirthdate, 0.6, LocalDateTime.of(1989, 1, 7, 16, 0));
			person.addAttribute(attributeKeycloakId, 0.0, "6ea705a3-f523-45a4-aca3-dc22e6c24f4f");

			service.insert(person);

			// create test questions
			Question questionFirstname = new Question(Question.getDefaultCodePrefix() + "FIRSTNAME", "Firstname",
					attributeFirstname);
			Question questionLastname = new Question(Question.getDefaultCodePrefix() + "LASTNAME", "Lastname",
					attributeLastname);
			Question questionBirthdate = new Question(Question.getDefaultCodePrefix() + "BIRTHDATE", "Birthdate",
					attributeBirthdate);

			service.insert(questionFirstname);
			service.insert(questionLastname);
			service.insert(questionBirthdate);

			// Now ask the question!

			Ask askFirstname = new Ask(questionFirstname, person, person);
			Ask askLastname = new Ask(questionLastname, person, person);
			Ask askBirthdate = new Ask(questionBirthdate, person, person);

			service.insert(askFirstname);
			service.insert(askLastname);
			service.insert(askBirthdate);

			Answer answerFirstname = new Answer(askFirstname, "Bob");
			Answer answerLastname = new Answer(askLastname, "Console");
			Answer answerBirthdate = new Answer(askBirthdate, "1989-01-07T16:00:00");

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

			Question question = service.findQuestionByCode(Question.getDefaultCodePrefix() + "FIRSTNAME");
			System.out.println(question);

			List<Ask> asks = service.findAsksByTargetBaseEntityId(person.getId());
			System.out.println(asks);

		} catch (BadDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}