/*
 * Created on 26-05-2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

/**
 * Gestione delle inizializzazione della pagina gare-pg-ditteConcorrenti.jsp
 *
 * @author Marcello Caminiti
 */
public class GestioneDitteConcorrentiFunction extends AbstractFunzioneTag {

  PgManagerEst1 pgManagerEst1 = null;

  public GestioneDitteConcorrentiFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
        pageContext, GeneManager.class);

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
        "tabellatiManager", pageContext, TabellatiManager.class);

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        pageContext, PgManager.class);

    pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);

    String codiceTornata = null;
    boolean isGaraLottiConOffertaUnica = false;
    boolean isGaraUsoAlbo = false;
    String codiceElenco=null;
    String tipgen=null;
    Double importoTotaleBaseDAsta = null;
    Double importoTornata = null;
    int gareTipgarg = -1;

    String codiceGara = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
    codiceGara = codiceGara.substring(codiceGara.indexOf(":") + 1);

    if(codiceGara == null || codiceGara.length() == 0){
        codiceGara = UtilityTags.getParametro(pageContext,
          UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT);
      codiceGara = codiceGara.substring(codiceGara.indexOf(":") + 1);
    }


    String updateLista = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA);
    if (updateLista == null || updateLista.length() == 0) updateLista = "0";
    pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA, updateLista,
        PageContext.REQUEST_SCOPE);


    try {
      Vector dati = sqlManager.getVector(
          "select CODGAR1,GENERE,TIPGEN,ELENCOE,IMPAPP,IMPTOR,TIPGARG from GARE,TORN where TORN.CODGAR = GARE.CODGAR1 and GARE.NGARA = ?",
          new Object[]{codiceGara});
      if(dati != null){
        codiceTornata = ((JdbcParametro) dati.get(0)).getStringValue();
        if(dati.get(1) != null){
          Long tmp = (Long) ((JdbcParametro) dati.get(1)).getValue();
          if(tmp != null && tmp.longValue() == 3)
              isGaraLottiConOffertaUnica = true;

          pageContext.setAttribute("isGaraLottiConOffertaUnica",
                  "" + isGaraLottiConOffertaUnica, PageContext.REQUEST_SCOPE);
        }

        //Caricamento dei dati per il calcolo di RIBAUO e IMPOFF
        pgManager.setDatiCalcoloImportoOfferto(pageContext,codiceGara);
        pgManager.setDatiCalcoloRibasso(pageContext,sqlManager,codiceGara);

        if(dati.get(2) != null){
          String tmp1 = ((JdbcParametro) dati.get(2)).getStringValue();
          tipgen=tmp1;
        }

        if(dati.get(3) != null){
          String tmpEleOpEco = ((JdbcParametro) dati.get(3)).getStringValue();
          String catiga="";
          String numeroClassifica="";
          String isFoglia = "";
          String tipoCategoria = "";

          if(tmpEleOpEco != null && !"".equals(tmpEleOpEco)){
            isGaraUsoAlbo = true;
            codiceElenco = tmpEleOpEco;

            Vector datiCatg = sqlManager.getVector("select catiga,numcla from catg where ngara=? and ncatg=1",
                new Object[] { codiceGara });
            if(datiCatg!=null && datiCatg.size()>0){
              catiga = ((JdbcParametro) datiCatg.get(0)).getStringValue();
              Long numcla = ((JdbcParametro) datiCatg.get(1)).longValue();

              if (catiga==null)
                catiga="";

              if(!"".equals(catiga)){
                isFoglia = (String) sqlManager.getObject("select isfoglia from v_cais_tit where caisim = ?", new Object[]{catiga});
                Long tiplavg = (Long)sqlManager.getObject("select tiplavg from cais where caisim = ?", new Object[]{catiga});
                if(tiplavg!=null)
                  tipoCategoria = tiplavg.toString();
              }

              if(numcla!=null)
                numeroClassifica = numcla.toString();

            }
          }

          pageContext.setAttribute("isGaraUsoAlbo",
                "" + isGaraUsoAlbo, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("codiceElenco",
              codiceElenco, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("codiceCategoriaPrev",
              ""  + catiga, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("tipoCategoria",
              ""  + tipoCategoria, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("classifica",
              ""  + numeroClassifica, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("isFoglia",
              ""  + isFoglia, PageContext.REQUEST_SCOPE);

          pageContext.setAttribute("tipoGara",
              ""  + tipgen, PageContext.REQUEST_SCOPE);


          // Valore del campo GARE.IMPAPP, necessario
          if (dati.get(5)!=null)
            importoTotaleBaseDAsta = (Double) ((JdbcParametro) dati.get(4)).getValue();

          //Valore del campo TORN.IMPTOR
          if (dati.get(6)!=null)
            importoTornata = (Double) ((JdbcParametro) dati.get(5)).getValue();

        }
        if(dati.get(6) != null){
          Long tmp = (Long) ((JdbcParametro)dati.get(6)).getValue();
          if (tmp != null){
            gareTipgarg = tmp.intValue();
          }
        }

      }
    }catch(SQLException s){
      throw new JspException("Errore durante la lettura del tipo di tornata della gara ", s);
    }catch (GestoreException e) {
      throw new JspException("Errore durante la lettura del numero di classifica della categoria prevalente ", e);
    }

    //Nel caso di profilo Affidamenti si deve prelevare la data termine ricezione offerte, prendendo
    //in considerazione solo il caso di gare a lotto unico
    String profiloAttivo = (String) pageContext.getSession().getAttribute("profiloAttivo");
    if("PG_GARE_AFFIDA".equals(profiloAttivo) ){
      try {

        Date data = null;
        String ora = null;

        Vector datiTorn = sqlManager.getVector(
            "select DTEOFF,OTEOFF from TORN where codgar = ?", new Object[]{"$"+codiceGara});

        if(datiTorn.get(0) != null)
          data = (Date) ((JdbcParametro) datiTorn.get(0)).getValue();

        if(data != null){
          pageContext.setAttribute("dataTerminePresentazioneOfferta",
              UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA));
        }

        if(datiTorn.get(1) != null)
          ora = (String) ((JdbcParametro) datiTorn.get(1)).getValue();

        if(ora!=null && !"".equals(ora))
          pageContext.setAttribute("oraTerminePresentazioneOfferta",ora);

      } catch (SQLException e1) {
        throw new JspException("Errore durante la lettura della data termine ricezione offerte ", e1);
      }
    }


    //nel caso di gare ad offerta unica l'importo da prendere in considerazione per
    //determinare il numero di ditte iscritte e del numero minime ditte da
    //iscrivere nella gara è IMPTOR.TORN (CRISTIAN deve fare sapere se è definitivo)
    if(isGaraLottiConOffertaUnica){
      importoTotaleBaseDAsta = importoTornata;
    }


    // Gestione del numero di ditte iscritte e del numero minime ditte da
    // iscrivere nella gara in funzione del campo TORN.TIPGARG
    if(codiceElenco != null){

      try {
        Long vet[] = pgManager.getNumeroMinimoDitte(gareTipgarg, tipgen, importoTotaleBaseDAsta, codiceGara, null);
        if (vet!= null && vet.length>1){
          pageContext.setAttribute("numeroMinimoOperatori",
              vet[0], PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("numeroOperatoriSelezionati",
              vet[1], PageContext.REQUEST_SCOPE);
        }
      } catch (GestoreException e) {
        throw new JspException("Errore durante la lettura del numero minimo " +
            "degli operatori da selezionare", e);
      }

      ProfiloUtente profiloUtente = (ProfiloUtente) this.getRequest().getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      String modalitaSelezione = pgManager.getModalitaSelezioneDitteElenco(profiloUtente);
      pageContext.setAttribute("modalitaSelezioneDitteElenco", modalitaSelezione, PageContext.REQUEST_SCOPE);
    }

    String numeroCifreDecimaliRibasso;
    try {
      numeroCifreDecimaliRibasso = this.pgManagerEst1.getNumeroDecimaliRibasso(codiceTornata);
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura del numero di decimali da usare per il ribasso della gara(NGARA = '" + codiceGara + ")", e);
    }
    pageContext.setAttribute("numeroCifreDecimaliRibasso",numeroCifreDecimaliRibasso);


    pgManager.getOFFAUM(pageContext, sqlManager, codiceGara);

    // Creazione del parametro con la chiave da passare alla pagina di controllo
    // delle autorizzazioni
    String inputFiltro = "CODGAR=T:".concat(codiceTornata);
    pageContext.setAttribute("inputFiltro", inputFiltro, PageContext.REQUEST_SCOPE);

    //Valori in sessione adoperati per il filtro della pagina di selezione operatori economici
    HttpSession sessione = pageContext.getSession();
    sessione.setAttribute("filtro", null);
    sessione.setAttribute("modalitaFiltroCategorie", null);
    sessione.setAttribute("applicatoFiltroInOr", null);
    sessione.setAttribute("elencoUlterioriCategorie", null);
    sessione.setAttribute("elencoNumcla", null);
    sessione.setAttribute("elencoTiplavgUltCategorie", null);
    sessione.setAttribute("prevalenteSelezionata", null);
    sessione.setAttribute("filtroSpecifico", null);
    sessione.setAttribute("filtroZoneAtt", null);
    sessione.setAttribute("filtroAffidatariEsclusi", null);
    sessione.setAttribute("elencoIdFiltriSpecifici", null);
    sessione.setAttribute("elencoIdZoneAttivita", null);
    sessione.setAttribute("elencoAffidatariEsclusi", null);
    return null;
  }
}
