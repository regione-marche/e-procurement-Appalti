/*
 * Created on 08-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pl.struts.gestori;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletRequest;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore per la tabella APPR delle approvazioni/perizie del lavoro
 * 
 * @author Marco.Franceschin
 */
public class GestoreAPPR extends AbstractGestoreChiaveNumerica {

  public String[] getAltriCampiChiave() {
    return new String[] { "CODLAV" };
  }

  public String getCampoNumericoChiave() {
    return "NAPRPR";
  }

  public String getEntita() {
    return "APPR";
  }

  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    GeneManager gene = (GeneManager) UtilitySpring.getBean("geneManager",
        this.getServletContext(), GeneManager.class);
    gene.deleteTabelle(new String[] { "aggi", "g2tecn", "dfina", "r2impf",
        "r2veri", "deli", "somm", "fina", "g2pare", "g2cdsp" , "ultapp_p" },
        "codlav = ? and naprpr = ?", new Object[] {
            impl.getString("APPR.CODLAV"), impl.getLong("APPR.NAPRPR") });

  }

  private Long getLong(String val) {
    if (val == null || val.length() == 0) return null;
    try {
      return Long.valueOf(val);
    } catch (Throwable t) {

    }
    return null;
  }

  private Double getDouble(String val) {
    if (val == null || val.length() == 0) return null;
    try {
      return Double.valueOf(val);
    } catch (Throwable t) {

    }
    return null;
  }

  private void updateSOMM(String codlav, Long naprpr, ServletRequest request)
      throws GestoreException {
    // Estraggo il numero di somme
    if (request.getParameter("SOM_COUNT") != null) {
      int numSomm = Integer.valueOf(request.getParameter("SOM_COUNT")).intValue();
      HashMap maxval = null;
      Vector altreAPPR = null;
      try {

        altreAPPR = new Vector();
        List l = this.getSqlManager().getListVector(
            "select naprpr from appr where codlav = ? and naprpr > ?",
            new Object[] { codlav, naprpr });
        if (l != null) {
          for (int k = 0; k < l.size(); k++) {
            altreAPPR.add(SqlManager.getValueFromVectorParam(l.get(k), 0).longValue());
          }
        }

        maxval = new HashMap();
        List ret = this.getSqlManager().getListVector(
            "select nsorif, max(nsomma) from somm where codlav = ? group by nsorif",
            new Object[] { codlav });
        if (ret != null)
          for (int k = 0; k < ret.size(); k++) {
            maxval.put(
                SqlManager.getValueFromVectorParam(ret.get(k), 0).longValue(),
                SqlManager.getValueFromVectorParam(ret.get(k), 1).longValue());
          }

      } catch (SQLException e) {
        throw new GestoreException("Errore durante l'update delle somme !",
            "updateSomme", e);
      }
      // Scorro tutte le somme
      for (int i = 0; i < numSomm; i++) {
        boolean ins = "1".equals(request.getParameter("SOM_INS." + i));
        boolean mod = "1".equals(request.getParameter("SOM_MOD." + i));
        boolean del = "1".equals(request.getParameter("SOM_DEL." + i));
        if (mod && !(ins && del)) {
          Long nsomma = getLong(request.getParameter("SOM_NSOMMA." + i));
          Long nsorif = getLong(request.getParameter("SOM_NSORIF." + i));
          if (del) {
            // Se si deve eliminare la somma la elimino per tutte le
            // approvazioni
            try {
              this.getSqlManager().update(
                  "delete from somm where codlav = ? and nsorif = ? and nsomma= ? and naprpr = ?",
                  new Object[] { codlav, nsorif, nsomma, naprpr });
            } catch (SQLException e) {
              throw new GestoreException(
                  "Errore durante l'eliminazione di una SOMMA !", "delSomma", e);
            }

          } else {
            Double value = getDouble(request.getParameter("SOM_VAL." + i));
            String descr = request.getParameter("SOM_DESCR." + i);
            Double perc = getDouble(request.getParameter("SOM_PERC." + i));
            // SS 30/01/2008: si considera il dato inseribile solo se si
            // attribuisce l'importo o la descrizione
            if (value == null && (descr == null || descr.length() == 0))
              ins = false;
            if (ins) {
              try {

                // Si tratta dell'inserimento della somma
                if (nsomma == null) {
                  if (maxval.get(nsorif) != null) {
                    nsomma = new Long(
                        ((Long) maxval.get(nsorif)).intValue() + 1);
                  } else {
                    nsomma = new Long(1);
                  }
                }
                if (naprpr.longValue() != 1)
                  this.getSqlManager().update(
                      "insert into somm (codlav, naprpr, nsorif, nsomma, desomm, imsomm, ivasom ) "
                          + "values ( ?, ?, ?, ?, ?, ?, ? )",
                      new Object[] { codlav, naprpr, nsorif, nsomma, descr,
                          value, perc });
                else
                  this.getSqlManager().update(
                      "insert into somm (codlav, naprpr, nsorif, nsomma, desomm, imsomm, ivasom, imsomp ) "
                          + "values ( ?, ?, ?, ?, ?, ?, ?, ? )",
                      new Object[] { codlav, naprpr, nsorif, nsomma, descr,
                          value, perc, value });
                for (int k = 0; k < altreAPPR.size(); k++) {

                  // inserisco anche per le approvazioni successive
                  this.getSqlManager().update(
                      "insert into somm (codlav, naprpr, nsorif, nsomma, desomm, ivasom) "
                          + "values ( ?, ?, ?, ?, ?, ? )",
                      new Object[] { codlav, altreAPPR.get(k), nsorif, nsomma,
                          descr, perc });

                }
                maxval.put(nsorif, nsomma);

              } catch (SQLException e) {
                throw new GestoreException(
                    "Errore durante l'inserimento di una SOMMA !", "insSomma",
                    e);
              }

            } else {
              // Modifica della somma
              try {
                if (naprpr.longValue() != 1)
                  this.getSqlManager().update(
                      "update somm set desomm = ?, imsomm = ?, ivasom = ? "
                          + "where codlav = ? and naprpr = ? and nsorif = ? and nsomma = ?",
                      new Object[] { descr, value, perc, codlav, naprpr,
                          nsorif, nsomma });
                else
                  this.getSqlManager().update(
                      "update somm set desomm = ?, imsomm = ?, ivasom = ?, imsomp = ?  "
                          + "where codlav = ? and naprpr = ? and nsorif = ? and nsomma = ?",
                      new Object[] { descr, value, perc, value, codlav, naprpr,
                          nsorif, nsomma });
                // Eseguo l'update anche di tutte le descrizioni successive
                this.getSqlManager().update(
                    "update somm set desomm = ?  "
                        + "where codlav = ? and naprpr > ? and nsorif = ? and nsomma = ?",
                    new Object[] { descr, codlav, naprpr, nsorif, nsomma });

              } catch (SQLException e) {
                throw new GestoreException(
                    "Errore durante l'update della SOMMA !", "modSomma", e);
              }
            }
          }

        }

      }

    }

  }

  private void updateCODPROE(DataColumnContainer impl) throws GestoreException {
    if (impl.isColumn("PERI.CODPROE")
        && impl.getColumn("PERI.CODPROE").isModified()) {
      try {
        impl.update("PERI", this.getSqlManager());
        // Ora eseguo l'update per l'appalto contratto e psal
        this.getSqlManager().update(
            "update appa set ivalav = ?, ivarp = ? where codlav = ?",
            new Object[] { impl.getDouble("PERI.CODPROE"),
                impl.getDouble("PERI.CODPROE"), impl.getString("APPR.CODLAV") });
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'update dell'iva del lavoro !",
            "updateCODPROE", e);
      }
    }
  }

  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    super.preInsert(status, impl);
    this.updateSOMM(impl.getString("APPR.CODLAV"), impl.getLong("APPR.NAPRPR"),
        this.getRequest());
    
    AbstractGestoreChiaveNumerica gestoreMultiploDELI = new DefaultGestoreEntitaChiaveNumerica(
        "DELI", "CON_FI", new String[] { "CODLAV", "NAPRPR" },
        this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, impl,
        gestoreMultiploDELI, "DELI", new DataColumn[] {
        impl.getColumn("APPR.CODLAV"),
        impl.getColumn("APPR.NAPRPR") }, null);
    
    this.gestioneULTAPP_P(status, impl);
    
    
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    // Prima dell'update eseguo la copia dei campi netti sul lordo solo se si
    // tratta della prima approvazione
    if (impl.isColumn("APPR.NAPRPR")
        && impl.getLong("APPR.NAPRPR") != null
        && impl.getLong("APPR.NAPRPR").longValue() == 1) {
      // Elenco dei campi da copiare nel formato [<SORGENTE>, <DESTINAZIONE>,]+
      String campiCopy[] = { "APPR.LAVFIM", "APPR.LAVPRM", "APPR.LAVFIC",
          "APPR.LAVPRC", "APPR.LAVFIE", "APPR.LAVPRE", "APPR.LAVFIO",
          "APPR.LAVPRO", "APPR.ILABAF", "APPR.ILABAP", "APPR.LAVFISM",
          "APPR.LAVPRSM", "APPR.LAVFISC", "APPR.LAVPRSC", "APPR.LAVFISE",
          "APPR.LAVPRSE", "APPR.LAVFIS", "APPR.LAVPRS", "APPR.SOMFI1",
          "APPR.SOMPR1", "APPR.SOMFI2", "APPR.SOMPR2", "APPR.SOMFI3",
          "APPR.SOMPR3", "APPR.SOMFI4", "APPR.SOMPR4", "APPR.SOMFI5",
          "APPR.SOMPR5", "APPR.SOMFI6", "APPR.SOMPR6", "APPR.SOMFI7",
          "APPR.SOMPR7", "APPR.SOMFI8", "APPR.SOMPR8", "APPR.SOMFI9",
          "APPR.SOMPR9", "APPR.SOMFI10", "APPR.SOMPR10", "APPR.SOMFI11",
          "APPR.SOMPR11", "APPR.SOMFI12", "APPR.SOMPR12", "APPR.SOMFI13",
          "APPR.SOMPR13", "APPR.SOMFI14", "APPR.SOMPR14", "APPR.ITSOMF",
          "APPR.ITSADP", "APPR.ITOTFI", "APPR.ITOTPR", "APPR.LAVNRM",
          "APPR.LAVPRNM", "APPR.LAVNRC", "APPR.LAVPRNC", "APPR.LAVNRE",
          "APPR.LAVPRNE", "APPR.LAVNRL", "APPR.LAVPRNL" };
      for (int i = 0; i < campiCopy.length - 1; i += 2) {
        // Esegue la copia solo se esiste la colonna sorgente
        if (impl.isColumn(campiCopy[i])
            && impl.getColumn(campiCopy[i]).isModified()) {
          // Se il campo non esiste lo aggiungo
          if (!impl.isColumn(campiCopy[i + 1]))
            impl.addColumn(campiCopy[i + 1],
                impl.getColumn(campiCopy[i]).getTipoCampo());
          // Altrimentis setto il suo valore
          impl.setValue(campiCopy[i + 1], impl.getObject(campiCopy[i]));
        }
      }
    }
    this.updateSOMM(impl.getString("APPR.CODLAV"), impl.getLong("APPR.NAPRPR"),
        this.getRequest());
    updateCODPROE(impl);
    
    AbstractGestoreChiaveNumerica gestoreMultiploDELI = new DefaultGestoreEntitaChiaveNumerica(
        "DELI", "CON_FI", new String[] { "CODLAV", "NAPRPR" },
        this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, impl,
        gestoreMultiploDELI, "DELI", new DataColumn[] {
        impl.getColumn("APPR.CODLAV"),
        impl.getColumn("APPR.NAPRPR") }, null);

    this.gestioneULTAPP_P(status, impl);
    
  }
  
  /**
   * Gestione categorie di iscrizione.
   * @param status
   * @param datiForm
   * @throws GestoreException
   */
  private void gestioneULTAPP_P(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    AbstractGestoreChiaveNumerica gestoreMultiploULTAPP_P = new DefaultGestoreEntitaChiaveNumerica("ULTAPP_P", "NOPEGA", new String[] {
        "CODLAV", "NAPRPR" }, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm, gestoreMultiploULTAPP_P, "ULTAPP_P",
        new DataColumn[] { datiForm.getColumn("APPR.CODLAV"), datiForm.getColumn("APPR.NAPRPR") }, null);
  }
  

}
