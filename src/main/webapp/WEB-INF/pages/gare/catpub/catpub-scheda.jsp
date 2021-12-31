<%
/*
 * Created on: 23-mar-2017
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 /* Dettaglio Pubblicazioni bando ed esito di gara */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="codtab" value="${gene:getValCampo(key,'CODTAB')}"/>
<c:set var="stringaControllo" value="SEZ.VIS.GARE.CATPUB-scheda.TABPUB"/>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="CATPUB-scheda">
	<%-- Settaggio delle stringhe utilizzate nel template --%>
	<gene:setString name="titoloMaschera" value="${gene:if(param.TIPO == 1,'Dettaglio pubblicazioni bando','Dettaglio pubblicazioni esito')}" />
	
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="CATPUB" gestisciProtezioni="true"  gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreCATPUB" >
			<gene:campoScheda>
				<!-- <td colspan="2"><b>Caratteristiche della gara<b></td> -->
				<td colspan="2"><b>Caratteristiche della gara<b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="CODTAB" visibile='false'/>
			<gene:campoScheda campo="TIPCLA" visibile='false' defaultValue="${param.TIPO}"/>
			<gene:campoScheda campo="TIPLAV" obbligatorio="true"/>
			<gene:campoScheda campo="TIPGAR" />
			<gene:campoScheda campo="LIMINF" >
				<gene:checkCampoScheda funzione='gestioneCampoLiminf("##")' obbligatorio="true" messaggio="L'importo minimo deve essere inferiore all'importo massimo." 
										onsubmit="true"/>
			</gene:campoScheda>
			<gene:campoScheda campo="LIMSUP" />
			<input type="hidden" id="TIPO" name="TIPO" value="${param.TIPO}"/>
			
			<c:if test='${modoAperturaScheda ne "NUOVO"}' >
				<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestionePubblicazioniFunction" parametro='${codtab}' />	
			</c:if>
	
			<gene:campoScheda visibile='${gene:checkProt(pageContext, stringaControllo) }' addTr="false">
				<tr id="rowTITOLO_PUBBLICAZIONI_CATEGORIA">
					<td colspan="2"><b>Pubblicazioni</b></td>
				</tr>
			</gene:campoScheda>			
		
			<gene:campoScheda campoFittizio="true" nome="dettaglioPUBBLICAZIONI">
				<td class="etichetta-dato"></td>
				<td>
					<table id="tabellaTabPub" class="griglia" style="width: 99%; margin-left: 1%;">					
			</gene:campoScheda>			
		
			<jsp:include page="/WEB-INF/pages/commons/interno-sezione-multipla-singola-riga.jsp" >
				<jsp:param name="entita" value='TABPUB'/>
				<jsp:param name="chiave" value='${codtab}'/>
				<jsp:param name="nomeAttributoLista" value='listaPubblicazioniBandoEsito' />
				<jsp:param name="idProtezioni" value="TABPUB" />
				<jsp:param name="sezioneListaVuota" value="true" />
				<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/catpub/pubblicazioniBandoEsito.jsp"/>
				<jsp:param name="arrayCampi" value="'TABPUB_CODTAB_', 'TABPUB_CODPUB_', 'TABPUB_TIPPUB_'"/>
				<jsp:param name="titoloSezione" value="<br>Pubblicazione" />
				<jsp:param name="titoloNuovaSezione" value="<br>Nuova pubblicazione" />
				<jsp:param name="descEntitaVociLink" value="pubblicazione" />
				<jsp:param name="msgRaggiuntoMax" value="e pubblicazioni"/>
				<jsp:param name="usaContatoreLista" value="true"/>
				<jsp:param name="numMaxDettagliInseribili" value="5"/>
				<jsp:param name="sezioneInseribile" value="true"/>
				<jsp:param name="sezioneEliminabile" value="true"/>
			</jsp:include>
			
			<gene:campoScheda addTr="false" visibile='${gene:checkProt(pageContext, stringaControllo) }'>
				</table>				
			</td>
			</tr>
			</gene:campoScheda>
			
			<gene:campoScheda>	
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
			
		</gene:formScheda>
		
		
	</gene:redefineInsert>
	
	<gene:javaScript>
		var schedaNuovoDefault = schedaNuovo;

		function schedaNuovoCustom(){
				document.forms[0].action += "&TIPO=" + getValue("CATPUB_TIPCLA");
		         schedaNuovoDefault();
		}

		var schedaNuovo = schedaNuovoCustom;
		
		
		$('#rowLinkAddTABPUB td:eq(1)').attr("colspan","4");
		$('#rowMsgLastTABPUB td:eq(1)').attr("colspan","4");
				
		$('table.griglia tr td.etichetta-dato').remove();
		$('table.griglia tr td.valore-dato').css('width','65%');
		
		function gestioneCampoLiminf(valore){
				var limsup = getValue("CATPUB_LIMSUP");
				if( (valore != '') && (limsup != '') ){
					var floatLiminf = parseFloat(valore);
					var floatLimsup = parseFloat(limsup);
					if(floatLiminf >= floatLimsup){
						return false;
					}
				}
				return true;
			}
		
		
	</gene:javaScript>
	
	
</gene:template>
