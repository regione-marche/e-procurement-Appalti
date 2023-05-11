/*
 * Created on 23/dic/2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.excel;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;

import java.util.List;


public class CampoImportExcel {

  private Campo   campo;

  private boolean obbligatorio;

  // Indice della colonna nel foglio Excel in cui e' presente il campo:
  // colonnaCampo = 3 --> colonna C
  private int     colonnaCampo;

  // Indice della colonna nell'array che conterra' il valore del campo
  private int     colonnaArrayValori;

  private Object  valoreDiDefault;

  private List<Tabellato>    tabellatoDelCampo;

  /** Manager dei tabellati */
  private TabellatiManager   tabellatiManager;


  public int getTipoCampo() {
    return campo.getTipoColonna();
  }

  public int getColonnaCampo() {
    return colonnaCampo;
  }

  public void setColonnaCampo(int colonnaCampo) {
    this.colonnaCampo = colonnaCampo;
  }

  public int getLunghezzaCampo() {
    return campo.getLunghezza();
  }

  public int getCifreDecimali() {
    return campo.getDecimali();
  }

  /**
   * @return Ritorna colonnaArrayValori.
   */
  public int getColonnaArrayValori() {
    return colonnaArrayValori;
  }

  /**
   * @param colonnaArrayValori
   *        colonnaArrayValori da settare internamente alla classe.
   */
  public void setColonnaArrayValori(int colonnaArrayValori) {
    this.colonnaArrayValori = colonnaArrayValori;
  }

  public void setObbligatorio(boolean isObbligatorio) {
    this.obbligatorio = isObbligatorio;
  }

  public boolean isObbligatorio() {
    return obbligatorio;
  }

  public Object getValoreDiDefault() {
    return valoreDiDefault;
  }

  public String getNomeFisicoCampo() {
    return campo.getNomeFisicoCampo();
  }

  public List<Tabellato> getTabellatoDelCampo() {
    return tabellatoDelCampo;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }


  public CampoImportExcel(String nomeFisicoCampo,Long tipoFornitura,TabellatiManager tabellatiManager) {
    super();
    if (nomeFisicoCampo != null) {
      if (nomeFisicoCampo.length() > 0) {
        campo = DizionarioCampi.getInstance().getCampoByNomeFisico(
            nomeFisicoCampo.substring(nomeFisicoCampo.indexOf(".") + 1));
        colonnaCampo = -1;
        colonnaArrayValori = -1;
        tabellatoDelCampo = null;
        if (campo.getCodiceTabellato() != null
            && campo.getCodiceTabellato().length() > 0) {
          List<Tabellato> listaTmp = tabellatiManager.getTabellato(campo.getCodiceTabellato());
          if (listaTmp != null && listaTmp.size() > 0) {
            tabellatoDelCampo = listaTmp;
          } else {
            tabellatoDelCampo = null;
          }
        }
      } else
        throw new IllegalArgumentException(
            "Argomento di ingresso al metodo e' stringa vuota");
    } else
      throw new NullPointerException(
          "Argomento di ingresso al metodo e' null");
  }


}
