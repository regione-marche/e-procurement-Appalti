
<%
  /*
			 * Created on 15-lug-2008
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

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
<c:set var="nomeApplicativo" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", nomeAppicativoCig)}'/>
<c:if test="${empty nomeApplicativo or nomeApplicativo eq '' }">
	<c:set var="nomeApplicativo" value='Vigilanza' />
</c:if>

<div style="width: 97%;"><gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:setString name="titoloMaschera" value='Invio dei dati a ${nomeApplicativo } per la richiesta di generazione del codice CIG' />

	<gene:redefineInsert name="corpo">
	
		<form action="${contextPath}/pg/InviaDatiRichiestaCig.do" method="post" name="formInviaDatiRichiestaCig" >
			<input type="hidden" name="codgar" value="${param.codgar}" />
			<input type="hidden" name="genere" value="${param.genere}" />
			<input type="hidden" name="username" value="${username}" />
			<input type="hidden" name="password" value="${password}" />
			<input type="hidden" name="credenziali" value="${credenziali}" />
			<input type="hidden" name="operazione" value="OP1.1" />  
			<input type="hidden" name="numeroLotto" value="${numeroLotto}" />
			<input type="hidden" name="tiporichiesta" value="${tiporichiesta}" />
						
			<table class="dettaglio-notab">
			<tr>
				<br>
				<b>L'invio dei dati non è completo per i seguenti motivi:</b>
				<br>
				<ul>
				<c:forEach items="${erroriNonBloccanti}" step="1" var="item">
					<li>${item}
				</c:forEach>
				</ul>
				<br>
				<br>
				<b>Vuoi proseguire con l'invio dei dati?</b>
				<br>
				<br>
				<br>
			</tr>
			
			<tr>
				<td colspan="2" class="comandi-dettaglio">
					<INPUT type="button" class="bottone-azione" value="Invia dati" title="Invia dati" onclick="javascript:inviadatirichiestacig();">
					<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
			</tr>
			
			</table>
		</form>	
	</gene:redefineInsert>

	<gene:javaScript>
		
		function annulla(){
			window.close();
		}
		
		function inviadatirichiestacig() {
			document.formInviaDatiRichiestaCig.submit();
			bloccaRichiesteServer();
		}
		
		
	</gene:javaScript>
</gene:template></div>

