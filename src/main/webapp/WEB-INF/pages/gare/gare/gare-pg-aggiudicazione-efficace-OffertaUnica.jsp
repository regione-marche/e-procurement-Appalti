<%
/*
 * Created on: 13/06/2016
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

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>

<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:if test='${isGaraLottiConOffertaUnica eq "true" and not empty param.codiceGara}' >
	<c:set var="codiceGara" value='${param.codiceGara}' />
</c:if>

<c:set var="ngara" value='${gene:getValCampo(key, "GARE.NGARA")}' />
<c:set var="modcont" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetModcontFunction", pageContext, codiceGara)}' />
<c:set var="ncont" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNCONTFunction", pageContext, ngara, modcont)}' />

<c:choose>
		<c:when test='${!empty param.codimp}'>
			<c:set var="codimp" value='${param.codimp}' />
		</c:when>
		<c:otherwise>
			<c:set var="codimp" value="${cenint}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.isAccordoQuadro}'>
			<c:set var="isAccordoQuadro" value='${param.isAccordoQuadro}' />
		</c:when>
		<c:otherwise>
			<c:set var="isAccordoQuadro" value="${isAccordoQuadro}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.codcont}'>
			<c:set var="codcont" value='${param.codcont}' />
		</c:when>
		<c:otherwise>
			<c:set var="codcont" value="${codcont}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.ngaral}'>
			<c:set var="ngaral" value='${param.ngaral}' />
		</c:when>
		<c:otherwise>
			<c:set var="ngaral" value="${ngaral}" />
		</c:otherwise>
	</c:choose>

<%/* Dati generali della gara */%>
<gene:formScheda entita="GARE" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInizializzazioniAttoContrattuale"
		gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreAttoContrattuale">

	
	<gene:redefineInsert name="schedaNuovo" />
	<gene:redefineInsert name="pulsanteNuovo"/>
	
	<jsp:include page="gare-interno-contratto-OffertaUnica.jsp">
		<jsp:param name="modcont" value="${modcont}"/>
		<jsp:param name="tipoContratto" value="aggEff"/>
		<jsp:param name="ncont" value="${ncont}"/>
	</jsp:include>
		
		
	<input type="hidden" name="MODCONT" id= "MODCONT" value="${modcont}" />
	<input type="hidden" name="codimp" id= "codimp" value="${codimp}" />
	<input type="hidden" name="isAccordoQuadro" id= "isAccordoQuadro" value="${isAccordoQuadro}" />
	<input type="hidden" name="codcont" id= "codcont" value="${codcont}" />
	<input type="hidden" name="ngaral" id= "ngaral" value="${ngaral}" />
	<input type="hidden" name="ncont" id= "ncont" value="${ncont}" />
	<input type="hidden" name="modcont" id= "modcont" value="${modcont}" />
	
	<gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="V_GARE_TORN"/>
			<jsp:param name="inputFiltro" value="CODGAR=T:${datiRiga.GARE_CODGAR1}"/>
			<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
		</jsp:include>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
	</gene:campoScheda>
</gene:formScheda>
<gene:javaScript>
	
	var ridisoPrec = getValue("GARE_RIDISO");
	
	//La funzione aggiorna il valore dell'importo cauzione in funzione di RIDISO
	//Se ridiso=1 si dimezza l'importo cauzione, altrimenti si raddoppia
	function aggiornaImportoCauzione(){
		var newRidiso = getValue("GARE_RIDISO");
		var oldRidiso = ridisoPrec;
		var nomeCampoImporto = "CAUZIONE";
		var modcont = "${modcont}";
		if(modcont=='1')
			nomeCampoImporto = "GARE_IMPGAR";
		
		var importoCauzione = getValue(nomeCampoImporto);
		
		if(importoCauzione == null ||importoCauzione=="") importoCauzione=0;
		importoCauzione=parseFloat(importoCauzione);
		
		if (newRidiso == null || newRidiso=="") newRidiso=2;
		if (oldRidiso == null || oldRidiso=="") oldRidiso=2;
		
		if (newRidiso != oldRidiso) {
			if (newRidiso==1) {
				importoCauzione = importoCauzione / 2;
			}else{ 
				importoCauzione = importoCauzione * 2;
			}
			setValue(nomeCampoImporto,  round(eval(importoCauzione), 5));
		}
		ridisoPrec = newRidiso;
	}
	
	var selezionaPaginaDefault = selezionaPagina;
	var selezionaPagina = selezionaPaginaCustom;
	function selezionaPaginaCustom(pageNumber){
		var modcont="${modcont }";
		var isAccordoQuadro="${isAccordoQuadro }";
		var codcont="${codcont }";
		var ncont="${ncont }";
		var ngaral="${ngaral }";
		var codimp="${codimp }";
		document.pagineForm.action += "&modcont=" + modcont + "&isAccordoQuadro=" + isAccordoQuadro + "&codcont=" + codcont + "&ncont=" + ncont + "&ngaral=" + ngaral + "&codimp=" + codimp;
		selezionaPaginaDefault(pageNumber);
	}
	
</gene:javaScript>