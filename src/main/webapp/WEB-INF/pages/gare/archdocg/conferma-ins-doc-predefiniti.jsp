<%
/*
 * Created on: 26-08-2010
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
		Finestra che visualizza la conferma per l'inserimento della documentazione predefinita
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
<gene:setString name="titoloMaschera" value='Inserimento documenti predefiniti' />

	
<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="TORN" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInsDatiDocumenti" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreInsertDocumentiPredefiniti">
	
	<gene:campoScheda>
		<td>&nbsp;&nbsp;</td>
		<td>
	
			<c:if test="${!empty requestScope.documentiPresenti}">
				<br>
				Confermi l'inserimento dei documenti predefiniti per la sezione corrente?<br>
				<br>
			</c:if>
			<c:if test="${empty requestScope.documentiPresenti}">
				<br>
				<b>Attenzione:</b> non ci sono documenti predefiniti per la gara relativi alla sezione corrente.<br>
				<gene:redefineInsert name="buttons">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
				</gene:redefineInsert>
				<br>
			</c:if>
			
		</td>
	</gene:campoScheda>

		<gene:campoScheda campo="CODGAR" campoFittizio="true" defaultValue="${param.codgar}" visibile="false" definizione="T21;0"/>
		<gene:campoScheda campo="NGARA" campoFittizio="true" defaultValue="${param.ngara}" visibile="false" definizione="T20;0"/>
		<gene:campoScheda campo="TIPLAV" campoFittizio="true" defaultValue="${param.tiplav}" visibile="false" definizione="T2;0"/>
		<gene:campoScheda campo="TIPGARG" campoFittizio="true" defaultValue="${param.tipgarg}" visibile="false" definizione="T2;0"/>
		<gene:campoScheda campo="LOTTODIGARA" campoFittizio="true" defaultValue="${param.lottoDiGara}" visibile="false" definizione="T1;0"/>
		<gene:campoScheda campo="ISOFFERTAUNICA" campoFittizio="true" defaultValue="${param.isOffertaUnica}" visibile="false" definizione="T1;0"/>
		<gene:campoScheda campo="IMPORTO" campoFittizio="true" defaultValue="${param.importo}" visibile="false" definizione="F24.5;0"/>
		<gene:campoScheda campo="CRITLIC" campoFittizio="true" defaultValue="${param.critlic}" visibile="false" definizione="N7;0"/>
		<gene:campoScheda campo="FASEINVITO" campoFittizio="true" defaultValue="${param.faseInvito}" visibile="false" definizione="T1;0"/>
		<gene:campoScheda campo="TIPOLOGIA" campoFittizio="true" defaultValue="${param.tipologia}" visibile="false" definizione="N7;0"/>
		<gene:campoScheda campo="BUSTA" campoFittizio="true" defaultValue="${param.busta}" visibile="false" definizione="N7;0"/>
		<gene:campoScheda campo="GRUPPO" campoFittizio="true" defaultValue="${param.gruppo}" visibile="false" definizione="N7;0"/>
		<gene:campoScheda campo="ISPROCEDURATELEMATICA" campoFittizio="true" defaultValue="${param.isProceduraTelematica}" visibile="false" definizione="T10;0"/>
		
	</gene:formScheda>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/archdocg/conferma-ins-doc-predefiniti.jsp";
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