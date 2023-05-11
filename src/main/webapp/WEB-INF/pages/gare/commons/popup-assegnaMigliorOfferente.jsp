<%
/*
 * Created on: 05-03-2015
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
		Finestra per la rettifica dei termini
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.assegnamentoEseguito and requestScope.assegnamentoEseguito eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${contextPath}/js/date.js"></script> 
</gene:redefineInsert>

<c:set var="modo" value="NUOVO" scope="request" />

	
	<c:choose>
		<c:when test='${!empty param.ngara}'>
			<c:set var="ngara" value="${param.ngara}" />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${ngara}" />
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value='Assegna a miglior offerente su tutte le voci' />
		
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GCAP" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestorePopupAssegnaMigliorOfferente" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAssegnaMigliorOfferente">
		<gene:campoScheda campo="NGARA" visibile="false"/>
		<gene:campoScheda>
			<td colSpan="2">
				<br>
				<c:choose>
					<c:when test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO"}' >
						${requestScope.msg }
					</c:when>
					<c:otherwise>
						Alla conferma tale funzione provvedera' ad assegnare il prodotto al fornitore che ha presentato il prezzo piu' basso.
						</br>
						A parita' di prezzo offerto, il prodotto verra' assegnato al fornitore con la data di consegna piu' vicina.
						</br>
						In caso di ulteriore parita' il prodotto non verra' assegnato, richiedendo l'intervento manuale dell'utente.
						<c:if test='${not empty requestScope.msgNonAssegnati}'>
							</br>
							</br>
							<b>${requestScope.msgNonAssegnati}</b>
						</c:if>
					</c:otherwise>
				</c:choose>
				<br>&nbsp;
			</td>
		</gene:campoScheda>
		<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
	</gene:redefineInsert>
</c:if>
	
	
	<gene:javaScript>
			
		function conferma() {
			var controlliSuperati=false;
			
			
			document.forms[0].jspPathTo.value="gare/commons/popup-assegnaMigliorOfferente.jsp";
			schedaConferma();
			
		}
		
		
		function annulla(){
			window.close();
		}
		
	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>