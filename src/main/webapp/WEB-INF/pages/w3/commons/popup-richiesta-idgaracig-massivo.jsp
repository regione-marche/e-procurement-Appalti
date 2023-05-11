
<%
	/*
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

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	
	<gene:setString name="titoloMaschera"  value='Pubblicazione gare massivo' />
	<c:set var="loginRPNT" value='${gene:callFunction("it.eldasoft.sil.w3.tags.funzioni.IsAttivaLoginRPNTFunction", pageContext)}' scope="request"/>

	<gene:redefineInsert name="corpo">
		<form action="${contextPath}/w3/RichiestaIDGARACIGMassivo.do" method="post" name="formIDGARACIG">
			<table class="dettaglio-notab">
			<tr>
					<td class="valore-dato" colspan="2" >
					</td>
				</tr>
				<tr>
					<td class="etichetta-dato">
						Sql
					</td>
					<td class="valore-dato">
						<input type="textarea" name="sql" value="" size="100" /><br> es: SELECT NUMGARA FROM W3GARA WHERE ...<br>
					</td>
				</tr>
				<tr>
					<td class="etichetta-dato">
						Id del centro di costo 
					</td>
					<td class="valore-dato">
						<input type="text" name="index" value="" /> <br>es: 0,1,2 etc...
					</td>
				</tr>
				<tr>
					<td class="etichetta-dato">
						Utente SIMOG (CF)
					</td>
					<td class="valore-dato">
						<input type="text" name="simogwsuser" id="simogwsuser" size="20" />
					</td>
				</tr>
				<c:choose>
					<c:when test="${((loginRPNT ne '1') ||  (loginRPNT eq '1' && erroreCredenzialiRPNT))}">
					<tr>
						<td class="etichetta-dato">
							Password SIMOG
						</td>
						<td class="valore-dato">
							<input type="password" name="simogwspass" id="simogwspass" size="20"/>
						</td>
						</tr>
					</c:when>
					<c:otherwise>
						<input type="hidden" name="simogwspass" id="simogwspass" size="20" value="."/>
					</c:otherwise>
				</c:choose>
				
				<tr>
					<td colspan="2" class="comandi-dettaglio">
						<INPUT type="button" class="bottone-azione" value="Invia richiesta di assegnazione" title="Invia richiesta di assegnazione" onclick="javascript:richiestaIDGARACIG();">
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</form>	
	</gene:redefineInsert>
	
	<gene:javaScript>
		document.forms[0].jspPathTo.value="w3/commons/popup-richiesta-idgaracig-massivo.jsp";
		
		function annulla(){
			window.close();
		}
		
		function richiestaIDGARACIG(){
		
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

