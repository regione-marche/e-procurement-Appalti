package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetWsfascicoloByGaraAction extends Action {

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();
    String entita = "GARE";
    String key1 = request.getParameter("key1");
    //Il parametro può contenere CODGAR nel caso di gare ad offerta unica o offerte distinte, ngara nel caso di lotti di gara
    //e tutti gli altri casi rimanenti

    boolean esito = true;
    String messaggio = null;
    JSONArray jsonArray = new JSONArray();

    //si deve ricavare la chiave da adoperare in wsfasciolo:
    //key1= CODGAR nel caso di gara ad offerta unica o offerte distinte, NGARA negli altri casi
    //entita= TORN nel caso di offerte distinte, GARE in tutti gli altri casi

    String selectDatiGara = "select codgar, genere from v_gare_genere where codice=?";
    Object par[] = new Object[] {key1};
    List<?> datiGara = sqlManager.getVector(selectDatiGara, par);
    if (datiGara != null && datiGara.size() > 0) {
      String codgar = (String) SqlManager.getValueFromVectorParam(datiGara, 0).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(datiGara, 1).getValue();
      if(new Long(100).equals(genere) || new Long(300).equals(genere))
        key1 = codgar;

      if(new Long(100).equals(genere) || new Long(1).equals(genere))
        entita = "TORN";

      String selectWSFASCICOLO = "select codice,classifica,anno,numero,codaoo,coduff,struttura,desclassi,desvoce,desaoo,desuff from wsfascicolo where entita = ? and key1 = ? ";
      par = new Object[] {entita, key1};

      List<?> datiWSFASCICOLO = sqlManager.getVector(selectWSFASCICOLO, par);

      if (datiWSFASCICOLO != null && datiWSFASCICOLO.size() > 0) {
        String codice = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 0).getValue();
        String classifica = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 1).getValue();
        Long anno = (Long) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 2).getValue();
        String numero = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 3).getValue();
        String codaoo = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 4).getValue();
        String coduff = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 5).getValue();
        String struttura = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 6).getValue();
        String descrizione = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 7).getValue();
        String voce = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 8).getValue();
        String desaoo = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 9).getValue();
        String desuff = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 10).getValue();

        if(codice==null || "".equals(codice)) {
          esito = false;
          messaggio = "Non è stato associato il fascicolo alla gara";
        }else if(classifica==null || "".equals(classifica)){
          esito = false;
          messaggio = "Non è valorizzata la classifica del fascicolo";
        }else {

          jsonArray.add(new Object[] { codice, classifica, anno, numero, codaoo, coduff, struttura,descrizione,voce,desaoo,desuff });
        }
      }else {
        esito = false;
        messaggio = "Non è stato associato il fascicolo alla gara";
      }
    }else {
      esito = false;
      messaggio = "Non è stato possibile leggere i dati dalla banca dati";
    }

    result.put("esito", esito);
    result.put("messaggio", messaggio);
    result.put("fascicolo", jsonArray);

    out.print(result);
    out.flush();

    return null;

  }

}
