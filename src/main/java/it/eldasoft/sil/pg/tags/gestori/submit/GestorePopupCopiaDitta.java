/*
 * Created on 07/03/12
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiRicezioneFunction;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per la pagina gare-popup-copia-ditte.jsp
 *
 * @author Marcello Caminiti
 */
public class GestorePopupCopiaDitta extends AbstractGestoreEntita {

	@Override
  public String getEntita() {
		return "GARE";
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

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	    String codiceDitta=UtilityStruts.getParametroString(this.getRequest(),"codiceDitta");
	    String  numeroGara = UtilityStruts.getParametroString(this.getRequest(),"numeroGara");
	    String codiceGara = UtilityStruts.getParametroString(this.getRequest(),"codiceGara");
	    String numeroFaseAttiva = UtilityStruts.getParametroString(this.getRequest(),"numeroFaseAttiva");
	    String[] listaDitteSelezionate = this.getRequest().getParameterValues("keys");
	    boolean modalitaNPROGGsuLotti = false;

		PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
	        this.getServletContext(), PgManager.class);

		TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
            this.getServletContext(), TabellatiManager.class);

		List listaTabellato = tabellatiManager.getTabellato("A1041");
        if(listaTabellato != null && listaTabellato.size() > 0){
          String descrTab = ((Tabellato) listaTabellato.get(0)).getDescTabellato();
          if(descrTab != null && descrTab.startsWith("1"))
            modalitaNPROGGsuLotti = true;
        }

		GestoreDITG gestDITG = new GestoreDITG();
		gestDITG.setRequest(this.getRequest());

		try {
			Vector elencoCampiDITG = new Vector();
			elencoCampiDITG.add(new DataColumn("DITG.CODGAR5",
                new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceGara)));

			String select ="";
			Object parametri[] = new Object[22];
			Long nprogg = null;
			int contatore = 1;
			String insert="insert into DITG(ngara5, codgar5, dittao, nprdom, dricind, oradom, mezdom" +
					",nprotg, dinvig, nproff, datoff, oraoff, mezoff, nomimo, nprogg, catimok, invgar, invoff, impappd,"
					+ "numordpl, dprdom, dproff) " +
					"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			for (int i = 0; i < listaDitteSelezionate.length; i++) {
			  String lottodestinazione = listaDitteSelezionate[i];

			  //Se nel lotto destinazione è già presente la ditta da copiare, per prima cosa viene eliminata
			  select ="select count(dittao) from ditg where codgar5 = ? and ngara5 = ? and dittao=?";
			  Long count = (Long)this.sqlManager.getObject(select, new Object[]{codiceGara,lottodestinazione,codiceDitta});

			  if(count!= null && count.longValue()>0){
			    if(elencoCampiDITG.size()>1){
                  elencoCampiDITG.remove(2);
                  elencoCampiDITG.remove(1);
			    }
			    elencoCampiDITG.add(new DataColumn("DITG.NGARA5",
                    new JdbcParametro(JdbcParametro.TIPO_TESTO, lottodestinazione)));

			    elencoCampiDITG.add(new DataColumn("DITG.DITTAO",
			        new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceDitta)));

			    DataColumnContainer dataColumnContainer = new DataColumnContainer(elencoCampiDITG);
                gestDITG.preDelete(status, dataColumnContainer);
                this.getGeneManager().deleteTabelle(new String[]{"DITG"},
                    "CODGAR5 = ? and DITTAO = ? and NGARA5 = ?",
                          new Object[]{codiceGara, codiceDitta, lottodestinazione});

			  }

  			  //Copia ditta
  	          parametri[0] = lottodestinazione;    //NGARA5
              parametri[1] = codiceGara;    //CODGAR5
              parametri[2] = codiceDitta;   //DITTAO

  	          select="select nomimo ";
  	        String dbFunctionDateToString = null;
  	          if(numeroFaseAttiva!=null && !"".equals(numeroFaseAttiva)){
  	            switch(Integer.parseInt(numeroFaseAttiva)){
  	              case GestioneFasiRicezioneFunction.FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE :
  	              dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
  	                new String[] { "dprdom" });
  	                select+=",nprdom, dricind, oradom, mezdom, " + dbFunctionDateToString;
    	            parametri[7] = null;
                    parametri[8] = null;
                    parametri[9] = null;
                    parametri[10] = null;
                    parametri[11] = null;
                    parametri[12] = null;
                    parametri[21] = null;

    	            break;
  	              case GestioneFasiRicezioneFunction.FASE_ELENCO_DITTE_INVITATE :
                    select+=",nprotg, dinvig";
                    parametri[3] = null;
                    parametri[4] = null;
                    parametri[5] = null;
                    parametri[6] = null;
                    parametri[9] = null;
                    parametri[10] = null;
                    parametri[11] = null;
                    parametri[12] = null;
                    parametri[20] = null;
                    parametri[21] = null;
                    break;
  	              case GestioneFasiRicezioneFunction.FASE_RICEZIONE_PLICHI :
  	              dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
                      new String[] { "dproff" });
  	                select+=",nproff, datoff, oraoff, mezoff," + dbFunctionDateToString;
                    parametri[3] = null;
                    parametri[4] = null;
                    parametri[5] = null;
                    parametri[6] = null;
                    parametri[7] = null;
                    parametri[8] = null;
                    parametri[20] = null;
                    break;
  	            }
  	          }
  	          select+=" from ditg where ngara5=? and codgar5=? and dittao=?";

  	          Vector datiDitg = this.sqlManager.getVector(select, new Object[]{numeroGara,codiceGara,codiceDitta});
  	          if(datiDitg!=null && datiDitg.size()>0){
    	          if(numeroFaseAttiva!=null && !"".equals(numeroFaseAttiva)){
    	            Timestamp data = null;
    	            switch(Integer.parseInt(numeroFaseAttiva)){
                    case GestioneFasiRicezioneFunction.FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE :
                      parametri[3] = ((JdbcParametro)datiDitg.get(1)).getStringValue();   //NPRDOM
                      parametri[4] = ((JdbcParametro)datiDitg.get(2)).dataValue();   //DRICIND
                      parametri[5] = ((JdbcParametro)datiDitg.get(3)).getStringValue();   //ORADOM
                      parametri[6] = ((JdbcParametro)datiDitg.get(4)).longValue();   //MEZDOM

                      String dprdomString = ((JdbcParametro)datiDitg.get(5)).getStringValue();
                      if(dprdomString!=null && !"".equals(dprdomString)){
                        Date dprdom = UtilityDate.convertiData(dprdomString, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
                        data =  new Timestamp(dprdom.getTime());
                      }
                      parametri[20] =  data;   //DPRDOM
                      break;
                    case GestioneFasiRicezioneFunction.FASE_ELENCO_DITTE_INVITATE :
                      select+=",nprotg, dinvig";
                      parametri[7] = ((JdbcParametro)datiDitg.get(1)).getStringValue();   //NPROTG
                      parametri[8] = ((JdbcParametro)datiDitg.get(2)).dataValue();   //DINVIG

                      break;
                    case GestioneFasiRicezioneFunction.FASE_RICEZIONE_PLICHI :
                      select+=",nproff, datoff, oraoff, mezoff";
                      parametri[9] = ((JdbcParametro)datiDitg.get(1)).getStringValue();   //NPROFF
                      parametri[10] = ((JdbcParametro)datiDitg.get(2)).dataValue();   //DATOFF
                      parametri[11] = ((JdbcParametro)datiDitg.get(3)).getStringValue();  //ORAOFF
                      parametri[12] = ((JdbcParametro)datiDitg.get(4)).longValue();   //MEZOFF
                      String dproffString = ((JdbcParametro)datiDitg.get(5)).getStringValue();
                      if(dproffString!=null && !"".equals(dproffString)){
                        Date dproff = UtilityDate.convertiData(dproffString, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
                        data =  new Timestamp(dproff.getTime());
                      }
                      parametri[21] =  data;   //DPROFF
                      break;
                  }
                }
  	            parametri[13] = ((JdbcParametro)datiDitg.get(0)).getStringValue();  //NOMIMO
  	          }


  	          //Calcolo NPROG
  	          if(!modalitaNPROGGsuLotti){
  	            nprogg = (Long)this.sqlManager.getObject("select nprogt from edit where codgar4=? and codime=?", new Object[]{codiceGara,codiceDitta});
  	          }else{
  	            // Determino il valore massimo del campo EDIT.NPROGT
    	        // per il codice di destinazione
    	        Long maxNPROGT = (Long) this.geneManager.getSql().getObject(
    	            "select MAX(NPROGT) from EDIT where EDIT.CODGAR4 = ?",
    	              new Object[] { codiceGara });
    	        if (maxNPROGT == null) maxNPROGT = new Long(0);
    	        nprogg = new Long(maxNPROGT.longValue() + contatore);
    	        contatore++;
  	          }
  	          parametri[14] = nprogg;    //NPROGG


  	          parametri[15] = "1";  //CATIMOK
  	          parametri[16] = "1";  //INVGAR

  	          Long tipgar = (Long)this.geneManager.getSql().getObject("select tipgar from torn where codgar=?",new Object[]{codiceGara});
  	          if(tipgar!=null && tipgar.longValue()==1)
  	            parametri[17] = "1";  //INVOFF
  	          else
  	            parametri[17] = null;  //INVOFF

  	          Double impapp = (Double)this.sqlManager.getObject("select impapp from gare where ngara=?", new Object[]{lottodestinazione});
  	          parametri[18] = impapp;  //IMPAPP
  	          parametri[19] = nprogg;  //NUMORDPL

  	          this.sqlManager.update(insert, parametri);

  	          //Viene popolata la IMPRDOCG a partire dalle occorrenze di DOCUMGARA,
              //della gara destinazione, impostando SITUAZDOCI a 2 e PROVENI a 1
              pgManager.inserimentoDocumentazioneDitta(codiceGara, lottodestinazione, codiceDitta);

              //Copia RAGDET
              List listaOccorrenzeDaCopiare = null;
              DataColumnContainer campiDaCopiare = null;

              listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
                  "select * from RAGDET where NGARA = ? and codimp = ?",
                  new Object[] { numeroGara, codiceDitta });

              if (listaOccorrenzeDaCopiare != null
                  && listaOccorrenzeDaCopiare.size() > 0) {
                campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
                    "RAGDET", "select * from RAGDET", new Object[] {});

                for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {

                  campiDaCopiare.setValoriFromMap(
                      ((HashMap) listaOccorrenzeDaCopiare.get(row)), true);
                  campiDaCopiare.getColumn("RAGDET.CODIMP").setChiave(true);
                  campiDaCopiare.getColumn("RAGDET.CODDIC").setChiave(true);
                  campiDaCopiare.getColumn("RAGDET.NUMDIC").setChiave(true);

                  //Si deve calcolare il valore di NUMDIC
                  Long maxNumdic = (Long) this.geneManager.getSql().getObject(
                          "select max(NUMDIC) from RAGDET where CODIMP= ? and CODDIC=?",
                          new Object[] { ((HashMap) listaOccorrenzeDaCopiare.get(row)).get("CODIMP").toString(),
                                  ((HashMap) listaOccorrenzeDaCopiare.get(row)).get("CODDIC").toString()});

                  long newNumdic=1;
                  if (maxNumdic != null && maxNumdic.longValue()>0)
                      newNumdic = maxNumdic.longValue() + 1;

                  campiDaCopiare.setValue("RAGDET.NUMDIC", new Long(newNumdic));
                  campiDaCopiare.setValue("RAGDET.NGARA", lottodestinazione);

                  campiDaCopiare.insert("RAGDET", this.geneManager.getSql());
                }
              }

              //Copia delle occorrenze del generatore attributi
              this.geneManager.copiaOccorrenzeGeneratoreAttributi("DITG"," CODGAR5 = ? and DITTAO = ? and NGARA5 = ? ", new Object[] {codiceGara, codiceDitta, numeroGara },
                  new String[] {"CODGAR5", "DITTAO", "NGARA5" }, new Object[] {codiceGara, codiceDitta, lottodestinazione }, false);
              //Copia delle occorrenze di note avvisi delle entita' copiate
              this.geneManager.copiaOccorrenzeNoteAvvisi("DITG"," CODGAR5 = ? and DITTAO = ? and NGARA5 = ? ", new Object[] {codiceGara, codiceDitta, numeroGara },
                  new String[] {"CODGAR5", "DITTAO", "NGARA5" }, new Object[] {codiceGara, codiceDitta, lottodestinazione }, false);

		    }

			this.getRequest().setAttribute("RISULTATO", "COPIAESEGUITA");

		} catch (SQLException e) {
			this.getRequest().setAttribute("RISULTATO", "ERRORI");
			throw new GestoreException(
					"Errore durante la copia della ditta)",
					"copiaDitta", e);
		}

	}

}
