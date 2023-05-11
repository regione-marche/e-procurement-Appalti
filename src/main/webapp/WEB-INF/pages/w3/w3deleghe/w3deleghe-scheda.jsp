
<%
	/*
	 * Created on 04-Nov-2008
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */

	// Scheda degli intestatari della concessione stradale
%>


<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>


<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="W3DELEGHE-Scheda" schema="W3">
	<c:choose>
		<c:when test='${modo eq "NUOVO"}'>
			<gene:setString name="titoloMaschera" value="Nuova collaborazione RUP" />
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value='Collaborazione RUP' />
		</c:otherwise>
	</c:choose>
	<c:set var="filtroUffint" value="${!empty sessionScope.uffint}"/> 

	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="W9DELEGHE" gestisciProtezioni="true" gestore="it.eldasoft.sil.w3.tags.gestori.submit.GestoreW3DELEGHE">
			
			<gene:campoScheda>
				<td colspan="2">
					
					<c:if test='${modo eq "NUOVO"}'>
						<br>
							Attraverso la compilazione del form sottostante &#232; possibile è definire una nuova collaborazione, 
							ovvero delegare un utente alla compilazione e all'eventuale invio delle richieste verso SIMOG.
							<br>
							<br>
							Il collaboratore pu&#242; essere scelto tra gli utenti abilitati<c:if test='${filtroUffint}'> che hanno accesso alla stazione appaltante corrente</c:if>.
							<br>
							<br>
							
							Il ruolo di "Sola compilazione" consente al collaboratore di accedere all'anagrafica delle richieste CIG del RUP delegante e di compilarne tutte le sezioni.
							<br>
							Il ruolo di "Gestione completa" consente, in aggiunta, di effettuare gli invii verso SIMOG (Richiesta numero di gara, richiesta CIG, richieste di modifica ecc.)
						
							<br>
							<br>
					</c:if>
					<br>
				</td>
			<c:if test='${modo eq "NUOVO"}'>				
				<tr>
					<td class="etichetta-dato">Utente (*)</td>
					<td class="valore-dato">
							<gene:callFunction obj="it.eldasoft.sil.w3.tags.funzioni.GetListaUtentiDelegabiliFunction" parametro="${codtec}" />
							<select name="listaDelegabili" id="listaDelegabili" onChange="javascript:cambiaDelegato();">
								<option value=""></option>
								<c:if test='${!empty listaDelegabili}'>
									<c:forEach items="${listaDelegabili}" var="listaDelegabili">
										<option value="${listaDelegabili[0]}">${listaDelegabili[1]} - ${listaDelegabili[0]}</option>
									</c:forEach>
								</c:if>
							</select>
					</td>
				</tr>
			</c:if>
			</gene:campoScheda>	
			<c:if test='${modo ne "NUOVO"}'>	
					<gene:campoScheda title="Utente" campo="ID" value="${datiRiga.USRSYS_SYSUTE} - ${datiRiga.W9DELEGHE_ID_COLLABORATORE}" campoFittizio="true" definizione="T100;;;;" modificabile="false" computed="true"/>
				<gene:campoScheda campo="SYSUTE" entita="USRSYS" where="USRSYS.SYSCON=W9DELEGHE.ID_COLLABORATORE" title="Utente" modificabile="false" visibile="false"/>	
			</c:if>
			
			<gene:campoScheda campo="ID" obbligatorio="true" visibile="false"/>
			
			<gene:campoScheda campo="CFRUP" obbligatorio="true" modificabile="false" value="${sessionScope.profiloUtente.codiceFiscale}" visibile="false"/>								
			<gene:campoScheda campo="CODEIN" obbligatorio="true" value="${filtroUffint ? sessionScope.uffint : null}" visibile="false"/>
			<gene:campoScheda campo="RUOLO" obbligatorio="true" />
			
			<gene:campoScheda campo="ID_COLLABORATORE" title="Utente" obbligatorio="true" visibile="false" modificabile="false"/>

			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>	
			
		</gene:formScheda>
	</gene:redefineInsert>

	<gene:javaScript>
	document.forms[0].jspPathTo.value="w3/w3deleghe/w3deleghe-scheda.jsp";

	function cambiaDelegato() {
		var sel = document.getElementById("listaDelegabili");
   		var codDel = sel.value;
		document.forms[0].W9DELEGHE_ID_COLLABORATORE.value=codDel;
	}
	</gene:javaScript>	

</gene:template>


