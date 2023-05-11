<%
/*
 * Created on: 19/12/2013
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<c:set var="ngara" value='${fn:substringBefore(param.chiave, ";")}' />

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="ID_${param.contatore}" entita="G1STIPULA" campoFittizio="true" visibile="false" definizione="N12;1;;;G1ST_ID " value="${item[0]}" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="G1STIPULA" campoFittizio="true" visibile="false" definizione="T20;0;;;G1ST_NGARA" value="${item[1]}" />
		<gene:campoScheda campo="CODSTIPULA_${param.contatore}" entita="G1STIPULA" campoFittizio="true" modificabile="false" definizione="T20;0;;;G1ST_CODSTIPULA" value="${item[2]}" />
		<gene:campoScheda campo="OGGETTO_${param.contatore}" entita="G1STIPULA" campoFittizio="true" modificabile="false" definizione="T2000;0;;;G1ST_OGGETTO" value="${item[3]}" />
		<gene:campoScheda campo="IMPSTIPULA_${param.contatore}" entita="G1STIPULA" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;G1ST_IMPSTIPULA" value="${item[4]}" />
		<gene:campoScheda title="Creato da" campo="CREATO_DA_${param.contatore}" entita="G1STIPULA" campoFittizio="true" definizione="T80;0;;;G_USYSUTE" value="${item[8]}" modificabile="false"/>
		<gene:campoScheda title="Contract Manager" campo="ASSEGNATO_A_${param.contatore}" entita="G1STIPULA" campoFittizio="true" definizione="T80;0;;;G_USYSUTE" value="${item[9]}" modificabile="false"/>
		<gene:campoScheda campo="STATO_${param.contatore}" entita="G1STIPULA" campoFittizio="true" modificabile="false" definizione="N3;0;A1180;;G1ST_STATO" value="${item[10]}" />
		<gene:campoScheda campo="TIATTO_${param.contatore}" entita="G1STIPULA" campoFittizio="true" modificabile="false" definizione="N3;0;A2042;;G1ST_TIATTO" value="${item[5]}" />
		<gene:campoScheda campo="NREPAT_${param.contatore}" entita="G1STIPULA" campoFittizio="true" modificabile="false" definizione="T40;0;;;G1ST_NREPAT" value="${item[6]}" />
		<gene:campoScheda campo="DAATTO_${param.contatore}" entita="G1STIPULA" campoFittizio="true" modificabile="false" definizione="D;0;;;G1ST_DAATTO" value="${item[7]}" />
		
		
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="ID_${param.contatore}" entita="G1STIPULA" campoFittizio="true" visibile="false" definizione="N12;1;;;G1ST_ID " />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="G1STIPULA" campoFittizio="true" visibile="false" definizione="T20;0;;;G1ST_NGARA" value="${ngara}"/>
		<gene:campoScheda campo="CODSTIPULA_${param.contatore}" entita="G1STIPULA" campoFittizio="true" modificabile="false" definizione="T20;0;;;G1ST_CODSTIPULA"  />
		<gene:campoScheda campo="OGGETTO_${param.contatore}" entita="G1STIPULA" campoFittizio="true" modificabile="false" definizione="T2000;0;;;G1ST_OGGETTO" />
		<gene:campoScheda campo="IMPSTIPULA_${param.contatore}" entita="G1STIPULA" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;G1ST_IMPSTIPULA" />
		<gene:campoScheda title="Creato da" campo="CREATO_DA_${param.contatore}" entita="G1STIPULA" campoFittizio="true" definizione="T80;0;;;G_USYSUTE" modificabile="false"/>
		<gene:campoScheda title="Contract Manager" campo="ASSEGNATO_A_${param.contatore}" entita="G1STIPULA" campoFittizio="true" definizione="T80;0;;;G_USYSUTE" modificabile="false"/>
		<gene:campoScheda campo="STATO_${param.contatore}" entita="G1STIPULA" campoFittizio="true" modificabile="false" definizione="N3;0;A1180;;G1ST_STATO" />
		<gene:campoScheda campo="TIATTO_${param.contatore}" entita="G1STIPULA" campoFittizio="true" modificabile="false" definizione="N3;0;A2042;;G1ST_TIATTO" />
		<gene:campoScheda campo="NREPAT_${param.contatore}" entita="G1STIPULA" campoFittizio="true" modificabile="false" definizione="T40;0;;;G1ST_NREPAT" />
		<gene:campoScheda campo="DAATTO_${param.contatore}" entita="G1STIPULA" campoFittizio="true" modificabile="false" definizione="D;0;;;G1ST_DAATTO" />
		
		
	</c:otherwise>
</c:choose>




