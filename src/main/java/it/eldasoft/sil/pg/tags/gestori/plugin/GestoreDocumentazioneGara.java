package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.tags.funzioni.GetITERGAMacroFunction;
import it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;

import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore preload per la pagina documentazione di gara.
 * Viene letto il valore del campo radio button "filtroDocumentazione",
 * ed in base al suo valore viene valorizzata la variabile "tipoDoc"
 *
 * @author Marcello Caminiti
 */

public class GestoreDocumentazioneGara extends AbstractGestorePreload {

  public GestoreDocumentazioneGara(BodyTagSupportGene tag) {
    super(tag);
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {

  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    String chiave = (String) page.getAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, PageContext.REQUEST_SCOPE);

    String filtroDocumentazione = UtilityTags.getParametro(page,"filtroDocumentazione");
    String isProceduraTelematica = UtilityTags.getParametro(page,"isProceduraTelematica");
    //String faseRicezione = UtilityTags.getParametro(page,"isFaseRicezione");
    String faseRicezione = (String) page.getAttribute("isFaseRicezione", PageContext.REQUEST_SCOPE);
    boolean isFasiRicezione= false;
    if(faseRicezione!=null && !"".equals(faseRicezione))
      isFasiRicezione=true;
    String lottoDiGara = UtilityTags.getParametro(page,"lottoDiGara");
    String idconfi = UtilityTags.getParametro(page,"idconfi");
    
    GetTipologiaGaraFunction function = new GetTipologiaGaraFunction();

    String genereGara = function.function(page, new Object[]{page,chiave});
    String pagina="";

    GetITERGAMacroFunction functionIterga = new GetITERGAMacroFunction();
    String itergaMacro = functionIterga.function(page, new Object[]{page,chiave});
    String tipoDocumento = filtroDocumentazione;
    Long iterga = (Long)page.getAttribute("iterga");

    page.setAttribute("iterga", iterga, PageContext.REQUEST_SCOPE);

    if(filtroDocumentazione == null || "".equals(filtroDocumentazione)){
      //Valutazione del tipo di documentazione in cui aprire la pagina la prima volta
      if("2".equals(genereGara))
        pagina="GARE-scheda";
      else if("3".equals(genereGara))
        pagina="TORN-OFFUNICA-scheda";
      else
        if(chiave.indexOf("NGARA") > 0)
          pagina="GARE-scheda";
        else
          pagina="TORN-scheda";

      HttpSession session = page.getSession();

      String profiloAttivo = (String) session.getAttribute("profiloAttivo");

      GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
          page, GeneManager.class);

      Long filtroAttivo = null;
      if(isFasiRicezione || geneManager.getProfili().checkProtec(profiloAttivo,"SEZ","VIS","GARE." + pagina + ".DOCUMGARA.DOCUMGARA"))
        filtroAttivo = new Long(1);
      else{
        if(geneManager.getProfili().checkProtec(profiloAttivo,"SEZ","VIS","GARE." + pagina + ".DOCUMGARA.DOCUMREQ") && !"3".equals(itergaMacro))
          filtroAttivo = new Long(2);
        else{
          if(geneManager.getProfili().checkProtec(profiloAttivo,"SEZ","VIS","GARE." + pagina + ".DOCUMGARA.DOCUMCONC"))
            filtroAttivo = new Long(3);

        }
      }


      page.setAttribute("tipoDoc", filtroAttivo, PageContext.REQUEST_SCOPE);
      if(filtroAttivo!=null)
        tipoDocumento = filtroAttivo.toString();
    }else if("1".equals(filtroDocumentazione)){
      page.setAttribute("tipoDoc", new Long(1), PageContext.REQUEST_SCOPE);
    }else if("2".equals(filtroDocumentazione)){
      page.setAttribute("tipoDoc", new Long(2), PageContext.REQUEST_SCOPE);
    }else if("3".equals(filtroDocumentazione)){
      page.setAttribute("tipoDoc", new Long(3), PageContext.REQUEST_SCOPE);
    }else if("4".equals(filtroDocumentazione)){
      page.setAttribute("tipoDoc", new Long(4), PageContext.REQUEST_SCOPE);
    }else if("5".equals(filtroDocumentazione)){
      page.setAttribute("tipoDoc", new Long(5), PageContext.REQUEST_SCOPE);
    }else if("6".equals(filtroDocumentazione)){
      page.setAttribute("tipoDoc", new Long(6), PageContext.REQUEST_SCOPE);
    }else if("8".equals(filtroDocumentazione)){
      //Non si tratta di una tipologia di documenti, ma poichè nelle fasi invito nella stessa pagina nel caso di offerte distinte
      //si devono riportare i documenti richiesti ai concorrenti relativi alla gara e quelli relativi ai lotti, uso la tipoDoc=3
      //per i primi, mentre adopero tipoDoc=8 per i secondi.
      page.setAttribute("tipoDoc", new Long(8), PageContext.REQUEST_SCOPE);
    }else if("10".equals(filtroDocumentazione)){
      page.setAttribute("tipoDoc", new Long(10), PageContext.REQUEST_SCOPE);
    }else if("15".equals(filtroDocumentazione)){
      page.setAttribute("tipoDoc", new Long(15), PageContext.REQUEST_SCOPE);
    }

    //Se è stata eseguita la pubblicazione su portale web si deve bloccare ogni
    //modifica nella pagina
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",page, SqlManager.class);

    HashMap key = UtilityTags.stringParamsToHashMap(
        (String) page.getAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, PageContext.REQUEST_SCOPE),null);

    String codiceGara = null;
    String select = null;
    if (key.get("GARE.NGARA") != null){
      String ngara = ((JdbcParametro) key.get("GARE.NGARA")).getStringValue();
      select="select CODGAR1 from GARE where NGARA=?";
      try {
        codiceGara = (String) sqlManager.getObject(select,new Object[] { ngara });
      }catch (SQLException e) {
        throw new JspException(
            "Errore durante la determinazione del codice della gara", e);
       }
    }else{
      codiceGara = ((JdbcParametro) key.get("TORN.CODGAR")).getStringValue();
    }


    if (UtilityTags.SCHEDA_MODO_VISUALIZZA.equals(modoAperturaScheda)) {

      //Controllo sulla pubblicazione bando sul portale
      select="select count(CODGAR9) from PUBBLI where CODGAR9 = ? and (TIPPUB=11 or TIPPUB=13 or TIPPUB=23)";
      if("6".equals(tipoDocumento) || (isFasiRicezione && (new Long(2).equals(iterga) || new Long(4).equals(iterga))))
        select="select count(CODGAR9) from PUBBLI where CODGAR9 = ? and (TIPPUB=13 or TIPPUB=23)";
      try {
        Long numeroPubblicazioni = (Long) sqlManager.getObject(select, new Object[] { codiceGara });

        if (numeroPubblicazioni != null && numeroPubblicazioni.longValue()>0)
          page.setAttribute("bloccoPubblicazionePortale","SI", PageContext.REQUEST_SCOPE);
      } catch (SQLException e) {
        throw new JspException(
            "Errore nel conteggio delle pubblicazioni bando su portale", e);
      }

      //Controllo sulla pubblicazione esito sul portale
      try {
        Long numeroPubblicazioniEsito = null;
        if (key.get("GARE.NGARA") != null){
          String ngara = ((JdbcParametro) key.get("GARE.NGARA")).getStringValue();
          select="select count(NGARA) from PUBG where NGARA = ? and TIPPUBG=12";
          numeroPubblicazioniEsito = (Long) sqlManager.getObject(select, new Object[] { ngara });
        }else{
          select="select count(NGARA) from PUBG where NGARA in (select NGARA from GARE where CODGAR1= ?) and TIPPUBG=12";
          numeroPubblicazioniEsito = (Long) sqlManager.getObject(select, new Object[] { codiceGara });
        }
        if (numeroPubblicazioniEsito != null && numeroPubblicazioniEsito.longValue()>0)
          page.setAttribute("bloccoPubblicazioneEsitoPortale","SI", PageContext.REQUEST_SCOPE);
      }catch (SQLException e) {
        throw new JspException(
            "Errore nel conteggio delle pubblicazioni esito su portale", e);
      }

    }
    //Il gestore viene richiamato anche nelle pagine "Invito" delle fasi ricezione, in cui serve
    //sapere nel caso di integrazione WSDM se il tipo documentale è PALEO e se è presente l'invio
    //mail a carico documentale.
    String integrazioneWSDM="0";

    GestioneWSDMManager gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        page, GestioneWSDMManager.class);

    try {
      boolean isIntegrazioneWSDMAttivaValida = gestioneWSDMManager.isIntegrazioneWSDMAttivaValida(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO, idconfi);
      if(isIntegrazioneWSDMAttivaValida)
        integrazioneWSDM="1";
    } catch (GestoreException e) {
      throw new JspException("Errore nella verifica dell'integrazione WSDM Protocollo per la gara " + codiceGara ,e);
    } catch (SQLException e) {
      throw new JspException("Errore nella verifica dell'integrazione WSDM Protocollo per la gara " + codiceGara ,e);
    }

    page.setAttribute("integrazioneWSDM",integrazioneWSDM, PageContext.REQUEST_SCOPE);

    if("1".equals(integrazioneWSDM)){



        boolean delegaInvioMailDocumentaleAbilitata = false;
        String valoreWSDM = ConfigManager.getValore("pg.wsdm.invioMailPec."+idconfi );
        if(valoreWSDM!=null  && "1".equals(valoreWSDM))
          delegaInvioMailDocumentaleAbilitata=true;

        String tipoWSDM=null;
        if(delegaInvioMailDocumentaleAbilitata){
          WSDMConfigurazioneOutType config;
          try {
            config = gestioneWSDMManager.wsdmConfigurazioneLeggi("FASCICOLOPROTOCOLLO",idconfi);
            if (config.isEsito())
              tipoWSDM = config.getRemotewsdm();
            if(delegaInvioMailDocumentaleAbilitata && "PALEO".equals(tipoWSDM) )
              page.setAttribute("invioDocumentalePaleo",new Boolean(true), PageContext.REQUEST_SCOPE);
          } catch (GestoreException e) {
            throw new JspException(
                "Errore nella lettura del tipo di documentale", e);
          }

        }

        if(GestioneWSDMManager.getAbilitazioneInvioSingolo(idconfi)){
          page.setAttribute("abilitatoInvioSingolo",new Boolean(true), PageContext.REQUEST_SCOPE);
        }

    }

    //Si determina se è attiva l'integrazione con WSERP
    String integrazioneWSERP="0";
    String urlWSERP = ConfigManager.getValore("wserp.erp.url");
    if(urlWSERP != null && !"".equals(urlWSERP)){
      integrazioneWSERP ="1";
      GestioneWSERPManager gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
          page, GestioneWSERPManager.class);

      try {
        WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
        if (configurazione.isEsito()) {
          String tipoWSERP = configurazione.getRemotewserp();
          tipoWSERP = UtilityStringhe.convertiNullInStringaVuota(tipoWSERP);
          page.setAttribute("tipoWSERP", tipoWSERP, PageContext.REQUEST_SCOPE);

          Long countRda = new Long(0);

          if("AVM".equals(tipoWSERP) || "UGOVPA".equals(tipoWSERP)){
            countRda = (Long) sqlManager.getObject(
                "select count(*) from garerda where codgar = ?",new Object[] { codiceGara });
          }

          if(new Long(0).equals(countRda)){
            if("3".equals(genereGara)){
              countRda = (Long) sqlManager.getObject(
                  "select count(*) from gcap where codrda is not null and ngara in " +
                  "(select ngara from gare where codgar1 = ?)",new Object[] { codiceGara });

            }else{
              if (key.get("GARE.NGARA") != null){
                String ngara = ((JdbcParametro) key.get("GARE.NGARA")).getStringValue();
                countRda = (Long) sqlManager.getObject(
                    "select count(*) from gcap where ngara = ? and codrda is not null ",new Object[] { ngara });
              }

            }
          }

          if(countRda > 0){
            String visAllegatiRda= "true";
            page.setAttribute("visAllegatiRda", visAllegatiRda, PageContext.REQUEST_SCOPE);
          }

        }

      } catch (GestoreException ge) {
        UtilityStruts.addMessage(page.getRequest(), "error",
            "wserpconfigurazione.erp.configurazioneleggi.remote.error",new Object[]{":\r\n Configurazione integrazione con sistema ERP non corretta"});
      } catch (SQLException sqle) {
        throw new JspException(
            "Errore durante la verifica delle RdA in gara" , sqle);
      }

    }
    page.setAttribute("integrazioneWSERP", integrazioneWSERP, PageContext.REQUEST_SCOPE);

  }
}
