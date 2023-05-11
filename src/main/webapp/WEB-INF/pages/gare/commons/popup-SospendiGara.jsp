<%
/*
 * Created on: 11-10-2012
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
		Finestra per la valorizzazione del campo ESINEG 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.esito and requestScope.esito eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:choose>
	<c:when test="${(param.opz eq 1)}">

	<c:set var="isInvitoPubblicato" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,param.codgar,"BANDO13","false")}' />
	
	<c:choose>
		<c:when test="${(param.iterga eq 2 || param.iterga eq 4 || param.iterga eq 7 || param.iterga eq 8) && isInvitoPubblicato eq 'FALSE'}">
			<c:set var="isSuperataDataTerminePresentazione" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsSuperataDataTerminePresentazioneFunction", pageContext, param.codgar, "2")}' />
		</c:when>
		<c:otherwise>
			<c:set var="isSuperataDataTerminePresentazione" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsSuperataDataTerminePresentazioneFunction", pageContext, param.codgar, "1")}' />
		</c:otherwise>
	</c:choose>

	<c:set var="msgConferma" value="Si desidera procedere con la sospensione della gara? <br><br>Indicare la motivazione qui sotto:"/>
	<c:set var="msgAvviso" value="sono stati superati i termini di gara."/>
	<gene:setString name="titoloMaschera" value='Sospensione gara' />
	<c:set var="msgBlocco" value="Non é possibile procedere perchè "/>
	
	<c:if test="${isSuperataDataTerminePresentazione=='true'}">
	<gene:redefineInsert name="buttons">
		<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
	</gene:redefineInsert>
	</c:if>			

	<c:set var="modo" value="NUOVO" scope="request" />
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARSOSPE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupSospendiGara">
	
		<gene:campoScheda>
			<td colSpan="2">
				<c:choose>
					<c:when test="${isSuperataDataTerminePresentazione=='true'}">
						<br>
						${msgBlocco} ${msgAvviso}<br>
						<br>
					</c:when>					
					<c:otherwise>
						<br>
						${msgConferma}<br>
						<br>
					</c:otherwise>
				</c:choose>
			</td>
		</gene:campoScheda>

		<c:if test="${isSuperataDataTerminePresentazione!='true'}">
			<gene:campoScheda campo="CODGARA" campoFittizio="true" defaultValue="${param.codgar}"  visibile="false" definizione="T21;0"/>
			<gene:campoScheda campo="OPZ" campoFittizio="true" defaultValue="${param.opz}"  visibile="false" definizione="T2;0"/>
			<gene:campoScheda campo="NOTE" title="Motivazione" campoFittizio="true" obbligatorio="true" definizione="T2000;0;;NOTE;G1NOTESOS"/>
			<gene:campoScheda>
				<td colSpan="2"><br></td>
			</gene:campoScheda>
		</c:if>
	</gene:formScheda>
	</gene:redefineInsert>
	
	</c:when>
	<c:otherwise>
		<c:set var="msgConferma" value="Si desidera procedere con la riattivazione della gara sospesa?"/>
		<gene:setString name="titoloMaschera" value='Riattivazione gara sospesa' />
		
		<c:set var="modo" value="NUOVO" scope="request" />
		<gene:redefineInsert name="corpo">
		<gene:formScheda entita="GARSOSPE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupSospendiGara">
		
		<gene:campoScheda>
				<c:choose>
					<c:when test="${isSuperataDataTerminePresentazione=='true'}">
						<br>
						${msgBlocco} ${msgAvviso}<br>
						<br>
					</c:when>					
					<c:otherwise>
						<br>
						${msgConferma}<br>
						<br>
					</c:otherwise>
				</c:choose>
		</gene:campoScheda>
		
		<gene:campoScheda campo="CODGARA" campoFittizio="true" defaultValue="${param.codgar}"  visibile="false" definizione="T21;0"/>
		<gene:campoScheda campo="OPZ" campoFittizio="true" defaultValue="${param.opz}"  visibile="false" definizione="T2;0"/>
		<gene:campoScheda campo="NOTE" title="Motivazione" campoFittizio="true" visibile="false" definizione="T2000;0;;NOTE;G1NOTESOS"/>
		<gene:campoScheda>
			<td colSpan="2"><br></td>
		</gene:campoScheda>
		</gene:formScheda>
		</gene:redefineInsert>
	</c:otherwise>
</c:choose>

	<gene:javaScript>
		function conferma() {
			
			document.forms[0].jspPathTo.value="gare/commons/popup-SospendiGara.jsp";
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
