<%
/*
 * Created on: 18-06-2014
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
	
		<c:choose>
			<c:when test='${not empty param.codiceGara}'>
				<c:set var="codiceGara" value="${param.codiceGara}" />
			</c:when>
			<c:otherwise>
				<c:set var="codiceGara" value="${codiceGara}" />
			</c:otherwise>
		</c:choose>
				
		<c:choose>
			<c:when test='${not empty param.isGaraLottiConOffertaUnica}'>
				<c:set var="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
			</c:when>
			<c:otherwise>
				<c:set var="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.bloccoModifica}'>
				<c:set var="messaggioControllo" value="${param.messaggioControllo}" />
		</c:when>
			<c:otherwise>
				<c:set var="messaggioControllo" value="${messaggioControllo}" />
			</c:otherwise>
		</c:choose>
		
		
		<gene:redefineInsert name="addHistory" />
		<gene:redefineInsert name="gestioneHistory" />
		<gene:setString name="titoloMaschera" value='Evadi ids associati' />
		<c:set var="esistonoIdsDaEvadere" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoIdsDaEvadereFunction", pageContext, codiceGara )}' />

		<gene:redefineInsert name="corpo">
			<table class="dettaglio-notab">
				<td class="valore-dato" colspan="2">
					<c:choose>
						<c:when test="${esistonoIdsDaEvadere eq 'true'}">
							<br>
							Mediante questa funzione &egrave; possibile evadere in maniera massiva gli ids della gara ancora inevasi.
							<br>
							<br>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${messaggioControllo eq 'ESECUZIONE_OK'}">
								<br>
								<b>Gli ids associati alla gara sono stati evasi.</b> 
								<br>
								<br>
								</c:when>
								<c:otherwise>
								<br>
								Non risultano ids della gara da evadere 
								<br>
								<br>
								</c:otherwise>
								</c:choose>
						
						</c:otherwise>
					</c:choose>
				</td>
	
				<tr>
					<td colspan="2" class="comandi-dettaglio">
					<c:choose>
						 <c:when test="${esistonoIdsDaEvadere eq 'true' && empty messaggioControllo }">
							<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:evadi('${codiceGara}');">
							<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
			        	</c:when>
			    	    <c:otherwise>
							<INPUT type="button" class="bottone-azione" value="Chiudi"	title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;			    	    
	    		    	</c:otherwise>
					</c:choose>
					</td>
				</tr>
			</table>
		</gene:redefineInsert>
		
		<gene:javaScript>
			
			function annulla(){
				window.close();
			}
			
			function evadi(codiceGara){
				bloccaRichiesteServer();
				var action = "${pageContext.request.contextPath}/pg/EvadiIdsAssociati.do?"+csrfToken+"&codiceGara=" + codiceGara;
				document.location.href=action;
			}
		
		</gene:javaScript>	
	</gene:template>

</div>


