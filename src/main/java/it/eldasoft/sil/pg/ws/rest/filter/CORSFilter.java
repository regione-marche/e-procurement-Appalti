package it.eldasoft.sil.pg.ws.rest.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

/**
 * Classe per permettere agli applicativi esterni di chiamare Appalti tramite REST
 * @author Manuel.Bridda
 *
 */
@Provider
public class CORSFilter implements ContainerResponseFilter {
	private final Logger      logger                           = Logger.getLogger(CORSFilter.class);

  public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
	  logger.debug("Called CORSFilter");
    response.getHeaders().add("Access-Control-Allow-Origin", "*");
    response.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
    response.getHeaders().add("Access-Control-Allow-Credentials", "true");
    response.getHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
  }
}