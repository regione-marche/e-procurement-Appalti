<%
  /*
			 * Created on 07/05/2018
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<c:choose>
	<c:when test='${!empty ngara}'>
		<c:set var="ngara" value='${ngara}' />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${param.ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty genere}'>
		<c:set var="genere" value='${genere}' />
	</c:when>
	<c:otherwise>
		<c:set var="genere" value="${param.genere}" />
	</c:otherwise>
</c:choose>

<div style="width:97%;">

<gene:template file="popup-message-template.jsp">
	<c:set var="modo" value="NUOVO" scope="request" />
		
	<gene:setString name="titoloMaschera" value="Richiesta verifica requisiti operatori art.80 DLgs.50/2016" />

	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupVerificaOperatoriArt80">
			<gene:campoScheda>
			<td colSpan="2">
			<c:choose>
				<c:when test="${!empty esito and esito eq 'NoOperatori'}">
					<br>
					Non sono stati trovati operatori su cui eseguire la verifica.
					<br>
		  			<br>
				</c:when>
				<c:when test="${!empty esito and esito eq 'OK'}">
					<br>
					Operazione completata.<br><br>
					Numero operatori per cui è stata richiesta la verifica: ${numOperatoriRichiesta}<br>
					
					<c:if test="${numVerificheGiaInviate > 0 }">
						(di cui operatori per i quali la richiesta è risultata già fatta in precedenza: ${numVerificheGiaInviate })<br>
					</c:if>
					<c:if test="${numVerificheErrore > 0 }">
						Numero operatori per cui la richiesta di verifica ha dato errore: ${numVerificheErrore }<br>
					</c:if>
					<br>
		  			<br>
				</c:when>
				<c:otherwise>
					<br>
					<c:choose>
						<c:when test="${genere eq 10 }">
							<c:set var="msgGenere" value="elenco"/>
						</c:when>
						<c:otherwise>
							<c:set var="msgGenere" value="catalogo"/>
						</c:otherwise>
					</c:choose>
					Mediante questa funzione viene richiesta la verifica dei requisiti di cui all'art.80 del DLgs.50/2016 per gli operatori in ${msgGenere } che sono nello stato abilitazione indicato sotto.<br><br>
					Confermi l'operazione?
					<br><br>
				</c:otherwise>
			</c:choose>
			</td>
			</gene:campoScheda>
			<gene:campoScheda campo="ABILITAZ" title="Stato abilitazione" campoFittizio="true" definizione="T100;;A1075;;ABILITAZ" obbligatorio="true" visibile="${empty esito }" defaultValue="6"/>
			<input type="hidden" name="ngara" id="ngara" value="${ngara }"/>
			<input type="hidden" name="genere" id="genere" value="${genere }"/>
		</gene:formScheda>
	  	</gene:redefineInsert>
<c:if test='${not empty requestScope.esito}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
	</gene:redefineInsert>
</c:if>	
	<gene:javaScript>

		<c:if test='${not empty requestScope.esito and requestScope.esito eq "OK" and numOperatoriRichiesta >0}' >
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
		</c:if>
	
		function annulla(){
			window.close();
		}
		
		
		function conferma() {
			document.forms[0].jspPathTo.value="gare/gare/popupVerificaOperatoriArt80.jsp";
			schedaConferma();
		}
		
	</gene:javaScript>
</gene:template>
</div>

