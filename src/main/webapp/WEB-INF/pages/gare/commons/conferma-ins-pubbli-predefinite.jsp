<%
/*
 * Created on: 17-dic-2008
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
		Finestra che visualizza la conferma per l'inserimento delle pubblicazioni bando/esito predefinite
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.pubblicazioniInserite and requestScope.pubblicazioniInserite eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">


<c:choose>
	<c:when test="${param.bando eq 1}">
		<gene:setString name="titoloMaschera" value='Inserimento pubblicazioni bando predefinite' />
	</c:when>
	<c:otherwise>
		<gene:setString name="titoloMaschera" value='Inserimento pubblicazioni esito predefinite' />
	</c:otherwise>
</c:choose>
	
<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="TORN" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInsDatiPubblicazioni" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreInsertPubblicazioniPredefinite">
	
		<gene:campoScheda>
			<td>&nbsp;&nbsp;</td>
			<td>
		<c:choose>
			<c:when test="${param.bando eq 1}">
				<c:if test="${requestScope.occTrovate}">
					<br>
					Confermi l'inserimento delle pubblicazioni bando predefinite per la gara?<br>
					<br>
				</c:if>
				<c:if test="${!requestScope.occTrovate}">
					<br>
					<b>Attenzione:</b> non ci sono pubblicazioni bando predefinite per la gara<br>
					<gene:redefineInsert name="buttons">
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
					</gene:redefineInsert>
					<br>
				</c:if>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${requestScope.errors}">
						<br>Attenzione: non sono stati definiti i lotti della gara<br>
						<br>
						<gene:redefineInsert name="buttons">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
						</gene:redefineInsert>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test='${!empty requestScope.ngara}'>
								<c:set var="ngara" value="${requestScope.ngara}" />
							</c:when>
							<c:otherwise>
								<c:set var="ngara" value="${param.ngara}" />
							</c:otherwise>
						</c:choose>	
						<c:if test="${requestScope.occTrovate}">
							<br>
							Selezionare il criterio con cui inserire le pubblicazioni:<br>
							<input type="radio" name="chkPubEsito" value="1" onclick="javascript:setValue('SRC_ESITO', 1)" checked="checked" />pubblicazioni esito predefinite per la gara<br>
							<input type="radio" name="chkPubEsito" value="2" onclick="javascript:setValue('SRC_ESITO', 2)" />pubblicazioni del bando di gara<br>
							<br>
						</c:if>
						<c:if test="${!requestScope.occTrovate}">
						<br>
							<b>Attenzione:</b> non ci sono pubblicazioni esito predefinite per la gara<br>
							<br>
							Confermi l'inserimento delle pubblicazioni esito a partire dalle pubblicazioni del bando di gara?<br>
							<br>
						</c:if>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
			</td>
		</gene:campoScheda>

		<gene:campoScheda campo="CODGAR" campoFittizio="true" defaultValue="${param.codgar}" visibile="false" definizione="T21;0"/>
		<gene:campoScheda campo="NGARA" campoFittizio="true" defaultValue="${ngara}" visibile="false" definizione="T20;0"/>
		<gene:campoScheda campo="GENERE" campoFittizio="true" defaultValue="${param.genere}" visibile="false" definizione="T20;0"/>
		<gene:campoScheda campo="BANDO" campoFittizio="true" defaultValue="${param.bando}" visibile="false" definizione="N1;0"/>
		<gene:campoScheda campo="SRC_ESITO" campoFittizio="true" defaultValue="1" visibile="false" definizione="N1;0"/>
	</gene:formScheda>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/commons/conferma-ins-pubbli-predefinite.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
	
	<c:if test="${param.bando ne 1 && !requestScope.occTrovate}">
		setValue('SRC_ESITO', 2);
	</c:if>

	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>