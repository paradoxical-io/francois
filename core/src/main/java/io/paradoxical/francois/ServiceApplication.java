package io.paradoxical.francois;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import de.thomaskrille.dropwizard_template_config.TemplateConfigBundle;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jersey.jackson.JacksonMessageBodyProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.dropwizard.views.ViewRenderer;
import io.dropwizard.views.mustache.MustacheViewRenderer;
import io.paradoxical.dropwizard.swagger.DefaultSwaggerResourcesLocator;
import io.paradoxical.dropwizard.swagger.SwaggerConfiguration;
import io.paradoxical.dropwizard.swagger.SwaggerResourcesLocator;
import io.paradoxical.dropwizard.swagger.SwaggerUIConfigurator;
import io.paradoxical.dropwizard.swagger.bundles.SwaggerUIBundle;
import io.paradoxical.dropwizard.swagger.resources.SwaggerApiResource;
import io.paradoxical.dropwizard.swagger.resources.SwaggerUIResource;
import io.paradoxical.francois.bundles.GuiceBundleProvider;
import io.paradoxical.francois.serialization.JacksonJsonMapper;
import io.swagger.jaxrs.config.BeanConfig;
import lombok.NonNull;
import org.joda.time.DateTimeZone;

import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static com.godaddy.logging.LoggerFactory.getLogger;


public class ServiceApplication extends Application<ServiceConfiguration> {

    private static final Logger logger = getLogger(ServiceApplication.class);
    private final GuiceBundleProvider guiceBundleProvider;

    public ServiceApplication(final GuiceBundleProvider guiceBundleProvider) {

        this.guiceBundleProvider = guiceBundleProvider;
    }

    public static void main(String[] args) throws Exception {
        DateTimeZone.setDefault(DateTimeZone.UTC);

        ServiceApplication serviceApplication = new ServiceApplication(new GuiceBundleProvider());

        try {
            serviceApplication.run(args);
        }
        catch (Throwable ex) {
            ex.printStackTrace();

            System.exit(1);
        }
    }

    @Override
    public void initialize(Bootstrap<ServiceConfiguration> bootstrap) {
        bootstrap.addBundle(new TemplateConfigBundle());

        initializeViews(bootstrap);

        initializeDepedencyInjection(bootstrap);
    }

    private void initializeViews(final Bootstrap<ServiceConfiguration> bootstrap) {
        List<ViewRenderer> viewRenders = new ArrayList<>();

        viewRenders.add(new MustacheViewRenderer());

        bootstrap.addBundle(new ViewBundle<>(viewRenders));

        bootstrap.addBundle(new SwaggerUIBundle(SwaggerUIConfigurator.forConfig(env -> getPublicSwagger(env))));

        bootstrap.addBundle(new AssetsBundle("/assets", "/assets"));
    }

    private void initializeDepedencyInjection(final Bootstrap<ServiceConfiguration> bootstrap) {
        bootstrap.addBundle(guiceBundleProvider.getBundle());
    }

    @Override
    public void run(ServiceConfiguration config, final Environment env) throws Exception {

        ArrayList<BiConsumer<ServiceConfiguration, Environment>> run = new ArrayList<>();

        run.add(this::configureJson);

        run.add(this::configureLogging);

        run.stream().forEach(configFunction -> configFunction.accept(config, env));
    }

    private void configureLogging(final ServiceConfiguration serviceConfiguration, final Environment environment) {
    }

    private SwaggerConfiguration getPublicSwagger(final Environment environment) {
        return new SwaggerConfiguration() {
            {
                setTitle("francois API");
                setDescription("francois API");
                setLicense("Apache 2.0");
                setResourcePackage(ServiceApplication.class.getPackage().getName());
                setLicenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html");
                setContact("admin@francois.io");
                setPrettyPrint(true);

                setBasePath(environment.getApplicationContext().getContextPath());

                setVersion("1.0");
            }
        };
    }

    protected void configureJson(ServiceConfiguration config, final Environment environment) {
        ObjectMapper mapper = new JacksonJsonMapper().getConfiguredMapper();

        JacksonMessageBodyProvider jacksonBodyProvider = new JacksonMessageBodyProvider(mapper, environment.getValidator());

        environment.jersey().register(jacksonBodyProvider);
    }
}
