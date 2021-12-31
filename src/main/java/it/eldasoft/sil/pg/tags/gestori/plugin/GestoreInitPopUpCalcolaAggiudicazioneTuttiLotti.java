/*
 * Created on 22/03/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore richiamato dalla pagina gare-popup-calcoloAggiudicazione-tuttiLotti.jsp
 *
 * @author Marcello Caminiti
 */
public class GestoreInitPopUpCalcolaAggiudicazioneTuttiLotti extends AbstractGestorePreload {

  public GestoreInitPopUpCalcolaAggiudicazioneTuttiLotti(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {


    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", page,SqlManager.class);


    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", page, TabellatiManager.class);

    // lettura dei parametri di input
    String ngara = page.getRequest().getParameter("ngara");

    Long conteggio;
    try {
      conteggio = (Long)sqlManager.getObject("select count(ngara) from gare where codgar1=? and ngara!=codgar1 and modlicg!=6 and calcsoang!='2'", new Object[]{ngara});
      if (conteggio!=null && conteggio.longValue()>=1) {
        page.setAttribute("precutVisibile","true", PageContext.REQUEST_SCOPE);

        String descr = tabellatiManager.getDescrTabellato("A1018", "1");
        page.setAttribute("initPrecut",descr, PageContext.REQUEST_SCOPE);
      }

      //Si controlla se vi sono dei lotti con importo a base di gara nullo
      List listaLotti = sqlManager.getListVector("select ngara from gare where codgar1=? and ngara!=codgar1 and impapp is null order by ngara",  new Object[]{ngara});
      if(listaLotti!=null && listaLotti.size()>0){
        String lotto = null;
        StringBuffer msg = new StringBuffer("<b>ATTENZIONE:</b> I seguenti lotti non hanno l'importo a base di gara valorizzato:<br><ul>");

        for (int i = 0; i < listaLotti.size(); i++) {
          lotto = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).getStringValue();
          msg.append("<li>").append(lotto).append("<br>");
        }
        msg.append("</ul>");
        page.setAttribute("importiNulli",msg, PageContext.REQUEST_SCOPE);
      }

      String isGaraDLGS2016Manuale = (String)page.getAttribute("isGaraDLGS2016Manuale");//page.getRequest().getParameter("isGaraDLGS2016Manuale");
      if("1".equals(isGaraDLGS2016Manuale)){
        //Si deve controllare se vi sono lotti che non hanno valorizzatto metsoglia
        Long numLottiSenzaMetodo=(Long)sqlManager.getObject("select count(g.ngara) from gare g, gare1 g1 where g.ngara = g1.ngara and g.codgar1=? "
            + "and g.ngara!=g.codgar1 and (g.modlicg = 13 or g.modlicg = 14) and g.dittap is null and metsoglia is null", new Object[]{ngara});
        if(numLottiSenzaMetodo!=null && numLottiSenzaMetodo.longValue()>0){
          List listaDatiGare1 = sqlManager.getListVector("select metsoglia, metcoeff from gare g, gare1 g1 where g.ngara = g1.ngara and g.codgar1=? "
              + "and g.ngara!=g.codgar1 and (g.modlicg = 13 or g.modlicg = 14) and g.dittap is null and metsoglia is not null", new Object[]{ngara});
          if(listaDatiGare1!=null && listaDatiGare1.size()>0){
            Long metsoglia = SqlManager.getValueFromVectorParam(listaDatiGare1.get(0), 0).longValue();
            Double metcoeff = SqlManager.getValueFromVectorParam(listaDatiGare1.get(0), 1).doubleValue();
            Long metsogliaTmp = null;
            Double metcoeffTmp = null;
            if(metsoglia!=null){
              if(listaDatiGare1.size() ==1){
                page.setAttribute("initMetsoglia",metsoglia, PageContext.REQUEST_SCOPE);
                page.setAttribute("initMetcoeff",metcoeff, PageContext.REQUEST_SCOPE);
              }else{
                boolean valoriTuttiUgualiMetsoglia = true;
                boolean valoriTuttiUgualiMetcoeff = true;
                for (int i = 1; i < listaDatiGare1.size(); i++) {
                  metsogliaTmp = SqlManager.getValueFromVectorParam(listaDatiGare1.get(i), 0).longValue();
                  metcoeffTmp = SqlManager.getValueFromVectorParam(listaDatiGare1.get(i), 1).doubleValue();
                  if(!metsoglia.equals(metsogliaTmp)){
                    valoriTuttiUgualiMetsoglia=false;
                    break;
                  }
                  if(metsoglia.equals(metsogliaTmp) && metsoglia.longValue()==5 && !metcoeff.equals(metcoeffTmp)){
                    valoriTuttiUgualiMetcoeff=false;
                    break;
                  }
                }
                if(valoriTuttiUgualiMetsoglia){
                  page.setAttribute("initMetsoglia",metsoglia, PageContext.REQUEST_SCOPE);
                  if(metsoglia.longValue()==5 && valoriTuttiUgualiMetcoeff)
                    page.setAttribute("initMetcoeff",metcoeff, PageContext.REQUEST_SCOPE);
                }
              }
            }
          }
        }
        page.setAttribute("numLottiSenzaMetodo",numLottiSenzaMetodo, PageContext.REQUEST_SCOPE);
      }
      //Si verifica se esiste almeno un lotto OEPV per cui è prevista la riparametrazione su almeno una delle buste tecnica ed economica e calcsoang='1'
      conteggio = (Long)sqlManager.getObject("select count(g.ngara) from gare g, gare1 g1 where g.ngara=g1.ngara and g.codgar1=? and g.ngara!=g.codgar1 "
          + "and g.modlicg=6 and g.calcsoang ='1' and (g1.riptec=1 or g1.riptec=2 or g1.ripeco=1 or g1.ripeco=2)", new Object[]{ngara});
      if(conteggio!=null && conteggio.longValue()>0)
        page.setAttribute("esistonoLottiOEPVRiparam","si", PageContext.REQUEST_SCOPE);

      //Verifica dell'esistenza di criteri economici di tipo non prezzo
      String abilitataGestionePrezzo = (String)page.getAttribute("abilitataGestionePrezzo");
      if("1".equals(abilitataGestionePrezzo)){
        conteggio = (Long)sqlManager.getObject("select count(goev.ngara) from goev,gare where goev.ngara=gare.ngara and codgar1=? and gare.ngara!=codgar1 "
            + "and tippar=2 and livpar in (1,3) and ISNOPRZ = '1'", new Object[]{ngara});
        if(conteggio==null)
          conteggio=new Long(0);
         page.setAttribute("numCriteriEcoNoPrezzo",conteggio, PageContext.REQUEST_SCOPE);

      }
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura dei dati dei lotti ", e);
    }catch (GestoreException e) {
      throw new JspException("Errore durante la lettura dei dati dei lotti ", e);
    }

  }

}