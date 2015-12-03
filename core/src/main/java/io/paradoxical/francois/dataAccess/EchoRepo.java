package io.paradoxical.francois.dataAccess;

import io.paradoxical.francois.dataAccess.interfaces.DbRepo;

public class EchoRepo implements DbRepo {
    @Override public String echo(final String data) {
        return data;
    }
}
