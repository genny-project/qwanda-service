
package life.genny.qwanda.main;

import java.util.Map;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.keycloak.Secured;
import org.wildfly.swarm.logging.LoggingFraction;
import org.wildfly.swarm.swagger.SwaggerArchive;

import life.genny.qwanda.AnswerLink;
import life.genny.qwanda.CoreEntity;
import life.genny.qwanda.attribute.Attribute;
import life.genny.qwanda.converter.ValidationListConverter;
import life.genny.qwanda.datatype.DataType;
import life.genny.qwanda.endpoint.QwandaEndpoint;
import life.genny.qwanda.entity.BaseEntity;
import life.genny.qwanda.exception.BadDataException;
import life.genny.qwanda.message.QMessage;
import life.genny.qwanda.model.Setup;
import life.genny.qwanda.providers.CORSFilter;
import life.genny.qwanda.rule.Rule;
import life.genny.qwanda.service.BaseEntityService;
import life.genny.qwanda.util.PersistenceHelper;
import life.genny.qwanda.validation.Validation;


/**
 * @author Adam Crow
 */
public class Main {

  static String driverModule;


  public static void main(final String[] args) throws Exception {

    final Swarm swarm = new Swarm();

    final String useDB = System.getProperty("swarm.use.db", "mysql");

    // Configure the Datasources subsystem with a driver
    // and a datasource
    switch (useDB.toLowerCase()) {
      case "h2":
        swarm.fraction(datasourceWithH2());
        driverModule = "com.h2database.h2";
        break;
      case "postgresql":
        swarm.fraction(datasourceWithPostgresql());
        driverModule = "org.postgresql";
        break;
      case "mysql":
        swarm.fraction(datasourceWithMysql());
        driverModule = "com.mysql";
        break;
      default:
        swarm.fraction(datasourceWithH2());
        driverModule = "com.h2database.h2";
    }

    final SwaggerArchive archive = ShrinkWrap.create(SwaggerArchive.class, "qwanda-service.war");
    final JAXRSArchive deployment =
        archive.as(JAXRSArchive.class).addPackage(Main.class.getPackage());
    // deployment.addAsLibraries(arg0)
    deployment.addModule(driverModule);

    deployment.addPackage(Setup.class.getPackage());
    deployment.addPackage(BaseEntityService.class.getPackage());
    deployment.addPackage(QwandaEndpoint.class.getPackage());
    deployment.addPackage(CORSFilter.class.getPackage());
    deployment.addPackage(PersistenceHelper.class.getPackage());


    deployment.addPackage(Validation.class.getPackage());
    deployment.addPackage(BadDataException.class.getPackage());
    deployment.addPackage(Attribute.class.getPackage());
    deployment.addPackage(BaseEntity.class.getPackage());
    deployment.addPackage(CoreEntity.class.getPackage());
    deployment.addPackage(AnswerLink.class.getPackage());
    deployment.addPackage(ValidationListConverter.class.getPackage());
    deployment.addPackage(Rule.class.getPackage());
    deployment.addPackage(QMessage.class.getPackage());
    deployment.addPackage(DataType.class.getPackage());
    deployment.as(Secured.class);
  
    deployment.addAsWebInfResource(
        new ClassLoaderAsset("META-INF/persistence.xml", Main.class.getClassLoader()),
        "classes/META-INF/persistence.xml");


    // Tell swagger where our resources are
    archive.setResourcePackages("life.genny.qwanda.main");

    deployment.addAllDependencies();
    swarm.fraction(LoggingFraction.createDefaultLoggingFraction()).start().deploy(deployment);



    // Swarm swarm = new Swarm();
    //
    // swarm.fraction(
    // UndertowFraction.createDefaultFraction( "keystore.jks", "password", "selfsigned" )
    // );
    //
    // JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class, "myapp.war");
    // deployment.addClass(MyResource.class);
    // deployment.addAllDependencies();
    // swarm.start().deploy(deployment);
  }

  private static DatasourcesFraction datasourceWithH2() {
    return new DatasourcesFraction().jdbcDriver("h2", (d) -> {
      d.driverClassName("org.h2.Driver");
      d.xaDatasourceClass("org.h2.jdbcx.JdbcDataSource");
      d.driverModuleName("com.h2database.h2");
    }).dataSource("ExampleDS", (ds) -> {
      ds.driverName("h2");
      ds.connectionUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
      ds.userName("sa");
      ds.password("sa");
    });
  }

  private static DatasourcesFraction datasourceWithPostgresql() {
    return new DatasourcesFraction().jdbcDriver("org.postgresql", (d) -> {
      d.driverClassName("org.postgresql.Driver");
      d.xaDatasourceClass("org.postgresql.xa.PGXADataSource");
      d.driverModuleName("org.postgresql");
    }).dataSource("ExampleDS", (ds) -> {
      ds.driverName("org.postgresql");
      ds.connectionUrl("jdbc:postgresql://localhost:5432/postgres");
      ds.userName("postgres");
      ds.password("postgres");
    });
  }

  private static DatasourcesFraction datasourceWithMysql() {
    final Map<String, String> envParams = System.getenv();
    final String username = envParams.get("MYSQL_USER");
    final String password = envParams.get("MYSQL_PASSWORD");
    final String db = envParams.get("MYSQL_DB");
    final String port = envParams.get("MYSQL_PORT");
    final String mysql_url = envParams.get("MYSQL_URL");

    return new DatasourcesFraction().jdbcDriver("com.mysql", (d) -> {
      d.driverClassName("com.mysql.jdbc.Driver");
      d.xaDatasourceClass("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
      d.driverModuleName("com.mysql");
    })
        // Okay to make this public
        .dataSource("MyPU", (ds) -> {
          ds.driverName("com.mysql");
          ds.connectionUrl("jdbc:mysql://" + mysql_url + ":" + port + "/" + db + "?useSSL=false");
          ds.userName(username);
          ds.password(password);
        });
  }

}
