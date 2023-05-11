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

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.SpringAppContext;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

public class AcquisisciProdottiMEManager {

  static Logger           logger = Logger.getLogger(AcquisisciProdottiMEManager.class);

  private SqlManager      sqlManager;

  private MEPAManager mepaManager;

  private PgManager pgManager;

  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
  *
  * @param sqlManager
  */

  /**
 *
 * @param mepaManager
 */
  public void setMepaManager(MEPAManager mepaManager) {
    this.mepaManager = mepaManager;
  }

  /**
   * Set PgManager
   *
   * @param pgManager
   */
  public void setPgManager(PgManager pgManager) {
      this.pgManager = pgManager;
  }

  /**
   *Gestione dei messaggi FS7 provenienti da portale per l'aggiornamento dei prodotti del Mercato Elettronico
   *
   */
  public void acquisisciProdotti() throws GestoreException{

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato ed inoltre deve esistere l'integrazione con il Portale Alice (OP114)
    if (WebUtilities.isAppNotReady()) return;
    ServletContext context = SpringAppContext.getServletContext();
    if(!GeneManager.checkOP(context, "OP114")) return;

    if (logger.isDebugEnabled())
      logger.debug("acquisisciProdotti: inizio metodo");

    Long idcom= null;
    //String select="select IDCOM from w_invcom where idprg = ? and comstato = ? and comtipo = ? order by IDCOM";
    String select = "SELECT idcom, userkey1, comkey2, comtipo FROM w_invcom, "
      + "w_puser WHERE idprg = ? AND comstato = ? AND (comtipo = ? OR comtipo = ?) AND comkey1 = usernome ORDER BY idcom";

    String idprg="PA";
    String comstato= "5";
    String user;
    String ngara;
    boolean operatoreTrovato=true;
    String comtipo=null;

    List listaIDCOM = null;
    try {
      listaIDCOM = sqlManager.getListVector(select,
            new Object[] { idprg, comstato,"FS7","FS8"});

    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della tabella W_INVCOM ", null, e);
    }
    if (listaIDCOM != null && listaIDCOM.size() > 0) {

      for (int i = 0; i < listaIDCOM.size(); i++) {
        idcom = SqlManager.getValueFromVectorParam(listaIDCOM.get(i), 0).longValue();
        user = SqlManager.getValueFromVectorParam(listaIDCOM.get(i), 1).getStringValue();
        ngara = SqlManager.getValueFromVectorParam(listaIDCOM.get(i), 2).getStringValue();
        comtipo = SqlManager.getValueFromVectorParam(listaIDCOM.get(i), 3).getStringValue();

        //Si deve controllare che l'impresa sia presente in gara sia direttamente che come
        //mandataria di una RT
        try {
            String datiControllo[] = pgManager.controlloEsistenzaDittaElencoGara(user, ngara, "$" + ngara,null);
            if("0".equals(datiControllo[0]) || "2".equals(datiControllo[0])){
              this.pgManager.aggiornaStatoW_INVOCM(idcom, "7", "");
              String msg="La ditta " + user + " che ha fatto richiesta di aggiornamento";
              if("0".equals(datiControllo[0]))
                msg+=" non è presente fra gli operatori del catalogo " + ngara;
              else if("2".equals(datiControllo[0]))
                msg+=" è presente come mandataria in più raggruppamenti temporanei del catalogo " + ngara;
              logger.error(msg);
              operatoreTrovato=false;
            }else{
              user = datiControllo[1];
              operatoreTrovato=true;
            }
          } catch (GestoreException e) {
            operatoreTrovato=false;
            this.pgManager.aggiornaStatoW_INVOCM(idcom, "7", "");
            logger.error("Errore nella ricerca del richiedente della richiesta di aggiornamento del catalogo " + ngara, e);
          }

        try {
          if(operatoreTrovato)
            mepaManager.insertProdotti(idcom, user, comtipo);
        } catch (GestoreException e) {
            mepaManager.aggiornaStatoW_INVOCM(idcom,"7");

        }

      }
    }


    if (logger.isDebugEnabled())
      logger.debug("acquisisciProdotti: fine metodo");

  }

}


