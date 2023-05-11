/*
 * Created on 19/12/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per l'import degli affidamenti da dati esterni
 *
 * @author Marcello.Caminiti
 */
public class GestorePopupImportaAffidamentiEsterni extends AbstractGestoreEntita {

  private static final String insertGara ="insert into gare(ngara, codgar1, codcig, tipgarg, not_gar, impapp, ditta, nomima, iaggiu, dattoa," +
    " fasgar, stepgar, modastg, estimp, sicinc, temesi, ribcal, precut, pgarof, garoff, idiaut, calcsoang, onsogrib, ribagg) " +
    " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

  private static final String insertTorn = "insert into torn(codgar, cenint, tipgen, offaum, compreq, istaut, iterga) values(?,?,?,?,?,?,?)";

  private static final String insertGarecont = "insert into garecont(ngara, ncont, dverbc, dcertu, impliq, codimp) values(?,?,?,?,?,?)";

  private static final String insertGare1 = "insert into gare1(ngara, codgar1) values(?,?)";

  private static final String updateGarecont="update garecont set dverbc=?, dcertu=?, impliq=? where ngara=? and ncont=?";
  private static final String updateAppa="update appa set dconsd=?, dult=?, itotaleliqui=? where codlav=? and nappal=?";

  private TabellatiManager tabellatiManager;

  @Override
  public String getEntita() {
    return "TORN";
  }

  public GestorePopupImportaAffidamentiEsterni() {
    super(false);
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    long numeroCigImportati=0;
    long numeroCigAggiornati=0;
    long numeroCigConErrori=0;

    String ufficioIntestatario=null;
    HttpSession session = this.getRequest().getSession();
    if ( session != null) {
      ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
    }

    //Si deve controllare che non vi siano gare con CIG nullo, con CIG <> 10 caratteri e che non
    //ci siano duplicazioni di CIG
    try {
      String select="select count(*) from v_gare_datiesterni where codcig is null or codcig=''";
      if(ufficioIntestatario!=null)
        select+=" and cenint='" + ufficioIntestatario + "'";
      Long conteggioCig = (Long)this.sqlManager.getObject(select, null);
      if(conteggioCig!=null && conteggioCig.longValue()>0){
        this.getRequest().setAttribute("esito", "ERRORE");
        throw new GestoreException(
            "Esistono delle gare con CIG nullo nella view v_gare_datiesterni",
            "datiEsterni.cigNulli", new Exception());
      }

      String dbFunctionLen = sqlManager.getDBFunction("LENGTH",
          new String[] { "CODCIG" });

      select="select count(codcig) from v_gare_datiesterni where " + dbFunctionLen + " <>10";
      if(ufficioIntestatario!=null)
        select+=" and cenint='" + ufficioIntestatario + "'";
      conteggioCig = (Long)this.sqlManager.getObject(select, null);
      if(conteggioCig!=null && conteggioCig.longValue()>0){
        this.getRequest().setAttribute("esito", "ERRORE");
        throw new GestoreException(
            "Esistono delle gare con CIG con lunghezza diversa da 10 caratteri nella view v_gare_datiesterni",
            "datiEsterni.cigErrati", new Exception());
      }

      select="select upper(codcig) from v_gare_datiesterni where codcig is not null group by upper(codcig) having(count(*)>1)";
      if(ufficioIntestatario!=null)
        select="select upper(codcig) from v_gare_datiesterni where cenint='" + ufficioIntestatario + "' and codcig is not null group by upper(codcig) having(count(*)>1)";

      List<Vector<JdbcParametro>> listaCigDuplicati = this.sqlManager.getListVector(
          select,null);

      if (listaCigDuplicati != null && listaCigDuplicati.size() > 0) {
        this.getRequest().setAttribute("esito", "ERRORE");
        throw new GestoreException(
            "Esistono delle gare con uguale CIG nella view v_gare_datiesterni",
            "datiEsterni.cigDuplicati", new Exception());
      }

    } catch (SQLException e) {
      this.getRequest().setAttribute("esito", "ERRORE");
      throw new GestoreException(
          "Errori nei controlli preliminari per l'import",null, e);
    }

    String select = "select codcig, prefcod, tipgen, cenint, oggetto, tipgarg, impapp, nomest, cfimp, cfest, nazimp, " +
    		"iaggiu, dattoa, dverbc, dcertu, impliq from v_gare_datiesterni";
    if(ufficioIntestatario!=null)
      select+=" where cenint='" + ufficioIntestatario + "'";
    select +=" order by codcig";

    StringBuffer messaggiErrore = new StringBuffer();
    try {
      List affidamentiEsterni = this.sqlManager.getListVector(select, null);
      if(affidamentiEsterni!=null && affidamentiEsterni.size()>0){
        String codcig=null;
        String cenint=null;
        String cfimp=null;
        String cfest=null;
        Long tipgarg=null;
        String codimp=null;
        String nomimp=null;
        String nomest=null;
        Long nazimp=null;

        Vector datiControlli = null;

        tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
            this.getServletContext(), TabellatiManager.class);

        String  cifreDecimaliRibasso = tabellatiManager.getDescrTabellato("A1028", "1");
        int cifreDecimali=0;
        if(cifreDecimaliRibasso!=null && !"".equals(cifreDecimaliRibasso)){
          cifreDecimali = (new Long(cifreDecimaliRibasso)).intValue();
        }

        String selectGare="select ngara,clavor,numera from gare where codcig=?";
        if(ufficioIntestatario!=null)
          selectGare="select ngara,clavor,numera from gare,torn where codcig=? and codgar1=codgar and cenint='" + ufficioIntestatario + "'";

        String insertImpr="Insert into impr(codimp, tipimp, nomest, nomimp, cfimp, nazimp) values(?,?,?,?,?,?)";

        for(int i=0;i<affidamentiEsterni.size();i++){
          codcig = SqlManager.getValueFromVectorParam(
              affidamentiEsterni.get(i), 0).getStringValue();
          //Tramite il cig si deve stabilire se inserire o aggiornare
          List listaNgara = this.sqlManager.getListVector(selectGare, new Object[]{codcig});
          if(listaNgara==null || (listaNgara!=null && listaNgara.size()==0)){
            //Inserimento
            cenint = SqlManager.getValueFromVectorParam(
                affidamentiEsterni.get(i), 3).getStringValue();
            if("".equals(cenint))
              cenint=null;
            cfimp = SqlManager.getValueFromVectorParam(
                affidamentiEsterni.get(i), 8).getStringValue();
            if("".equals(cfimp))
              cfimp=null;
            cfest = SqlManager.getValueFromVectorParam(
                affidamentiEsterni.get(i), 9).getStringValue();
            if("".equals(cfest))
              cfest=null;
            tipgarg = SqlManager.getValueFromVectorParam(
                affidamentiEsterni.get(i), 5).longValue();
            nomest = SqlManager.getValueFromVectorParam(
                affidamentiEsterni.get(i), 7).getStringValue();
            nazimp = SqlManager.getValueFromVectorParam(
                affidamentiEsterni.get(i), 10).longValue();
            datiControlli = this.controlliPreliminariInserimento(cenint, cfimp, cfest, tipgarg, codcig, messaggiErrore);
            if(((Boolean)datiControlli.get(0))==true){
              if(((Long)datiControlli.get(1)).longValue()==0){
                //Si deve inserire in IMPR
                codimp = geneManager.calcolaCodificaAutomatica("IMPR", "CODIMP");
                String nomestImpr = nomest;
                if(nomestImpr!= null && nomestImpr.length()>2000)
                  nomestImpr=nomestImpr.substring(0, 2000);
                String nomimpImpr = nomest;
                if(nomimpImpr!= null && nomimpImpr.length()>61)
                  nomimpImpr=nomimpImpr.substring(0, 61);
                nomimp=nomimpImpr;
                String cfimpImpr=cfimp;
                if(cfimpImpr==null )
                  cfimpImpr = cfest;
                this.sqlManager.update(insertImpr, new Object[]{codimp,new Long(1),nomestImpr,nomimpImpr,cfimpImpr,nazimp});
              }else{
                String codfisc = cfimp;
                if(codfisc==null)
                  codfisc = cfest;
                Vector datiImpr = this.sqlManager.getVector("select codimp,nomimp from impr where upper(cfimp)=?", new Object[]{codfisc});
                if(datiImpr!=null && datiImpr.size()>0){
                  codimp= SqlManager.getValueFromVectorParam(
                      datiImpr, 0).getStringValue();
                  nomimp= SqlManager.getValueFromVectorParam(
                      datiImpr, 1).getStringValue();
                }
              }
              //Inserimento in GARE
              this.inserimentoGara((Vector)affidamentiEsterni.get(i), codimp, nomimp, cenint, status, datiForm,cifreDecimali);
              numeroCigImportati++;
            }else{
              //Non sono stati superati i controlli preliminari.
              numeroCigConErrori++;
            }

          }else if(listaNgara!=null && listaNgara.size()==1){
            //Si deve controllare se la gara è collegata ad un lavoro
            String clavor= SqlManager.getValueFromVectorParam(
                listaNgara.get(0), 1).stringValue();
            Long numera= SqlManager.getValueFromVectorParam(
                listaNgara.get(0), 2).longValue();
            if(clavor==null || "".equals(clavor)){
              //Aggiornamento GARECONT
              Object parametri[] = new Object[5];

              //dverbc
              parametri[0] = SqlManager.getValueFromVectorParam(
                  affidamentiEsterni.get(i), 13).dataValue();
              //dcertu
              parametri[1] = SqlManager.getValueFromVectorParam(
                  affidamentiEsterni.get(i), 14).dataValue();
              //impliq
              parametri[2] = SqlManager.getValueFromVectorParam(
                  affidamentiEsterni.get(i), 15).doubleValue();
              parametri[3] = SqlManager.getValueFromVectorParam(
                  listaNgara.get(0), 0).stringValue();
              parametri[4] = new Long(1);

              this.sqlManager.update(updateGarecont, parametri);
              numeroCigAggiornati++;
            }else if (numera != null && numera!=0){
              //Aggiornamento APPA
              Object parametri[] = new Object[5];

              //dconsd
              parametri[0] = SqlManager.getValueFromVectorParam(
                  affidamentiEsterni.get(i), 13).dataValue();
              //dult
              parametri[1] = SqlManager.getValueFromVectorParam(
                  affidamentiEsterni.get(i), 14).dataValue();
              //itotaleliqui
              parametri[2] = SqlManager.getValueFromVectorParam(
                  affidamentiEsterni.get(i), 15).doubleValue();
              parametri[3] = clavor;
              parametri[4] = numera;

              this.sqlManager.update(updateAppa, parametri);
              numeroCigAggiornati++;
            }
          }else{
            //Ci sono più gare con lo stesso cig, errore
            messaggiErrore.append("\n\nCIG ").append(codcig).append(":\n");
            messaggiErrore.append("  sono presenti più gare con uguale codice CIG\n");
            numeroCigConErrori++;
          }
        }
      }
    } catch (SQLException e) {
      this.getRequest().setAttribute("esito", "ERRORE");
      throw new GestoreException(
          "Errori nell'import",null, e);
    }
    this.getRequest().setAttribute("esito", "OK");
    this.getRequest().setAttribute("numeroCigImportati", new Long(numeroCigImportati));
    this.getRequest().setAttribute("numeroCigAggiornati", new Long(numeroCigAggiornati));
    this.getRequest().setAttribute("numeroCigConErrori", new Long(numeroCigConErrori));
    this.getRequest().setAttribute("messaggiErrore", messaggiErrore.toString());
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }

  /**
   * Effettua i controlli preliminari per l'inserimento di una gara. Viene restituito
   * un Vector contenente in posizione 0 l'esito(true se superati, altrimenti false) e
   * in posizione 1 l'eventuale numero di imprese in anagrafica con codice fiscale uguale
   * a quello passato come parametro
   *
   * @param cenint
   * @param cfimp
   * @param cfest
   * @param tipgarg
   * @param cig
   * @param messaggi
   * @return Vector
   * @throws SQLException
   */
  private Vector controlliPreliminariInserimento(String cenint, String cfimp, String cfest, Long tipgarg, String cig, StringBuffer messaggi) throws SQLException {
    Vector ret = new Vector();
    boolean esito = true;
    boolean bloccoCodFisc=false;
    boolean bloccoStazApp=false;
    StringBuffer msgControlli = new StringBuffer();

    //String messaggio="Non è stata importata la gara con codice CIG = " + cig + " poichè ";
    if(cenint==null){
      msgControlli.append("  non è valorizzato il riferimento alla stazione appaltante (campo CENINT)\n");
      esito=false;
      bloccoStazApp=true;
    }

    if(tipgarg==null ){
      msgControlli.append("  non è valorizzato il tipo di procedura (campo TIPGARG)\n");
      esito=false;
    }

    if(cfimp == null && cfest == null){
      msgControlli.append("  non è valorizzato nè il codice fiscale nè l'ID fiscale estero della ditta aggiudicataria (campi CFIMP o CFEST)\n");
      bloccoCodFisc=true;
      esito=false;
    }

    if(cfimp!=null && cfest!=null){
      msgControlli.append("  sono valorizzati sia il codice fiscale che l'ID fiscale estero della ditta aggiudicataria (campi CFIMP e CFEST)\n");
      bloccoCodFisc=true;
      esito=false;
    }

    if((cfimp!=null && cfimp.length()>16) || (cfest!=null && cfest.length()>16)){
      if(cfimp!=null && cfimp.length()>16)
        msgControlli.append("  il codice fiscale della ditta aggiudicataria è più lungo di 16 caratteri (campo CFIMP)\n");
      else
        msgControlli.append("  l'ID fiscale estero della ditta aggiudicataria è più lungo di 16 caratteri (campo CFEST)\n");
      esito=false;
    }

    Long conteggioImpr = null;
    if(!bloccoCodFisc){
      String codfisc=null;
      if(cfimp!=null){
        codfisc= cfimp.toUpperCase();
      }else{
        codfisc= cfest.toUpperCase();
      }
      conteggioImpr = (Long)this.sqlManager.getObject("select count(codimp) from impr where upper(cfimp) = ?", new Object[]{codfisc});
      if(conteggioImpr!=null && conteggioImpr.longValue()>1){
        msgControlli.append("  in anagrafica sono presenti più ditte con codice fiscale uguale a quello della ditta aggiudicataria (").append(codfisc).append(")\n");
        esito=false;
      }
    }
    if(!bloccoStazApp){
      Long conteggioUffint = (Long)this.sqlManager.getObject("select count(codein) from uffint where upper(codein) = ?", new Object[]{cenint.toUpperCase()});
      if(conteggioUffint==null || (conteggioUffint!=null && conteggioUffint.longValue()==0)){
        msgControlli.append("  in anagrafica non è definita la stazione appaltante con codice '").append(cenint.toUpperCase()).append("'\n");
        esito=false;
      }
    }
    if(!esito)
      messaggi.append("\n\nCIG ").append(cig).append(":\n").append(msgControlli);

    ret.add(esito);
    ret.add(conteggioImpr);
    return ret;
  }

  private void inserimentoGara(Vector affidamentiEsterni, String codimp, String nomimp,String cenint,TransactionStatus status,
      DataColumnContainer datiForm, int cifreDecimaliRibasso) throws GestoreException{
    String ngara=null;
    Long tipgen = SqlManager.getValueFromVectorParam(
        affidamentiEsterni, 2).longValue();


    Object parametriGare[] = new Object[24];

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);

    if(geneManager.isCodificaAutomatica("TORN", "CODGAR")){
      HashMap hm = pgManager.calcolaCodificaAutomatica("GARE", Boolean.TRUE, null,
          null);
      ngara =  (String) hm.get("numeroGara");
    }else{
      GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
          this.getServletContext(), GenChiaviManager.class);

      String prefcod = SqlManager.getValueFromVectorParam(
          affidamentiEsterni,1).getStringValue();

      String progressivo = (new Long(genChiaviManager.getNextId("V_GARE_DATIESTERNI"))).toString();
      progressivo = UtilityStringhe.fillLeft(progressivo, '0', 6);
      ngara = prefcod + progressivo;
    }


    try {

      //Inserimento Gara
      //ngara
      parametriGare[0] = ngara;
      //codgar1
      parametriGare[1] = "$" + ngara;
      //codcig
      parametriGare[2] = SqlManager.getValueFromVectorParam(
          affidamentiEsterni, 0).getStringValue();
      //tipgarg
      parametriGare[3] = SqlManager.getValueFromVectorParam(
          affidamentiEsterni, 5).longValue();
      //not_gar
      parametriGare[4] = SqlManager.getValueFromVectorParam(
          affidamentiEsterni, 4).getStringValue();
      //impapp
      parametriGare[5] = SqlManager.getValueFromVectorParam(
          affidamentiEsterni, 6).doubleValue();
      //ditta
      parametriGare[6] = codimp;
      //nomima
      parametriGare[7] = nomimp;
      //iaggiu
      parametriGare[8] = SqlManager.getValueFromVectorParam(
          affidamentiEsterni, 11).doubleValue();
      //dattoa
      parametriGare[9] = SqlManager.getValueFromVectorParam(
          affidamentiEsterni, 12).dataValue();
      //fasgar
      parametriGare[10] = new Long(-3);
      //stepgar
      parametriGare[11] = new Long(-30);
      //modastg
      parametriGare[12] = new Long(2);
      //estimp
      parametriGare[13] = "1";
      //sicinc
      parametriGare[14] = "1";
      //temesi
      parametriGare[15] = new Long(1);
      //ribcal
      parametriGare[16] = new Long(1);
      //precut
      parametriGare[17] = new Long(tabellatiManager.getDescrTabellato("A1018", "1"));
      //pgarof
      parametriGare[18] = pgManager.initPGAROF(tipgen);
      //garoff
      parametriGare[19] = pgManager.calcolaGAROFF((Double)parametriGare[5],(Double)parametriGare[18],tipgen);
      //idiaut
      parametriGare[20] = pgManager.getContributoAutoritaStAppaltante((Double)parametriGare[5], "A1z01");
      //calcsoang
      parametriGare[21] = "1";
      //onsogrib
      parametriGare[22] = "1";
      //ribagg
      if(parametriGare[5]==null || (parametriGare[5]!=null && ((Double)parametriGare[5]).doubleValue()==0))
        parametriGare[23] = new Double(0);
      else{
        Double iaggiu = (Double)parametriGare[8];
        if(iaggiu==null)
          iaggiu= new Double(0);
        Double ribagg = (iaggiu - (Double)parametriGare[5])*100/(Double)parametriGare[5];
        if (cifreDecimaliRibasso!=0){
          ribagg = UtilityNumeri.convertiDouble(UtilityNumeri.convertiDouble(ribagg, UtilityNumeri.FORMATO_DOUBLE_CON_VIRGOLA_DECIMALE, cifreDecimaliRibasso));
        }
        parametriGare[23] = ribagg;
      }
      this.sqlManager.update(insertGara, parametriGare);

      //Inserimento in GARE1
      Object parametriGare1[] = new Object[2];
      parametriGare1[0] = ngara;
      //codgar1
      parametriGare1[1] = "$" + ngara;
      this.sqlManager.update(insertGare1, parametriGare1);

      //Inserimento TORN
      Object parametriTorn[] = new Object[7];
      //codgar
      parametriTorn[0] = "$" + ngara;
      //cenint
      parametriTorn[1] = cenint;
      //tipgen
      parametriTorn[2] = tipgen;
      //offaum
      parametriTorn[3] = "2";
      //compreq
      parametriTorn[4] = "2";
      //istaut
      parametriTorn[5] = pgManager.getContributoAutoritaStAppaltante(
          (Double)parametriGare[5], "A1z02");
      //iterga
      parametriTorn[6] = pgManager.getITERGA((Long)parametriGare[3]);

      this.sqlManager.update(insertTorn, parametriTorn);


      //Inserimento GARECONT
      Object parametriGarecont[] = new Object[6];
      //ngara
      parametriGarecont[0] = ngara;
      //ncont
      parametriGarecont[1] = new Long(1);
      //dverbc
      parametriGarecont[2] = SqlManager.getValueFromVectorParam(
          affidamentiEsterni, 13).dataValue();
      //dcertu
      parametriGarecont[3] = SqlManager.getValueFromVectorParam(
          affidamentiEsterni, 14).dataValue();
      //impliq
      parametriGarecont[4] = SqlManager.getValueFromVectorParam(
          affidamentiEsterni, 15).doubleValue();
      //codimp
      parametriGarecont[5] = codimp;

      this.sqlManager.update(insertGarecont, parametriGarecont);


      //Inserimento in DITG
      Vector elencoCampi = new Vector();
      elencoCampi.add(new DataColumn("DITG.NGARA5",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
      elencoCampi.add(new DataColumn("DITG.CODGAR5",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, "$"+ngara)));
      elencoCampi.add(new DataColumn("DITG.DITTAO",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, codimp)));
      elencoCampi.add(new DataColumn("DITG.NOMIMO",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, nomimp)));
      elencoCampi.add(new DataColumn("DITG.NPROGG",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
      elencoCampi.add(new DataColumn("DITG.NUMORDPL",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
      elencoCampi.add(new DataColumn("DITG.IMPOFF",
          new JdbcParametro(JdbcParametro.TIPO_DECIMALE, parametriGare[8])));
      elencoCampi.add(new DataColumn("DITG.INVOFF",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, "1")));
      elencoCampi.add(new DataColumn("DITG.IMPAPPD",
          new JdbcParametro(JdbcParametro.TIPO_DECIMALE, parametriGare[5])));
      elencoCampi.add(new DataColumn("DITG.RIBAUO",
          new JdbcParametro(JdbcParametro.TIPO_DECIMALE, parametriGare[23])));

      DataColumnContainer containerDITG = new DataColumnContainer(elencoCampi);
      GestoreDITG gestoreDITG = new GestoreDITG();
      gestoreDITG.setRequest(this.getRequest());
      gestoreDITG.inserisci(status, containerDITG);

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'inserimento della gara con ngara=" + ngara,null, e);
    }

    // INSERIMENTO PERMESSI DI ACCESSO ALLA GARA
    datiForm.addColumn("TORN.CODGAR", JdbcParametro.TIPO_TESTO, "$"+ngara);
    this.inserisciPermessi(datiForm, "CODGAR", new Integer(2));

  }
}
