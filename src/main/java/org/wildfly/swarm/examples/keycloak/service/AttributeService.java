package org.wildfly.swarm.examples.keycloak.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import life.genny.qwanda.attribute.Attribute;
import life.genny.qwanda.attribute.AttributeText;
import life.genny.qwanda.attribute.EntityAttribute;
import life.genny.qwanda.entity.BaseEntity;
import life.genny.qwanda.entity.Company;
import life.genny.qwanda.entity.Person;
import life.genny.qwanda.entity.Product;
import life.genny.qwanda.exception.BadDataException;

/**
 * This Service bean demonstrate various JPA manipulations of {@link Attribute}
 *
 * @author Adam Crow
 */
@ApplicationScoped
@Startup
public class AttributeService {

	@Inject
	private Event<Attribute> attributeEventSrc;
	
	@Inject
	Event<BaseEntity> empvt;

	@PersistenceContext(unitName = "MyPU")
	private EntityManager em;

	private List<String> commitMsg = new ArrayList<>();

	private List<String> rollbackMsg = new ArrayList<>();


	public Long insert(Attribute attribute) {
		em.persist(attribute);
		attributeEventSrc.fire(attribute);
		return attribute.getId();
	}

	public Attribute findAttributeById(final Long id) {
		return em.find(Attribute.class, id);
	}

	public Attribute findAttributeByCode(@NotNull final String attributeCode) {

		Attribute result = (Attribute)em.createQuery(
			    "SELECT a FROM Attribute a where a.code=:attributeCode")
			    .setParameter("attributeCode", attributeCode)
			    .getSingleResult();
		return result;
	}

	private Attribute createAttributeText(String code, String attributeName) {
		Attribute attribute = null;
		try {
			attribute = findAttributeByCode(AttributeText.getDefaultCodePrefix() + attributeName);
		} catch (NoResultException e) {

			attribute = new AttributeText(code, StringUtils.capitalize(attributeName));

			em.persist(attribute);
		}
		return attribute;
	}


	/**
	 * @return all the {@link Attribute} in the db
	 */
	public List<Attribute> getAll() {
		Query query = em.createQuery("SELECT e FROM Attribute e");
		return query.getResultList();

	}

	/**
	 * Remove {@link Attribute} one by one and throws an exception at a given
	 * point to simulate a real error and test Transaction bahaviour
	 *
	 * @param failOn
	 *            index where the method should fail
	 * @throws IllegalStateException
	 *             when removing {@link Attribute} at given index
	 */
	public void removeById(Long id) {
		resetMsgLists();
			Attribute attribute = em.find(Attribute.class, id);
			attributeEventSrc.fire(attribute);
			em.remove(attribute);
		
	}

	public void resetMsgLists() {
		commitMsg.clear();
		rollbackMsg.clear();
	}

	/**
	 * Add a message to the commit messages list
	 *
	 * @param msg
	 *            to add
	 */
	public void addCommitMsg(String msg) {
		commitMsg.add(msg);
	}

	/**
	 * Add a message to the roll back messages list
	 *
	 * @param msg
	 *            to add
	 */
	public void addRollbackMsg(String msg) {
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


	public static String set(final Object item) {

		ObjectMapper mapper = new ObjectMapper();
		// mapper.registerModule(new JavaTimeModule());

		String json = null;

		try {
			json = mapper.writeValueAsString(item);
		} catch (JsonProcessingException e) {

		}
		return json;
	}

// TODO
	public void importAttributes(InputStream in, String filename) {
		// import csv
		String line = "";
		String cvsSplitBy = ",";
		boolean headerLine = true;
		Map<Integer, Attribute> attributes = new HashMap<Integer, Attribute>();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			int rowNumber = 0;
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] columns = line.split(cvsSplitBy);
				if (headerLine) {
					headerLine = false;
					for (int i = 0; i < columns.length; i++) {
						String[] code_name = columns[i].split(":");

						Attribute attribute = findAttributeByCode(code_name[0]);
						if (attribute == null) {
							attribute = new AttributeText(code_name[0], code_name[1]);
							em.persist(attribute);
						}

						attributes.put(i, attribute);
					}
				} else {
					BaseEntity entity = null;
					String code = filename + "-" + rowNumber;
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
						// determine if it is a person, company or product else
						// baseentity

						Attribute attribute = attributes.get(i);
						if (attribute.getCode().equalsIgnoreCase("NAME")) {
							entity.setName(columns[i]);
						}
						if (attribute.getCode().equalsIgnoreCase("CODE")) {
							entity.setCode(columns[i]);
						}
						try {
							entity.addAttribute(attribute, 1.0, columns[i]);
						} catch (BadDataException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					if (entity instanceof Person) {
						if (!entity.containsEntityAttribute("NAME")) {
							// get first
							Optional<EntityAttribute> firstname = entity.findEntityAttribute("PRI_FIRSTNAME");
							Optional<EntityAttribute> lastname = entity.findEntityAttribute("PRI_LASTNAME");

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

							} catch (BadDataException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
					// Now check if not already there by comparing specific
					// fields
					em.persist(entity);
				}
				rowNumber++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}