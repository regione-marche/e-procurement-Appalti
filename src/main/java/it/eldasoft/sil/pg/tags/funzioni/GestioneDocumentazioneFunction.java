/*
 * Created on 19/07/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
/**
 * Funzione per inizializzare le sezioni delle documentazione di gara,
 * oltre a prelevare le informazioni dall'entità DOCUMGARE, vengono
 * prelevati i dati anche dall'entità collegata W_DOCDIG
 *
 * @author Marcello Caminiti
 */
public class GestioneDocumentazioneFunction extends AbstractFunzioneTag {

  public static final String    ELENCO_CAMPI_DOCUMGARA  = "CODGAR, NGARA, NORDDOCG, GRUPPO, FASGAR, IDPRG, IDDOCDG, STATODOC, REQCAP, TIPODOC, CONTESTOVAL, VALENZA, "
      + "OBBLIGATORIO, DESCRIZIONE, DITTAAGG, BUSTA, FASELE, MODFIRMA, URLDOC, IDSTAMPA, GENTEL, ALLMAIL, DATARILASCIO";

  public static final String    SELECT_W_DOCDIC = "select DIGDESDOC,DIGNOMDOC,DIGFIRMA from W_DOCDIG where IDPRG = ? and IDDOCDIG = ?";


  public GestioneDocumentazioneFunction() {
    super(5, new Class[] { PageContext.class,String.class,String.class,Long.class, String.class });
  }

  SqlManager sqlManager= null;

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String codGara = (String) params[1];
    String nGara = (String) params[2];
    Long tipoDocumentazione = (Long) params[3];
    String gestioneBustalotti = (String)params[4];
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String abilitataRichiestaFirma = ConfigManager.getValore(CostantiAppalti.PROP_RICHIESTA_FIRMA);

    try {
      String select="select " +  ELENCO_CAMPI_DOCUMGARA + " from DOCUMGARA where CODGAR=? and NGARA = ? and GRUPPO = ? and (ISARCHI is null or ISARCHI<>'1') order by numord, norddocg";
      if (nGara== null)
        select="select " +  ELENCO_CAMPI_DOCUMGARA + " from DOCUMGARA where CODGAR=? and NGARA is null and GRUPPO = ? and (ISARCHI is null or ISARCHI<>'1') order by numord, norddocg";

      if(tipoDocumentazione.longValue()==1){
        List listaDocumentiGara = null;

        if (nGara!= null)
          listaDocumentiGara = sqlManager.getListVector(
              select, new Object[] { codGara,nGara , new Long(1)});
        else{
          listaDocumentiGara = sqlManager.getListVector(
              select, new Object[] { codGara, new Long(1)});
        }
        if (listaDocumentiGara != null && listaDocumentiGara.size() > 0){

          //Per ogni occorrenza di DOCUMGARA si deve ricercare la relativa occorrenza in W_DOCDIG
          List listaDatiFile = this.popolaListaAllegati(listaDocumentiGara,pageContext,abilitataRichiestaFirma);

          pageContext.setAttribute("documentiGara", listaDocumentiGara,
              PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("documentiGaraDescFile", listaDatiFile,
              PageContext.REQUEST_SCOPE);
        }
      }else if(tipoDocumentazione.longValue()==2){
        List requisitiConcorrenti=null;

        if (nGara!= null)
          requisitiConcorrenti = sqlManager.getListVector(
              select, new Object[] { codGara,nGara , new Long(2)});
        else{
          requisitiConcorrenti = sqlManager.getListVector(
              select, new Object[] { codGara, new Long(2)});
        }
        if (requisitiConcorrenti != null && requisitiConcorrenti.size() > 0){
          List listaDatiFileConcorrenti = this.popolaListaAllegati(requisitiConcorrenti,pageContext,abilitataRichiestaFirma);
          pageContext.setAttribute("requisitiConcorrenti", requisitiConcorrenti,
              PageContext.REQUEST_SCOPE);

          pageContext.setAttribute("requisitiConcorrentiDescFile", listaDatiFileConcorrenti,
              PageContext.REQUEST_SCOPE);
        }
      }else if(tipoDocumentazione.longValue()==3 || tipoDocumentazione.longValue()==7){
        //con valore 7 si indica la documentazione richiesta ai concorrenti della fase invito, che richiede
        //l'ulteriore filtro che busta<>4
        List documentazioneConcorrenti = null;
        //select="select " +  ELENCO_CAMPI_DOCUMGARA + ", TAB1NORD from DOCUMGARA left join TAB1 on TAB1COD=? and TAB1TIP=BUSTA where CODGAR=? and NGARA = ? and GRUPPO = ? and (ISARCHI is null or ISARCHI<>'1') order by TAB1NORD,FASELE,NORDDOCG";
        select="select " +  ELENCO_CAMPI_DOCUMGARA + ", TAB1NORD from DOCUMGARA left join TAB1 on TAB1COD=? and TAB1TIP=BUSTA where CODGAR=? and NGARA = ? and GRUPPO = ? ";
        if(tipoDocumentazione.longValue()==7)
          select+=" and (BUSTA is null or BUSTA<>4) ";
        select+=" and (ISARCHI is null or ISARCHI<>'1') order by NUMORD,TAB1NORD,FASELE,NORDDOCG";
        if (nGara== null){
          //select="select " +  ELENCO_CAMPI_DOCUMGARA + ", TAB1NORD from DOCUMGARA left join TAB1 on TAB1COD=? and TAB1TIP=BUSTA where CODGAR=? and NGARA is null and GRUPPO = ? and (ISARCHI is null or ISARCHI<>'1') order by TAB1NORD,FASELE,NORDDOCG";
          if("1".equals(gestioneBustalotti))
            select="select " +  ELENCO_CAMPI_DOCUMGARA + ", TAB1NORD from DOCUMGARA left join TAB1 on TAB1COD=? and TAB1TIP=BUSTA where CODGAR=? and GRUPPO = ? ";
          else
            select="select " +  ELENCO_CAMPI_DOCUMGARA + ", TAB1NORD from DOCUMGARA left join TAB1 on TAB1COD=? and TAB1TIP=BUSTA where CODGAR=? and NGARA is null and GRUPPO = ? ";
          if(tipoDocumentazione.longValue()==7)
            select+=" and (BUSTA is null or BUSTA<>4) ";
          select+=" and (ISARCHI is null or ISARCHI<>'1') order by NUMORD,TAB1NORD,FASELE,NORDDOCG";
        }
        if (nGara!= null)
          documentazioneConcorrenti = sqlManager.getListVector(
              select, new Object[] { "A1013",codGara,nGara , new Long(3)});
        else{
          documentazioneConcorrenti = sqlManager.getListVector(
              select, new Object[] { "A1013", codGara, new Long(3)});
        }

        if (documentazioneConcorrenti != null && documentazioneConcorrenti.size() > 0){
          List listaDatiFileConcorrenti = this.popolaListaAllegati(documentazioneConcorrenti,pageContext,abilitataRichiestaFirma);

          pageContext.setAttribute("documentazioneConcorrenti", documentazioneConcorrenti,
              PageContext.REQUEST_SCOPE);

          pageContext.setAttribute("documentazioneConcorrentiDescFile", listaDatiFileConcorrenti,
              PageContext.REQUEST_SCOPE);
        }
      }else if(tipoDocumentazione.longValue()==4){
        List documentiEsito=null;

        if (nGara!= null)
          documentiEsito = sqlManager.getListVector(
              select, new Object[] { codGara,nGara , new Long(4)});
        else{
          documentiEsito = sqlManager.getListVector(
              select, new Object[] { codGara, new Long(4)});
        }
        if (documentiEsito != null && documentiEsito.size() > 0){
          List listaDatiFileConcorrenti = this.popolaListaAllegati(documentiEsito,pageContext,abilitataRichiestaFirma);

          pageContext.setAttribute("documentiEsito", documentiEsito,
              PageContext.REQUEST_SCOPE);

          pageContext.setAttribute("documentiEsitoDescFile", listaDatiFileConcorrenti,
              PageContext.REQUEST_SCOPE);
        }
      }else if(tipoDocumentazione.longValue()==5){
        //Documenti per la trasparenza
        List documentiTrasparenza = null;
        if (nGara!= null)
          documentiTrasparenza = sqlManager.getListVector(
              select, new Object[] { codGara,nGara , new Long(5)});
        else{
          documentiTrasparenza = sqlManager.getListVector(
              select, new Object[] { codGara, new Long(5)});
        }
        if (documentiTrasparenza != null && documentiTrasparenza.size() > 0){
          List listaDatiFileTrasparenza = new ArrayList(
              documentiTrasparenza.size() * 2);

          List listaRagioneSocialeDitta = new ArrayList(
              documentiTrasparenza.size()) ;

          int numDocAttesaFirma=0;
          for (int i = 0; i < documentiTrasparenza.size(); i++) {
            List listaTmp = sqlManager.getListVector(SELECT_W_DOCDIC,
                new Object[] {SqlManager.getValueFromVectorParam(documentiTrasparenza.get(i),5).getStringValue(),
                SqlManager.getValueFromVectorParam(documentiTrasparenza.get(i),6).longValue()});
            if(listaTmp!=null && listaTmp.size()>0){
              listaDatiFileTrasparenza.add(SqlManager.getValueFromVectorParam(listaTmp.get(0),0).getStringValue());
              listaDatiFileTrasparenza.add(SqlManager.getValueFromVectorParam(listaTmp.get(0),1).getStringValue());
              listaDatiFileTrasparenza.add(SqlManager.getValueFromVectorParam(listaTmp.get(0),2).getStringValue());
              if("1".equals(SqlManager.getValueFromVectorParam(listaTmp.get(0),2).getStringValue()) && "1".equals(abilitataRichiestaFirma))
                numDocAttesaFirma++;
            }else{
              listaDatiFileTrasparenza.add(null);
              listaDatiFileTrasparenza.add(null);
              listaDatiFileTrasparenza.add(null);
            }
            String ragioneSociale = (String)sqlManager.getObject("select nomimp from impr where codimp = ?",
                new Object[] {SqlManager.getValueFromVectorParam(documentiTrasparenza.get(i),14).getStringValue()});
            listaRagioneSocialeDitta.add(ragioneSociale);
          }

          pageContext.setAttribute("numDocAttesaFirma", new Long(numDocAttesaFirma));

          pageContext.setAttribute("documentiTrasparenza", documentiTrasparenza,
              PageContext.REQUEST_SCOPE);

          pageContext.setAttribute("documentiTrasparenzaDescFile", listaDatiFileTrasparenza,
              PageContext.REQUEST_SCOPE);

          pageContext.setAttribute("documentiTrasparenzaRagSoc", listaRagioneSocialeDitta,
              PageContext.REQUEST_SCOPE);
        }
      }else if(tipoDocumentazione.longValue()==6 || tipoDocumentazione.longValue()==12){
        //Documenti dell'invito
        List documentiInvito=null;
        String formatoAllegati = ConfigManager.getValore(CostantiAppalti.FORMATO_ALLEGATI);

        if (nGara!= null)
          documentiInvito = sqlManager.getListVector(
              select, new Object[] { codGara,nGara , tipoDocumentazione});
        else{
          documentiInvito = sqlManager.getListVector(
              select, new Object[] { codGara, new Long(6)});
        }
        if (documentiInvito != null && documentiInvito.size() > 0){
          List listaDatiFileConcorrenti = this.popolaListaAllegati(documentiInvito,pageContext,abilitataRichiestaFirma);

          pageContext.setAttribute("documentiInvito", documentiInvito,
              PageContext.REQUEST_SCOPE);

          pageContext.setAttribute("documentiInvitoDescFile", listaDatiFileConcorrenti,
              PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("formatoAllegati", formatoAllegati,
              PageContext.REQUEST_SCOPE);
        }
      }else if(tipoDocumentazione.longValue()==10){
        List attiDocumenti=null;

        if (nGara!= null)
          attiDocumenti = sqlManager.getListVector(
              select, new Object[] { codGara,nGara , new Long(10)});
        else{
          attiDocumenti = sqlManager.getListVector(
              select, new Object[] { codGara, new Long(10)});
        }
        if (attiDocumenti != null && attiDocumenti.size() > 0){
          List listaDatiFileAtti = this.popolaListaAllegati(attiDocumenti,pageContext,abilitataRichiestaFirma);

          pageContext.setAttribute("atti", attiDocumenti,
              PageContext.REQUEST_SCOPE);

          pageContext.setAttribute("attiDescFile", listaDatiFileAtti,
              PageContext.REQUEST_SCOPE);
        }
      }else if(tipoDocumentazione.longValue()==11){
        //Allegati all'ordine
        List allegatiOrdine=null;

        allegatiOrdine = sqlManager.getListVector(
              select, new Object[] { codGara,nGara , new Long(11)});

        if (allegatiOrdine != null && allegatiOrdine.size() > 0){
          List listaDatiFileAllegati = this.popolaListaAllegati(allegatiOrdine, pageContext,abilitataRichiestaFirma);

          pageContext.setAttribute("allegatiOrdine", allegatiOrdine,
              PageContext.REQUEST_SCOPE);

          pageContext.setAttribute("allegatiOrdineDescFile", listaDatiFileAllegati,
              PageContext.REQUEST_SCOPE);
        }
      }



    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre le documentazioni  "
          + "della gara "
          + nGara, e);
    }catch (GestoreException e) {
      throw new JspException("Errore nell'estrarre le documentazioni "
          + "della gara "
          + nGara, e);
    }

    return null;
  }

  private List popolaListaAllegati(List allegati, PageContext pageContext, String abilitataRichiestaFirma) throws SQLException, GestoreException{
    List listaDatiFileAllegati = new ArrayList(
        allegati.size() * 3);
    int numDocAttesaFirma=0;

    for (int i = 0; i < allegati.size(); i++) {
      List listaTmp = sqlManager.getListVector(SELECT_W_DOCDIC,
          new Object[] {SqlManager.getValueFromVectorParam(allegati.get(i),5).getStringValue(),
          SqlManager.getValueFromVectorParam(allegati.get(i),6).longValue()});
      if(listaTmp!=null && listaTmp.size()>0){
        listaDatiFileAllegati.add(SqlManager.getValueFromVectorParam(listaTmp.get(0),0).getStringValue());
        listaDatiFileAllegati.add(SqlManager.getValueFromVectorParam(listaTmp.get(0),1).getStringValue());
        listaDatiFileAllegati.add(SqlManager.getValueFromVectorParam(listaTmp.get(0),2).getStringValue());
        if("1".equals(SqlManager.getValueFromVectorParam(listaTmp.get(0),2).getStringValue()) && "1".equals(abilitataRichiestaFirma))
          numDocAttesaFirma++;
      }else{
        listaDatiFileAllegati.add(null);
        listaDatiFileAllegati.add(null);
        listaDatiFileAllegati.add(null);
      }
    }
    pageContext.setAttribute("numDocAttesaFirma", new Long(numDocAttesaFirma),
        PageContext.REQUEST_SCOPE);

    return listaDatiFileAllegati;

  }

}
