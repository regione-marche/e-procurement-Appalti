/*
 * Created on 11-03-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che legge il valore di una property specificata come parametro di ingresso
 *
 * @author Marcello Caminiti
 */
public class ControllaDatiExportDitteFunction extends AbstractFunzioneTag {

  /**
   * Costruttore
   */
  public ControllaDatiExportDitteFunction() {
    super(1, new Class[] { String.class });
  }


  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String codgar = ((String) params[0]);

    String esito = "ok";
    String message = "";
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
        "sqlManager", pageContext, SqlManager.class);
    

    boolean erroreCF = false;
    boolean erroreMandataria = false;
    
    String ditteErroreMandataria = "";
    String ditteErroreCF = "";
    List listaOperatori;
    
    try {
      listaOperatori = sqlManager.getListVector("select dittao, nomimo from ditg where codgar5 = ? order by NPROGG", new Object[]{codgar});
      
      if(listaOperatori != null && listaOperatori.size()>0){
        for (int i = 0; i < listaOperatori.size(); i++) {
          String dittao = SqlManager.getValueFromVectorParam(listaOperatori.get(i), 0).getStringValue();
          String nomimo = SqlManager.getValueFromVectorParam(listaOperatori.get(i), 1).getStringValue();
          Vector<JdbcParametro> impr = sqlManager.getVector("select tipimp, cfimp from impr where codimp = ?", new Object[]{dittao});
          Long tipimp = (Long) SqlManager.getValueFromVectorParam(impr, 0).getValue();
          if(tipimp != null && (tipimp.intValue()==3 || tipimp.intValue()==10)){
            List listaComponenti =  sqlManager.getListVector("select coddic from ragimp where codime9 = ?", new Object[]{dittao});
            if(listaComponenti != null && listaComponenti.size()>0){
              for (int j = 0; j < listaComponenti.size(); j++) {
                String coddic = SqlManager.getValueFromVectorParam(listaComponenti.get(j), 0).getStringValue();
                if(coddic != null && !"".equals(coddic)){
                  Vector<JdbcParametro> vector = sqlManager.getVector("select cfimp,nomimp from impr where codimp = ?", new Object[]{coddic});
                  String cfimp = (String) SqlManager.getValueFromVectorParam(vector, 0).getValue();
                  String nomimp = (String) SqlManager.getValueFromVectorParam(vector, 1).getValue();
                  if(cfimp == null || "".equals(cfimp)){
                    erroreCF = true;
                    ditteErroreCF = componiMessaggio(ditteErroreCF,coddic + " - "+nomimp);
                  }
                }
              }
              Long impman = (Long) sqlManager.getObject("select count(*) from ragimp where codime9 = ? and impman = 1", new Object[]{dittao});
              if(impman == null || impman.intValue()==0){
                erroreMandataria=true;
                ditteErroreMandataria = componiMessaggio(ditteErroreMandataria,dittao + " - "+ nomimo);
              }
            }else{
              erroreMandataria=true;
              ditteErroreMandataria = componiMessaggio(ditteErroreMandataria, dittao+ " - "+ nomimo);
            }
          }else{
            String cfimp = (String) SqlManager.getValueFromVectorParam(impr, 1).getValue();
            if(cfimp==null || "".equals(cfimp)){
              erroreCF = true;
              ditteErroreCF = componiMessaggio(ditteErroreCF,dittao + " - "+ nomimo);
            }
          }
        }
      }else{
        esito = "error";
        message="<span>Non ci sono ditte in gara.</span>";
      }
      
    } catch (SQLException e) {
      esito = "error";
      message = e.getMessage();
    }
    
    if(erroreCF || erroreMandataria){
      esito = "error";
      if(erroreCF){
        message+="<br><span>Le seguenti ditte non hanno il codice fiscale valorizzato:</span>";
        message+="<ul>"+ditteErroreCF+"</ul><br>";
      }
      if(erroreMandataria){
        message+="<br><span>I seguenti raggruppamenti non hanno specificato la mandataria:</span>";
        message+="<ul>"+ditteErroreMandataria+"</ul><br>";
      }
    }
 
  pageContext.setAttribute("esitocontrollo", esito, PageContext.REQUEST_SCOPE);
  pageContext.setAttribute("message", message, PageContext.REQUEST_SCOPE);
  
  return null;
  }
  
  private String componiMessaggio(String messaggio, String campo){
    
    messaggio+="<li>"+campo+"</li>";
    return messaggio;
  }
}