package io.paradoxical.francois.dataAccess;

import io.paradoxical.francois.dataAccess.interfaces.DbRepo;
import com.google.inject.Inject;
import lombok.Getter;

public class RepositoryAggregator {

    @Getter
    private final DbRepo rgGateway;

    @Inject
    public RepositoryAggregator(DbRepo rgGateway) {
        this.rgGateway = rgGateway;
    }
}
