/*
 * Created on 24-ago-2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts.sigla;

import java.io.Serializable;
import java.util.List;

/**
 * Bean per l'interfacciamento 
 *
 * @author Alex.Mancini
 */
public class ObjpaginazioneListaArticoli implements Serializable {

  /**
   * UID.
   */
  private static final Long serialVersionUID = 2572089103460119720L;


  private int              totalNumItems;
  private int              maxItemsPerPage;
  private int              activePage;
  private List<Articolo>    elements;
  
  public int getTotalNumItems() {
    return totalNumItems;
  }
  
  public void setTotalNumItems(int totalNumItems) {
    this.totalNumItems = totalNumItems;
  }
  
  public int getMaxItemsPerPage() {
    return maxItemsPerPage;
  }
  
  public void setMaxItemsPerPage(int maxItemsPerPage) {
    this.maxItemsPerPage = maxItemsPerPage;
  }
  
  public int getActivePage() {
    return activePage;
  }
  
  public void setActivePage(int activePage) {
    this.activePage = activePage;
  }
  
  public List<Articolo> getElements() {
    return elements;
  }
  
  public void setElements(List<Articolo> elements) {
    this.elements = elements;
  }
  
  public static Long getSerialversionuid() {
    return serialVersionUID;
  }
  
}