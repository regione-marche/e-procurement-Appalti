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
<c:choose>
	<c:when test='${fn:containsIgnoreCase(param.chiaveRiga, "CODGAR")}'>
		<c:set var="valoreChiave" value='${gene:getValCampo(param.chiaveRiga, "CODGAR")}' />
		<c:choose>
			<c:when test='${fn:startsWith(valoreChiave, "$") and empty param.genere}' >
				<gene:setString name="titoloMaschera" value='Eliminazione gara a lotto unico ${fn:substringAfter(valoreChiave, "$")}' />
			</c:when>
			<c:when test='${fn:startsWith(valoreChiave, "$") and param.genere eq 10}' >
				<gene:setString name="titoloMaschera" value='Eliminazione elenco ${fn:substringAfter(valoreChiave, "$")}' />
			</c:when>
			<c:when test='${fn:startsWith(valoreChiave, "$") and param.genere eq 20}' >
				<gene:setString name="titoloMaschera" value='Eliminazione catalogo ${fn:substringAfter(valoreChiave, "$")}' />
			</c:when>
			<c:otherwise>
				<gene:setString name="titoloMaschera" value='Eliminazione gara divisa in lotti ${valoreChiave}' />
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:when test='${fn:containsIgnoreCase(param.chiaveRiga, "NGARA") and param.genere eq 11 }'>
		<gene:setString name="titoloMaschera" value='Eliminazione avviso ${gene:getValCampo(param.chiaveRiga,"NGARA")}' />
	</c:when>
	<c:otherwise>
		<gene:setString name="titoloMaschera" value='Eliminazione lotto ${gene:getValCampo(param.chiaveRiga,"NGARA")}' />
	</c:otherwise>
</c:choose>

	<c:set var="modo" value="MODIFICA" scope="request" />
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="TORN" gestisciProtezioni="false" >
			<gene:campoScheda campo="CODGAR" visibile="false" />
			<gene:campoScheda>
				<td>
				<br>
					&nbsp;&nbsp;Confermi l'eliminazione?
				<br>
				<br>
				</td>
			</gene:campoScheda>
		</gene:formScheda>

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