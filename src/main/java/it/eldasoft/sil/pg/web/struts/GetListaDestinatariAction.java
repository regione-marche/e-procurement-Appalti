package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.utility.UtilityStringhe;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetListaDestinatariAction extends ActionAjaxLogged {

  static Logger  logger = Logger.getLogger(GetListaDestinatariAction.class);
  
  private SqlManager sqlManager;
  
  private TabellatiManager tabellatiManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  @Override
  public final ActionForward runAction(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws IOException {

    JSONObject result = new JSONObject();

    if (logger.isDebugEnabled()) logger.debug("GetListaDestinatariAction: inizio metodo");

    PrintWriter out = response.getWriter();


    String entita = request.getParameter("entita");
    int numeroPopup = Integer.parseInt(request.getParameter("numeroPopup"));
    String idprg = new String(request.getParameter("idprg"));
    Long idcom = new Long(request.getParameter("idcom"));
    HttpSession sessione = request.getSession();
    
    if (entita != null && !"".equals(entita) && !sqlManager.isTable(entita)) {
      result.put("messaggio", "Si è verificato un errore.");
    }else {

      String trovaAddWhere = UtilityTags.getAttributeForSqlBuild(sessione, entita, numeroPopup, "trovaAddWhere");
      String trovaParameter = UtilityTags.getAttributeForSqlBuild(sessione, entita, numeroPopup, "trovaParameter");

      //devo gestire le date secondo il db
      if(StringUtils.isNotBlank(trovaParameter)) {
        String[] whereSenzaDate = handleDateParameters(trovaAddWhere, trovaParameter);
        trovaAddWhere = whereSenzaDate[0];
        trovaParameter = whereSenzaDate[1];
      }
      
      String trovaParameterGestoreTrova = UtilityTags.getAttributeForSqlBuild(sessione, entita, numeroPopup, "trovaParameterDestinatariAnagrafica");
      
      String select = "select DITG.NGARA5,DITG.DITTAO,IMPR.NOMIMP,DITG.INVGAR,DITG.INVOFF,DITG.AMMGAR,DITG.FASGAR,DITG.ABILITAZ,DITG.ABILITAZ,DITG.NOMIMO,DITG.IDANONIMO from DITG left join IMPR on DITG.DITTAO=IMPR.CODIMP";
      boolean consirareRT=true;
      String entitaPerIndirizzi="IMPR";
      if("IMPR".equals(entita)) {
        consirareRT=false;
        select = "select CODIMP, NOMIMP, CFIMP, PIVIMP, LOCIMP, EMAIIP, EMAI2IP FROM IMPR ";
      }
      if("TECNI".equals(entita)) {
        consirareRT=false;
        entitaPerIndirizzi="TECNI";
        select = "select CODTEC, NOMTEC, CFTEC, PIVATEC, EMATEC, EMA2TEC FROM TECNI ";
      }
      int numparametriprimaparte = 0;
      int numparametrisecondaparte = 0;
      boolean hasFiltroElenchi=false;
      boolean hasFiltroNessunElenco=false;
      if(StringUtils.isNotEmpty(trovaAddWhere)) {
        if(trovaAddWhere.indexOf("<end> )")>-1) {//manipolazione dei parametri del form trova per l'utilizzo in più punti del filtro creato nel gestoreTrova
          hasFiltroElenchi=true;
          String trovaFromForm=trovaAddWhere.split("<end> \\)")[1]; //salvo la seconda parte
          trovaAddWhere = trovaAddWhere.split("<end> \\)")[0]+")"; //elimino la seconda parte
          if(trovaAddWhere.indexOf("#trovaAddWhere#")>-1) {
            String primaparte=trovaAddWhere.substring(0,trovaAddWhere.indexOf("#trovaAddWhere#"));
            numparametriprimaparte = primaparte.length() - primaparte.replace("?", "").length();
            String secondaparte=trovaAddWhere.substring(trovaAddWhere.indexOf("#trovaAddWhere#"),trovaAddWhere.lastIndexOf("#trovaAddWhere#"));
            numparametrisecondaparte = secondaparte.length() - secondaparte.replace("?", "").length()+numparametriprimaparte;
            trovaAddWhere = trovaAddWhere.replace("#trovaAddWhere#", trovaFromForm);
            if(trovaAddWhere.indexOf("<NOT>")>-1) {
              hasFiltroNessunElenco=true;
              trovaAddWhere = trovaAddWhere.replace("<NOT>", "NOT");
              trovaAddWhere+=trovaFromForm; //Applico una terza volta il filtro (siamo nel caso di ditta non iscritta ad alcun elenco)
            }
          }
        }
        select += " where "+trovaAddWhere;
      }
      String parameters= concatParameters(trovaParameterGestoreTrova,trovaParameter,hasFiltroElenchi,hasFiltroNessunElenco,numparametriprimaparte,numparametrisecondaparte);
      
      try {
        List<?> destinatari = sqlManager.getListVector(select, getWhereParameters(parameters));
        JSONArray array =  new JSONArray();
        if (destinatari != null && destinatari.size() > 0) {
          for (int l = 0; l < destinatari.size(); l++) {
            JSONObject item = new JSONObject();
            try {
            //questi oggetti ci servono per recuperare gli indirizzi e sono dati comuni
              item.put("idprg", idprg);
              item.put("idcom", idcom);
              item.put("entitaPerIndirizzi", entitaPerIndirizzi);
              item.put("considerareRT", consirareRT);
              
              if("IMPR".equals(entita)) {
                String codimp = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 0).getValue();
                String nomimp = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 1).getValue();
                String cfimp = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 2).getValue();
                String pivimp = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 3).getValue();
                String locimp = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 4).getValue();
                String emaiip = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 5).getValue();
                String emai2p = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 6).getValue();
                
                item.put("dittao", codimp);
                item.put("nomimp", nomimp);
                item.put("cfimp",cfimp);
                item.put("pivimp",pivimp);
                item.put("locimp", locimp);
                item.put("email", emaiip);
                item.put("pec",emai2p);
                getIndirizzi(item, sqlManager,false);
                
              }else if("TECNI".equals(entita)) {
                String codtec = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 0).getValue();
                String nomtec = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 1).getValue();
                String cftec = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 2).getValue();
                String pivatec = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 3).getValue();
                String ematec = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 4).getValue();
                String ematec2 = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 5).getValue();
                
                item.put("dittao", codtec);
                item.put("nomimp",nomtec);
                item.put("cfimp",cftec);
                item.put("pivimp",pivatec);
                item.put("email", ematec);
                item.put("pec",ematec2);
                getIndirizzi(item, sqlManager,false);
                
              }else {//siamo su DITG
                String ngara5 = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 0).getValue();
                String dittao = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 1).getValue();
                String nomimp = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 2).getValue();
                String invgar = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 3).getValue();
                invgar=convertiCampoSN(invgar, false);
                String invoff = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 4).getValue();
                invoff=convertiCampoSN(invoff, false);
                String ammgar = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 5).getValue();
                ammgar=convertiCampoSN(ammgar, true);
                Long fasgar = (Long) SqlManager.getValueFromVectorParam(destinatari.get(l), 6).getValue();
                String fasgarString = null;
                if(fasgar!=null)
                  fasgarString = tabellatiManager.getDescrTabellato("A1011",  fasgar.toString());
                Long abilitaz = (Long) SqlManager.getValueFromVectorParam(destinatari.get(l), 7).getValue();
                String abilitazString = null;
                if(abilitaz!=null)
                  abilitazString = tabellatiManager.getDescrTabellato("A1075",  abilitaz.toString());
                String idanonimo = (String) SqlManager.getValueFromVectorParam(destinatari.get(l), 10).getValue();
                
                item.put("dittao", dittao);
                item.put("ngara", ngara5);
                item.put("invgar", invgar);
                item.put("invoff",invoff);
                item.put("ammgar", ammgar);
                item.put("fasgar", fasgarString);
                item.put("abilitaz", abilitazString);
                item.put("idanonimo", idanonimo);
                
                if(StringUtils.isNotBlank(idanonimo)) {//caso ditg.idanonimo valorizzato
                  nomimp=(String) SqlManager.getValueFromVectorParam(destinatari.get(l), 9).getValue(); //prendiamo il nomimo di ditg 
                  getIndirizzi(item, sqlManager,false);
                }else {//non mi interessano le mail se sono nel caso di DITG.IDANONIMO valorizzato
                  getIndirizzi(item, sqlManager,true);
                }
                item.put("nomimp", nomimp);
              }
              array.add(item);     
            }catch(Exception e) {
              logger.error(e);
            }
          }
        }

        result.put("listaDestinatari",array);



      }catch(SQLException e) {
        logger.error(e);
        result.put("messaggio", "Si è verificato un errore nell'interazione con la base dati.");
      }
    }

    if (logger.isDebugEnabled()) logger.debug("GetListaDestinatariAction: fine metodo");

    out.print(result);
    out.flush();

    return null;

  }
  
  private static String concatParameters(String params1, String params2, boolean handleFiltroElenchi,boolean handleFiltroNessunElenco, int parametriprimaparte, int parametrisecondaparte) {
    String result="";
    if(StringUtils.isNotBlank(params1) && StringUtils.isNotBlank(params2)) {
      if(handleFiltroElenchi) {
        String[] tmp = params1.split(";");
        if(parametriprimaparte==0)
          tmp[0] = params2 +";"+ tmp[0]; //appendo gli elementi in testa al primo elemento
        else
          tmp[parametriprimaparte-1] = tmp[parametriprimaparte-1] + ";" +params2; //appendo gli elementi in coda all'ultimo elemento della prima parte
        
        if(parametrisecondaparte==0)    //appendo gli elementi in testa all'ultimo elemento della prima parte
          tmp[0] = params2 +";"+ tmp[0]; //appendo gli elementi in testa al primo elemento
        else
          tmp[parametrisecondaparte-1] = tmp[parametrisecondaparte-1] +";"+params2; //appendo gli elementi all'ultimo elemento della seconda parte
          
          result = String.join(";", tmp);
          if(handleFiltroNessunElenco) { //triplico i parametri dal form di ricerca per la ricerca degli operatori non iscritti agli elenchi
            result +=";"+params2;   
          }
      }else {
        result +=params1+";"+params2;
      }
    }else {
      result =StringUtils.stripToEmpty(params1)+StringUtils.stripToEmpty(params2);
      if(handleFiltroElenchi && StringUtils.isNotBlank(params2)) { //raddoppio i parametri dal form di ricerca per la gestione degli elenchi
        result +=";"+params2; 
        if(handleFiltroNessunElenco) { //triplico i parametri dal form di ricerca per la ricerca degli operatori non iscritti agli elenchi
          result +=";"+params2;   
        }
      }
    }   
    return result;
  }

  private String[] handleDateParameters(String trovaAddWhere, String trovaParameter){
    String parameters = StringUtils.replace(trovaParameter, "\\;", "çç");
    List<String> newTrovaParameter = new ArrayList<>();
    String[] parametri = parameters.split(";");
    for (int i=0; i<parametri.length; i++) {
      parametri[i] = StringUtils.replace(parametri[i], "çç", "\\;");
      String tipo = parametri[i].substring(0, 1);
      if (("D".equals(tipo))) {
        String valore = parametri[i].substring(2);
        String dbFunctionStringToDate = sqlManager.getDBFunction("stringtodate",
                new String[]{valore});
        trovaAddWhere = trovaAddWhere.replaceFirst("\\?", dbFunctionStringToDate);
      }else{
        trovaAddWhere = trovaAddWhere.replaceFirst("\\?", "#ç#");
        newTrovaParameter.add(parametri[i]);
      }
    }
    return new String[]{trovaAddWhere.replace("#ç#", "?"), String.join(";", newTrovaParameter)};
  }
  
  private static Object[] getWhereParameters(String trovaParameter) {
    
    List<Object> parameters = new ArrayList<Object>();
    
    if (trovaParameter!=null && !"".equals(trovaParameter)) {
      
      String parametriElaborati = StringUtils.replace(trovaParameter, "\\;", "çç");
      String[] parametri = parametriElaborati.split(";");
      for (int i=0; i<parametri.length; i++) {
        parametri[i] =  StringUtils.replace(parametri[i], "çç", "\\;" );
        String valore = parametri[i].substring(2);
        parameters.add(valore);
      }
    }
    return parameters.toArray();
  }
  
  private static String convertiCampoSN(String campo, boolean campoSiNoInvertito) {
    if(campo==null)
      return null;
    
    boolean is1=false;
    if("1".equals(campo))
      is1=true;
    if(campoSiNoInvertito)
      is1=!is1;
    
    return is1? "Si":"No";
  }
  
  private static void getIndirizzi(JSONObject item, SqlManager sqlManager, boolean eseguiSelectSuImpr) throws JspException {
  String[] email = new String[3];
  boolean[] emailDisabled = new boolean[4];

  String codiceDitta=item.getString("dittao");
  String idprg=item.getString("idprg");
  Long idcom=item.getLong("idcom");
  String entita=item.getString("entitaPerIndirizzi");
  boolean considerareRT=item.getBoolean("considerareRT");

  boolean mandatariaPresente=true;

  if (codiceDitta != null && !"".equals(codiceDitta)) {
    try {
      if(eseguiSelectSuImpr) {
        String select="select emai2ip,emaiip,faximp,tipimp from impr where codimp=?";
        if("TECNI".equals(entita))
          select="select ema2tec,ematec from tecni where codtec=?";
        Vector datiImpr = sqlManager.getVector(select, new Object[] { codiceDitta });
        if(datiImpr!=null){
          String tipoImpr=null;
          if("IMPR".equals(entita))
            tipoImpr = ((JdbcParametro) datiImpr.get(3)).getStringValue();
  
          //Se l'impresa è di tipo ATI, considera l'indirizzo mail della mandataria
          if(("3".equals(tipoImpr) || "10".equals(tipoImpr)) && considerareRT){
  
  
              String selectComponenti= "select CODDIC, NOMDIC, emai2ip, emaiip, faximp" +
                      " from RAGIMP,IMPR where CODIME9 = ? and CODDIC=CODIMP and IMPMAN='1'";
              List listaComponenti = sqlManager.getListVector(selectComponenti,new Object[]{ codiceDitta });
              if (listaComponenti != null && listaComponenti.size() == 1){
                for (int k = 0; k< listaComponenti.size(); k++) {
                  String codComponente = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 0).getStringValue();
                  String nomeComponente = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 1).getStringValue();
                  String emai2ip = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 2).getStringValue();
                  emai2ip = UtilityStringhe.convertiNullInStringaVuota(emai2ip);
                  if(!"".equals(emai2ip)){
                    email[0] =emai2ip;
                  }
                  String emaiip = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 3).getStringValue();
                  emaiip = UtilityStringhe.convertiNullInStringaVuota(emaiip);
                  if(!"".equals(emaiip)){
                    email[1] = emaiip;
                  }
                  String faximp = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 4).getStringValue();
                  faximp = UtilityStringhe.convertiNullInStringaVuota(faximp);
                  if(!"".equals(faximp)){
                    email[2] = faximp;
                  }
                }
              }else{
                mandatariaPresente=false;
              }
  
          } else {
            email[0] = ((JdbcParametro) datiImpr.get(0)).getStringValue();
            email[1] = ((JdbcParametro) datiImpr.get(1)).getStringValue();
            if("IMPR".equals(entita))
              email[2] = ((JdbcParametro) datiImpr.get(2)).getStringValue();
          }
        }
        item.put("pec", email[0]);
        item.put("email", email[1]);
        item.put("fax", email[2]);
      }
      //Verifica per quali tipo indirizzo la ditta è già stata inserita tra i destinatari
      emailDisabled[0] = false;
      emailDisabled[1] = false;
      emailDisabled[2] = false;
      emailDisabled[3] = false;
      List destinatariDitta = sqlManager.getListVector(
          "select comtipma from w_invcomdes where descodent=? and idprg = ? and idcom = ? and " +
            "descodsog = ?", new Object[] {entita,idprg, idcom, codiceDitta});
      if (destinatariDitta != null && destinatariDitta.size() > 0) {
        for (int i = 0; i < destinatariDitta.size(); i++) {
          Long tipoIndirizzo = (Long) ((JdbcParametro) ((Vector) destinatariDitta.get(i)).get(0)).getValue();
          if(tipoIndirizzo==null)//siamo nel caso di comunicazione anonima
            emailDisabled[3] = true;
          else
            emailDisabled[tipoIndirizzo.intValue() - 1] = true;
        }
      }

    } catch (SQLException e) {
      logger.error(e);
      throw new JspException(
          "Errore durante la lettura dei dati nell'anagrafica impresa ", e);
    }
  }
  item.put("pecDis", emailDisabled[0]);
  item.put("emailDis", emailDisabled[1]);
  item.put("faxDis", emailDisabled[2]);
  item.put("anonimaDis", emailDisabled[3]);
  item.put("mandatariaPresente", mandatariaPresente);

}
}

