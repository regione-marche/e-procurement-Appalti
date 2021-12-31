/*
 * Created on 26/03/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.SpringAppContext;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

public class CondivisioneGaraComponentiCommissioneTask {

  static Logger           logger = Logger.getLogger(CondivisioneGaraComponentiCommissioneTask.class);

  private SqlManager      sqlManager;

  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   *
   * @throws GestoreException
   */
  public void setPermessiComponentiCommisione() throws GestoreException {

    if (WebUtilities.isAppNotReady()) return;
    ServletContext context = SpringAppContext.getServletContext();
    
    String codfof;
    String codgar;
    String ngara;
    Long genere;
    String codice;
    String nomtec;
    String cenint;
    String errDescr = "";
    
    if (logger.isDebugEnabled())
      logger.debug("setPermessiComponentiCommisione: inizio metodo");

    //Definisco i parametri che devono essere adoperati per il metodo registrazione
    String select="select gf.codfof,ga.ngara,ga.codgar1,gf.nomfof,v.cenint,v.genere from v_gare_torn v, gare ga, gfof gf where gf.ngara2 = ga.ngara and ga.codgar1 = v.codgar and v.isaggiu = '2' and (gf.indisponibilita is null or gf.indisponibilita = '2')";
    String selectPermessi = "select usrsys.syscon from tecni, usrsys where tecni.codtec  = ? and tecni.cftec = usrsys.syscf and (usrsys.sysdisab is null or usrsys.sysdisab = '0')";
    		
    try {
      List listaTecnici = sqlManager.getListVector(select,new Object[] { });
      
      if (listaTecnici != null && listaTecnici.size() > 0) {
        for (int i = 0; i < listaTecnici.size(); i++) {
          codfof = SqlManager.getValueFromVectorParam(listaTecnici.get(i), 0).getStringValue();
          ngara = SqlManager.getValueFromVectorParam(listaTecnici.get(i), 1).getStringValue();
          codgar = SqlManager.getValueFromVectorParam(listaTecnici.get(i), 2).getStringValue();
          nomtec = SqlManager.getValueFromVectorParam(listaTecnici.get(i), 3).getStringValue();
          cenint = SqlManager.getValueFromVectorParam(listaTecnici.get(i), 4).getStringValue();
          genere = (Long) SqlManager.getValueFromVectorParam(listaTecnici.get(i), 5).getValue();
          codice = codgar;
          if(genere != null && genere.intValue() == 2){
            codice = ngara;
          }
          String descrizione = "Condivisione privilegi di accesso alla gara per il commissario '" + nomtec + "' mediante task. ";
          errDescr = "";
          int livello = 1;
          
          String permessiAttribuitiCount = "select count(*) from usrsys,tecni,g_permessi where tecni.codtec = ? and usrsys.syscf = tecni.cftec and" +
          " g_permessi.syscon = usrsys.syscon and g_permessi.codgar = ?";
          Long cntPermesssi = (Long) this.sqlManager.getObject(permessiAttribuitiCount, new Object[] { codfof, codgar });
          if(cntPermesssi.intValue()<=0){
            
            List listaUtenti = sqlManager.getListVector(selectPermessi,new Object[] {codfof });
            
            if (listaUtenti != null && listaUtenti.size() == 1) {
                Long syscon = (Long) SqlManager.getValueFromVectorParam(listaUtenti.get(0), 0).longValue();
                Long numper = _getNextNumper();
                this.sqlManager.update("insert into g_permessi (numper, syscon, autori, propri, codgar, meruolo) values (?,?,?,?,?,?)",
                    new Object[]{ numper, syscon, 1, null, codgar, 2 });
                descrizione += "Privilegi assegnati all'utente con id. " + syscon + ".";
                
                String uffintAbilitata = ConfigManager.getValore("it.eldasoft.associazioneUffintAbilitata");
                if("1".equals(uffintAbilitata) && (cenint != null && !"".equals(cenint))){
                  String countUSR_EIN = "select count(*) from usr_ein where syscon = ? and usr_ein.codein = ?";
                  Long cntUsrein = (Long) this.sqlManager.getObject(countUSR_EIN, new Object[] { syscon, cenint });
                  if(cntUsrein.intValue()<=0){
                    String insertUSR_EIN = "insert into usr_ein (syscon, codein) values (?,?)";
                    Object[] objUSR_EIN = new Object[2];
                    objUSR_EIN[0] = syscon;
                    objUSR_EIN[1] = cenint;
                    this.sqlManager.update(insertUSR_EIN, objUSR_EIN);
                  }
                }
                
            }else{
              livello = 2;
              descrizione += "Privilegi NON assegnati perchè ";
              if(listaUtenti.size() == 0){
                descrizione += "non ci sono utenti applicativo abilitati con uguale codice fiscale.";
              }else if(listaUtenti.size() > 1){
                descrizione += "ci sono più utenti applicativo abilitati con uguale codice fiscale."; 
              }
            }
            
            try {
              LogEvento logEvento = new LogEvento();
              logEvento.setCodApplicazione("PG");
              logEvento.setOggEvento(codice);
              logEvento.setLivEvento(livello);
              logEvento.setCodEvento("GA_ASSEGNA_PERMESSI_TASK");
              logEvento.setDescr(descrizione);
              logEvento.setErrmsg(errDescr);
              LogEventiUtils.insertLogEventi(logEvento);
            } catch (Exception le) {
              logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
            }
          }
        }
      }
      
      
      
      
    } catch (SQLException e) {
      throw new GestoreException("Errore nella scrittura dei permessi per i componenti commissione", null, e);
    }
    
    if (logger.isDebugEnabled())
      logger.debug("setPermessiComponentiCommisione: fine metodo");

  }

  /**
   * 
   * @return
   * @throws SQLException
   */
  private Long _getNextNumper() throws SQLException {
    Long nextNumper = (Long) this.sqlManager.getObject("select max(numper) from g_permessi", new Object[] {});
    if (nextNumper == null) nextNumper = new Long(0);
    nextNumper = new Long(nextNumper.longValue() + 1);
    return nextNumper;
  }

}
