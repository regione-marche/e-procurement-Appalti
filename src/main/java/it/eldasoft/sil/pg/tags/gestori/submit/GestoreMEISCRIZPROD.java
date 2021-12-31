package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;

public class GestoreMEISCRIZPROD extends AbstractGestoreEntita {

  private GenChiaviManager        genChiaviManager        = null;

  @Override
  public String getEntita() {
    return "MEISCRIZPROD";
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager di Piattaforma Gare
    genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);

  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    Date dataOdierna = UtilityDate.getDataOdiernaAsDate();
    Timestamp dataOdiernaAsTime = new Timestamp(dataOdierna.getTime());
    Long idMeiscrizprod = datiForm.getLong("MEISCRIZPROD.ID");

    try {
      sqlManager.update("update mestoiscrizprod set datfin=? where idiscrizprod=? and datfin is null", new Object[]{dataOdiernaAsTime, idMeiscrizprod});
    } catch (SQLException e) {
      throw new GestoreException("Errore nella chiusura dello storico per il prodotto:" + idMeiscrizprod.toString(), null, e);
    }


    datiForm.setValue("MEISCRIZPROD.DATMOD", dataOdierna);

    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);

    Long id = new Long(genChiaviManager.getNextId("MESTOISCRIZPROD"));

    String codgarOriginale= StringUtils.stripToNull(datiForm.getColumn("MEISCRIZPROD.CODGAR").getOriginalValue().getStringValue());
    String codimpOriginale = StringUtils.stripToNull(datiForm.getColumn("MEISCRIZPROD.CODIMP").getOriginalValue().getStringValue());
    String ngaraOriginale= StringUtils.stripToNull(datiForm.getColumn("MEISCRIZPROD.NGARA").getOriginalValue().getStringValue());
    Long idartcatOriginale= datiForm.getColumn("MEISCRIZPROD.IDARTCAT").getOriginalValue().longValue();
    Long qunimisprzOriginale= datiForm.getColumn("MEISCRIZPROD.QUNIMISPRZ").getOriginalValue().longValue();
    Long qunimisacqOriginale= datiForm.getColumn("MEISCRIZPROD.QUNIMISACQ").getOriginalValue().longValue();
    Long tempoconsOriginale= datiForm.getColumn("MEISCRIZPROD.TEMPOCONS").getOriginalValue().longValue();
    String marcaprodutOriginale= StringUtils.stripToNull(datiForm.getColumn("MEISCRIZPROD.MARCAPRODUT").getOriginalValue().getStringValue());
    String codprodutOriginale= StringUtils.stripToNull(datiForm.getColumn("MEISCRIZPROD.CODPRODUT").getOriginalValue().getStringValue());
    String nomeOriginale= StringUtils.stripToNull(datiForm.getColumn("MEISCRIZPROD.NOME").getOriginalValue().getStringValue());
    String codoeOriginale= StringUtils.stripToNull(datiForm.getColumn("MEISCRIZPROD.CODOE").getOriginalValue().getStringValue());
    Double przunitOriginale= datiForm.getColumn("MEISCRIZPROD.PRZUNIT").getOriginalValue().doubleValue();
    Double przunitprodOriginale= datiForm.getColumn("MEISCRIZPROD.PRZUNITPROD").getOriginalValue().doubleValue();
    Object  descagg= datiForm.getColumn("MEISCRIZPROD.DESCAGG").getOriginalValue().getValue();
    String dimensioniOriginale= StringUtils.stripToNull(datiForm.getColumn("MEISCRIZPROD.DIMENSIONI").getOriginalValue().getStringValue());
    Long garanziaOriginale= datiForm.getColumn("MEISCRIZPROD.GARANZIA").getOriginalValue().longValue();
    Long percivaOriginale= datiForm.getColumn("MEISCRIZPROD.PERCIVA").getOriginalValue().longValue();
    Date datscadoffOriginale = datiForm.getColumn("MEISCRIZPROD.DATSCADOFF").getOriginalValue().dataValue();
    Long stato= datiForm.getLong("MEISCRIZPROD.STATO");

    String insert="insert into MESTOISCRIZPROD(id, datini, datfin, idiscrizprod,codgar, codimp, ngara, idartcat, qunimisprz, qunimisacq," +
    		"tempocons, marcaprodut, codprodut, nome, codoe, przunit, przunitprod, descagg, dimensioni, garanzia, " +
    		"perciva, datscadoff, stato) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    try {
      this.sqlManager.update(insert, new Object[]{id, dataOdiernaAsTime, null, idMeiscrizprod, codgarOriginale, codimpOriginale, ngaraOriginale,
          idartcatOriginale, qunimisprzOriginale, qunimisacqOriginale, tempoconsOriginale, marcaprodutOriginale, codprodutOriginale, nomeOriginale,
          codoeOriginale, przunitOriginale, przunitprodOriginale, descagg, dimensioniOriginale, garanziaOriginale,
          percivaOriginale, datscadoffOriginale, stato});
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'inserimento in MESTOISCRIZPROD dei dati relativi a MEISCRIZPROD con id = " + idMeiscrizprod.toString(), null, e);
    }

    //Copia occorrenze da mealliscrizprod a meallprod per immagine
    this.archiviazioneDocumenti(idMeiscrizprod, id, new Long(1));
    //Copia occorrenze da mealliscrizprod a meallprod per certificazioni
    this.archiviazioneDocumenti(idMeiscrizprod, id, new Long(2));
    //Copia occorrenze da mealliscrizprod a meallprod per schede tecniche
    this.archiviazioneDocumenti(idMeiscrizprod, id, new Long(3));

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

  private void archiviazioneDocumenti(Long idProdotto,Long idProdottoStoricizzato,Long tipo) throws GestoreException{

    String select="select idprg, iddocdig from mealliscrizprod where idiscrizprod=? and tipo=?";
    String insertMeallprod="insert into meallprod(id,idprod,idprg,iddocdig,tipo) values(?,?,?,?,?)";
    try {
      List<?> listaDatiDocumenti = this.sqlManager.getListVector(select,new Object[]{idProdotto, tipo});
      if(listaDatiDocumenti!=null & listaDatiDocumenti.size()>0){
        for (int i = 0; i < listaDatiDocumenti.size(); i++) {
          String idprg = (String) SqlManager.getValueFromVectorParam(listaDatiDocumenti.get(i), 0).getValue();
          Long iddocdig = (Long) SqlManager.getValueFromVectorParam(listaDatiDocumenti.get(i), 1).getValue();
          Long idMEALLPROD = new Long(genChiaviManager.getNextId("MEALLPROD"));
          this.sqlManager.update(insertMeallprod, new Object[]{idMEALLPROD,idProdottoStoricizzato,idprg,iddocdig,tipo});

        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore durante la storicizzazione di MEALLPROD del prodotto:" + idProdotto.toString(), null, e);
    }
  }

}
