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
	<c:when test="${param.isOffertaUnica eq 'Si' }">
		<c:set var="tipo" value="3"/>
	</c:when>
	<c:when test="${param.isOfferteDistinte eq 'Si' }">
		<c:set var="tipo" value="1"/>
	</c:when>
	<c:otherwise>
		<c:set var="tipo" value="2"/>
	</c:otherwise>
</c:choose>
<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetDatiNegazioneFunction",  pageContext,param.ngara,tipo)}' />	
<c:set var="codStatoGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetStatoGaraFunction", pageContext, param.codgar1)}' />	


<c:choose>
	<c:when test="${param.isLottoOffUnica eq 'Si'}">
		<c:set var="msgConferma" value="Selezionare il tipo di esito per cui il lotto viene concluso senza essere aggiudicato."/>
		<c:set var="msgAvviso" value="il lotto risulta aggiudicato."/>
		<gene:setString name="titoloMaschera" value='Impostazione lotto non aggiudicato' />
	</c:when>
	<c:otherwise>
		<c:set var="msgConferma" value="Selezionare il tipo di esito per cui la gara viene conclusa senza essere aggiudicata."/>
		<c:set var="msgAvviso" value="la gara risulta aggiudicata."/>
		<gene:setString name="titoloMaschera" value='Impostazione gara non aggiudicata' />
	</c:otherwise>
</c:choose>

<c:set var="msgBlocco" value="Non é possibile procedere perchè "/>

<c:if test="${aggiudicazione eq 'Si' or codStatoGara eq '4'}">
	<gene:redefineInsert name="buttons">
		<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
	</gene:redefineInsert>
</c:if>			


<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupImpostaGaraNonAggiudicata">
	
		<gene:campoScheda>
			<td colSpan="2">
				<c:choose>
					<c:when test="${codStatoGara eq '4'}">
						<br>
						${msgBlocco} la gara risulta sospesa.<br>
						<br>
					</c:when>					
					<c:otherwise>
						<c:if test="${aggiudicazione ne 'Si'}">
							<br>
							${msgConferma}<br>
							<br>
						</c:if>
						<c:choose>
							<c:when test="${param.isOfferteDistinte eq 'Si' || param.isOffertaUnica eq 'Si'}">
								<c:choose>
									<c:when test="${aggiudicazione ne 'Si' }">
										<b>ATTENZIONE:</b> l'esito impostato per la gara viene riportato in tutti i lotti.<br>
										<br>
										<c:if test="${esitoPresente eq 'Si'}">
											<b>ATTENZIONE:</b>&nbsp;
												ci sono dei lotti della gara conclusi con esito negativo.<br>
											<br>
										</c:if>
									</c:when>	
									<c:otherwise>
										<br>
										${msgBlocco} ci sono dei lotti della gara che risultano aggiudicati.<br>
										<br>
									</c:otherwise>
								</c:choose>
							</c:when>					
							<c:otherwise>
								<c:if test="${aggiudicazione eq 'Si' }">
									<br>
									${msgBlocco} ${msgAvviso}<br>
									<br>
								</c:if>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</td>
		</gene:campoScheda>

		<c:if test="${aggiudicazione ne 'Si' and codStatoGara ne '4'}">
			<gene:campoScheda campo="NGARA" campoFittizio="true" defaultValue="${param.ngara}"  visibile="false" definizione="T20;0"/>
			<gene:campoScheda campo="CODGARA" campoFittizio="true" defaultValue="${param.codgar1}"  visibile="false" definizione="T21;0"/>
			<gene:campoScheda campo="ESINEG" campoFittizio="true" defaultValue="${param.esineg}" title="Tipo di esito" definizione="T100;0;A1088;;G1ESINEGG"/>
			<gene:campoScheda campo="DATNEG" campoFittizio="true" defaultValue="${param.datneg}" title="Data" definizione="D;0;;DATA_ELDA;G1DATNEGG"/>
			<c:if test='${gene:checkProt(pageContext,"COLS.VIS.GARE.GARE1.NPANNREVAGG")}' >
				<gene:campoScheda campo="NPANNREVAGG" campoFittizio="true" defaultValue="${param.npannrevagg}" title="N.protocollo" definizione="T14;0;;;NPANREVAGG"/>
			</c:if>
			<gene:campoScheda campo="NOTNEG" campoFittizio="true" defaultValue="${initNotneg}" title="Note" definizione="T2000;0;;NOTE;G1NOTNEG"/>
			<gene:campoScheda campo="ISLOTTOOFFDISTINTE" campoFittizio="true" defaultValue="${param.isLottoOffDistinte}"  visibile="false" definizione="T2;0"/>
			<gene:campoScheda campo="ISOFFERTAUNICA" campoFittizio="true" defaultValue="${param.isOffertaUnica}"  visibile="false" definizione="T2;0"/>
			<gene:campoScheda campo="ISOFFERTEDISTINTE" campoFittizio="true" defaultValue="${param.isOfferteDistinte}"  visibile="false" definizione="T2;0"/>
			<gene:campoScheda campo="ISLOTTOOFFUNICA" campoFittizio="true" defaultValue="${param.isLottoOffUnica}"  visibile="false" definizione="T2;0"/>
			<gene:campoScheda>
				<td colSpan="2"><br></td>
			</gene:campoScheda>
			<gene:fnJavaScriptScheda funzione="sbiancaData('#ESINEG#')" elencocampi="ESINEG" esegui="false" />
		</c:if>	
	</gene:formScheda>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			var esineg=getValue("ESINEG");
			var datneg=getValue("DATNEG");
			if(esineg!=null && esineg!="" && (datneg==null || datneg=="")){
				alert("Valorizzare il campo 'Data'");
				return;
			}
			if(esineg==null || esineg==""){
				setValue("DATNEG","");
				setValue("NOTNEG","");
				setValue("NPANNREVAGG","");
			}
			
			document.forms[0].jspPathTo.value="gare/commons/popup-ImpostaGaraNonAggiudicata.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
		
		function sbiancaData(esineg){
			if(esineg==null || esineg==""){
				setValue("DATNEG","");
				setValue("NOTNEG","");
				setValue("NPANNREVAGG","");
			}
		}
	</gene:javaScript>
</gene:template>
</div>

</c:otherwise>
</c:choose>
