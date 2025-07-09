<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="codiceLavoro" value="${componiVerbaleForm.valChiavi[0]}"/>
<c:set var="entita" value="${componiVerbaleForm.entita}"/>

<gene:setIdPagina schema="GENE" maschera="C0OGGASS-AssociaModelloComposto" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />

<script type="text/javascript">
	<!--
  function downloadFile(){
  if (navigator.appName == "Microsoft Internet Explorer") {
  	window.onblur = null;
  }
  	document.componiVerbaleForm.metodo.value="download";
    document.componiVerbaleForm.submit();
  }

  function associaFile(){
    bloccaRichiesteServer();
  	document.componiVerbaleForm.metodo.value="associaModello";
    document.componiVerbaleForm.submit();
  }

  function tornaAllaLista(){
    bloccaRichiesteServer();
  	document.componiVerbaleForm.metodo.value="eliminaComposizione";
    document.componiVerbaleForm.submit();
  }
  -->
</script>
</HEAD>

<BODY onload='setVariables();checkLocation();'>

<!-- Parte durante la composizione -->
<table style="border: thin; position: absolute; top: 30%; left: 5%;"
	align="left" width="90%">
	<tr>
		<TD align="center">
		<div class="titolomaschera">Composizione modello ${componiVerbaleForm.nomeModello} avvenuta con successo</div>
		<div class="contenitore-errori-arealavoro">
		<jsp:include page="/WEB-INF/pages/commons/serverMsg.jsp" />
		</div>
		<div class="contenitore-dettaglio">

		<table class="lista">
			<tr>
				<td>
		<c:if test='${componiVerbaleForm.paginaSorgente eq "scheda"}' >
			<c:if test='${sessionScope.entitaPrincipaleModificabile eq "1" && gene:checkProt(pageContext, "FUNZ.VIS.INS.GENE.C0OGGASS-AssociaModelloComposto.INS")}'>
				<br>
				<a href="javascript:associaFile();" title="Associa il documento"><img src="${contextPath}/img/allegato.gif" /></a>
				&nbsp;<a href="javascript:associaFile();" title="Associa il documento"><img src="${contextPath}/img/open_folder.gif" /></a>
				&nbsp;<a href="javascript:associaFile();" title="Associa il documento"><b>Associa il documento</b></a><br>
				Il documento prodotto viene salvato associandolo alla scheda di dettaglio a cui risulta collegato.<br>
				Il documento sarà sempre accessibile dalla scheda dati mediante la funzione Documenti associati.<br>
				<br>
			</c:if>
		</c:if>
				<br>
				<a href="javascript:downloadFile();" title="Apri o salva il documento"><img src="${contextPath}/img/open_folder.gif" /></a>
				&nbsp;<a href="javascript:downloadFile();" title="Apri o salva il documento"><b>Apri o salva il documento</b></a><br>
				Il documento prodotto non viene salvato pertanto pu&ograve; essere scaricato (download) solo ora nel proprio PC client.<br>
				<br>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio">
				<INPUT type="button" class="bottone-azione"	value="Esci" title="Esci" 
				onclick="javascript:tornaAllaLista();">&nbsp;
				<td>
			</tr>
		</table>
		</div>
		</TD>
	</tr>
</table>

<!-- Form per la compilazione -->
<html:form action="/ComponiVerbale">
	<input type="hidden" name="metodo" value="componiModello"/>
	<jsp:include page="/WEB-INF/pages/commons/csrf.jsp"></jsp:include>
	<html:hidden property="idSessione"/>
	<html:hidden property="riepilogativo"/>
	<html:hidden property="idModello"/>
	<html:hidden property="fileComposto"/>
	<html:hidden property="nomeModello"/>
	<html:hidden property="entita"/>
	<html:hidden property="nomeChiavi"/>
	<html:hidden property="valori"/>
	<html:hidden property="paginaSorgente"/>
	<html:hidden property="noFiltroEntitaPrincipale"/>
	<c:forEach items="${componiVerbaleForm.valChiavi}" var="chiave">
		<input type="hidden" name="valChiavi" value="${chiave}"/>
	</c:forEach>
</html:form>

<script type="text/javascript">
  // aggiunge la gestione in primo piano della popup anzichè definire
  // l'evento onblur sul body, dato che non è standard W3C, ma una
  // particolarità esclusivamente di internet explorer
  if (navigator.appName == "Microsoft Internet Explorer") {
  	window.onblur = new Function("window.focus()");
  }
</script>

<jsp:include page="/WEB-INF/pages/commons/bloccaRichieste.jsp" />
</BODY>
</HTML>
