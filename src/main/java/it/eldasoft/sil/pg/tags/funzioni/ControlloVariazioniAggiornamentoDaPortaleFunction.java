/*
 * Created on 25/03/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoIscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.CategoriaType;
import it.eldasoft.sil.portgare.datatypes.DocumentoType;
import it.eldasoft.sil.portgare.datatypes.ListaCategorieIscrizioneType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.xmlbeans.XmlException;

/**
 * Vengono eseguiti i controlli sulle variazioni apportate dal messaggio FS4
 * proveniente da Portale Alice
 * Valori ritornati: 1: ok
 *                   -1: ci sono messaggi precedenti non processati
 *                   -2: Errore
 *                   -3: Il richiedente non è presente in nell'elenco
 *                   -4: Il richiedente è presente in più RT
 *                   -5: ci sono messaggi FS4 per cui ci sono in sospeso richieste di aggiornamento per la ditta in oggetto
 *
 * @author Marcello Caminiti
 */
public class ControlloVariazioniAggiornamentoDaPortaleFunction extends
    AbstractFunzioneTag {

  static String     nomeFileXML_Aggiornamento = "dati_aggisc.xml";

  public ControlloVariazioniAggiornamentoDaPortaleFunction() {
    super(5, new Class[] { PageContext.class, String.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    FileAllegatoManager fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager",
        pageContext, FileAllegatoManager.class);

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        pageContext, TabellatiManager.class);

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        pageContext, PgManager.class);

    String ngara = (String) params[1];
    String idocmString = (String) params[2];
    Long idcom =  new Long(idocmString);
    String user = (String) params[3];
    String tipo = (String) params[4];
    String select="";
    boolean errori=false;

    //Si deve controllare se vi sono messaggi FS2 e FS4 relativi allo stesso elenco e allo stesso richiedente
    select="select count(IDPRG ) from w_invcom where idprg='PA' and COMKEY2 = ? and (COMTIPO = 'FS2' or COMTIPO = 'FS4') and COMKEY1 = ? and COMSTATO = '5' and IDCOM < ?";
    try {
      Long numOccorrenze = (Long) sqlManager.getObject(
          select, new Object[] { ngara,user,idcom });

      if(numOccorrenze!= null && numOccorrenze.longValue()>0)
        return "-1";
    } catch (SQLException e) {
      throw new JspException("Errore nella valutazione della presenza di richieste precedenti non processate", e);
    }

    //Determinazione del codice dell'impresa
    select="select USERKEY1 from w_puser where USERNOME = ? ";
    String codiceDitta;
    try {
      codiceDitta = (String)sqlManager.getObject(select, new Object[]{user});
    } catch (SQLException e2) {
      pageContext.setAttribute("erroreAcquisizione", "1",PageContext.REQUEST_SCOPE);
      throw new JspException("Errore nella lettura della tabella W_PUSER ", e2);
    }


    //Si controlla il richiedente è presente nell'elenco nel caso di FS4
    if("FS4".equals(tipo)){
      try {
        String datiControllo[] = pgManager.controlloEsistenzaDittaElencoGara(codiceDitta, ngara, "$" + ngara,null);
        if("0".equals(datiControllo[0]) || "2".equals(datiControllo[0])){
          if("0".equals(datiControllo[0]))
            return "-3";
          else if("2".equals(datiControllo[0]))
            return "-4";
        }else
          codiceDitta = datiControllo[1];
      } catch (GestoreException e) {
        throw new JspException("Errore nella valutazione della presenza del richiedente fra i partecipanti dell'elenco", e);
      }
    }



    //Vengono letti i documenti associati ad ogni occorrenza di di W_INVCOM
    select="select idprg,iddocdig from w_docdig where DIGENT = ? and digkey1 = ? and idprg = ? and dignomdoc = ? ";
    String digent="W_INVCOM";
    String idprgW_DOCDIG="PA";

    Vector datiW_DOCDIG = null;
    try {

        datiW_DOCDIG = sqlManager.getVector(select,
            new Object[]{digent, idcom.toString(), idprgW_DOCDIG, nomeFileXML_Aggiornamento});

    } catch (SQLException e) {
      pageContext.setAttribute("erroreAcquisizione", "1",PageContext.REQUEST_SCOPE);
      throw new JspException("Errore nella lettura della tabella W_DOCDIG ", e);
    }
    String idprgW_INVCOM = null;
    Long iddocdig = null;
    if(datiW_DOCDIG != null ){
      if(((JdbcParametro)datiW_DOCDIG.get(0)).getValue() != null)
        idprgW_INVCOM = ((JdbcParametro) datiW_DOCDIG.get(0)).getStringValue();

      if(((JdbcParametro)datiW_DOCDIG.get(1)).getValue() != null)
      try {
        iddocdig = ((JdbcParametro) datiW_DOCDIG.get(1)).longValue();
      } catch (GestoreException e2) {
        pageContext.setAttribute("erroreAcquisizione", "1",PageContext.REQUEST_SCOPE);
        throw new JspException("Errore nella lettura della tabella W_DOCDIG ", e2);
      }


      //Lettura del file xml immagazzinato nella tabella W_DOCDIG
      BlobFile fileAllegato = null;
      try {
        fileAllegato = fileAllegatoManager.getFileAllegato(idprgW_INVCOM,iddocdig);
      } catch (Exception e) {
        pageContext.setAttribute("erroreAcquisizione", "1",PageContext.REQUEST_SCOPE);
        throw new JspException("Errore nella lettura del file allegato presente nella tabella W_DOCDIG", e);
      }
      String xml=null;
      if(fileAllegato!=null && fileAllegato.getStream()!=null){
        xml = new String(fileAllegato.getStream());
        try {
          AggiornamentoIscrizioneImpresaElencoOperatoriDocument document = AggiornamentoIscrizioneImpresaElencoOperatoriDocument.Factory.parse(xml);

          //Controllo delle variazioni sulle categorie
          String msgCategorie = this.controlloCategorie(document, ngara, codiceDitta, sqlManager, tabellatiManager, pgManager);

          if(!"".equals(msgCategorie) && "FS4".equals(tipo)){
            boolean saltaTracciaturaCategorie = false;
            select = "select abilitaz, numordpl from ditg where ngara5=? and dittao=?";
            Vector datiDITG = sqlManager.getVector(select, new Object[]{ngara,codiceDitta});
            if(datiDITG!=null && datiDITG.size()>0){
              Long abilitaz = SqlManager.getValueFromVectorParam(datiDITG, 0).longValue();
              Long numordpl = SqlManager.getValueFromVectorParam(datiDITG, 1).longValue();
              if(new Long(1).equals(abilitaz) || numordpl!=null)
                saltaTracciaturaCategorie = true;
            }

            if(saltaTracciaturaCategorie ){
              boolean saltareAggCateg = false;
              //Si deve confrontare la porzione di xml relativa alle categorie della comunicazione corrente, con quella
              //del xml a cui punta la riga in garacquisiz, se sono uguali vuol dire che non ci sono modifiche alle categorie,
              //quindi si può procedere all'acquisizione
              select="select w.idprg,w.iddocdig from w_docdig w, garacquisiz g where w.digent = ? and w.digkey1 = " + sqlManager.getDBFunction("inttostr",  new String[] {"g.idcom"}) +
                  "  and w.idprg = ? and w.dignomdoc = ? and w.idprg=g.idprg and g.ngara=? and g.codimp=? and g.stato=?";
              Vector datiComunicazioneGaracquisiz = sqlManager.getVector(select, new Object[]{digent, idprgW_DOCDIG,nomeFileXML_Aggiornamento,ngara, codiceDitta, new Long(1)});
              if(datiComunicazioneGaracquisiz!= null && datiComunicazioneGaracquisiz.size()>0){
                idprgW_INVCOM = ((JdbcParametro) datiComunicazioneGaracquisiz.get(0)).getStringValue();
                iddocdig = ((JdbcParametro) datiComunicazioneGaracquisiz.get(1)).longValue();
                BlobFile fileAllegatoGaracquisiz = null;
                try {
                  fileAllegatoGaracquisiz = fileAllegatoManager.getFileAllegato(idprgW_INVCOM,iddocdig);
                  String xmlGarecquisiz=null;
                  if(fileAllegatoGaracquisiz!=null && fileAllegatoGaracquisiz.getStream()!=null){
                    xmlGarecquisiz = new String(fileAllegatoGaracquisiz.getStream());
                    document = AggiornamentoIscrizioneImpresaElencoOperatoriDocument.Factory.parse(xml);
                    AggiornamentoIscrizioneImpresaElencoOperatoriDocument documentGareacquisiz = AggiornamentoIscrizioneImpresaElencoOperatoriDocument.Factory.parse(xmlGarecquisiz);
                    ListaCategorieIscrizioneType listaCategorieIscrizione = document.getAggiornamentoIscrizioneImpresaElencoOperatori().getCategorieIscrizione();
                    ListaCategorieIscrizioneType listaCategorieIscrizioneAcquisiz = documentGareacquisiz.getAggiornamentoIscrizioneImpresaElencoOperatori().getCategorieIscrizione();
                    if(!listaCategorieIscrizione.xmlText().equals(listaCategorieIscrizioneAcquisiz.xmlText())){
                      //Sono presenti delle variazioni alle categorie
                      return "-5";
                    }else{
                      //Non ci sono variazioni per le categorie rispetto a quelle in sospeso in GARACQUISIZ
                      msgCategorie="";
                      saltareAggCateg = true;
                    }
                  }
                } catch (Exception e) {
                  pageContext.setAttribute("erroreAcquisizione", "1",PageContext.REQUEST_SCOPE);
                  throw new JspException("Errore nella lettura del file allegato presente nella tabella W_DOCDIG", e);
                }
              }else{
                saltareAggCateg = true;
                //Non ci sono occorrenze in GARACQUISIZ e ci sono aggiornamenti delle categorie
                this.getRequest().setAttribute("msgCategorie", msgCategorie);
                msgCategorie="\nPresenti modifiche alle categorie d'iscrizione. " +
                		"\nPoichè l'operatore richiedente risulta già abilitato in elenco, tali modifiche non vengono elaborate subito, " +
                		"ma devono essere prese in carico successivamente a questa acquisizione direttamente " +
                		"nella form di dettaglio delle categorie d'iscrizione dell'operatore stesso (fase 'Apertura domande di iscrizione')\n";
              }
              this.getRequest().setAttribute("saltareAggCateg", saltareAggCateg);
            }
          }

          //Controllo documenti
          String msgDocumenti = this.controlloDocumenti(document);

          //Controllo variazioni coordinatore sicurezza
          String msgCoordinatoreSic="";
          if("FS4".equals(tipo)){
            String coordsicGarealbo=(String)sqlManager.getObject("select coordsic from garealbo where ngara=?", new Object[]{ngara});
            if("1".equals(coordsicGarealbo)){
              String coordsicOld = (String)sqlManager.getObject("select coordsic from ditg where ngara5=? and dittao=?", new Object[]{ngara,codiceDitta});
              String coordsicNew="";
              if(document.getAggiornamentoIscrizioneImpresaElencoOperatori().isSetRequisitiCoordinatoreSicurezza()){
                boolean reqCoordinatoreSicurezza= document.getAggiornamentoIscrizioneImpresaElencoOperatori().getRequisitiCoordinatoreSicurezza();
                if(reqCoordinatoreSicurezza)
                  coordsicNew = "1";
                else
                  coordsicNew = "2";
              }

              if("1".equals(coordsicNew))
                coordsicNew = "Si";
              else if ("2".equals(coordsicNew))
                coordsicNew = "No";

              if("1".equals(coordsicOld))
                coordsicOld = "Si";
              else if ("2".equals(coordsicOld))
                coordsicOld = "No";
              else
                coordsicOld = "";

              if(!coordsicOld.equals(coordsicNew)){
                  msgCoordinatoreSic+="\nIl campo Possesso requisiti coordinatore sicurezza? viene impostato a '" + coordsicNew + "'\n";
              }
            }
          }

          String msg = msgCategorie + msgDocumenti + msgCoordinatoreSic;
          pageContext.setAttribute("messaggi", msg,PageContext.REQUEST_SCOPE);
        } catch (XmlException e) {
          this.getRequest().setAttribute("erroreAcquisizione", "1");
          throw new JspException("Errore nella lettura del file XML", e);
        } catch (SQLException e) {
          this.getRequest().setAttribute("erroreAcquisizione", "1");
          throw new JspException("Errore nella lettura dei dati per potere procedere con la valutazione delle modifiche contenute nell'aggiornamento", e);
        }catch (GestoreException e) {
          this.getRequest().setAttribute("erroreAcquisizione", "1");
          throw new JspException("Errore nella lettura della tabella DITG", e);
        }

      }else{
        errori=true;
      }
    }else{
      errori=true;
    }

    if(errori){
      pageContext.setAttribute("erroreAcquisizione", "1",PageContext.REQUEST_SCOPE);
      return "-2";
    }

    return "1";
  }

  /**
   * Viene prodotto il log con i nuovi documenti da inserire
   * @param document
   *
   */
  private String controlloDocumenti(AggiornamentoIscrizioneImpresaElencoOperatoriDocument document) {
    String msg="";
    //ListaDocumentiType listaDocumenti = ((AggiornamentoIscrizioneImpresaElencoOperatoriDocument)document).getAggiornamentoIscrizioneImpresaElencoOperatori().getDocumenti();
    ListaDocumentiType listaDocumenti = document.getAggiornamentoIscrizioneImpresaElencoOperatori().getDocumenti();

    if(listaDocumenti!=null){
      for (int j = 0; j < listaDocumenti.sizeOfDocumentoArray(); j++) {
        DocumentoType datoCodificato = listaDocumenti.getDocumentoArray(j);
        String nomeFile = datoCodificato.getNomeFile();
        String descrizione = datoCodificato.getDescrizione();
        msg+="\nViene inserito il documento '"+ nomeFile +"' - " + descrizione + "\n";


      }

    }

    return msg;
  }

  /**
   * Viene prodotto il log con le variazioni sulle categorie
   * @param document
   * @param ngara
   * @param codiceDitta
   * @param sqlManager
   * @param tabellatiManager
   * @param pgManager
   *
   * @throws JspException
   *
   */
  public String controlloCategorie(AggiornamentoIscrizioneImpresaElencoOperatoriDocument document,String ngara, String codiceDitta,
      SqlManager sqlManager,TabellatiManager tabellatiManager,PgManager pgManager) throws JspException{
    String msg="";
    String select="";
    boolean categoriaTrovata = false;
    boolean invitiPresenti = false;

    try {
      select="select codcat,infnumclass,supnumclass,invrea,tipcat,ultinf from iscrizcat where ngara=? and codgar=? and codimp=? and codcat<>'0' order by codcat";

      //Categorie in DB
      List listaDatiCategorie = sqlManager.getListVector(select, new Object[]{ngara, "$" + ngara, codiceDitta});

      //Categorie provenienti da Portale
      ListaCategorieIscrizioneType listaCategorieIscrizione = document.getAggiornamentoIscrizioneImpresaElencoOperatori().getCategorieIscrizione();

      ListaCategorieIscrizioneType listaNuovaCategorieIscrizione = document.getAggiornamentoIscrizioneImpresaElencoOperatori().getCategorieIscrizione();

      //Ricerca delle categorie che sono presenti in db e che verranno cancellate o modidificate
      if(listaDatiCategorie != null && listaDatiCategorie.size()>0){
        String infnumclassString = null;
        String supnumclassString = null;
        boolean classificaInfDiff = false;
        boolean classificaSupDiff = false;
        String ultDati = null;
        String nota=null;
        for (int i = 0; i < listaDatiCategorie.size(); i++) {
          Vector tmp = (Vector) listaDatiCategorie.get(i);
          String tmpCodcat = ((JdbcParametro) tmp.get(0)).getStringValue();
          Long tmpInfnumclassLong = ((JdbcParametro) tmp.get(1)).longValue();
          Long tmpSupnumclassLong = ((JdbcParametro) tmp.get(2)).longValue();
          Long tmpInvrea = ((JdbcParametro) tmp.get(3)).longValue();

          Long tipcat = ((JdbcParametro) tmp.get(4)).longValue();
          //Codice del tabellato contenente la descrizione breve della classifica dipendente dalla tipologia della categoria
          String codiceTabellato = this.getTabellatoClassifica(tipcat);

          categoriaTrovata = false;
          invitiPresenti = false;

          if(tmpInvrea != null && tmpInvrea.longValue()>0)
            invitiPresenti=true;

          ultDati = ((JdbcParametro) tmp.get(5)).stringValue();
          if(ultDati==null)
            ultDati ="";

          if(listaCategorieIscrizione != null && listaCategorieIscrizione.sizeOfCategoriaArray()>0){
            for (int j = 0; j < listaCategorieIscrizione.sizeOfCategoriaArray(); j++) {
              CategoriaType datoCodificato = listaCategorieIscrizione.getCategoriaArray(j);
              String categoria = datoCodificato.getCategoria();

              if(tmpCodcat.equals(categoria)){
                categoriaTrovata = true;

                String isfoglia = pgManager.isfoglia(categoria);
                if("1".equals(isfoglia)){
                  Long infnumclass = null;
                  if (datoCodificato.isSetClassificaMinima()){
                    infnumclassString = datoCodificato.getClassificaMinima();
                    if(infnumclassString!=null && !"".equals(infnumclassString))
                      infnumclass = Long.parseLong(infnumclassString);
                  }

                  Long supnumclass = null;
                  if(datoCodificato.isSetClassificaMassima()){
                    supnumclassString = datoCodificato.getClassificaMassima();
                    if(supnumclassString!=null && !"".equals(supnumclassString))
                      supnumclass =  Long.parseLong(supnumclassString);
                  }
                  nota = "";
                  if(datoCodificato.isSetNota()){
                    nota = datoCodificato.getNota();
                  }
                  if(nota==null)
                    nota="";

                  classificaInfDiff = false;
                  if( (tmpInfnumclassLong==null && infnumclass!=null) || (tmpInfnumclassLong!=null && infnumclass==null) || (tmpInfnumclassLong!=null && infnumclass!=null && !tmpInfnumclassLong.equals(infnumclass))){
                    classificaInfDiff = true;
                    String valoreOrignale = "";
                    String valoreNuovo = "";

                    if(tmpInfnumclassLong != null)
                       valoreOrignale = tabellatiManager.getDescrTabellato(codiceTabellato, String.valueOf(tmpInfnumclassLong));

                    if(infnumclass != null)
                      valoreNuovo = tabellatiManager.getDescrTabellato(codiceTabellato, String.valueOf(infnumclass));

                    msg+="\nPer la categoria " + categoria + " la classifica minima e' cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + valoreOrignale + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + valoreNuovo + "\n";

                  }

                  classificaSupDiff = false;
                  if((tmpSupnumclassLong==null && supnumclass!=null) || (tmpSupnumclassLong!=null && supnumclass==null) || (tmpSupnumclassLong!=null && supnumclass!=null && !tmpSupnumclassLong.equals(supnumclass))){
                    classificaSupDiff = true;
                    String valoreOrignale = "";
                    String valoreNuovo = "";

                    if(tmpSupnumclassLong != null)
                      valoreOrignale = tabellatiManager.getDescrTabellato(codiceTabellato, String.valueOf(tmpSupnumclassLong));

                    if(supnumclass != null)
                      valoreNuovo = tabellatiManager.getDescrTabellato(codiceTabellato, String.valueOf(supnumclass));

                    msg+="\nPer la categoria " + categoria + " la classifica massima e' cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + valoreOrignale + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + valoreNuovo + "\n";

                  }

                  if(invitiPresenti && (classificaInfDiff || classificaSupDiff))
                    msg+="\nPer tale categoria l'operatore risulta essere già stato invitato a delle gare.\n";

                  if(!(ultDati).equals(nota))
                    msg+="\nPer la categoria " + categoria + " sono variate le ulteriori informazioni.\n";
                }

                listaNuovaCategorieIscrizione.removeCategoria(j);
                break;
              }

            }
          }


          if(!categoriaTrovata){
            String isfoglia = pgManager.isfoglia(tmpCodcat);
            if("1".equals(isfoglia)){
              msg+="\nViene eliminata la categoria "+ tmpCodcat ;
              if(invitiPresenti)
                msg+=". Per tale categoria l'operatore risulta essere già stato invitato a delle gare.";
              msg+="\n";
            }
          }
        }
      }

      //Ricerca delle nuove categorie da inserire
      /*
      if(listaCategorieIscrizione!= null && listaCategorieIscrizione.sizeOfCategoriaArray()>0){
        for (int j = 0; j < listaCategorieIscrizione.sizeOfCategoriaArray(); j++) {
          CategoriaType datoCodificato = listaCategorieIscrizione.getCategoriaArray(j);
          String categoria = datoCodificato.getCategoria();
          boolean nuovaCategoria = true;
          if(listaDatiCategorie!= null){
            for (int i = 0; i < listaDatiCategorie.size(); i++) {
              Vector tmp = (Vector) listaDatiCategorie.get(i);
              String tmpCodcat = ((JdbcParametro) tmp.get(0)).getStringValue();
              if(categoria.equals(tmpCodcat)){
                nuovaCategoria = false;
                break;
              }
            }
          }
          if(nuovaCategoria){
            msg+="\nViene inserita la nuova categoria "+ categoria + "\n";
          }
        }
      }
      */
      if(listaNuovaCategorieIscrizione!= null && listaNuovaCategorieIscrizione.sizeOfCategoriaArray()>0){
        for (int j = 0; j < listaNuovaCategorieIscrizione.sizeOfCategoriaArray(); j++) {
          CategoriaType datoCodificato = listaNuovaCategorieIscrizione.getCategoriaArray(j);
          String categoria = datoCodificato.getCategoria();
          String isfoglia = pgManager.isfoglia(categoria);
          if("1".equals(isfoglia)){
            msg+="\nViene inserita la nuova categoria "+ categoria;
            select="select TIPLAVG from CAIS where CAISIM = ?";
            if(datoCodificato.isSetClassificaMinima() || datoCodificato.isSetClassificaMassima()){
              Long tipcat = (Long)sqlManager.getObject(select,
                new Object[]{categoria});
              String codiceTabellato = this.getTabellatoClassifica(tipcat);
              if(datoCodificato.isSetClassificaMinima()){
                msg+= " con classifica minima " + tabellatiManager.getDescrTabellato(codiceTabellato, datoCodificato.getClassificaMinima());
                if(datoCodificato.isSetClassificaMassima())
                  msg+= " e";
              }
              if(datoCodificato.isSetClassificaMassima())
                msg+= " con classifica massima " + tabellatiManager.getDescrTabellato(codiceTabellato, datoCodificato.getClassificaMassima());
              msg+="\n";
            }
          }

        }
      }
    }catch (SQLException e) {
      throw new JspException("Errore nella lettura dei dati delle categorie", e);
    } catch (GestoreException e) {
      throw new JspException("Errore nella lettura dei dati delle categorie", e);
    }


    return msg;
  }

  private String getTabellatoClassifica(Long tipcat){
    String codiceTabellato = "A1015";
    if (tipcat.longValue() == 2)
      codiceTabellato = "G_035";
    else if (tipcat.longValue() == 3)
      codiceTabellato = "G_036";
    else if (tipcat.longValue() == 4)
      codiceTabellato = "G_037";
    else if (tipcat.longValue() == 5)
      codiceTabellato = "G_049";
    return codiceTabellato;
  }
}