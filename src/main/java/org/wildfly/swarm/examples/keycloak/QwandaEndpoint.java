package org.wildfly.swarm.examples.keycloak;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.wildfly.swarm.examples.keycloak.models.Setup;
import org.wildfly.swarm.examples.keycloak.service.BaseEntityService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import life.genny.qwanda.Answer;
import life.genny.qwanda.AnswerLink;
import life.genny.qwanda.Ask;
import life.genny.qwanda.Question;
import life.genny.qwanda.attribute.Attribute;
import life.genny.qwanda.attribute.EntityAttribute;
import life.genny.qwanda.entity.BaseEntity;
import life.genny.qwanda.message.QDataAskMessage;
import life.genny.qwanda.rule.Rule;



/**
 * Transactional JAX-RS endpoint
 *
 * @author Adam Crow
 */

@Path("/qwanda")
@Api(value = "/qwanda", description = "Qwanda API", tags = "qwanda")
@Produces(MediaType.APPLICATION_JSON)


@RequestScoped
@Transactional

public class QwandaEndpoint {

	@Context
	SecurityContext sc;
	
	@Inject
	private BaseEntityService service;

	
	
	@POST
	@Consumes("application/json")
	@Path("/rules")
	public Response create(Rule entity) {
	    service.insert(entity);
	    return Response.created(UriBuilder.fromResource(QwandaEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
	}    


	@POST
	@Consumes("application/json")
	@Path("/attributes")
	public Response create(Attribute entity) {
	    service.insert(entity);
	    return Response.created(UriBuilder.fromResource(QwandaEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
	}    

	@POST
	@Consumes("application/json")
	@Path("/questions")
	public Response create(Question entity) {
	    service.insert(entity);
	    return Response.created(UriBuilder.fromResource(QwandaEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
	}    	

	@POST
	@Consumes("application/json")
	@Path("/asks")
	public Response create(Ask entity) {
	    service.insert(entity);
	    return Response.created(UriBuilder.fromResource(QwandaEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
	}    		
	
	@POST
	@Consumes("application/json")
	@Path("/answers")
	public Response create(Answer entity) {
		
	    service.insert(entity);
	    return Response.created(UriBuilder.fromResource(QwandaEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
	}    			
	
	@POST
	@Consumes("application/json")
	@Path("/baseentitys")
	public Response create(BaseEntity entity) {
	    service.insert(entity);
	    return Response.created(UriBuilder.fromResource(QwandaEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
	}
	
	@POST
	@Path("/baseentitys")
	@Consumes("application/json")
	public Response create(@FormParam("name") String name, @FormParam("uniqueCode") String uniqueCode) {
		BaseEntity entity = new BaseEntity(uniqueCode, name);
	    service.insert(entity);
	    return Response.created(UriBuilder.fromResource(QwandaEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
	}

	@GET
	@Path("/rules/{id}")
	public Response fetchRuleById(@PathParam("id") Long id) {
	    Rule entity = service.findRuleById(id);
	    return  Response.status(200).entity(entity).build();
	}
	
	@GET
	@Path("/baseentitys/{sourceCode}")
	public Response fetchBaseEntityByCode(@PathParam("sourceCode") String code) {
	    BaseEntity entity = service.findBaseEntityByCode(code);
	    return  Response.status(200).entity(entity).build();
	}
	
	@GET
	@Path("/questions/{id}")
	public Response fetchQuestionById(@PathParam("id") Long id) {
	    Question entity = service.findQuestionById(id);
	    return  Response.status(200).entity(entity).build();
	}

	@GET
	@Path("/questions")
	public Response fetchQuestions() {
	    List<Question> entitys = service.findQuestions();
	    return  Response.status(200).entity(entitys).build();
	}
	
	@GET
	@Path("/rules")
	public Response fetchRules() {
	    List<Rule> entitys = service.findRules();
	    
	    System.out.println(entitys);
	    return  Response.status(200).entity(entitys).build();
	}
	
	@GET
	@Path("/asks")
	public Response fetchAsks() {
	    List<Ask> entitys = service.findAsks();
	    
	    System.out.println(entitys);
	    return  Response.status(200).entity(entitys).build();
	}
	
	@GET
	@Path("/asksmsg")
	public Response fetchAsksMsg() {
	    List<Ask> entitys = service.findAsks();
	    QDataAskMessage qasks = new QDataAskMessage(entitys.toArray(new Ask[0]));
	    System.out.println(qasks);
	    return  Response.status(200).entity(qasks).build();
	}
	
	@GET
	@Path("/attributes/{id}")
	public Response fetchAttributeById(@PathParam("id") Long id) {
	    Attribute entity = service.findAttributeById(id);
	    return  Response.status(200).entity(entity).build();
	}
	
	@GET
	@Path("/asks/{id}")
	public Response fetchAskById(@PathParam("id") Long id) {
	    Ask entity = service.findAskById(id);
	    return  Response.status(200).entity(entity).build();
	}
	
	@GET
	@Path("/answers/{id}")
	public Response fetchAnswerById(@PathParam("id") Long id) {
	    Answer entity = service.findAnswerById(id);
	    return  Response.status(200).entity(entity).build();
	}
	
	@GET
	@Path("/contexts/{id}")
	public Response fetchContextById(@PathParam("id") Long id) {
	    life.genny.qwanda.Context entity = service.findContextById(id);
	    return  Response.status(200).entity(entity).build();
	}

	@GET
	@Path("/baseentitys/{sourceCode}/attributes")
	@ApiOperation(value = "attributes", notes = "BaseEntity Attributes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<EntityAttribute> fetchAttributesByBaseEntityCode(@PathParam("sourceCode") String code) {
		List<EntityAttribute> entityAttributes = service.findAttributesByBaseEntityCode(code);
	    return  entityAttributes;
	}

	@GET
	@Path("/baseentitys/{id}/asks/source")
	@ApiOperation(value = "asks", notes = "Source BaseEntity Asks")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Ask> fetchAsksBySourceBaseEntityId(@PathParam("id") Long id) {
		List<Ask> items = service.findAsksBySourceBaseEntityId(id);
	    return  items;
	}

	@GET
	@Path("/baseentitys/{id}/asks/target")
	@ApiOperation(value = "asks", notes = "BaseEntity Asks about Targets")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Ask> fetchAsksByTargetBaseEntityId(@PathParam("id") Long id) {
		List<Ask> items = service.findAsksBySourceBaseEntityId(id);
	    return  items;
	}
	
	@GET
	@Path("/baseentitys/{id}/answers")
	@ApiOperation(value = "answers", notes = "BaseEntity AnswerLinks")
	@Produces(MediaType.APPLICATION_JSON)
	public List<AnswerLink> fetchAnswersByTargetBaseEntityId(@PathParam("id") Long id) {
		List<AnswerLink> items = service.findAnswersByTargetBaseEntityId(id);
	    return  items;
	}
	
	@GET
	@Path("/answers")
	@ApiOperation(value = "answers", notes = "AnswerLinks")
	@Produces(MediaType.APPLICATION_JSON)
	public List<AnswerLink> fetchAnswerLinks() {
		List<AnswerLink> items = service.findAnswerLinks();
	    return  items;
	}

	@GET
	@Path("/logout")
	@ApiOperation(value = "Logout", notes = "Logout", response = String.class)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(@Context HttpServletRequest req) {
		try {
			req.logout();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(200).entity("Logged Out").build();
	}


	@GET
	@Path("/init")
	@Produces("application/json")
	public Response init() {
		if (sc != null) {
			if (sc.getUserPrincipal()!=null) {
			if (sc.getUserPrincipal() instanceof KeycloakPrincipal) {
				KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) sc
						.getUserPrincipal();

		service.init(kp.getKeycloakSecurityContext());
			}
			}
		}
		return Response.status(200).entity("Initialised").build();
	}

	@GET
	@Path("/baseentitys")
	@Produces("application/json")
	public Response getAll() {
		return  Response.status(200).entity(service.getAll()).build();
	}



	@GET
	@Path("/setup")
	@Produces("application/json")
	public Response getSetup() {
		Setup setup = new Setup();
		setup.setLayout("error-layout1");
		
		// this will set the user id as userName
		if (sc != null) {
			if (sc.getUserPrincipal()!=null) {
			String userName = sc.getUserPrincipal().getName();

			if (sc.getUserPrincipal() instanceof KeycloakPrincipal) {
				KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) sc
						.getUserPrincipal();

				// this is how to get the real userName (or rather the login
				// name)
				
				System.out.println("kc context:"+kp.getKeycloakSecurityContext());
				setup = service.setup(kp.getKeycloakSecurityContext());
			}
		}
		}
		return Response.status(200).entity(setup).build();

	}

	@GET
	@Path("/baseentitys/{sourceCode}/linkcodes/{linkCode}")
	@Produces("application/json")
	public Response getTargets(@PathParam("sourceCode") String sourceCode,@DefaultValue("LNK_CORE") @PathParam("linkCode") String linkCode) {
		List<BaseEntity> targets = service.findChildrenByAttributeLink(sourceCode, linkCode);
		
		return Response.status(200).entity(targets).build();
	}

	 @POST
	    @Path("/baseentitys/uploadcsv")
	    @Consumes("multipart/form-data")
	    public Response uploadFile(MultipartFormDataInput input) throws IOException {
	          
	        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
	 
	        // Get file data to save
	        List<InputPart> inputParts = uploadForm.get("attachment");
	 
	        for (InputPart inputPart : inputParts) {
	            try {
	 
	                MultivaluedMap<String, String> header = inputPart.getHeaders();
	                String fileName = getFileName(header);
	   
	                // convert the uploaded file to inputstream
	                InputStream inputStream = inputPart.getBody(InputStream.class,
	                        null);
	 
	             //   byte[] bytes = IOUtils.toByteArray(inputStream);
	                // constructs upload file path
	            //    writeFile(bytes, fileName);
	                service.importBaseEntitys(inputStream,fileName);
	                  
	                return Response.status(200).entity("Imported file name : " + fileName)
	                        .build();
	 
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        return null;
	    }
	 
	    private String getFileName(MultivaluedMap<String, String> header) {
	 
	        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
	 
	        for (String filename : contentDisposition) {
	            if ((filename.trim().startsWith("filename"))) {
	 
	                String[] name = filename.split("=");
	 
	                String finalFileName = name[1].trim().replaceAll("\"", "");
	                return finalFileName;
	            }
	        }
	        return "unknown";
	    }
	 


}
