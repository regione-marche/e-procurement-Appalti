package it.eldasoft.sil.pg.tags.gestori.submit;



import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;


public class GestoreW_INVCOMDESAggiungiDesMandantiRT extends AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestoreW_INVCOMDESAggiungiDesMandantiRT.class);

  @Override
  public String getEntita() {
    return "W_INVCOMDES";
  }

  public GestoreW_INVCOMDESAggiungiDesMandantiRT() {
    super(false);
}

public GestoreW_INVCOMDESAggiungiDesMandantiRT(boolean isGestoreStandard) {
    super(isGestoreStandard);
}

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {


  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    String idprg = UtilityStruts.getParametroString(this.getRequest(),"idprg");
    String idcom = UtilityStruts.getParametroString(this.getRequest(),"idcom");
    String idconfiStringa = UtilityStruts.getParametroString(this.getRequest(),"idconfi");
    String integrazioneWSDM= UtilityStruts.getParametroString(this.getRequest(),"integrazioneWSDM");
    Long idcomLong = new Long(idcom);
    boolean mailInCaricoDocumentale=false;
    if("1".equals(integrazioneWSDM)){
      String valoreWSDM = ConfigManager.getValore("pg.wsdm.invioMailPec."+idconfiStringa);
      if(valoreWSDM!=null && "1".equals(valoreWSDM))
        mailInCaricoDocumentale=true;
    }

    //Si deve individuare se fra le occorrenze presenti in W_INVCOMDES vi sono dei raggruppamenti temporanei
    String select="select descodsog, count(descodsog) from w_invcomdes where idprg=? and idcom=? and descodent=? group by descodsog";
    try {
      List listaDestinatari = this.sqlManager.getListVector(select, new Object[]{idprg, idcomLong, "IMPR"});
      if(listaDestinatari!=null && listaDestinatari.size()>0){
        String descodsog=null;
        Long numDescodsog=null;
        Long tipimp = null;
        String ragsocRT=null;
        Vector vettRT=null;
        String ragsocMandante=null;
        List componentiRaggruppamento=null;
        String codMandante=null;
        Long conteggio=null;
        String mail=null;
        String pec=null;
        StringBuffer buf = new StringBuffer("<br><ul>");
        boolean mandantiNoMail=false;
        String ragsocDest=null;
        long newIdcomdes=0;
        Long maxIdcomdes=null;
        Long comtipma=null;
        String desmail=null;
        int numRT =0;
        boolean inserito=false;
        for(int i=0;i<listaDestinatari.size();i++){
          descodsog = SqlManager.getValueFromVectorParam(listaDestinatari.get(i), 0).stringValue();
          numDescodsog = SqlManager.getValueFromVectorParam(listaDestinatari.get(i), 1).longValue();
          //Nella vecchia gestione venivano inserite anche le mandanti di un raggruppamento, riportando
          //per queste il codice del raggruppamento. In questo caso non si deve fare nulla.
          if(numDescodsog.longValue()==1){
            //Si deve controllare se l'impresa è un raggruppamento
            vettRT=this.sqlManager.getVector("select tipimp,nomimp from impr where codimp=?", new Object[]{descodsog});
            if(vettRT!=null && vettRT.size()>0){
              tipimp = SqlManager.getValueFromVectorParam(vettRT, 0).longValue();
              ragsocRT= SqlManager.getValueFromVectorParam(vettRT, 1).stringValue();
              if(tipimp!=null && (tipimp.longValue()==3 || tipimp.longValue()==10)){
                numRT++;
                //si estraggono le mandanti del raggruppamento
                componentiRaggruppamento = this.sqlManager.getListVector("select coddic,emai2ip, emaiip,nomimp from ragimp,impr where codime9=? and (impman = '2' or impman is null) and coddic=codimp", new Object[]{descodsog});
                if(componentiRaggruppamento!=null && componentiRaggruppamento.size() >0){
                  for(int j=0;j<componentiRaggruppamento.size();j++){
                    codMandante = SqlManager.getValueFromVectorParam(componentiRaggruppamento.get(j), 0).stringValue();
                    ragsocMandante = SqlManager.getValueFromVectorParam(componentiRaggruppamento.get(j), 3).stringValue();
                    //Si verifica se la mandante è già presente fra i destinatari, altrimenti la si inserisce
                    conteggio=(Long)this.sqlManager.getObject("select count(idcom) from w_invcomdes where idprg=? and idcom=? and descodsog=? and descodent=?", new Object[]{idprg, idcomLong,codMandante,"IMPR"});
                    if(conteggio==null || (conteggio!=null && conteggio.longValue()==0)){
                      pec = SqlManager.getValueFromVectorParam(componentiRaggruppamento.get(j), 1).stringValue();
                      mail = SqlManager.getValueFromVectorParam(componentiRaggruppamento.get(j), 2).stringValue();
                      if((mailInCaricoDocumentale && (pec==null || "".equals(pec))) || (!mailInCaricoDocumentale && ((pec==null || "".equals(pec)) && (mail==null || "".equals(mail))))){
                        buf.append("<li style=\"list-style-type: disc;margin-left: 30px;\" >");
                        buf.append(descodsog);
                        buf.append(" - ");
                        buf.append(ragsocRT);
                        buf.append(" - ");
                        buf.append(codMandante);
                        buf.append(" - ");
                        buf.append(ragsocMandante);
                        buf.append("</li>");
                        mandantiNoMail=true;
                      }else{
                        //Inserimento destinatario

                        ragsocDest = ragsocRT+" - "+ragsocMandante+" - Mandante";
                        maxIdcomdes=(Long)this.sqlManager.getObject("select max(idcomdes) from w_invcomdes where idprg=? and idcom=?", new Object[]{idprg,idcomLong});
                        if (maxIdcomdes==null || maxIdcomdes.longValue()==0)
                          newIdcomdes++;
                        else
                          newIdcomdes = maxIdcomdes.longValue() + 1;
                        if(pec!=null && !"".equals(pec)){
                          desmail = pec;
                          comtipma = new Long(1);
                        }else{
                          desmail = mail;
                          comtipma = new Long(2);
                        }
                        this.sqlManager.update("insert into w_invcomdes(idprg,idcom,idcomdes,descodsog,descodent,desmail,desintest,comtipma) values(?,?,?,?,?,?,?,?)",
                             new Object[]{idprg,idcomLong, new Long(newIdcomdes),codMandante,"IMPR",desmail,ragsocDest,comtipma});
                        inserito=true;
                      }
                    }
                  }
                }
              }
            }
          }
        }

        if(numRT==0){
          this.getRequest().setAttribute("inserimentoEseguito", "noRT");
        }else{
          if(inserito)
            this.getRequest().setAttribute("inserimentoEseguito", "OK");
          else
            this.getRequest().setAttribute("inserimentoEseguito", "NoInserimenti");
          if(mandantiNoMail){
            buf.append("</ul>");
            this.getRequest().setAttribute("msg", buf.toString());
            this.getRequest().setAttribute("mailInCaricoDocumentale",mailInCaricoDocumentale);
          }
        }
      }else{
        this.getRequest().setAttribute("inserimentoEseguito", "noDestinatari");
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'inserimento dei destinatari a partire dalle mandatarie dei raggruppamenti temporanei",null, e);

    }

  }

}
