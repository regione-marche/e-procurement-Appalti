<%/*
       * Created on 13-mag-2009
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA TEMPORANEA CHE INDICA L'ELABORAZIONE DEL MODELLO IN CORSO
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test="${!empty param.key}">
		<c:set var="KEY" value="${param.key}" />		
	</c:when>
	<c:otherwise>
		<c:set var="KEY" value="${requestScope.chiave}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.tipoProtocollo}">
		<c:set var="tipProt" value="${param.tipoProtocollo}" />		
	</c:when>
	<c:otherwise>
		<c:set var="tipProt" value="${requestScope.tipoProtocollo}" />
	</c:otherwise>
</c:choose>

<HTML>
	<HEAD>
		<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
	</HEAD>

	<BODY>
		<!-- Parte durante la composizione -->
		<table style="border: thin; position: absolute; top: 15%;left: 5%;"
			align="left" width="90%">
			<tr>
				<td align="center">
				<div class="titolomaschera">Composizione etichetta protocollo</div>
				<div class="contenitore-dettaglio">

				<table class="lista">
					<tr>
						<td align="center">
							<img src="${contextPath}/img/${applicationScope.pathCss}progressbar.gif" alt=""/>
							<br><br>Composizione in corso...
						</td>
					</tr>
				</table>
				</div>
				</td>
			</tr>
		</table>

		<form action="${contextPath}/pg/StampaEtichetta.do" method="post" name="stampaEtichettaForm" id="stampaEtichettaForm" >
			<input type="hidden" name="chiave" value="${KEY}"/>
			<input type="hidden" name="tipoProtocollo" value="${tipProt}" />
			<input type="hidden" name="metodo" value="componiEtichetta" />
			<input type="hidden" name="isIE" value="0" />
			<c:if test="${!empty requestScope.chiave}">
				<input type="hidden" name="barcode" value="si" />
			</c:if>
		<form>

		<script type="text/javascript">
		  // aggiunge la gestione in primo piano della popup anzichè definire
		  // l'evento onblur sul body, dato che non è standard W3C
		  if (navigator.appName == "Microsoft Internet Explorer"){
		  	window.onblur = new Function("window.focus()");
		  	document.stampaEtichettaForm.isIE.value = "1";
		  }
		  // inoltra la richiesta di composizione dopo aver caricato la pagina e
		  // dopo aver reso visibile la gif animata della progressbar, il cui
		  // comportamento dipende dal browser
		  if (navigator.appName == "Microsoft Internet Explorer") {
		    document.stampaEtichettaForm.submit();
		  } else {
		    window.setTimeout('document.stampaEtichettaForm.submit()', 500);
		  }
		</script>
	</BODY>
</HTML>