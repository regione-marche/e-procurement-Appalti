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
		<form action="${contextPath}/w3/ConsultaGaraLotto.do" method="post" name="formIDGARACIG">
			<table class="dettaglio-notab">
				<jsp:include page="../commons/sezione-credenziali-simog.jsp"/>
				
				<tr>	
					<td class="etichetta-dato">Codice CIG (*)</td>
					<td class="valore-dato">
						<input type="text" name="cig" id="cig" size="10" />
					</td>
				</tr>
				<tr>
					<td colspan="2" class="comandi-dettaglio">
						<INPUT type="button" class="bottone-azione" value="Recupera da SIMOG" title="Recupera da SIMOG" onclick="javascript:recuperaSIMOG();">
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</form>	
	</gene:redefineInsert>
	
	<gene:javaScript>
		
		function annulla(){
			goHome('${moduloAttivo}');
		}
		
		function recuperaSIMOG(){
		
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
			
			var cig = document.formIDGARACIG.cig;
			if (invia == "true" && cig.value == "") {
				alert("Inserire il codice CIG del lotto da recuperare o lo SmartCig");
				invia = "false";
			}

			if (invia == "true") {
				document.formIDGARACIG.submit();
				bloccaRichiesteServer();
			}				
				
		}
		
		
	
	</gene:javaScript>	
	
	
</gene:template>


