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
	
	<c:set var="art80statuslist" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "art80.statuslist")}'/>
	<c:set var="art80monitoring" value="true" />
	<c:set var="art80one_shot" value="true" />

	
	<c:if test="${!empty art80statuslist}">
		<c:choose>
			<c:when test="${fn:contains(art80statuslist,'monitoring')}">
				<c:set var="art80monitoring" value="true" />
			</c:when>
			<c:otherwise>
				<c:set var="art80monitoring" value="false" />
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${fn:contains(art80statuslist,'one_shot')}">
				<c:set var="art80one_shot" value="true" />
			</c:when>
			<c:otherwise>
				<c:set var="art80one_shot" value="false" />
			</c:otherwise>
		</c:choose>
	</c:if>
		
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
					Mediante questa funzione viene richiesta la verifica dei requisiti di cui all'art.80 del DLgs.50/2016 per gli operatori in ${msgGenere } che sono nello stato abilitazione indicato sotto.
					<br>Vengono considerati solo gli operatori per cui non è mai stata fatta alcuna richiesta.
					<br>Specificare la tipologia di controllo da richiedere.<br><br>
					Confermi l'operazione?
					<br><br>
				</c:otherwise>
			</c:choose>
			</td>
			</gene:campoScheda>
			<gene:campoScheda campo="ABILITAZ" title="Stato abilitazione operatore" campoFittizio="true" definizione="T100;;A1075;;ABILITAZ" obbligatorio="true" visibile="${empty esito }" defaultValue="6"/>
			<gene:campoScheda visibile="${empty esito }">
				<td class="etichetta-dato">Tipologia di controllo (*)</td>
				<td class="valore-dato" >
					<select name="status_service" id="status_service" >
						<option value="" title="" >&nbsp;</option>
						<c:if test="${art80one_shot eq 'true'}">
							<option value="one_shot" title="One shot" <c:if test="${status_service eq 'one_shot' }">selected="selected"</c:if>>One shot</option>
						</c:if>
						<c:if test="${art80monitoring eq 'true'}">
							<option value="monitoring" title="Monitoraggio" <c:if test="${status_service eq 'monitoring' }">selected="selected"</c:if>>Monitoraggio</option>
						</c:if>
					</select>
				</td>
			</gene:campoScheda>
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
			clearMsg();
			var status_service = $("#status_service").val();
			if(status_service==null || status_service == ""){
				outMsg('Il campo "Tipologia di controllo" è obbligatorio', "ERR");
				onOffMsg();
				return;
			}
			document.forms[0].jspPathTo.value="gare/gare/popupVerificaOperatoriArt80.jsp";
			schedaConferma();
		}
		
	</gene:javaScript>
</gene:template>
</div>

