package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import net.sf.json.JSONObject;

public class CreaFascicoloPregressoAction extends Action {

  static Logger               logger = Logger.getLogger(CreaFascicoloPregressoAction.class);

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  private GestioneWSDMManager gestioneWSDMManager;

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  private GenChiaviManager genChiaviManager;

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }


  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    String elencoUffintFiltro = request.getParameter("elencoUffintFiltro");
    List<Object> parameters = new ArrayList<Object>();

    String select="select v.codice, v.genere,v.oggetto, v.codgar from V_GARE_GENERE v where v.genere in (1,2,3,10,11,20) "
        + "and exists (select * from pubbli where codgar=codgar9 and tippub in (11,12,13)) "
        + "and not exists (select * from wsfascicolo where key1=v.codice) "
        + "and not exists (select * from gare where ngara=codice and preced is not null) ";

    if(elencoUffintFiltro!=null && !"".equals(elencoUffintFiltro)) {
      String[] uffint = elencoUffintFiltro.replace("(", "").replace(")","").replace("'","").split(",");
      String inClause = "";
      for(int i=0;i<uffint.length;i++) {
        if(i>0) {
          inClause += ", ";
        }
        inClause += "?";
        parameters.add(uffint[i]);
      }
      inClause = "(" +inClause + ")";
      select += GestioneWSDMManager.FILTRO_UFFINT_SELECT_CONTEGGIO_FASCICOLI_MANCANTI.replace("$", inClause);
    }
    select += " order by v.genere";

    List<?> listaDati = null;
    boolean erroreDb=false;
    try {
      listaDati = sqlManager.getListVector(select, parameters.toArray());
    } catch (Exception e) {
      erroreDb=true;
      result.put("esito", "NOK");
      result.put("msg", "Errore nella lettura dei dati");
    }

    int fascicoliCreati=0;
    int fascicoliErrore=0;

    if(!erroreDb) {
      if(listaDati!=null && listaDati.size()>0) {

        int livEvento =1;
        String codEvento = "GA_WSDM_CREA_FASCICOLO";
        String oggEvento = "";
        String descrEvento = "Creazione fascicolo documentale";
        String errMsgEvento = "";

        String username = request.getParameter("username");
        String password = request.getParameter("password");


        String oggettofascicolo = null;
        String classificafascicolo = request.getParameter("classificafascicolo");
        String descrizionefascicolo = null;
        String struttura = request.getParameter("struttura");

        String tipoWSDM = "ENGINEERINGDOC";
        String entita = null;
        String key1 = null;
        String idconfi = request.getParameter("idconfi");
        Long genereGara = null;
        String servizio = "FASCICOLOPROTOCOLLO";

        HashMap<String, Object> parWSDM = null;
        int i=0;
        for(i=0; i<listaDati.size();i++) {
          try {
            livEvento =1;

            key1 = SqlManager.getValueFromVectorParam(listaDati.get(i), 0).getStringValue();
            genereGara = SqlManager.getValueFromVectorParam(listaDati.get(i), 1).longValue();
            if(new Long(11).equals(genereGara)) {
              oggettofascicolo = (String)this.sqlManager.getObject("select oggetto from gareavvisi where ngara=?", new Object[] {key1});
            }else if(new Long(10).equals(genereGara) || new Long(20).equals(genereGara)) {
              oggettofascicolo = (String)this.sqlManager.getObject("select oggetto from garealbo where ngara=?", new Object[] {key1});
            }else {
              oggettofascicolo = SqlManager.getValueFromVectorParam(listaDati.get(i), 2).getStringValue();
            }
            oggettofascicolo = key1 + " - " + oggettofascicolo;
            descrizionefascicolo = oggettofascicolo;
            oggEvento = key1;
            if(new Long(1).equals(genereGara))
              entita = "TORN";
            else
              entita = "GARE";

            parWSDM = new HashMap<String, Object>();
            parWSDM.put(GestioneWSDMManager.LABEL_CLASSIFICA_FASCICOLO, classificafascicolo);
            parWSDM.put(GestioneWSDMManager.LABEL_DESCRIZIONE_FASCICOLO, descrizionefascicolo);
            parWSDM.put(GestioneWSDMManager.LABEL_OGGETTO_FASCICOLO, oggettofascicolo);
            parWSDM.put(GestioneWSDMManager.LABEL_STRUTTURA, struttura);
            parWSDM.put(GestioneWSDMManager.LABEL_USERNAME, username);
            parWSDM.put(GestioneWSDMManager.LABEL_PASSWORD, password);

            String messaggio = this.gestioneWSDMManager.setFascicolo(tipoWSDM, servizio, idconfi, entita, key1, null, parWSDM);
            if(messaggio==null || "".equals(messaggio)){
              errMsgEvento="";
              fascicoliCreati++;
            }else{
              livEvento =3;
              errMsgEvento = messaggio;
              fascicoliErrore++;
            }


          } catch (Exception e) {
            livEvento =3;
            errMsgEvento = e.getMessage();
            fascicoliErrore++;
          }finally{
            LogEvento logevento = LogEventiUtils.createLogEvento(request);
            logevento.setLivEvento(livEvento);
            logevento.setOggEvento(oggEvento);
            logevento.setCodEvento(codEvento);
            logevento.setDescr(descrEvento);
            logevento.setErrmsg(errMsgEvento);
            LogEventiUtils.insertLogEventi(logevento);
            //Variabile di sessione che viene adoperata per gestire l'avanzamento della progress bar
            request.getSession().setAttribute(GestioneWSDMManager.CONTATORE_FASCICOLI_CREATI, new Long(i));
          }
        }



      }

      if(!erroreDb)
        result.put("esito", "OK");
      result.put("fascicoliCreati", new Long(fascicoliCreati));
      result.put("fascicoliErrore", new Long(fascicoliErrore));

      //Gestione rilanci
      String conteggioRilanci = request.getParameter("conteggioRilanci");
      if(conteggioRilanci!= null && !"".equals(conteggioRilanci) && !"0".equals(conteggioRilanci)) {

        int fascicoliRilanciCreati=0;
        int fascicoliRilanciErrore=0;


        select="select preced,ngara from gare where preced is not null "
            + "and exists (select * from pubbli where codgar1=codgar9 and tippub in (11,12,13)) "
            + "and not exists (select * from wsfascicolo where key1=ngara)";

        if(elencoUffintFiltro!=null && !"".equals(elencoUffintFiltro)) {
          select += GestioneWSDMManager.FILTRO_UFFINT_SELECT_CONTEGGIO_FASCICOLI_MANCANTI_RILANCI.replace("$", elencoUffintFiltro);
        }

        select += " order by ngara";

        try {
          listaDati = sqlManager.getListVector(select, new Object[] {});
        } catch (Exception e) {
          result.put("esitoR", "NOK");
          result.put("msgR", "Errore nella lettura dei dati dei rilanci");
        }

        if(listaDati!=null && listaDati.size()>0) {
          String selectFascicolo = "select entita, codice, anno, numero, classifica, struttura from wsfascicolo where key1 =?";
          String chiave =null;
          String ngaraRilancio=null;

          TransactionStatus status = null;
          boolean commit = true;

          int i=0;
          for(i=0; i<listaDati.size();i++) {
            try {
              status = this.sqlManager.startTransaction();
              chiave = SqlManager.getValueFromVectorParam(listaDati.get(i), 0).getStringValue();
              ngaraRilancio = SqlManager.getValueFromVectorParam(listaDati.get(i), 1).getStringValue();
              Vector<?> datiGara = this.sqlManager.getVector("select genere,codgar from v_gare_genere where codice =?", new Object[]{ chiave});
              if(datiGara!=null && datiGara.size()>0){
                if(new Long(300).equals( SqlManager.getValueFromVectorParam(datiGara, 0).longValue()))
                  chiave = SqlManager.getValueFromVectorParam(datiGara, 1).getStringValue();
              }
              Vector<?> datiFascicolo = this.sqlManager.getVector(selectFascicolo, new Object[]{chiave});
              if(datiFascicolo!=null && datiFascicolo.size()>0){
                String insert ="insert into wsfascicolo(id,entita, key1, codice, anno, numero, classifica, struttura ) "
                    + "values(?,?,?,?,?,?,?,?)";
                Object[] par = new Object[8];
                par[0] = new Long(this.genChiaviManager.getNextId("WSFASCICOLO"));
                par[1] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 0).getStringValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 0).getStringValue();
                par[2] = ngaraRilancio;
                par[3] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 1).getStringValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 1).getStringValue();
                par[4] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 2).longValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 2).longValue();
                par[5] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 3).getStringValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 3).getStringValue();
                par[6] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 4).getStringValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 4).getStringValue();
                par[7] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 5).getStringValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 5).getStringValue();

                this.sqlManager.update(insert, par);
                fascicoliRilanciCreati++;
                commit = true;
              }
            } catch (Exception e) {
              commit = false;
              fascicoliRilanciErrore++;
              logger.error("Errore nella creazione del fascicolo per la gara di rilancio " + ngaraRilancio, e);
            }finally {
              if (status != null) {

                if (commit == true) {
                  this.sqlManager.commitTransaction(status);
                } else {
                  this.sqlManager.rollbackTransaction(status);
                }

              }
              //Variabile di sessione che viene adoperata per gestire l'avanzamento della progress bar
              request.getSession().setAttribute(GestioneWSDMManager.CONTATORE_FASCICOLI_CREATI, new Long(i));
            }
          }
          result.put("esitoR", "OK");
          result.put("fascicoliRilanciCreati", new Long(fascicoliRilanciCreati));
          result.put("fascicoliRilanciErrore", new Long(fascicoliRilanciErrore));
          //Variabile di sessione che viene adoperata per gestire l'avanzamento della progress bar
          request.getSession().removeAttribute(GestioneWSDMManager.CONTATORE_FASCICOLI_CREATI);
        }
      }

    }

    out.println(result);
    out.flush();

    return null;

  }
}
