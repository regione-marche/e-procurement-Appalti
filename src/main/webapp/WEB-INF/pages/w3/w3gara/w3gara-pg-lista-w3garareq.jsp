
<%
	/*
	 * Created on 23-Apr-2008
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="stato_simog_gara" value='${gene:callFunction2("it.eldasoft.sil.w3.tags.funzioni.GetStatoSimogW3GaraFunction",pageContext,numgara)}' />
<c:set var="idgara" value='${gene:callFunction2("it.eldasoft.sil.w3.tags.funzioni.GetIdGaraW3GaraFunction",pageContext,numgara)}' />
<c:set var="entita" value="W3GARAREQ" />
<c:set var="numgara" value='${gene:getValCampo(key,"NUMGARA")}' scope="request" />
<c:set var="numreq" value='0' scope="request" />


<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>
<table class="dettaglio-tab-lista">
	<tr>
		<td>
		<gene:formLista entita="W3GARAREQ" pagesize="20" sortColumn="4"
			where="W3GARAREQ.NUMGARA = #W3GARA.NUMGARA#" tableclass="datilista"
			gestisciProtezioni="true" gestore="it.eldasoft.sil.w3.tags.gestori.submit.GestoreRequisitiGara">
			
			<c:choose>
				<c:when test='${updateLista eq 1}'>
					<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
					<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
					<gene:redefineInsert name="addToAzioni">
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:listaConferma();" title="Salva modifiche" tabindex="1500">
									${gene:resource("label.tags.template.dettaglio.schedaConferma")}
								</a>
							</td>
						</tr>
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:listaAnnullaModifica();" title="Annulla modifiche" tabindex="1501">
									${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}
								</a>
							</td>
						</tr>
						</gene:redefineInsert>
					</c:when>
					<c:otherwise>
						<gene:redefineInsert name="addToAzioni">
						<c:if test="${not (currentRow == 0 or stato_simog_gara eq '5' or stato_simog_gara eq '6' or stato_simog_gara eq '7')}">
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:listaApriInModifica();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1501">
										${gene:resource("label.tags.template.dettaglio.schedaModifica")}
									</a>
								</td>
							</tr>
						</c:if>
						<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.W3.richiesteSIMOG")}'>
							<c:if test="${currentRow != 0 and (stato_simog_gara eq '2' or stato_simog_gara eq '3' or stato_simog_gara eq '4') and idgara ne ''}">
								<jsp:include page="/WEB-INF/pages/w3/commons/addtodocumenti-requisiti-gara.jsp" />
								<tr>
									<c:choose>
										<c:when test='${isNavigazioneDisattiva ne "1" && updateLista eq 0}'>
										  <td class="vocemenulaterale">
											<a href="javascript:popupRichiestaRequisiti('${numgara}', '${numreq}', '${idgara}');" title="Invia richiesta requisiti gara" tabindex="1513">
												Invia richiesta requisiti gara
											</a>
										  </td>
										</c:when>
										<c:otherwise>
										   <td>
											 Invia richiesta requisiti gara
										   </td>
										</c:otherwise>
									</c:choose>
								</tr>
							</c:if>
						</c:if>
					</gene:redefineInsert>
				</c:otherwise>
			</c:choose>
			
			<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>" width="50">
				<c:if test="${currentRow >= 0}">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza requisito" />
						<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD")}'>
							<c:if test="${!(stato_simog_gara eq '5' or stato_simog_gara eq '6' or stato_simog_gara eq '7')}">
									<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica"
										title="Modifica requisito" />
							</c:if>
						</c:if>
						<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}'>
							<c:if test="${!(stato_simog_gara eq '5' or stato_simog_gara eq '6' or stato_simog_gara eq '7') and !(datiRiga.W3GARAREQ_NUMREQ <= 17)}">
									<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina"
										title="Elimina requisito" />
							</c:if>
						</c:if>
					</gene:PopUp>
					<c:if test='${gene:checkProtFunz(pageContext, "DEL","LISTADELSEL")}'>
						<c:choose>
							<c:when test="${!(stato_simog_gara eq '5' or stato_simog_gara eq '6' or stato_simog_gara eq '7') and !(datiRiga.W3GARAREQ_NUMREQ <= 17)}">
								<input type="checkbox" name="keys" value="${chiaveRiga}" />
							</c:when>
							<c:otherwise>
								<input type="checkbox" name="keys" value="${chiaveRiga}" disabled="disabled"/>
							</c:otherwise>
						</c:choose>
					</c:if>
				</c:if>
			
			</gene:campoLista>
			
			<gene:campoLista campo="NUMGARA" visibile="false" edit="${updateLista eq 1 }"/>
			<gene:campoLista title="N." campo="NUMERO_REQUISITO" value="${datiRiga.W3GARAREQ_NUMREQ}" width="30" campoFittizio="true" definizione="N3;;;;W3_GARAREQ_NUMREQ" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" ordinabile="true"/>
					
			<gene:campoLista title="N." campo="NUMREQ" visibile="false" edit="${updateLista eq 1 }" width="30" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			<gene:campoLista title="Requisito" campo="CODICE_DETTAGLIO" gestore="it.eldasoft.sil.w3.tags.funzioni.decoratori.campi.GestoreCampoW3TABREQ"
				href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>
			<gene:campoLista campo="FLAG_ESCLUSIONE" edit="${updateLista eq 1 }"/>
			<gene:campoLista campo="FLAG_COMPROVA_OFFERTA" edit="${updateLista eq 1 }"/>
			<gene:campoLista campo="FLAG_AVVALIMENTO" edit="${updateLista eq 1 }"/>
			<gene:campoLista campo="FLAG_BANDO_TIPO" edit="${updateLista eq 1 }"/>
			<gene:campoLista campo="FLAG_RISERVATEZZA" edit="${updateLista eq 1 }"/>
			<input type="hidden" name="numeroRequisiti" id="numeroRequisiti" value="" />
		</gene:formLista>
		</td>
	</tr>
	<c:if test="${currentRow == 0 or stato_simog_gara eq '5' or stato_simog_gara eq '6' or stato_simog_gara eq '7'}">
		<gene:redefineInsert name="pulsanteListaInserisci"/>
		<gene:redefineInsert name="pulsanteListaEliminaSelezione"/>
		<gene:redefineInsert name="listaNuovo"/>
		<gene:redefineInsert name="listaEliminaSelezione"/>
	</c:if>
	
	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<c:choose>
				<c:when test='${updateLista eq 1 }'>
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="listaConferma();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:listaAnnullaModifica();">
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${currentRow == 0 or stato_simog_gara eq '5' or stato_simog_gara eq '6' or stato_simog_gara eq '7'}">
							&nbsp;
						</c:when>
						<c:otherwise>
							<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:listaApriInModifica();">&nbsp;
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</tr>


</table>

<gene:javaScript>

	document.getElementById("numeroRequisiti").value = ${currentRow}+1;	
	
</gene:javaScript>	




