<%
/*
 * Created on: 10-04-20210
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
		
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.documentiInseriti and requestScope.documentiInseriti eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value='Inserisci documenti da delibera a contrarre' />

	
<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="TORN" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInsertDocumentiGruppo15" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreInsertDocumentiGruppo15">
	
	<gene:campoScheda>
		<td>&nbsp;&nbsp;</td>
		<td>
	
			<c:if test="${!empty requestScope.documentiPresenti}">
				<br>
				Confermi la copia dei documenti della delibera a contrarre nella sezione corrente?<br>
				<br>
			</c:if>
			<c:if test="${empty requestScope.documentiPresenti}">
				<br>
				<b>Attenzione:</b> non sono specificati i documenti della delibera a contrarre per la gara.<br>
				<gene:redefineInsert name="buttons">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
				</gene:redefineInsert>
				<br>
			</c:if>
			
		</td>
	</gene:campoScheda>

		<gene:campoScheda campo="CODGAR" campoFittizio="true" defaultValue="${param.codgar}" visibile="false" definizione="T21;0"/>
		<gene:campoScheda campo="NGARA" campoFittizio="true" defaultValue="${param.ngara}" visibile="false" definizione="T21;0"/>
		<gene:campoScheda campo="TIPOLOGIA" campoFittizio="true" defaultValue="${param.tipologia}" visibile="false" definizione="N2;0"/>
				
	</gene:formScheda>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/documgara/conferma-ins-doc-gruppo15.jsp";
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