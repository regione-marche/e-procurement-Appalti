package it.eldasoft.sil.pg.ws.rest.cg;

import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.sil.pg.bl.CalcoloPunteggiManager;
import it.eldasoft.sil.pg.bl.utils.JwtTokenUtilities;
import it.eldasoft.utils.spring.UtilitySpring;
import net.sf.json.JSONObject;

@Path("/cg")
public class CgRestController {
	private final Logger logger = Logger.getLogger(CgRestController.class);

	private static final String JSON_CF = "USER_CF";

	@GET
	@Path("/calcoli/{ngara}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDatiPunteggiCalcolati(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("ngara")String ngara, @Context HttpServletRequest request) {
		long start = System.currentTimeMillis();
		try {
			logger.info("Ottenuta auth: "+auth);

			if(StringUtils.isBlank(auth)) return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();

			String token = StringUtils.substringAfter(auth, "Bearer ");
		    if(StringUtils.isBlank(token)) return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();

            String chiaveSegretaJwtChiaro = JwtTokenUtilities.getChiaveSegretaJwtChiaro();
		    JSONObject body = JSONObject.fromObject(JwtTokenUtilities.getBodyFromJwt(JwtTokenUtilities.parseJwt(token,chiaveSegretaJwtChiaro)));
		    String cfCommissario = body.getString(JSON_CF);

		    if(StringUtils.isBlank(cfCommissario) ) {
		       logger.warn("Impossibile trovare nel token il codice fiscale del commissario");
		       return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();
		     }

		    ServletContext sc = request.getSession().getServletContext();
		    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", sc, SqlManager.class);

		    //Si deve fare verifica di integrità del codice fiscale del commissario
		    String selectDatiGara="select genere, codgar from v_gare_genere where codice=?";
            Vector<JdbcParametro> datiGara=sqlManager.getVector(selectDatiGara, new String[] {ngara});
            String chiaveCommissione=ngara;
            if(datiGara!=null) {
              Long genere= SqlManager.getValueFromVectorParam(datiGara,0).longValue();
              if(new Long(300).equals(genere)) {
                String codgar=SqlManager.getValueFromVectorParam(datiGara,1).stringValue();
                chiaveCommissione = codgar;
              }
            }

		    String selectCfCommissari="select count(*)  from gfof, tecni  where ngara2=? and codtec=codfof and espgiu='1' and commicg='1' and cftec=?";
		    Long conteggio=(Long)sqlManager.getObject(selectCfCommissari, new String[] {chiaveCommissione,cfCommissario});
		    if(conteggio==null || new Long(0).equals(conteggio)) {
		      logger.warn("Il codice fiscale fornito nel token non corrisponde a quello di nessun commissario della gara");
              return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();
		    }

			logger.info("Ottenuta gara: "+ngara);

			CalcoloPunteggiManager calcoloPunteggiManager = (CalcoloPunteggiManager) UtilitySpring.getBean("calcoloPunteggiManager", sc, CalcoloPunteggiManager.class);
			CgResponse resp = calcoloPunteggiManager.calcolaPunteggi(chiaveCommissione, ngara, CalcoloPunteggiManager.CALCOLO_PUNT_TEC, CalcoloPunteggiManager.CALCOLO_DA_APP_ESTERNA);
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(resp);
			logger.debug("CgRestController - getDatiPunteggiCalcolati risultato: " + json);
			return Response.status(Status.OK).entity(resp).type(MediaType.APPLICATION_JSON_TYPE).build();
		} catch(ExpiredJwtException e){
	      logger.error("Error reading Jwt-token data",e);
	    } catch(MalformedJwtException e){
	      logger.error("Error reading Jwt-token data",e);
	    } catch(IllegalArgumentException e){
	      logger.error("Error reading Jwt-token data",e);
		} catch (Exception e) {
		  logger.error("Error retrieving data",e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).build();
		} finally {
	      logger.info("CgRestController - getDatiPunteggiCalcolati time execution "+(System.currentTimeMillis()-start)+"ms");
	    }
		return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();
	}


}
