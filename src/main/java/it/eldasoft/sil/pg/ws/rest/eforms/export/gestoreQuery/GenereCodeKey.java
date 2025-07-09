package it.eldasoft.sil.pg.ws.rest.eforms.export.gestoreQuery;

import java.util.Objects;

/**
 * Rappresenta la chiave composta da (genere, code).
 */
public class GenereCodeKey {
    private final int genere;
    private final PathJsonEform code;

    public GenereCodeKey(int genere, PathJsonEform code) {
        this.genere = genere;
        this.code = code;
    }

    public int getGenere() {
        return genere;
    }

    public PathJsonEform getCode() {
        return code;
    }

    // equals() e hashCode() servono per usare GenereCodeKey come chiave in una mappa

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenereCodeKey)) return false;
        GenereCodeKey that = (GenereCodeKey) o;
        return genere == that.genere &&
               Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genere, code);
    }

    @Override
    public String toString() {
        return "GenereCodeKey{genere=" + genere + ", code='" + code.getCode() + "'}";
    }
}
