/*
 * Created on 06/04/2022
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class GetFaseleDocumentiOperatoriJsonFunction extends AbstractFunzioneTag {

  public GetFaseleDocumentiOperatoriJsonFunction() {
    super(4, new Class[] { PageContext.class,String.class,String.class,String.class });
    // TODO Auto-generated constructor stub
  }

  static Integer     ISCRIZIONE = 1;
  static Integer     RINNOVO = 2;

  static String      TITOLOISCRIZIONE = "Fase di iscrizione e aggiornamento";
  static String      TITOLORINNOVO = "Fase di rinnovo";

  private String sqlCount = "select count(*) from DOCUMGARA D where D.CODGAR=? and (D.FASELE=? or D.FASELE=?)";
  private String sqlCountPubb = "select count(*) from DOCUMGARA D where D.CODGAR=? and (D.FASELE=? or D.FASELE=?) and D.statodoc = 5 and D.ISARCHI is null";
  private String sqlCountAttesaPubb = "select count(*) from DOCUMGARA D where D.CODGAR=? and (D.FASELE=? or D.FASELE=?) and D.statodoc is null and D.ISARCHI is null";
  private String sqlCountArchi = "select count(*) from DOCUMGARA D where D.CODGAR=? and (D.FASELE=? or D.FASELE=?) and D.ISARCHI is NOT null";
  private String sqlCountQform = "select count(*) from QFORM where ENTITA='GARE' and KEY1=?";
  private String sqlCountQformStato = "select count(*) from QFORM where ENTITA='GARE' and KEY1=? and STATO=?";


  SqlManager sqlManager;

  /**
   * Parametri da passare alla funzione:
   * pagecontext
   * codice gara
   * numero gara
   * variabile questionari"
   */
  @Override
  public String function(PageContext pageContext, Object[] params)
  throws JspException {

    String codiceGara = (String) params[1];
    String ngara = (String) params[2];
    String gestioneQuestionariIscrizione = (String) params[3];

    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);
    JSONArray json =  new JSONArray();
    String nomeNodo = "";
    Integer val1Fasele = 0;
    Integer val2Fasele = 0;

    try {
          for (int i = 1; i < 3; i++) {

            if(i == ISCRIZIONE){
              nomeNodo = TITOLOISCRIZIONE;
              val1Fasele = 1;
              val2Fasele = 2;
            }
            if(i == RINNOVO){
              nomeNodo = TITOLORINNOVO;
              val1Fasele = 2;
              val2Fasele = 3;
            }

            boolean gestioneQform = false;

            if(i == ISCRIZIONE && PgManagerEst1.QFORM_VISUALIZZAZIONE.equals(gestioneQuestionariIscrizione))
              gestioneQform=true;

            JSONObject jsonItem = this.composeJson(codiceGara, ngara,i,val1Fasele,val2Fasele, nomeNodo,gestioneQform,pageContext);
            //Aggiungo tipologia documento
            json.add(jsonItem);
          }


    } catch (Exception ex) {
        ;
    }

  String result = json.toString();
  result = result.replaceAll("\"CLASS\"", "\"class\"");
  return result;

  }

  private JSONObject composeJson(String codiceGara, String ngara, int indice,Integer val1Fasele,Integer val2Fasele,String nome,boolean gestioneQform,PageContext pageContext) throws SQLException{
    JSONObject jsonItem =  new JSONObject();
    JSONObject jsonItemLi =  new JSONObject();
    Long nrPubblicazioniTotali = new Long(0);
    jsonItem.put("id", val1Fasele.toString() + val2Fasele.toString());
    jsonItem.put("parent", "#");
    if(gestioneQform ) {
      nrPubblicazioniTotali = (Long)sqlManager.getObject(sqlCountQform, new Object[] {ngara});
    }else {
      nrPubblicazioniTotali = (Long)sqlManager.getObject(sqlCount, new Object[] {codiceGara, val1Fasele, val2Fasele});
    }
    Long numeroPubb = null;
    Long numeroAttesaPubb = null;
    Long numeroArchiviati = null;
    Long numeroRettificati = null;
    String contextPath = ((HttpServletRequest) pageContext.getRequest()).getContextPath();

    if (nrPubblicazioniTotali > 0) {
      if(gestioneQform) {
        numeroPubb = (Long)sqlManager.getObject(sqlCountQformStato, new Object[] {ngara, new Long(5)});
        numeroAttesaPubb = (Long)sqlManager.getObject(sqlCountQformStato, new Object[] {ngara, new Long(1)});
        numeroArchiviati = (Long)sqlManager.getObject(sqlCountQformStato, new Object[] {ngara, new Long(8)});
        numeroRettificati = (Long)sqlManager.getObject(sqlCountQformStato, new Object[] {ngara, new Long(7)});
      }else {
        numeroPubb = (Long)sqlManager.getObject(sqlCountPubb, new Object[] {codiceGara, val1Fasele, val2Fasele});
        numeroAttesaPubb = (Long)sqlManager.getObject(sqlCountAttesaPubb, new Object[] {codiceGara, val1Fasele, val2Fasele});
        numeroArchiviati = (Long)sqlManager.getObject(sqlCountArchi, new Object[] {codiceGara, val1Fasele, val2Fasele});
      }
      String riepilogoPubb = "Da pubblicare: " + numeroAttesaPubb + ", pubblicati: " + numeroPubb;
      if(!new Long(0).equals(numeroArchiviati)){
        riepilogoPubb = riepilogoPubb + ", archiviati: "+ numeroArchiviati;
      }
      if(gestioneQform) {
        if(!new Long(0).equals(numeroRettificati)){
          if(!new Long(0).equals(numeroPubb))
            riepilogoPubb = "Q-form pubblicato, " ;
          riepilogoPubb += "Rettifica in corso" ;
        } else if(!new Long(0).equals(numeroAttesaPubb))
          riepilogoPubb = "Q-form da pubblicare" ;
        else if(!new Long(0).equals(numeroPubb))
          riepilogoPubb = "Q-form pubblicato" ;

        if(!new Long(0).equals(numeroArchiviati))
          riepilogoPubb += ", archiviati: "+ numeroArchiviati;

      }

      String icon = "";
      if(numeroPubb > 0){icon = "<img src=\""+ contextPath +"/img/documento_pubblicato.png\" title=\""+riepilogoPubb+"\" style=\" float:right;\" width=\"16\" height=\"16\" />";}
      jsonItem.put("text", "<b>" + nome + "</b>" + icon +  "<br><i>" + riepilogoPubb + "</i><span style=\"display:none;\">" + ngara + "</span>");
    } else {
      jsonItem.put("text", nome + "<span style=\"display:none;\">" + ngara + "</span>");
    }
    JSONObject jsonItemA =  new JSONObject();
    jsonItemA.put("href", "javascript:visualizzaDocumentiOperatori('" + ngara + "'," + val1Fasele +"," + val2Fasele+ ",'"+ nome + "')");
    jsonItem.put("a_attr", jsonItemA);

    //
    if (indice % 2 == 0) {
        jsonItemLi.put("CLASS", "even");
    } else {
        jsonItemLi.put("CLASS", "odd");
    }
    jsonItem.put("li_attr", jsonItemLi);
    return jsonItem;
  }

}
