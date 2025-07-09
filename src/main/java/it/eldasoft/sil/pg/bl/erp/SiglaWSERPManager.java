/*
 * Created on 23/11/2020
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.erp;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.erp.WSERPAllegatoType;
import it.maggioli.eldasoft.ws.erp.WSERPAnagraficaType;
import it.maggioli.eldasoft.ws.erp.WSERPGaraType;
import it.maggioli.eldasoft.ws.erp.WSERPOdaType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;

import java.sql.SQLException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;


public class SiglaWSERPManager {
  /** Logger */
  static Logger               logger                = Logger.getLogger(SiglaWSERPManager.class);

  private SqlManager          sqlManager;

  private GestioneWSERPManager gestioneWSERPManager;

  private FileAllegatoManager      fileAllegatoManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }

  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  public WSERPRdaResType  inviaDatiContratto (String username, String password, HashMap datiMask) throws GestoreException, SQLException{
    WSERPRdaResType wserpRdaRes = new WSERPRdaResType();
    String codgar = (String)datiMask.get("codgar");
    String ngara = (String)datiMask.get("ngara");
    String oggettoGara = (String)datiMask.get("oggettoGara");
    Double iaggiu = (Double)datiMask.get("iaggiu");
    String tipoContratto = (String)datiMask.get("tipoContratto");
    
    String tipoNorma = (String)datiMask.get("tipoNorma");
    String proceduraAmministrativa = (String)datiMask.get("proceduraAmministrativa");
    String tipoOrgano = (String)datiMask.get("tipoOrgano");
    

    WSERPGaraType datiGara = new WSERPGaraType();
    WSERPAnagraficaType[] anagraficaTypes = new WSERPAnagraficaType[2];
    anagraficaTypes[0] = new WSERPAnagraficaType();
    anagraficaTypes[1] = new WSERPAnagraficaType();
    WSERPOdaType[] odaArray = new WSERPOdaType[1];
    odaArray[0] = new WSERPOdaType();
    try {
      
      oggettoGara = UtilityStringhe.convertiNullInStringaVuota(oggettoGara);
      if(oggettoGara.length()>120){
        oggettoGara = oggettoGara.substring(0, 119);
      }
      
      String codiga = (String)this.sqlManager.getObject(
          "select codiga from gare where ngara = ?", new Object[] { ngara });
      
      Long genere  = (Long)this.sqlManager.getObject(
          "select genere from v_gare_genere where codgar = ? and codice = ?", new Object[] { codgar, ngara });
      
      String codfisPivaFirmatarioExt = (String)this.sqlManager.getObject(
          "select t.cftec from GARTECNI gt "
              + "inner join tecni t on gt.codtec = t.codtec "
              + "where gt.inctec = 2 and gt.CODGAR = ?", new Object[] { codgar });
      codfisPivaFirmatarioExt = UtilityStringhe.convertiNullInStringaVuota(codfisPivaFirmatarioExt);
      anagraficaTypes[1].setCodiceFiscale(codfisPivaFirmatarioExt);
      anagraficaTypes[1].setIncarico(Long.valueOf(2));

      Vector<?> datiGare = this.sqlManager.getVector("select t.CODRUP, g.ditta, g.not_gar, concat(g.TATTOG,g.DATTOG,g.NATTOG) as dsAtto, g.DAATTO, g.cupprg, g.CODCIG, t.modcont "
          + "FROM TORN t inner join gare g on t.CODGAR = g.CODGAR1 "
          + "WHERE t.CODGAR = ? and g.ditta is not null", new Object[] { codgar });

      if(datiGare!=null && !datiGare.isEmpty()){
        Long isModalitaStipulaContrattoPerAggiudicatario = (Long) SqlManager.getValueFromVectorParam(datiGare, 7).getValue();
        String codrupForTECNI = (String) SqlManager.getValueFromVectorParam(datiGare, 0).getValue();
        codrupForTECNI = UtilityStringhe.convertiNullInStringaVuota(codrupForTECNI);
        String codDittaForIMPR = (String) SqlManager.getValueFromVectorParam(datiGare, 1).getValue();
        codDittaForIMPR = UtilityStringhe.convertiNullInStringaVuota(codDittaForIMPR);
        if(!"".equals(codDittaForIMPR)) {
          String codfisPivaAggiudicatarioExt = (String)this.sqlManager.getObject(
              "select cfimp from impr where codimp = ?", new Object[] { codDittaForIMPR });
          codfisPivaAggiudicatarioExt = UtilityStringhe.convertiNullInStringaVuota(codfisPivaAggiudicatarioExt);
          odaArray[0].setCfFornitore(codfisPivaAggiudicatarioExt);
          if(isModalitaStipulaContrattoPerAggiudicatario!= null && !Long.valueOf(2).equals(isModalitaStipulaContrattoPerAggiudicatario)) {
            odaArray[0].setNumeroGara(ngara);
            String codcig = (String) SqlManager.getValueFromVectorParam(datiGare, 6).getValue();
            codcig = UtilityStringhe.convertiNullInStringaVuota(codcig);
            datiGara.setCodiceCig(codcig);
          } else {
            String query = "select ngara,codcig from gare where ditta = ? and codgar1 = ?";
            List<?> ngareEcig = this.sqlManager.getListVector(query, new Object[] { codDittaForIMPR, codgar });
            if(ngareEcig != null && !ngareEcig.isEmpty()) {
              String codiciLotto = "";
              String codiciCig = "";
              for (int i = 0; i < ngareEcig.size(); i++){
                String codiceLotto = SqlManager.getValueFromVectorParam(ngareEcig.get(i), 0).stringValue();
                codiceLotto = UtilityStringhe.convertiNullInStringaVuota(codiceLotto);
                String codiceCig = SqlManager.getValueFromVectorParam(ngareEcig.get(i), 1).stringValue();
                codiceCig = UtilityStringhe.convertiNullInStringaVuota(codiceCig);
                if(!"".equals(codiceLotto)) {
                  codiciLotto += SqlManager.getValueFromVectorParam(ngareEcig.get(i), 0).stringValue()+",";                  
                }
                if(!"".equals(codiceCig)) {
                  codiciCig += SqlManager.getValueFromVectorParam(ngareEcig.get(i), 1).stringValue()+",";                  
                }
              }
              if(!"".equals(codiciLotto)) {
                odaArray[0].setNumeroGara(codiciLotto.substring(0, codiciLotto.length()-1));
              }
              if(!"".equals(codiciCig)) {
                datiGara.setCodiceCig(codiciCig.substring(0, codiciCig.length()-1));
              }
            }
          }
        }
        
        oggettoGara = (String) SqlManager.getValueFromVectorParam(datiGare, 2).getValue();
        oggettoGara = UtilityStringhe.convertiNullInStringaVuota(oggettoGara);
        
        String ds_atto = (String) SqlManager.getValueFromVectorParam(datiGare, 3).getValue();
        ds_atto = UtilityStringhe.convertiNullInStringaVuota(ds_atto);
        if("".equals(ds_atto)) {
          ds_atto = "non disponibile";
        }
        datiGara.setNumeroAttoAggiudicazione(ds_atto);

        Date dt_stipula = (Date) SqlManager.getValueFromVectorParam(datiGare, 4).getValue();
        if(dt_stipula != null){
          Calendar calDt_stipula = Calendar.getInstance();
          calDt_stipula.setTime(dt_stipula);
          datiGara.setDataContratto(calDt_stipula);
        }
        
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiGare, 5).getValue();
        cupprg = UtilityStringhe.convertiNullInStringaVuota(cupprg);
        String[] codiceCupArray = new String[1];
        codiceCupArray[0] = cupprg;
        datiGara.setCodiceCupArray(codiceCupArray);
        /*
          String codcig = (String) SqlManager.getValueFromVectorParam(datiGare, 6).getValue();
          codcig = UtilityStringhe.convertiNullInStringaVuota(codcig);
          datiGara.setCodiceCig(codcig);
         */
        String codfisPivaRupExt = (String)this.sqlManager.getObject(
            "select cftec from TECNI where codtec = ?", new Object[] { codrupForTECNI });
        codfisPivaRupExt = UtilityStringhe.convertiNullInStringaVuota(codfisPivaRupExt);
        anagraficaTypes[0].setCodiceFiscale(codfisPivaRupExt);
        anagraficaTypes[0].setIncarico(Long.valueOf(1));


        Vector<?> datiGarecont  = this.sqlManager.getVector(
            "select DVERBC,DCERTU,DREGCO from GARECONT gc,GARE ga "
                + "where gc.codimp = ga.ditta and ((gc.ngara=ga.ngara and gc.ncont=1) or "
                + "(gc.ngara=ga.codgar1 and (gc.ngaral is null or gc.ngaral=ga.ngara))) "
                + "and ga.ditta is not null and ga.ngara= ? and ga.ditta = ?", new Object[] { ngara, codDittaForIMPR });

        if(datiGarecont!=null && !datiGarecont.isEmpty()){
          Date dtDVERBC = (Date) SqlManager.getValueFromVectorParam(datiGarecont, 0).getValue();
          Date dtDCERTU = (Date) SqlManager.getValueFromVectorParam(datiGarecont, 1).getValue();
          Date dtDREGCO = (Date) SqlManager.getValueFromVectorParam(datiGarecont, 2).getValue();
          if(dtDVERBC != null){
            Calendar calDtDVERBC = Calendar.getInstance();
            calDtDVERBC.setTime(dtDVERBC);
            datiGara.setDataInizioValidita(calDtDVERBC);
          }
          if(dtDCERTU != null){
            Calendar calDtDCERTU = Calendar.getInstance();
            calDtDCERTU.setTime(dtDCERTU);
            datiGara.setDataFineValidita(calDtDCERTU);
          }
          if(dtDREGCO != null){
            Calendar calDtDREGCO = Calendar.getInstance();
            calDtDREGCO.setTime(dtDREGCO);
            datiGara.setDataRegContratto(calDtDREGCO);
          }
        }
      }
      //provvisorio definire oggettogara
      datiGara.setDefinizioneCig(oggettoGara);
      datiGara.setImportoAggiudicazione(iaggiu);
      datiGara.setTecniciArray(anagraficaTypes);
      datiGara.setTipoContratto(tipoContratto);
      datiGara.setTipoNorma(tipoNorma);
      datiGara.setTipoOrgano(tipoOrgano);
      datiGara.setProceduraAmministrativa(proceduraAmministrativa);

      List<?> listaDatiDocAllegati = null;
      if(genere != null && Long.valueOf(2).equals(genere)) {
        listaDatiDocAllegati = sqlManager.getListVector("select w.dignomdoc, w.digtipdoc, w.idprg, w.iddocdig "
            + "from w_docdig w "
            + "inner join c0oggass c on w.idprg = c.c0aprg and w.digkey1 = CAST(c.c0acod AS VARCHAR(10)) "
            + "where w.idprg= ? "
            + "and c.c0akey1 = ? "
            + "and w.digent = ? "
            + "and trim(c.c0atipo) = ? ",
            new Object[] {"PG",ngara,"C0OGGASS", "100"});
      } else {
        listaDatiDocAllegati = sqlManager.getListVector("select w.dignomdoc, w.digtipdoc, w.idprg, w.iddocdig "
            + "from w_docdig w "
            + "inner join c0oggass c on w.idprg = c.c0aprg and w.digkey1 = CAST(c.c0acod AS VARCHAR(10)) "
            + "where w.idprg= ? "
            + "and c.c0akey1 = ? "
            + "and w.digent = ? "
            + "and trim(c.c0atipo) = ? "
            + "and c.c0akey2 = ? ",
            new Object[] {"PG",codgar,"C0OGGASS", "100", codiga});
      }
      
      
      if(listaDatiDocAllegati != null && !listaDatiDocAllegati.isEmpty()){
        WSERPAllegatoType[] allegatoArray = new WSERPAllegatoType[listaDatiDocAllegati.size()];
        for (int i = 0; i < listaDatiDocAllegati.size(); i++){
          WSERPAllegatoType allegato = new WSERPAllegatoType();
          String nomdoc = SqlManager.getValueFromVectorParam(listaDatiDocAllegati.get(i), 0).stringValue();
          nomdoc = UtilityStringhe.convertiNullInStringaVuota(nomdoc);
          String tipdoc = SqlManager.getValueFromVectorParam(listaDatiDocAllegati.get(i), 1).stringValue();
          tipdoc = UtilityStringhe.convertiNullInStringaVuota(tipdoc);
          String idprg = SqlManager.getValueFromVectorParam(listaDatiDocAllegati.get(i), 2).stringValue();
          Long iddocdig = (Long) SqlManager.getValueFromVectorParam(listaDatiDocAllegati.get(i), 3).getValue();

          BlobFile digogg = fileAllegatoManager.getFileAllegato(idprg, iddocdig);
          allegato.setNome(nomdoc);
          allegato.setContenuto(Base64.getEncoder().encode(digogg.getStream()));
          allegato.setTipo(tipdoc);
          
          allegatoArray[i] = allegato;
        }
        datiGara.setAllegatoArray(allegatoArray);
      }
    } catch (Exception e) {
      throw new GestoreException("Errore nel recupero dei documenti dell'aggiudicatario ", null, e);
    }

    wserpRdaRes = this.gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, "WSERP", odaArray, datiGara);
    return wserpRdaRes;
  }


}


