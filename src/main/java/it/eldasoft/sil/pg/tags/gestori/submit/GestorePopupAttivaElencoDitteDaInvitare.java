/*
 * Created on 04/07/16
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
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per la pagina gare-popup-attivaElencoDitteDaInvitare.jsp
 *
 * @author Marcello Caminiti
 */
public class GestorePopupAttivaElencoDitteDaInvitare extends AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestorePopupAttivaElencoDitteDaInvitare.class);

	@Override
  public String getEntita() {
		return "GARE";
	}

  public GestorePopupAttivaElencoDitteDaInvitare() {
	    super(false);
	  }

	/**
	 * @param isGestoreStandard
	*/
  public GestorePopupAttivaElencoDitteDaInvitare(boolean isGestoreStandard) {
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

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	  String  codgar = UtilityStruts.getParametroString(this.getRequest(),"codgar");
	  String  ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
	  String  sortinv = UtilityStruts.getParametroString(this.getRequest(),"sortinv");
	 

      //variabili per tracciatura eventi
      String messageKey = null;
      int livEvento = 3;
      String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
      String msgElencoInvitate="";
      String descrSorteggio="";
      try{
        try{
        	if("1".equals(sortinv)) {
        	  String elencoInvitate="";
    		  Long numope= null;
              List<?> datiTorn = sqlManager.getVector("select sortinv,numope from torn where codgar = ?", new Object[] {codgar});
              if(datiTorn != null && datiTorn.size()>0) {
            	numope =(Long) SqlManager.getValueFromVectorParam(datiTorn, 1).getValue();
              }
    	      String selectCOUNT_DITGAMMIS = "select count(*) from ditgammis where codgar = ? and ngara = ? and dittao = ? and fasgar = ?";
              String insertDITGAMMIS = "insert into ditgammis (codgar, ngara, dittao, fasgar, ammgar) values (?,?,?,?,?)";
              String updateDITGAMMIS = "update ditgammis set ammgar = ? where codgar = ? and ngara = ? and dittao = ? and fasgar = ?";
              String updateDITG = "update ditg set ammgar = ?, fasgar = ? where codgar5 = ? and ngara5 = ? and dittao = ?";

          	  List listaDitteStatoAmmissioneNullo = sqlManager.getListVector( "select vd.dittao from v_ditgammis vd, ditg d"
          	  		+ " where vd.ngara=? and vd.fasgar=? and vd.ammgar is null and vd.ngara=d.ngara5 and vd.dittao =d.dittao"
          	  		+ " and (d.ammgar <> '2' or d.ammgar is null) and (d.fasgar >= ? or d.fasgar is null)",
          	  		new Object[]{ngara,Long.valueOf(-4),Long.valueOf(-4)});
        	  for(int i=0;i<listaDitteStatoAmmissioneNullo.size();i++){
                  String codiceDitta = SqlManager.getValueFromVectorParam(listaDitteStatoAmmissioneNullo.get(i), 0).stringValue();
                  Long count_ditgammis = (Long) this.sqlManager.getObject(selectCOUNT_DITGAMMIS,
                		  new Object[] { codgar, ngara, codiceDitta, Long.valueOf(-4) });
                  if (count_ditgammis != null && count_ditgammis.longValue() > 0) {
                    // Aggiornamento
                    this.sqlManager.update(updateDITGAMMIS, new Object[] { new Long(2), codgar, ngara, codiceDitta, Long.valueOf(-4) });
                  } else {
                    // Inserimento
                    this.sqlManager.update(insertDITGAMMIS, new Object[] { codgar, ngara, codiceDitta, Long.valueOf(-4), Long.valueOf(7) });
                  }
                  //aggiorno anche DITG:
                  this.sqlManager.update(updateDITG, new Object[] { Long.valueOf(2), Long.valueOf(-4), codgar, ngara, codiceDitta });
        	  }
        	  
        	  List listaDitteOrdinataNOI = sqlManager.getListVector( "select dittao, numordinv from ditg"
        	  		+ " where codgar5= ? and ngara5= ? and (ammgar <> '2' or ammgar is null) and (fasgar is null or fasgar >= ?)"
        	  		+ " order by numordinv",new Object[]{codgar,ngara,Long.valueOf(-4)});
        	  if(numope != null) {
	        	  int intNumOpe = numope.intValue();
	        	  for(int j=0;j<listaDitteOrdinataNOI.size();j++){
	        		  String codiceDitta = SqlManager.getValueFromVectorParam(listaDitteOrdinataNOI.get(j), 0).stringValue();
	        		  Long numeroOrdineInvito = SqlManager.getValueFromVectorParam(listaDitteOrdinataNOI.get(j), 1).longValue();
	        		  this.sqlManager.update("update ditg set invgar = ?, sortinv = ? where codgar5 = ? and ngara5 = ? and dittao = ?"
	        				  , new Object[] {"1","1",codgar, ngara, codiceDitta });
	        		  
	        		  if(intNumOpe<j+1) {
	            		  //esclusione
	            		  this.sqlManager.update("update ditg set invgar = ?, sortinv = ? where codgar5 = ? and ngara5 = ? and dittao = ?"
	            				  , new Object[] {"2","2",codgar, ngara, codiceDitta });
	                      Long count_ditgammis = (Long) this.sqlManager.getObject(selectCOUNT_DITGAMMIS,
	                    		  new Object[] { codgar, ngara, codiceDitta, Long.valueOf(-3) });
	                      if (count_ditgammis != null && count_ditgammis.longValue() > 0) {
	                        // Aggiornamento
	                        this.sqlManager.update(updateDITGAMMIS, new Object[] { new Long(2), codgar, ngara, codiceDitta, Long.valueOf(-3) });
	                      } else {
	                        // Inserimento
	                        this.sqlManager.update(insertDITGAMMIS, new Object[] { codgar, ngara, codiceDitta, Long.valueOf(-3), Long.valueOf(2) });
	                      }
	                      //aggiorno anche DITG:
	                      this.sqlManager.update(updateDITG, new Object[] { Long.valueOf(2), Long.valueOf(-3), codgar, ngara, codiceDitta });
	        		  }else {
	        	           if(!"".equals(elencoInvitate)) {
	        	        	   elencoInvitate+=", ";   
	        	           }
	        			  elencoInvitate+=numeroOrdineInvito + " " + codiceDitta;
	        		  }
	        	  }
        	  }
        	  msgElencoInvitate="Ditte da invitare : " + elencoInvitate + " ";
        	  descrSorteggio=" con selezione automatica degli invitati";
        	}//sortinv eq 1 
        	
        	
          this.sqlManager.update("update gare set fasgar=?, stepgar=? where ngara=?", new Object[]{new Long(-3), new Long(-30),ngara});
          livEvento = 1;
          errMsgEvento = "";

        }catch(SQLException e) {
          livEvento = 3;
          errMsgEvento = "Errore durante l'operazione di attivazione dell'elenco ditta da invitare";
          this.getRequest().setAttribute("operazioneEseguita", "ERRORI");
          throw new GestoreException(
                  "Errore durante l'operazione di attivazione dell'elenco ditta da invitare)",
                 null, e);
        }

      }finally{
        //Tracciatura eventi
        try {
          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(ngara);
          logEvento.setCodEvento("GA_ATTIVA_FASE_INVITO");
          logEvento.setDescr("Attivazione fase elenco ditte da invitare"+descrSorteggio);
          logEvento.setErrmsg(msgElencoInvitate + errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          messageKey = "errors.logEventi.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), le);
        }
      }


      this.getRequest().setAttribute("operazioneEseguita", "OK");

	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	}
}
