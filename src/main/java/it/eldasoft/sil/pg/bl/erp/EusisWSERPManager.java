/*
 * Created on 10/12/2024
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.erp;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.npa.model.Soggetto;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.erp.WSERPAnagraficaType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreType;
import it.maggioli.eldasoft.ws.erp.WSERPGaraType;
import it.maggioli.eldasoft.ws.erp.WSERPOdaType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;


public class EusisWSERPManager {
  /** Logger */
  static Logger               logger                = Logger.getLogger(EusisWSERPManager.class);

  private SqlManager          sqlManager;

  private GestioneWSERPManager gestioneWSERPManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }

  //Aggiudica RDA
  public WSERPRdaResType  inviaDatiAggiudicazione (String username, String password, String codgar, String ngara, String codevento) throws Exception{

    WSERPRdaResType wserpRdaRes = new WSERPRdaResType();
    /*
    String genereSql = "select genere from v_gare_genere where codgar = ? ";
    Long genere = (Long) this.sqlManager.getObject(genereSql, new Object[]{codgar});
     */
    String selezioneGareDaAggiornare = "select g.codcig, g.dacqcig, g.iaggiu, g.impapp, "
        + "g.not_gar, g.notega, t.idappaltonpa, t.accqua, g.ditta "
        + "from gare g "
        + "inner join torn t on t.codgar = g.codgar1 "
        + "where g.codgar1 = ? and g.ngara = ? ";
    List<?> listaGareDaAgg = sqlManager.getListVector(selezioneGareDaAggiornare, new Object[] { codgar, ngara });
    WSERPGaraType datiGara = new WSERPGaraType();
    if(!listaGareDaAgg.isEmpty()){
      for (int g = 0; g < listaGareDaAgg.size(); g++){
        WSERPOdaType oda = new WSERPOdaType();
        String codcig = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 0).stringValue());
        Date dacqcig = (Date) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 1).getValue();
        Double iaggiu = checkDoubleValue(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 2).getValue());
        Double impapp = checkDoubleValue(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 3).getValue());
        String notgar = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 4).stringValue());
        String notega = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 5).stringValue());
        String idappaltonpa = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 6).stringValue());
        String accqua = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 7).getStringValue());
        String ditta = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 8).stringValue());
        
        
        datiGara.setTipoOrgano("O");
        //accqua e' 1 P o F    DA CAPIRE
        if(accqua != null && Long.valueOf(1).equals(accqua)) {
          datiGara.setTipoOrgano("P");
        }

        // CIG dell’ AQ quando si tratta di una adesione
        //datiGara.setCodiceCigMaster("");

        //GARCPV.CODCPV stato 1 principale 
        String queryCpv = "select ga.codcpv "
            + "from gare g "
            + "inner join GARCPV ga on ga.ngara = g.ngara "
            + "where ga.tipcpv = '1' "
            + "and g.ngara = ? ";
        oda.setCodiceProdotto((String) this.sqlManager.getObject(queryCpv,new Object[] { ngara }));

        oda.setNumeroGara(ngara);
        datiGara.setCodiceCig(codcig);
        oda.setDescrizione(notgar);
        
        Calendar dacqcigCalendar = Calendar.getInstance();
        dacqcigCalendar.setTime(new java.util.Date(dacqcig.getTime()));
        datiGara.setDataInizioValidita(dacqcigCalendar);
        
        Calendar fineValiditaCalendar = (Calendar) dacqcigCalendar.clone();
        fineValiditaCalendar.add(Calendar.YEAR, 1); // aggiungo 1 anno
        datiGara.setDataFineValidita(fineValiditaCalendar);


        datiGara.setDefinizioneCig(notega);
        datiGara.setNumeroAVCP(idappaltonpa);

        datiGara.setImportoCig(impapp);
        datiGara.setImportoAggiudicazione(iaggiu);
        
        if("GA_RETTIFICA_IMPORTO_AGG".equals(codevento)) {
          oda.setImporto(iaggiu);
          datiGara.setImportoAggiudicazione((Double) sqlManager.getObject("select iaggiuini from gare1 where codgar1 = ? and ngara = ? ", new Object[] { codgar, ngara }));  
        }
        //DA FARE
        //oda.setImporto() //variazione
        //oda.setDataConsegna() //variazione //usare la data per capire se siamo in variazione o aggiudicazione
        
        
        //RECUPERARE IL CF E LA PARTITA IVA DI DITTA
        String getDatiImpr = "select cfimp, pivimp from impr where codimp = ? ";
        WSERPFornitoreType[] aggiudicatari = null;
        Long tipimp = (Long) sqlManager.getObject("select tipimp from impr where codimp = ?", new Object[] { ditta });
        if(tipimp != null && (Long.valueOf(3).equals(tipimp) || Long.valueOf(10).equals(tipimp))){
          String getDatiRagimp = "select coddic, impman from ragimp where codime9 = ?";
          List<String> raggrImprese = this.sqlManager.getListVector(getDatiRagimp, new Object[]{ditta});
          if(raggrImprese != null && !raggrImprese.isEmpty()){
            aggiudicatari = new WSERPFornitoreType[raggrImprese.size()];
            for (int y = 0; y < raggrImprese.size(); y++) {
              String codiceDittaInt = (String) SqlManager.getValueFromVectorParam(raggrImprese.get(y), 0).getValue();
              Vector<?> datiImpr = sqlManager.getVector(getDatiImpr, new Object[]{codiceDittaInt});

              if(datiImpr!=null && !datiImpr.isEmpty()){
                String cfimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 0).getValue();
                String pivimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 1).getValue();

                WSERPFornitoreType aggiudicatario = new WSERPFornitoreType();
                aggiudicatario.setCodiceFiscale(cfimp);
                aggiudicatario.setPartitaIva(pivimp);
                aggiudicatario.setRuolo("Unico");
                aggiudicatari[y] = aggiudicatario;
              }
            }
          }
        } else {
          Vector<?> datiImpr = sqlManager.getVector(getDatiImpr, new Object[]{ditta});
          aggiudicatari = new WSERPFornitoreType[1];
          if(datiImpr!=null && !datiImpr.isEmpty()){
            String cfimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 0).getValue();
            String pivimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 1).getValue();

            WSERPFornitoreType aggiudicatario = new WSERPFornitoreType();
            aggiudicatario.setCodiceFiscale(cfimp);
            aggiudicatario.setPartitaIva(pivimp);
            aggiudicatario.setRuolo("Unico");
            aggiudicatari[0] = aggiudicatario;
          }
        }
        datiGara.setAggiudicatariArray(aggiudicatari);
        
        //I PARTECIPANTI
        String getPartecipantiDitG = "select dittao "
            + "from DITG "
            + "where (fasgar is null or fasgar > 1)  "
            + "and codgar5 = ? "
            + "and ngara5 = ? "
            + "and invoff = '1' ";
        List<String> datiPartecipantiDitG = this.sqlManager.getListVector(getPartecipantiDitG, new Object[]{codgar, ngara});
        if(datiPartecipantiDitG != null && !datiPartecipantiDitG.isEmpty()){
          List<WSERPAnagraficaType> listaPartecipanti = new ArrayList<>();
          for (int y = 0; y < datiPartecipantiDitG.size(); y++) {
            String dittao = (String) SqlManager.getValueFromVectorParam(datiPartecipantiDitG.get(y), 0).getValue();

            Long tipimpPar = (Long) sqlManager.getObject("select tipimp from impr where codimp = ?", new Object[] { dittao });
            if(tipimpPar != null && (Long.valueOf(3).equals(tipimpPar) || Long.valueOf(10).equals(tipimpPar))){
              String getDatiRagimp = "select coddic, impman from ragimp where codime9 = ?";
              List<String> raggrImprese = this.sqlManager.getListVector(getDatiRagimp, new Object[]{ditta});
              if(raggrImprese != null && !raggrImprese.isEmpty()){
                for (int x = 0; x < raggrImprese.size(); x++) {
                  String codiceDittaInt = (String) SqlManager.getValueFromVectorParam(raggrImprese.get(x), 0).getValue();
                  Vector<?> datiImpr = sqlManager.getVector(getDatiImpr, new Object[]{codiceDittaInt});

                  if(datiImpr!=null && !datiImpr.isEmpty()){
                    String cfimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 0).getValue();
                    String pivimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 1).getValue();

                    WSERPAnagraficaType partecipante = new WSERPAnagraficaType();
                    partecipante.setCodiceFiscale(cfimp);
                    partecipante.setPartitaIva(pivimp);
                    //partecipanti.setTipo(ditta);
                    listaPartecipanti.add(partecipante);
                  }
                }
              }
            } else {
              Vector<?> datiImprPartecip = sqlManager.getVector(getDatiImpr, new Object[]{dittao});

              if(datiImprPartecip!=null && !datiImprPartecip.isEmpty()){
                String cfimp = (String) SqlManager.getValueFromVectorParam(datiImprPartecip, 0).getValue();
                String pivimp = (String) SqlManager.getValueFromVectorParam(datiImprPartecip, 1).getValue();

                WSERPAnagraficaType partecipante = new WSERPAnagraficaType();
                partecipante.setCodiceFiscale(cfimp);
                partecipante.setPartitaIva(pivimp);
                //partecipanti.setTipo(ditta);
                listaPartecipanti.add(partecipante);
              }
            }
          }
          WSERPAnagraficaType[] partecipanti = listaPartecipanti.toArray(new WSERPAnagraficaType[0]);
          datiGara.setTecniciArray(partecipanti);
        }
        WSERPOdaType[] odaArray = new WSERPOdaType[1];
        odaArray[0] = oda;

        wserpRdaRes = gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, "WSERP", odaArray, datiGara);

        // DA FARE
        //this.gestioneWSERPManager.updNumeroRdo("EUSIS", codgar, ngara, numRdoComplessivo);
      }
    }

    return wserpRdaRes;

  }

  public Double checkDoubleValue(Object value) {
    Double valueDouble = null;
    if (value instanceof Number) {
      valueDouble = ((Number) value).doubleValue();
    }
    return valueDouble;
  }


}


