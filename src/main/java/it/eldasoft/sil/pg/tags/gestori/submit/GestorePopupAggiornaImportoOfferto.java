/*
 * Created on 20-06-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.sil.pg.bl.ControlliOepvManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityMath;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standart per la popup "Aggiorna importo offerto della ditta"
 *
 * @author Marcello Caminiti
 */
public class GestorePopupAggiornaImportoOfferto extends AbstractGestoreEntita {


	@Override
  public String getEntita() {
		return "DITG";
	}

	public GestorePopupAggiornaImportoOfferto() {
	    super(false);
	  }


	  public GestorePopupAggiornaImportoOfferto(boolean isGestoreStandard) {
	    super(isGestoreStandard);
	  }

	@Override
  public void postDelete(DataColumnContainer datiForm)
			throws GestoreException {
	}


	@Override
  public void postInsert(DataColumnContainer datiForm)
			throws GestoreException {
	}


	@Override
  public void postUpdate(DataColumnContainer datiForm)
			throws GestoreException {
	}


	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	}

	//Gestione dell'inserimento nelle entita' GCAP e DPRE
	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

	  TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
          "tabellatiManager", this.getServletContext(), TabellatiManager.class);

        //Aggiornamento dalla lista
       String newImportoEffettivo = UtilityStruts.getParametroString(this.getRequest(), "totaleOfferto");
       String codgar = UtilityStruts.getParametroString(this.getRequest(),"codiceGara");
       String ngara = UtilityStruts.getParametroString(this.getRequest(),"numeroGara");
       String ditta = UtilityStruts.getParametroString(this.getRequest(),"codiceDitta");
       String dittaAgg = UtilityStruts.getParametroString(this.getRequest(),"dittaAgg");
       String sicinc = UtilityStruts.getParametroString(this.getRequest(),"sicinc");
       String onsogrib = UtilityStruts.getParametroString(this.getRequest(),"onsogrib");
       String  garaOffertaUnica = UtilityStruts.getParametroString(this.getRequest(),"isGaraLottiConOffertaUnica");
       String  ribcal = UtilityStruts.getParametroString(this.getRequest(),"ribcal");
       String faseGara = UtilityStruts.getParametroString(this.getRequest(),"faseGara");
       String  reqmin_ditta ="";
       String  isPrequalifica = UtilityStruts.getParametroString(this.getRequest(),"isPrequalifica");
       String sbiancareAggiudicazione = UtilityStruts.getParametroString(this.getRequest(),"sbiancareAggiudicazione");
       boolean annullaRibasso = false;
       String riboepvVis = UtilityStruts.getParametroString(this.getRequest(),"riboepvVis");

      //Importo da utilizzare per aggiornare IMPOFF
      Double importoDouble = UtilityNumeri.convertiDouble(newImportoEffettivo);
      try {
          if (!"TRUE".equals(garaOffertaUnica.toUpperCase())){
              //Gestione gara semplice
              //Si aggiorna IMPOFF.DITG con il Totale offerto complessivo della ditta
                  //calcolato nella v_gcap_dpre-lista.jsp
              this.getSqlManager().update(
                          "update DITG set IMPOFF = ?, REQMIN = ?  where NGARA5 = ? and CODGAR5 = ? and DITTAO = ?",
                          new Object[] { importoDouble, reqmin_ditta, ngara, codgar, ditta});

                  String select="select onprge, impsic,dittap,impapp,impnrl from gare where ngara = ?";

                  Double impsic = null;
                  Double onprge = null;
                  String dittap = "";
                  Double  impapp = null;
                  Double impnrl = null;

                  Vector<?> datiGARE = sqlManager.getVector(select,
                          new Object[] { ngara });
                  if (datiGARE != null && datiGARE.size() > 0) {
                      onprge = ((Double) ((JdbcParametro) datiGARE.get(0)).getValue());
                      impsic = ((Double) ((JdbcParametro) datiGARE.get(1)).getValue());
                      dittap = ((String) ((JdbcParametro) datiGARE.get(2)).getValue());
                      impapp = ((Double) ((JdbcParametro) datiGARE.get(3)).getValue());
                      impnrl = ((Double) ((JdbcParametro) datiGARE.get(4)).getValue());
                  }

                  if (impsic == null)
                      impsic = new Double(0);
                  if (onprge == null)
                      onprge = new Double(0);
                  if (dittap == null)
                      dittap = "";
                  if (impapp == null)
                      impapp = new Double(0);
                  if (impnrl == null)
                      impnrl = new Double(0);

                  double iaggiu;
                  //iaggiu = importoDouble.doubleValue() + onprge.doubleValue();
                  iaggiu = importoDouble.doubleValue();

                  if (sicinc != null && "2".equals(sicinc)) iaggiu += impsic.doubleValue();
                  iaggiu = UtilityMath.round(iaggiu, 2);

              //Nel caso la ditta corrente sia quella aggiudicataria in via definitiva
                  //si deve aggiornare IAGGIU.GARE
              if ("SI".equals(dittaAgg)){
                      this.getSqlManager().update(
                              "update GARE set IAGGIU = ?  where NGARA = ? ",
                              new Object[] { new Double(iaggiu), ngara});
                  }

                //Nel caso la ditta corrente sia quella aggiudicataria in via provvisoria
                //si deve aggiornare IAGPRO.GARE
                if (dittap.equals(ditta)){
                      this.getSqlManager().update(
                              "update GARE set IAGPRO = ?  where NGARA = ? ",
                              new Object[] { new Double(iaggiu), ngara});
                  }

                //se ribcal=2 e fasgar<7 si deve aggiornare RIBAUO
                if (("2".equals(ribcal) || "true".equals(riboepvVis)) && Long.parseLong(faseGara)<7){
                  Object[] parametri = new Object[7];
                  parametri[0] = impapp;
                  parametri[1] = onprge;
                  parametri[2] = impsic;
                  parametri[3] = impnrl;
                  parametri[4] = sicinc;
                  parametri[5] = importoDouble;
                  parametri[6] = onsogrib;
                  this.settaRibasso(sqlManager, codgar, ngara, ditta, parametri, "true".equals(sbiancareAggiudicazione),riboepvVis);
              }
          } else {
              // Gestione gara divisa a lotti con offerta unica
              // Si aggiornano i campi IMPOFF.DITG, IAGPRO, IAGGIU.GARE relativi ai lotti.
              Double importoNoRibasso = null;
              Double importoSicurezza = null;
              double totaleOffertoLotto;
              double totImpoff=0;
              Double importoOneriProg = null;
              Double importoBaseAsta = null;
              Long modlicg= null;
              String riboepvVisLotto = null;

              String select="select NGARA,IMPNRL,IMPSIC,ONPRGE,DITTAP,DITTA,IMPAPP,MODLICG,OFFAUM from gare,ditg,torn where codgar1 = ? and codgar1=codgar and (genere <> 3 or genere is null) " +
              		"and (modlicg = 5 or modlicg = 6 or modlicg = 14 or modlicg = 16) and codgar1=codgar5 and ngara5=ngara and dittao = ? and (partgar='1' or partgar is null) and (ditg.fasgar > 4 or ditg.fasgar is null) ";

              List<?> datiGARE = sqlManager.getListVector(select, new Object[] { codgar,ditta });

              if (datiGARE != null && datiGARE.size() > 0) {
                ControlliOepvManager controlliOepvManager = (ControlliOepvManager) UtilitySpring.getBean("controlliOepvManager",
                    this.getServletContext(), ControlliOepvManager.class);

                for (int i = 0; i < datiGARE.size(); i++) {
                          String ngaraLotto = SqlManager.getValueFromVectorParam(
                                  datiGARE.get(i), 0).stringValue();

                          //Importo non soggetto al ribasso
                          importoNoRibasso = SqlManager.getValueFromVectorParam(datiGARE.get(i),1).doubleValue();
                          if(importoNoRibasso == null)
                              importoNoRibasso = new Double(0);

                          //Importo sicurezza
                          importoSicurezza = SqlManager.getValueFromVectorParam(datiGARE.get(i),2).doubleValue();
                          if(importoSicurezza == null)
                              importoSicurezza = new Double(0);

                          //Importo dettaglio prezzi
                          select = "select impoff,reqmin from dpre where ngara= ? and dittao = ?";

                          totImpoff= 0;
                          String reqmin_tot = "";
                          Boolean tutteLavorazioniConformi = null;
                          List<?> retDPRE = sqlManager.getListVector(select,
                                  new Object[] { ngaraLotto, ditta});
                          if (retDPRE != null && retDPRE.size() > 0){
                            tutteLavorazioniConformi = new Boolean(true);
                            for (int j = 0; j < retDPRE.size(); j++) {
                                  Double impoff = SqlManager.getValueFromVectorParam(retDPRE.get(j),0).doubleValue();
                                  if (impoff  == null ) impoff = new Double(0);
                                  totImpoff += impoff.doubleValue();
                                  String reqmin = SqlManager.getValueFromVectorParam(retDPRE.get(j),1).getStringValue();
                                  if("2".equals(reqmin)){
                                    reqmin_tot = new String("2");
                                    tutteLavorazioniConformi = new Boolean(false);
                                  }else if(reqmin==null || "".equals(reqmin)){
                                    tutteLavorazioniConformi = new Boolean(false);
                                  }
                              }
                          }

                          if("false".equals( isPrequalifica)){

                              totaleOffertoLotto = importoNoRibasso.doubleValue() + totImpoff;
                              if (sicinc == null || "1".equals(sicinc)) totaleOffertoLotto += importoSicurezza.doubleValue();

                              //Aggiornamento IMPOFF.DITG
                              Double impoff = null;
                              if(totaleOffertoLotto!=0)
                                impoff = new Double(totaleOffertoLotto);
                              this.getSqlManager().update(
                                  "update DITG set IMPOFF = ? where NGARA5 = ? and CODGAR5 = ? and DITTAO = ? AND (FASGAR IS NULL OR FASGAR >5 )",
                                   new Object[] { impoff, ngaraLotto, codgar, ditta});

                              if(totImpoff>0){
                                //Aggiornamento PARTGAR.DITG e INVOFF
                                Vector<?> datiDitg = sqlManager.getVector("select partgar,invoff from ditg where NGARA5 = ? and CODGAR5 = ? and DITTAO =? ", new Object[]{ngaraLotto, codgar, ditta});
                                String partgar = SqlManager.getValueFromVectorParam(datiDitg, 0).getStringValue();
                                String invoff = SqlManager.getValueFromVectorParam(datiDitg, 1).getStringValue();
                                if("".equals(partgar))
                                  partgar=null;
                                if("".equals(invoff))
                                  invoff=null;
                                if(partgar==null || invoff==null ){
                                  String update="update ditg set";
                                  if(partgar==null)
                                    update+=" partgar='1'";
                                  if(invoff==null){
                                    if(partgar==null)
                                      update+=",";
                                    update+=" invoff='1'";
                                  }
                                  update+="  where NGARA5 = ? and CODGAR5 = ? and DITTAO = ? AND (FASGAR IS NULL OR FASGAR >5 )";
                                  this.getSqlManager().update(update, new Object[] { ngaraLotto, codgar, ditta});
                                }

                              }

                              //Oneri progettazione
                              importoOneriProg = SqlManager.getValueFromVectorParam(datiGARE.get(i),3).doubleValue();
                              if(importoOneriProg == null) importoOneriProg = new Double(0);

                              //Importo a base d'asta
                              importoBaseAsta = SqlManager.getValueFromVectorParam(datiGARE.get(i),6).doubleValue();
                              if(importoBaseAsta == null) importoBaseAsta = new Double(0);

                              //Aggiudicazione provvisoria
                              String dittap = SqlManager.getValueFromVectorParam(
                                      datiGARE.get(i), 4).stringValue();
                              if (dittap == null)
                                  dittap = "";

                              //Aggiudicazione definitiva
                              String dittaDef = SqlManager.getValueFromVectorParam(
                                      datiGARE.get(i), 5).stringValue();
                              if (dittaDef == null)
                                  dittaDef = "";

                              if (dittap.equals(ditta) || dittaDef.equals(ditta)){
                                  double iaggiu;
                                  iaggiu = totaleOffertoLotto + importoOneriProg.doubleValue();
                                  if (sicinc != null && "2".equals(sicinc)) iaggiu += importoSicurezza.doubleValue();
                                  iaggiu = UtilityMath.round(iaggiu, 2);

                                  //Nel caso la ditta corrente sia quella aggiudicataria in via definitiva
                                  //si deve aggiornare IAGGIU.GARE
                              if (dittaDef.equals(ditta)){
                                      this.getSqlManager().update(
                                              "update GARE set IAGGIU = ?  where NGARA = ? ",
                                              new Object[] { new Double(iaggiu), ngaraLotto});
                                  }

                              //Nel caso la ditta corrente sia quella aggiudicataria in via provvisoria
                              //si deve aggiornare IAGPRO.GARE
                              if (dittap.equals(ditta)){
                                      this.getSqlManager().update(
                                              "update GARE set IAGPRO = ?  where NGARA = ? ",
                                              new Object[] { new Double(iaggiu), ngaraLotto});
                                  }
                              }

                              //Aggiornamento di RIBAUO se ribcal=2 e fasgar<7
                              riboepvVisLotto = null;
                              modlicg = SqlManager.getValueFromVectorParam(datiGARE.get(i),7).longValue();
                              if(new Long(6).equals(modlicg)){
                                boolean formato51 = controlliOepvManager.checkFormato(ngaraLotto, new Long(51));
                                boolean vecchiaOepv = controlliOepvManager.isVecchiaOepvFromNgara(codgar);
                                if(formato51 || vecchiaOepv)
                                  riboepvVisLotto="true";
                              }

                              if (("2".equals(ribcal) || "true".equals(riboepvVisLotto)) && Long.parseLong(faseGara)<7){
                                Object[] parametri = new Object[7];
                                parametri[0] = importoBaseAsta;
                                parametri[1] = importoOneriProg;
                                parametri[2] = importoSicurezza;
                                parametri[3] = importoNoRibasso;
                                parametri[4] = sicinc;
                                parametri[5] = new Double(totaleOffertoLotto);
                                parametri[6] = '1'; //per le gare non lavori, onsogrib è sempre 1

                                //Se importo offerto è maggiore dell'importo a base d'asta e offaum = '2'
                                //si deve annullare il ribasso
                                String offaum = SqlManager.getValueFromVectorParam(
                                    datiGARE.get(i), 8).stringValue();
                                if(impoff==null)
                                  impoff = new Double(0);
                                if(impoff.longValue() > importoBaseAsta.longValue() && "2".equals(offaum))
                                  annullaRibasso = true;
                                else
                                  annullaRibasso = false;
                                this.settaRibasso(sqlManager, codgar, ngaraLotto, ditta, parametri,annullaRibasso,riboepvVisLotto);
                              }
                          }else{
                            //prequalifica
                            //Aggiornamento AMMGAR.DITG e FASGAR.DITG....Addizionare una motivazione...
                            //Inserimento oppure aggiornamento di ditgammis

                            if(tutteLavorazioniConformi!= null && tutteLavorazioniConformi.booleanValue())
                              reqmin_tot = new String("1");
                            reqmin_ditta = reqmin_tot;
                            //Aggiornamento IMPOFF.DITG,PARTGAR.DITG,AMMGAR.DITG e FASGAR.DITG
                            this.getSqlManager().update(
                              "update DITG set  REQMIN = ?  where NGARA5 = ? and CODGAR5 = ? and DITTAO = ?",
                              new Object[] { reqmin_ditta, ngaraLotto, codgar, ditta});

                            GestoreDITG gestOffAmm = new GestoreDITG();
                            gestOffAmm.setRequest(this.getRequest());
                            if("2".equals(reqmin_ditta)){
                              DataColumn d1 = new DataColumn("V_DITGAMMIS.AMMGAR", new JdbcParametro(
                                  JdbcParametro.TIPO_NUMERICO, new Long(2)));
                              d1.setObjectOriginalValue(new Long(1));
                              //String detEsclusione = tabellatiManager.getDescrTabellato("A2054", "102");
                              DataColumn d2 = new DataColumn("V_DITGAMMIS.MOTIVESCL", new JdbcParametro(
                                  JdbcParametro.TIPO_NUMERICO, new Long(102)));
                              DataColumn d3 = new DataColumn("V_DITGAMMIS.DETMOTESCL", new JdbcParametro(
                                  JdbcParametro.TIPO_TESTO, ""));
                              d3.setObjectOriginalValue("  ");
                              gestOffAmm.gestioneDITGAMMIS(codgar, ngaraLotto, ditta,
                                  new Long(5), new DataColumn[] { d1,d2,d3 }, status);
                              gestOffAmm.aggiornaStatoAmmissioneDITG(codgar, ngaraLotto, ditta,
                                  new Long(5) , new DataColumn[] { d1,d2,d3 }, status);
                            }else if("1".equals(reqmin_ditta) || reqmin_ditta == null || "".equals(reqmin_ditta)){
                              DataColumn d1 = new DataColumn("V_DITGAMMIS.AMMGAR", new JdbcParametro(
                                  JdbcParametro.TIPO_NUMERICO, null));
                              d1.setObjectOriginalValue(new Long(2));
                              String detEsclusione = tabellatiManager.getDescrTabellato("A2054", "102");
                              DataColumn d2 = new DataColumn("V_DITGAMMIS.MOTIVESCL", new JdbcParametro(
                                  JdbcParametro.TIPO_NUMERICO,null));
                              d2.setObjectOriginalValue(new Long(102));
                              DataColumn d3 = new DataColumn("V_DITGAMMIS.DETMOTESCL", new JdbcParametro(
                                  JdbcParametro.TIPO_TESTO,"" ));
                              d3.setObjectOriginalValue(detEsclusione);

                              gestOffAmm.gestioneDITGAMMIS(codgar, ngaraLotto, ditta,
                                  new Long(5), new DataColumn[] { d1,d2,d3 }, status);
                              gestOffAmm.aggiornaStatoAmmissioneDITG(codgar, ngaraLotto, ditta,
                                  new Long(5) , new DataColumn[] { d1,d2,d3 }, status);
                            }

                            if("2".equals(reqmin_ditta) || "1".equals(reqmin_ditta)){
                              this.getSqlManager().update(
                                  "update DITG set  PARTGAR = ?, INVOFF = ? where NGARA5 = ? and CODGAR5 = ? and DITTAO = ?",
                                  new Object[] { "1", "1", ngaraLotto, codgar, ditta});
                            }


                          }


                      }
                  }
              }
            this.getRequest().setAttribute("aggiornamentoEseguito", "1");
          } catch (SQLException e) {
              throw new GestoreException(
                        "Errore durante l'aggiornamento dell'importo offerto effettivo",
                        null, e);
          }


	}


	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm )
			throws GestoreException {



	}

	/**
   * Metodo che calcola il campo ribasso (RIBAUO.DITG):
   *
   * @param sqlManager
   * @param codiceGara
   * @param numeroGara
   * @param ditta
   * @param parametriImporti
   * @param annullaRibasso
   * @param riboepvVis
   * @return
   * @throws GestoreException
   */
	private void settaRibasso(SqlManager sqlManager, String codiceGara,
	 		String numeroGara, String ditta,Object[] parametriImporti, boolean annullaRibasso, String riboepvVis) throws GestoreException{

	 	TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
			  "tabellatiManager", this.getServletContext(), TabellatiManager.class);

	  AggiudicazioneManager aggiudicazioneManager = (AggiudicazioneManager)
	  		UtilitySpring.getBean("aggiudicazioneManager", this.getServletContext(),
		            AggiudicazioneManager.class);

	  double ribasso=0.0;
	  Double impapp = (Double) parametriImporti[0];
	  Double onprge = (Double) parametriImporti[1];
	  Double impsic = (Double) parametriImporti[2];
	  Double impnrl = (Double) parametriImporti[3];
	  String sicinc = (String) parametriImporti[4];
	  Double impoff = (Double) parametriImporti[5];
	  String onsogrib = "";
	  if(parametriImporti[6] instanceof Character)
	    onsogrib = ((Character) parametriImporti[6]).toString();
	  else
	    onsogrib =(String)parametriImporti[6];

	  int cifreDecimali=9;
	  if(annullaRibasso){
	    impoff = null;
	  }else{
	    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean(
	          "pgManagerEst1", this.getServletContext(), PgManagerEst1.class);
	    String cifreDecimaliTabellato;
        try {
          cifreDecimaliTabellato = pgManagerEst1.getNumeroDecimaliRibasso(codiceGara);
        } catch (SQLException e) {
          throw new GestoreException("Errore durante la lettura del numero di decimali da usare per il ribasso della gara(NGARA = '" + numeroGara + ") ", null, e);
        }
    	  if (cifreDecimaliTabellato != null || !"".equals(cifreDecimaliTabellato))
        		cifreDecimali = Integer.parseInt(cifreDecimaliTabellato);
    	  ribasso = aggiudicazioneManager.calcolaRIBAUO(impapp.doubleValue(),
    	  		onprge.doubleValue(), impsic.doubleValue(), impnrl.doubleValue(),
    	  		sicinc, impoff.doubleValue(),onsogrib);
    	  ribasso = UtilityMath.round(ribasso, cifreDecimali);
	  }

	  String campoRibasso = "RIBAUO";
	  if("true".equals(riboepvVis))
	    campoRibasso = "RIBOEPV";

	  try {
	    //CF 05-08-2010 Aggiorno il ribasso a null quando l'importo offerto e' null
	    if (impoff != null && impoff.doubleValue()!=0){
	      this.getSqlManager().update(
              "update DITG set " + campoRibasso + " = ?  where NGARA5 = ? and CODGAR5 = ? and DITTAO = ? and IMPOFF is not NULL and IMPOFF<>0 ",
              new Object[] { new Double(ribasso), numeroGara, codiceGara, ditta});
	    }else{
	        this.getSqlManager().update(
	            "update DITG set " + campoRibasso + " = null  where NGARA5 = ? and CODGAR5 = ? and DITTAO = ?  ",
	            new Object[] { numeroGara, codiceGara, ditta});
	    }

	  } catch (SQLException e) {
	  	throw new GestoreException("Errore durante l'aggiornamento  " +
	              "del ribasso della ditta ", null, e);
	  }
  }




}