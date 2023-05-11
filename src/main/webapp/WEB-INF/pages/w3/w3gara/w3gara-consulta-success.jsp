<%/*
       * Created on 28-Mar-2008
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

<c:set var="moduloAttivo" value="${sessionScope.moduloAttivo}" scope="request" />
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="W3GARA-consulta-gara-lotto" schema="W3">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />

	<gene:setString name="titoloMaschera" value='Recupera anagrafiche gara e lotto da SIMOG'/>
	<gene:redefineInsert name="corpo">
	<form name="formSchedaW3GARA" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="w3/w3smartcig/w3smartcig-scheda.jsp" /> 
		<input type="hidden" name="entita" value="W3SMARTCIG" />
		<input type="hidden" name="key" value="" />
		<input type="hidden" name="metodo" value="apri" />
		<input type="hidden" name="activePage" value="0" />
		<table class="dettaglio-notab">
			
			<tr>
				<td class="valore-dato" colspan="2">
					<br>
					Il recupero dei dati della gara e del lotto dall'ANAC è avvenuto con successo.
					<br>
					<br>
					<c:choose>
						<c:when test="${w3gara_esistente eq '1'}">
							E' stato aggiunto il lotto <b>${w3lott_oggetto}</b> identificato dal codice CIG <b>${w3lott_cig}</b>
							alla gara, gi&agrave; presente in base dati, <b>${w3gara_oggetto}</b>.
						</c:when>
						<c:when test="${w3smartcig_esistente eq '1'}">
							Lo SMARTCIG <b>${w3lott_cig}</b> gi&agrave; esiste nella banca dati.
						</c:when>
						<c:when test="${w3smartcig_esistente eq '2'}">
							E' stata creata una nuova gara <b>${w3smartcig_oggetto}</b> 
							identificata dal codice SMARTCIG <b>${w3lott_cig}</b>.	
						</c:when>
						<c:otherwise>
							E' stata creata una nuova gara <b>${w3gara_oggetto}</b> ed 
							un nuovo lotto <b>${w3lott_oggetto}</b>	identificato dal codice CIG <b>${w3lott_cig}</b>.				
						</c:otherwise>
					</c:choose>
					<br>
					<br>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="comandi-dettaglio">
					<INPUT type="button" class="bottone-azione" value="Consulta la gara" title="Consulta la gara" onclick="javascript:apriW3GARA();">&nbsp;&nbsp;
					<INPUT type="button" class="bottone-azione" value="Ritorna alla pagina iniziale" title="Ritorna alla pagina iniziale" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
			</tr>
			
		</table>
	</form>
	
	</gene:redefineInsert>
	<gene:javaScript>
	
	function annulla(){
		document.location.href = '${pageContext.request.contextPath}/Home.do?' + csrfToken;
	}
	
	function apriW3GARA() {
		<c:if test="${!empty w3smartcig_numgara}">
			var key = "W3SMARTCIG.CODRICH=N:${w3smartcig_numgara}";
			document.formSchedaW3GARA.key.value=key;
			bloccaRichiesteServer();
			document.formSchedaW3GARA.submit();
		</c:if>
		<c:if test="${!empty w3gara_numgara}">
			var key = "W3GARA.NUMGARA=N:${w3gara_numgara}";
			document.formSchedaW3GARA.href.value="w3/w3gara/w3gara-scheda.jsp";
			document.formSchedaW3GARA.entita.value="W3GARA";
			document.formSchedaW3GARA.key.value=key;
			bloccaRichiesteServer();
			document.formSchedaW3GARA.submit();
		</c:if>
	}
	</gene:javaScript>
</gene:template>


