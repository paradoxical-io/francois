package io.paradoxical.francois.unittests.server;

import io.paradoxical.francois.ServiceApplication;
import io.paradoxical.francois.bundles.GuiceBundleProvider;
import io.paradoxical.common.test.guice.ModuleUtils;
import io.paradoxical.common.test.guice.OverridableModule;
import com.google.inject.Module;

import java.util.List;

public class TestService extends ServiceApplication {
    public TestService(final List<OverridableModule> modules) {
        super(new TestGuiceBundleProvier(modules));
    }

    private static class TestGuiceBundleProvier extends GuiceBundleProvider {
        private List<OverridableModule> overridableModules;

        public TestGuiceBundleProvier(final List<OverridableModule> overridableModules) {
            this.overridableModules = overridableModules;
        }

        @Override
        protected List<Module> getModules() {
            return ModuleUtils.mergeModules(super.getModules(), overridableModules);
        }
    }
}
