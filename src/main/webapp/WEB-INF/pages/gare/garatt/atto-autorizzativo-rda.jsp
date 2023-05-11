<%
/*
 * Created on: 28/10/2008
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
		<gene:campoScheda campo="CODGAR_${param.contatore}" entita="GARATT" campoFittizio="true" visibile="false" definizione="T21;1;;;G1CODGARAT" value="${item[0]}" />
		<gene:campoScheda campo="NUMATT_${param.contatore}" entita="GARATT" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NUMATT" value="${item[1]}" />
		<gene:campoScheda campo="TATTOT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="N7;0;A2045;;G1TATTOAT" value="4" />
		<gene:archivio titolo="rda"
			lista='gare/garerda/popup-lista-rda.jsp'
			scheda=""
			schedaPopUp=""
			campi="V_SMAT_RDA.DATA_APPROVAZIONE;V_SMAT_RDA.NUMERO_RDA"
			functionId="skip"
			chiave=""
			formName="formAttoAutorizzativoRda${param.contatore}"
			inseribile="false">
			<gene:campoScheda campo="DATTOT_${param.contatore}" entita="GARATT" campoFittizio="true" modificabile ="false" definizione="D;0;;;G1DATTOAT" value="${item[3]}" /> 
			<gene:campoScheda campo="NATTOT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="T30;0;;;G1NATTOAT" value="${item[4]}" />
		</gene:archivio>
		<gene:campoScheda campo="NPROAT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="T20;0;;;G1NPROAAT" value="${item[5]}" />
		<gene:campoScheda campo="DPROAA_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="T10;0;;;G1DPROAA" value="${item[6]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="CODGAR_${param.contatore}" entita="GARATT" campoFittizio="true" visibile="false" definizione="T21;1;;;G1CODGARAT" value="${param.chiave}" />
		<gene:campoScheda campo="NUMATT_${param.contatore}" entita="GARATT" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NUMATT" />
		<gene:campoScheda campo="TATTOT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="N7;0;A2045;;G1TATTOAT" value="4" />
		<gene:archivio titolo="rda"
			lista='gare/garerda/popup-lista-rda.jsp'
			scheda=""
			schedaPopUp=""
			campi="V_SMAT_RDA.DATA_APPROVAZIONE;V_SMAT_RDA.NUMERO_RDA"
			functionId="skip"
			chiave=""
			formName="formAttoAutorizzativoRda${param.contatore}"
			inseribile="false">
			<gene:campoScheda campo="DATTOT_${param.contatore}" entita="GARATT" campoFittizio="true" modificabile ="false" definizione="D;0;;;G1DATTOAT" /> 
			<gene:campoScheda campo="NATTOT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="T30;0;;;G1NATTOAT" />
		</gene:archivio>
		<gene:campoScheda campo="NPROAT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="T20;0;;;G1NPROAAT" />
		<gene:campoScheda campo="DPROAA_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="T10;0;;;G1DPROAA" />
	</c:otherwise>
</c:choose>