/*
 * Created on 21/02/18
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.bl.erp.AmiuWSERPManager;
import it.eldasoft.sil.pg.bl.erp.AtacWSERPManager;
import it.eldasoft.sil.pg.bl.erp.CavWSERPManager;
import it.eldasoft.sil.pg.bl.erp.RaiwayWSERPManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityMath;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreResType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreType;
import it.maggioli.eldasoft.ws.erp.WSERPGaraType;
import it.maggioli.eldasoft.ws.erp.WSERPOdaType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore della popup della comunicazione dell'esito
 * delle rda in gara
 *
 * @author Cristian Febas
 */

public class GestorePopupComunicaEsitoRda extends
AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestorePopupComunicaEsitoRda.class);

  /** Manager Integrazione WSERP */
  private GestioneWSERPManager gestioneWSERPManager;

  @Override
  public String getEntita() {
    return "GARE";
  }

  public GestorePopupComunicaEsitoRda() {
    super(false);
  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
        this.getServletContext(), GestioneWSERPManager.class);

    String servizio = null;
    String username = null;
    String password = null;
    String ngara = null;

    String tipoWSERP = "";


    String infoFornitore = "";
    String dataCreazioneFornitore = "";
    Long statoFornitore = null;



    boolean isGaraLottiConOffertaUnica = false;
    String tmp = this.getRequest().getParameter("isGaraLottiConOffertaUnica");
    if(tmp == null || "".equals(tmp))
      tmp = (String) this.getRequest().getAttribute("isGaraLottiConOffertaUnica");
    if(tmp != null && !"".equals(tmp))
      isGaraLottiConOffertaUnica = true;

    try {
      ngara = impl.getString("NGARA");
      String codcig = impl.getString("CODCIG");
      codcig= UtilityStringhe.convertiNullInStringaVuota(codcig);
      String codgar = impl.getString("CODGAR1");
      String ditta = impl.getString("DITTA");
      Double impapp = impl.getDouble("IMPAPP");
      Double iaggiu = impl.getDouble("IAGGIU");
      Double ribagg = impl.getDouble("RIBAGG");
      Long modlicg = impl.getLong("MODLICG");
      String cupprg = impl.getString("CUPPRG");
      String idFornitore = null;
      String gruppoConti = null;
      String condizioniPagamento = null;
      String modalitaPagamento = null;
      String oggettoGara = null;
      String tipoContratto = null;
      Date daatto= null;
      String nrepat = null;
      String dittaRTI = "";

      int esitoComunicazioneRda = 0;
      String accqua = null;
      Long aqoper = null;
      Long aqnumope = null;
      Long tipgen = null;
      String codcigaq = null;
      String[] listaDitte = null;

      WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
      if(configurazione.isEsito()){
        tipoWSERP = configurazione.getRemotewserp();
        servizio = "WSERP";
        ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        Long syscon = new Long(profilo.getId());
        String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);
        username = credenziali[0];
        password = credenziali[1];

        if("CAV".equals(tipoWSERP)){
          CavWSERPManager cavWSERPManager = (CavWSERPManager) UtilitySpring.getBean("cavWSERPManager",
              this.getServletContext(), CavWSERPManager.class);
          HashMap datiMask = new HashMap();
          idFornitore = impl.getString("IDFORNITORE");
          idFornitore = UtilityStringhe.convertiNullInStringaVuota(idFornitore);
          gruppoConti = impl.getString("GRUPPO_CONTI");
          gruppoConti = UtilityStringhe.convertiNullInStringaVuota(gruppoConti);
          condizioniPagamento = impl.getString("COND_PAG");
          condizioniPagamento = UtilityStringhe.convertiNullInStringaVuota(condizioniPagamento);
          modalitaPagamento = impl.getString("MODO_PAG");
          modalitaPagamento = UtilityStringhe.convertiNullInStringaVuota(modalitaPagamento);
          oggettoGara = impl.getString("NOT_GAR");
          oggettoGara = UtilityStringhe.convertiNullInStringaVuota(oggettoGara);
          tipoContratto = impl.getString("TIPO_CONTR");
          tipoContratto = UtilityStringhe.convertiNullInStringaVuota(tipoContratto);


          datiMask.put("idFornitore", idFornitore);
          datiMask.put("gruppoConti", gruppoConti);
          datiMask.put("condizioniPagamento", condizioniPagamento);
          datiMask.put("modalitaPagamento", modalitaPagamento);
          datiMask.put("oggettoGara", oggettoGara);
          datiMask.put("tipoContratto", tipoContratto);
          datiMask.put("ditta", ditta);
          WSERPFornitoreResType wserpFornitoreRes = cavWSERPManager.inviaDatiFornitore(username,password,datiMask);

          if(wserpFornitoreRes.isEsito()){
            WSERPFornitoreType newFornitore = wserpFornitoreRes.getFornitore();
            String newIdFornitore = newFornitore.getIdFornitore();
            idFornitore = newIdFornitore;
            idFornitore = UtilityStringhe.convertiNullInStringaVuota(idFornitore);
            if(!"".equals(idFornitore)){
              //occorre verificare se si tratta di una RTI
			  String mandataria = null;
              Long tipimp = (Long)this.sqlManager.getObject("select tipimp from impr where codimp = ?", new Object[]{ditta});
              if(tipimp != null && (tipimp.longValue()==3 || tipimp.longValue()==10)){
                mandataria = (String)this.sqlManager.getObject("select coddic from ragimp" +
                    " where codime9 = ? and impman = ? ", new Object[]{ditta, "1"});
              }
				if(!"".equals(StringUtils.stripToEmpty(mandataria))) {
				  this.sqlManager.update("update impr set cgenimp = ? where codimp = ? and cgenimp is null", new Object[] {idFornitore, mandataria});
				} else {
				  this.sqlManager.update("update impr set cgenimp = ? where codimp = ? and cgenimp is null", new Object[] {idFornitore, ditta});	                      
				}
            }
          }else{
            String comunicazione = "";
            this.getRequest().setAttribute("RISULTATO", "ERRORI");
            throw new GestoreException(
                "Errore durante la creazione del fornitore nell' ERP",
                "creaFornitoreErp",new Object[] {comunicazione + wserpFornitoreRes.getMessaggio()}, null);
          }

        }else if("RAIWAY".equals(tipoWSERP)){
          //inserire la parte relativa al fornitore
          RaiwayWSERPManager raiwayWSERPManager = (RaiwayWSERPManager) UtilitySpring.getBean("raiwayWSERPManager",
              this.getServletContext(), RaiwayWSERPManager.class);
          HashMap datiMask = new HashMap();
          idFornitore = impl.getString("IDFORNITORE");
          idFornitore = UtilityStringhe.convertiNullInStringaVuota(idFornitore);


          datiMask.put("idFornitore", idFornitore);
          datiMask.put("ditta", ditta);
          ditta =StringUtils.stripToEmpty(ditta);
          if(!"".equals(ditta)) {
            WSERPFornitoreResType wserpFornitoreRes = raiwayWSERPManager.inviaDatiFornitore(null,null,datiMask);
            if(wserpFornitoreRes.isEsito()){
              WSERPFornitoreType newFornitore = wserpFornitoreRes.getFornitore();
              String newIdFornitore = newFornitore.getIdFornitore();
              idFornitore = newIdFornitore;
              idFornitore = UtilityStringhe.convertiNullInStringaVuota(idFornitore);
              if(!"".equals(idFornitore)){
                //occorre verificare se si tratta di una RTI
                Long tipimp = (Long)this.sqlManager.getObject("select tipimp from impr where codimp = ?", new Object[]{ditta});
                if(tipimp != null && (tipimp.longValue()==3 || tipimp.longValue()==10)){
                  String mandataria = (String)this.sqlManager.getObject("select coddic from ragimp" +
                      " where codime9 = ? and impman = ? ", new Object[]{ditta, "1"});
                  ditta= mandataria;
                }
                this.sqlManager.update("update impr set cgenimp = ? where codimp = ? and cgenimp is null", new Object[] {idFornitore, ditta});
              }
            }else{
              String comunicazione = "";
              this.getRequest().setAttribute("RISULTATO", "ERRORI");
              throw new GestoreException(
                  "Errore durante la creazione del fornitore nell' ERP",
                  "creaFornitoreErp",new Object[] {comunicazione + wserpFornitoreRes.getMessaggio()}, null);
            }
          }
        }else{
          if("AVM".equals(tipoWSERP) || "TPER".equals(tipoWSERP)){
            idFornitore = impl.getString("IDFORNITORE");
            idFornitore = UtilityStringhe.convertiNullInStringaVuota(idFornitore);
            gruppoConti = impl.getString("GRUPPO_CONTI");
            gruppoConti = UtilityStringhe.convertiNullInStringaVuota(gruppoConti);
            condizioniPagamento = impl.getString("COND_PAG");
            condizioniPagamento = UtilityStringhe.convertiNullInStringaVuota(condizioniPagamento);
            modalitaPagamento = impl.getString("MODO_PAG");
            modalitaPagamento = UtilityStringhe.convertiNullInStringaVuota(modalitaPagamento);
            oggettoGara = impl.getString("OGG_GARA");
            oggettoGara = UtilityStringhe.convertiNullInStringaVuota(oggettoGara);
            tipoContratto = impl.getString("TIPO_CONTR");
            tipoContratto = UtilityStringhe.convertiNullInStringaVuota(tipoContratto);


            if("".equals(idFornitore) || "TPER".equals(tipoWSERP)){
              WSERPFornitoreType fornitore = new WSERPFornitoreType();
              //recupero i dati da  DITTA
              if (ditta != null) {
                Long tipimp = (Long)this.sqlManager.getObject("select tipimp from impr where codimp = ?", new Object[]{ditta});
                if(tipimp != null && (tipimp.longValue()==3 || tipimp.longValue()==10)){
                  String mandataria = (String)this.sqlManager.getObject("select coddic from ragimp" +
                      " where codime9 = ? and impman = ? ", new Object[]{ditta, "1"});
                  dittaRTI = ditta;
                  ditta= mandataria;
                }
                String selectIMPR = "select cfimp,pivimp,nomimp,indimp,locimp,nazimp,capimp,nciimp,proimp," +
                    "emai2ip,telimp,emaiip,telcel,iscrcciaa,faximp,coorba" +
                    " from impr where codimp = ?";
                List<?> datiIMPR = sqlManager.getListVector(selectIMPR, new Object[] { ditta });
                if (datiIMPR != null && datiIMPR.size() > 0) {
                  // Dati AC
                  String cfimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 0).getValue();
                  cfimp = UtilityStringhe.convertiNullInStringaVuota(cfimp);
                  String pivimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 1).getValue();
                  pivimp = UtilityStringhe.convertiNullInStringaVuota(pivimp);
                  String nomimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 2).getValue();
                  String indimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 3).getValue();
                  indimp = UtilityStringhe.convertiNullInStringaVuota(indimp);
                  String locimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 4).getValue();
                  Long nazimp = (Long) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 5).getValue();
                  String siglanaz = "";//"Non presente";
                  if(nazimp != null){
                    if(new Long(1).equals(nazimp)){
                      siglanaz = "IT";
                    }else{
                      siglanaz = (String) this.sqlManager.getObject("select tab2tip from tab2 where tab2cod= ?" +
                          " and tab2d1 =? ", new Object[]{"UBUY1",nazimp.toString()});
                    }
                  }
                  String capimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 6).getValue();
                  String nciimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 7).getValue();
                  nciimp = UtilityStringhe.convertiNullInStringaVuota(nciimp);
                  String proimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 8).getValue();
                  proimp = UtilityStringhe.convertiNullInStringaVuota(proimp);
                  String pecimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 9).getValue();
                  pecimp = UtilityStringhe.convertiNullInStringaVuota(pecimp);
                  if("".equals(pecimp)){
                    pecimp = "";//"Non presente";
                  }
                  String telimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 10).getValue();
                  String cellimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 12).getValue();
                  telimp = UtilityStringhe.convertiNullInStringaVuota(telimp);
                  cellimp = UtilityStringhe.convertiNullInStringaVuota(cellimp);
                  if("".equals(telimp)){
                    if("".equals(cellimp)){
                      telimp = "";//"Non presente";
                    }else{
                      telimp = cellimp;
                    }
                  }
                  String mailimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 11).getValue();
                  mailimp = UtilityStringhe.convertiNullInStringaVuota(mailimp);
                  if("".equals(mailimp)){
                    mailimp = "";//"Non presente";
                  }
                  String iscrcciaa = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 13).getValue();
                  iscrcciaa = UtilityStringhe.convertiNullInStringaVuota(iscrcciaa);
                  Boolean iscrCCIAABool = false;
                  if("1".equals(iscrcciaa)){
                    iscrCCIAABool = true;
                  }

                  String faximp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 14).getValue();
                  faximp = UtilityStringhe.convertiNullInStringaVuota(faximp);
                  if("".equals(faximp)){
                    faximp = "";//"Non presente";
                  }

                  String iban = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 15).getValue();
                  iban = UtilityStringhe.convertiNullInStringaVuota(iban);

                  if("TPER".equals(tipoWSERP) && !"".equals(idFornitore)){
                    fornitore.setCodiceFornitore(idFornitore);
                  }

                  fornitore.setCodiceFiscale(cfimp);
                  fornitore.setPartitaIva(pivimp);
                  fornitore.setRagioneSociale(nomimp);
                  fornitore.setLocalita(locimp);
                  fornitore.setIndirizzo(indimp);
                  fornitore.setCap(capimp);
                  fornitore.setProvincia(proimp);
                  fornitore.setCivico(nciimp);
                  fornitore.setPec(pecimp);
                  fornitore.setTelefono(telimp);
                  fornitore.setEmail(mailimp);
                  fornitore.setNazionalita(siglanaz);
                  fornitore.setGruppoConti(gruppoConti);
                  fornitore.setIscrizioneCCIAA(iscrCCIAABool);
                  fornitore.setCellulare(cellimp);
                  fornitore.setFax(faximp);
                  fornitore.setCondizionePagamento(condizioniPagamento);
                  fornitore.setModalitaPagamento(modalitaPagamento);
                }
                //chiamo il Crea Fornitore

                WSERPFornitoreResType wserpFornitoreRes = gestioneWSERPManager.wserpCreaFornitore(username, password, servizio, fornitore);
                infoFornitore  = wserpFornitoreRes.getMessaggio();
                infoFornitore = UtilityStringhe.convertiNullInStringaVuota(infoFornitore);
                statoFornitore = wserpFornitoreRes.getStato();
                if(wserpFornitoreRes.isEsito()){
                  WSERPFornitoreType newFornitore = wserpFornitoreRes.getFornitore();
                  String newIdFornitore = newFornitore.getIdFornitore();
                  idFornitore = newIdFornitore;
                  idFornitore = UtilityStringhe.convertiNullInStringaVuota(idFornitore);
                  if("TPER".equals(tipoWSERP) && !"".equals(idFornitore)){
                    this.sqlManager.update("update impr set cgenimp = ? where codimp = ? ", new Object[] {idFornitore, ditta});
                  }
                  if(infoFornitore.length()>=8){
                    dataCreazioneFornitore = infoFornitore.substring(0,8);
                    dataCreazioneFornitore = UtilityStringhe.convertiNullInStringaVuota(dataCreazioneFornitore);
                  }
                }else{
                  String comunicazione = "";
                  this.getRequest().setAttribute("RISULTATO", "ERRORI");
                  throw new GestoreException(
                      "Errore durante la creazione del fornitore nell' ERP",
                      "creaFornitoreErp",new Object[] {comunicazione + wserpFornitoreRes.getMessaggio()}, null);
                }

              }
            }else{
              //ditta = idFornitore;
              ;
            }

          }//END IF AVM

        }//not CAV

      }// END CONFIGURAZIONE TIPO WSERP

      //PROVVISORIO
      String db = ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
      if("CAV".equals(tipoWSERP)){
        CavWSERPManager cavWSERPManager = (CavWSERPManager) UtilitySpring.getBean("cavWSERPManager",
            this.getServletContext(), CavWSERPManager.class);
        HashMap datiMask = new HashMap();
        /*
                idFornitore = impl.getString("IDFORNITORE");
                idFornitore = UtilityStringhe.convertiNullInStringaVuota(idFornitore);
                gruppoConti = impl.getString("GRUPPO_CONTI");
                gruppoConti = UtilityStringhe.convertiNullInStringaVuota(gruppoConti);
                condizioniPagamento = impl.getString("COND_PAG");
                condizioniPagamento = UtilityStringhe.convertiNullInStringaVuota(condizioniPagamento);
                modalitaPagamento = impl.getString("MODO_PAG");
                modalitaPagamento = UtilityStringhe.convertiNullInStringaVuota(modalitaPagamento);
         */
        oggettoGara = impl.getString("NOT_GAR");
        oggettoGara = UtilityStringhe.convertiNullInStringaVuota(oggettoGara);
        tipoContratto = impl.getString("TIPO_CONTR");
        tipoContratto = UtilityStringhe.convertiNullInStringaVuota(tipoContratto);
        daatto = impl.getData("GARE.DAATTO");
        nrepat = impl.getString("GARE.NREPAT");
        codcigaq = impl.getString("CODCIGAQ");
        codcigaq= UtilityStringhe.convertiNullInStringaVuota(codcigaq);

        datiMask.put("codgar", codgar);
        datiMask.put("ngara", ngara);
        datiMask.put("codcig", codcig);
        datiMask.put("codcigaq", codcigaq);
        datiMask.put("impapp", impapp);
        datiMask.put("iaggiu", iaggiu);
        datiMask.put("ribagg", ribagg);
        datiMask.put("modlicg", modlicg);
        datiMask.put("idFornitore", idFornitore);
        datiMask.put("gruppoConti", gruppoConti);
        datiMask.put("condizioniPagamento", condizioniPagamento);
        datiMask.put("modalitaPagamento", modalitaPagamento);
        datiMask.put("oggettoGara", oggettoGara);
        datiMask.put("tipoContratto", tipoContratto);
        datiMask.put("ditta", ditta);
        datiMask.put("daatto", daatto);
        datiMask.put("nrepat", nrepat);
        WSERPRdaResType wserpRdaRes = cavWSERPManager.inviaDatiContratto(username, password, datiMask);
        if(!wserpRdaRes.isEsito()){
          esitoComunicazioneRda++ ;
          if("wserp.erp.mancataValorizzazioneRda.error".equals(wserpRdaRes.getMessaggio())){
            UtilityStruts.addMessage(this.getRequest(), "error",
                "wserp.erp.mancataValorizzazioneRda.error",
                new Object[] { idFornitore});
          }else{
            if("wserp.erp.importoRdaOffertoDiversi.warning".equals(wserpRdaRes.getMessaggio())){
              UtilityStruts.addMessage(this.getRequest(), "warning",
                  "wserp.erp.importoRdaOffertoDiversi.warning",
                  new Object[] {idFornitore});

            }else{
              UtilityStruts.addMessage(this.getRequest(), "warning",
                  "wserp.erp.mancataIntegrazioneMultiplaNoDitta.warning",
                  new Object[] {wserpRdaRes.getMessaggio()});
            }
          }
        }else{
          //aggiorno il numero Rdo
          String codRdo = wserpRdaRes.getMessaggio();
          codRdo = UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA);
          this.getSqlManager().update(
              "update gare1 set numrdo = ?, tipocontrattoerp = ?" +
                  "  where codgar1 = ? and ngara = ? ",
                  new Object[] { codRdo, tipoContratto, codgar, ngara });

        }

        if(esitoComunicazioneRda > 0){
          this.getRequest().setAttribute("RISULTATO", "CALCOLOWARNING");
        }else{
          this.getRequest().setAttribute("RISULTATO", "CALCOLOESEGUITO");
        }

      }else if("RAIWAY".equals(tipoWSERP)){
        //inserire la parte relativa ad aggiorna/Procedura
        RaiwayWSERPManager raiwayWSERPManager = (RaiwayWSERPManager) UtilitySpring.getBean("raiwayWSERPManager",
            this.getServletContext(), RaiwayWSERPManager.class);
        HashMap datiMask = new HashMap();
        datiMask.put("codgar", codgar);
        datiMask.put("ngara", ngara);

        WSERPRdaResType wserpRdaRes = raiwayWSERPManager.inviaDatiProcedura(username, password, datiMask);
        if(!wserpRdaRes.isEsito()){
          esitoComunicazioneRda++ ;
          UtilityStruts.addMessage(this.getRequest(), "error",
              "wserp.erp.aggiornaProceduraERP.error",
              new Object[] { wserpRdaRes.getMessaggio()});
        }else{
          Timestamp ts = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
          String tsStr = "OK-" + ts.toString();
          this.gestioneWSERPManager.updNumeroRdo(tipoWSERP, codgar, ngara, tsStr);

        }

        if(esitoComunicazioneRda > 0){
          this.getRequest().setAttribute("RISULTATO", "CALCOLOINFO");
        }else{
          this.getRequest().setAttribute("RISULTATO", "CALCOLOINFO");
        }

      }else if("AMIU".equals(tipoWSERP)){

        AmiuWSERPManager amiuWSERPManager = (AmiuWSERPManager) UtilitySpring.getBean("amiuWSERPManager",
            this.getServletContext(), AmiuWSERPManager.class);
        HashMap datiMask = new HashMap();
        oggettoGara = impl.getString("NOT_GAR");
        oggettoGara = UtilityStringhe.convertiNullInStringaVuota(oggettoGara);
        tipoContratto = impl.getString("TIPO_CONTR");
        tipoContratto = UtilityStringhe.convertiNullInStringaVuota(tipoContratto);
        daatto = impl.getData("GARE.DAATTO");
        nrepat = impl.getString("GARE.NREPAT");
        codcigaq = impl.getString("CODCIGAQ");
        codcigaq= UtilityStringhe.convertiNullInStringaVuota(codcigaq);

        datiMask.put("codgar", codgar);
        datiMask.put("ngara", ngara);
        datiMask.put("codcig", codcig);
        datiMask.put("codcigaq", codcigaq);
        datiMask.put("impapp", impapp);
        datiMask.put("iaggiu", iaggiu);
        datiMask.put("ribagg", ribagg);
        datiMask.put("modlicg", modlicg);
        datiMask.put("idFornitore", idFornitore);
        datiMask.put("gruppoConti", gruppoConti);
        datiMask.put("condizioniPagamento", condizioniPagamento);
        datiMask.put("modalitaPagamento", modalitaPagamento);
        datiMask.put("oggettoGara", oggettoGara);
        datiMask.put("tipoContratto", tipoContratto);
        datiMask.put("ditta", ditta);
        datiMask.put("daatto", daatto);
        datiMask.put("nrepat", nrepat);
        WSERPRdaResType wserpRdaRes = amiuWSERPManager.inviaDatiContratto(username, password, datiMask);
        if(!wserpRdaRes.isEsito()){
          esitoComunicazioneRda++ ;
          UtilityStruts.addMessage(this.getRequest(), "warning",
              "wserp.erp.mancataComEsito.warning",
              new Object[] {ditta,wserpRdaRes.getMessaggio()});
        }else{
          //aggiorno il numero Rdo
          String codRdo = wserpRdaRes.getMessaggio();
          this.getSqlManager().update(
              "update gare1 set numrdo = ?  where codgar1 = ? and ngara = ? ",
              new Object[] { codRdo, codgar, ngara });

        }

        if(esitoComunicazioneRda > 0){
          this.getRequest().setAttribute("RISULTATO", "CALCOLOWARNING");
        }else{
          this.getRequest().setAttribute("RISULTATO", "CALCOLOESEGUITO");
        }



      }else if("ATAC".equals(tipoWSERP)){

        AtacWSERPManager atacWSERPManager = (AtacWSERPManager) UtilitySpring.getBean("atacWSERPManager",
            this.getServletContext(), AtacWSERPManager.class);
        HashMap datiMask = new HashMap();
        oggettoGara = impl.getString("NOT_GAR");
        oggettoGara = UtilityStringhe.convertiNullInStringaVuota(oggettoGara);
        tipoContratto = impl.getString("TIPO_CONTR");
        tipoContratto = UtilityStringhe.convertiNullInStringaVuota(tipoContratto);
        daatto = impl.getData("GARE.DAATTO");
        nrepat = impl.getString("GARE.NREPAT");
        codcigaq = impl.getString("CODCIGAQ");
        codcigaq= UtilityStringhe.convertiNullInStringaVuota(codcigaq);

        datiMask.put("codgar", codgar);
        datiMask.put("ngara", ngara);
        datiMask.put("codcig", codcig);
        datiMask.put("codcigaq", codcigaq);
        datiMask.put("impapp", impapp);
        datiMask.put("iaggiu", iaggiu);
        datiMask.put("ribagg", ribagg);
        datiMask.put("modlicg", modlicg);
        datiMask.put("idFornitore", idFornitore);
        datiMask.put("gruppoConti", gruppoConti);
        datiMask.put("condizioniPagamento", condizioniPagamento);
        datiMask.put("modalitaPagamento", modalitaPagamento);
        datiMask.put("oggettoGara", oggettoGara);
        datiMask.put("tipoContratto", tipoContratto);
        datiMask.put("ditta", ditta);
        datiMask.put("daatto", daatto);
        datiMask.put("nrepat", nrepat);
        WSERPRdaResType wserpRdaRes = atacWSERPManager.inviaDatiAggiudicazione(username, password, datiMask);
        
        if(!wserpRdaRes.isEsito()){
          esitoComunicazioneRda++ ;
          UtilityStruts.addMessage(this.getRequest(), "warning",
              "wserp.erp.mancataComEsito.warning",
              new Object[] {ditta,wserpRdaRes.getMessaggio()});
        }else{
          //aggiorno il numero Rdo
          /* DA VERIFICARE
          String codRdo = wserpRdaRes.getMessaggio();
          this.getSqlManager().update(
              "update gare1 set numrdo = ?  where codgar1 = ? and ngara = ? ",
              new Object[] { codRdo, codgar, ngara });
*/
        }

        if(esitoComunicazioneRda > 0){
          this.getRequest().setAttribute("RISULTATO", "CALCOLOWARNING");
        }else{
          this.getRequest().setAttribute("RISULTATO", "CALCOLOESEGUITO");
        }

        //this.getRequest().setAttribute("RISULTATO", "CALCOLOESEGUITO");


      }else{
        Vector datiTorn = sqlManager.getVector("select accqua, aqoper, aqnumope, codcigaq, tipgen from torn, gare where ngara = ? and codgar1 = codgar", new Object[]{ngara});
        if(datiTorn!=null){
          accqua = SqlManager.getValueFromVectorParam(datiTorn, 0).stringValue();
          accqua = UtilityStringhe.convertiNullInStringaVuota(accqua);
          aqoper = SqlManager.getValueFromVectorParam(datiTorn, 1).longValue();
          aqnumope = SqlManager.getValueFromVectorParam(datiTorn, 2).longValue();
          codcigaq = SqlManager.getValueFromVectorParam(datiTorn, 3).stringValue();
          codcigaq= UtilityStringhe.convertiNullInStringaVuota(codcigaq);
          tipgen = SqlManager.getValueFromVectorParam(datiTorn, 4).longValue();
        }

        //Gestione accordo quadtro più operatori
        if(new Long(2).equals(aqoper)){
          List<?> listaDitteAQ = sqlManager.getListVector("select dittao from ditgaq where ngara = ? and dittao is not null", new Object[]{ngara});
          if(listaDitteAQ.size() > 0){
            listaDitte = new String[listaDitteAQ.size()];
            for (int i = 0; i < listaDitteAQ.size(); i++) {
              String dittaAQ = (String) SqlManager.getValueFromVectorParam(listaDitteAQ.get(i), 0).getValue();
              listaDitte[i] = dittaAQ;
            }
          }
        }else{
          listaDitte = new String[1];
          if(ditta != null){
            listaDitte[0] = ditta;
          }
        }

        WSERPGaraType datiGara = new WSERPGaraType();

        if(listaDitte != null){
          for(int i=0;i<listaDitte.length;i++){
            ditta = listaDitte[i];
            //inizio integrazione ERP
            if(ditta != null){
              //AVM o CAV caso in cui non si ha la lista delle lavorazioni
              String linkrda = "";
              if(("AVM".equals(tipoWSERP) && !"1".equals(accqua))|| "UGOVPA".equals(tipoWSERP)){
                List listaRdaGara  = this.sqlManager.getListVector(
                    "select CODGAR,NUMRDA,POSRDA,DATACONS,LUOGOCONS," +
                        "CODCARR,CODVOC,VOCE,UNIMIS,CODCAT,PERCIVA,ID,QUANTI,PREZUN " +
                        " from GARERDA where CODGAR = ? ", new Object[] { codgar });

                if(listaRdaGara != null && listaRdaGara.size() > 0){
                  linkrda = "1";
                  int nRda = listaRdaGara.size();
                  if(nRda == 1){
                    Vector vectRdaGara = (Vector) listaRdaGara.get(0);
                    String numRda = ((JdbcParametro) vectRdaGara.get(1)).getStringValue();
                    String posRda = ((JdbcParametro) vectRdaGara.get(2)).getStringValue();
                    Date dataCons= (Date) ((JdbcParametro) vectRdaGara.get(3)).getValue();
                    String luogoCons = ((JdbcParametro) vectRdaGara.get(4)).getStringValue();
                    String idLotto = ((JdbcParametro) vectRdaGara.get(5)).getStringValue();
                    String codvoc = ((JdbcParametro) vectRdaGara.get(6)).getStringValue();
                    String voce = ((JdbcParametro) vectRdaGara.get(7)).getStringValue();
                    String um = ((JdbcParametro) vectRdaGara.get(8)).getStringValue();
                    String codCat = ((JdbcParametro) vectRdaGara.get(9)).getStringValue();
                    Long perciva = (Long) ((JdbcParametro) vectRdaGara.get(10)).getValue();
                    Long contaf = (Long) ((JdbcParametro) vectRdaGara.get(11)).getValue();
                    Double quantita = null;
                    Object quantitaObj = ((JdbcParametro) vectRdaGara.get(12)).getValue();
                    if (quantitaObj instanceof Long){
                      quantita = ((Long) quantitaObj).doubleValue();
                    }else{
                      if(quantitaObj instanceof Double){
                        quantita = (Double) quantitaObj;
                      }
                    }
                    Double prezzo = null;
                    Object prezzoObj = ((JdbcParametro) vectRdaGara.get(13)).getValue();
                    if (prezzoObj instanceof Long){
                      prezzo = ((Long) prezzoObj).doubleValue();
                    }else{
                      if(prezzoObj instanceof Double){
                        prezzo = (Double) prezzoObj;
                      }
                    }

                    if(iaggiu != null){
                      WSERPOdaType[] odaArray = new WSERPOdaType[1];
                      WSERPOdaType oda = new WSERPOdaType();
                      WSERPRdaResType wserpRdaRes = new WSERPRdaResType();
                      if(quantita != null && !new Double(0).equals(quantita)){
                        oda.setQuantita(quantita);
                        Double impRda = iaggiu/quantita;
                        impRda = UtilityMath.round(impRda, 2);
                        oda.setPrezzo(impRda);
                      }else{
                        oda.setQuantita(quantita);
                        oda.setPrezzo(iaggiu);
                      }
                      if("AVM".equals(tipoWSERP)){
                        oda.setCodiceFornitore(idFornitore);
                      }else{
                        oda.setCodiceFornitore(ditta);
                      }
                      oda.setCodiceRda(numRda);
                      oda.setPosizioneRda(posRda);
                      oda.setNumeroGara(ngara);
                      if(dataCons != null){
                        Calendar calDataCons = Calendar.getInstance();
                        calDataCons.setTime(dataCons);
                        oda.setDataConsegna(calDataCons);
                      }

                      oda.setIdLotto(idLotto);
                      //PROVVISORIAMENTE METTO QUI IL CIG PER AVM
                      if("AVM".equals(tipoWSERP)){
                        oda.setIdLotto(codcig);
                      }
                      oda.setCodiceProdotto(codvoc);
                      oda.setDescrizione(voce);
                      oda.setUm(um);
                      oda.setCod(contaf);

                      //AGGIUDICA
                      odaArray[0] = oda;

                      wserpRdaRes = this.gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, servizio, odaArray, datiGara);
                      if(!wserpRdaRes.isEsito()){
                        esitoComunicazioneRda++ ;
                        if("AVM".equals(tipoWSERP)){
                          UtilityStruts.addMessage(this.getRequest(), "warning",
                              "wserp.erp.mancataIntegrazioneMultiplaNoDitta.warning",
                              new Object[] {wserpRdaRes.getMessaggio()});
                        }else{
                          UtilityStruts.addMessage(this.getRequest(), "warning",
                              "wserp.erp.mancataIntegrazione.warning",
                              new Object[] { oda.getCodiceRda(),oda.getCodiceFornitore(), wserpRdaRes.getMessaggio()});
                        }
                      }else{
                        //aggiorno il numero Rdo
                        String codRdo = wserpRdaRes.getMessaggio();
                        codRdo =UtilityStringhe.convertiNullInStringaVuota(codRdo);
                        if(!"".equals(codRdo)){
                          this.getSqlManager().update(
                              "update gare1 set numrdo = ?  where codgar1 = ? and ngara = ? ",
                              new Object[] { codRdo, codgar, ngara });
                        }
                      }

                    }

                  }else{

                    if(iaggiu != null){

                      WSERPOdaType[] odaArrayAvm = new WSERPOdaType[listaRdaGara.size()];
                      for (int k = 0; k < listaRdaGara.size(); k++) {
                        Vector vectRdaGara = (Vector) listaRdaGara.get(k);
                        String numRda = ((JdbcParametro) vectRdaGara.get(1)).getStringValue();
                        String posRda = ((JdbcParametro) vectRdaGara.get(2)).getStringValue();
                        Date dataCons= (Date) ((JdbcParametro) vectRdaGara.get(3)).getValue();
                        String luogoCons = ((JdbcParametro) vectRdaGara.get(4)).getStringValue();
                        String idLotto = ((JdbcParametro) vectRdaGara.get(5)).getStringValue();
                        String codvoc = ((JdbcParametro) vectRdaGara.get(6)).getStringValue();
                        String voce = ((JdbcParametro) vectRdaGara.get(7)).getStringValue();
                        String um = ((JdbcParametro) vectRdaGara.get(8)).getStringValue();
                        String codCat = ((JdbcParametro) vectRdaGara.get(9)).getStringValue();
                        Long perciva = (Long) ((JdbcParametro) vectRdaGara.get(10)).getValue();
                        Long contaf = (Long) ((JdbcParametro) vectRdaGara.get(11)).getValue();
                        Double quantita = null;
                        Object quantitaObj = ((JdbcParametro) vectRdaGara.get(12)).getValue();
                        if (quantitaObj instanceof Long){
                          quantita = ((Long) quantitaObj).doubleValue();
                        }else{
                          if(quantitaObj instanceof Double){
                            quantita = (Double) quantitaObj;
                          }
                        }
                        Double prezzo = null;
                        Object prezzoObj = ((JdbcParametro) vectRdaGara.get(13)).getValue();
                        if (prezzoObj instanceof Long){
                          prezzo = ((Long) prezzoObj).doubleValue();
                        }else{
                          if(prezzoObj instanceof Double){
                            prezzo = (Double) prezzoObj;
                          }
                        }
                        Double impRda = prezzo; //importo unitario perche ora passo la quantita
                        Double quozRip = new Double(0);
                        if(impapp!= null){
                          quozRip = (impapp-iaggiu)/impapp;
                        }
                        if(impRda==null){
                          impRda= new Double(0);
                        }
                        Double impAggiudicazioneRipartito = impRda*(1-quozRip);
                        impAggiudicazioneRipartito = UtilityMath.round(impAggiudicazioneRipartito, 2);
                        WSERPOdaType[] odaArray = new WSERPOdaType[1];
                        WSERPOdaType oda = new WSERPOdaType();
                        WSERPRdaResType wserpRdaRes = new WSERPRdaResType();
                        if("AVM".equals(tipoWSERP)){
                          oda.setCodiceFornitore(idFornitore);
                        }else{
                          oda.setCodiceFornitore(ditta);
                        }
                        oda.setQuantita(quantita);
                        oda.setPrezzo(impAggiudicazioneRipartito);
                        oda.setCodiceRda(numRda);
                        oda.setPosizioneRda(posRda);
                        oda.setNumeroGara(ngara);
                        if(dataCons != null){
                          Calendar calDataCons = Calendar.getInstance();
                          calDataCons.setTime(dataCons);
                          oda.setDataConsegna(calDataCons);
                        }
                        oda.setIdLotto(idLotto);
                        //PROVVISORIAMENTE METTO QUI IL CIG PER AVM
                        if("AVM".equals(tipoWSERP)){
                          oda.setIdLotto(codcig);
                        }

                        oda.setCodiceProdotto(codvoc);
                        oda.setDescrizione(voce);
                        oda.setUm(um);
                        oda.setCod(contaf);

                        //AGGIUDICA
                        if("AVM".equals(tipoWSERP)){
                          odaArrayAvm[k] = oda;
                        }else{
                          odaArray[0] = oda;
                          wserpRdaRes = this.gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, servizio, odaArray, datiGara);
                          if(!wserpRdaRes.isEsito()){
                            esitoComunicazioneRda++ ;
                            UtilityStruts.addMessage(this.getRequest(), "warning",
                                "wserp.erp.mancataIntegrazione.warning",
                                new Object[] { oda.getCodiceRda(),oda.getCodiceFornitore(),wserpRdaRes.getMessaggio()});
                          }else{
                            String codRdo = wserpRdaRes.getMessaggio();
                            //aggiorno il numero Rdo
                            this.getSqlManager().update(
                                "update gare1 set numrdo = ?  where codgar1 = ? and ngara = ? ",
                                new Object[] { codRdo, codgar, ngara });
                          }
                        }

                      }//for

                      //AGGIUDICA
                      if("AVM".equals(tipoWSERP)){
                        WSERPRdaResType wserpRdaRes = new WSERPRdaResType();
                        wserpRdaRes = this.gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, servizio, odaArrayAvm, datiGara);
                        if(!wserpRdaRes.isEsito()){
                          esitoComunicazioneRda++ ;
                          UtilityStruts.addMessage(this.getRequest(), "warning",
                              "wserp.erp.mancataIntegrazioneMultiplaNoDitta.warning",
                              new Object[] {wserpRdaRes.getMessaggio()});
                        }else{
                          //aggiorno il numero Rdo
                          String codRdo = wserpRdaRes.getMessaggio();
                          this.getSqlManager().update(
                              "update gare1 set numrdo = ?  where codgar1 = ? and ngara = ? ",
                              new Object[] { codRdo, codgar, ngara });
                        }

                      }//if avm

                    }//if n righe
                  }
                }
              }

              //PROVVISORIO PER TPER
              if("TPER".equals(tipoWSERP)){
                Timestamp ts = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
                String tsStr =ts.toString();

                linkrda="1"; //Attenzione
                WSERPOdaType[] odaArray = new WSERPOdaType[1];
                WSERPOdaType oda = new WSERPOdaType();
                WSERPRdaResType wserpRdaRes = new WSERPRdaResType();

                //popolamento datiGara
                if(!"".equals(codcig)){
                  datiGara.setCodiceCig(codcig);
                }
                if(!"".equals(codcigaq)){
                  datiGara.setCodiceCigMaster(codcigaq);
                }


                List<String> datiNCup = sqlManager.getListVector("select cup from garecup where ngara = ?", new Object[]{ngara});
                if(datiNCup!=null){
                  String[] codiceCupArray = new String[datiNCup.size()+1];
                  codiceCupArray[0] = cupprg;
                  for (int c = 0; c < datiNCup.size(); c++) {
                    String cup = (String) SqlManager.getValueFromVectorParam(datiNCup.get(c), 0).getValue();
                    codiceCupArray[c+1] = cup;
                  }
                  datiGara.setCodiceCupArray(codiceCupArray);
                }else{
                  String[] codiceCupArray = new String[1];
                  codiceCupArray[0] = cupprg;
                }
                datiGara.setCodiceFornitore(idFornitore);//esterno(SAP)
                datiGara.setDefinizioneCig(oggettoGara);
                datiGara.setImportoAggiudicazione(iaggiu);

                Double importoCig = null;
                Object importoCigObj = sqlManager.getObject("select valmax from v_gare_importi" +
                    " where codgar = ? and ngara = ? ", new Object[]{codgar,ngara});
                if(importoCigObj!=null && importoCigObj instanceof Double){
                  importoCig = (Double)importoCigObj;
                }else if(importoCigObj!=null && importoCigObj instanceof Long){
                  importoCig = new Double((Long)importoCigObj);
                }

                String impStr =String.valueOf(importoCig);



                datiGara.setImportoCig(importoCig);
                //popolamento oda..per ora nullo
                odaArray[0] = oda;
                infoFornitore = infoFornitore+"";

                if(new Long(3).equals(statoFornitore)){
                  //Anagrafica BLOCCATA
                  esitoComunicazioneRda++ ;
                  if(!"".equals(dataCreazioneFornitore)){
                    String anno = dataCreazioneFornitore.substring(0,4);
                    String mese = dataCreazioneFornitore.substring(4,6);
                    String giorno = dataCreazioneFornitore.substring(6,8);
                    String nDataStr = giorno+"/"+mese+"/"+anno;
                    String comunicazione = "\nL'anagrafica fornitore e' stata creata il "+nDataStr+ " e risulta bloccata." +
                        "\nNon risulta possibile proseguire con la creazione dell'anagrafica CIG";
                    UtilityStruts.addMessage(this.getRequest(), "warning",
                        "wserp.erp.mancataComEsito.warning",
                        new Object[] {ditta,comunicazione});

                  }

                }else{
                  //Anagrafica non bloccata
                  wserpRdaRes = this.gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, servizio, odaArray, datiGara);
                  if(!wserpRdaRes.isEsito()){
                    esitoComunicazioneRda++ ;
                    String comunicazione = wserpRdaRes.getMessaggio();
                    if(comunicazione!= null && comunicazione.length()>=2){
                      comunicazione = comunicazione.substring(2);
                      comunicazione = "\nL'anagrafica CIG non e' stata creata" + comunicazione;
                      if(!"".equals(dataCreazioneFornitore)){
                        String anno = dataCreazioneFornitore.substring(0,4);
                        String mese = dataCreazioneFornitore.substring(4,6);
                        String giorno = dataCreazioneFornitore.substring(6,8);
                        String nDataStr = giorno+"/"+mese+"/"+anno;
                        comunicazione = "\nL'anagrafica fornitore e' stata creata il "+nDataStr+ "."+ comunicazione;
                      }
                      UtilityStruts.addMessage(this.getRequest(), "warning",
                          "wserp.erp.mancataComEsito.warning",
                          new Object[] {ditta,comunicazione});
                    }

                  }else{
                    String msgSuss = wserpRdaRes.getMessaggio();
                    msgSuss =UtilityStringhe.convertiNullInStringaVuota(msgSuss);

                    //aggiorno il numero Rdo con esito e data
                    String esitoRdo = wserpRdaRes.getMessaggio();
                    esitoRdo = UtilityStringhe.convertiNullInStringaVuota(esitoRdo);

                    if(!"".equals(esitoRdo) && esitoRdo.length()>=2){
                      esitoRdo =  esitoRdo.substring(0,2) + "-" + tsStr;
                      this.getSqlManager().update(
                          "update gare1 set numrdo = ?  where codgar1 = ? and ngara = ? ",
                          new Object[] { esitoRdo, codgar, ngara });
                    }

                    if(!"".equals(msgSuss) && "OK".equals(msgSuss)){
                      String comunicazione = wserpRdaRes.getMessaggio();
                      if(comunicazione!= null && comunicazione.length()>=2){
                        comunicazione = comunicazione.substring(2);
                        comunicazione = "\nL'anagrafica CIG e' stata creata" + comunicazione;
                        if(!"".equals(dataCreazioneFornitore)){
                          esitoComunicazioneRda++ ;
                          String anno = dataCreazioneFornitore.substring(0,4);
                          String mese = dataCreazioneFornitore.substring(4,6);
                          String giorno = dataCreazioneFornitore.substring(6,8);
                          String nDataStr = giorno+"/"+mese+"/"+anno;
                          comunicazione = "\nL'anagrafica fornitore e' stata creata il "+nDataStr+ "."+ comunicazione;
                        }
                      }
                      UtilityStruts.addMessage(this.getRequest(), "warning",
                          "wserp.erp.comEsito.warning",
                          new Object[] {ditta,comunicazione});

                    }

                  }

                }

              }

              //CASO STANDARD CON LAVORAZIONI (AVM e SMEUP)
              if(!"1".equals(linkrda)){
                List listaProdottiAggiudicataria = null;
                if(dittaRTI!=null && !"".equals(dittaRTI)){
                  //nel caso in cui l'aggiudicataria sia una RTI ..devo mandare i dati della mandataria
                  //ma poi devo recuperare la lista lavori e forniture dalla rti
                  listaProdottiAggiudicataria = this.sqlManager.getListVector(
                      "select CODGAR,NGARA,CODVOC,VOCE,UNIMISEFF,QUANTIEFF,PREOFF,CODCARR,CODRDA,POSRDA,CONTAF,DATACONS" +
                          " from V_GCAP_DPRE where CODGAR = ? and NGARA= ? and COD_DITTA = ? ", new Object[] { codgar,ngara,dittaRTI });
                }else{
                  listaProdottiAggiudicataria = this.sqlManager.getListVector(
                      "select CODGAR,NGARA,CODVOC,VOCE,UNIMISEFF,QUANTIEFF,PREOFF,CODCARR,CODRDA,POSRDA,CONTAF,DATACONS" +
                          " from V_GCAP_DPRE where CODGAR = ? and NGARA= ? and COD_DITTA = ? ", new Object[] { codgar,ngara,ditta });
                }
                if (listaProdottiAggiudicataria != null && listaProdottiAggiudicataria.size() > 0) {
                  Vector<?> datiDitta = null;
                  String codiceDitta = ditta;
                  Long tipimp = (Long) this.sqlManager.getObject( "select TIPIMP from IMPR where CODIMP = ?", new Object[] { ditta });
                  if(tipimp != null && (new Long(3).equals(tipimp) || new Long(2).equals(tipimp) || new Long(10).equals(tipimp) || new Long(11).equals(tipimp))){
                    Vector<?> datiDittaMandataria  = this.sqlManager.getVector(
                        "select CODDIC,NOMDIC from RAGIMP where CODIME9 = ? and IMPMAN = '1' ", new Object[] { ditta });
                    if(datiDittaMandataria!=null && datiDittaMandataria.size()>0){
                      codiceDitta = (String) SqlManager.getValueFromVectorParam(datiDittaMandataria, 0).getValue();
                    }
                  }
                  //controllo sul completamento dei prezzi offerti
                  boolean controlloPrezziOfferti = true;
                  Double impCalcolato = new Double(0);
                  for (int k = 0; k < listaProdottiAggiudicataria.size(); k++) {
                    Vector vectProdottiAgg = (Vector) listaProdottiAggiudicataria.get(k);
                    Double quantieff = null;
                    Object quantieffObj = ((JdbcParametro) vectProdottiAgg.get(5)).getValue();
                    if(quantieffObj != null){
                      if (quantieffObj instanceof Long){
                        quantieff = ((Long) quantieffObj).doubleValue();
                      }else{
                        if(quantieffObj instanceof Double){
                          quantieff = (Double) quantieffObj;
                        }
                      }
                    }else{
                      controlloPrezziOfferti = false;
                      esitoComunicazioneRda++ ;
                      UtilityStruts.addMessage(this.getRequest(), "error",
                          "wserp.erp.mancataValorizzazioneRda.error",
                          new Object[] { codiceDitta});
                      break;
                    }

                    Double preoff = null;
                    Object preoffObj = ((JdbcParametro) vectProdottiAgg.get(6)).getValue();
                    if(preoffObj != null){
                      if (preoffObj instanceof Long){
                        preoff = ((Long) preoffObj).doubleValue();
                      }else{
                        if(preoffObj instanceof Double){
                          preoff = (Double) preoffObj;
                        }
                      }
                    }else{
                      controlloPrezziOfferti = false;
                      esitoComunicazioneRda++ ;
                      UtilityStruts.addMessage(this.getRequest(), "error",
                          "wserp.erp.mancataValorizzazioneRda.error",
                          new Object[] {codiceDitta });
                      break;
                    }

                    impCalcolato = impCalcolato + quantieff*preoff;
                  }//for di controllo

                  impCalcolato = UtilityMath.round(impCalcolato, 2);
                  Double tollerance = new Double(0.001);
                  if(Math.abs(impCalcolato-iaggiu)> tollerance){
                    controlloPrezziOfferti = true;
                    esitoComunicazioneRda++ ;
                    UtilityStruts.addMessage(this.getRequest(), "warning",
                        "wserp.erp.importoRdaOffertoDiversi.warning",
                        new Object[] {codiceDitta});
                  }

                  if(controlloPrezziOfferti){
                    WSERPOdaType[] odaArray = new WSERPOdaType[listaProdottiAggiudicataria.size()];
                    for (int k = 0; k < listaProdottiAggiudicataria.size(); k++) {
                      Vector vectProdottiAgg = (Vector) listaProdottiAggiudicataria.get(k);
                      //String codgar = ((JdbcParametro) tmp.get(0)).getStringValue();
                      //String ngara = ((JdbcParametro) tmp.get(1)).getStringValue();
                      String codvoc = ((JdbcParametro) vectProdottiAgg.get(2)).getStringValue();
                      String voce = ((JdbcParametro) vectProdottiAgg.get(3)).getStringValue();
                      String unimiseff = ((JdbcParametro) vectProdottiAgg.get(4)).getStringValue();
                      Double quantieff = null;
                      Object quantieffObj = ((JdbcParametro) vectProdottiAgg.get(5)).getValue();
                      if (quantieffObj instanceof Long){
                        quantieff = ((Long) quantieffObj).doubleValue();
                      }else{
                        if(quantieffObj instanceof Double){
                          quantieff = (Double) quantieffObj;
                        }
                      }
                      Double preoff = null;
                      Object preoffObj = ((JdbcParametro) vectProdottiAgg.get(6)).getValue();
                      if (preoffObj instanceof Long){
                        preoff = ((Long) preoffObj).doubleValue();
                      }else{
                        if(preoffObj instanceof Double){
                          preoff = (Double) preoffObj;
                        }
                      }
                      String idLotto = ((JdbcParametro) vectProdottiAgg.get(7)).getStringValue();
                      String codiceRda = ((JdbcParametro) vectProdottiAgg.get(8)).getStringValue();
                      String posizioneRda = ((JdbcParametro) vectProdottiAgg.get(9)).getStringValue();
                      Long contaf = (Long) ((JdbcParametro) vectProdottiAgg.get(10)).getValue();
                      Date dataConsegna = (Date) ((JdbcParametro) vectProdottiAgg.get(11)).getValue();

                      WSERPOdaType oda = new WSERPOdaType();
                      WSERPRdaResType wserpRdaRes = new WSERPRdaResType();
                      if("AVM".equals(tipoWSERP)){
                        oda.setCodiceFornitore(idFornitore);
                        oda.setQuantita(quantieff);
                      }else{
                        oda.setCodiceFornitore(codiceDitta);
                      }

                      Vector<?> datiFornitoreAggiudicatario  = this.sqlManager.getVector(
                          "select CFIMP,PIVIMP,CGENIMP from IMPR where CODIMP = ?", new Object[] { ditta });
                      if(datiFornitoreAggiudicatario!=null && datiFornitoreAggiudicatario.size()>0){
                        String cfFornitore = (String) SqlManager.getValueFromVectorParam(datiFornitoreAggiudicatario, 0).getValue();
                        oda.setCfFornitore(cfFornitore);
                        String pivaFornitore = (String) SqlManager.getValueFromVectorParam(datiFornitoreAggiudicatario, 1).getValue();
                        oda.setPivaFornitore(pivaFornitore);
                      }

                      oda.setCodiceProdotto(codvoc);
                      oda.setCodiceRda(codiceRda);
                      oda.setPosizioneRda(posizioneRda);
                      oda.setDescrizione(voce);
                      oda.setIdLotto(idLotto);
                      //oda.setNumeroOrdine(numeroOrdine);
                      oda.setPrezzo(preoff);
                      oda.setUm(unimiseff);
                      oda.setNumeroGara(ngara);
                      oda.setCod(contaf);
                      if(dataConsegna != null){
                        Calendar calDataCons = Calendar.getInstance();
                        calDataCons.setTime(dataConsegna);
                        oda.setDataConsegna(calDataCons);
                      }

                      if("SMEUP".equals(tipoWSERP)){
                        //AGGIUDICA
                        odaArray[0] = oda;
                        wserpRdaRes = this.gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, servizio, odaArray, datiGara);
                        if(!wserpRdaRes.isEsito()){
                          esitoComunicazioneRda++ ;
                          UtilityStruts.addMessage(this.getRequest(), "warning",
                              "wserp.erp.mancataComEsitoRda.warning",
                              new Object[] { oda.getCodiceRda(),oda.getCodiceFornitore(),wserpRdaRes.getMessaggio()});
                        }
                      }else{
                        if("AVM".equals(tipoWSERP) || "UGOVPA".equals(tipoWSERP)){
                          if("AVM".equals(tipoWSERP)){
                            //PROVVISORIAMENTE METTO QUI IL CIG
                            oda.setIdLotto(codcig);
                          }

                          odaArray[k] = oda;
                        }

                      }



                    }//for aggiudicazione

                    if("AVM".equals(tipoWSERP) || "UGOVPA".equals(tipoWSERP) || "CAV".equals(tipoWSERP)){
                      WSERPRdaResType wserpRdaRes = new WSERPRdaResType();
                      wserpRdaRes = this.gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, servizio, odaArray, datiGara);
                      if(!wserpRdaRes.isEsito()){
                        esitoComunicazioneRda++ ;
                        if("AVM".equals(tipoWSERP)){
                          UtilityStruts.addMessage(this.getRequest(), "warning",
                              "wserp.erp.mancataIntegrazioneMultiplaNoDitta.warning",
                              new Object[] {wserpRdaRes.getMessaggio()});
                        }else{
                          UtilityStruts.addMessage(this.getRequest(), "warning",
                              "wserp.erp.mancataIntegrazioneMultipla.warning",
                              new Object[] { ditta});
                        }
                        //CAMBIARE QUI
                      }else{
                        //aggiorno il numero Rdo
                        String codRdo = wserpRdaRes.getMessaggio();
                        if(!"".equals(codRdo)){
                          this.getSqlManager().update(
                              "update gare1 set numrdo = ?  where codgar1 = ? and ngara = ? ",
                              new Object[] { codRdo, codgar, ngara });
                        }

                      }

                    }

                  }
                }

              }

            }//fine integrazione WSERP

          }//for ditte

        }




        if(esitoComunicazioneRda > 0){
          this.getRequest().setAttribute("RISULTATO", "CALCOLOWARNING");
        }else{
          this.getRequest().setAttribute("RISULTATO", "CALCOLOESEGUITO");
        }


      }

    } catch (GestoreException e) {
      this.getRequest().setAttribute("RISULTATO", "ERRORI");
      throw e;
    } catch (Throwable e) {
      this.getRequest().setAttribute("RISULTATO", "ERRORI");
      throw new GestoreException(
          "Errore durante la comunicazione dell'esito delle RdA",
          "comunicaEsitoRda", e);
    }finally{
      this.getRequest().setAttribute("NGARA", ngara);
      this.getRequest().setAttribute("tipoWSERP", tipoWSERP);
    }



  }

}