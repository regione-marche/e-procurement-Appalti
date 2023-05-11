<%/*
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

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<div style="width:97%;">

	<gene:template file="popup-template.jsp">
	
		<c:set var="stato_simog_gara" value='${gene:callFunction2("it.eldasoft.sil.w3.tags.funzioni.GetStatoSimogW3GaraFunction",pageContext,param.numgara)}' />
	
		<gene:redefineInsert name="addHistory" />
		<gene:redefineInsert name="gestioneHistory" />
	
		<gene:setString name="titoloMaschera"  value='Elaborazione di tutte le richieste' />
	
		<gene:redefineInsert name="corpo">
			<form action="${contextPath}/w3/ElaboraRichiesteLotti.do" method="post" name="formIDGARACIG">
			
				<input type="hidden" name="numgara" value="${param.numgara}" />
				<INPUT type="hidden" name="inviaConCigNonPresenti" value="1">
				
				<table class="dettaglio-notab">
				
					<td class="valore-dato" colspan="2">
							<b>Attenzione: in ANAC sono presenti dei CIG (${cigNonPresenti.size()}) per questa gara non associati ad alcuno dei lotti in anagrafica.</b>
							<br>
							<br>
							Per associare tali CIG, annullare l'operazione e avviare la richiesta CIG sul singolo lotto. Altrimenti, procedere con l'elaborazione.
							<br>
							<br>
					</td>
				
					<jsp:include page="../commons/sezione-credenziali-simog.jsp"/>
					<tr>
						<td colspan="2" class="comandi-dettaglio">
							<INPUT type="button" class="bottone-azione" value="Elabora le richieste" title="Elabora le richieste" onclick="javascript:elaborarichieste();">
							<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
					</tr>
				</table>
			</form>	
		</gene:redefineInsert>
		
		<gene:javaScript>
			document.forms[0].jspPathTo.value="w3/w3gara/w3gara-elabora-richieste-lotti.jsp";
			
			function annulla(){
				window.close();
			}
			
			function elaborarichieste(){
				var invia = "true";
				var simogwsuser = document.formIDGARACIG.simogwsuser;
				if (simogwsuser.value == "") {
					alert("Inserire l'utente");
					invia = "false";
				}
				
				var simogwspass = document.formIDGARACIG.simogwspass;			
				if (invia == "true" && simogwspass.value == "") {
					alert("Inserire la password");
					invia = "false";
				}
	
				if (invia == "true") {
					document.formIDGARACIG.submit();
					bloccaRichiesteServer();
				}				
			}
		</gene:javaScript>	
	</gene:template>
</div>
