/*
 * Created on 11-Set-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore per la pagina di dettaglio lavorazione o fornitura (GCAP)
 *
 * @author Marcello Caminiti
 */
public class GestoreGCAP extends AbstractGestoreChiaveNumerica {

  /** Manager Integrazione WSERP */
  private GestioneWSERPManager gestioneWSERPManager;

  @Override
  public String[] getAltriCampiChiave() {
    return new String[] { "NGARA" };
  }

  @Override
  public String getCampoNumericoChiave() {
    return "CONTAF";
  }

  @Override
  public String getEntita() {
    return "GCAP";
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

	  String ngara = datiForm.getString("GCAP.NGARA");
	  Long contaf = datiForm.getLong("GCAP.CONTAF");

	    //Integrazione con WSERP
	    String urlWSERP = ConfigManager.getValore("wserp.erp.url");
	    urlWSERP = UtilityStringhe.convertiNullInStringaVuota(urlWSERP);
	    if(!"".equals(urlWSERP)){
    	    try {
              Vector<?> rdaVect = this.getSqlManager().getVector(
                    "select codrda,posrda from GCAP where NGARA = ? and CONTAF = ?",
                    new Object[] { ngara, contaf});

              String codiceRda = (String) SqlManager.getValueFromVectorParam(rdaVect, 0).getValue();
              String posizioneRda = (String) SqlManager.getValueFromVectorParam(rdaVect, 1).getValue();
              codiceRda = UtilityStringhe.convertiNullInStringaVuota(codiceRda);
              posizioneRda = UtilityStringhe.convertiNullInStringaVuota(posizioneRda);
              if(!"".equals(codiceRda) && !"".equals(posizioneRda)){
                gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
                    this.getServletContext(), GestioneWSERPManager.class);
                  WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
                  if(configurazione.isEsito()){
                    String tipoWSERP = configurazione.getRemotewserp();
                    if("AVM".equals(tipoWSERP)){
                      String[] res = this.gestioneWSERPManager.scollegaRda(null, ngara, "2", codiceRda, posizioneRda, this.getRequest());
                      String ris = res[0];
                      int intRis = 0;
                      if(ris!=null) {
                      	 intRis=Long.valueOf(ris).intValue();
                      }
                      if(intRis < 0){
                        throw new GestoreException(
                            "Errore durante l'operazione di scollegamento delle RdA dalla gara",
                            "scollegaRdaGara", null);
                      }
                    }
                  }
              }
            } catch (SQLException e) {
              throw new GestoreException(
                  "Errore durante l'operazione di scollegamento delle RdA dalla gara",
                  "scollegaRdaGara", null);
            }
	    }

		// Cancellazione delle occorrenze correlate nell'entita' DPRE e GCAP_EST
		try {
			this.getSqlManager().update(
							"delete from DPRE where NGARA = ? and CONTAF = ?",
							new Object[] { ngara, contaf});
			this.getSqlManager().update(
					"delete from GCAP_EST where NGARA = ? and CONTAF = ?",
					new Object[] { ngara, contaf});

			this.getSqlManager().update(
                "delete from GCAP_SAN where NGARA = ? and CONTAF = ?",
                new Object[] { ngara, contaf});


		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nella cancellazione delle occorrenze figlie di GCAP con chiave "
							+ "NGARA = " + ngara + ", CONTAF = "
							+ contaf.toString() , null, e);
		}
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void afterDeleteEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    String esitoControlloAdesione=this.getRequest().getParameter("esitoControlloAdesione");
    String aqoper=this.getRequest().getParameter("aqoper");
    if("true".equals(esitoControlloAdesione) && "1".equals(aqoper)){
      String ngara = datiForm.getString("GCAP.NGARA");
      PgManager pgManager = (PgManager)UtilitySpring.getBean("pgManager", this.getServletContext(),PgManager.class);
      pgManager.aggiornamentoImportiDaLavorazioni(ngara, false);
    }
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
	  super.preInsert(status, datiForm);

	  //Controllo unicità codvoc
	  this.controlloUnicitaCodvoc(datiForm);

	  PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
				this.getServletContext(), PgManager.class);

	  String ngara = datiForm.getString("GCAP.NGARA");
      Long tipforn = pgManager.prelevaTIPFORN(ngara);

    //Integrazione con WSERP
      String urlWSERP = ConfigManager.getValore("wserp.erp.url");
      urlWSERP = UtilityStringhe.convertiNullInStringaVuota(urlWSERP);
      if(!"".equals(urlWSERP)){
        //integrazione ERP : per SmeUp controllo su  dei beni e servizi
        gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
            this.getServletContext(), GestioneWSERPManager.class);
          WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
          if(configurazione.isEsito()){
            String tipoWSERP = configurazione.getRemotewserp();
            tipoWSERP = UtilityStringhe.convertiNullInStringaVuota(tipoWSERP);
            if("SMEUP".equals(tipoWSERP)){
              String codiceCategoria = datiForm.getString("GCAP.CODCAT");
              codiceCategoria = UtilityStringhe.convertiNullInStringaVuota(codiceCategoria);
              if (!"".equals(codiceCategoria)) {
                String selVerificaCategoria = "select codcat from v_ubuy_beniservizi where codcat = ?";
                try {
                  String cat = (String) sqlManager.getObject(selVerificaCategoria, new Object[] { codiceCategoria });
                  cat = UtilityStringhe.convertiNullInStringaVuota(cat);
                  if ("".equals(cat)) {
                    throw new GestoreException("La categoria non risulta collegata a beni/servizi","CategoriaNoBeniServizi");
                  }
                } catch (SQLException e) {
                  throw new GestoreException("Errore nella verifica del collegamento della categoria a beni/servizi:" + codiceCategoria, null, e);
                }
              }
            }
          }
      }//if integrazione WSERP

      String profiloAttivo = "PG_DEFAULT";
      HttpSession session = this.getRequest().getSession();
      if ( session != null) {
        profiloAttivo = (String) session.getAttribute("profiloAttivo");
      }

      if (this.geneManager.getProfili().checkProtec(profiloAttivo, "FUNZ",
          "VIS", "ALT.GARE.ImportExportZoo")) {
          tipforn = new Long(98);
      }

	  //Aggiornamento di GCAP_EST
      if (tipforn== null || tipforn.longValue()==3)
        pgManager.aggiornaGCAP_EST("GCAP", datiForm);

	  //Aggiornamento di GCAP_SAN nel caso di TIPFORN = 1,2
	  if (tipforn!= null && (tipforn.longValue()==1 || tipforn.longValue()==2 || tipforn.longValue()==98)){
	    pgManager.aggiornaGCAP_SAN("GCAP",datiForm);
	  }
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    //Controllo unicità codvoc
    this.controlloUnicitaCodvoc(datiForm);

	  PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
				this.getServletContext(), PgManager.class);

	  String ngara = datiForm.getString("GCAP.NGARA");
	  String aqoper=this.getRequest().getParameter("aqoper");

      String integrazioneCineca = ConfigManager.getValore("integrazioneAnagraficheUGOV");
	  integrazioneCineca = UtilityStringhe.convertiNullInStringaVuota(integrazioneCineca);
	  //integrazione Cineca Gestione dei beni e servizi
	  if("1".equals(integrazioneCineca)){
	    String codiceCategoria = datiForm.getString("GCAP.CODCAT");
	    codiceCategoria = UtilityStringhe.convertiNullInStringaVuota(codiceCategoria);
        if (!"".equals(codiceCategoria)) {
          String selVerificaCategoria = "select codcat from v_ubuy_beniservizi where codcat = ?";
          try {
            String cat = (String) sqlManager.getObject(selVerificaCategoria, new Object[] { codiceCategoria });
            cat = UtilityStringhe.convertiNullInStringaVuota(cat);
            if ("".equals(cat)) {
              throw new GestoreException("La categoria non risulta collegata a beni/servizi","CategoriaNoBeniServizi");
            }
          } catch (SQLException e) {
            throw new GestoreException("Errore nella verifica del collegamento della categoria a beni/servizi:" + codiceCategoria, null, e);
          }
        }
	  }

	  String esitoControlloAdesione=this.getRequest().getParameter("esitoControlloAdesione");
	  if("true".equals(esitoControlloAdesione)){
	    try {
	      String ngaraaq = this.getRequest().getParameter("ngaraaq");
	      Long contafaq = datiForm.getLong("GCAP.CONTAFAQ");
	      Object quantiOrig = null;
	      if(contafaq!=null){
	        if("1".equals(aqoper)){
	          String ditta=this.getRequest().getParameter("ditta");
	          quantiOrig = sqlManager.getObject("select quantieff from v_gcap_dpre where ngara=? and contaf=? and cod_ditta=?", new Object[]{ngaraaq, contafaq,ditta});
	        }else{
	          quantiOrig = sqlManager.getObject("select quanti from gcap where ngara=? and contaf=?", new Object[]{ngaraaq, contafaq});
	        }
	      }
	      Double quantiOrigDouble = pgManager.getValoreImportoToDouble(quantiOrig);
	      Double quanti = datiForm.getDouble("GCAP.QUANTI");
	      if(quanti!=null && quantiOrigDouble!=null && quanti.doubleValue()>quantiOrigDouble.doubleValue()){
	        throw new GestoreException(
	              "La quantità specificata é maggiore di quella della corrispondente lavorazione nell'accordo quadro.", "riferimentoAccordoQuadro.Quantita",
	              new Object[]{(quantiOrigDouble.toString()).replace(".", ",")},new Exception());
	      }
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore nella lettura del campo V_GCAP_DPRE. QUANTIEFF per la gara "+  ngara , null, e);
        }
	  }

	  Long tipforn =pgManager.prelevaTIPFORN(ngara);

      String profiloAttivo = "PG_DEFAULT";
      HttpSession session = this.getRequest().getSession();
      if ( session != null) {
        profiloAttivo = (String) session.getAttribute("profiloAttivo");
      }

      if (this.geneManager.getProfili().checkProtec(profiloAttivo, "FUNZ",
          "VIS", "ALT.GARE.ImportExportZoo")) {
          tipforn = new Long(98);
      }

      //Aggiornamento di GCAP_EST
      if (tipforn== null || tipforn.longValue()==3)
        pgManager.aggiornaGCAP_EST("GCAP", datiForm);

	  //Aggiornamento di GCAP_SAN nel caso di TIPFORN = 1,2
      if (tipforn!= null && (tipforn.longValue()==1 || tipforn.longValue()==2 || tipforn.longValue()==98)){
        pgManager.aggiornaGCAP_SAN("GCAP",datiForm);
      }
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void afterUpdateEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    String esitoControlloAdesione=this.getRequest().getParameter("esitoControlloAdesione");
    String aqoper=this.getRequest().getParameter("aqoper");
    if("true".equals(esitoControlloAdesione) && "1".equals(aqoper)){
      String ngara = datiForm.getString("GCAP.NGARA");
      PgManager pgManager = (PgManager)UtilitySpring.getBean("pgManager", this.getServletContext(),PgManager.class);
      pgManager.aggiornamentoImportiDaLavorazioni(ngara,true);
    }
  }

  private void controlloUnicitaCodvoc(DataColumnContainer datiForm) throws GestoreException{
    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        this.getServletContext(), TabellatiManager.class);

    String descr = tabellatiManager.getDescrTabellato("A1162", "1");
    if(descr!=null && descr.length()>0 && "1".equals(descr.substring(0, 1))){
      String codvoc = datiForm.getString("GCAP.CODVOC");
      String ngara = datiForm.getString("GCAP.NGARA");
      Long contaf = datiForm.getLong("GCAP.CONTAF");
      String select = "select count(ngara) from gcap where ngara=? and contaf<>? and codvoc=?";
      try {

        Long conteggio = (Long)this.sqlManager.getObject(select, new Object[]{ngara,contaf,codvoc});
        if(conteggio!=null && conteggio>0){
          Long genere = (Long)this.sqlManager.getObject("select genere from v_gare_genere where codice =?", new Object[]{ngara});
          String tipo="nella gara";
          if(genere!=null && genere.longValue()!=2)
            tipo = "nel lotto";
          throw new GestoreException("Il codice della lavorazione è già specificato in altre lavorazioni della gara", "codvocDuplicati",new Object[]{tipo},new Exception());
        }
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nel controllo di unicità del valore del campo CODVOC per la gara " + ngara, null, e);
      }
    }
  }
}

