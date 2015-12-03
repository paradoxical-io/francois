package io.paradoxical.francois.modules;

import io.paradoxical.francois.dataAccess.RepositoryAggregator;
import io.paradoxical.francois.dataAccess.EchoRepo;
import io.paradoxical.francois.dataAccess.interfaces.DbRepo;
import com.google.inject.AbstractModule;

public class DataAccessModule extends AbstractModule {

    @Override protected void configure() {
        bind(DbRepo.class).to(EchoRepo.class);

        // self bind
        bind(RepositoryAggregator.class);
    }
}
