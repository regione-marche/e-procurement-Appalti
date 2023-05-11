<%
/*
 * Created on: 17-feb-2015
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

<gene:redefineInsert name="head" >
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.documento.js"></script>
	
	<style type="text/css">
		.dataTables_filter {
	     	display: none;
		}
		
		.dataTables_length {
			padding-top: 5px;
			padding-bottom: 5px;
		}
		
		.dataTables_length label {
			vertical-align: bottom;
		}
		
		.dataTables_paginate {
			padding-bottom: 5px;
		}

	</style>
	
</gene:redefineInsert>
	
<form id="formwsdm" name="formwsdm">
	<table class="dettaglio-tab">
		
		<input id="syscon" type="hidden" value="${profiloUtente.id}" /> 
		<input id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
		
		<input id="modoapertura" type="hidden" value="VISUALIZZA" /> 
		<input id="entita" type="hidden" value="${param.entita}" /> 
		<input id="key1" type="hidden" value="${param.key1}" /> 
		<input id="key2" type="hidden" value="${param.key2}" />
		<input id="key3" type="hidden" value="${param.key3}" />
		<input id="key4" type="hidden" value="${param.key4}" />
		<input id="idconfi" type="hidden" value="${param.idconfi}" />
		
		<tr>
			<td colspan="2" style="border-bottom: 0px;">
				<br>
				<b>Lista degli elementi documentali inviati al sistema remoto ed eventualmente protocollati</b>
				<span id="comins">
					<img title="Protocolla comunicazione" alt="Protocolla comunicazione" style="float: right; padding-bottom:5px;" height="24" width="24" src="img/Files-76.png">
				</span>
				<br>
				<div id="documenticontainer"></div>
				<div style="display: none;" class="error" id="documentimessaggio"></div>
			</td>
		</tr>
	</table>	
</form>
		
<form name="formwsdmcomins" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/wsdm/wsdm-comunicazione.jsp" /> 
	<input type="hidden" name="entita" value="${param.entita}" />
	<input type="hidden" name="key1" value="${param.key1}" />
	<input type="hidden" name="key2" value="${param.key2}" /> 
	<input type="hidden" name="key3" value="${param.key3}" />
	<input type="hidden" name="key4" value="${param.key4}" />
	<input type="hidden" name="metodo" value="apri" />
	<input type="hidden" name="idconfi" value="${param.idconfi}" />
	<input type="hidden" name="activePage" value="0" />
</form>
		

