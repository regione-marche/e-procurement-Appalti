/*
 * Created on 06/03/20
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.decoratori;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

/**
 * Gestore del campo fittizzio numero offerte della pagina delle fasi di iscrizione,
 * per tale campo si deve calcolare il numero totale di offerte presentate dall'operatore
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoDittaAggiudicatariaGara extends AbstractGestoreCampo {

  private SqlManager sqlManager = null;

   @Override
  public String gestisciDaTrova(Vector params, DataColumn colWithValue,
            String conf, SqlManager manager) {
        return null;
    }

    @Override
  public String getClasseEdit() {
        return null;
    }

    @Override
  public String getClasseVisua() {
        return null;
    }



    @Override
  public String getValore(String valore) {
        return null;
    }

    @Override
  public String getValorePerVisualizzazione(String valore) {

      sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
                this.getPageContext(), SqlManager.class);

        HashMap datiRiga = (HashMap) this.getPageContext().getAttribute("datiRiga",
                PageContext.REQUEST_SCOPE);

        String ngara = datiRiga.get("DITG_NGARA5").toString();
        String codgar = datiRiga.get("GARE_CODGAR1").toString();
        String dittao = datiRiga.get("DITG_DITTAO").toString();
        String genere = datiRiga.get("GARE_GENERE").toString();

        String valoreCampo = "No";


        try {
          if("3".equals(genere)){
            String selectLotti = "select ngara from gare where codgar1 = ? and codgar1 != ngara";
            List<?> lotti = sqlManager.getListVector(selectLotti, new Object[]{codgar});
            if(lotti!=null && lotti.size()>0){
              for(int i=0;i<lotti.size() && "No".equals(valoreCampo);i++){
                String codiceLotto = SqlManager.getValueFromVectorParam(lotti.get(i), 0).stringValue();
                valoreCampo = this.controlloRT(codiceLotto, dittao);
              }
            }
          }else{
            valoreCampo = this.controlloRT(ngara, dittao);
          }
        } catch (SQLException e) {
        } catch (GestoreException e) {
        }
        return valoreCampo;
    }


  private String isAggiudicatariaLotto(String dittao, String ngara) throws SQLException{

    String valoreCampo = "No";
    Long count = (Long) sqlManager.getObject("select count(*) from ditgaq where dittao = ? and ngara = ?", new Object[]{dittao,ngara});
    if(count.intValue()>0){
      valoreCampo = "Sì";
    }else{
      count = (Long) sqlManager.getObject("select count(*) from gare where ditta = ? and ngara = ?", new Object[]{dittao,ngara});
      if(count.intValue()>0){
        valoreCampo = "Sì";
      }
    }

    return valoreCampo;

  }

  private String controlloRT(String ngara, String dittao) throws SQLException{
    String valoreCampo = "No";

    List<?> ditteDaInvitoRT = sqlManager.getListVector("select dittao from ditg where ngara5=? and dittainv=?", new Object[]{ngara,dittao});
    if(ditteDaInvitoRT!=null && ditteDaInvitoRT.size()>0){
      String ditta = null;
      String dittaAggiud=null;
      for(int i=0; i< ditteDaInvitoRT.size();i++){
        ditta = SqlManager.getValueFromVectorParam(ditteDaInvitoRT.get(i), 0).getStringValue();
        dittaAggiud = isAggiudicatariaLotto(ditta,ngara);
        if("Sì".equals(dittaAggiud)){
          valoreCampo = dittaAggiud;
          break;
        }
      }
    }else{
      valoreCampo = isAggiudicatariaLotto(dittao,ngara);
    }
    return valoreCampo;
  }

    @Override
  public String getValorePreUpdateDB(String valore) {
        return null;
    }

    @Override
  protected void initGestore() {

    }

    @Override
  public String postHTML(boolean visualizzazione, boolean abilitato) {
        return null;
    }

    @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {
        return null;
    }


  @Override
  public String getHTML(boolean visualizzazione, boolean abilitato) {

    return null;
  }

}