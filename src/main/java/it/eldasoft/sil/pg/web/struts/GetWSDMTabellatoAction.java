package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.TabellatoWsdm;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.conf.WSDMTabellatoElementoType;
import it.maggioli.eldasoft.ws.conf.WSDMTabellatoType;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetWSDMTabellatoAction extends Action {
  
  private TabellatiManager tabellatiManager;

  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }
  
  private GestioneWSDMManager gestioneWSDMManager;

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }
  
  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONArray jsonArray = new JSONArray();

    String codice = request.getParameter("codice");
    String sistema = request.getParameter("sistema");
    
    String servizio = request.getParameter("servizio");
    
    String tabellatiInDB = request.getParameter("tabellatiInDB");
    
    Long idconfi = null;
    String idconfiString = request.getParameter("idconfi");
    if(idconfiString!=null && !"".equals(idconfiString)){
      idconfi = Long.parseLong(idconfiString);
    }
    String genereGara = request.getParameter("genereGara");
  
    if("TRUE".equals(tabellatiInDB)){
      List <TabellatoWsdm> tabellati = this.tabellatiManager.getTabellatiWsdm(idconfi, sistema, codice);
      if (tabellati != null && tabellati.size() > 0) {
        for (int e = 0; e < tabellati.size(); e++) {
          Object[] row = new Object[4];
          row[0] = tabellati.get(e).getValore();
          row[1] = tabellati.get(e).getDescri();
          if(genereGara==null || "".equals(genereGara)){
            jsonArray.add(row);
          }else{
            if(row[1]!=null){
              if(("10".equals(genereGara) || "20".equals(genereGara)) && ((String)row[1]).startsWith("ALBO"))
                jsonArray.add(row);
              else if("11".equals(genereGara) && ((String)row[1]).startsWith("AVVISO"))
                jsonArray.add(row);
              else if(((String)row[1]).startsWith("GARE"))
                jsonArray.add(row);
            }
          }
        }
      }
    }else{
      
      WSDMConfigurazioneOutType configurazione = gestioneWSDMManager.wsdmConfigurazioneLeggi(servizio,idconfiString);
  
      if (configurazione.isEsito()) {
        if (configurazione.getTabellati() != null) {
          WSDMTabellatoType[] wsdmTabellati = configurazione.getTabellati();
          if (wsdmTabellati != null && wsdmTabellati.length > 0) {
            for (int t = 0; t < wsdmTabellati.length; t++) {
              if (codice.equals(wsdmTabellati[t].getNome())) {
                WSDMTabellatoElementoType[] elementi = wsdmTabellati[t].getElementi();
                if (elementi != null && elementi.length > 0) {
                  for (int e = 0; e < elementi.length; e++) {
                    Object[] row = new Object[4];
                    row[0] = elementi[e].getCodice();
                    row[1] = elementi[e].getDescrizione();
                    if(genereGara==null || "".equals(genereGara)){
                      jsonArray.add(row);
                    }else{
                      if(row[1]!=null){
                        if(("10".equals(genereGara) || "20".equals(genereGara)) && ((String)row[1]).startsWith("ALBO"))
                          jsonArray.add(row);
                        else if("11".equals(genereGara) && ((String)row[1]).startsWith("AVVISO"))
                          jsonArray.add(row);
                        else if(((String)row[1]).startsWith("GARE"))
                          jsonArray.add(row);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    out.println(jsonArray);
    out.flush();

    return null;

  }

}
