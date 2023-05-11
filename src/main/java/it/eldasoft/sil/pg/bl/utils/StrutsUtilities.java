/*
 * Created on 6 set 2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.utils;

import java.io.File;

import javax.servlet.ServletContext;

import it.eldasoft.utils.spring.SpringAppContext;

public class StrutsUtilities {
  /**
   * Ritorna la directory per i file temporanei, prendendola da
   * javax.servlet.context.tempdir (area temporanea nella work della
   * webapplication)
   *
   * @return path alla directory per i file temporanei
   */
  public static String getTempDir() {
    ServletContext context = SpringAppContext.getServletContext();
    File tmp= (File) context.getAttribute("javax.servlet.context.tempdir");
    return tmp.getAbsolutePath();
   }
}
