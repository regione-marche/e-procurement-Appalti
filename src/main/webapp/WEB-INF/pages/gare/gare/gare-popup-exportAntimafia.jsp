<%
/*
 * Created on: 04/06/2010
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
		Finestra pop-up per fare l'export in formato XML delle ditte per gli accertamenti antimafia
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div style="width:97%;">

<gene:template file="popup-message-template.jsp">
	
	<c:set var="ngara" value='${param.codiceGara}' />
	<c:set var="tipoRichiesta" value='${param.tipoRichiesta}' />
	<c:set var="islottoGara" value='${param.islottoGara}' />
	
	<c:choose>
		<c:when test='${tipoRichiesta eq "VERIFICA"}'>
			<c:set var="dicituraGare" value=' alla gara ' />
			<c:if test = '${islottoGara eq "true"}'>
				<c:set var="dicituraGare" value=' al lotto di gara ' />
			</c:if>
			<gene:setString name="titoloMaschera" value="Esportazione ditte partecipanti alla gara per verifica interdizione" />
		</c:when>
		<c:when test='${tipoRichiesta eq "ACCERTAMENTO"}'>
			<c:set var="dicituraGare" value=' della gara ' />
			<c:if test = '${islottoGara eq "true"}'>
				<c:set var="dicituraGare" value=' del lotto di gara ' />
			</c:if>
			<gene:setString name="titoloMaschera" value="Esportazione ditta aggiudicataria provvisoria per accertamento antimafia" />
		</c:when>
	</c:choose>
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="corpo">
		<br>&nbsp;
		<c:choose>
			<c:when test='${tipoRichiesta eq "VERIFICA"}'>
				<br>Confermi l'esportazione dell'elenco delle ditte partecipanti ${dicituraGare} ${ngara} per la verifica interdizione?
			</c:when>
			<c:when test='${tipoRichiesta eq "ACCERTAMENTO"}'>
				<br>Confermi l'esportazione dei dati anagrafici della ditta aggiudicataria provvisoria ${dicituraGare} ${ngara} per l'accertamento antimafia?
			</c:when>
		</c:choose>
		<br>&nbsp;
		<br>&nbsp;
  	</gene:redefineInsert>
	<gene:redefineInsert name="buttons">
		<INPUT type="button" class="bottone-azione" value="Esporta" title="Esporta" onclick="javascript:esporta()">&nbsp;
		<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi()">&nbsp;
	</gene:redefineInsert>
  	
  	<gene:javaScript>

  	function esporta(){
			document.location.href = "${pageContext.request.contextPath}/pg/EsportaAntimafia.do?"+csrfToken+"&codiceGara=${ngara}&tipoRichiesta=${tipoRichiesta}&islottoGara=${islottoGara}";
		}

		function chiudi(){
			window.close();
		}
		
 	</gene:javaScript>
</gene:template>

</div>
