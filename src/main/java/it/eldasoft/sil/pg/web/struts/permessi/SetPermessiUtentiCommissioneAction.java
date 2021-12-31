package it.eldasoft.sil.pg.web.struts.permessi;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.properties.ConfigManager;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

public class SetPermessiUtentiCommissioneAction extends Action {

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, 
		  final HttpServletRequest request, final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    String operation = request.getParameter("operation");
    String codgar = request.getParameter("codgar");
    Long syscon = new Long(request.getParameter("syscon"));
    String gartel =request.getParameter("gartel");

    TransactionStatus status = null;
    boolean commitTransaction = false;
    
    String codice = "";
    String descEvento = "";
    String codEvento = "";
    String errMsg = "";
    int livello = 1;
    status = this.sqlManager.startTransaction();
    try {
      String utente = (String)sqlManager.getObject("select sysute from usrsys where syscon = ? ", new Object[] { syscon });
      Vector<?> datiGenere = this.sqlManager.getVector("select codice, genere from v_gare_genere where codgar = ? ", new Object[] { codgar });
      codice = (String)((JdbcParametro) datiGenere.get(0)).getValue();
      Long genere = (Long)((JdbcParametro) datiGenere.get(1)).getValue();
      if(genere!=null && (genere.intValue()==1 || genere.intValue()==3)){
        codice = codgar;
      }
      codEvento = "GA_ASSEGNA_PERMESSI";
      
      if ("DELETE".equals(operation)) {
        String deleteG_PERMESSI = "delete from g_permessi where codgar = ? and syscon = ?";
        this.sqlManager.update(deleteG_PERMESSI, new Object[] { codgar, syscon });
        descEvento+= "Rimossi i privilegi per l'utente '"+ utente +"'("+syscon+")";
      } else if ("INSERTUPDATE".equals(operation)) {

        Long autori = new Long(request.getParameter("autori"));
        String propri = request.getParameter("propri");
        Long ruolo = null;
        try{
          ruolo = new Long(request.getParameter("ruolo"));
        }catch(Exception e){
        }
        
        String selectG_PERMESSI = "select count(*) from g_permessi where syscon = ? and codgar = ?";
        Long cnt = (Long) this.sqlManager.getObject(selectG_PERMESSI, new Object[] { syscon, codgar });

        if (cnt != null && cnt.longValue() > 0) {
          String updateG_PERMESSI = "update g_permessi set autori = ?, propri = ?, meruolo = ? where syscon = ? and codgar = ?";
          this.sqlManager.update(updateG_PERMESSI, new Object[] { autori, propri, ruolo, syscon, codgar });
          descEvento+= "Modificati i privilegi dell'utente '"+ utente +"'("+syscon+"): ";
        } else {
          String insertG_PERMESSI = "insert into g_permessi (numper, syscon, autori, propri, codgar, meruolo) values (?,?,?,?,?,?)";
          Object[] obj = new Object[6];
          obj[0] = _getNextNumper();
          obj[1] = syscon;
          obj[2] = autori;
          obj[3] = propri;
          obj[4] = codgar;
          if("1".equals(gartel)){
            obj[5] = new Long(2);
          }else{
            obj[5] = null;
          }
          this.sqlManager.update(insertG_PERMESSI, obj);
          descEvento+= "Assegnati i privilegi per l'utente '"+ utente +"'("+syscon+"): ";
          
          String uffintAbilitata = ConfigManager.getValore("it.eldasoft.associazioneUffintAbilitata");
          if("1".equals(uffintAbilitata)){
            String cenint = (String) this.sqlManager.getObject("select cenint from torn where torn.codgar = ?", new Object[] { codgar });
            if(cenint != null && !"".equals(cenint)){
              String countUSR_EIN = "select count(*) from usr_ein where syscon = ? and codein = ?";
              Long cntUsrein = (Long) this.sqlManager.getObject(countUSR_EIN, new Object[] { syscon, cenint });
              if(cntUsrein.intValue()<=0){
                String insertUSR_EIN = "insert into usr_ein (syscon, codein) values (?,?)";
                Object[] objUSR_EIN = new Object[2];
                objUSR_EIN[0] = syscon;
                objUSR_EIN[1] = cenint;
                this.sqlManager.update(insertUSR_EIN, objUSR_EIN);
              }
            }
          }
        }

        descEvento+= "lettura = SI";
        if(autori!= null && autori.intValue() == 1){
          descEvento+= ", scrittura = SI";
        }else{
          descEvento+= ", scrittura = NO";
        }
        
        if("1".equals(propri)){
          descEvento+= ", controllo completo = SI";
        }else{
          descEvento+= ", controllo completo = NO";
        }
        
        if(ruolo!= null && ruolo.intValue() == 2){
          descEvento+= ", ruolo = Punto istruttore";
        }else if(ruolo!= null && ruolo.intValue() == 1){
          descEvento+= ", ruolo = Punto ordinante";
        }
        
      }

      this.sqlManager.commitTransaction(status);
      
      livello = 1;
      commitTransaction = true;
    } catch (Exception e) {
      livello = 3;
      commitTransaction = false;
      errMsg = e.getMessage();
    } finally {
      if (status != null) {
        if (commitTransaction) {
          this.sqlManager.commitTransaction(status);
        } else {
          this.sqlManager.rollbackTransaction(status);
        }
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(livello);
        logEvento.setOggEvento(codice);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descEvento );
        logEvento.setErrmsg(errMsg);
        LogEventiUtils.insertLogEventi(logEvento);
      }
    }
    return null;
  }

  /**
   * 
   * @return
   * @throws SQLException
   */
  private Long _getNextNumper() throws SQLException {
    Long nextNumper = (Long) this.sqlManager.getObject("select max(numper) from g_permessi", new Object[] {});
    if (nextNumper == null) nextNumper = new Long(0);
    nextNumper = new Long(nextNumper.longValue() + 1);
    return nextNumper;
  }

}
