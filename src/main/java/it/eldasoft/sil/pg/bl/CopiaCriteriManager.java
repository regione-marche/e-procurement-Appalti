/*
 * Created on 16/ott/2018
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.bl;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;


public class CopiaCriteriManager {

  GenChiaviManager genChiaviManager;
  SqlManager sqlManager;

  private static final String ALTRO_LOTTO          = "lotto";
  private static final String MODELLO              = "modello";

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;}




  public void copiaCriteri(String codiceGara, List listaCriteri, String ngaraDestinazione, String tipoSorgente) throws SQLException, GestoreException{

    boolean criterioTecnicoDefinito = false;

      if (listaCriteri != null && listaCriteri.size() > 0) {
        Long necvan = null;
        Double norpar = null;
        Long tippar = null;
        Double maxpun = null;
        String despar = null;
        Long livpar = null;
        Long necvan1 = null;
        Double norpar1 = null;
        Double minpun = null;
        Long id = null;
        Long nuovoId = null;
        String descri = null;
        Double maxpunG1cridef = null;
        Long formato = null;
        Long modpunti = null;
        Long modmanu = null;
        Long numdeci = null;
        Long formula = null;
        String isnoprz = null;
        Long tipcal = null;
        Long seztec = null;
        String insertGoev="insert into goev (NGARA,NECVAN,NORPAR,TIPPAR,MAXPUN,DESPAR," +
            "LIVPAR,NECVAN1,NORPAR1,MINPUN, ISNOPRZ, SEZTEC) values(?,?,?,?,?,?,?,?,?,?,?,?)";
        String insertG1cridef="insert into g1cridef (ID,NGARA,NECVAN,DESCRI,MAXPUN,FORMATO,MODPUNTI,MODMANU,NUMDECI,FORMULA)" +
            " values(?,?,?,?,?,?,?,?,?,?)";

        for (Iterator iterator = listaCriteri.iterator(); iterator.hasNext();) {
          Vector criterio = (Vector) iterator.next();
          //Long necvan = ((JdbcParametro) criterio.get(0)).longValue();

          necvan = null;
          if(criterio.get(0)!=null)
            necvan = ((JdbcParametro) criterio.get(0)).longValue();

          norpar = null;
          if(criterio.get(1)!=null)
            norpar = ((JdbcParametro) criterio.get(1)).doubleValue();

          tippar = null;
          if(criterio.get(2)!=null)
            tippar = ((JdbcParametro) criterio.get(2)).longValue();


          maxpun = null;
          if(criterio.get(3)!=null)
            maxpun = ((JdbcParametro) criterio.get(3)).doubleValue();

          despar = null;
          if(criterio.get(4)!=null)
            despar = ((JdbcParametro) criterio.get(4)).stringValue();

          livpar = null;
          if(criterio.get(5)!=null)
            livpar = ((JdbcParametro) criterio.get(5)).longValue();

          necvan1 = null;
          if(criterio.get(6)!=null)
            necvan1 = ((JdbcParametro) criterio.get(6)).longValue();

          norpar1 = null;
          if(criterio.get(7)!=null)
            norpar1 = ((JdbcParametro) criterio.get(7)).doubleValue();

          minpun = null;
          if(criterio.get(8)!=null)
            minpun = ((JdbcParametro) criterio.get(8)).doubleValue();

          isnoprz = null;
          if(criterio.get(15)!=null)
            isnoprz = ((JdbcParametro) criterio.get(15)).stringValue();

          if(tipoSorgente.equals(ALTRO_LOTTO)){
            seztec = null;
            if(criterio.get(19)!=null)
              seztec = ((JdbcParametro) criterio.get(19)).longValue();
          }

          //Se è valorizzatto l'id di G1CRIDEF si procede con l'inserimento dell'occorrenza
          id = ((JdbcParametro)criterio.get(9)).longValue();
          if(id != null){

            descri=null;
            if(criterio.get(10)!=null)
              descri = ((JdbcParametro) criterio.get(10)).getStringValue();

            maxpunG1cridef=null;
            if(criterio.get(11)!=null)
              maxpunG1cridef = ((JdbcParametro) criterio.get(11)).doubleValue();

            formato=null;
            if(criterio.get(12)!=null)
              formato = ((JdbcParametro) criterio.get(12)).longValue();

            modpunti=null;
            if(criterio.get(13)!=null)
              modpunti = ((JdbcParametro) criterio.get(13)).longValue();

            modmanu=null;
            if(criterio.get(14)!=null)
              modmanu = ((JdbcParametro) criterio.get(14)).longValue();

            numdeci=null;
            if(criterio.get(16)!=null)
              numdeci = ((JdbcParametro) criterio.get(16)).longValue();

            formula=null;
            if(criterio.get(17)!=null)
              formula = ((JdbcParametro) criterio.get(17)).longValue();

            if(tipoSorgente.equals(ALTRO_LOTTO)){
              tipcal=null;
              if(criterio.get(18)!= null)
                tipcal = ((JdbcParametro) criterio.get(18)).longValue();
            }
          }

          if(tipoSorgente.equals(ALTRO_LOTTO)){
            insertGoev="insert into goev (NGARA,NECVAN,NORPAR,TIPPAR,MAXPUN,DESPAR," +
            "LIVPAR,NECVAN1,NORPAR1,MINPUN, ISNOPRZ,TIPCAL,SEZTEC) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            this.sqlManager.update(insertGoev, new Object[] { ngaraDestinazione, necvan, norpar,
                tippar, maxpun, despar, livpar, necvan1, norpar1, minpun, isnoprz,tipcal,seztec});
          }else{
            this.sqlManager.update(insertGoev, new Object[] { ngaraDestinazione, necvan, norpar,
                tippar, maxpun, despar, livpar, necvan1, norpar1, minpun, isnoprz,seztec});
          }

          if(id != null){
            nuovoId=new Long(genChiaviManager.getNextId("G1CRIDEF"));
            this.sqlManager.update(insertG1cridef, new Object[] { nuovoId, ngaraDestinazione, necvan, descri,
                maxpunG1cridef, formato, modpunti, modmanu, numdeci, formula});

            List datiG1crireg = sqlManager.getListVector("select coeffi, puntuale, valmin, valmax from g1crireg where idcridef = ? order by id", new Object[]{id});

            Double coeffi = null;
            String puntuale = null;
            Double valmin = null;
            Double valmax = null;
            Long nuovoIdCrireg = null;

            if(datiG1crireg!=null && datiG1crireg.size()>0){
              for (int i = 0; i < datiG1crireg.size(); i++) {

                coeffi = (Double) SqlManager.getValueFromVectorParam(
                    datiG1crireg.get(i), 0).getValue();

                puntuale = (String) SqlManager.getValueFromVectorParam(
                    datiG1crireg.get(i), 1).getValue();

                valmin = (Double) SqlManager.getValueFromVectorParam(
                    datiG1crireg.get(i), 2).getValue();

                valmax =(Double) SqlManager.getValueFromVectorParam(
                    datiG1crireg.get(i), 3).getValue();

                nuovoIdCrireg = new Long(genChiaviManager.getNextId("G1CRIREG"));

                String insertCrireg = "insert into g1crireg (id, idcridef, coeffi, puntuale, valmin, valmax) values (?,?,?,?,?,?)";

                sqlManager.update(insertCrireg, new Object[]{nuovoIdCrireg,nuovoId,coeffi,puntuale,valmin,valmax});
              }
            }
            if(!criterioTecnicoDefinito && tippar != null && tippar == 1 && formato != null && formato != 100){
              criterioTecnicoDefinito = true;
            }
          }
        }
      }
      if(criterioTecnicoDefinito){
        //inserisco documento se non esiste già
        String insertDoc = "insert into documgara (codgar,ngara,norddocg,numord,busta,gruppo,tipodoc,descrizione,obbligatorio,modfirma,valenza,gentel,seztec) values (?,?,?,1,2,3,1,'Offerta tecnica','1',1,0,'1',2)";
        String queryCriteriDefiniti = "select formato from g1cridef, goev where g1cridef.ngara = ? and g1cridef.necvan = goev.necvan and g1cridef.ngara = goev.ngara and goev.tippar=1 and formato != 100";
        String selectDescrizioni = "select descrizione from documgara where ngara = ?";
        String selectMaxNord = "select max(norddocg) from documgara where codgar = ?";
        boolean trovataOffTecnicaDoc = false;
        List listaDoc = this.sqlManager.getListVector(selectDescrizioni,new Object[]{ngaraDestinazione});
        for(int i=0; i < listaDoc.size(); i++){
          String descrizione = (String) SqlManager.getValueFromVectorParam(listaDoc.get(i), 0).getValue();
          if(descrizione != null && descrizione.equals("Offerta tecnica")){
            trovataOffTecnicaDoc = true;
          };
        }
        if(!trovataOffTecnicaDoc){
          Long maxNorddocg = new Long(0);
          Long norddocg = (Long) sqlManager.getObject(selectMaxNord,new Object[] { codiceGara });
          if(norddocg != null){maxNorddocg = norddocg;}
          sqlManager.update(insertDoc, new Object[]{codiceGara,ngaraDestinazione,maxNorddocg+1});
        }
      }
    }

}
