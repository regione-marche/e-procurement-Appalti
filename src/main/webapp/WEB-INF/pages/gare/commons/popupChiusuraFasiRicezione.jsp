<%
/*
 * Created on: 27-nov-2008
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>


<c:choose>
	<c:when test='${not empty param.garaTelematica}'>
		<c:set var="garaTelematica" value="${param.garaTelematica}" />
	</c:when>
	<c:otherwise>
		<c:set var="garaTelematica" value="${garaTelematica}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codiceGara}'>
		<c:set var="codiceGara" value="${param.codiceGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value="${codiceGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.faseGara}'>
		<c:set var="faseGara" value="${param.faseGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="faseGara" value="${faseGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.isGaraLottiConOffertaUnica}'>
		<c:set var="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
	</c:when>
	<c:otherwise>
		<c:set var="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.calcoloSogliaAnomaliaExDLgs2017}'>
		<c:set var="calcoloSogliaAnomaliaExDLgs2017" value="${param.calcoloSogliaAnomaliaExDLgs2017}" />
	</c:when>
	<c:otherwise>
		<c:set var="calcoloSogliaAnomaliaExDLgs2017" value="${calcoloSogliaAnomaliaExDLgs2017}" />
	</c:otherwise>
</c:choose>

<c:if test='${param.operazione eq "ATTIVA"}'>		
<c:choose>
	<c:when test="${garaTelematica eq 'true' }">
		<c:set var="isSuperataDataAperturaPlichi" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsSuperataDataAperturaPlichiFunction", pageContext, ngara)}' />
		<c:set var="isSuperataDataTerminePresentazione" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsSuperataDataTerminePresentazioneFunction", pageContext, ngara, "1")}' />
		<c:set var="esistonoAcquisizioniOfferteDaElaborare" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, ngara, "FS11" )}' />
		<c:set var="EsistonoAcquisizioniRinunceDaElaborare" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, ngara, "FS14" )}' />
		<c:if test="${esistonoAcquisizioniOfferteDaElaborare eq 'false' or EsistonoAcquisizioniRinunceDaElaborare eq 'false'}">
			<c:choose>
				<c:when test="${isGaraLottiConOffertaUnica eq 'true'}">
					<c:set var="whereDitte" value=" and codgar5=ngara5 and (invoff is null and (ammgar is null or ammgar='1'))"/>
				</c:when>
				<c:otherwise>
					<c:set var="whereDitte" value=" and (invoff is null and (ammgar is null or ammgar='1'))"/>
				</c:otherwise>
			</c:choose>
			<c:set var="esistonoAcquisizioniOfferteDaElaborare1" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteInGaraFunction", pageContext, "CODGAR5", codiceGara,whereDitte)}' />
		</c:if>
		<c:set var="faseRicezionePlichi" value='${faseGara eq "1"}' />
		<c:set var="esistonoDitteInGara" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteInGaraFunction", pageContext, "CODGAR5", codiceGara," and (fasgar > 1 or fasgar is null)")}' />
	</c:when>
	<c:otherwise>
		<c:set var="messaggioControlloEco" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlloPunteggioTecnicoEconomicoFunction", pageContext, ngara,"60", "aperturaDocAmministrativa","false" )}' />
		<c:if test="${empty messaggioControlloEco  }">
			<c:set var="messaggioControlloTec" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlloPunteggioTecnicoEconomicoFunction", pageContext, ngara,"50", "aperturaDocAmministrativa","false" )}' />
		</c:if>
	</c:otherwise>
</c:choose>
</c:if>

<c:set var="whereNobustamm" value="codgar='${codiceGara }'"/>
<c:set var="nobustamm" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "nobustamm","torn", whereNobustamm)}' />

<c:choose>
	<c:when test='${ nobustamm ne "1"}'>
		<c:set var="msg" value="documentazione amministrativa"/>
	</c:when>
	<c:otherwise>
		<c:set var="msg" value="offerte"/>
	</c:otherwise>
</c:choose>
<gene:template file="popup-message-template.jsp">
<c:choose>
	<c:when test='${param.operazione eq "ATTIVA"}'>
		<gene:setString name="titoloMaschera" value='Attiva apertura ${msg}' />
	</c:when>
	<c:otherwise>
		<gene:setString name="titoloMaschera" value='Disattiva apertura ${msg}' />
	</c:otherwise>
</c:choose>

	<gene:redefineInsert name="corpo">
		<c:choose>
			<c:when test='${param.operazione eq "ATTIVA"}'>		
				<c:choose>
					<c:when test="${garaTelematica eq 'true' && (isSuperataDataAperturaPlichi ne 'true' || isSuperataDataTerminePresentazione ne 'true' || esistonoAcquisizioniOfferteDaElaborare ne 'false' || faseRicezionePlichi ne 'true' || esistonoDitteInGara eq 'false'
						|| esistonoAcquisizioniOfferteDaElaborare1 ne 'false') }">
						<c:set var="bloccoSalvataggio" value='true'/>
						<c:choose>
							<c:when test="${isSuperataDataAperturaPlichi eq 'false'}">
								<br>
								Non &egrave; possibile procedere alla fase di apertura ${msg} perch&egrave; non &egrave; ancora scaduto il termine di apertura dei plichi.
								<br>
								<br>
								L'apertura dei plichi è prevista il giorno <b>${torn_desoff}</b>
								<c:if test="${!empty torn_oesoff and torn_oesoff ne '00:00'}">alle ore <b>${torn_oesoff}</b></c:if>.
								<br>
								<br>
							</c:when>
							<c:when test="${isSuperataDataTerminePresentazione eq 'false'}">
								<br>
								Non &egrave; possibile procedere alla fase di apertura ${msg} perch&egrave; non &egrave; ancora scaduto il termine di presentazione delle offerte.
								<br>
								<br>
								Il termine per la presentazione delle offerte scade il giorno <b>${dataScadenza}</b> alle ore <b>${oraScadenza}</b>.
								<br>
								<br>
							</c:when>
							<c:when test="${esistonoAcquisizioniOfferteDaElaborare eq 'true' || esistonoAcquisizioniOfferteDaElaborare1 eq 'true'}">
								<br>
								Non &egrave; possibile procedere alla fase di apertura ${msg} perch&egrave; devono essere prima acquisite le offerte da portale Appalti. 
								<br>
								Ritornare alla fase 'Ricezione plichi' e attivare la funzione 'Acquisisci offerte da portale Appalti'.
								<br>
								<br>
							</c:when>
							<c:when test="${esistonoDitteInGara eq 'false' }">
								<br>
								Non &egrave; possibile procedere alla fase di apertura ${msg} perch&egrave; non ci sono ditte in gara.
								<br>
								<br>
							</c:when>
							<c:otherwise>
								<br>
								Non &egrave; possibile procedere alla fase di apertura ${msg} perch&egrave; la fase della gara non è "Ricezione plichi". 
								<br>
								<br>
							</c:otherwise>			
						</c:choose>
					</c:when>
					<c:when test="${garaTelematica ne 'true' && (!empty messaggioControlloEco || !empty messaggioControlloTec) }">
						<c:set var="bloccoSalvataggio" value='true'/>
							<c:if test="${!empty messaggioControlloTec}">
								<br>
								${messaggioControlloTec}
								<br>
								<br>
							</c:if>
							<c:if test="${!empty messaggioControlloEco}">
								<br>
								${messaggioControlloEco}
								<br>
								<br>
							</c:if>
					</c:when>
					<c:otherwise>
						<br>
						Mediante questa funzione &egrave; possibile procedere alla fase di apertura ${msg}.
												
						<c:set var="esitoControlloCommissione" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlliComponentiCommissioneFunction", pageContext, ngara,"false", ngara, isGaraLottiConOffertaUnica)}' />
						<c:if test="${ esitoControlloCommissione eq 'NOK'}">
							${msgCommissione}
						</c:if>
						<br><br>
						
						<c:if test="${bloccoSalvataggio ne 'true' and calcoloSogliaAnomaliaExDLgs2017 ne '1'}">
							<c:set var="IsMODLICG_13_14" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsMODLICG_13_14Function", pageContext, ngara,isGaraLottiConOffertaUnica)}' />
							<c:if test="${IsMODLICG_13_14 eq 'true' }">
								<c:set var="isGaraDopoDLGS2016Manuale" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.IsGaraDopoDLGS2016ManualeFunction", pageContext, ngara,"true","false","true")}' />
								<c:if test="${esitoControlloDitteDLGS2016 && isGaraDopoDLGS2016Manuale eq '1' && (isGaraDLGS2016 or isGaraDLGS2017)}"> 
									${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetMetodoCalcoloSogliaFunction", pageContext, ngara, isGaraLottiConOffertaUnica)}
									<c:set var="gestioneSogliaManuale" value="true"/>
									Confermi l'operazione ?<br>
									Prima di procedere, impostare il metodo di calcolo della soglia di anomalia.
									<c:if test="${isGaraLottiConOffertaUnica eq 'true'}">
										<br>Il metodo selezionato viene assegnato uguale a tutti i lotti della gara. 
									</c:if>
									<br>
									<br>
								</c:if>
								<c:if test="${esitoControlloDitteDLGS2016 && isGaraDopoDLGS2016Manuale ne '1' && (isGaraDLGS2016 or isGaraDLGS2017)}">
									 <c:set var="gestioneSogliaAutomatica" value="true"/>
									 Confermi l'operazione ?<br>
									 Contestualmente viene assegnato, mediante sorteggio, il metodo di calcolo della soglia di anomalia.
									 Sarà possibile consultare il metodo assegnato nella fase di apertura documentazione amministrativa.
									 <br>
									 <br>
								</c:if>
							</c:if>
							
						</c:if>	
						
						<c:choose>
							<c:when test="${gestioneSogliaManuale eq 'true'}">
								<table class="dettaglio-notab">
								<form action="" name="soglia">
								<tr id="rowMETSOGLIA" >
								<td class="etichetta-dato" width="30%">Metodo calcolo soglia anomalia (*)</td>
								<td style="PADDING-LEFT: 10px; TEXT-ALIGN: left; BORDER-TOP: #A1BAA9 1px solid;" width="40%">
								<select id="METSOGLIA" name="METSOGLIA" title="Metodo di calcolo soglia anomalia applicato" >
								<option value="" title="&nbsp;" selected="selected" >&nbsp;</option>
								${gene:callFunction3("it.eldasoft.gene.tags.functions.GetListaValoriTabellatoFunction", pageContext, "A1126", "tipiMetodi")}
								<c:forEach var="metodo" items="${requestScope.tipiMetodi}">
									<option value="${metodo.tipoTabellato }" title="${metodo.descTabellato }" <c:if test="${metodo.tipoTabellato eq  metsoglia}">selected="selected"</c:if>>${metodo.descTabellato }</option>
								</c:forEach>
								</select>
								</td>
								<td style="PADDING-LEFT: 10px; TEXT-ALIGN: left; BORDER-TOP: #A1BAA9 1px solid; " width="30%">
					 			<a href="javascript:apriDocumento('${pageContext.request.contextPath}/doc/DLgs50-2016_calcoloSogliaAnomalia.pdf');" title="Consulta manuale" style="color:#002E82;">
					 				<img width="16" height="16" title="Consulta manuale" alt="Consulta manuale" src="${pageContext.request.contextPath}/img/consultazioneManuale.png"/> Consulta manuale
					 			</a>
					 			</td>
								</tr>
								<c:choose>
									<c:when test="${isGaraDLGS2016 }">
										<c:set var="tipoLegge" value="DLGS2016"/>
									</c:when>
									<c:otherwise>
										<c:set var="tipoLegge" value="DLGS2017"/>
									</c:otherwise>
								</c:choose>
								${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoriCampoMETCOEFFFunction", pageContext, tipoLegge)}
								<tr id="rowMETCOEFF" style="display:none;">
								<td class="etichetta-dato">Coefficiente (*)</td>
								<td style="PADDING-LEFT: 10px; TEXT-ALIGN: left; BORDER-TOP: #A1BAA9 1px solid;" colspan="2">
								<select id="METCOEFF" name="METCOEFF" title="Coefficiente"  >
								<c:if test='${not empty listaValoriTabellatoDLGS}'>
									<option value="" title="&nbsp;" >&nbsp;</option>
									<c:forEach items="${listaValoriTabellatoDLGS}" var="valoreTabellato" varStatus="status" >
										<option value="${valoreTabellato[0]}" title="${valoreTabellato[1]}" <c:if test="${valoreTabellato[0] eq  metcoeff}">selected="selected"</c:if>>${valoreTabellato[1]}</option>
									</c:forEach>
								</c:if>
								</select>
								</td>
								</tr>
								</form>
								</table>
							
							</c:when>
							<c:when test="${gestioneSogliaAutomatica eq 'true' and isGaraLottiConOffertaUnica eq 'true'}">
								<table class="dettaglio-notab">
								<form action="" name="soglia">
								<tr id="rowTUTTI_LOTTI" >
								<td class="etichetta-dato" width="30%">Fare unico sorteggio e assegnare lo stesso metodo a tutti i lotti? (*)</td>
								<td style="PADDING-LEFT: 10px; TEXT-ALIGN: left; BORDER-TOP: #A1BAA9 1px solid;" >
								<select id="TUTTI_LOTTI" name="TUTTI_LOTTI" title="Fare unico sorteggio e assegnare lo stesso metodo a tutti i lotti?" >
									<option value="1" title="Si" >Si</option>
									<option value="2" title="No" selected="selected">No</option>
								</select>
								</td>
								</tr>
								</form>
								</table>
							</c:when>
							<c:when test="${gestioneSogliaAutomatica ne 'true' and gestioneSogliaManuale ne 'true'}">
								Confermi l'operazione ?
							</c:when>
						</c:choose>
											
						<br>
						<br>
					</c:otherwise>
				</c:choose>
				
			</c:when>
			<c:otherwise>
				<br>
				Mediante questa funzione &egrave; possibile disattivare la fase di apertura ${msg}.
				<br><br>
				Confermi l'operazione ?
				<br>
				<br>
			</c:otherwise>
		</c:choose>
		
		
		
		<input type="hidden" id="garaTelematica" value="${garaTelematica}" />
		<input type="hidden" id="ngara" value="${ngara}" />
		<input type="hidden" id="faseGara" value="${faseGara}" />
		<input type="hidden" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
		<input type="hidden" id="codiceGara" value="${codiceGara}" />
  </gene:redefineInsert>
	 <c:if test='${ bloccoSalvataggio eq "true"}' >
	  	<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
		</gene:redefineInsert>
	  </c:if>
	
	<gene:javaScript>
		<c:if test="${gestioneSogliaManuale eq 'true'}"> 
		$('#METSOGLIA').change(function() {
			 metosoglia = $( this ).val();
			 if(metosoglia==5)
			 	$("#rowMETCOEFF").show();
			 else{
			 	$("#rowMETCOEFF").hide();
			 	$("#METCOEFF").val('');
			 }
	    });
	    
	    var metsogliaIni = $("#METSOGLIA").val();
		if(metsogliaIni==5)
			$("#rowMETCOEFF").show();
	    </c:if>
	
		function conferma(){
			<c:if test="${gestioneSogliaManuale eq 'true'}">
				var metsoglia = $("#METSOGLIA").val();
				if(metsoglia==null || metsoglia ==""){
					clearMsg();
					outMsg("Il campo \"Metodo calcolo soglia anomalia \" è obbligatorio","ERR");
					onOffMsgFlag(true);
					return;
				}
				var metcoff = $("#METCOEFF").val();
				if(metsoglia==5 && (metcoff==null || metcoff =="")){
					clearMsg();
					outMsg("Il campo \"Coefficiente per calcolo metodo E\" è obbligatorio","ERR");
					onOffMsgFlag(true);
					return;
				}
				
				window.opener.setValue("METSOGLIA", metsoglia);
				window.opener.setValue("METCOEFF", metcoff);
				window.opener.setValue("GESTIONE_SOGLIA","MAN");
				window.opener.setValue("ISGARADLGS2017","${isGaraDLGS2017 }");
				
			</c:if>
			<c:if test="${gestioneSogliaAutomatica eq 'true'}">
				window.opener.setValue("GESTIONE_SOGLIA","AUTO");
				window.opener.setValue("ISGARADLGS2017","${isGaraDLGS2017 }");
				var tuttiLotti = $("#TUTTI_LOTTI").val();
				window.opener.setValue("APPLICA_TUTTI_LOTTI",tuttiLotti);	
			</c:if>
			
			window.opener.bloccaRichiesteServer();
	<c:choose>
		<c:when test='${param.operazione eq "ATTIVA"}'>
			window.opener.attivaFasiGara();
		</c:when>
		<c:otherwise>
			window.opener.disattivaFasiGara();
		</c:otherwise>
	</c:choose>
			window.close();
		}

		function annulla(){
			window.close();
		}

	</gene:javaScript>
</gene:template>