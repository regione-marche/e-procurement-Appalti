<%
/*
 * Created on 28-ott-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI
 // SELEZIONE DEL TIPO DI GARA (IN FASE DI CREAZIONE)
%>


<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<form action="" name="formRadioBut">
	<table class="dettaglio-notab">
		<tr>
		 	<td>
		 		<c:choose>
		 		
		 		<c:when test='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.GARTEL") && fn:contains(listaOpzioniDisponibili, "OP132#") && fn:contains(listaOpzioniDisponibili, "OP114#")}'>
		 			<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetDefaultProceduraTelematicaFunction" />
		 			<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetDefaultModalitaPresentazioneOffertaFunction" />
		 			
		 			<c:if test='${!gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.GARTEL")}'>
		 				<c:set var="proceduraTelematicadisabled" value='disabled="disabled"' />
		 			</c:if>
		 			
		 			<c:if test='${!gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.OFFTEL")}'>
		 				<c:set var="modalitaPresentazionedisabled" value='disabled="disabled"' />
		 			</c:if>
		 			
		 			<c:if test='${!gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.OFFTEL")}'>
		 				<c:set var="modalitaPresentazioneNascosta" value='style="display:none;"' />
		 			</c:if>
		 			
		 			<c:choose>
		 				<c:when test="${(defaultModalitaPresentazione eq '1'&& gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.garaTelematicaInserimentoImporti')) || (defaultModalitaPresentazione eq '2'&& !gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.garaTelematicaSoloUpload'))}">
		 					<c:set var="defaultmodalitaPresentazione1" value='checked="checked"' />
		 				</c:when>
		 				<c:when test="${(defaultModalitaPresentazione eq '2' && gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.garaTelematicaSoloUpload')) || (defaultModalitaPresentazione eq '1' && !gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.garaTelematicaInserimentoImporti'))}">
		 					<c:set var="defaultmodalitaPresentazione2" value='checked="checked"' />
		 				</c:when>
		 			</c:choose>
		 			
					<br>
					<b>Impostare la modalità di espletamento della procedura:</b>		 			
		 			<br>
		 			<c:choose>
		 				<c:when test="${defaultProceduraTelematica eq '1'}">
		 					<c:set var="lottidistintinascoto" value='style="display:none;"' />
		 					&nbsp;<input type="radio" name="proceduraTelematica" value="1" checked="checked" onclick="javascript:aggiornaTelematica(1);" ${proceduraTelematicadisabled}/>&nbsp;Telematica nella piattaforma 
							<br>
							<div id="presentazione" ${modalitaPresentazioneNascosta }>
							<br>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Compilazione su portale mediante:</b>
							<br>
							<c:if test="${gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.garaTelematicaInserimentoImporti')}">
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="modalitaPresentazione" value="1" ${defaultmodalitaPresentazione1 }  ${modalitaPresentazionedisabled }  />&nbsp;Inserimento importi o ribassi offerti e upload di documenti
							<br>
							</c:if>
							<c:if test="${gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.garaTelematicaSoloUpload')}">
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="modalitaPresentazione" value="2"  ${defaultmodalitaPresentazione2 } ${modalitaPresentazionedisabled }  />&nbsp;Solo upload di documenti
							<br>
							</c:if>
							<c:if test="${gene:checkProt(pageContext,'FUNZ.VIS.ALT.GENEWEB.QuestionariQForm') && gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.QuestionariQForm.associazioneBusta.tutte') && fn:contains(listaOpzioniDisponibili, 'OP135#')}">
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="modalitaPresentazione" value="3"  ${modalitaPresentazionedisabled } />&nbsp;Compilazione guidata con Q-Form
							<br>
							</c:if>
							<br>
							</div>
							&nbsp;<input type="radio" name="proceduraTelematica" value="2" onclick="javascript:aggiornaTelematica(2);" ${proceduraTelematicadisabled}/>&nbsp;Telematica in altre piattaforme o cartacea
							<br>
		 				</c:when>
		 				<c:when test="${defaultProceduraTelematica eq '2'}">
		 					&nbsp;<input type="radio" name="proceduraTelematica" value="1" onclick="javascript:aggiornaTelematica(1);" ${proceduraTelematicadisabled}/>&nbsp;Telematica nella piattaforma
							<br>
							<div id="presentazione" style="display:none;">
							<br>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Compilazione su portale mediante:</b>
							<br>
							<c:if test="${gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.garaTelematicaInserimentoImporti')}">
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="modalitaPresentazione" value="1" ${defaultmodalitaPresentazione1 }  ${modalitaPresentazionedisabled } />&nbsp;Inserimento importi o ribassi offerti e upload di documenti
							<br>
							</c:if>
							<c:if test="${gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.garaTelematicaSoloUpload')}">
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="modalitaPresentazione" value="2"  ${defaultmodalitaPresentazione2 } ${modalitaPresentazionedisabled } />&nbsp;Solo upload di documenti
							<br>
							</c:if>
							<c:if test="${gene:checkProt(pageContext,'FUNZ.VIS.ALT.GENEWEB.QuestionariQForm') && gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.QuestionariQForm.associazioneBusta.tutte') && fn:contains(listaOpzioniDisponibili, 'OP135#')}">
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="modalitaPresentazione" value="3"  ${modalitaPresentazionedisabled }  />&nbsp;Compilazione guidata con Q-Form
							<br>
							</c:if>
							</div>
							&nbsp;<input type="radio" name="proceduraTelematica" value="2" checked="checked" onclick="javascript:aggiornaTelematica(2);" ${proceduraTelematicadisabled}/>&nbsp;Telematica in altre piattaforme o cartacea
							<br>
		 				</c:when>
						<c:otherwise>
							&nbsp;<input type="radio" name="proceduraTelematica" value="1" onclick="javascript:aggiornaTelematica(1);" ${proceduraTelematicadisabled}/>&nbsp;Telematica nella piattaforma
							<br>
							<c:if test="${empty proceduraTelematicadisabled }">
								<div id="presentazione" ${modalitaPresentazioneNascosta }>
								<br>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Compilazione su portale mediante:</b>
								<br>
								<c:if test="${gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.garaTelematicaInserimentoImporti')}">
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="modalitaPresentazione" value="1" ${defaultmodalitaPresentazione1 } ${modalitaPresentazionedisabled }  />&nbsp;Inserimento importi o ribassi offerti e upload di documenti
								<br>
								</c:if>
								<c:if test="${gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.garaTelematicaSoloUpload')}">
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="modalitaPresentazione" value="2"  ${defaultmodalitaPresentazione2 } ${modalitaPresentazionedisabled }  />&nbsp;Solo upload di documenti
								<br>
								</c:if>
								<c:if test="${gene:checkProt(pageContext,'FUNZ.VIS.ALT.GENEWEB.QuestionariQForm') && gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.QuestionariQForm.associazioneBusta.tutte') && fn:contains(listaOpzioniDisponibili, 'OP135#')}">
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="modalitaPresentazione" value="3"  ${modalitaPresentazionedisabled }  />&nbsp;Compilazione guidata con Q-Form
								<br>
								</c:if>
								</div>
							</c:if>
							&nbsp;<input type="radio" name="proceduraTelematica" value="2" onclick="javascript:aggiornaTelematica(2);" ${proceduraTelematicadisabled}/>&nbsp;Telematica in altre piattaforme o cartacea
							<br>
						</c:otherwise>	 			
		 			</c:choose>
		 		</c:when>
		 		<c:otherwise>
		 			<input type="radio" name="proceduraTelematica" value="" style="display:none;"/>
		 			<input type="radio" name="modalitaPresentazione" value="" style="display:none;"/>
		 		</c:otherwise>
		 		
		 		</c:choose>
		 		<br>
		 		<!-- (SS140909) distinto il caso in cui non è prevista da profilo la gestione delle gare divise in lotti -->
		 		<!-- (SS141009) semplificato gestione controllo da profilo dopo l'introduzione del tipo di gara 'divise in lotti con offerta unica' -->
		 	    <c:choose>
					<c:when test='${(not gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareALotti")) and (not gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareLottiOffUnica"))}' >
						<input type="hidden" name="lotti" value="1"/>
					</c:when>
					<c:when test='${(not gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareALottoUnico")) and (not gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareALotti"))}' >
						<input type="hidden" name="lotti" value="1"/>
					</c:when>
					<c:otherwise>
		 				<b>Impostare il tipo di gara da creare:</b>
				 		<br>
						&nbsp;<input type="radio" name="gara" value="1" id="radiogara1" checked="checked" />&nbsp;Gara a lotto unico
						<br>
						<div style="padding-left: 29px;">
							<i>Gara singola o a lotto unico.</i>
						</div>
						<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareLottiOffUnica")}' >
							<div id="divOffUnica" >
							<br>
							&nbsp;<input type="radio" name="gara" id="radiogara3" value="3" />&nbsp;Gara divisa in lotti
							<br>
							<div style="padding-left: 29px;">
								<i>Gara suddivisa in pi&ugrave; lotti. Ogni concorrente presenta un unico plico, indipendentemente
								dal numero di lotti per cui concorre, contenente la busta amministrativa 
								e una o più buste tecniche ed economiche.</i>
							</div>
							</div>
						</c:if> 
						<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareALotti")}' >
							<br>
							<div id="divLottiDistinti" ${lottidistintinascoto }>
							&nbsp;<input type="radio" name="gara" id="radiogara2" value="2" />&nbsp;Gara divisa in lotti con plichi distinti per ogni lotto
							<br>
							<div style="padding-left: 29px;">
								<i>Gara suddivisa in pi&ugrave; lotti 'indipendenti' o 'tornata di gare'.
								Consente di effettuare un'unica pubblicazione per tutti i lotti.
								Ciascun lotto &egrave; trattato come una gara singola: ogni concorrente presenta un plico distinto per ogni lotto.</i>
							</div>
							</div>
						</c:if>
					</c:otherwise>
		 	    </c:choose>
		 	    <br>
		 	    <b>Impostare il tipo di appalto della gara:</b>
					<c:choose>
						<c:when test="${gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.GARE.GestioneGarePerLavori')}">
				 	    	<c:set var='checkLavori' value='checked="checked"'/>
							<c:set var="displayLavori" value=''/>
				 	    	<c:set var='checkForniture' value=''/>
						</c:when>
						<c:otherwise>
							<c:set var="checkLavori" value=''/>
							<c:set var="displayLavori" value='style="display:none;"'/>
				 	    	<c:set var='checkForniture' value='checked="checked"'/>
						</c:otherwise>
					</c:choose>
			 		<span ${displayLavori}>
				 		<br>
						&nbsp;<input type="radio" name="appalto" value="1" ${checkLavori}/>&nbsp;Lavori
					</span>
					<br>
					&nbsp;<input type="radio" name="appalto" value="2" ${checkForniture}/>&nbsp;Forniture
					<br>
					&nbsp;<input type="radio" name="appalto" value="3" />&nbsp;Servizi
			 	<br>
			 	<br>
			</td>
		</tr>
		<tr>
			<td class="comandi-dettaglio" colSpan="2">
		      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaCreazione();">&nbsp;&nbsp;&nbsp;&nbsp;
		      <INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:creaNuovaGara();">&nbsp;
			</td>
		</tr>
	</table>
</form>

<form name="listaNuovo" action="${contextPath}/Lista.do" method="post">
	<input type="hidden" name="jspPath" value="" /> 
	<input type="hidden" name="jspPathTo" value="" /> 
	<input type="hidden" name="activePage" value="0" /> 
	<input type="hidden" name="isPopUp" value="0" /> 
	<input type="hidden" name="numeroPopUp" value="0" /> 
	<input type="hidden" name="metodo" value="nuovo" /> 
	<input type="hidden" name="entita" value="" /> 
	<input type="hidden" name="gestisciProtezioni" value="1" />
</form>
