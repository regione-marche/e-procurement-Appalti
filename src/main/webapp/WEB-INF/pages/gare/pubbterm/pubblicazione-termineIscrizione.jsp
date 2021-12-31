<%
/*
 * Created on: 29/07/2010
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

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="CODGAR_${param.contatore}" entita="PUBBTERM" campoFittizio="true" visibile="false" definizione="T20;1;;;CODGARPT" value="${item[0]}" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="PUBBTERM" campoFittizio="true" visibile="false" definizione="T10;1;;;NGARAPT" value="${item[1]}" />
		<gene:campoScheda campo="NUMPT_${param.contatore}" entita="PUBBTERM" campoFittizio="true" visibile="false" definizione="N3;1;;;NUMPT" value="${item[2]}" />

		<gene:campoScheda campo="NPUBAVVBAN_${param.contatore}" entita="PUBBTERM" campoFittizio="true" definizione="T20;0;;;NPAVVBANPT" value="${item[3]}" />
		<gene:campoScheda campo="DPUBAVVBAN_${param.contatore}" entita="PUBBTERM" campoFittizio="true" definizione="T10;0;;DATA_ELDA;DPAVVBANPT" value="${item[4]}" />
		<gene:campoScheda campo="OPUBAVVBAN_${param.contatore}" entita="PUBBTERM" campoFittizio="true" definizione="T6;0;;ORA;ORAABANPT" value="${item[5]}" />
		<gene:campoScheda campo="DTERMPRES_${param.contatore}"  entita="PUBBTERM" campoFittizio="true" definizione="T10;0;;DATA_ELDA;DATTPRESPT" value="${item[6]}" />
		<gene:campoScheda campo="OTERMPRES_${param.contatore}"  entita="PUBBTERM" campoFittizio="true" definizione="T6;0;;ORA;ORATPRESPT" value="${item[7]}" />
		<gene:campoScheda campo="DSORTEGGIO_${param.contatore}" entita="PUBBTERM" campoFittizio="true" definizione="T10;0;;DATA_ELDA;DATSORTPT" value="${item[8]}" />
		<gene:campoScheda campo="OSORTEGGIO_${param.contatore}" entita="PUBBTERM" campoFittizio="true" definizione="T6;0;;ORA;ORASORTPT" value="${item[9]}" />
		<gene:campoScheda campo="NOTEPT_${param.contatore}"     entita="PUBBTERM" campoFittizio="true" definizione="T2000;0;;NOTE;NOTEPT" value="${item[10]}" />
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${param.tipoDettaglio eq 2}">
				<gene:campoScheda campo="CODGAR_${param.contatore}" entita="PUBBTERM" campoFittizio="true" visibile="false" definizione="T20;1;;;CODGARPT" />
				<gene:campoScheda campo="NGARA_${param.contatore}" entita="PUBBTERM" campoFittizio="true" visibile="false" definizione="T10;1;;;NGARAPT" />
			</c:when>
			<c:otherwise>
				<c:set var="tmpCodGar" value="$ ${param.chiave}" />
				<gene:campoScheda campo="CODGAR_${param.contatore}" entita="PUBBTERM" campoFittizio="true" visibile="false" definizione="T20;1;;;CODGARPT" value="${gene:if(modo eq 'MODIFICA', fn:replace(tmpCodGar, ' ', ''), '')}" />  
				<gene:campoScheda campo="NGARA_${param.contatore}" entita="PUBBTERM" campoFittizio="true" visibile="false" definizione="T20;1;;;NGARAPT" value="${gene:if(modo eq 'MODIFICA', param.chiave, '')}" /> 
			</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="NUMPT_${param.contatore}" entita="PUBBTERM" campoFittizio="true" visibile="false" definizione="N3;1;;;NUMPT" />
		<gene:campoScheda campo="NPUBAVVBAN_${param.contatore}" entita="PUBBTERM" campoFittizio="true" definizione="T20;0;;;NPAVVBANPT" />
		<gene:campoScheda campo="DPUBAVVBAN_${param.contatore}" entita="PUBBTERM" campoFittizio="true" definizione="T10;0;;DATA_ELDA;DPAVVBANPT" />
		<gene:campoScheda campo="OPUBAVVBAN_${param.contatore}" entita="PUBBTERM" campoFittizio="true" definizione="T6;0;;ORA;ORAABANPT" />
		<gene:campoScheda campo="DTERMPRES_${param.contatore}"  entita="PUBBTERM" campoFittizio="true" definizione="T10;0;;DATA_ELDA;DATTPRESPT" />
		<gene:campoScheda campo="OTERMPRES_${param.contatore}"  entita="PUBBTERM" campoFittizio="true" definizione="T6;0;;ORA;ORATPRESPT" />
		<gene:campoScheda campo="DSORTEGGIO_${param.contatore}" entita="PUBBTERM" campoFittizio="true" definizione="T10;0;;DATA_ELDA;DATSORTPT" />
		<gene:campoScheda campo="OSORTEGGIO_${param.contatore}" entita="PUBBTERM" campoFittizio="true" definizione="T6;0;;ORA;ORASORTPT" />
		<gene:campoScheda campo="NOTEPT_${param.contatore}"     entita="PUBBTERM" campoFittizio="true" definizione="T2000;0;;NOTE;NOTEPT" />
	</c:otherwise>
</c:choose>
