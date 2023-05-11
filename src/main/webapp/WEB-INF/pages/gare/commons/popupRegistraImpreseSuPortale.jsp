<%
/*
 * Created on: 29-04-2012
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
		Finestra per l'attivazione della funzione 'Registra imprese sul portale'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<div style="width: 97%;"><gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:setString name="titoloMaschera" value='Registra imprese sul portale' />

	<gene:redefineInsert name="corpo">
	
		<form action="${contextPath}/pg/RegistraImpreseSuPortale.do" method="post" name="formRegistraImprese" >
			<input type="hidden" name="ngara" value="${param.ngara}" />
			<c:set var="esistonoImprese" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.ControlliImpreseRegistrateElencoFunction", pageContext, param.ngara)}'/>
			
						
			<table class="dettaglio-notab">
				<tr>
					<td class="valore-dato" colspan="2">
						<br>
						${msg }
						<br>
						<br>
						<br>
					</td>
				</tr>
								
				<tr>
					<td colspan="2" class="comandi-dettaglio">
						<c:if test='${esistonoImprese eq "SI" }'>
							<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma"	onclick="javascript:conferma();">
						</c:if>
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</form>	
	</gene:redefineInsert>

	<gene:javaScript>
		
		function annulla(){
			window.close();
		}
		
		
		function conferma() {
			document.formRegistraImprese.submit();
			bloccaRichiesteServer();
		}
		
		
		
		
		
		
	</gene:javaScript>
</gene:template></div>
