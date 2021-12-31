/*
 * Created on 23/feb/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.cifrabuste;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.maggioli.eldasoft.security.PGPEncryptionUtils;
import it.maggioli.eldasoft.security.PGPKeyPairGenerator;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.sql.SQLException;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;


public class CifraturaBusteManager {
  /** Logger */
  static Logger               logger                = Logger.getLogger(CifraturaBusteManager.class);

  /** Manager SQL per le operazioni su database */
  private SqlManager          sqlManager;

  private GenChiaviManager    genChiaviManager;

  static {
    // si imposta il provider sicurezza da utilizzare
    BouncyCastleProvider provider = new BouncyCastleProvider();
    String name = provider.getName();
    synchronized (Security.class) {
        Security.removeProvider(name); // remove old instance
        Security.addProvider(provider);
    }
  }

  /**
   * Set SqlManager
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
  *
  * @param genChiaviManager
  */
   public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
     this.genChiaviManager = genChiaviManager;
   }

  /**
   * Viene creata la coppia di password(pubblica e privata) a partire dalla passphrase
   *
   * @param ngara
   * @return ByteArrayOutputStream[],
   *              indice 0 - chiave privata
   *              indice 1 - chiave pubblica
   *
   * @throws NoSuchAlgorithmException, PGPException, IOException
   */
  private ByteArrayOutputStream[] getChiavi( String pin) throws NoSuchAlgorithmException, PGPException, IOException{

    if (logger.isDebugEnabled())
      logger.debug("getChiavi: inizio metodo");

    String uuid = UUID.randomUUID().toString();

    PGPKeyPairGenerator generator = new PGPKeyPairGenerator(uuid, pin);

    // Private key
    ByteArrayOutputStream baosPrivateKey = new ByteArrayOutputStream();
    generator.getAsciiArmoredPrivateKey(baosPrivateKey);
    baosPrivateKey.close();

    // Public key
    ByteArrayOutputStream baosPublicKey = new ByteArrayOutputStream();
    generator.getAsciiArmoredPublicKey(baosPublicKey);
    baosPublicKey.close();

    if (logger.isDebugEnabled())
      logger.debug("getChiavi: fine metodo");

    return new ByteArrayOutputStream[]{baosPrivateKey,baosPublicKey};
  }

  /**
   * Viene popolata l'entità CHIAVIBUSTE
   *
   * @param ngara
   * @param pinA
   * @param pinB
   * @param pinC
   *
   * @throws GestoreException
   */
  public void popolaChiavibuste(String ngara, String pinA, String pinB, String pinC) throws GestoreException{

      if (logger.isDebugEnabled()) logger.debug("CifraturaBusteManager.popolaChiavibuste: inizio metodo");

      try {

        //Chiavi per busta Amministrativa
        ByteArrayOutputStream chiavi[] = this.getChiavi(pinA);

        //fare controllo sull'esistenza della busta
        Long id = null;
        Long conteggio=null;
        final String select="select count(id) from chiavibuste where ngara=? and busta=?";
        conteggio = (Long)this.sqlManager.getObject(select, new Object[]{ngara,"FS11A"});
        if(conteggio == null || (conteggio!=null &&conteggio.longValue()==0)){
          id = new Long(this.genChiaviManager.getNextId("CHIAVIBUSTE"));
          this.sqlManager.update("insert into chiavibuste (id, ngara, busta, uuid, chiavepubb, chiavepriv) values (?, ?, ?, ?, ?, ?)", new Object[] {
              id, ngara, "FS11A", null, chiavi[1].toString(), chiavi[0].toString()});
        }

        //Chiavi per busta Tecnica
        if(pinB!=null){
          conteggio = (Long)this.sqlManager.getObject(select, new Object[]{ngara,"FS11B"});
          if(conteggio == null || (conteggio!=null &&conteggio.longValue()==0)){
            chiavi = this.getChiavi(pinB);

            id = new Long(this.genChiaviManager.getNextId("CHIAVIBUSTE"));
            this.sqlManager.update("insert into chiavibuste (id, ngara, busta, uuid, chiavepubb, chiavepriv) values (?, ?, ?, ?, ?, ?)", new Object[] {
                id, ngara, "FS11B", null, chiavi[1].toString(), chiavi[0].toString()});
          }
        }

        //Chiavi per busta Economica
        if(pinC!=null){
          conteggio = (Long)this.sqlManager.getObject(select, new Object[]{ngara,"FS11C"});
          if(conteggio == null || (conteggio!=null &&conteggio.longValue()==0)){
            chiavi = this.getChiavi(pinC);

            id = new Long(this.genChiaviManager.getNextId("CHIAVIBUSTE"));
            this.sqlManager.update("insert into chiavibuste (id, ngara, busta, uuid, chiavepubb, chiavepriv) values (?, ?, ?, ?, ?, ?)", new Object[] {
                id, ngara, "FS11C", null, chiavi[1].toString(), chiavi[0].toString()});
          }
        }

      } catch (NoSuchAlgorithmException e) {
        throw new GestoreException("Errore nella generazione delle chiavi pubblica e privata per la gara: " + ngara, null, e);
      }catch (PGPException e) {
        throw new GestoreException("Errore nella generazione delle chiavi pubblica e privata per la gara: " + ngara, null, e);
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'inserimento delle chiavi nella tabella CHIAVIBUSTE per la gara: " + ngara,null, e);
      } catch (IOException e) {
        throw new GestoreException("Errore nella generazione delle chiavi pubblica e privata per la gara: " + ngara, null, e);
    }


      if (logger.isDebugEnabled()) logger.debug("CifraturaBusteManager.popolaChiavibuste: fine metodo");

    }

  /**
   * Viene decripata la chiave privata memorizzata nel campo CHIAVIBUSTE.CHIAVEPRI tramite il pin, e tale chiave viene adoperata per
   * decifrare la chiave di sessione ed ottenere così il decifratore per le buste
   *
   * @param pin
   *          pin per decifrare la chiave privata in CHIAVIBUSTE.CHIAVEPRI
   * @param gara
   * @param tipoBusta
   *        FS11A , FS11B e FS11C
   * @param chiaveSessione
   *        W_INVCOM.COMKEYSESS
   * @param chiaveProtezione
   *        W_INVCOM.COMKEY1
   * @return Cipher
   *
   * @throws GestoreException
   */
  public Cipher getDecifratoreBuste(String pin, String gara, String tipoBusta, String chiaveSessione, String chiaveProtezione) throws GestoreException{
    Cipher decoder = null;
    try {

      String chiavepriv = (String)sqlManager.getObject("select chiavepriv from chiavibuste where ngara=? and busta=?", new Object[]{gara,tipoBusta});
      if(chiavepriv!=null && !"".equals(chiavepriv)){
        ByteArrayInputStream in=new ByteArrayInputStream(chiavepriv.getBytes());
        byte[] decodedSessionKey = PGPEncryptionUtils.decrypt(in, pin, chiaveSessione.getBytes());

        if (decodedSessionKey != null){
          try {
            decoder = SymmetricEncryptionUtils.getDecoder(decodedSessionKey,
                chiaveProtezione);
          } catch (InvalidKeyException e) {
            throw new GestoreException("Errore nella decifrazione della chiave di sessione", null, e);
          } catch (UnsupportedEncodingException e) {
            throw new GestoreException("Errore nella decifrazione della chiave di sessione", null, e);
          } catch (NoSuchAlgorithmException e) {
            throw new GestoreException("Errore nella decifrazione della chiave di sessione", null, e);
          } catch (NoSuchProviderException e) {
            throw new GestoreException("Errore nella decifrazione della chiave di sessione", null, e);
          } catch (NoSuchPaddingException e) {
            throw new GestoreException("Errore nella decifrazione della chiave di sessione", null, e);
          } catch (InvalidAlgorithmParameterException e) {
            throw new GestoreException("Errore nella decifrazione della chiave di sessione", null, e);
          }
        }
      }else{
        //Si deve controllare se esiste l'occorrenza, se esiste allora è un errore altrimenti non è attiva la cifratura
        Long conteggio = (Long)sqlManager.getObject("select count(id) from chiavibuste where ngara=? and busta=?", new Object[]{gara,tipoBusta});
        if(conteggio!= null && conteggio.longValue()>0)
          throw new GestoreException("Non è stata trovata la chiave privata criptata in CHIAVIBUSTE per la gara " + gara , null, new Exception());
      }

    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura di CHIAVIBUSTE.CHIAVEPRIV per la gara " + gara, null, e);
    } catch (GeneralSecurityException e1) {
      throw new GestoreException("Errore nella decifrazione della chiave privata per la busta di tipo " + tipoBusta + " della gara " + gara, null, e1);
    } catch (IOException e1) {
      throw new GestoreException("Errore nella decifrazione della chiave privata per la busta di tipo " + tipoBusta + " della gara " + gara, null, e1);
    } catch (PGPException e1) {
      String errore = e1.getMessage();
      //tracciatura nel caso di password errata
      if(errore.indexOf("checksum mismatch")>=0){
        errore="Password inserita non corretta per decifrare la busta di tipo " + tipoBusta + " della gara " + gara;
      }else{
        errore="Errore nella decifrazione della chiave privata per la busta di tipo " + tipoBusta + " della gara " + gara;
      }
      throw new GestoreException(errore, null, e1);
    }

    return decoder;
  }

  /**
   * Viene inserita una busta nell'entità CHIAVIBUSTE
   *
   * @param ngara
   * @param pin
   * @param busta
   *
   * @throws GestoreException
   */
  public void inserisciBustaInChiavibuste(String ngara, String pin, String busta) throws GestoreException{

      if (logger.isDebugEnabled()) logger.debug("CifraturaBusteManager.inserisciBustaInChiavibuste: inizio metodo");

      try {

        //Chiavi per busta Amministrativa
        ByteArrayOutputStream chiavi[] = this.getChiavi(pin);
        Long conteggio = (Long)this.sqlManager.getObject("select count(id) from chiavibuste where ngara=? and busta=?", new Object[]{ngara,busta});
        if(conteggio == null || (conteggio!=null &&conteggio.longValue()==0)){
          Long id = new Long(this.genChiaviManager.getNextId("CHIAVIBUSTE"));
          this.sqlManager.update("insert into chiavibuste (id, ngara, busta, uuid, chiavepubb, chiavepriv) values (?, ?, ?, ?, ?, ?)", new Object[] {
              id, ngara, busta, null, chiavi[1].toString(), chiavi[0].toString()});
        }
      } catch (NoSuchAlgorithmException e) {
        throw new GestoreException("Errore nella generazione delle chiavi pubblica e privata per la gara: " + ngara, null, e);
      }catch (PGPException e) {
        throw new GestoreException("Errore nella generazione delle chiavi pubblica e privata per la gara: " + ngara, null, e);
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'inserimento delle chiavi nella tabella CHIAVIBUSTE per la gara: " + ngara,null, e);
      } catch (IOException e) {
        throw new GestoreException("Errore nella generazione delle chiavi pubblica e privata per la gara: " + ngara, null, e);
    }


      if (logger.isDebugEnabled()) logger.debug("CifraturaBusteManager.inserisciBustaInChiavibuste: fine metodo");

    }
}


