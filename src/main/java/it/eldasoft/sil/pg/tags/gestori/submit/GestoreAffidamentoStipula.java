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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Gestore non standard di gare create rapidamente con i dati di aggiudicazione per poter fare una stipula
 *
 * @author Peruzzo Riccardo
 */
public class GestoreAffidamentoStipula extends AbstractGestoreEntita {

  private static final String insertGara ="insert into gare(ngara, codgar1, codcig, tipgarg, not_gar, impapp, ditta, nomima, iaggiu, dattoa," +
    " fasgar, stepgar, modastg, ribagg, sicinc, temesi, ribcal, precut, pgarof, garoff, idiaut, calcsoang, onsogrib) " +
    " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

  private static final String insertTorn = "insert into torn(codgar, cenint, tipgen, offaum, compreq, istaut, iterga, codrup, numavcp, settore, modrea, accqua, isadesione, altrisog, prerib, cliv2) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

  private static final String insertGarecont = "insert into garecont(ngara, ncont, codimp) values(?,?,?)";

  private static final String insertGare1 = "insert into gare1(ngara, codgar1) values(?,?)";

  private static final String updateAppa="update appa set dconsd=?, dult=?, itotaleliqui=? where codlav=? and nappal=?";

  private TabellatiManager tabellatiManager;

  /** Manager di PG */
  private PgManager        pgManager        = null;

  private PgManagerEst1    pgManagerEst1    = null;

  private GenChiaviManager genChiaviManager = null;

  @Override
  public String getEntita() {
    return "TORN";
  }

  public GestoreAffidamentoStipula() {
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
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

	String cenint = null;
	String codimp = null;
	String nomimp = null;
    String nomest=null;

    String ufficioIntestatario=null;
    HttpSession session = this.getRequest().getSession();
    if ( session != null) {
      ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
    }

	tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
            this.getServletContext(), TabellatiManager.class);

	codimp = datiForm.getString("GARE.DITTA");
	nomest = datiForm.getString("GARE.NOMIMA");
	cenint = datiForm.getString("TORN.CENINT");
	nomimp = nomest;

	// Inserimento

	int cifreDecimali = datiForm.getLong("TORN.PRERIB").intValue();

	// Inserimento in GARE
	this.inserimentoGara(codimp, nomimp, cenint, status, datiForm, cifreDecimali);

  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }

  private void inserimentoGara(String codimp, String nomimp,String cenint,TransactionStatus status,
      DataColumnContainer datiForm, int cifreDecimaliRibasso) throws GestoreException{

    String ngara=null;

    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
            this.getServletContext(), PgManager.class);

    pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
            this.getServletContext(), PgManagerEst1.class);

    genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
            this.getServletContext(), GenChiaviManager.class);


        if(geneManager.isCodificaAutomatica("TORN", "CODGAR")){
          HashMap hm = pgManager.calcolaCodificaAutomatica("GARE", Boolean.TRUE, null,
              null);
          ngara =  (String) hm.get("numeroGara");
          this.getRequest().setAttribute("ngara", ngara);
        }else{
          GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
              this.getServletContext(), GenChiaviManager.class);

        }

    Long tipgen = datiForm.getLong("TORN.TIPGEN");
    if(tipgen==null)
    	throw new GestoreException(
      		  "Errore nell'inserimento della gara con ngara=" + ngara + " :Tipo appalto non valorizzato",null);

    // Gestione del codice CIG fittizio
    if (datiForm.isColumn("ESENTE_CIG") && datiForm.isModifiedColumn("ESENTE_CIG")) {
    	String esenteCig = datiForm.getString("ESENTE_CIG");
    	String codCigFittizio = datiForm.getString("CODCIG_FIT");
    	if ("1".equals(esenteCig)) {
    		if (StringUtils.isEmpty(codCigFittizio) || " ".equals(codCigFittizio)) {
    			int nextId = this.genChiaviManager.getNextId("GARE.CODCIG");
    			codCigFittizio = "#".concat(StringUtils.leftPad(""+nextId, 9, "0"));
    			datiForm.setValue("GARE.CODCIG", codCigFittizio);
    		} else {
    			datiForm.setValue("GARE.CODCIG", codCigFittizio);
    		}
    	}
    }

    String codcig=datiForm.getString("GARE.CODCIG");

    try {
        if(datiForm.isColumn("GARE.CODCIG") && datiForm.getColumn("GARE.CODCIG").getValue().stringValue()!=null
              && !"".equals(datiForm.getColumn("GARE.CODCIG").getValue().stringValue())){

          String msg = pgManager.controlloUnicitaCIG(datiForm.getColumn("GARE.CODCIG").getValue().stringValue(),ngara);
          if(msg!=null){
            String descrizione = (String) this.sqlManager.getObject(
                "select tab1desc from tab1 where tab1cod = ? and tab1tip = ?",
                new Object[] { "A1151","1" });
            if(descrizione.substring(0, 1).equals("0")){
                UtilityStruts.addMessage(this.getRequest(), "warning",
                "warnings.gare.codiceCIGDuplicato",
                new Object[] {msg });
            }else{
            	throw new GestoreException("Errore durante l'aggiornamento del campo GARE.CODCIG", "gare.codiceCIGDuplicato",new Object[] {msg },  new Exception());
            }
          }
        }
     } catch (SQLException e1) {
          throw new GestoreException("Errore nella lettura della campo tabellato A1151",null,e1);
     }

    //Controllo che  TORN.NUMAVCP sia numerico
    if (datiForm.isColumn("TORN.NUMAVCP")) {
      String numavcp = datiForm.getString("TORN.NUMAVCP");
      numavcp = UtilityStringhe.convertiNullInStringaVuota(numavcp);
      if (!"".equals(numavcp)) {
        if (!GestoreAffidamentoStipula.isNumeric(numavcp)) {
        	String errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.NGaraAnacNoNumerico");
        	throw new GestoreException("Il valore specificato per N.gara ANAC deve essere numerico","NGaraAnacNoNumerico");
        }
      }
    }

    String iscuc=datiForm.getString("UFFINT.ISCUC");
    Long altrisog = null;

	//Inizializzazione ALTRISOG

	if("1".equals(iscuc)){
		altrisog=new Long(1);
	}

    //Inizializzazione del campo MODREA, ma solo per lotto unico e per offerte distinte o offerta unica(ma non per i lotti)
    String modrea = null;
    if(datiForm.isColumn("TORN.ACCQUA") && ( datiForm.isColumn("GARE.TIPGARG"))) {
      Long tipgar = null;
      tipgar = datiForm.getLong("GARE.TIPGARG");
      modrea = this.pgManagerEst1.getModrea(datiForm.getString("TORN.ACCQUA"), altrisog, tipgar);
    }

    Object parametriGare[] = new Object[23];

    try {

      //Inserimento Gara
      //ngara
      parametriGare[0] = ngara;
      //codgar1
      parametriGare[1] = "$" + ngara;
      //codcig
      parametriGare[2] = codcig;
      //tipgarg
      parametriGare[3] = datiForm.getLong("GARE.TIPGARG");
      //not_gar
      parametriGare[4] = datiForm.getString("GARE.NOT_GAR");
      //impapp
      parametriGare[5] = datiForm.getDouble("GARE.IMPAPP");
      if(parametriGare[5]==null)
    	  parametriGare[5]= new Double(0);
      //ditta
      parametriGare[6] = codimp;
      //nomima
      parametriGare[7] = nomimp;
      //iaggiu
      parametriGare[8] =  datiForm.getDouble("GARE.IAGGIU");;
      if(parametriGare[8]==null)
    	  parametriGare[8]= new Double(0);
      //dattoa
      parametriGare[9] = datiForm.getData("GARE.DATTOA");
      //fasgar
      parametriGare[10] = new Long(-3);
      //stepgar
      parametriGare[11] = new Long(-30);
      //modastg
      parametriGare[12] = new Long(2);
      //ribagg
      if(parametriGare[5]==null || (parametriGare[5]!=null && ((Double)parametriGare[5]==0)))
        parametriGare[13] = new Double(0);
      else{
        Double iaggiu = ((Double)parametriGare[8]);
        if(iaggiu==null)
          iaggiu= new Double(0);
        Double ribagg = (iaggiu - (Double)parametriGare[5])*100/(Double)parametriGare[5];
        if (cifreDecimaliRibasso!=0){
          ribagg = UtilityNumeri.convertiDouble(UtilityNumeri.convertiDouble(ribagg, UtilityNumeri.FORMATO_DOUBLE_CON_VIRGOLA_DECIMALE, cifreDecimaliRibasso));
        }
        parametriGare[13] = ribagg;
      }
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
      if(parametriGare[18]==null)
    	  parametriGare[18]= new Double(0);
      //garoff
      parametriGare[19] = pgManager.calcolaGAROFF((Double)parametriGare[5],(Double)parametriGare[18],tipgen);
      //idiaut
      parametriGare[20] = pgManager.getContributoAutoritaStAppaltante((Double)parametriGare[5], "A1z01");
      //calcsoang
      parametriGare[21] = "2";
      //onsogrib
      parametriGare[22] = "1";
      this.sqlManager.update(insertGara, parametriGare);

      //Inserimento in GARE1
      Object parametriGare1[] = new Object[2];
      parametriGare1[0] = ngara;
      //codgar1
      parametriGare1[1] = "$" + ngara;
      this.sqlManager.update(insertGare1, parametriGare1);

      //Inserimento TORN
      Object parametriTorn[] = new Object[16];
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
      //codrup
      parametriTorn[7] = datiForm.getString("TORN.CODRUP");
      //numavcp
      parametriTorn[8] = datiForm.getString("TORN.NUMAVCP");
      //settore
      parametriTorn[9] = datiForm.getString("TORN.SETTORE");
      //modrea
      parametriTorn[10] = modrea;
      //accqua
      parametriTorn[11] = datiForm.getString("TORN.ACCQUA");
      //isadesione
      parametriTorn[12] = datiForm.getString("TORN.ISADESIONE");
      //altrisog
      parametriTorn[13] = altrisog;
      //prerib
      parametriTorn[14] = datiForm.getLong("TORN.PRERIB");
      //cliv2
      parametriTorn[15] = datiForm.getLong("TORN.CLIV2");

      this.sqlManager.update(insertTorn, parametriTorn);


      //Inserimento GARECONT
      Object parametriGarecont[] = new Object[3];
      //ngara
      parametriGarecont[0] = ngara;
      //ncont
      parametriGarecont[1] = new Long(1);
      //codimp
      parametriGarecont[2] = codimp;

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
          new JdbcParametro(JdbcParametro.TIPO_DECIMALE, parametriGare[13])));

      DataColumnContainer containerDITG = new DataColumnContainer(elencoCampi);
      GestoreDITG gestoreDITG = new GestoreDITG();
      gestoreDITG.setRequest(this.getRequest());
      gestoreDITG.inserisci(status, containerDITG);

      //Gestione CPV
      GestoreGARE gg= new GestoreGARE();
      gg.setRequest(this.getRequest());
      if(!datiForm.isColumn("GARE.NGARA"))
        datiForm.addColumn("GARE.NGARA", ngara);
      if (datiForm.isModifiedColumn("GARCPV.CODCPV")) {
        datiForm.setValue("GARCPV.NGARA", ngara);
      }
      gg.updateGARCPV(status,datiForm);


    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'inserimento della gara con ngara=" + ngara,null, e);
    }


    // INSERIMENTO PERMESSI DI ACCESSO ALLA GARA
    datiForm.addColumn("TORN.CODGAR", JdbcParametro.TIPO_TESTO, "$"+ngara);
    this.inserisciPermessi(datiForm, "CODGAR", new Integer(2));

  }
  /*
   * Verifico che una stringa rappresenti un numeor
   */
  private static boolean isNumeric(String str) {

    boolean numerico = true;
    char[] seq = str.toCharArray();

    for (int i=0; i< seq.length; i++) {
      try {
        Integer.parseInt(Character.toString(seq[i]));
      } catch (Exception e) {
        numerico = false;
      }
    }

    return numerico;
  }
}


