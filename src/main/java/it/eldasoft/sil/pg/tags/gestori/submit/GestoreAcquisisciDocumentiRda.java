/*
 * Created on Aprile 2022
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
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.gene.ws.objectstorage.ObjectStorageUtils;
import it.eldasoft.gene.ws.objectstorage.UploadInfo;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.bl.erp.AtacWSERPManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPAllegatoType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore per importare le lavorazioni dalle rda
 *
 *
 */
public class GestoreAcquisisciDocumentiRda extends AbstractGestoreChiaveNumerica {

  static Logger               logger                    = Logger.getLogger(GestoreAcquisisciDocumentiRda.class);

  @Override
  public String[] getAltriCampiChiave() {
    return null;
  }

  @Override
  public String getCampoNumericoChiave() {
    return "ID";
  }

  @Override
  public String getEntita() {
    return "GARE";
  }

  public GestoreAcquisisciDocumentiRda() {
    super(false);
  }

  public GestoreAcquisisciDocumentiRda(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }


  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {

  }


  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("preInsert: inizio metodo");

    GestioneWSERPManager gestioneWSERPManager =
        (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager", this.getServletContext(), GestioneWSERPManager.class);
    AtacWSERPManager atacWSERPManager = 
        (AtacWSERPManager) UtilitySpring.getBean("atacWSERPManager", this.getServletContext(), AtacWSERPManager.class);
    
    String tipoWSERP =null;
    WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
    if(configurazione.isEsito()){
      tipoWSERP = configurazione.getRemotewserp();
    }

    ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long syscon = new Long(profilo.getId());
    String[] credenziali = gestioneWSERPManager.wserpGetLogin(syscon, "WSERP");
    String username = credenziali[0];
    String password = credenziali[1];

    WSERPRdaType[] erpSearch = new WSERPRdaType[1];
    erpSearch[0] = new WSERPRdaType();
    /*
        String strSqlInsert="insert into GCAP (NGARA,CONTAF,NORVOC,CODVOC,QUANTI,PREZUN,CLASI1," +
      	      "SOLSIC,SOGRIB,VOCE,IDVOC,UNIMIS,CODCARR,CODRDA,POSRDA,CODCAT,PERCIVA,DATACONS,LUOGOCONS)" +
      	      " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String strSqlInsertEst = "insert into GCAP_EST (DESEST,NGARA,CONTAF) values(?,?,?)";

	    String codiceAppalto = datiForm.getString("CODAPPALTO");
     */
    String codiceGara = datiForm.getString("GARE.CODGAR1");
    Long iterga = datiForm.getLong("TORN.ITERGA");

    WSERPRdaType[] rdaArray = null;
    if("ATAC".equals(tipoWSERP)){
      try {


        List<String>  countDOCUMGARAImportati = this.sqlManager.getListVector("select codgar,norddocg,idprg,iddocdg from DOCUMGARA where codgar = ? and docprov = ?", 
        new Object[] { codiceGara, 1 });
        if (countDOCUMGARAImportati != null && !countDOCUMGARAImportati.isEmpty()) {
          for (int i = 0; i < countDOCUMGARAImportati.size(); i++) {
            String codgar = (String) SqlManager.getValueFromVectorParam(countDOCUMGARAImportati.get(i), 0).getValue();
            codgar = UtilityStringhe.convertiNullInStringaVuota(codgar);
            Long norddocg = SqlManager.getValueFromVectorParam(countDOCUMGARAImportati.get(i), 1).longValue();
            String idprg = (String) SqlManager.getValueFromVectorParam(countDOCUMGARAImportati.get(i), 2).getValue();
            idprg = UtilityStringhe.convertiNullInStringaVuota(idprg);
            Long iddocdg = SqlManager.getValueFromVectorParam(countDOCUMGARAImportati.get(i), 3).longValue();

            UploadInfo oldBucketInfo = ObjectStorageUtils.getUploadInfo("select bucket_ref, bucket_uuid from w_docdig where idprg=? and iddocdig=?",new Object[] {idprg, iddocdg});
            ObjectStorageUtils.deleteFile(oldBucketInfo.bucket_ref(),oldBucketInfo.uuid());
            this.sqlManager.update("delete from W_DOCDIG where idprg = ? and iddocdig = ?", new Object[] {idprg,iddocdg});
            this.sqlManager.update("delete from DOCUMGARA where codgar = ? and norddocg = ?", new Object[] {codgar,norddocg});
            
          }
        }
        
        Long genere = null;
        List<String>  listaGare = this.sqlManager.getListVector("select codgar, codice, genere from v_gare_genere where codgar = ?", 
        new Object[] { codiceGara });
        if (listaGare != null && !listaGare.isEmpty()) {
          for (int i = 0; i < listaGare.size(); i++) {
            String codgar = (String) SqlManager.getValueFromVectorParam(listaGare.get(i), 0).getValue();
            codgar = UtilityStringhe.convertiNullInStringaVuota(codgar);
            String codice = (String) SqlManager.getValueFromVectorParam(listaGare.get(i), 1).getValue();
            codice = UtilityStringhe.convertiNullInStringaVuota(codice);
            
            if(codgar.equals(codice)) {
              genere = SqlManager.getValueFromVectorParam(listaGare.get(i), 2).longValue();
            }
          }
        }
        Boolean insertBando = true;
        Boolean insertInvito = true;
        for (int i = 0; i < listaGare.size(); i++) {
          //ngara = (String) this.getSqlManager().getObject("select valore from debug_alex where id=?",new Object[]{3});
          String codice = (String) SqlManager.getValueFromVectorParam(listaGare.get(i), 1).getValue();
          codice = UtilityStringhe.convertiNullInStringaVuota(codice);
          erpSearch[0].setCodiceGara(codice);
          WSERPRdaResType wserpRdaRes = gestioneWSERPManager.wserpListaFilesRda(username, password, "WSERP", erpSearch );
          String messageInfo = "";
          if(wserpRdaRes.isEsito()) {
            rdaArray  = wserpRdaRes.getRdaArray();
            
            if(rdaArray != null && rdaArray.length > 0){
              
              String insertWDocdig = "insert into W_DOCDIG (idprg,iddocdig,digent,digkey1,digkey2,dignomdoc,digogg) values (?,?,?,?,?,?,?)";
              String insertDocumGara = "insert into DOCUMGARA( ALLMAIL, TIPOLOGIA, NGARA, DESCRIZIONE, IDPRG, IDDOCDG, GRUPPO, NORDDOCG, CODGAR, VALENZA, DOCPROV, NUMORD) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
              
              for (int h = 0; h < rdaArray.length; h++) {
                WSERPRdaType hRda = rdaArray[h];
                WSERPAllegatoType[] allegatoArray = hRda.getAllegatoArray();
                if(allegatoArray != null && allegatoArray.length>0) {
                  for (int i1 = 0; i1 < allegatoArray.length; i1++) {
                    String titolo = allegatoArray[i1].getTitolo();
                    String nome = allegatoArray[i1].getNome();
                    byte[] contenuto = allegatoArray[i1].getContenuto();
                    String tipo = allegatoArray[i1].getTipo();
                    String allMail = "2";
                    Long tipologia = null;
                    Long gruppo = null;
                    
                    switch (iterga.intValue()) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 7:
                      gruppo = Long.valueOf(1);
                      break;
                    case 5:
                    case 6:
                      gruppo = Long.valueOf(6);
                      break;
    
                    default:
                      break;
                    }
                    
                    
                    if("bandoFirmato".equals(tipo) && Boolean.TRUE.equals(insertBando) && Objects.equals(gruppo, Long.valueOf(1))) {
                      if(Objects.equals(gruppo, Long.valueOf(1))) {
                        insertBando = false;
                        titolo = "Bando di Gara";
                      }
                      if(Objects.equals(gruppo, Long.valueOf(6))) {
                        allMail = "1";
                        insertInvito = false;
                        titolo = "Lettera di invito";
                      }
                    } else {
                      titolo = "Allegato "+titolo.split("\\.")[0];
                    }
                    
                    
                    Long maxProgressivoNORDDOCG = (Long) this.getSqlManager().getObject("select max(NORDDOCG) from DOCUMGARA where CODGAR=?",new Object[]{codiceGara});
                    long newProgressivoNORDDOCG = 1;
    
                    if (maxProgressivoNORDDOCG != null && maxProgressivoNORDDOCG.longValue() > 0){
                      newProgressivoNORDDOCG = maxProgressivoNORDDOCG.longValue() + 1;
                    }
    
                    
                    Long maxIDDOCDIG = (Long) this.geneManager.getSql().getObject( "select max(IDDOCDIG) from W_DOCDIG where IDPRG= ?", new Object[] { "PG" });
                    long newIDDOCDIG = 1;
                    if (maxIDDOCDIG != null && maxIDDOCDIG.longValue() > 0){
                      newIDDOCDIG = maxIDDOCDIG.longValue() + 1;
                    }
                    
                    Long numord = (Long) this.geneManager.getSql().getObject( "select max(NUMORD) from DOCUMGARA where CODGAR=? and GRUPPO=?", new Object[] { codiceGara, gruppo });
                    long newnumord = 1;
                    if (numord != null && numord.longValue() > 0){
                      newnumord = numord.longValue() + 1;
                    }
                    
                    tipologia = atacWSERPManager.getTipologiaFromGruppo(codiceGara,gruppo);
                    
                    LobHandler lobHandler = new DefaultLobHandler();
                    this.sqlManager.update(insertWDocdig, new Object[] {"PG",newIDDOCDIG,"DOCUMGARA",codiceGara,newProgressivoNORDDOCG,nome,new SqlLobValue(contenuto, lobHandler)});
                    ObjectStorageUtils.addFile("PG", newIDDOCDIG);
                    this.sqlManager.update(insertDocumGara, new Object[] {allMail,tipologia,codice,titolo,"PG",newIDDOCDIG,gruppo,newProgressivoNORDDOCG,codiceGara,0,1,newnumord});
                    messageInfo += "\n"+codice+": documentazione importata con successo";
                  }
                } else {
                  messageInfo += "\n"+codice+": non presenta ancora documentazione allegata";
                }
              }
              
              
              
            }
            this.getRequest().setAttribute("importRdaEseguito", "1");
            this.getRequest().setAttribute("messageInfo", messageInfo);
            
          }else {
            this.getRequest().setAttribute("importRdaEseguito", "2");
            this.getRequest().setAttribute("messageInfo", messageInfo);
            throw new GestoreException(wserpRdaRes.getMessaggio(), "acquisisci.documenti", new Exception());
          }//esito
        }
        
        if(Boolean.FALSE.equals(insertBando)) {
          //ordinamento Documgara Bando
          String update1 = "update DOCUMGARA set numord = ("
              + "   select numord "
              + "   from documgara "
              + "   where descrizione = 'Bando di Gara' "
              + "   and codgar = ?) "
              + "where codgar = ? "
              + "and numord = 1 ";
          String update2 = "update DOCUMGARA set numord = 1 "
              + "where codgar = ? "
              + "and descrizione = 'Bando di Gara'";
          this.sqlManager.update(update1, new Object[] { codiceGara, codiceGara });
          this.sqlManager.update(update2, new Object[] { codiceGara });
        }
        if(Boolean.FALSE.equals(insertInvito)) {
          //ordinamento Documgara Invito
          String update1 = "update DOCUMGARA set numord = ("
              + "   select numord "
              + "   from documgara "
              + "   where descrizione = 'Lettera di invito' "
              + "   and codgar = ?) "
              + "where codgar = ? "
              + "and numord = 1 ";
          String update2 = "update DOCUMGARA set numord = 1 "
              + "where codgar = ? "
              + "and descrizione = 'Lettera di invito'";
          this.sqlManager.update(update1, new Object[] { codiceGara, codiceGara });
          this.sqlManager.update(update2, new Object[] { codiceGara });
        }
      } catch (SQLException e) {
        this.getRequest().setAttribute("importRdaEseguito", "2");   
        throw new GestoreException("Errore in fase di import delle rda in gara", null, e);
      }
    }//ATAC

    if (logger.isDebugEnabled()) logger.debug("preInsert: fine metodo");

  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {

  }




}