package io.paradoxical.francois.bundles;

import io.paradoxical.francois.ServiceConfiguration;
import io.paradoxical.francois.ServiceApplication;
import io.paradoxical.francois.modules.DefaultApplicationModules;
import com.google.inject.Module;
import com.hubspot.dropwizard.guice.GuiceBundle;
import lombok.Getter;

import java.util.List;

public class GuiceBundleProvider {

    @Getter
    private final GuiceBundle<ServiceConfiguration> bundle;

    public GuiceBundleProvider() {
        bundle = buildGuiceBundle();
    }

    protected List<Module> getModules() {
        return DefaultApplicationModules.getModules();
    }

    private GuiceBundle<ServiceConfiguration> buildGuiceBundle() {
        final GuiceBundle.Builder<ServiceConfiguration> builder = GuiceBundle.<ServiceConfiguration>newBuilder();

        builder.enableAutoConfig(ServiceApplication.class.getPackage().getName())
               .setConfigClass(ServiceConfiguration.class);

        getModules().stream().forEach(builder::addModule);

        final GuiceBundle<ServiceConfiguration> guiceBundle = builder.build();

        return guiceBundle;
    }
}
