package it.eldasoft.sil.pg.ws.rest.eforms.exceptions;

public class UnauthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message) {
        super(message);
    }
}
