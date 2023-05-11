package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

public class GestoreG1STIPULA extends AbstractGestoreChiaveIDAutoincrementante {

  private static final Logger logger = Logger.getLogger(GestoreG1STIPULA.class);

  GenChiaviManager genChiaviManager = null;

  @Override
  public String getCampoNumericoChiave() {
    return "ID";
  }

  @Override
  public String getEntita() {
    return "G1STIPULA";
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    super.preInsert(status, datiForm);

    ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long syscon = new Long(profilo.getId());

    //Imposto il codice dell'ordine in base a codifica automatica
    if(geneManager.isCodificaAutomatica("G1STIPULA", "CODSTIPULA")){
      String codStipula = geneManager.calcolaCodificaAutomatica("G1STIPULA", "CODSTIPULA");
      datiForm.setValue("G1STIPULA.CODSTIPULA", codStipula);
    } else {
      String codStipula = datiForm.getString("G1STIPULA.CODSTIPULA");
    }

    Long idStipula = datiForm.getLong("G1STIPULA.ID");
    datiForm.setValue("G1STIPULA.SYSCON", syscon);
    datiForm.setValue("G1STIPULA.STATO", Long.valueOf(1));

    Long idPadre = datiForm.getLong("G1STIPULA.ID_PADRE");
    if (idPadre==null) {
    	datiForm.setValue("G1STIPULA.ID_ORIGINARIO", idStipula);
    }

    datiForm.setValue("G1STIPULA.LIVELLO", Long.valueOf(0));

    //Inserimento in G_PERMESSI solo per l'utente corrente,

    //successivamente viene inserito per ogni occorrenza di g_permessi di gare
    String codstipula = datiForm.getString("G1STIPULA.CODSTIPULA");
    String codice = datiForm.getString("G1STIPULA.NGARA");
    Long ncont = datiForm.getLong("G1STIPULA.NCONT");
    String codgar=null;
    String ngara=null;
    String codimp = null;
    Long modcont = null;

    try {


      Vector<?> datiAggiudicatarioStipula = this.sqlManager.getVector("select codgar,ngara,codimp,modcont from v_aggiudicatari_stipula where codice = ? and ncont = ? ",
	    		new Object[]{codice,ncont});
      if(datiAggiudicatarioStipula!=null && datiAggiudicatarioStipula.size()>0){
	    	codgar =  SqlManager.getValueFromVectorParam(datiAggiudicatarioStipula, 0).getStringValue();
	    	ngara =  SqlManager.getValueFromVectorParam(datiAggiudicatarioStipula, 1).getStringValue();
	        codimp =  SqlManager.getValueFromVectorParam(datiAggiudicatarioStipula, 2).getStringValue();
	        modcont =  (Long) SqlManager.getValueFromVectorParam(datiAggiudicatarioStipula, 3).getValue();
      }

      Long idUtente = new Long(profilo.getId());
      String selectG_PERMESSI = "select syscon,autori,propri from g_permessi where codgar = ? union select "+idUtente +",1,'1' from g_permessi ";
      List<?> datiG_PERMESSI = this.sqlManager.getListVector(selectG_PERMESSI, new Object[] { codgar });

      for (int p = 0; p < datiG_PERMESSI.size(); p++) {
        Long utente  = (Long) SqlManager.getValueFromVectorParam(datiG_PERMESSI.get(p), 0).getValue();
        Long autori  = (Long) SqlManager.getValueFromVectorParam(datiG_PERMESSI.get(p), 1).getValue();
        String propri  = (String) SqlManager.getValueFromVectorParam(datiG_PERMESSI.get(p), 2).getValue();
          // si inserisce l'utente solo se non esiste l'associazione nella
          // G_PERMESSI con l'entita'
          Vector ret = this.sqlManager.getVector(
              "select count(numper) from g_permessi where idstipula"
                  + " = ? and syscon = ?", new Object[] { idStipula, utente });
          if (ret.size() > 0) {
            Long count = SqlManager.getValueFromVectorParam(ret, 0).longValue();
            if (count != null && count.longValue() == 0) {
              // non esiste, quindi tento l'inserimento
              long maxNumper = this.getMaxIdGPermessi() + 1;
              String sql = "insert into g_permessi (numper, syscon, autori, propri, idStipula) values (?, ?, ?, ?, ?)";
              this.sqlManager.update(sql, new Object[] { new Long(maxNumper), utente, autori, propri, idStipula });
            }
          }
      }


    if (idPadre==null) {

    Object[] params = new Object[11];
    params[0] = datiForm.getLong("GARE.TIATTO");
    params[1] = datiForm.getString("GARE.NREPAT");
    params[2] = datiForm.getData("GARE.DAATTO");
    params[3] = datiForm.getString("GARE.RIDISO");
    params[4] = datiForm.getString("GARE.NQUIET");
    params[5] = datiForm.getData("GARE.DQUIET");
    params[6] = datiForm.getString("GARE.ISTCRE");
    params[7] = datiForm.getString("GARE.INDIST");

    params[8] = codgar;
    params[9] = codimp;
    params[10] = ngara;

    if(Long.valueOf(2).equals(modcont)){
	    //Aggiorno tutti i lotti tranne il lotto corrente che viene aggiornato dal
		//gestore
			this.sqlManager.update(
			  			"update GARE set TIATTO = ?, NREPAT = ?, DAATTO = ?, RIDISO = ?," +
			  			                "NQUIET = ?, DQUIET = ?, ISTCRE = ?, INDIST = ? " +
			  			 "where CODGAR1 = ? and DITTA = ? and NGARA <>? and genere is null",
			  			params);

		if (datiForm.getColumn("GARE.RIDISO").isModified()) {
			String ridiso = datiForm.getString("GARE.RIDISO");
			String oldRidiso = datiForm.getColumn("GARE.RIDISO").getOriginalValue().getStringValue();

			if (ridiso==null || "".equals(ridiso)) ridiso="2";
			if (oldRidiso==null || "".equals(oldRidiso)) oldRidiso="2";

			String update="";
			if(!ridiso.equals(oldRidiso)){
				if ("1".equals(ridiso)) update="update gare set impgar = impgar /2 where CODGAR1 = ? and DITTA = ? and genere is null";
				else update="update gare set impgar = impgar * 2 where CODGAR1 = ? and DITTA = ? and genere is null";

				this.sqlManager.update(update, new Object[]{params[8],params[9]});
			}

		}
    }
    }

    } catch (SQLException e) {
    	throw new GestoreException("Errore nell'inserimento della stipula!", null, e);
    }



    DefaultGestoreEntita gestoreGARE = new DefaultGestoreEntita("GARE", this.getRequest());
    gestoreGARE.update(status, datiForm);
    datiForm.setValue("GARECONT.NGARA", codice);
    datiForm.setValue("GARECONT.NCONT", ncont);
    datiForm.setOriginalValue("GARECONT.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO,codice));
    datiForm.setOriginalValue("GARECONT.NCONT", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,ncont));
    DefaultGestoreEntita gestoreGARECONT = new DefaultGestoreEntita("GARECONT", this.getRequest());
    gestoreGARECONT.update(status, datiForm);

  }

  @Override
  public void afterInsertEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager", this.getServletContext(),
        GenChiaviManager.class);


    Long idStipula = datiForm.getLong("G1STIPULA.ID");

    HttpSession session = this.getRequest().getSession();
    String uffint = (String) session.getAttribute("uffint");

	try {
	//Aggiorno i livelli delle stipule con id_origine in comune
	Object[] params = new Object[2];
    params[0] = datiForm.getLong("G1STIPULA.ID_ORIGINARIO");
    params[1] = datiForm.getLong("G1STIPULA.ID");
	this.sqlManager.update(
	  			"update G1STIPULA set LIVELLO = LIVELLO+1" +
	  			 "where ID_ORIGINARIO = ? and ID <> ?",
	  			params);
	}
	catch (SQLException e) {
			throw new GestoreException(
					"Errore nell'aggiornamento livelli della catena di stipula ", null,e);
	}

  }


  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    Long idStipula = null;
    String codStipula = null;
    Long idOriginario = null;
    if(datiForm.isColumn("V_GARE_STIPULA.ID")){
      idStipula =datiForm.getLong("V_GARE_STIPULA.ID");
    }else{
      idStipula =datiForm.getLong("G1STIPULA.ID");
    }

    //Cancellazione della G_PERMESSI

    try {
      codStipula = (String)sqlManager.getObject("select codstipula from g1stipula where id = ?", new Object[]{idStipula});
      sqlManager.update("delete from G_PERMESSI where idstipula = ?", new Object[] { idStipula });
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella cancellazione della G_PERMESSI della stipula con codice=" + codStipula,
          null, e);
    }

    //Gestione livello catena stipule

    try {
      idOriginario = (Long)sqlManager.getObject("select id_originario from g1stipula where id = ?", new Object[]{idStipula});
      sqlManager.update("update G1STIPULA set livello=livello-1 where id_originario = ?", new Object[] { idOriginario });
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell' aggiornamento dei livelli nella G1STIPULA",
          null, e);
    }

    this.geneManager.deleteTabelle(new String[] { "WSFASCICOLO" }, "entita=? and key1 = ? ",
        new Object[] { "G1STIPULA",Long.toString(idStipula) });
    this.geneManager.deleteTabelle(new String[] { "WSDOCUMENTO" }, "entita=? and key1 = ? ",
        new Object[] { "G1STIPULA", Long.toString(idStipula) });

   PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager", this.getServletContext(),
       PgManager.class);
   pgManager.deleteComunicazioni("G1STIPULA", codStipula);
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

	  Long idStipula = datiForm.getLong("G1STIPULA.ID");
      String modcont = this.getRequest().getParameter("modcont");
      String codice = datiForm.getString("G1STIPULA.NGARA");
      Long ncont = datiForm.getLong("G1STIPULA.NCONT");
      Long idPadre = datiForm.getLong("G1STIPULA.ID_PADRE");
	try {
	  if (idPadre==null) {
      Vector<?> datiAggiudicatarioStipula = this.sqlManager.getVector("select codgar,ngara,codimp" +
        		" from v_aggiudicatari_stipula where codice = ? and ncont = ? ", new Object[]{codice,ncont});
        if(datiAggiudicatarioStipula!=null && datiAggiudicatarioStipula.size()>0){
            String codgar =  SqlManager.getValueFromVectorParam(datiAggiudicatarioStipula, 0).getStringValue();
            String ngara =  SqlManager.getValueFromVectorParam(datiAggiudicatarioStipula, 1).getStringValue();
            String codimp =  SqlManager.getValueFromVectorParam(datiAggiudicatarioStipula, 2).getStringValue();

            Object[] params = new Object[11];
            params[0] = datiForm.getLong("GARE.TIATTO");
            params[1] = datiForm.getString("GARE.NREPAT");
            params[2] = datiForm.getData("GARE.DAATTO");
            params[3] = datiForm.getString("GARE.RIDISO");
            params[4] = datiForm.getString("GARE.NQUIET");
            params[5] = datiForm.getData("GARE.DQUIET");
            params[6] = datiForm.getString("GARE.ISTCRE");
            params[7] = datiForm.getString("GARE.INDIST");

            params[8] = codgar;
            params[9] = codimp;
            params[10] = ngara;

            if("2".equals(modcont)){
        	    //Aggiorno tutti i lotti tranne il lotto corrente che viene aggiornato dal
        		//gestore
      			this.sqlManager.update(
      			  			"update GARE set TIATTO = ?, NREPAT = ?, DAATTO = ?, RIDISO = ?," +
      			  			                "NQUIET = ?, DQUIET = ?, ISTCRE = ?, INDIST = ? " +
      			  			 "where CODGAR1 = ? and DITTA = ? and NGARA <>? and genere is null",
      			  			params);

        		if (datiForm.getColumn("GARE.RIDISO").isModified()) {
        			String ridiso = datiForm.getString("GARE.RIDISO");
        			String oldRidiso = datiForm.getColumn("GARE.RIDISO").getOriginalValue().getStringValue();

        			if (ridiso==null || "".equals(ridiso)) ridiso="2";
        			if (oldRidiso==null || "".equals(oldRidiso)) oldRidiso="2";

        			String update="";
        			if(!ridiso.equals(oldRidiso)){
        				if ("1".equals(ridiso)) update="update gare set impgar = impgar /2 where CODGAR1 = ? and DITTA = ? and genere is null";
        				else update="update gare set impgar = impgar * 2 where CODGAR1 = ? and DITTA = ? and genere is null";

        				this.sqlManager.update(update, new Object[]{params[8],params[9]});
        			}

        		}
            }


        }
	  }
	} catch (SQLException e) {
		throw new GestoreException(
				"Errore nell'allineamento dei dati dei lotti aggiudicati dalla stipula ", null,e);
	}

    DefaultGestoreEntita gestoreGARE = new DefaultGestoreEntita("GARE", this.getRequest());
   	gestoreGARE.update(status, datiForm);

    DefaultGestoreEntita gestoreGARECONT = new DefaultGestoreEntita("GARECONT", this.getRequest());
    gestoreGARECONT.update(status, datiForm);

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }


  /**
   * Ritorna l'ultimo id generato per la tabella G_PERMESSI
   *
   * @return ultimo id generato, 0 altrimenti
   * @throws GestoreException
   */
  private long getMaxIdGPermessi() throws GestoreException {
    long id = 0;
    try {
      Vector ret = this.sqlManager.getVector(
          "select max(numper) from g_permessi", new Object[] {});
      if (ret.size() > 0) {
        Long count = SqlManager.getValueFromVectorParam(ret, 0).longValue();
        if (count != null && count.longValue() > 0) {
          id = count.longValue();
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'estrazione dell'ultimo id utilizzato nella G_PERMESSI",
          "getMaxIdPermessi", e);
    }
    return id;
  }

}
