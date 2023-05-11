package it.eldasoft.sil.pg.tags.gestori.plugin;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

public class GestoreQform extends AbstractGestorePreload {

  SqlManager sqlManager = null;

  public GestoreQform(BodyTagSupportGene tag) {
    super(tag);
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {

  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);

    String modoQform = UtilityTags.getParametro(page,"modoQform");
    String busta = UtilityTags.getParametro(page,"busta");
    String fasEle = UtilityTags.getParametro(page,"fasEle");

    if (!"INSQFORM".equals(modoQform) && !"INSQFORM_RETT".equals(modoQform)) {

      HashMap<?,?> key = UtilityTags.stringParamsToHashMap(
          (String) page.getAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
              PageContext.REQUEST_SCOPE), null);
      String ngara = ((JdbcParametro) key.get("GARE.NGARA")).getStringValue();
      String codmodello = null;
      String titolo = null;
      Long idRettifica=null;
      boolean garaPub=false;
      try {
        Vector<?> dati = null;
        String dbDultaggString = sqlManager.getDBFunction("DATETIMETOSTRING",
            new String[] { "l.dultagg" });
        String dbdultaggmodString = sqlManager.getDBFunction("DATETIMETOSTRING",
            new String[] { "q.dultaggmod" });
        //String select = "select l.codmodello,l.titolo,q.datpub," + dbDultaggString + ", " + dbdultaggmodString + ", q.stato from qformlib l, qform q where q.entita='GARE' and q.key1=? and q.busta=? and q.idmodello=l.id ";
        String select = "select l.codmodello,l.titolo,q.datpub," + dbDultaggString + ", " + dbdultaggmodString + ", q.stato from qformlib l, qform q where q.entita='GARE' and q.key1=?";
        if(busta!=null && !"".equals(busta))
          select += " and q.busta=" +  busta;
        select += " and q.idmodello=l.id ";
        if ("VISQFORM_RETT".equals(modoQform)) {
          select+=" and q.stato=7";
        }else {
          select+=" and q.stato!=7 and q.stato!=8";

        }
        dati= this.sqlManager.getVector(select, new Object[] {ngara});
        if(dati!=null && dati.size()>0) {
         codmodello = SqlManager.getValueFromVectorParam(dati, 0).getStringValue();
         titolo = SqlManager.getValueFromVectorParam(dati, 1).getStringValue();
         Timestamp data = SqlManager.getValueFromVectorParam(dati, 2).dataValue();
         if(data!=null)
           garaPub=true;
         String dultaggStringValue = SqlManager.getValueFromVectorParam(dati, 3).getStringValue();
         String dultaggmodStringValue = SqlManager.getValueFromVectorParam(dati, 4).getStringValue();
         if(dultaggStringValue!=null && !"".equals(dultaggStringValue) && dultaggmodStringValue!=null && !"".equals(dultaggmodStringValue)) {
           Date dultagg = UtilityDate.convertiData(dultaggStringValue, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
           Date dultaggmod = UtilityDate.convertiData(dultaggmodStringValue, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
           //Timestamp dultagg = Timestamp.valueOf(dultaggStringValue);
           //Timestamp dultaggmod = Timestamp.valueOf(dultaggmodStringValue);
           if(dultagg.after(dultaggmod)){
             page.setAttribute("initDultagg", dultaggStringValue, PageContext.REQUEST_SCOPE);
           }
         }
         if("VISQFORM".equals(modoQform)) {
           Long stato = SqlManager.getValueFromVectorParam(dati, 5).longValue();
           if(new Long(5).equals(stato) && "4".equals(busta)) {
             Long tippub = (Long)this.sqlManager.getObject("select count(codgar9) from pubbli, gare where ngara=? and codgar1=codgar9 and tippub=13", new Object[] {ngara});
             if(tippub!=null && tippub.longValue()>0)
               page.setAttribute("bloccoCreaRettifica", new Boolean(true), PageContext.REQUEST_SCOPE);
           }
         }
       }

       if(!"VISQFORM_RETT".equals(modoQform)) {
         //Verifica se esiste la rettifica
         if(busta!=null && !"".equals(busta))
           idRettifica = (Long)this.sqlManager.getObject("select id from qform where entita='GARE' and key1=? and busta=? and stato=?", new Object[] {ngara, new Long(busta), new Long(7)});
         else
           idRettifica = (Long)this.sqlManager.getObject("select id from qform where entita='GARE' and key1=? and stato=?", new Object[] {ngara,  new Long(7)});
       }

       if(!"VISQFORM".equals(modoQform)) {

       }
      } catch (Exception e) {
        throw new JspException("Errore nell'inizializzazione dei dati del QFORM",e);
      }
      page.setAttribute("initCodiceModello", codmodello, PageContext.REQUEST_SCOPE);
      page.setAttribute("initTitoloModello", titolo, PageContext.REQUEST_SCOPE);

      page.setAttribute("garaPub", new Boolean(garaPub), PageContext.REQUEST_SCOPE);
      page.setAttribute("idRettifica", idRettifica, PageContext.REQUEST_SCOPE);

    }else {
      if ("INSQFORM_RETT".equals(modoQform)) {
        HashMap<?,?> key = UtilityTags.stringParamsToHashMap(
            (String) page.getAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
                PageContext.REQUEST_SCOPE), null);
        String ngara = ((JdbcParametro) key.get("GARE.NGARA")).getStringValue();
        String codmodello = null;
        String titolo = null;
        String dultaggmodString = null;
        try {
          String dbdultaggmodString = sqlManager.getDBFunction("DATETIMETOSTRING",
              new String[] { "q.dultaggmod" });
          String select="";
          Vector<?> dati = null;
          if(busta!=null && !"".equals(busta)) {
            select = "select l.codmodello,l.titolo, " + dbdultaggmodString + " from qformlib l, qform q where q.entita='GARE' and q.key1=? and q.busta=? and q.idmodello=l.id and q.stato!=7 and q.stato!=8";
            dati= this.sqlManager.getVector(select, new Object[] {ngara, new Long(busta)});
          }else {
            select = "select l.codmodello,l.titolo, " + dbdultaggmodString + " from qformlib l, qform q where q.entita='GARE' and q.key1=? and q.idmodello=l.id and q.stato!=7 and q.stato!=8";
            dati= this.sqlManager.getVector(select, new Object[] {ngara});
          }
          if(dati!=null && dati.size()>0) {
            codmodello = SqlManager.getValueFromVectorParam(dati, 0).getStringValue();
            titolo = SqlManager.getValueFromVectorParam(dati, 1).getStringValue();
            dultaggmodString = SqlManager.getValueFromVectorParam(dati, 2).getStringValue();
          }

        } catch (Exception e) {
          throw new JspException("Errore nell'inizializzazione dei dati del QFORM",e);
        }
        page.setAttribute("initCodiceModello", codmodello, PageContext.REQUEST_SCOPE);
        page.setAttribute("initTitoloModello", titolo, PageContext.REQUEST_SCOPE);
        page.setAttribute("initDultaggmod", dultaggmodString, PageContext.REQUEST_SCOPE);
      }
    }
  }
}
