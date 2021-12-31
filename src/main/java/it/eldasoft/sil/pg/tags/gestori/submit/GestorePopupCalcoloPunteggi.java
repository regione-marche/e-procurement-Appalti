/*
 * Created on 11/07/17
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityMath;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire il calcolo
 * dei punteggi lanciato dal popCalcoloPunteggi.jp
 *
 * @author Marcello Caminiti
 */
public class GestorePopupCalcoloPunteggi extends
    AbstractGestoreEntita {

  static Logger               logger         = Logger.getLogger(GestorePopupCalcoloPunteggi.class);

  public GestorePopupCalcoloPunteggi() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  private GenChiaviManager  genChiaviManager;

  private TabellatiManager  tabellatiManager;

  private PgManager  pgManager;

  private String insertG1Crival = "insert into G1CRIVAL(id,ngara,necvan,dittao,idcridef,coeffi,punteg) values(?,?,?,?,?,?,?)";
  private String insertDpun = "insert into DPUN(ngara,necvan,dittao,coeffi,punteg) values(?,?,?,?,?)";
  private String updatePunteggi ="update ditg set #NOMECAMPO#=? where ngara5=? and dittao=?";
  private String selectG1CridefMaxpun0="select g1.necvan, g1.id from g1cridef g1, goev g where g1.ngara=? and g.ngara=g1.ngara and g.necvan=g1.necvan and g.tippar=? and g1.modpunti=1 and g1.maxpun = 0";
  private String selectG1CridefAuto = "select g1.necvan, g1.id, g1.formula, g1.formato, g1.maxpun from g1cridef g1, goev g where g1.ngara=? and g.ngara=g1.ngara and g.necvan=g1.necvan and g.tippar=? and modpunti = 2";
  private String selectG1Crival="select v.necvan, v.coeffi,v.punteg, g.livpar,g.necvan1 from g1cridef d, goev g, g1crival v where d.ngara=? and g.ngara=d.ngara and g.necvan=d.necvan and d.id=v.idcridef "
      + "and v.dittao=? and g.tippar=? and (g.livpar=1 or g.livpar=2) order by norpar, necvan1,norpar1,necvan";

  private String selectG1CrivalFiglieG1Cridef = "select count(id) from g1crival where idcridef=? and ngara=? and dittao=?";
  private String selectG1CrivalNulli = "select g1crival.id from g1crival, g1cridef where g1crival.idcridef=? and g1cridef.id = g1crival.idcridef and g1crival.ngara=? and g1crival.dittao=? and g1cridef.necvan = ? and g1cridef.formato != 100";

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
    genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);
    tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        this.getServletContext(), TabellatiManager.class);
    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);
  }

  @Override
  public String getEntita() {
    return "DPUN";
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
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    //variabili per tracciatura eventi
    int livEvento = 3;
    String codEvento = "GA_OEPV_PUNTEGGI_TEC";
    String oggEvento = "";
    String descrEvento = "Calcolo punteggi ditte per criteri di valutazione busta tecnica";
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
    
    String codice = UtilityStruts.getParametroString(this.getRequest(),"codice");
    String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String tipo= UtilityStruts.getParametroString(this.getRequest(),"tipo");
    String bustalotti=UtilityStruts.getParametroString(this.getRequest(),"bustalotti");

    oggEvento = ngara;
    if("2".equals(tipo)){
      codEvento = "GA_OEPV_PUNTEGGI_ECO";
      descrEvento = "Calcolo punteggi ditte per criteri di valutazione busta economica";
    }

    try{
      try {
        //si cancellano tutte le occorrenze in DPUN per la gara
        this.sqlManager.update("delete from dpun where dpun.ngara=? and dpun.necvan=(select goev.necvan from goev where goev.ngara=dpun.ngara and goev.necvan=dpun.necvan and tippar=?)", new Object[]{ngara, new Long(tipo)});
      } catch (SQLException e) {
        livEvento = 3;
        this.getRequest().setAttribute("calcoloEseguito", "2");
        errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.calcoloPunteggi.cancellaDPUN");
        throw new GestoreException(errMsgEvento, "calcoloPunteggi.cancellaDPUN",e);
      }
      String select="select dittao from ditg where ngara5=? and (fasgar is null or fasgar > ?) order by NUMORDPL asc,NOMIMO asc";
      Object par[] = new Object[2];
      Long fasgar;
      par[0]=ngara;
      if("1".equals(tipo)){
        par[1]=new Long(5);
        fasgar=new Long(5);
      } 
      else{
        par[1]=new Long(6);
        fasgar=new Long(6);
      }

      List<?> listaDitteGara= null;
      try {
        listaDitteGara=sqlManager.getListVector(select, par);
      }catch (SQLException e) {
        livEvento = 3;
        this.getRequest().setAttribute("calcoloEseguito", "2");
        errMsgEvento="Errore nella lettura delle ditte in gara";
        throw new GestoreException(errMsgEvento, "calcoloPunteggi",e);
      }
      if(listaDitteGara!=null && listaDitteGara.size()>0){
        String ditta = null;
        Long necvan=null;
        Long idCridef=null;
        Long formula=null;
        Long formato=null;
        long idCrival;
        Double maxpun = null;

        List<?> datiG1cridefmaxpun0 = null;
        List<?> datiG1cridefAuto = null;
        List<?> datiG1crival = null;
        Long necvang1crival=null;
        Double coeffi = null;
        Long livpar = null;
        Double punteg = null;
        Long necvan1 = null;
        Long necvanPadre = null;
        double punteggioPadre = 0;
        double punteggioTotale=0;
        boolean gruppoCriteriFigli=false;
        boolean ultimoGruppoSalvato=false;
        try {
          datiG1cridefmaxpun0 = this.sqlManager.getListVector(selectG1CridefMaxpun0, new Object[]{ngara,new Long(tipo)});
          datiG1cridefAuto = this.sqlManager.getListVector(selectG1CridefAuto, new Object[]{ngara,new Long(tipo)});
        } catch (SQLException e) {
          livEvento = 3;
          this.getRequest().setAttribute("calcoloEseguito", "2");
          errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.calcoloPunteggi.G1CRIDEF");
          throw new GestoreException(errMsgEvento, "calcoloPunteggi.G1CRIDEF",e);
        }

        //Inserimento occorrenza in G1CRIVAL per occorrenze di G1CRIDEF con MODPUNTI = 2
        if(datiG1cridefAuto!=null && datiG1cridefAuto.size()>0){
          Long numeroOcc=null;
          for(int k=0;k<datiG1cridefAuto.size();k++){
            //Prima di inserire l'occorrenza, si deve controllare che non esista già
            necvan=SqlManager.getValueFromVectorParam(datiG1cridefAuto.get(k),0).longValue();
            idCridef=SqlManager.getValueFromVectorParam(datiG1cridefAuto.get(k),1).longValue();
            formula=SqlManager.getValueFromVectorParam(datiG1cridefAuto.get(k),2).longValue();
            formato=SqlManager.getValueFromVectorParam(datiG1cridefAuto.get(k),3).longValue();
            maxpun=SqlManager.getValueFromVectorParam(datiG1cridefAuto.get(k),4).doubleValue();
            this.SetCoefficenteDaFormula(idCridef,necvan,formula,formato,maxpun,fasgar);
          }
        }
        
        for(int i=0;i<listaDitteGara.size();i++){
          ditta = SqlManager.getValueFromVectorParam(listaDitteGara.get(i), 0).getStringValue();
          punteggioPadre = 0;
          punteggioTotale=0;
          gruppoCriteriFigli=false;
          necvanPadre=null;
          try {
            //Inserimento occorrenza in G1CRIVAL per occorrenze di G1CRIDEF con MODPUNTI = 1 & MAXPUN = 0
            if(datiG1cridefmaxpun0!=null && datiG1cridefmaxpun0.size()>0){
              Long numeroOcc=null;
              for(int k=0;k<datiG1cridefmaxpun0.size();k++){
                //Prima di inserire l'occorrenza, si deve controllare che non esista già
                necvan=SqlManager.getValueFromVectorParam(datiG1cridefmaxpun0.get(k),0).longValue();
                idCridef=SqlManager.getValueFromVectorParam(datiG1cridefmaxpun0.get(k),1).longValue();
                //se non ci sono occorrenze in crival 
                numeroOcc = (Long)this.sqlManager.getObject(selectG1CrivalFiglieG1Cridef, new Object[]{idCridef,ngara,ditta});
                if(numeroOcc==null || (new Long(0)).equals(numeroOcc)){
                  idCrival = this.genChiaviManager.getNextId("G1CRIVAL");
                  this.sqlManager.update(insertG1Crival, new Object[]{ idCrival,ngara,necvan,ditta,idCridef,new Double(0),new Double(0)});
                }
                else{
                  Long id = (Long)this.sqlManager.getObject(selectG1CrivalNulli, new Object[]{idCridef,ngara,ditta,necvan});
                  if(id!=null){
                    this.sqlManager.update("update G1CRIVAL set coeffi = ?, punteg = ? where g1crival.id = ?", new Object[]{ new Double(0),new Double(0), id});
                  }
                }
              }
            }
            
            Long conteggioGfof = (Long) sqlManager.getObject("select count(*) from gfof where ngara2 = ? and espgiu = '1'", new Object[] {codice});
            if(conteggioGfof != null && conteggioGfof.intValue()>0){
              String selectCriteri = "select v.id, g.maxpun from g1cridef g, g1crival v where g.id = v.idcridef and g.modpunti = '1' and g.maxpun > 0 and v.coeffi is null and v.punteg is null and g.ngara = ? and dittao = ?";
              datiG1crival = this.sqlManager.getListVector(selectCriteri, new Object[]{ngara, ditta});
              if(datiG1crival!=null && datiG1crival.size()>0){
                for(int j=0;j<datiG1crival.size();j++){
                   Long idcrival =SqlManager.getValueFromVectorParam(datiG1crival.get(j),0).longValue();
                   Double maxpunCridef =(Double) SqlManager.getValueFromVectorParam(datiG1crival.get(j),1).getValue();
                   
                   List listaValutazioniCommissari = this.sqlManager.getListVector("select coeffi from g1crivalcom g where g.idcrival = ?", new Object[]{idcrival});
                   Double mediaVal = new Double(0);
                   if(listaValutazioniCommissari!=null && listaValutazioniCommissari.size()>0){
                     for(int n=0;n<listaValutazioniCommissari.size();n++){
                       Double coeffiCommissario =(Double) SqlManager.getValueFromVectorParam(listaValutazioniCommissari.get(n),0).getValue();
                       if (coeffiCommissario == null)
                         coeffiCommissario = new Double(0);
                       mediaVal = mediaVal+ coeffiCommissario;
                     }
                     mediaVal = mediaVal/listaValutazioniCommissari.size();
                     Integer numeroDecimali = new Integer(tabellatiManager.getDescrTabellato("A1049", "1"));
                     Double puntegCrival = UtilityMath.round(mediaVal * maxpunCridef, numeroDecimali);
                     this.sqlManager.update("update G1CRIVAL set coeffi = ?, punteg = ? where g1crival.id = ?", new Object[]{ mediaVal,puntegCrival, idcrival});
                   }
                }
              }
              
            }
            
            datiG1crival = this.sqlManager.getListVector(selectG1Crival, new Object[]{ngara, ditta, new Long(tipo)});
            if(datiG1crival!=null && datiG1crival.size()>0){
              for(int j=0;j<datiG1crival.size();j++){
                //Inserimento occorrenze in DPUN relative solo ai criteri senza sottocriteri e ai sottocriteri
                necvang1crival=SqlManager.getValueFromVectorParam(datiG1crival.get(j),0).longValue();
                coeffi=SqlManager.getValueFromVectorParam(datiG1crival.get(j),1).doubleValue();
                punteg=SqlManager.getValueFromVectorParam(datiG1crival.get(j),2).doubleValue();
                livpar=SqlManager.getValueFromVectorParam(datiG1crival.get(j),3).longValue();
                necvan1=SqlManager.getValueFromVectorParam(datiG1crival.get(j),4).longValue();

                this.sqlManager.update(insertDpun, new Object[]{ ngara,necvang1crival,ditta,coeffi,punteg});
                //Inserimento occorrenza in DPUN relativa ai criteri con sottocriteri
                //Per fare ciò si sfrutta il fatto che le occorrenze caricate da G1CRIVAL sono ordinate in modo da
                //raggruppare tutti i sottocriteri di uno stesso criterio
                ultimoGruppoSalvato=false;
                if(new Long(2).equals(livpar)){
                  if(necvanPadre==null || !necvanPadre.equals(necvan1)){
                    gruppoCriteriFigli=true;
                    if(necvanPadre!=null && !necvanPadre.equals(necvan1)){
                      punteggioTotale += punteggioPadre;
                      this.insertDpunPadre(ngara, necvanPadre, ditta, punteggioPadre);
                      punteggioPadre = 0;
                    }
                    necvanPadre = necvan1;
                    punteggioPadre += punteg.doubleValue();

                  }else{
                    punteggioPadre += punteg.doubleValue();
                  }
                }else if(new Long(1).equals(livpar)){
                  if(gruppoCriteriFigli){
                    punteggioTotale += punteggioPadre;
                    this.insertDpunPadre(ngara, necvanPadre, ditta, punteggioPadre);
                    ultimoGruppoSalvato=true;
                  }
                  punteggioTotale+= punteg.doubleValue();
                  gruppoCriteriFigli=false;
                  necvanPadre=null;
                  punteggioPadre=0;
                }
              }
              if(!ultimoGruppoSalvato && gruppoCriteriFigli==true){
                punteggioTotale += punteggioPadre;
                this.insertDpunPadre(ngara, necvanPadre, ditta, punteggioPadre);
              }

              //Punteggio totale della ditta
              if("1".equals(tipo))
                updatePunteggi=updatePunteggi.replace("#NOMECAMPO#", "puntec");
              else
                updatePunteggi=updatePunteggi.replace("#NOMECAMPO#", "puneco");

              this.sqlManager.update(updatePunteggi, new Object[]{ new Double(punteggioTotale), ngara,ditta});

            }
          } catch (SQLException e) {
            livEvento = 3;
            this.getRequest().setAttribute("calcoloEseguito", "2");
            errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.calcoloPunteggi");
            throw new GestoreException(errMsgEvento, "calcoloPunteggi",e);
          }
        }
        
        
        //Se tutto è andato a buon fine si deve aggiornare lo stato della gara!
        try{
          int numeroStepAttivo = GestioneFasiGaraFunction.FASE_VALUTAZIONE_TECNICA;
          if("2".equals(tipo))
            numeroStepAttivo = GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE;
          pgManager.aggiornaFaseGara(new Long(numeroStepAttivo), ngara, false);
          if("2".equals(bustalotti) || "1".equals(bustalotti)){
            String codiceTornata = (String)this.sqlManager.getObject("select codgar1 from gare where ngara=?", new Object[]{ngara});
            Long valoreFasgarAggiornamento = pgManager.getStepAttivo(new Long(numeroStepAttivo), codiceTornata);
            // Aggiornamento del FASGAR dell'occorrenza complementare
            pgManager.aggiornaFaseGara(valoreFasgarAggiornamento, codiceTornata, false);
          }
        }catch (GestoreException e) {
          livEvento = 3;
          this.getRequest().setAttribute("calcoloEseguito", "2");
          errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.calcoloPunteggi");
          throw e;
        }catch (SQLException e) {
          livEvento = 3;
          this.getRequest().setAttribute("calcoloEseguito", "2");
          errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.calcoloPunteggi");
          throw new GestoreException(errMsgEvento, "calcoloPunteggi",e);
        }
      }

      livEvento = 1;
      errMsgEvento = "";
      this.getRequest().setAttribute("calcoloEseguito", "1");
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally{
      //Tracciatura eventi
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(oggEvento);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descrEvento);
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        String messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
        this.getRequest().setAttribute("calcoloEseguito", "2");
      }
    }
  }


  /**
   * Viene inserita l'occorenza in DPUN relativa al caso di criterio con sottocriteri (LIVPAR=3)
   */
  private void insertDpunPadre(String ngara, Long necvanPadre, String ditta, double punteggioPadre) throws SQLException{
    Double maxpun = (Double)this.sqlManager.getObject("select maxpun from goev where ngara=? and necvan=?", new Object[]{ngara,necvanPadre});
    if(maxpun!=null){
      double coeffiPadre;
      if(maxpun.doubleValue()!=0){
        coeffiPadre = punteggioPadre / maxpun.doubleValue();
        coeffiPadre =UtilityMath.round(coeffiPadre, 9);
      }else{
        coeffiPadre = new Double(0);
      }
      this.sqlManager.update(this.insertDpun, new Object[]{ ngara,necvanPadre,ditta,new Double(coeffiPadre), new Double(punteggioPadre)});
    }
  }

  private void SetCoefficenteDaFormula(Long idCridef, Long necvan, Long formula, Long formato, Double maxpun, Long fasgar) throws SQLException, GestoreException{
    String selectCrivalStg = "select valstg, id from g1crival where idcridef = ? and necvan = ? and g1crival.dittao = (select dittao from ditg where ngara5 = ? and ditg.dittao = g1crival.dittao and (fasgar is null or fasgar > " + fasgar.intValue() + ") )";
    String selectCrivalNum = "select valnum, id from g1crival where idcridef = ? and necvan = ? and g1crival.dittao = (select dittao from ditg where ngara5 = ? and ditg.dittao = g1crival.dittao and (fasgar is null or fasgar > " + fasgar.intValue() + ") )";
    String updateCrival = "update G1CRIVAL set coeffi = ?, punteg = ? where id = ?";
    
    String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    
    String tabA1049 = (String)sqlManager.getObject("select tab1desc from tab1 where tab1cod=? and tab1tip=?", new Object[]{"A1049",new Long(1)});
    int decimali = Integer.parseInt(tabA1049); 
    int precision = 9;
    
    if(formula == 1){
      List datiG1crival = this.sqlManager.getListVector(selectCrivalStg, new Object[]{idCridef,necvan,ngara});
      if(datiG1crival!=null && datiG1crival.size()>0){
        for(int k=0;k<datiG1crival.size();k++){
          String valstg = SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).stringValue();
          Long id = (Long) SqlManager.getValueFromVectorParam(datiG1crival.get(k),1).getValue();
          Double criregCoeffi = (Double) this.sqlManager.getObject("select g1crireg.coeffi from g1crireg where g1crireg.idcridef = ? and g1crireg.puntuale = ?", new Object[]{idCridef,valstg});
          if(criregCoeffi == null){
            String errMsgEvento="Errore nella lettura del valore puntuale";
            throw new GestoreException(errMsgEvento, "calcoloPunteggi.valorePuntuale",null);}
          this.sqlManager.update(updateCrival, new Object[]{ criregCoeffi,UtilityMath.round((criregCoeffi*maxpun), decimali),id});
          }
        } 
    }
    if(formula == 2){
      List datiG1crival = this.sqlManager.getListVector(selectCrivalNum, new Object[]{idCridef,necvan,ngara});
      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        Long id = (Long) SqlManager.getValueFromVectorParam(datiG1crival.get(k),1).getValue();
        Double criregCoeffi = (Double) this.sqlManager.getObject("select g1crireg.coeffi from g1crireg where g1crireg.idcridef = ? and g1crireg.valmin <= ? and g1crireg.valmax > ?", new Object[]{idCridef,valnum,valnum});
        this.sqlManager.update(updateCrival, new Object[]{ criregCoeffi,UtilityMath.round((criregCoeffi*maxpun), decimali),id});
        }
    }
    if(formula == 3){
      List datiG1crival = this.sqlManager.getListVector(selectCrivalNum, new Object[]{idCridef,necvan,ngara});
      BigDecimal max = new BigDecimal(0);
      BigDecimal bigMaxpun = BigDecimal.valueOf(maxpun); 
      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        BigDecimal bigValnum= BigDecimal.valueOf(valnum);
        int res = bigValnum.compareTo(max);
        if(res > 0){max = bigValnum;}
      }
      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        BigDecimal bigValnum= BigDecimal.valueOf(valnum);
        Long id = (Long) SqlManager.getValueFromVectorParam(datiG1crival.get(k),1).getValue();
        BigDecimal criregCoeffi;
        if(max.compareTo(BigDecimal.ZERO)==0){
          criregCoeffi = new BigDecimal(0);
        }else{
          criregCoeffi = bigValnum.divide(max,precision, BigDecimal.ROUND_HALF_UP);
        }
        criregCoeffi = criregCoeffi.setScale(precision, BigDecimal.ROUND_HALF_UP);
        this.sqlManager.update(updateCrival, new Object[]{ criregCoeffi.doubleValue(),((criregCoeffi.multiply(bigMaxpun)).setScale(decimali, BigDecimal.ROUND_HALF_UP)).doubleValue(),id});
        }
    }
    if(formula == 4 || formula == 5 || formula == 6){
      BigDecimal x = null;
      if(formula == 4){
        x = BigDecimal.valueOf(0.8);
      }else{
        if(formula == 5){
          x = BigDecimal.valueOf(0.85);
        }else{
          if(formula == 6){
            x = BigDecimal.valueOf(0.9);
          }
        }
      }
      List datiG1crival = this.sqlManager.getListVector(selectCrivalNum, new Object[]{idCridef,necvan,ngara});
      BigDecimal media = new BigDecimal(0);
      BigDecimal max = new BigDecimal(0);
      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        BigDecimal bigValnum= BigDecimal.valueOf(valnum);
        media = media.add(bigValnum); 
        int res = bigValnum.compareTo(max);
        if(res > 0){max = bigValnum;}
      }
      media = media.divide(BigDecimal.valueOf(datiG1crival.size()),precision, BigDecimal.ROUND_HALF_UP);
      media.setScale(precision, BigDecimal.ROUND_HALF_UP);
      
      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        BigDecimal bigValnum= BigDecimal.valueOf(valnum);
        Long id = (Long) SqlManager.getValueFromVectorParam(datiG1crival.get(k),1).getValue();
        BigDecimal criregCoeffi = null;
        if(media.compareTo(BigDecimal.ZERO)==0 && bigValnum.compareTo(BigDecimal.ZERO)==0){
          criregCoeffi = new BigDecimal(0);
        }else{
          int res = bigValnum.compareTo(media);
          if(res <= 0){
            criregCoeffi = (x.multiply(bigValnum)).divide(media,precision, BigDecimal.ROUND_HALF_UP);
          }else{
            if(res > 0){
              if(media.compareTo(max)==0){
                //Non si dovrebbe mai entrare in questa casistica, perchè max == media solo se tutti i valori offerti sono uguali, quindi avremmo valnum == media
                //e si ricadrebbe nel caso precedente con res == 0
                criregCoeffi = new BigDecimal(0);
              }else{
                criregCoeffi = x.add(BigDecimal.valueOf(1).subtract(x).multiply((bigValnum.subtract(media)).divide((max.subtract(media)),precision, BigDecimal.ROUND_HALF_UP)));
              }
            }
          }
        }
        criregCoeffi = criregCoeffi.setScale(precision, BigDecimal.ROUND_HALF_UP);
        this.sqlManager.update(updateCrival, new Object[]{ criregCoeffi.doubleValue(),((criregCoeffi.multiply(BigDecimal.valueOf(maxpun))).setScale(decimali, BigDecimal.ROUND_HALF_UP)).doubleValue(),id});
        }
    }
    if(formula == 7){
      List datiG1crival = this.sqlManager.getListVector(selectCrivalNum, new Object[]{idCridef,necvan,ngara});
      BigDecimal min = null;
      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        BigDecimal bigValnum= BigDecimal.valueOf(valnum);
        if(min == null){
          min = bigValnum;
        }else{
          int res = bigValnum.compareTo(min);
          if(res < 0){
            min = bigValnum; 
          }
        }
      }
      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        BigDecimal bigValnum = BigDecimal.valueOf(valnum);
        Long id = (Long) SqlManager.getValueFromVectorParam(datiG1crival.get(k),1).getValue();
        BigDecimal criregCoeffi;
        if(bigValnum.compareTo(BigDecimal.ZERO)==0){
          criregCoeffi = new BigDecimal(1);
        }else{
          criregCoeffi = min.divide(bigValnum,precision, BigDecimal.ROUND_HALF_UP);
        }
        criregCoeffi = criregCoeffi.setScale(precision, BigDecimal.ROUND_HALF_UP);
        this.sqlManager.update(updateCrival, new Object[]{ criregCoeffi.doubleValue(),((criregCoeffi.multiply(BigDecimal.valueOf(maxpun))).setScale(decimali, BigDecimal.ROUND_HALF_UP)).doubleValue(),id});
        }
    }
    if(formula == 8 || formula == 9 || formula == 10){
      
      Double impapp = (Double)this.sqlManager.getObject("select impapp from gare where ngara = ?", new Object[]{ngara});
      BigDecimal bigImpapp = BigDecimal.valueOf(impapp);
      
      BigDecimal x = null;
      if(formula == 8){
        x = BigDecimal.valueOf(0.8);
      }else{
        if(formula == 9){
          x = BigDecimal.valueOf(0.85);
        }else{
          if(formula == 10){
            x = BigDecimal.valueOf(0.9);
          }
        }
      }
      List datiG1crival = this.sqlManager.getListVector(selectCrivalNum, new Object[]{idCridef,necvan,ngara});
      BigDecimal media = new BigDecimal(0);
      BigDecimal min = null;
      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        BigDecimal bigValnum= BigDecimal.valueOf(valnum);
        media = media.add(bigValnum); 
        if(min == null){
          min = bigValnum;
        }else{
          int res = bigValnum.compareTo(min);
          if(res < 0){
            min = bigValnum; 
          }
        }
      }
      media = media.divide(BigDecimal.valueOf(datiG1crival.size()),precision, BigDecimal.ROUND_HALF_UP);
      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        BigDecimal bigValnum= BigDecimal.valueOf(valnum);
        Long id = (Long) SqlManager.getValueFromVectorParam(datiG1crival.get(k),1).getValue();
        BigDecimal criregCoeffi = null;
        int res = bigValnum.compareTo(media);
        if(bigImpapp.compareTo(media)==0){
          criregCoeffi = new BigDecimal(0);
        }else{
          if(res >= 0){
            criregCoeffi = x.multiply((bigImpapp.subtract(bigValnum))).divide((bigImpapp.subtract(media)),precision, BigDecimal.ROUND_HALF_UP);
          }else{
            if(res < 0){
              if(min.compareTo(media)==0){
                //Non si dovrebbe mai entrare in questa casistica, perchè min == media solo se tutti i valori offerti sono uguali, quindi avremmo valnum == media
                //e si ricadrebbe nel caso precedente con res == 0
                criregCoeffi = new BigDecimal(0);
              }else{
                criregCoeffi = x.add((BigDecimal.valueOf(1).subtract(x)).multiply(((media.subtract(bigValnum)).divide((media.subtract(min)),precision, BigDecimal.ROUND_HALF_UP))));
              }
            }
          }
        }
        criregCoeffi = criregCoeffi.setScale(precision, BigDecimal.ROUND_HALF_UP);
        this.sqlManager.update(updateCrival, new Object[]{ criregCoeffi.doubleValue(),((criregCoeffi.multiply(BigDecimal.valueOf(maxpun))).setScale(decimali, BigDecimal.ROUND_HALF_UP)).doubleValue(),id});
      }
    }
  }
  
  
  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
