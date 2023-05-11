/*
 * Created on 29 apr 2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.sil.portgare.datatypes.DocumentoType;

//Solo nel caso di busta composta mediante qform, i documenti non vanno processati nell'ordine in cui sono presenti nel file xml prodotto da Portale,
//ma vanno processati in base al valore di uuid di ogni documento. Per limitare l'impatto sulla gestione esistente, si è
//deciso di ordinare il contenuto di listaDocumenti in base all'uuid e quindi continuare a processare il file xml.
//Quando la busta è presentata tramite qform sono sempre i file "PDF Riepilogo" e "questionario", quindi perchè abbia senso applicare
//la gestione del riordino dei file, devono esserci almeno 3 file
public class ListaDocumentiPortaleUtilities {

  private static class ComporatoreUuidDocumenti implements Comparator<DocumentoType> {

    @Override
    public int compare(DocumentoType o1, DocumentoType o2) {
      return o1.getUuid().compareTo(o2.getUuid());
    }
  }

  /**
   *
   * @param arrayDoc
   * @return Iterator<DocumentoType>
   */
  public static Iterator<DocumentoType> getIteratore(DocumentoType arrayDoc[]){
    boolean gestioneQform=false;
    Iterator<DocumentoType> iterator = Arrays.stream(arrayDoc).iterator();
    TreeSet<DocumentoType>  set = new TreeSet<DocumentoType>(new ComporatoreUuidDocumenti());
    if(arrayDoc.length>2) {
      DocumentoType documento = null;
      String nomeFile = null;
      while(iterator.hasNext()) {
        documento = iterator.next();
        nomeFile = documento.getNomeFile();
        set.add(documento);
        if(CostantiAppalti.nomeFileQestionario.equals(nomeFile))
          gestioneQform=true;
      }
    }
    if(gestioneQform) {
      iterator = set.iterator();
    }else {
      set = null;
      iterator = Arrays.stream(arrayDoc).iterator();
    }
    return iterator;
  }
}
