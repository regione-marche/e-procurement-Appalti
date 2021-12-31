/*
 * Created on 06/09/10
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
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ElencoOperatoriManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard che si occupa di preparare i dati prelevati dalle
 * viste V_DITTE_ELECAT e V_DITTE_ELESUM per potere eseguire l'inserimento
 * delle ditte in gara sfruttando il gestore GestoreFasiRicezione
 *
 * @author Marcello Caminiti
 */
public class GestorePopupAssociaOperatoriEconomici extends GestoreFasiRicezione {

  static Logger               logger         = Logger.getLogger(GestorePopupAssociaOperatoriEconomici.class);

  @Override
  public String getEntita() {
    return "DITG";
  }

  public GestorePopupAssociaOperatoriEconomici() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupAssociaOperatoriEconomici(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer
      dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {


  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer dataColumnContainer)
      throws GestoreException {

    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);


    //Anche se la pagina è aperta in modifica, vi è la necessità di eseguire le operazioni
    //legate all'inserimento di una ditta, operazioni si trovano nel preInsert GestoreFasiRicezione,
    //quindi anche se sono nel preUpdate si richiama super.preInsert.

    String categoriaPrev=UtilityStruts.getParametroString(this.getRequest(),"categoriaPrev");
    String codiceditta=null;
    String ngaraElenco = UtilityStruts.getParametroString(this.getRequest(),"garaElenco");
    String  ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String  where = UtilityStruts.getParametroString(this.getRequest(),"where");
    String[] listaDitteSelezionate = this.getRequest().getParameterValues("keys");
    String ultimaAggiudicataria = UtilityStruts.getParametroString(this.getRequest(),"ultimaAggiudicataria");
    String aggnumord =  UtilityStruts.getParametroString(this.getRequest(),"aggnumord");
    String eseguireAggiornamentoNumOrdine = UtilityStruts.getParametroString(this.getRequest(),"eseguireAggiornamentoNumOrdine");
    String selezioneAutomaticaDitte = UtilityStruts.getParametroString(this.getRequest(),"selezioneAutomaticaDitte");
    String stazioneAppaltante = UtilityStruts.getParametroString(this.getRequest(),"stazioneAppaltante");
    String ctrlaggiu = UtilityStruts.getParametroString(this.getRequest(),"ctrlaggiu");
    String eseguireCalcoloImporto = UtilityStruts.getParametroString(this.getRequest(),"eseguireCalcoloImporto");

    String[] listaDittePerTracciatura = this.getRequest().getParameterValues("imprese");
    String[] listaDisabilitatoImportoSopraLimite = this.getRequest().getParameterValues("campoDisabilitatoImportoSopraLimite");
    String criterioRotazioneDesc = UtilityStruts.getParametroString(this.getRequest(),"criterioRotazioneDesc");
    String filtriUlteriori = UtilityStruts.getParametroString(this.getRequest(),"filtriUlteriori");
    String filtroCategoria = UtilityStruts.getParametroString(this.getRequest(),"filtroCategoria");
    String filtriZone = UtilityStruts.getParametroString(this.getRequest(),"filtriZone");
    String filtriAffidatariEsclusi = UtilityStruts.getParametroString(this.getRequest(),"filtriAffidatariEsclusi");

    String ctrlimpValorePeriodo = UtilityStruts.getParametroString(this.getRequest(),"ctrlimpValorePeriodo");
    String ctrlimp = UtilityStruts.getParametroString(this.getRequest(),"ctrlimp");
    String ctrlimpga = UtilityStruts.getParametroString(this.getRequest(),"ctrlimpga");
    if(ctrlimp!=null && !"".equals(ctrlimp)){
      Double ctrlimpDouble = Double.parseDouble(ctrlimp);
      ctrlimp = UtilityNumeri.convertiDouble(ctrlimpDouble) + "€";
    }

    int pgCorrente = Integer.parseInt(UtilityStruts.getParametroString(this.getRequest(),"pgCorrente"));

    //variabili per tracciatura eventi
    String oggEvento = ngara;
    String codEvento = "GA_SELEZIONE_DA_ELENCO_MANU";
    String descrEvento = "Inserimento ditte in gara mediante selezione manuale da elenco operatori/catalogo (cod.elenco: " + ngaraElenco + ")";
    if("true".equals(selezioneAutomaticaDitte)){
      codEvento = "GA_SELEZIONE_DA_ELENCO_AUTO";
      descrEvento = "Inserimento ditte in gara mediante selezione automatica da elenco operatori/catalogo (cod.elenco: " + ngaraElenco + ")";
    }
    String messageKey = null;
    int livEvento = 3;
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");

    if("1".equals(ultimaAggiudicataria)){
      codEvento = "GA_SELEZIONE_AGGIU_DA_ELENCO";
      descrEvento = "Inserimento ditte in gara mediante selezione ultima aggiudicataria da elenco operatori/catalogo  (cod.elenco: " + ngaraElenco + ")";
    }

    try{
      //Se c'è il flag per eseguire il calcolo del numero ordine ed il ricalcolo ancora non è
      //stato eseguito si procede al ricalcolo
      if("1".equals(aggnumord) && ("SI".equals(eseguireAggiornamentoNumOrdine) || "true".equals(selezioneAutomaticaDitte))){
        if("SI".equals(eseguireAggiornamentoNumOrdine)){
          oggEvento = ngaraElenco;
          codEvento = "GA_ASSEGNA_ORDINE_ELENCO";
          descrEvento ="Assegnazione numero ordine operatori in elenco o catalogo con modalità casuale, in fase di selezione operatori in gara (cod.gara " + ngara + " )";
        }
        GestoreAssegnaNumOrdine gestoreAssegnaNumOrdine = new GestoreAssegnaNumOrdine();
        gestoreAssegnaNumOrdine.setRequest(this.getRequest());
        try{
          gestoreAssegnaNumOrdine.modalitaCasuale(ngaraElenco, "3", status);
        }catch (Exception e){
          errMsgEvento = "Errore nel calcolo del numero ordine degli operatori dell'elenco" + ngara;
          this.getRequest().setAttribute("AGGIORNAMENTO_NUMORD", "NOK");
          throw new GestoreException("Errore nel calcolo del numero ordine degli operatori dell'elenco" + ngara, null, e);
        }
        if("SI".equals(eseguireAggiornamentoNumOrdine)){
          livEvento=1;
          errMsgEvento="";
          this.getRequest().setAttribute("AGGIORNAMENTO_NUMORD", "OK");
          return;
        }

      }

      //Se per l'elenco è previsto il controllo sull'importo aggiudicato nel periodo dei singoli operatori (CRTRLAGGIU.GAREALBO = 1,2),
      //si deve eseguire il calcolo dell'importo aggiudicato
      if(("1".equals(ctrlaggiu) || "2".equals(ctrlaggiu)) && "SI".equals(eseguireCalcoloImporto)){
        oggEvento = ngaraElenco;
        codEvento = "GA_IMPORTOAGG_ELENCO";
        descrEvento ="Calcolo importo aggiudicato nel periodo dell'operatore in elenco/catalogo";
        ElencoOperatoriManager elencoOperatoriManager = (ElencoOperatoriManager) UtilitySpring.getBean("elencoOperatoriManager",
            this.getServletContext(), ElencoOperatoriManager.class);
        try {
          elencoOperatoriManager.conteggioImportoAggiudicatoNelPeriodo(ngaraElenco);
        } catch (Exception e) {
          livEvento = 3;
          this.getRequest().setAttribute("AGGIORNAMENTO_IMPORTO", "NOK");
          errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.calcoloImportoAggiudicatoPeriodo");
          throw new GestoreException(errMsgEvento, "calcoloImportoAggiudicatoPeriodo",e);
        }
        livEvento=1;
        errMsgEvento="";
        this.getRequest().setAttribute("AGGIORNAMENTO_IMPORTO", "OK");
        return;
      }

      //Nel caso di selezione da elenco operatori economici ultima aggiudicataria
      //vi sono dei controlli preliminari da effettuare
      String select="";
      StringBuffer elencoDitteGiaPresenti= new StringBuffer();
      StringBuffer elencoDitteNonAttive= new StringBuffer();
      StringBuffer elencoDitteNonIscrittePerCategoria=new StringBuffer();
      String codiceDittaTmp = null;
      String codiceGara = null;
      String ragsoc = null;

      ArrayList<String> codiciDitteSel = new ArrayList<String>();
      String lastDittaSelezionata = "";

      if("1".equals(ultimaAggiudicataria)){

        try {
          codiceGara = (String)sqlManager.getObject("select codgar1 from gare where ngara=?", new Object[]{ngara});
        } catch (SQLException e) {
          throw new GestoreException("Errore nella lettura del codice gara della gara " + ngara, null, e);
        }
        for (int i = 0; i < listaDitteSelezionate.length; i++) {
          String[] valoriDittaSelezionata = listaDitteSelezionate[i].split(";");
          if(valoriDittaSelezionata.length <= 2 ){

            codiceditta= valoriDittaSelezionata[0];
            codiciDitteSel.add(codiceditta);
            lastDittaSelezionata = codiceditta;
            ragsoc = valoriDittaSelezionata[1];

            select="select dittao from ditg where ngara5=? and codgar5=? and dittao=?";
            try {
              codiceDittaTmp = (String)sqlManager.getObject(select, new Object[]{ngara,codiceGara,codiceditta});
              if(codiceDittaTmp!=null && !"".equals(codiceDittaTmp)){
                if(elencoDitteGiaPresenti.length()==0)
                  elencoDitteGiaPresenti.append("<br><ul>");

                elencoDitteGiaPresenti.append("<li style=\"list-style-type: disc;margin-left: 30px;\" >");
                elencoDitteGiaPresenti.append(ragsoc);
                elencoDitteGiaPresenti.append("</li>");
              }

              select="select abilitaz, dabilitaz, numordpl from ditg where ngara5=? and codgar5=? and dittao=?";
              Vector datiDitg = sqlManager.getVector(select, new Object[]{ngaraElenco, "$" + ngaraElenco, codiceditta});
              Long abilitaz = null;
              Date dabilitaz = null;
              Long numordpl = null;
              if(datiDitg!= null && datiDitg.size()>0){
                if(datiDitg.get(0)!= null)
                  abilitaz = (Long)((JdbcParametro)datiDitg.get(0)).getValue();
                if(datiDitg.get(1)!=null)
                  dabilitaz = (Date)((JdbcParametro)datiDitg.get(1)).getValue();
                if(datiDitg.get(2)!=null)
                  numordpl = (Long)((JdbcParametro)datiDitg.get(2)).getValue();
              }


              if(!(abilitaz!=null && abilitaz.longValue()==1 && dabilitaz !=null && numordpl!=null)){
                if(elencoDitteNonAttive.length()==0)
                  elencoDitteNonAttive.append("<br><ul>");

                elencoDitteNonAttive.append("<li style=\"list-style-type: disc;margin-left: 30px;\" >");
                elencoDitteNonAttive.append(ragsoc);
                elencoDitteNonAttive.append("</li>");

              }

              //Si controlla se la ditta è presente in elenco
              select="select dittao from ditg where ngara5=? and codgar5=? and dittao=?";
              codiceDittaTmp = (String)sqlManager.getObject(select, new Object[]{ngaraElenco, "$" + ngaraElenco,codiceditta});

              //Si determina il tipo algo dell'elenco
              Long tipoalgo = null;
              try {
                tipoalgo = (Long)sqlManager.getObject("select tipoalgo from garealbo where ngara=? and codgar= ?", new Object[]{ngaraElenco, "$" + ngaraElenco});
              } catch (SQLException e1) {
                throw new GestoreException("Errore nella lettura della tipo algoritmo dell'elenco ", null, e1);
              }

              if(categoriaPrev!=null && codiceDittaTmp!=null && !"".equals(codiceDittaTmp) && (tipoalgo.longValue()==1 || tipoalgo.longValue()==3 || tipoalgo.longValue()==4
                  || tipoalgo.longValue()==5 || tipoalgo.longValue()==11 || tipoalgo.longValue()==12 || tipoalgo.longValue()==14 || tipoalgo.longValue()==15)){
                Long tiplavg = (Long)sqlManager.getObject("select tiplavg from cais where caisim=?", new Object[]{categoriaPrev});

                select="select count(codgar) from iscrizcat where codgar=? and ngara=? and codimp=? and codcat=? and tipcat=?";
                Long conteggio = (Long)sqlManager.getObject(select, new Object[]{"$" + ngaraElenco,ngaraElenco,codiceditta,categoriaPrev,tiplavg});
                if(!(conteggio!=null && conteggio.longValue()==1 )){
                  if(elencoDitteNonIscrittePerCategoria.length()==0)
                    elencoDitteNonIscrittePerCategoria.append("<br><ul>");

                  elencoDitteNonIscrittePerCategoria.append("<li style=\"list-style-type: disc;margin-left: 30px;\" >");
                  elencoDitteNonIscrittePerCategoria.append(ragsoc);
                  elencoDitteNonIscrittePerCategoria.append("</li>");
                }
              }


            } catch (SQLException e) {
              throw new GestoreException("Errore nella lettura dei dati della ditta " + codiceditta, null, e);
            }
          }
        }

        String msg=null;
        if(elencoDitteGiaPresenti.length()>0){
          if(listaDitteSelezionate.length==1)
            msg = "La ditta selezionata risulta già inserita in gara";
          else{
            elencoDitteGiaPresenti.append("</ul>");
            msg = "Le seguenti ditte selezionate risultano già inserite in gara: " + elencoDitteGiaPresenti.toString();
          }

        }

        if(elencoDitteNonAttive.length()>0){
          if(listaDitteSelezionate.length==1){
            if (msg!=null)
              msg+="<br>";
            else
              msg="";
            msg += "La ditta selezionata non risulta presente o attiva in elenco.<br>Per inserirla comunque in gara utilizzare la funzione 'Aggiungi ditta da anagrafica'.";

          }else{
            elencoDitteNonAttive.append("</ul>");
            if (msg!=null)
              msg+="<br>";
            else
              msg="";
            msg += "Le seguenti ditte selezionate non risultano presenti o attive in elenco: " + elencoDitteNonAttive.toString() +".<br>Per inserirle comunque in gara utilizzare la funzione 'Aggiungi ditta da anagrafica'.";
          }

        }

        if(elencoDitteNonIscrittePerCategoria.length()>0){
          if(listaDitteSelezionate.length==1){
            if (msg!=null)
              msg+="<br>";
            else
              msg="";
            msg += "La ditta selezionata non risulta essere iscritta in elenco per la categoria o prestazione prevalente della gara.<br>Per inserirla comunque in gara utilizzare la funzione 'Aggiungi ditta da anagrafica'.";
          }else{
            elencoDitteNonIscrittePerCategoria.append("</ul>");
            if (msg!=null)
              msg+="<br>";
            else
              msg="";
            msg += "Le seguenti ditte selezionate non risultano essere iscritte in elenco per la categoria o prestazione prevalente della gara: " + elencoDitteNonIscrittePerCategoria.toString()+".<br>Per inserirle comunque in gara utilizzare la funzione 'Aggiungi ditta da anagrafica'.";
          }

        }
        if(msg!=null){
          livEvento = 3;
          messageKey = "errors.gestoreException.*.selezElencoltimaAggiudicataria";
          errMsgEvento = this.resBundleGenerale.getString(messageKey);
          SQLException e = new SQLException();
          throw new GestoreException("Errore inserimento ditta da funzione Selezione da elenco ultima aggiudicataria", "selezElencoltimaAggiudicataria",new Object[]{msg}, e);
        }

      }

      int numeroOperatoriDaSelezionare=listaDitteSelezionate.length;
      if("true".equals(selezioneAutomaticaDitte)){
        String numeroSelezionati = UtilityStruts.getParametroString(this.getRequest(),"numeroSelezionati");
        numeroOperatoriDaSelezionare = Integer.parseInt(numeroSelezionati);
      }

      for (int i = 0; i < numeroOperatoriDaSelezionare; i++) {
        String[] valoriDittaSelezionata = listaDitteSelezionate[i].split(";");
        ragsoc = null;
        if (valoriDittaSelezionata.length <= 2 && !"1".equals(ultimaAggiudicataria)) {
          int indice=0;
          ngaraElenco= valoriDittaSelezionata[0];
          indice=ngaraElenco.indexOf(":");
          ngaraElenco = ngaraElenco.substring(indice +1);
          codiceditta= valoriDittaSelezionata[1];
          indice=codiceditta.indexOf(":");
          codiceditta=codiceditta.substring(indice + 1);
          codiciDitteSel.add(codiceditta);
          lastDittaSelezionata = codiceditta;

          /*
          select="select ragsoc from v_ditte_elecat where codice = ?";

          //Se il codice categoria è nullo, allora l'entità su cui si sta lavorando
          //è la v_ditte_elesum
          if(categoriaPrev==null)
            select="select ragsoc from v_ditte_elesum where codice = ?";

          if (where!=null && !"".equals(where)){
            select = select + " and " + where;
          }
          */
          select="select nomimp from impr where codimp = ?";

          try {
            ragsoc = (String) sqlManager.getObject(
                select, new Object[] { codiceditta });



          } catch (SQLException e) {
            throw new GestoreException("Errore nella lettura della ragione sociale della ditta ", null, e);
          }
        }else if(valoriDittaSelezionata.length <= 2 && "1".equals(ultimaAggiudicataria)){
          ngaraElenco = UtilityStruts.getParametroString(this.getRequest(),"garaElenco");
          codiceditta= valoriDittaSelezionata[0];
          if(valoriDittaSelezionata.length == 2)
            ragsoc = valoriDittaSelezionata[1];
        }

        if(codiceditta!=null && !"".equals(codiceditta)){
          //campi chiave di ditg
          Vector elencoCampi = new Vector();
          elencoCampi.add(new DataColumn("DITG.NGARA5",
              new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
          elencoCampi.add(new DataColumn("DITG.CODGAR5",
              new JdbcParametro(JdbcParametro.TIPO_TESTO, null)));
          elencoCampi.add(new DataColumn("DITG.DITTAO",
              new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceditta)));

          //campi che si devono inserire perchè adoperati nel gestore
          //GestoreFasiRicezione
          elencoCampi.add(new DataColumn("DITG.NOMIMO",
              new JdbcParametro(JdbcParametro.TIPO_TESTO, ragsoc)));
          elencoCampi.add(new DataColumn("DITG.NPROGG",
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
          elencoCampi.add(new DataColumn("DITG.NUMORDPL",
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
          elencoCampi.add(new DataColumn("DITG.ACQUISIZIONE",
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(3))));
          if("true".equals(selezioneAutomaticaDitte))
            elencoCampi.add(new DataColumn("DITG.ACQAUTO",
                new JdbcParametro(JdbcParametro.TIPO_TESTO, "1")));
          DataColumnContainer containerDITG = new DataColumnContainer(elencoCampi);

          super.preInsert(status, containerDITG);

          //Se l'impresa è un consorzio (tipimp=2,11) si devono caricare in ragdet le componenti del consorzio
          //della ditta
          try {
            Long tipimp= (Long)sqlManager.getObject("select tipimp from impr where codimp= ?", new Object[]{codiceditta});
            if(tipimp!=null && (tipimp.longValue()==2 || tipimp.longValue()==11)){
              List datiRagdet = sqlManager.getListVector("select coddic from ragdet where codimp=? and ngara=?", new Object[]{codiceditta,ngaraElenco});
              if(datiRagdet!=null && datiRagdet.size()>0){
                String coddic=null;
                Long numdic=null;
                for(int j=0;j<datiRagdet.size();j++){
                  coddic = SqlManager.getValueFromVectorParam(datiRagdet.get(j), 0).stringValue();
                  numdic= (Long)this.sqlManager.getObject("select max(numdic) from ragdet where codimp=? and coddic=?",  new Object[]{codiceditta,coddic});
                  if(numdic==null)
                    numdic = new Long(0);
                  else
                    numdic = new Long(numdic.longValue() + 1);

                  this.sqlManager.update("insert into ragdet(codimp,coddic,numdic,ngara) values(?,?,?,?)", new Object[]{codiceditta,coddic,numdic,ngara});
                }
              }
            }
          } catch (SQLException e) {
            throw new GestoreException("Errore nell'inserimento delle componenti del raggruppamento ", null, e);
          }

          //Copia degli eventuali dati dell'avvalimento
          List listaOccorrenzeDaCopiare = null;
          DataColumnContainer campiDaCopiare = null;
          try {
            listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
                "select * from DITGAVVAL " + "where NGARA = ? and DITTAO = ? ", // codiceGaraDestinazione"
                new Object[] { ngaraElenco, codiceditta  });
            if (listaOccorrenzeDaCopiare != null
                && listaOccorrenzeDaCopiare.size() > 0) {
              campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
                  "DITGAVVAL", "select * from DITGAVVAL", new Object[] {});

              for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {
                campiDaCopiare.setValoriFromMap(
                    ((HashMap) listaOccorrenzeDaCopiare.get(row)), true);
                campiDaCopiare.getColumn("DITGAVVAL.ID").setChiave(true);
                Long newId = new Long(genChiaviManager.getNextId("DITGAVVAL"));
                campiDaCopiare.setValue("DITGAVVAL.ID", newId);
                campiDaCopiare.setValue("DITGAVVAL.NGARA", ngara);
                campiDaCopiare.insert("DITGAVVAL", this.geneManager.getSql());
              }

            }

          } catch (SQLException d) {
            throw new GestoreException("Errore nell'inserimento delle componenti del raggruppamento ", null, d);
          }




          //Devo ricavare il tipo della gara
          Long tipgen=null;
          try {
            String codgar = containerDITG.getString("DITG.CODGAR5");
            tipgen = (Long)sqlManager.getObject("select tipgen from torn where codgar=?", new Object[]{codgar});
          } catch (SQLException e) {
            throw new GestoreException("Errore nella lettura della tipo della gara ", null, e);
          }



          //Si determina il tipo algo dell'elenco
          Long tipoalgo = null;
          try {
            tipoalgo = (Long)sqlManager.getObject("select tipoalgo from garealbo where ngara=? and codgar= ?", new Object[]{ngaraElenco, "$" + ngaraElenco});
          } catch (SQLException e1) {
            throw new GestoreException("Errore nella lettura della tipo algoritmo dell'elenco ", null, e1);
          }

          String modo="INS";
          if(tipoalgo.longValue()==1 || tipoalgo.longValue()==3 || tipoalgo.longValue()==4 || tipoalgo.longValue()==5 || tipoalgo.longValue()==11 || tipoalgo.longValue()==12 || tipoalgo.longValue()==14 || tipoalgo.longValue()==15){
            String categoriaPrevTmp = categoriaPrev;
            if(categoriaPrevTmp==null || "".equals(categoriaPrevTmp)){
              categoriaPrevTmp = "0";
            }

            String codiceGaraElenco="$" + ngaraElenco;  //Gli elenchi hanno la caratteristica che codiceGara = $ + numeroGara
            pgManager.aggiornaNumInviti(codiceGaraElenco, ngaraElenco, codiceditta, categoriaPrevTmp, modo, tipgen,null,null);

            //Se per la categoria prevalente è stata specificata la classe, si devono calcolare gli inviti
            //sul dettaglio della classe della categoria d'iscrizione dell'operatore
            if(categoriaPrev!=null && !"".equals(categoriaPrev)){
              try {
                Long classifica = (Long)sqlManager.getObject("select numcla from catg where ngara=? and ncatg = ?", new Object[]{ngara, new Long(1)});
                if(classifica!=null){
                  pgManager.aggiornaNumInviti(codiceGaraElenco, ngaraElenco, codiceditta, categoriaPrev, modo, tipgen,classifica,null);
                }
              } catch (SQLException e) {
                throw new GestoreException("Errore nella lettura della classifica della categoria prevalente ", null, e);
              }
            }

          }else if(tipoalgo.longValue()==2 || tipoalgo.longValue()==6 || tipoalgo.longValue()==7 || tipoalgo.longValue()==10 || tipoalgo.longValue()==13){
            pgManager.aggiornaNumInviti("$" + ngaraElenco, ngaraElenco, codiceditta, "0", modo, tipgen,null,null);
          }else if(tipoalgo.longValue()==8 || tipoalgo.longValue()==9){
            //Si deve fare il conteggio sulla categoria '0'
            pgManager.aggiornaNumInviti("$" + ngaraElenco, ngaraElenco, codiceditta, "0", modo, tipgen,null,null);
            //Si deve fare il conteggio anche su ISCRIZUFF.INVREA sempre per la categoria '0'
            pgManager.aggiornaNumInviti("$" + ngaraElenco, ngaraElenco, codiceditta, "0", modo, tipgen,null,stazioneAppaltante);
          }


        }
      }

      if(listaDittePerTracciatura!= null){
        if(!"1".equals(ultimaAggiudicataria)){
          errMsgEvento = "Criterio di rotazione: " + criterioRotazioneDesc;
          if("1".equals(aggnumord)){
            errMsgEvento+= " con assegnazione numero ordine con modalità casuale.";
          }
          if(filtroCategoria == null || "".equals(filtroCategoria))
            filtroCategoria = "nessuna categoria selezionata";
          errMsgEvento = errMsgEvento+ "\nCriterio di filtro: Vengono considerate le ditte abilitate all'elenco operatori economici "+
                "a cui è stato assegnato un numero ordine e qualificate per le seguenti categorie o prestazioni della gara corrente:\n" + filtroCategoria;
          if((filtriUlteriori != null && !"".equals(filtriUlteriori)) || (filtriZone != null && !"".equals(filtriZone))){
            errMsgEvento = errMsgEvento+ "\nGli operatori sono ulteriormente filtrati in base ai seguenti criteri:";
          }
          if(filtriUlteriori != null && !"".equals(filtriUlteriori)){
            errMsgEvento = errMsgEvento+ filtriUlteriori;
          }
          if(filtriZone != null && !"".equals(filtriZone)){
            errMsgEvento = errMsgEvento+ "\nZone di attività:" + filtriZone;
          }
          if(filtriAffidatariEsclusi != null && !"".equals(filtriAffidatariEsclusi)){
            errMsgEvento = errMsgEvento+ "\nViene escluso dalla selezione l'affidatario uscente:" + filtriAffidatariEsclusi;
          }
          if(("false".equals(selezioneAutomaticaDitte) && ("1".equals(ctrlaggiu) || "2".equals(ctrlaggiu))) ||
              ("true".equals(selezioneAutomaticaDitte) && "1".equals(ctrlaggiu))){
            errMsgEvento+="\nE' previsto il controllo sull'importo aggiudicato complessivo degli operatori: ";
            errMsgEvento+="per ogni operatore vengono considerate le procedure aggiudicate negli ultimi "+ ctrlimpValorePeriodo +" giorni. ";
            if("1".equals(ctrlimpga)){
              errMsgEvento+="\nNell'importo aggiudicato viene conteggiato anche l'importo a base di gara della procedura corrente. ";
            }
            errMsgEvento+="L'importo limite è "+ ctrlimp;
            if("1".equals(ctrlaggiu)){
              errMsgEvento+=" e gli operatori che superano tale limite non vengono selezionati. ";
            }
          }

          errMsgEvento = errMsgEvento + "\nN. operatori selezionati: " + numeroOperatoriDaSelezionare;
          if("true".equals(selezioneAutomaticaDitte)){
            errMsgEvento+= "\nSelezione automatica da elenco.";
          }else{
            errMsgEvento+= "\nSelezione manuale da elenco.";
          }
          if(pgCorrente != 0){
            errMsgEvento+= "\nSelezione su pagina della lista successiva alla prima (pag. " + (pgCorrente+1) + ").";
          }
        }else{
          errMsgEvento = "N. operatori selezionati: " + listaDitteSelezionate.length;
        }

        boolean ultima = false;
        for (int i = 0; i < listaDittePerTracciatura.length && !ultima; i++) {
          String codicedittaTracciatura;
          if("1".equals(ultimaAggiudicataria)){
            codicedittaTracciatura = listaDittePerTracciatura[i];
          }else{
            String[] dittaSingola = listaDittePerTracciatura[i].split(";");
            int indice;
            codicedittaTracciatura= dittaSingola[1];
            indice=codicedittaTracciatura.indexOf(":");
            codicedittaTracciatura=codicedittaTracciatura.substring(indice + 1);
          }
          String disabilitato = "";
          if(listaDisabilitatoImportoSopraLimite != null){
            disabilitato = listaDisabilitatoImportoSopraLimite[i];
          }
          String selezionata = "";
          if(codiciDitteSel.contains(codicedittaTracciatura)){
            selezionata = "selezionata";
          }else{
            if("true".equals(disabilitato)){
              selezionata = "NON selezionata (importo aggiudicato complessivo oltre il limite)";
            }else{
              selezionata = "NON selezionata";
            }
          }
          errMsgEvento = errMsgEvento + "\n" + (i+1) + " - " + codicedittaTracciatura + " - " + selezionata;
          if(codicedittaTracciatura.equals(lastDittaSelezionata)){
            ultima = true;
          }
        }
      }
      //best case
      livEvento = 1;
    }catch(GestoreException e){
      errMsgEvento = e.getMessage();
      this.getRequest().setAttribute("RISULTATO", "NOK");
      throw e;
    }finally{
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
        messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
      }
    }


  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

}