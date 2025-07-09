package it.eldasoft.sil.pg.ws.rest.largefile;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.ws.objectstorage.ObjectStorageUtils;
import it.eldasoft.sil.pg.bl.utils.JwtTokenUtilities;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.TransactionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

/**
 * This class will be used to receive data in REST mode from M-LARGEFILES
 * 
 * @author Manuel Bridda
 *
 */
@Path("/largefile")
public class LargeFileCallback {

  private final Logger logger = Logger.getLogger(getClass());

  private void setMaxInactiveIntervalSession(HttpServletRequest request){
    request.getSession().setMaxInactiveInterval(60);
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Response azioneScheda(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @Context HttpServletRequest request, @RequestBody JSONObject body) {

    if(logger.isDebugEnabled()) logger.debug("Called LargeFileCallback end point");

    setMaxInactiveIntervalSession(request);

    if (StringUtils.isBlank(auth))
      return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();

    String token = StringUtils.substringAfter(auth, "Bearer ");
    if (StringUtils.isBlank(token))
      return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();

    try {
      JwtTokenUtilities.parseJwt(token, ConfigManager.getValore("largefiles.symkey"));

      String codgar = (String) body.opt("codgar");
      String norddocg = (String) body.opt("norddocg");
      String file = (String) body.opt("file");
      String link = (String) body.opt("link");
      String bucket_ref = (String) body.opt("bucket_ref");
      String bucket_uuid = (String)body.opt("bucket_uuid");
      String fileName = (String)body.opt("file_name");
      String user = (String) body.opt("user");
      if(StringUtils.isEmpty(codgar) || StringUtils.isEmpty(norddocg) || StringUtils.isEmpty(file) || StringUtils.isEmpty(link)
              || StringUtils.isEmpty(bucket_ref) || StringUtils.isEmpty(bucket_uuid) || StringUtils.isEmpty(fileName))
        return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).build();

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", request.getSession().getServletContext(), SqlManager.class);

      byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(file);
      LobHandler lobHandler = new DefaultLobHandler();
      Long newIddocdig = (Long) sqlManager.getObject("SELECT max(iddocdig) FROM w_docdig WHERE idprg=?", new Object[]{"PG"});
      if (newIddocdig == null)
        newIddocdig = new Long(0);
      newIddocdig = new Long(newIddocdig.longValue() + 1);
      TransactionStatus status = sqlManager.startTransaction();
      try {
        List data = sqlManager.getListVector("select d.largefiledata, g.nome, d.gruppo from documgara d left join g1cf_pubb g on d.tipologia=g.id where d.codgar=? and d.norddocg=?", new Object[]{codgar, norddocg});
        if(data!=null && !data.isEmpty()) {
          String largefiledata = (String) SqlManager.getValueFromVectorParam(data.get(0), 0).getValue();
          if (StringUtils.isEmpty(largefiledata)) {
            String nome = (String) SqlManager.getValueFromVectorParam(data.get(0), 1).getValue();
            Long gruppo = (Long) SqlManager.getValueFromVectorParam(data.get(0), 2).getValue();
            sqlManager.update("INSERT INTO w_docdig(IDPRG,IDDOCDIG,DIGENT,DIGKEY1,DIGKEY2,"
                    + "DIGNOMDOC,DIGOGG) VALUES (?,?,?,?,?,?,?)", new Object[]{"PG", newIddocdig, "DOCUMGARA", codgar, norddocg, "PG" + newIddocdig + "_riepilogoLargefile.pdf", new SqlLobValue(bytes, lobHandler)});
            sqlManager.update("update DOCUMGARA set iddocdg=?, urldoc=?, largefiledata=? where codgar=? and norddocg=?", new Object[]{newIddocdig, link, bucket_ref + "|" + bucket_uuid + "|" + fileName, codgar, NumberUtils.toLong(norddocg)});
            ObjectStorageUtils.addFile("PG", newIddocdig);

            try {
              if (codgar != null && codgar.startsWith("$")) {
                codgar = codgar.substring(1);
              }
              LogEvento logEvento = LogEventiUtils.createLogEvento(request);
              logEvento.setLivEvento(1);
              logEvento.setOggEvento(codgar);
              logEvento.setCodEvento("GA_LARGEFILE_UPLOAD");
              logEvento.setDescr("Upload file da M-Largefile (id: " + norddocg + " - " + nome + " - " + gruppo + ")");
              logEvento.setIdUtente(Integer.parseInt(user));
              LogEventiUtils.insertLogEventi(logEvento);
            } catch (Exception le) {
              logger.error(le);
            }
          } else {
            return Response.status(Status.TOO_MANY_REQUESTS).type(MediaType.APPLICATION_JSON_TYPE).build();
          }
        } else {
          return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).build();
        }
        sqlManager.commitTransaction(status);
      } catch (Exception e) {
        sqlManager.rollbackTransaction(status);
        logger.error(e);
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type(MediaType.APPLICATION_JSON_TYPE).build();
      }

      return Response.ok().type(MediaType.APPLICATION_JSON_TYPE).build();
    }catch (Exception e) {
      logger.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

  }

}
