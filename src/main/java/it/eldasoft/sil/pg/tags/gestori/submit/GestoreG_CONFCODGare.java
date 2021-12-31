        /*
 * Created on: 06-apr-2017
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.gestori.submit.GestoreG_CONFCOD;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore di submit dell'entita' G_CONFCOD per GARE
 *
 * estende Gestore di submit dell'entita' G_CONFCOD di GENE
 *
 * @author Francesco.DiMattei
 */
public class GestoreG_CONFCODGare extends GestoreG_CONFCOD {


  @Override
  public String getEntita() {
    return "G_CONFCOD";
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    super.preUpdate(status, datiForm);


    String noment = datiForm.getColumn("G_CONFCOD.NOMENT").getValue().getStringValue();
    String nomcam = datiForm.getColumn("G_CONFCOD.NOMCAM").getValue().getStringValue();
    String chkcodificaattiva = datiForm.getColumn("CHKCODIFICAATTIVA").getValue().getStringValue();
    String codcal = datiForm.getColumn("G_CONFCOD.CODCAL").getValue().getStringValue();
    String codapp = datiForm.getColumn("G_CONFCOD.CODAPP").getValue().getStringValue();
    String nomentSelect = "";
    String nomcamSelect = "";
    String chkcodificaattivaSelect = "";
    String codcalSelectReplace = "";
    long lengthNgara = 0 ;
    Object parametri[] = null;
    String selCodcal = "SELECT CODCAL FROM G_CONFCOD WHERE NOMENT=? AND NOMCAM=?";
    //CONTROLLI DA FARE SOLO PER CODGAR.TORN e NGARA.GARE, rispettivamente 'Codice gara' e 'Suffisso lotto'
    
    if("G1".equals(codapp) && codcal!=null && codcal.contains("$")){
      throw new GestoreException("Sintassi non corretta. Il carattere '$' non è accettato per i campi riguardanti 'Appalti e Affidamenti'", "configCodificaAutomatica.carattNonAccettatiPG",null);
    }
    
    if(!"PRECED".equals(nomcam)){
      if(noment.toUpperCase().equals("TORN") || noment.toUpperCase().equals("GARE")){
        if(noment.toUpperCase().equals("TORN") ){
          nomentSelect = "GARE";
          nomcamSelect = "NGARA";
        }else {
          nomentSelect = "TORN";
          nomcamSelect = "CODGAR";
        }
        try {

          parametri = new Object[]{nomentSelect,nomcamSelect};
          String codcalSelect = (String) this.sqlManager.getObject(selCodcal, parametri);
          if(codcalSelect == null || codcalSelect.equals("")){
            chkcodificaattivaSelect = "0";
          }else{
            chkcodificaattivaSelect = "1";
            //la somma della lunghezza massima delle due codifiche non può superare la dimensione del campo NGARA
            Campo campoNgara = DizionarioCampi.getInstance().getCampoByNomeFisico(
                "GARE.NGARA");
            lengthNgara = campoNgara.getLunghezza();
            codcalSelectReplace = codcalSelect.replaceAll("[<>\"]", "");
            if((codcalSelectReplace.length() + super.caratteri) > lengthNgara){
              throw new GestoreException("Sintassi non corretta. La somma della lunghezza massima delle codifiche di 'Codice gara' e 'Suffisso lotto' non può superare la lunghezza del campo 'NGARA'", "configCodificaAutomaticaGare.CodificaCodgarNgaraSuperioreLunghezzaNgara");
            };
          };
          //Visualizzo un messaggio di avviso (non bloccante) se, al salvataggio di una delle due codifiche, non risultano entrambe con la codifica attivata o disattivata.
          if(!chkcodificaattiva.equals(chkcodificaattivaSelect) && !"PRECED".equals(nomcam)){
            UtilityStruts.addMessage(this.getRequest(), "warning",
                "warnings.gare.configCodificaAutomatica.codificaDisallineataCodgarNgara", null);
          };
        } catch (SQLException e) {
          throw new GestoreException("Errore nel salvataggio dell'entità G_CONFCOD", null,e);
        }
      }
    }else if("PRECED".equals(nomcam)  && "1".equals(chkcodificaattiva)){
      //Nel caso di codifica automatica per il suffisso rilancio, si deve controllare soltanto che il num caratteri
      //per la codifica automatica di gare + il num di caratteri per la codifica automatica per il suffisso lotto +
      //num di caratteri per la codifica automatica per il suffisso rilancio si <= dimensione campo
      try {
        String codcalGara = (String) this.sqlManager.getObject(selCodcal, new Object[]{"TORN","CODGAR"});
        if(codcalGara!=null && !"".equals(codcalGara)){
          String codcalLotto = (String) this.sqlManager.getObject(selCodcal, new Object[]{"GARE","NGARA"});
          if(codcalLotto!=null && !"".equals(codcalLotto)){
            Campo campoNgara = DizionarioCampi.getInstance().getCampoByNomeFisico(
                "GARE.PRECED");
            lengthNgara = campoNgara.getLunghezza();
            codcalGara = codcalGara.replaceAll("[<>\"]", "");
            codcalLotto = codcalLotto.replaceAll("[<>\"]", "");
            if((codcalGara.length() + codcalLotto.length() + super.caratteri) > lengthNgara){
              throw new GestoreException("Sintassi non corretta. La somma della lunghezza massima delle codifiche di 'Codice gara', 'Suffisso lotto' e 'Suffisso rilancio' non può superare la lunghezza del campo 'NGARA'", "configCodificaAutomaticaGare.CodificaCodgarNgaraRilancioSuperioreLunghezzaNgara");
            };
          }
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nel salvataggio dell'entità G_CONFCOD", null,e);
      }

    }
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm)
      throws GestoreException {
  }

}