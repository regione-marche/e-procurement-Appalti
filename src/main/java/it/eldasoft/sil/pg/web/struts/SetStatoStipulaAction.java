package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.tags.funzioni.GestioneLottiStipulaFunction;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONObject;

public class SetStatoStipulaAction extends Action {

  /**
   * Manager per la gestione delle interrogazioni di database.
   */
  private SqlManager sqlManager;

  private GenChiaviManager genChiaviManager;


  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  /**
   * @param sqlManager
   *        the sqlManager to set
   */
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

    String id = request.getParameter("id");
    String opz = request.getParameter("opz");
    String motneg = request.getParameter("motneg");

    Long statoStipula = null;
    String codStipula = null;
    Date datpubStipula = null;


    int livEvento = 1;
    String codEvento="";
    String descEvento="";
    String msgErr="";

    String ngara=null;
    String codgar=null;
    Long tipgarg=null;
    boolean bloccoTipgar=false;

    TransactionStatus status = null;
    boolean commitTransaction = false;
    JSONObject result = new JSONObject();

    if (id != null && opz !=null) {

      try {
        String selectStipula = "select codstipula,datpub,g1.ngara, g.codgar1, tipgarg from g1stipula g1, gare g where id = ? and g1.ngara=g.ngara";
        Vector<?> datiStipula = sqlManager.getVector(selectStipula,new Object[] { id });
        if (datiStipula != null && datiStipula.size() > 0){
          codStipula = SqlManager.getValueFromVectorParam(datiStipula, 0).stringValue();
          datpubStipula = (Date) SqlManager.getValueFromVectorParam(datiStipula, 1).getValue();
          ngara = SqlManager.getValueFromVectorParam(datiStipula, 2).stringValue();
          codgar = SqlManager.getValueFromVectorParam(datiStipula, 3).stringValue();
          tipgarg = SqlManager.getValueFromVectorParam(datiStipula, 4).longValue();
        }

        String update="update G1STIPULA set STATO =? where ID=?";
        boolean annullaStipuleDerivate=false;
        String selectLottiAgg="select GARE.DITTA, GARE.IAGGIU, GARE.TATTOA, GARE.DATTOA, GARE.NATTOA, GARE.NGARA from GARE ";

        Object par[]=null;

        if("1".equals(opz)) {
          codEvento="GA_STIPULA_ATTIVA";
          descEvento = "Attivazione contratto";
          statoStipula = Long.valueOf(5);
          par = new Object[] {statoStipula, Long.valueOf(id)};
        }else if("2".equals(opz)) {
          codEvento="GA_STIPULA_DISATTIVA";
          descEvento = "Disattivazione contratto";
          if(datpubStipula!=null) {
              statoStipula = Long.valueOf(3);
          }else {
              statoStipula = Long.valueOf(1);
          }
          par = new Object[] {statoStipula, Long.valueOf(id)};
        }else if("3".equals(opz)) {
          annullaStipuleDerivate=true;
          codEvento="GA_STIPULA_ANNULLA";
          descEvento = "Annullamento contratto";
          statoStipula = Long.valueOf(6);
          Long genere = new Long(1);
          if(codgar != null && ngara!= null && !ngara.equals(codgar) && '$' != codgar.charAt(0))
            genere=new Long(2);
          else if(codgar != null && ngara!= null && ngara.equals(codgar))
            genere=new Long(3);
          if (genere.longValue()==2 && tipgarg >=51 && tipgarg <=89)
            bloccoTipgar=true;

          Long ncont = (Long)this.sqlManager.getObject("select ncont from v_gare_stipula where id = ?", new Object[] {new Long(id)});

          Long tiatto=null;
          Timestamp daatto=null;
          String nrepat=null;
          String ridiso=null;
          Double impgar=null;
          String nquiet=null;
          Timestamp dquiet=null;
          Date dscapo=(Date)this.sqlManager.getObject("select dscapo from garecont where ngara=? and ncont=?", new Object[] {ngara,ncont});
          String istcre=null;
          String indist=null;
          String selectLotti = "select GARE.TIATTO, GARE.DAATTO, GARE.NREPAT, GARE.RIDISO, GARE.IMPGAR, GARE.NQUIET, GARE.DQUIET, GARE.ISTCRE, GARE.INDIST from GARE ";

          String whereLottiAggiudicati ="GARECONT.NGARA = '" + ngara + "' and GARECONT.NCONT = "+ ncont + " AND (GENERE IS NULL OR GENERE<>3)";

          if(genere.longValue()!=2 || bloccoTipgar) {
            selectLotti += GestioneLottiStipulaFunction.JOIN_CONTRATTO + whereLottiAggiudicati;
          }else {
            selectLotti += " WHERE GARE.NGARA='"+ ngara + "'";
          }
          selectLottiAgg += GestioneLottiStipulaFunction.JOIN_CONTRATTO + whereLottiAggiudicati;
          Vector<?> datiLotti = this.sqlManager.getVector(selectLotti, null);
          if (datiLotti != null && datiLotti.size() > 0){
            tiatto = SqlManager.getValueFromVectorParam(datiLotti, 0).longValue();
            daatto = SqlManager.getValueFromVectorParam(datiLotti, 1).dataValue();
            nrepat = SqlManager.getValueFromVectorParam(datiLotti, 2).stringValue();
            ridiso = SqlManager.getValueFromVectorParam(datiLotti, 3).stringValue();
            impgar = SqlManager.getValueFromVectorParam(datiLotti, 4).doubleValue();
            nquiet = SqlManager.getValueFromVectorParam(datiLotti, 5).stringValue();
            dquiet = SqlManager.getValueFromVectorParam(datiLotti, 6).dataValue();
            istcre = SqlManager.getValueFromVectorParam(datiLotti, 7).stringValue();
            indist=  SqlManager.getValueFromVectorParam(datiLotti, 8).stringValue();
          }

          Date oggi = UtilityDate.getDataOdiernaAsDate();

          update="update G1STIPULA set NCONT = ?, DATNEG = ?, MOTNEG =?, STATO =?, TIATTO = ?, DAATTO = ?, NREPAT = ?, RIDISO = ?, IMPGAR = ?, NQUIET = ?, DQUIET = ?, DSCAPO = ?, ISTCRE = ?, INDIST = ?   where ID=?";
          par = new Object[] {null, new Timestamp(oggi.getTime()), motneg, statoStipula, tiatto, daatto, nrepat, ridiso, impgar, nquiet, dquiet, dscapo, istcre, indist, Long.valueOf(id)};

        }



        status = this.sqlManager.startTransaction();
        this.sqlManager.update(update, par);
        if(annullaStipuleDerivate){
          Date oggi = UtilityDate.getDataOdiernaAsDate();
          this.sqlManager.update("update G1STIPULA set NCONT=null, DATNEG = ?, MOTNEG =?, STATO=6 where ID_ORIGINARIO=?", new Object[]{ new Timestamp(oggi.getTime()), motneg, Long.valueOf(id)});
        }
        if("3".equals(opz)) {
          String insert="insert into G1AGGISTO (ID, IDSTIPULA, NGARA, CODGAR,  DITTA, IAGGIU, TATTOA, DATTOA, NATTOA) values(?,?,?,?,?,?,?,?,?)";
          List listaLotti = this.sqlManager.getListVector(selectLottiAgg, null);
          if(listaLotti!=null && listaLotti.size()>0) {
            int idG1aggisto=0;
            String ditta=null;
            Double iaggiu=null;
            Long tattoa=null;
            Timestamp dattoa=null;
            String nattoa=null;
            String nLotto=null;
            for(int j=0; j<listaLotti.size();j++) {
              ditta = SqlManager.getValueFromVectorParam(listaLotti.get(j),0).stringValue();
              iaggiu = SqlManager.getValueFromVectorParam(listaLotti.get(j),1).doubleValue();
              tattoa = SqlManager.getValueFromVectorParam(listaLotti.get(j),2).longValue();
              dattoa = SqlManager.getValueFromVectorParam(listaLotti.get(j),3).dataValue();
              nattoa = SqlManager.getValueFromVectorParam(listaLotti.get(j),4).stringValue();
              nLotto = SqlManager.getValueFromVectorParam(listaLotti.get(j),5).stringValue();
              idG1aggisto=this.genChiaviManager.getNextId("G1AGGISTO");
              par = new Object[] {new Long(idG1aggisto), Long.valueOf(id), nLotto, codgar, ditta, iaggiu, tattoa, dattoa, nattoa};
              this.sqlManager.update(insert, par);
            }
          }

        }
        commitTransaction = true;
      } catch (Exception e) {
        livEvento = 3;
        commitTransaction = false;
        msgErr = e.getMessage();
        throw e;
      } finally {
        if (status != null) {
          if (commitTransaction) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        }
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(codStipula);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descEvento);
        logEvento.setErrmsg(msgErr);
        LogEventiUtils.insertLogEventi(logEvento);
      }

      result.put("esito", Boolean.valueOf(commitTransaction));
      result.put("messaggio", msgErr);
      out.println(result);
      out.flush();
    }

    return null;

  }

}
