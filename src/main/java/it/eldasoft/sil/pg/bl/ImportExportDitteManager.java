/*
 * Created on 13/mar/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.bl;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class ImportExportDitteManager {


  /** Logger */
  static Logger logger = Logger.getLogger(AurManager.class);

  /** Manager SQL per le operazioni su database */
  private SqlManager sqlManager;

  private PgManager pgManager;

  private TabellatiManager tabellatiManager;
  
  private GeneManager geneManager;


  /**
   * Set SqlManager
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
      this.sqlManager = sqlManager;
  }

  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }

  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }
  
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  public final static String CODAPP = "codapp";
  public final static String NUMVER = "numver";
  public final static String DATETIMEEXPORT = "datetimeExport";
  public final static String UTENTECOD = "utenteCod";
  public final static String UTENTENOME = "utenteNome";
  public final static String DATIGARA = "datiGara";
  public final static String STAZIONEAPPALTANTECOD = "stazioneAppaltanteCod";
  public final static String STAZIONEAPPALTANTEDESC = "stazioneAppaltanteDesc";
  public final static String STAZIONEAPPALTANTECF = "stazioneAppaltanteCF";
  public final static String STAZIONEAPPALTANTEPI = "stazioneAppaltantePI";
  public final static String CODICEGARA = "codiceGara";
  public final static String OGGETTO = "oggetto";
  public final static String TIPOAPPALTOCOD = "tipoAppaltoCod";
  public final static String TIPOAPPALTODESC = "tipoAppaltoDesc";
  public final static String TIPOPROCEDURACOD = "tipoProceduraCod";
  public final static String TIPOPROCEDURADESC = "tipoProceduraDesc";
  public final static String CATEGORIACOD = "categoriaCod";
  public final static String CATEGORIADESC = "categoriaDesc";
  public final static String CLASSECATEGORIACOD = "classeCategoriaCod";
  public final static String CLASSECATEGORIADESC = "classeCategoriaDesc";
  public final static String IMPORTO = "importo";
  public final static String CODICEELENCO = "codiceElenco";
  public final static String NMINOPERATORI = "nminoperatori";
  public final static String NMAXOPERATORI = "nmaxoperatori";
  public final static String CODICE = "codice";
  public final static String RAGIONESOCIALE = "ragioneSociale";
  public final static String CODICEFISCALE = "codiceFiscale";
  public final static String PARTITAIVA = "partitaIva";
  public final static String EMAIL = "email";
  public final static String PEC = "PEC";
  public final static String TIPOIMPRESACOD = "tipoImpresaCod";
  public final static String TIPOIMPRESADESC = "tipoImpresaDesc";
  public final static String MANDATARIA = "mandataria";
  public final static String QUODIC = "quotaPartecipazione";
  public final static String ACQUISIZIONE = "acquisizione";
  
@SuppressWarnings("unchecked")
public JSONObject getDatiGara(String codgar) throws SQLException, GestoreException{
    
    JSONObject res = new JSONObject();
    
    String selectDatiGen = "select t.cenint, u.nomein, u.cfein, u.ivaein, g.ngara, g.not_gar, t.tipgen, g.tipgarg, g.impapp, g.elencoe from torn t,gare g,uffint u where t.cenint = u.codein and g.codgar1 = t.codgar and t.codgar = ?";
    Vector<JdbcParametro> datiGara = sqlManager.getVector(selectDatiGen, new Object[]{codgar});
    if(datiGara!=null && datiGara.size()>0){
      String cenint = (String) (datiGara.get(0)).getValue();
      if(cenint!=null && !"".equals(cenint)){
        res.put(STAZIONEAPPALTANTECOD, cenint);
      }
      String nomein = (String) (datiGara.get(1)).getValue();
      if(nomein!=null && !"".equals(nomein)){
        res.put(STAZIONEAPPALTANTEDESC, nomein);
      }
      String cfein = (String) (datiGara.get(2)).getValue();
      if(cfein!=null && !"".equals(cfein)){
        res.put(STAZIONEAPPALTANTECF, cfein);
      }
      String ivaein = (String) (datiGara.get(3)).getValue();
      if(ivaein!=null && !"".equals(ivaein)){
        res.put(STAZIONEAPPALTANTEPI, ivaein);
      }
      String ngara = (String) (datiGara.get(4)).getValue();
      if(ngara!=null && !"".equals(ngara)){
        res.put(CODICEGARA, ngara);
      }
      String not_gar = (String) (datiGara.get(5)).getValue();
      if(not_gar!=null && !"".equals(not_gar)){
        res.put(OGGETTO, not_gar);
      }
      Long tipgen = (Long) (datiGara.get(6)).getValue();
      if(tipgen!=null){
        res.put(TIPOAPPALTOCOD, tipgen);
        String tipoappaltodesc = tabellatiManager.getDescrTabellato("A1007", tipgen.toString());
        res.put(TIPOAPPALTODESC, tipoappaltodesc);
      }
      Long tipgarg = (Long) (datiGara.get(7)).getValue();
      if(tipgarg!=null){
        res.put(TIPOPROCEDURACOD , tipgarg);
        String tipoproceduradesc = tabellatiManager.getDescrTabellato("A2044", tipgarg.toString());
        res.put(TIPOPROCEDURADESC, tipoproceduradesc);
      }
      Double impapp = (Double) (datiGara.get(8)).getValue();
      if(impapp!=null){
        res.put(IMPORTO , impapp);
      }
      String elencoe = (String) (datiGara.get(9)).getValue();
      if(elencoe!=null && !"".equals(elencoe)){
        res.put(CODICEELENCO, elencoe);
      }
      
      String catiga = (String) sqlManager.getObject("select catiga from catg where ngara = ?", new Object[]{ngara}); 
      if(catiga != null && !"".equals(catiga)){
        res.put(CATEGORIACOD , catiga);
      }
      
      Vector<JdbcParametro> datiCategoria = sqlManager.getVector("select DESCAT,NUMCLA,DESCLA from V_GARE_CATEGORIE where NGARA = ?", new Object[]{ngara});
      if(datiCategoria!=null && datiCategoria.size()>0){
        String descat = (String) (datiCategoria.get(0)).getValue();
        if(descat!=null && !"".equals(descat)){
          res.put(CATEGORIADESC, descat);
        }
        Long numcla = (Long) (datiCategoria.get(1)).getValue();
        if(numcla!=null){
          res.put(CLASSECATEGORIACOD, numcla);
        }
        String descla = (String) (datiCategoria.get(2)).getValue();
        if(descla!=null && !"".equals(descla)){
          res.put(CLASSECATEGORIADESC, descla);
        }
      }
      
      
      
      if(elencoe != null && !"".equals(elencoe)){
       Long[] numeroDitte =  pgManager.getNumeroMinimoDitte(tipgarg.intValue(), tipgen.toString(), impapp, codgar, "");
       Long numeroMinimo = numeroDitte[0];
       if(numeroMinimo!=null && !"".equals(numeroMinimo)){
         res.put(NMINOPERATORI, numeroMinimo);
       }
       Long numeroMassimo = numeroDitte[2];
       if(numeroMassimo!=null && !"".equals(numeroMassimo)){
         res.put(NMAXOPERATORI, numeroMassimo);
       }
      }
      
      JSONArray ar = new JSONArray();
      List listaOperatori =  sqlManager.getListVector("select dittao from ditg where codgar5 = ? order by NPROGG", new Object[]{codgar});
      if(listaOperatori != null && listaOperatori.size()>0){
        for (int i = 0; i < listaOperatori.size(); i++) {
          String dittao = SqlManager.getValueFromVectorParam(listaOperatori.get(i), 0).getStringValue();
          JSONObject obj = getOperatore(dittao,codgar,false,null);
          ar.add(obj);
        }
      }
      res.put("operatori", ar);
      
    }
    
    return res;
  }
  
  @SuppressWarnings("unchecked")
  private JSONObject getOperatore(String dittao, String codgar, boolean componenteRT, String codimpRT) throws SQLException{
    
    JSONObject obj = new JSONObject();
    
    String select = "select i.nomest, i.cfimp, i.pivimp, i.emaiip, i.emai2ip, i.tipimp from impr i where i.codimp = ?";
    Vector<JdbcParametro> datiOperatore = sqlManager.getVector(select,
        new Object[] { dittao });
    if (datiOperatore != null && datiOperatore.size() > 0) {
        
        if(dittao!=null && !"".equals(dittao)){
          obj.put(CODICE , dittao);
        }
        String nomest = (String) (datiOperatore.get(0)).getValue();
        if(nomest!=null && !"".equals(nomest)){
          obj.put(RAGIONESOCIALE , nomest);
        }
        String cfimp = (String) (datiOperatore.get(1)).getValue();
        if(cfimp!=null && !"".equals(cfimp)){
          obj.put(CODICEFISCALE , cfimp);
        }
        String pivimp = (String) (datiOperatore.get(2)).getValue();
        if(pivimp!=null && !"".equals(pivimp)){
          obj.put(PARTITAIVA , pivimp);
        }
        String emaiip =(String) (datiOperatore.get(3)).getValue();
        if(emaiip!=null && !"".equals(emaiip)){
          obj.put(EMAIL , emaiip);
        }
        String emai2ip =(String) (datiOperatore.get(4)).getValue();
        if(emai2ip!=null && !"".equals(emai2ip)){
          obj.put(PEC , emai2ip);
        }
        Long tipimp = (Long) (datiOperatore.get(5)).getValue();
        if(tipimp!=null){
          obj.put(TIPOIMPRESACOD , tipimp);
          String tipoimpresadesc = tabellatiManager.getDescrTabellato("Ag008", tipimp.toString());
          obj.put(TIPOIMPRESADESC , tipoimpresadesc);
        }
        if(componenteRT){
          Vector<JdbcParametro> datiRagimp = sqlManager.getVector("select impman, quodic from ragimp where coddic = ? and codime9 = ?", new Object[]{dittao,codimpRT}); 
          String impman = (String) (datiRagimp.get(0)).getValue();
          Double quodic = (Double) (datiRagimp.get(1)).getValue();
          if(impman != null && !"".equals(impman)){
            if("1".equals(impman)){
              obj.put(MANDATARIA , true);
            }else{
              obj.put(MANDATARIA , false);
            }
          }
          if(quodic!=null){
            obj.put(QUODIC , quodic);
          }
        }
        //gestione RT
        if(tipimp != null && (tipimp.intValue()==3 || tipimp.intValue()==10)){
          JSONArray ar = new JSONArray();
          List listaOperatori =  sqlManager.getListVector("select coddic from ragimp where codime9 = ?", new Object[]{dittao});
          if(listaOperatori != null && listaOperatori.size()>0){
            for (int i = 0; i < listaOperatori.size(); i++) {
              String coddic = SqlManager.getValueFromVectorParam(listaOperatori.get(i), 0).getStringValue();
              JSONObject objRag = getOperatore(coddic,codgar,true,dittao);
              ar.add(objRag);
            }
          }
          obj.put("raggruppamento", ar);
        }
        
        select = "select ACQUISIZIONE from DITG where dittao = ? and ngara5 = ?";
        String acquisizione = (String)sqlManager.getObject(select, new Object[]{dittao,codgar});
        if(acquisizione!=null){
          obj.put(ACQUISIZIONE , acquisizione);
        }
        
    }
    return obj;
    
  }
  
  public String isOperatoreRegistrato(String dittao, String codiceFiscale, String piva) throws SQLException{
    
    String select="select impr.codimp from impr, w_puser where impr.codimp = w_puser.userkey1 and impr.cfimp = ?";
    String codiceDitta;
    codiceDitta = (String)sqlManager.getObject(select, new Object[]{codiceFiscale});
    if(codiceDitta == null){
      
      if((codiceFiscale != null && !"".equals(codiceFiscale)) && (piva != null && !"".equals(piva))){
        select = "select CODIMP from impr where cfimp = ? and pivimp = ? order by codimp desc";
        codiceDitta = (String)sqlManager.getObject(select, new Object[]{codiceFiscale,piva});
      }
      if(codiceDitta == null){
        if(piva != null && !"".equals(piva)){
          select = "select CODIMP from impr where pivimp = ? order by codimp desc";
          codiceDitta = (String)sqlManager.getObject(select, new Object[]{piva});
        }
        if(codiceDitta == null){
          if(codiceFiscale != null && !"".equals(codiceFiscale)){
            select = "select CODIMP from impr where cfimp = ? order by codimp desc";
            codiceDitta = (String)sqlManager.getObject(select, new Object[]{codiceFiscale});
          }
          if(codiceDitta == null){
            if(piva != null && !"".equals(piva)){
              select = "select CODIMP from impr where cfimp = ? order by codimp desc";
              codiceDitta = (String)sqlManager.getObject(select, new Object[]{piva});
            }
            if(codiceDitta == null){
              if(codiceFiscale != null && !"".equals(codiceFiscale)){
                select = "select CODIMP from impr where pivimp = ? order by codimp desc";
                codiceDitta = (String)sqlManager.getObject(select, new Object[]{codiceFiscale});
              }
            }
          }
        }
      }
    }
    return codiceDitta;
  }
  

  public String insertDittaAnagrafica(Long tipimp, String nomest, String nomimp, String cfimp, String pivimp, String emaiip, String emai2ip) throws GestoreException, SQLException{
    
    String codimp;
    String insertQuery = "INSERT INTO IMPR (CODIMP,TIPIMP,NOMEST,NOMIMP,CFIMP,PIVIMP,EMAIIP,EMAI2IP) VALUES (?,?,?,?,?,?,?,?)";
    codimp = geneManager.calcolaCodificaAutomatica("IMPR","CODIMP");
    sqlManager.update(insertQuery, new Object[]{codimp,tipimp,nomest,nomimp,cfimp,pivimp,emaiip,emai2ip});
    
    return codimp;
    
  }
  
  public boolean fileGiaImportato(String ngara) throws SQLException{
    boolean res = false;
    Long count = (Long) sqlManager.getObject("select count(*) from ditg where ngaraexp = ?",new Object[]{ngara});
    if(count.intValue()>0){
      res = true;
    }
    return res;
  }
  
}
