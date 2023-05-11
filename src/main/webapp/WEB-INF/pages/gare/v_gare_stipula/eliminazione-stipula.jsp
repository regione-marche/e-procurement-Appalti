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
		Finestra che visualizza la conferma di eliminazione della gara
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="popup-message-template.jsp">
	<c:set var="valoreChiave" value='${gene:getValCampo(param.chiaveRiga, "ID")}' />
	<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDatiStipulaFunction", pageContext, valoreChiave)}'/>
	<gene:setString name="titoloMaschera" value='Eliminazione stipula ${requestScope.codStipula}' />
	<c:set var="isStipulaCollegataSimog" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteStipulaCollegataSimogFunction", pageContext, valoreChiave )}'/>
	<c:set var="msgSimog" value='Non è possibile eliminare la stipula perchè è stata già creata la relativa anagrafica SIMOG per la richiesta CIG collegato' />

	<c:set var="modo" value="MODIFICA" scope="request" />
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="G1STIPULA" gestisciProtezioni="false" >
			<gene:campoScheda campo="ID" visibile="false" />
			<c:choose>
				<c:when test='${isStipulaCollegataSimog ne "true"}' >
				<gene:campoScheda>
					<td>
					<br>
						&nbsp;&nbsp;Confermi l'eliminazione?
					<br>
					<br>
					</td>
				</gene:campoScheda>
				</c:when>
				<c:otherwise>
				<gene:campoScheda>
					<td>
					<br>
						${msgSimog}
					<br>
					<br>
					</td>
			</gene:campoScheda>
				</c:otherwise>
			</c:choose>
		</gene:formScheda>
		
	<gene:redefineInsert name="buttons">
	<c:if test='${isStipulaCollegataSimog ne "true"}'>
		<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma()">&nbsp;
	</c:if>
		<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
	</gene:redefineInsert>

  </gene:redefineInsert>
	<gene:javaScript>

		function conferma(){
			chiaveRiga="${param.chiaveRiga}";
			opener.bloccaRichiesteServer();
			opener.confermaDelete();
			
		}

		function annulla(){
			window.close();
		}

	</gene:javaScript>
</gene:template>