<%
/*
 * Created on: 21/11/2008
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
			<gene:campoScheda campo="NGARA_${param.contatore}" entita="GARSTR" campoFittizio="true" visibile="false" definizione="T20;1;;;G1NGARASTR" value="${item[0]}" />
			<gene:campoScheda campo="NUMSTR_${param.contatore}" entita="GARSTR" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NUMSTR" value="${item[1]}" />
			<gene:archivio titolo="Strade"
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.GARSTR.CODVIA"), "gene/astra/astra-lista-archivio-popup.jsp", "")}'
				scheda=""
				schedaPopUp=""
				campi="ASTRA.CODVIA;ASTRA.VIAPIA"
				chiave=""
				where=""
				inseribile="false" >
				<gene:campoScheda campo="CODVIA_${param.contatore}" entita="GARSTR" campoFittizio="true" definizione="T20;0;;;G1CODVIA" value="${item[2]}" modificabile="${param.datiModificabili }"/>
				<gene:campoScheda title="Denominazione" campo="VIAPIA_${param.contatore}" entita="ASTRA" campoFittizio="true" definizione="T60;0;;;VIAPIA" value="${item[3]}" 
				modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARSTR.CODVIA") && param.datiModificabili}' 
				visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARSTR.CODVIA")}'/>
			</gene:archivio>
			<gene:campoScheda campo="NOTSTR_${param.contatore}" entita="GARSTR" campoFittizio="true" definizione="T2000;0;;NOTE;G1NOTSTR" value="${item[4]}" modificabile="${param.datiModificabili }"/> 
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${param.tipoDettaglio eq 2}">
				<gene:campoScheda campo="NGARA_${param.contatore}" entita="GARSTR" campoFittizio="true" visibile="false" definizione="T20;1;;;G1NGARASTR" />
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="NGARA_${param.contatore}" entita="GARSTR" campoFittizio="true" visibile="false" definizione="T20;1;;;G1NGARASTR" value="${param.chiave}"/>
			</c:otherwise>
		</c:choose>
			<gene:campoScheda campo="NUMSTR_${param.contatore}" entita="GARSTR" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NUMSTR" />
			<gene:archivio titolo="Strade"
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.GARSTR.CODVIA"), "gene/astra/astra-lista-archivio-popup.jsp", "")}'
				scheda=""
				schedaPopUp=""
				campi="ASTRA.CODVIA;ASTRA.VIAPIA"
				chiave=""
				where=""
				inseribile="false" >
				<gene:campoScheda campo="CODVIA_${param.contatore}" entita="GARSTR" campoFittizio="true" definizione="T20;0;;;G1CODVIA" modificabile="${param.datiModificabili }"/>
				<gene:campoScheda title="Denominazione" campo="VIAPIA_${param.contatore}" entita="ASTRA" campoFittizio="true" definizione="T60;0;;;VIAPIA" 
				modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARSTR.CODVIA") and param.datiModificabili}' 
				visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARSTR.CODVIA")}'/>
			</gene:archivio>
			<gene:campoScheda campo="NOTSTR_${param.contatore}" entita="GARSTR" campoFittizio="true" definizione="T2000;0;;NOTE;G1NOTSTR" modificabile="${param.datiModificabili }"/> 
	</c:otherwise>
</c:choose>
