<%
/*
 * Created on: 10-nov-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 *
 * Popup per campi ulteriori relativi alla ditta presenta nella lista delle
 * fasi di ricezione in analisi
 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>


<c:set var="idprg" value="${param.idprg}"/>
<c:set var="idcom" value="${param.idcom}"/>
	<c:if test='${modo eq "NUOVO"}'>
		<c:set var="comintest" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetCOMINTESTFunction",pageContext,idprg,idcom)}' />
	</c:if>
<div style="width:97%;">
<gene:template file="popup-template.jsp">
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="corpo">
		<gene:setString name="titoloMaschera" value='Aggiungi destinatario' />
		<gene:formScheda entita="W_INVCOMDES" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_INVCOMDES">
			<gene:campoScheda campo="IDPRG" visibile="false" defaultValue="${param.idprg}"/>
			<gene:campoScheda campo="IDCOM" visibile="false" defaultValue="${param.idcom}"/>
			<gene:campoScheda campo="IDCOMDES" visibile="false"/>
			<c:choose>
				<c:when test="${comintest eq '1'}">
						<gene:campoScheda campo="DESINTEST" visibile="true" defaultValue="Ditta "/>
				</c:when>
				<c:otherwise>
						<gene:campoScheda campo="DESINTEST" visibile="false"/>
				</c:otherwise>
			</c:choose>
			<gene:campoScheda campo="COMTIPMA" defaultValue="2" obbligatorio="true" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoTipoIndirizzo"/>
			<gene:campoScheda campo="DESMAIL"/>
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:aggiungi();">&nbsp;
																																														
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:window.close();">&nbsp;
				</td>
			</gene:campoScheda>
			
			<input type="hidden" name="idconfi" value="${idconfi}" />
			
		</gene:formScheda>
	</gene:redefineInsert>
	
	
	<gene:javaScript>

		function aggiungi(){

			document.forms[0].jspPathTo.value = document.forms[0].jspPath.value;
			
			schedaConferma();
		}
		
	<c:if test='${modo eq "VISUALIZZA"}'>
		var openerActivePage = window.opener.document.forms[0].activePage.value;
		
		if(openerActivePage != null)
			window.opener.listaVaiAPagina(0);
		else
			window.opener.listaVaiAPagina(openerActivePage);
		window.close();
	</c:if>
</gene:javaScript>
</gene:template>
</div>
	