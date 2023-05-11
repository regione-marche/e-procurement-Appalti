/*
 * Created on 18-08-2015
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che determina la visibilità o meno del campo discriminante la
 * applicazione della legge regionale per la Sicilia
 *
 * @author Cristian Febas
 */
public class InitLeggeRegioneSiciliaFunction extends AbstractFunzioneTag {

  public InitLeggeRegioneSiciliaFunction() {
    super(3, new Class[] { PageContext.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    // Codice della gara
    String ngara = (String) params[1];

    //Nel caso la funzione sia lanciata dalla pagina di aggiudicazione per tutti i lotti, non si deve
    //andare a fare l'inizializzazione del campo LEGRECSIC prendendo il valore fra quelli dei lotti aggiudicati
    String aggiudicazioneTuttiLotti = (String) params[2];

    String result = "0";
    String initLegRegSic = "";
    Boolean inizializzazoneEffettuata = false;

      try {
        String codgar = (String)sqlManager.getObject("select codgar1 from gare where ngara=?", new Object[]{ngara});
        if(!"Si".equals(aggiudicazioneTuttiLotti)){
          Long genere= (Long)sqlManager.getObject("select genere from gare where ngara=?", new Object[]{codgar});
          if(genere!=null && genere.longValue()==3){
            //Nel caso di genere=3, se ci sono già dei lotti aggiudicati, si inizializza
            //con il valore del primo lotto aggiudicato in lista(lotti ordinati per ngara)
            Long numLottiAgg = (Long)sqlManager.getObject("select count(ngara) from gare where codgar1=? and ngara!=codgar1 and ditta is not null", new Object[]{codgar});
            if(numLottiAgg!=null && numLottiAgg.longValue()>0){
              initLegRegSic= (String)sqlManager.getObject("select g1.legregsic from gare g, gare1 g1 where g.codgar1=? and g.ngara!=g.codgar1 "
                  + "and g.ditta is not null and g.ngara = g1.ngara order by g.ngara", new Object[]{codgar});
              inizializzazoneEffettuata = true;
            }
          }
        }else{
         String lotto = (String)sqlManager.getObject("select ngara from gare where codgar1=? and ngara!=codgar1 and modlicg!=6 and calcsoang!='2' order by ngara", new Object[]{ngara});
         if(lotto==null ){
           result = "-1";
           return result;
         }else{
           ngara = lotto;
         }
        }

        if(!inizializzazoneEffettuata){
          Date dataInizioApplicazione = null;
          Date dataFineApplicazione = null;
          Double impSogliaGareLavori = null;
          Double impSogliaGareFornitureServizi = null;

          List listTabA1116 = sqlManager.getListVector("select TAB1TIP, TAB1DESC from TAB1 WHERE TAB1COD = 'A1116'" +
                  " order by TAB1TIP", new Object[] {});

          if (listTabA1116 != null && listTabA1116.size() > 0) {
            for (int i = 0; i < listTabA1116.size(); i++) {
              Long tipTab =  SqlManager.getValueFromVectorParam(listTabA1116.get(i), 0).longValue();
              String descTab = SqlManager.getValueFromVectorParam(listTabA1116.get(i), 1).toString();
              descTab = descTab.substring(0, descTab.indexOf(' '));

              if (descTab.length() > 0) {
                if (new Long(1).equals(tipTab)){
                  if ("0".equals(descTab)){
                    result = "-1";
                    return result;
                  }
                }
                if (new Long(2).equals(tipTab)){
                  impSogliaGareLavori = new Double(UtilityNumeri.convertiDouble(
                      descTab.replace(",", "."), UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE).doubleValue());
                }
                if (new Long(3).equals(tipTab)){
                  impSogliaGareFornitureServizi = new Double(UtilityNumeri.convertiDouble(
                      descTab.replace(",", "."), UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE).doubleValue());
                }
                if (new Long(4).equals(tipTab)){
                  dataInizioApplicazione = UtilityDate.convertiData(descTab, UtilityDate.FORMATO_GG_MM_AAAA);
                }
                if (new Long(5).equals(tipTab)){
                  dataFineApplicazione = UtilityDate.convertiData(descTab, UtilityDate.FORMATO_GG_MM_AAAA);
                }

              }else{
                ;//gestisci eccezione
              }
            }//for
          }

          //Recupera i dati della gara/tornata
          Double impapp = null;
          Long tipgen = null;
          Long iterga = null;
          Date dinvit = null;
          Date dpubav = null;
          Long modlicg = null;

          String selectDatiGara = "select g.IMPAPP, t.TIPGEN, t.ITERGA, t.DINVIT, t.DPUBAV, g.modlicg" +
                  " from GARE g, TORN t" +
                  " where t.CODGAR = g.CODGAR1 and g.NGARA = ? ";
          Vector datiGara = sqlManager.getVector(selectDatiGara, new Object[] {ngara});
          if (datiGara!= null && datiGara.size()>0){
            if(((JdbcParametro)datiGara.get(0)).getValue()!=null){
              impapp = (Double)((JdbcParametro)datiGara.get(0)).getValue();
            }
            if(((JdbcParametro)datiGara.get(1)).getValue()!=null){
              tipgen = (Long)((JdbcParametro)datiGara.get(1)).getValue();
            }
            if(((JdbcParametro)datiGara.get(2)).getValue()!=null){
              iterga = (Long)((JdbcParametro)datiGara.get(2)).getValue();
            }

            if(((JdbcParametro)datiGara.get(3)).getValue()!=null){
              dinvit = (Date)((JdbcParametro)datiGara.get(3)).getValue();
            }
            if(((JdbcParametro)datiGara.get(4)).getValue()!=null){
              dpubav = (Date)((JdbcParametro)datiGara.get(4)).getValue();
            }
            if(((JdbcParametro)datiGara.get(5)).getValue()!=null){
              modlicg = (Long)((JdbcParametro)datiGara.get(5)).getValue();
            }
          }

          Vector datiGareLotti = sqlManager.getVector("select genere, importo from v_gare_torn where codgar=?", new Object[]{codgar});
          if(datiGareLotti!=null && datiGareLotti.size()>0){
            Long genere = SqlManager.getValueFromVectorParam(datiGareLotti, 0).longValue();
            if(genere.longValue()==1 || genere.longValue()==3){
              impapp = SqlManager.getValueFromVectorParam(datiGareLotti, 1).doubleValue();
            }
          }

          if(impapp == null){
            impapp = new Double(0);
          }
          if(tipgen == null){
            tipgen = new Long(1);
          }

          Date datpub = (Date)sqlManager.getObject("select datpub from pubbli where codgar9=? and tippub=?", new Object[]{codgar, new Long(11)});
          Date dataControllo = null;


          boolean flagImporto = false;
          boolean flagPeriodo = false;
          if (new Long(13).equals(modlicg) || new Long(14).equals(modlicg)){
            //Controllo sugli importi
            if(new Long(1).equals(tipgen) && impapp < impSogliaGareLavori ){
              flagImporto = true;
            }else{
              if((new Long(2).equals(tipgen) || new Long(3).equals(tipgen) )&& impapp < impSogliaGareFornitureServizi){
                flagImporto = true;
              }
            }

            //Controllo sulle date
            if (iterga == 3 || iterga == 4 || iterga == 5 || iterga == 6)
              dataControllo = dinvit;
            else{
              if(datpub!=null)
                dataControllo = datpub;
              else
                dataControllo = dpubav;
            }
            if(dataControllo==null)
              dataControllo = UtilityDate.getDataOdiernaAsDate();

            if( (dataInizioApplicazione.before(dataControllo) || dataControllo.equals(dataInizioApplicazione))
                && (dataFineApplicazione.after(dataControllo) || dataControllo.equals(dataFineApplicazione))){
              flagPeriodo = true;
            }
          }



          if(flagImporto && flagPeriodo){
            initLegRegSic = "1" ;
          }else{
            initLegRegSic = "2" ;
          }
        }

        pageContext.setAttribute("initLegRegSic", initLegRegSic, PageContext.REQUEST_SCOPE);

      } catch (SQLException e) {
        throw new JspException(
            "Errore durante la lettura dei dati della gara per la Legge Regionale Sicilia", e);
      } catch (GestoreException e) {
        throw new JspException(
            "Errore in fase di determinazione dei parametri della gara per la Legge Regionale Sicilia", e);
      }


    return result;
  }

}
