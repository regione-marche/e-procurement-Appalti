
<%
	/*
	 * Created on 04-Giu-2010
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




<gene:template file="popup-template.jsp">
	
	<c:choose>
		<c:when test='${!empty param.idprg}'>
			<c:set var="idprg" value="${param.idprg}" />
		</c:when>
		<c:otherwise>
			<c:set var="idprg" value="${idprg}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.idcom}'>
			<c:set var="idcom" value="${param.idcom}" />
		</c:when>
		<c:otherwise>
			<c:set var="idcom" value="${idcom}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.integrazioneWSDM}'>
			<c:set var="integrazioneWSDM" value="${param.integrazioneWSDM}" />
		</c:when>
		<c:otherwise>
			<c:set var="integrazioneWSDM" value="${integrazioneWSDM}" />
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value="Aggiungi destinatari da mandanti RT" />

	<gene:redefineInsert name="corpo">
	
		
		
			
							
		<c:set var="modo" value="NUOVO" scope="request" />
		<table class="lista">
			<tr>
				<td>
					<c:choose>
						<c:when test="${empty requestScope.inserimentoEseguito}">
							Mediante questa funzione &egrave; possibile aggiungere ai destinatari della comunicazione le mandanti dei raggruppamenti temporanei, già presenti tra i destinatari con la sola ditta mandataria.  
							<br><br>
							Confermi l'operazione ?
							<br><br>
						</c:when>
						<c:when test="${requestScope.inserimentoEseguito eq 'NoInserimenti' }">
							Non ci sono mandanti di raggruppamenti temporanei da aggiungere ai destinatari della comunicazione.<br><br>
							<c:if test="${!empty requestScope.msg }">
							Le seguenti ditte non sono state inserite tra i destinatari perchè non hanno un indirizzo PEC <c:if test="${not requestScope.mailInCaricoDocumentale }">o E-mail</c:if> specificato in anagrafica:
							${requestScope.msg }
							</c:if>
						</c:when>
						<c:when test="${requestScope.inserimentoEseguito eq 'noRT' }">
							Non ci sono raggruppamenti temporanei tra i destinatari della comunicazione.<br><br>
						</c:when>
						<c:when test="${requestScope.inserimentoEseguito eq 'noDestinatari' }">
							Non ci sono raggruppamenti temporanei tra i destinatari della comunicazione.<br><br>
						</c:when>
						<c:otherwise>
							Operazione conclusa.<br><br>
							<c:if test="${!empty requestScope.msg }">
							Le seguenti ditte non sono state inserite tra i destinatari perchè non hanno un indirizzo PEC <c:if test="${not requestScope.mailInCaricoDocumentale }">o E-mail</c:if> specificato in anagrafica:
							${requestScope.msg }
							</c:if>
						</c:otherwise>
					</c:choose>	
				</td>
			</tr>
			<tr>
				<td>
					<gene:formScheda entita="W_INVCOMDES"  gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_INVCOMDESAggiungiDesMandantiRT">
						
						<input type="hidden" name="idprg" id="idprg" value="${idprg}">
						<input type="hidden" name="idcom" id="idcom" value="${idcom }">
						<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}">
						<input type="hidden" name="integrazioneWSDM" id="integrazioneWSDM" value="${integrazioneWSDM }">
					</gene:formScheda>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<c:choose>
					<c:when test="${empty requestScope.inserimentoEseguito }">
						<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:schedaConferma();">
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:chiudi();">&nbsp;&nbsp;
					</c:when>
					<c:otherwise>
						<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;&nbsp;
					</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</table>			
			
	</gene:redefineInsert>

	<gene:javaScript> 
		
		
		document.forms[0].jspPathTo.value="geneweb/w_invcomdes/w_invcomdes-aggiungi-destinatariMandantiRT-popup.jsp";
		
		
		function chiudi(){
			window.close();
		}
		
		<c:if test="${requestScope.inserimentoEseguito eq 'OK'  }">
			window.opener.aggiorna();
		</c:if>
		
	</gene:javaScript>

</gene:template>
