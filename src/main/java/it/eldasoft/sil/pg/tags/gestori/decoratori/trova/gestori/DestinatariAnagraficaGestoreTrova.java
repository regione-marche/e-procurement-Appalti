package it.eldasoft.sil.pg.tags.gestori.decoratori.trova.gestori;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.decorators.trova.gestori.AbstractGestoreTrova;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;

public class DestinatariAnagraficaGestoreTrova extends AbstractGestoreTrova {

  // Costanti
  private static final String CAMPO_FITT_NAME = "CampoFitt";
  private static final String CAMPO_FITT_ISCRIZ_NAME = "CampoFittIscriz";
  private static final String FILTRO_RADIO_NAME = "filtroRadio";
  private static final String IDPRG_NAME = "idprg";
  private static final String IDCOM_NAME = "idcom";
  private static final String NGARA_NAME = "CampoFittNgara";
  private static final String ABILITAZ_NAME = "CampoFittAbilitaz";
  private static final String CAMPO_FITT_RT_NAME = "CampoFittRT";
  
  private static final String GARE_LOTTO_1_FR1_part1="IMPR.CODIMP NOT IN (select DESCODSOG from w_invcomdes where IDPRG = ? and IDCOM = ? AND DESCODENT='IMPR' AND DESCODSOG is not null)";
  private static final String GARA_LOTTO_2_FR1_FILTER="(IMPR.TIPIMP is null or (IMPR.TIPIMP <> 3 and IMPR.TIPIMP <> 10))";
  
  private static final String GARA_LOTTO_1_FR1_FILTER = GARE_LOTTO_1_FR1_part1+" and "+GARA_LOTTO_2_FR1_FILTER;
  private static final String GARA_LOTTO_1_FR2_FILTER =
      "TECNI.CODTEC NOT IN (select DESCODSOG from w_invcomdes where IDPRG = ? and IDCOM = ? AND DESCODENT='TECNI' AND DESCODSOG is not null)";
  private static final String USRSYS_FILTER =  "exists (select *  from USRSYS where USRSYS.SYSCF = TECNI.CFTEC)"; 
  private static final String DITG_EXISTS_FILTER =  "(EXISTS (SELECT 1 FROM DITG JOIN GARE ON DITG.NGARA5=GARE.NGARA WHERE DITG.DITTAO=IMPR.CODIMP AND GARE.GENERE=10 #trovaAddWhere#"; 
  private static final String NGARA_FILTER =  " AND DITG.NGARA5= ? AND EXISTS (select 1 from GAREALBO where GAREALBO.NGARA= ? )";
  private static final String ABILITAZ_FILTER =  " AND DITG.ABILITAZ= ?";
  private static final String RT_MANDATARIE_FILTER = " OR EXISTS (SELECT 1 FROM RAGIMP WHERE %s EXISTS (SELECT 1 FROM DITG JOIN GARE ON DITG.NGARA5=GARE.NGARA WHERE DITG.DITTAO=RAGIMP.CODIME9 AND RAGIMP.CODDIC=IMPR.CODIMP AND GARE.GENERE=10 AND EXISTS(SELECT 1 FROM RAGIMP R1 JOIN IMPR ON R1.CODDIC=IMPR.CODIMP WHERE R1.IMPMAN='1' AND R1.CODIME9=RAGIMP.CODIME9 #trovaAddWhere#)";
  
  private final String IDPRG;
  private final String IDCOM;
  private final String NGARA;
  private final String ABILITAZ;

  private List<String> parameters = new ArrayList<String>();

  // Costruttori
  public DestinatariAnagraficaGestoreTrova(HttpServletRequest request, String entity) {
    super(Logger.getLogger(DestinatariAnagraficaGestoreTrova.class), request, entity);

    IDPRG = UtilityStruts.getParametroString(request, IDPRG_NAME);
    IDCOM = UtilityStruts.getParametroString(request, IDCOM_NAME);
    NGARA = UtilityStruts.getParametroString(request, NGARA_NAME);
    ABILITAZ = UtilityStruts.getParametroString(request, ABILITAZ_NAME);

    popUpLevel=1;
  }

  // Metodi
  @Override
  public String composeFilter() {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("composeFilter: inizio metodo");

    final int campoFittVal = Integer.parseInt(UtilityStruts.getParametroString(request, CAMPO_FITT_NAME));
    String WHERE =  getFilters()[campoFittVal - 1];
    
    if(campoFittVal==1) {
      parameters.add("T:"+IDPRG);
      parameters.add("N:"+IDCOM);
    }
    
    //APP-1316
    String campoFittIscriz = UtilityStruts.getParametroString(request, CAMPO_FITT_ISCRIZ_NAME);
    if(StringUtils.isNotEmpty(campoFittIscriz)){
      Integer campoFittIscrizVal = Integer.parseInt(campoFittIscriz);
      if(StringUtils.isNotEmpty(WHERE))
        WHERE += " AND ";
      if(campoFittIscrizVal==2)
        WHERE += "<NOT> ";
      WHERE+=DITG_EXISTS_FILTER;

      WHERE = addFilters(WHERE);
      WHERE = addMandatarie(WHERE);
      WHERE += "<end>";
    }
    //necessito di questa istruzione per aggiungere alcuni parameters che arrivano dal request. Non posso concaternarli nel filter come succedeva prima!
    UtilityTags.putAttributeForSqlBuild(request.getSession(), ENTITY, popUpLevel, "trovaParameterDestinatariAnagrafica",  String.join(";", parameters), true);
    
    if (LOGGER.isDebugEnabled()) LOGGER.debug("composeFilter: fine metodo");

    return WHERE;
  }

  private String[] getFilters() {
    final int radioFilterValue = Integer.parseInt(UtilityStruts.getParametroString(request, FILTRO_RADIO_NAME));

    switch (radioFilterValue) {
    case 1:
      return new String[] {GARA_LOTTO_1_FR1_FILTER, GARA_LOTTO_2_FR1_FILTER };
    case 2:
      return new String[] {GARA_LOTTO_1_FR2_FILTER, "" };
    case 3:
      return new String[] {USRSYS_FILTER+" AND " + GARA_LOTTO_1_FR2_FILTER, USRSYS_FILTER };
    default:
      return new String[] { "", "" };
    }
  }

  private String addFilters(String filter) {
    if(StringUtils.isNotEmpty(NGARA)) {
      filter+=  NGARA_FILTER;
      parameters.add("T:"+NGARA);
      parameters.add("T:"+NGARA);
    }
    if(StringUtils.isNotEmpty(ABILITAZ)) {
      filter+=  ABILITAZ_FILTER;
      parameters.add("N:"+ABILITAZ);
    }
    return filter+")";
  }
  
  private String addMandatarie(String filter) {
    
    final int campoFittRT = Integer.parseInt(UtilityStruts.getParametroString(request, CAMPO_FITT_RT_NAME));
    
    String filterAdd = RT_MANDATARIE_FILTER;
    //solo mandatarie?
    filterAdd = String.format(filterAdd, campoFittRT==1? "IMPMAN='1' AND" : "");

    filterAdd = addFilters(filterAdd);
    
    return filter+filterAdd+"))";
  }

}
