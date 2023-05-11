<%
/*
 * Created on 24-09-2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE I VARI TIPI DI DOCUMENTAZIONE
 // PER IL QUALE E' POSSIBILE ACCEDERE ALLA LISTA G1DOCUMOD
%>


<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<c:set var="Titolo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1064","1","false")}'/>

<gene:template file="scheda-template.jsp">
<gene:setString name="titoloMaschera" value="Documentazione di gara e di contratto" />
<gene:redefineInsert name="corpo">
<gene:redefineInsert name="documentiAssociati" />
<gene:redefineInsert name="noteAvvisi" />

<style>
table.listadocstyle {
  border-collapse: collapse;
  width: 100%;
}

tr.listadocstyle:nth-child(even){background-color: #E7F1FF}

tr.listadocstyle:nth-child(odd){background-color: #CEDAEB}

tr.listadocstyle {
height: 40px;
}

a.listadocstyle{
font: 11px Verdana, Arial, Helvetica, sans-serif;
text-decoration: underline;
color: #000000;
padding: 2px 4px 2px 4px;
}

p.listadocstyle{
font: 11px Verdana, Arial, Helvetica, sans-serif;
}

</style>

<p class="listadocstyle">Selezionare il gruppo di documenti che si vuole configurare</p>

<table class="listadocstyle">

	<tr class="listadocstyle">
	 	<td>
			<a class="listadocstyle" href='javascript:document.location.href="${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/g1documod/g1documod-lista.jsp&gruppo=1&busta= "'>
			${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1064","1","false")}</a>
		</td>
	</tr>
	<tr class="listadocstyle">
		<td>
			<a class="listadocstyle" href='javascript:document.location.href="${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/g1documod/g1documod-lista.jsp&gruppo=6&busta= "'>
			${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1064","6","false")}</a>
		</td>
	</tr>
	<tr class="listadocstyle">
	 	<td>
			<a class="listadocstyle" href='javascript:document.location.href="${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/g1documod/g1documod-lista.jsp&gruppo=4&busta= "'>
			${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1064","4","false")}</a>
		</td>
	</tr>
	<tr class="listadocstyle">
	 	<td>
			<a class="listadocstyle" href='javascript:document.location.href="${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/g1documod/g1documod-lista.jsp&gruppo=3&busta=4"'>
			Documenti busta ${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1013","4","false")}</a>
		</td>
	</tr>
	<tr class="listadocstyle">
	 	<td>
			<a class="listadocstyle" href='javascript:document.location.href="${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/g1documod/g1documod-lista.jsp&gruppo=3&busta=1"'>
			Documenti busta ${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1013","1","false")}</a>
		</td>
	</tr>
	<tr class="listadocstyle">
	 	<td>
			<a class="listadocstyle" href='javascript:document.location.href="${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/g1documod/g1documod-lista.jsp&gruppo=3&busta=2"'>
			Documenti busta ${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1013","2","false")}</a>
		</td>
	</tr>
	<tr class="listadocstyle">
	 	<td>
			<a class="listadocstyle" href='javascript:document.location.href="${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/g1documod/g1documod-lista.jsp&gruppo=3&busta=3"'>
			Documenti busta ${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1013","3","false")}</a>
		</td>
	</tr>
	<tr class="listadocstyle">
		<td>
			<a class="listadocstyle" href='javascript:document.location.href="${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/g1documod/g1documod-lista.jsp&gruppo=20&busta= "'>
			${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1064","20","false")}</a>
		</td>
	</tr>
	<tr class="listadocstyle">
		<td>
			<a class="listadocstyle" href='javascript:document.location.href="${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/g1documod/g1documod-lista.jsp&gruppo=2&busta= "'>
			${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1064","2","false")}</a>
		</td>
	</tr>
	<br>
</table>



</gene:redefineInsert>
<gene:javaScript>

	function annullaProcedi(){
		bloccaRichiesteServer();
		historyBack();
	}
	
</gene:javaScript>	
</gene:template>