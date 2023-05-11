
<%
/*
 * Created on: 08-ott-2008
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
		Finestra che visualizza il click su imposta prodotto non assegnato
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<c:choose>
	<c:when
		test='${not empty requestScope.esito and requestScope.esito eq "1"}'>
		<script type="text/javascript">
			opener.historyReload();
			window.close();
		</script>
	</c:when>
	<c:otherwise>

		<gene:template file="popup-message-template.jsp">

			<c:set var="ngara" value='${param.numeroGara}' />
			<c:set var="contaf" value='${param.contaf}' />
			<c:set var="isprodneg" value='${param.isprodneg}' />
			<c:set var="msgDescrizione" value="Mediante questa funzione &egrave possibile contrassegnare la lavorazione come non assegnata ad alcun offerente." />

			<gene:setString name="titoloMaschera"
				value='Imposta prodotto non assegnato' />

			<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsProdottoAssegnato", pageContext, ngara,contaf)}' />

			<c:choose>
				<c:when test='${isAssegnato eq "true"}'>
					<c:set var="msg" value="Non &egrave possibile procedere perch&egrave &egrave stato gi&agrave assegnato il prodotto. Accedere alla funzione ''Assegna all'offerente'' e deselezionare l'offerente" />
				</c:when>
				<c:when test='${isprodneg eq "1"}'>
					<c:set var="msg" value="Non &egrave possibile procedere perch&egrave la lavorazione risulta gi&agrave ''non assegnata''" />
				</c:when>
				<c:otherwise>
					<c:set var="msg" value="Confermi l'operazione?" />
				</c:otherwise>
			</c:choose>


			<c:set var="modo" value="NUOVO" scope="request" />
			<gene:redefineInsert name="corpo">
				<gene:formScheda entita="GCAP" gestisciProtezioni="false"
					gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupImpostaProdottoNonAssegnato">
					
					<gene:campoScheda>
						<td>
							<br>${msgDescrizione}<br>
							<br>${msg}<br><br>
						</td>
					</gene:campoScheda>
				
					<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
					<input type="hidden" name="contaf" id="contaf" value="${contaf}" />

				</gene:formScheda>

				<gene:redefineInsert name="buttons">
					<c:if test='${isAssegnato ne "true" && isprodneg ne "1"}'>
						<INPUT type="button" class="bottone-azione" value="Conferma"
							title="Conferma" onclick="javascript:conferma()">&nbsp;
			</c:if>
					<INPUT type="button" class="bottone-azione" value="Annulla"
						title="Annulla" onclick="javascript:annulla()">&nbsp;
			</gene:redefineInsert>


			</gene:redefineInsert>
			<gene:javaScript>

				function conferma() {
					document.forms[0].jspPathTo.value="gare/v_gcap_dpre/popupImpostaProdottoNonAssegnato.jsp";
					schedaConferma();
				}
		
				function annulla(){
					window.close();
				}
		
			</gene:javaScript>
		</gene:template>
	</c:otherwise>
</c:choose>