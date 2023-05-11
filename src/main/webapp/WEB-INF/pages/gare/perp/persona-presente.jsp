<%
/*
 * Created on: 24/11/2008
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

<c:set var="numeroGara" value='${fn:substringBefore(param.chiave, ";")}' />
<c:set var="numeroSeduta" value='${fn:substringAfter(param.chiave, ";")}' />

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="PERP" campoFittizio="true" visibile="false" definizione="T20;1;;;G1NGARAP" value="${item[0]}" />
		<gene:campoScheda campo="NUMPER_${param.contatore}" entita="PERP" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NUMPER" value="${item[1]}" />
		<gene:campoScheda campo="NOMPER_${param.contatore}" entita="PERP" campoFittizio="true" definizione="T60;0;;;G1NOMPER" value="${item[2]}" />
		<gene:archivio titolo="ditte partecipanti alla gara" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.PERP.CODIMP"), "gene/impr/impr-lista-popup.jsp", "")}'
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
			campi="IMPR.CODIMP;IMPR.NOMIMP"
			functionId="perp"
			parametriWhere="T:${numeroGara}"
			chiave="PERP_CODIMP_${param.contatore}"
			formName="formDittaRappresentata${param.contatore}"
			inseribile="false">
			<gene:campoScheda campo="CODIMP_${param.contatore}" entita="PERP" campoFittizio="true" definizione="T10;;;;G1CODIMP" value="${item[3]}" />
			<gene:campoScheda campo="NOMIMP_${param.contatore}" entita="PERP" campoFittizio="true" definizione="T61;0;;;G1NOMIMP" value="${item[4]}" /> 
		</gene:archivio>
		<gene:campoScheda campo="DESPER_${param.contatore}" entita="PERP" campoFittizio="true" definizione="T240;0;;NOTE;G1DESPER" value="${item[5]}" />
		<gene:campoScheda campo="NUMSED_${param.contatore}" entita="PERP" campoFittizio="true" definizione="N3;1;;;G1NUMSEDP" visibile="false" value="${item[6]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="PERP" campoFittizio="true" visibile="false" definizione="T20;1;;;G1NGARAP" value="${numeroGara}" />
		<gene:campoScheda campo="NUMPER_${param.contatore}" entita="PERP" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NUMPER" />
		<gene:campoScheda campo="NOMPER_${param.contatore}" entita="PERP" campoFittizio="true" definizione="T60;0;;;G1NOMPER" />
		<gene:archivio titolo="ditte partecipanti alla gara"
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.PERP.CODIMP"), "gene/impr/impr-lista-popup.jsp", "")}'
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
			campi="IMPR.CODIMP;IMPR.NOMIMP"
			functionId="perpInvoff"
			parametriWhere="T:${numeroGara}"
			chiave="PERP_CODIMP_${param.contatore}"
			formName="formDittaRappresentata${param.contatore}"
			inseribile="false">
			<gene:campoScheda campo="CODIMP_${param.contatore}" entita="PERP" campoFittizio="true" definizione="T10;;;;G1CODIMP" />
			<gene:campoScheda campo="NOMIMP_${param.contatore}" entita="PERP" campoFittizio="true" definizione="T61;0;;;G1NOMIMP" /> 
		</gene:archivio>
		<gene:campoScheda campo="DESPER_${param.contatore}" entita="PERP" campoFittizio="true" definizione="T240;0;;NOTE;G1DESPER" />
		<gene:campoScheda campo="NUMSED_${param.contatore}" entita="PERP" campoFittizio="true" definizione="N3;1;;;G1NUMSEDP" visibile="false" value="${numeroSeduta}" />
	</c:otherwise>
</c:choose>	