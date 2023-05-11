<%
/*
 * Created on: 11-07-2018
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra che visualizza la conferma per la copia degli attributi aggiuntivi nei lotti
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>



<div style="width:97%;">

<gene:template file="popup-template.jsp">
	
	<c:choose>
		<c:when test='${!empty RISULTATO}'>
			<c:set var="lottoSorgente" value='${lottoSorgente}' />
			<c:set var="codiceGara" value='${codiceGara}' />
			<c:set var="isLotto" value='${param.isLotto}' />
		</c:when>
		<c:otherwise>
			<c:set var="lottoSorgente" value="${param.lottoSorgente}" />
			<c:set var="codiceGara" value='${param.codiceGara}' />
			<c:set var="isLotto" value='${!param.lottounico}' />
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value="Importazione delle RdA/RdI collegate ${isLotto != 'true'? 'alla gara' : 'al lotto'}" />
		
	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		
		<c:set var="modo" value="MODIFICA" scope="request" />
	
		<table class="dettaglio-notab">
					<tr>
						<td colspan="2">
							<c:choose>
								<c:when test="${!empty RISULTATO and RISULTATO eq 'ok'}">
									<br>
									Le RdA/RdI collegate ${isLotto != 'true'? 'alla gara' : 'al lotto'} sono state importate correttamente.
									<br>
						  			<br>
								</c:when>
								<c:otherwise>
									Mediante questa funzione è possibile importare la lista lavorazioni e forniture a partire dalle RdA/RdI collegate ${isLotto != 'true'? 'alla gara' : ('al lotto '.concat(lottoSorgente))}</b>.
									<br><br>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					
					<tr>
						<td class="comandi-dettaglio" colSpan="2">
							<c:if test="${empty RISULTATO}" >
								<INPUT type="button" class="bottone-azione" value="Importa" title="Importa" onclick="javascript:confermaImportRda();">
							</c:if>
							<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
						
		</table>

  	</gene:redefineInsert>

	<gene:javaScript>
		
		<c:if test="${empty RISULTATO}">
			function confermaImportRda(){
				bloccaRichiesteServer();
				var isLotto = ${isLotto};
				var href = contextPath + "/pg/EseguiImportRda.do?"+csrfToken+"&codgar=${codiceGara}&ngara=${lottoSorgente}&isLotto="+isLotto;
				document.location.href = href;
			}
		</c:if>
		
		function annulla(){
			window.close();
		}
		
		<c:if test='${RISULTATO eq "ok"}'>
		<% // Ricarica della lista sottostante %>
		var n = window.opener.document.forms[0].pgCorrente.value;
		window.opener.listaVaiAPagina(0);
		
		window.onfocus=fnFocus;
	</c:if>

	</gene:javaScript>
	
	</gene:template>
</div>
