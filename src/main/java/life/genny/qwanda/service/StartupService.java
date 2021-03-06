package life.genny.qwanda.service;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;

import org.apache.logging.log4j.Logger;

import life.genny.daoservices.BatchLoading;
import life.genny.qwanda.Ask;
import life.genny.qwanda.Question;
import life.genny.qwanda.attribute.Attribute;
import life.genny.qwanda.attribute.AttributeLink;
import life.genny.qwanda.attribute.EntityAttribute;
import life.genny.qwanda.datatype.DataType;
import life.genny.qwanda.entity.BaseEntity;
import life.genny.qwanda.entity.EntityEntity;
import life.genny.qwanda.exception.BadDataException;
import life.genny.qwanda.util.PersistenceHelper;
import life.genny.qwanda.validation.Validation;
import life.genny.qwanda.validation.ValidationList;
import life.genny.qwandautils.GennySheets2;

/**
 * This Service bean demonstrate various JPA manipulations of {@link BaseEntity}
 *
 * @author Adam Crow
 */
@Singleton
@Startup
public class StartupService {

	/**
	 * Stores logger object.
	 */
	protected static final Logger log = org.apache.logging.log4j.LogManager
			.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());

	private final static String secret = System.getenv("GOOGLE_CLIENT_SECRET");
	private final static String genny = System.getenv("GOOGLE_SHEETID");
	private final static String channel40 = "1jNe-MOXx8DFxA2kDeCHjdZ7U-4Fqk1cqyhTgsZvUZ4o";
	static File gennyPath = new File(System.getProperty("user.home"), ".credentials/genny");
	static File channelPath = new File(System.getProperty("user.home"), ".credentials/channel");

	private final String g = System.getenv("GOOGLE_CLIENT_SECRET");
	private final String go = System.getenv("GOOGLE_SHEETID");
	File credentialPath = new File(System.getProperty("user.home"),
			".credentials/sheets.googleapis.com-java-quickstart");

	@Inject
	private BaseEntityService service;

	@Inject
	private SecurityService securityService;

	@Inject
	private PersistenceHelper helper;

	// @PostConstruct
	public void init() {
		securityService.setImportMode(true); // ugly way of getting past security

		BatchLoading bl = new BatchLoading(helper.getEntityManager());
		bl.persistProject();

		securityService.setImportMode(false);
	}

	@PostConstruct
	public void init2() {
		securityService.setImportMode(true); // ugly way of getting past security

		System.out.println("**************************** IMPORTING  *************************************");

		final GennySheets2 gennySheets = new GennySheets2(g, go, credentialPath);

		System.out.println("**************************** IMPORTING VALIDATIONS *************************************");

		// Validations
		final Map<String, Validation> validationMap = new HashMap<String, Validation>();

		List<Validation> validations = null;
		try {
			validations = gennySheets.getBeans(Validation.class);
		} catch (final IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		validations.stream().forEach(object -> {
			service.insert(object);
			validationMap.put(object.getCode(), object);
		});

		System.out.println("**************************** IMPORTING DATATYPES *************************************");

		// DataTypes
		final Map<String, DataType> dataTypeMap = new HashMap<String, DataType>();
		List<Map> obj = null;
		try {
			obj = gennySheets.row2DoubleTuples(DataType.class.getSimpleName());
		} catch (final IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		obj.stream().forEach(object -> {
			final String code = (String) object.get("code");
			final String name = (String) object.get("name");
			final String validationss = (String) object.get("validations");
			final ValidationList validationList = new ValidationList();
			validationList.setValidationList(new ArrayList<Validation>());
			if (validationss != null) {
				final String[] validationListStr = validationss.split(",");
				for (final String validationCode : validationListStr) {
					validationList.getValidationList().add(validationMap.get(validationCode));
				}
			}
			if (!dataTypeMap.containsKey(code)) {
				final DataType dataType = new DataType(name, validationList);
				dataTypeMap.put(code, dataType);
			}
		});

		System.out.println("**************************** IMPORTING ATTRIBUTES *************************************");

		List<Map> attrs = null;
		try {
			attrs = gennySheets.row2DoubleTuples(Attribute.class.getSimpleName());
		} catch (final IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		attrs.stream().forEach(data -> {
			Attribute attribute = null;
			final String datatype = (String) data.get("datatype");
			try {
				System.out.println("ATTRIBUTE:::Code:" + data.get("code") + ":name" + data.get("name") + ":datatype:"
						+ data.get("datatype"));
				attribute = service.findAttributeByCode((String) data.get("code"));

			} catch (final NoResultException e) {
				attribute = new Attribute((String) data.get("code"), (String) data.get("name"),
						dataTypeMap.get(datatype));
				// System.out.println("ATTRIBUTE:::Code:" + data.get("code") + ":name" +
				// data.get("name")
				// + ":datatype:" + data.get("datatype") + "##################" + attribute);
				service.insert(attribute);
			} catch (final OptimisticLockException ee) {
				attribute = new Attribute((String) data.get("code"), (String) data.get("name"),
						dataTypeMap.get(datatype));
				// System.out.println("ATTRIBUTE:::Code:" + data.get("code") + ":name" +
				// data.get("name")
				// + ":datatype:" + data.get("datatype") + "##################" + attribute);
				// service.insert(attribute);
			}
		});
		System.out.println("**************************** IMPORTING BASEENTITYS *************************************");

		List<BaseEntity> bes = null;
		try {
			bes = gennySheets.getBeans(BaseEntity.class);
			bes.stream().forEach(object -> {
				service.insert(object);
			});
		} catch (final IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		if (true) {
			System.out.println(
					"**************************** IMPORTING BASEENTITY - ATTRIBUTE LINKS *************************************");

			// Now link Attributes to Baseentitys
			List<Map> obj2 = null;
			try {
				obj2 = gennySheets.row2DoubleTuples(EntityAttribute.class.getSimpleName());
			} catch (final IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Number of Links = " + obj2.size());

			obj2.stream().forEach(object -> {
				final String beCode = (String) object.get("baseEntityCode");
				final String attributeCode = (String) object.get("attributeCode");
				final String weightStr = (String) object.get("weight");
				final String valueString = (String) object.get("valueString");
				System.out.println("BECode:" + beCode + ":attCode" + attributeCode + ":weight:" + weightStr
						+ ": valueString:" + valueString);
				Attribute attribute = null;
				BaseEntity be = null;
				try {
					attribute = service.findAttributeByCode(attributeCode);
					be = service.findBaseEntityByCode(beCode);
					System.out.println("In EA : found " + be.getCode() + "," + attribute.getCode());
					final Double weight = Double.valueOf(weightStr);
					try {
						be.addAttribute(attribute, weight, valueString);

					} catch (final BadDataException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					service.update(be);
				} catch (final NoResultException e) {

				}
			});

			// Now link be to be
			List<Map> obj8 = null;
			try {
				obj8 = gennySheets.row2DoubleTuples(AttributeLink.class.getSimpleName());
			} catch (final IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			obj8.stream().forEach(object -> {
				new HashMap<String, AttributeLink>();
				final String code = (String) object.get("code");
				final String name = (String) object.get("name");
				final AttributeLink linkAttribute = new AttributeLink(code, name);
				service.insert(linkAttribute);
			});

			List<Map> obj3 = null;
			try {
				obj3 = gennySheets.row2DoubleTuples(EntityEntity.class.getSimpleName());
			} catch (final IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			obj3.stream().forEach(object -> {
				final String parentCode = (String) object.get("parentCode");
				final String targetCode = (String) object.get("targetCode");
				final String link = (String) object.get("linkCode");
				final String weightStr = (String) object.get("weight");
				object.get("valueString");
				BaseEntity sbe = null;
				BaseEntity tbe = null;
				try {
					sbe = service.findBaseEntityByCode(parentCode);
					tbe = service.findBaseEntityByCode(targetCode);
					final Attribute linkAttribute = service.findAttributeByCode(link);
					final Double weight = Double.valueOf(weightStr);
					sbe.addTarget(tbe, linkAttribute, weight);
					service.update(tbe);
				} catch (final NoResultException e) {

				} catch (final BadDataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}

		System.out.println(
				"**************************** IMPORTING QUESTION - QUESTION*************************************");

		List<Map> obj4 = null;
		try {
			obj4 = gennySheets.row2DoubleTuples(Question.class.getSimpleName());
		} catch (final IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		obj4.stream().map(object -> {
			final Map<String, Question> map = new HashMap<String, Question>();
			final String code = (String) object.get("code");
			final String name = (String) object.get("name");
			final String attrCode = (String) object.get("attribute_code");
			Attribute attr;
			attr = service.findAttributeByCode(attrCode);
			final Question q = new Question(code, name, attr);
			service.insert(q);
			map.put(code, q);
			return map;
		}).reduce((ac, acc) -> {
			ac.putAll(acc);
			return ac;
		}).get();

		System.out.println("**************************** IMPORTING ASK - ASK*************************************");

		List<Map> obj5 = null;
		try {
			obj5 = gennySheets.row2DoubleTuples(Ask.class.getSimpleName());
		} catch (final IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		obj5.stream().map(object -> {
			final Map<String, Ask> map = new HashMap<String, Ask>();
			final String qCode = (String) object.get("question_code");
			final String name = (String) object.get("name");
			final String sourceCode = (String) object.get("sourceCode");
			final String targetCode = (String) object.get("targetCode");
			object.get("refused");
			object.get("expired");

			Question q;
			q = service.findQuestionByCode(qCode);
			final BaseEntity sourceObj = service.findBaseEntityByCode(sourceCode);
			final BaseEntity targetObj = service.findBaseEntityByCode(targetCode);
			final Ask a = new Ask(q, sourceObj.getCode(), targetObj.getCode());
			a.setName(name);
			service.insert(a);
			map.put(q.getCode() + sourceObj.getCode(), a);
			return map;
		}).reduce((ac, acc) -> {
			ac.putAll(acc);
			return ac;
		}).get();

		securityService.setImportMode(false);
	}

	// @PostConstruct
	public void init3() {
		final Map<String, Runnable> projects = new HashMap<String, Runnable>();
		projects.put("genny", () -> genny(genny, gennyPath));
		projects.put("channel40", () -> genny(channel40, channelPath));
		projects.get("channel40").run();

		System.out.println("\n\n**************************************************************************\n\n");
		// projects.get("genny").run();
	}

	public Map<String, Object> genny(final String projectType, final File path) {

		final GennySheets2 sheets = new GennySheets2(secret, projectType, path);

		final Map<String, Validation> gValidations = sheets.validationData();
		gValidations.entrySet().stream().map(tuple -> tuple.getValue()).forEach(validation -> {
			service.insert(validation);
		});
		final Map<String, DataType> gDataTypes = sheets.dataTypesData(gValidations);
		final Map<String, Attribute> gAttrs = sheets.attributesData(gDataTypes);
		gAttrs.entrySet().stream().map(tuple -> tuple.getValue()).forEach(attr -> {
			service.insert(attr);
		});
		final Map<String, BaseEntity> gBes = sheets.baseEntityData();
		gBes.entrySet().stream().map(tuple -> tuple.getValue()).forEach(be -> {
			service.insert(be);
		});
		final Map<String, BaseEntity> gAttr2Bes = sheets.attr2BaseEntitys(gAttrs, gBes);
		gAttr2Bes.entrySet().stream().map(tuple -> tuple.getValue()).forEach(be -> {
			System.out.println(be + "***********************");
			service.update(be);
		});
		final Map<String, AttributeLink> gAttrLink = sheets.attrLink();
		gAttrLink.entrySet().stream().map(tuple -> tuple.getValue()).forEach(link -> {
			service.insert(link);
		});
		final Map<String, BaseEntity> gBes2Bes = sheets.be2BeTarget(gAttrLink, gAttr2Bes);
		gBes2Bes.entrySet().stream().map(tuple -> tuple.getValue()).forEach(tbe -> {
			service.update(tbe);
		});
		final Map<String, Question> gQuestions = sheets.questionsData(gAttrs);
		gQuestions.entrySet().stream().map(tuple -> tuple.getValue()).forEach(q -> {
			service.insert(q);
		});
		final Map<String, Ask> gAsks = sheets.asksData(gQuestions, gBes);
		gAsks.entrySet().stream().map(tuple -> tuple.getValue()).forEach(ask -> {
			service.insert(ask);
		});
		final Map<String, Object> genny = new HashMap<String, Object>();
		genny.put("validations", gValidations);
		genny.put("dataType", gDataTypes);
		genny.put("attributes", gAttrs);
		genny.put("baseEntitys", gBes);
		genny.put("attibutesEntity", gAttr2Bes);
		genny.put("attributeLink", gAttrLink);
		genny.put("basebase", gBes2Bes);
		genny.put("questions", gQuestions);
		genny.put("ask", gAsks);

		return genny;
	}
}
