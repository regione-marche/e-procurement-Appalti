<%
/*
 * Created on: 05/09/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="ID_${param.contatore}" entita="ANTICORDITTE" campoFittizio="true" visibile="false" definizione="N12;1;;;IDANTICORD" value="${item[0]}" />
		<gene:campoScheda campo="IDANTICORPARTECIP_${param.contatore}" entita="ANTICORDITTE" campoFittizio="true" visibile="false" definizione="N12;0;;;FKIDANTICORD" value="${item[1]}" />
		<gene:archivio titolo="imprese"
			obbligatorio="false" 
			scollegabile="true"
			lista='gene/impr/impr-listaL190-popup.jsp' 
			scheda="" 
			schedaPopUp="" 
			campi="IMPR.NOMEST;IMPR.CFIMP;IMPR.PIVIMP;IMPR.NAZIMP" 
			functionId="anticor"
			chiave=""
			inseribile="false"
			formName="formArchivioImprese_${param.contatore}">
			<gene:campoScheda campo="RAGSOC_${param.contatore}" entita="ANTICORDITTE" campoFittizio="true" definizione="T250;0;;;RAGSOCANTICORD" value="${item[2]}" />
			<gene:campoScheda campo="CF_FIT_${param.contatore}" campoFittizio="true" definizione="T50" visibile="false"/>
			<gene:campoScheda campo="PIVA_FIT_${param.contatore}" campoFittizio="true" definizione="T50" visibile="false"/>
			<gene:campoScheda campo="NAZIMP_FIT_${param.contatore}" campoFittizio="true" definizione="T50" visibile="false"/>
		</gene:archivio>
		<gene:campoScheda campo="CODFISC_${param.contatore}" entita="ANTICORDITTE" campoFittizio="true" definizione="T16;0;;;CFANTICORD" value="${item[3]}" />
		<gene:campoScheda campo="IDFISCEST_${param.contatore}" entita="ANTICORDITTE" campoFittizio="true" definizione="T16;0;;;CFESTANTICORD" value="${item[4]}" />
		<gene:campoScheda campo="RUOLO_${param.contatore}" entita="ANTICORDITTE" campoFittizio="true" definizione="N7;0;A1094;;RUOLOANTICORD" value="${item[5]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="ID_${param.contatore}" entita="ANTICORDITTE" campoFittizio="true" visibile="false" definizione="N12;1;;;IDANTICORD"  />
		<gene:campoScheda campo="IDANTICORPARTECIP_${param.contatore}" entita="ANTICORDITTE" campoFittizio="true" visibile="false" definizione="N12;0;;;FKIDANTICORD" value="${param.chiave}" />
		<gene:archivio titolo="imprese"
			obbligatorio="false" 
			scollegabile="true"
			lista='gene/impr/impr-listaL190-popup.jsp' 
			scheda="" 
			schedaPopUp="" 
			campi="IMPR.NOMEST;IMPR.CFIMP;IMPR.PIVIMP;IMPR.NAZIMP" 
			functionId="anticor"
			chiave=""
			inseribile="false"
			formName="formArchivioImprese_${param.contatore}">
			<gene:campoScheda campo="RAGSOC_${param.contatore}" entita="ANTICORDITTE" campoFittizio="true" definizione="T250;0;;;RAGSOCANTICORD"  />
			<gene:campoScheda campo="CF_FIT_${param.contatore}" campoFittizio="true" definizione="T50" visibile="false"/>
			<gene:campoScheda campo="PIVA_FIT_${param.contatore}" campoFittizio="true" definizione="T50" visibile="false"/>
			<gene:campoScheda campo="NAZIMP_FIT_${param.contatore}" campoFittizio="true" definizione="T50" visibile="false"/>
		</gene:archivio>
		<gene:campoScheda campo="CODFISC_${param.contatore}" entita="ANTICORDITTE" campoFittizio="true" definizione="T16;0;;;CFANTICORD" />
		<gene:campoScheda campo="IDFISCEST_${param.contatore}" entita="ANTICORDITTE" campoFittizio="true" definizione="T16;0;;;CFESTANTICORD" />
		<gene:campoScheda campo="RUOLO_${param.contatore}" entita="ANTICORDITTE" campoFittizio="true" definizione="N7;0;A1094;;RUOLOANTICORD"  />
		
	</c:otherwise>
</c:choose>