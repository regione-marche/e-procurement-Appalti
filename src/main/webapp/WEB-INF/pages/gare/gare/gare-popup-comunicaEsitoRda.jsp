<%
	/*
	 * Created on 03-nov-2009
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%//Popup per comunicare l'esito delle rda in gara%>

<c:choose>
	<c:when test='${RISULTATO eq "CALCOLOESEGUITO"}' >
		<script type="text/javascript">
				opener.historyReload();
				window.close();
		</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="head" >
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wserpsupporto.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wserp.attributi.fornitore.js"></script>
	
		<style type="text/css">
			
			TABLE.grigliaforn {
				margin-left: 0px;
				margin-right: 0px;
				margin-top: 2px;
				margin-bottom: 2px;
				padding: 0px;
				width: 100%;
				font-size: 11px;
				border-collapse: collapse;
			}
			
			TABLE.grigliaforn TR {
				background-color: #FFFFFF;
			}
			
			TABLE.grigliaforn TR.intestazione, TABLE.grigliaforn TR.riepilogo {
				background-color: #EFEFEF;
			}
			
			TABLE.grigliaforn TR.intestazione TH {
				padding: 4 2 4 2;
				text-align: center;
				border: 1px solid #A0AABA;	
				height: 40px;
			}
					
			TABLE.grigliaforn TR TD {
				padding-left: 2px;
				padding-right: 2px;
				padding-top: 2px;
				padding-bottom: 2px;
				height: 25px;
				text-align: left;
				border: 1px solid #A0AABA;
			}
			
			TABLE.grigliaforn TR.intestazione TH.forn, TABLE.grigliaforn TR TD.forn {
				width: 110px;
			}

			TABLE.grigliaforn TR.intestazione TH.descrizione, TABLE.grigliaforn TR TD.descrizione {
				width: 360px;
			}
		
			TABLE.grigliaforn TR.intestazione TH.newdescrizione, TABLE.grigliaforn TR TD.newdescrizione {
				width: 685px;
			}

			TABLE.grigliaforn TR.intestazione TH.tipologia, TABLE.grigliaforn TR TD.tipologia {
				width: 320px;
			}
		
			div.operazione {
				padding-right: 50px;
				padding-top: 6px;
				padding-bottom: 0px;
			}
			
			div.legenda {
				border: 1px dotted #A0AABA;
				padding-top: 2px;
				padding-bottom: 2px;
				padding-left: 5px;
				margin-top: 10px;
			}
		
		</style>
		
	</gene:redefineInsert>


	<c:choose>
		<c:when test='${RISULTATO != null}'>
			<c:set var="ngara" value='${NGARA}' />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${fn:substringAfter(param.ngara,':')}" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test='${!empty param.isGaraLottiConOffertaUnica}'>
			<c:set var="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
		</c:when>
		<c:otherwise>
			<c:set var="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.tipoWSERP}'>
			<c:set var="tipoWSERP" value="${param.tipoWSERP}" />
		</c:when>
		<c:otherwise>
			<c:set var="tipoWSERP" value="${tipoWSERP}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${tipoWSERP eq "AVM"}'>
			<c:set var="masktitle" value="Creazione Rdo/offerta" />
		</c:when>
		<c:when test='${tipoWSERP eq "TPER"}'>
			<c:set var="masktitle" value="Creazione anagrafica fondo" />
		</c:when>
		<c:when test='${tipoWSERP eq "RAIWAY"}'>
			<c:set var="masktitle" value="Aggiorna procedura su ERP" />
		</c:when>
		<c:otherwise>
			<c:set var="masktitle" value="Aggiorna RdA con i dati di aggiudicazione" />
		</c:otherwise>
	</c:choose>
		
	<c:set var="ditta" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDittaFunction", pageContext, ngara)}'/>
	<c:set var="not_gar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetOggettoGaraFunction", pageContext, ngara)}' />
	<c:if test='${!empty not_gar}'>
		<c:set var="oggettoGara" value="${fn:substring(not_gar,0,40)}" />
	</c:if>
	
	<c:set var="controlloSuperato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetWSERPDatiObbligatoriFunction", pageContext, ngara, tipoWSERP)}'/>
	<c:if test='${tipoWSERP eq "CAV"}'>
		<c:set var="isRda" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDatiRdaGaraFunction", pageContext, ngara)}'/>
	</c:if>
	
	<c:if test='${tipoWSERP eq "ATAC"}'>
		<c:set var="str_esito" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetWSERPVerificaFornitoreFunction", pageContext, ditta)}'/>
		<c:set var="isFornitoreERP" value="false" />
	</c:if>
	
	
	<gene:setString name="titoloMaschera" value="${masktitle}" />
	
	<gene:redefineInsert name="corpo">
		<c:set var="chiave" value="${ngara}" />
		
		<c:choose>
			<c:when test='${RISULTATO eq "CALCOLOESEGUITO"}'>
				<c:set var="modo" value="APRI" scope="request" />	
			</c:when>
			<c:otherwise>
				<c:set var="modo" value="MODIFICA" scope="request" />
			</c:otherwise>
		</c:choose>

		<gene:formScheda entita="GARE" where="GARE.NGARA = '${chiave}'" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupComunicaEsitoRda">
		

		<c:set var="msgCTRL" value="Si sono presentate le seguenti anomalie:"/>

		<c:choose>
			<c:when test='${controlloSuperato ne "true"}'>
			<gene:campoScheda>
				<td id="msgctrl"colSpan="2">
					<br>
					${msgCTRL}
					<br>&nbsp;
				</td>
			</gene:campoScheda>
			
			<gene:campoScheda >
				<td colSpan="2">
					<textarea cols="70" rows="10" readonly="readonly">${requestScope.messaggi}</textarea>
				</td>
			</gene:campoScheda>
			</c:when>
			<c:otherwise>
		

			<c:if test='${empty RISULTATO && !empty ditta && (tipoWSERP eq "AVM" || tipoWSERP eq "TPER" || tipoWSERP eq "CAV")}'>
				<gene:campoScheda addTr="false">
					<tr>
						<td colspan="2" style="border-bottom: 0px #FFFFFF solid">
							<br>
							<b>Dati di confronto per il fornitore</b>
							<br>
							<span id="messaggioSezioneDatiForn"></span>
							<br>
							<table class="grigliaforn">
								<tr class="intestazione">
									<th class="forn">Attributo</th>
									<th class="newdescrizione">Valore Appalti e Contratti</th>
									<c:if test='${tipoWSERP eq "AVM" || tipoWSERP eq "CAV"}'>
										<th class="newdescrizione">Valore ERP</th>
									</c:if>
								</tr>
				</gene:campoScheda>
				
				
				<c:choose>
					<c:when test='${tipoWSERP eq "AVM"}'>
						<c:set var="cont_i" value="1" />
					</c:when>
					<c:when test='${tipoWSERP eq "TPER" || tipoWSERP eq "CAV"}'>
						<c:set var="cont_i" value="0" />
					</c:when>
					<c:otherwise>
						<c:set var="cont_i" value="0" />
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test='${tipoWSERP eq "AVM"}'>
						<c:set var="cont_f" value="13" />
					</c:when>
					<c:when test='${tipoWSERP eq "TPER"}'>
						<c:set var="cont_f" value="15" />
					</c:when>
					<c:when test='${tipoWSERP eq "CAV"}'>
						<c:set var="cont_f" value="4" />
					</c:when>

					<c:otherwise>
						<c:set var="cont_f" value="0" />
					</c:otherwise>
				</c:choose>
				
				<c:forEach begin="${cont_i}" end="${cont_f}" var="numforn">
					<gene:campoScheda addTr="false">
								<tr id="rowIMPR_NEW_ROW_${numforn}" >
					</gene:campoScheda>
					<gene:campoScheda addTr="false">
									<td class="newdescrizione">
										<span id="IMPR_DESCRIZIONE_${numforn}" name="IMPR_DESCRIZIONE_${numforn}" />
									</td>					
									<td class="newdescrizione">
										<span id="IMPR_AC_DESCRIZIONE_${numforn}" name="IMPR_AC_DESCRIZIONE_${numforn}" />
									</td>
									<c:if test='${tipoWSERP eq "AVM" || tipoWSERP eq "CAV"}'>					
										<td class="newdescrizione">
											<span id="IMPR_SAP_DESCRIZIONE_${numforn}" name="IMPR_SAP_DESCRIZIONE_${numforn}" />
										</td>
									</c:if>					
								</tr>
					</gene:campoScheda>
				</c:forEach>
				
				<gene:campoScheda addTr="false">
					</table>
				</gene:campoScheda>

					
				
				<gene:campoScheda addTr="false">
					<tr>
						<td id ="parCreaFornitore" colspan="2" style="border-bottom: 0px #FFFFFF solid">
							<br>
							<b>Parametri per la creazione del nuovo fornitore</b>
							<br>
							<br>
							<table id ="tabCreaFornitore" class="dettaglio-notab">
							<c:if test='${tipoWSERP eq "AVM" || tipoWSERP eq "CAV"}'>						
								<tr>
									<td class="etichetta-dato">Gruppo conti</td>
									<td class="valore-dato">
										<select id="p1" name="grp_conti"></select>
									</td>						
								</tr>
							</c:if>
							<c:if test='${tipoWSERP eq "TPER" || tipoWSERP eq "CAV"}'>						
								<tr>
									<td class="etichetta-dato">Condizioni pagamento</td>
									<td class="valore-dato">
										<select id="condpag" name="cond_pag"></select>
									</td>						
								</tr>
							</c:if>
							<c:if test='${tipoWSERP eq "CAV"}'>						
								<tr>
									<td class="etichetta-dato">Modo pagamento</td>
									<td class="valore-dato">
										<select id="p3" name="modo_pag"></select>
									</td>						
								</tr>
						</c:if>
					</tr>

				</gene:campoScheda>
				<gene:campoScheda addTr="false">
						</table>
				</gene:campoScheda>
				<c:if test='${tipoWSERP eq "TPER"}'>
					<gene:campoScheda addTr="false">
						<tr>
							<td id ="parCreaCig" colspan="2" style="border-bottom: 0px #FFFFFF solid">
								<br>
								<b>Parametri per la creazione del Cig</b>
								<br>
								<br>
								<table id ="tabCreaCig" class="dettaglio-notab">
									<tr>
										<td class="etichetta-dato">Definizione Cig</td>
										<td class="valore-dato" style="padding-top: 8px; border-bottom: 0px;">
											<textarea id="defcig" rows="3" cols="40" maxlength="40" style="resize: none;">${oggettoGara}</textarea>
										</td>						
									</tr>
						</tr>
					</gene:campoScheda>
					<gene:campoScheda addTr="false">
							</table>
					</gene:campoScheda>
				</c:if>
				<c:if test='${tipoWSERP eq "CAV"}'>

					<gene:campoScheda addTr="false">
							<tr>
								<td id ="parCreaAffidamento" colspan="2" style="border-bottom: 0px #FFFFFF solid">
								<br>
								<b>Contratto</b>
								<br>
								<br>								
								<table id ="tabCreaCig" class="dettaglio-notab">
									<tr>
									<td class="etichetta-dato">Tipologia contratto</td>
									<td class="valore-dato">
										<select id="p4" name="tipo_contr"></select>
									</td>						
									</tr>
						</tr>
					</gene:campoScheda>
					<gene:campoScheda addTr="false">
							</table>
					</gene:campoScheda>
				</c:if>
			</c:if>
			
			
			<c:choose>
				<c:when test='${tipoWSERP eq "TPER"}'>
					<c:set var="str_calcolowarning" value="L'aggiornamento dei dati di aggiudicazione ha presentato degli errori/warning." />
					<c:set var="str_preinvio" value="Vengono aggiornati i dati in gara
						 a seguito del calcolo dell'aggiudicazione." />
				</c:when>
				<c:when test='${tipoWSERP eq "RAIWAY"}'>
					<c:set var="str_preinvio" value="Vengono aggiornati i dati della procedura su ERP." />
					<c:set var="str_calcoloinfo" value="Aggiornamento della procedura ad ERP effettuato." />						 
				</c:when>
				<c:when test='${tipoWSERP eq "ATAC"}'>
					<c:choose>
						<c:when test='${str_esito eq "PIVA_OK"}'>
							<c:set var="str_esito_message" value="Procedere alla creazione dell'ordine?" />
							<c:set var="isFornitoreERP" value="true" />
						</c:when>
						<c:when test='${str_esito eq "CF_OK"}'>
							<c:set var="str_esito_message" value="CF_OK procedo?" />
							<c:set var="isFornitoreERP" value="true" />
						</c:when>
						<c:when test='${str_esito eq "PIVA_DUPLICATA"}'>
							<c:set var="str_esito_message" value="Attenzione: il fornitore non identificabile per PIVA duplicata. Verificare le anagrafiche SAP e sanare la situazione per poter procedere alla creazione." />
							<c:set var="isFornitoreERP" value="false" />
						</c:when>
						<c:when test='${str_esito eq "CF_DUPLICATO"}'>
							<c:set var="str_esito_message" value="Attenzione: il fornitore non identificabile per CF duplicato. Verificare le anagrafiche SAP e sanare la situazione per poter procedere alla creazione." />
							<c:set var="isFornitoreERP" value="false" />
						</c:when>
						<c:when test='${(str_esito eq "PIVA_KO") || (str_esito eq "CF_KO") || (str_esito eq "PIVA_KO_E_CF_KO")}'>
							<c:set var="str_esito_message" value="Attenzione: il fornitore non esiste. Creare l'anagrafica in SAP per poter procedere alla creazione dell'ODA." />
							<c:set var="isFornitoreERP" value="false" />
						</c:when>
						<c:otherwise>
							<c:set var="str_esito_message" value="Errore durante la lettura dei dati del fornitore" />
							<c:set var="isFornitoreERP" value="false" />
						</c:otherwise>
					</c:choose>
				</c:when>	
				<c:otherwise>
					<c:set var="str_calcolowarning" value="L'aggiornamento delle RdA con i dati di aggiudicazione ha presentato degli errori/warning." />
					<c:set var="str_preinvio" value="Vengono aggiornate le RdA in gara
						 a seguito del calcolo dell'aggiudicazione." />
					
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test='${!(tipoWSERP eq "RAIWAY") && (empty ditta or ditta eq "")}'>
					<gene:campoScheda>
						<td colSpan="2"><br>La gara non risulta aggiudicata in via definitiva 
							<br>&nbsp;<br>
						</td>
					</gene:campoScheda>
				</c:when>
				<c:when test='${RISULTATO eq "CALCOLOWARNING"}'>
					<gene:campoScheda>
						<td colSpan="2"><br>${str_calcolowarning} 
							<br>&nbsp;<br>
						</td>
					</gene:campoScheda>
				</c:when>
				<c:when test='${RISULTATO eq "CALCOLOINFO"}'>
					<gene:campoScheda>
						<td colSpan="2"><br>${str_calcoloinfo} 
							<br>&nbsp;<br>
						</td>
					</gene:campoScheda>
				</c:when>
				<c:when test='${tipoWSERP eq "ATAC"}'>
					<gene:campoScheda>
						<td colSpan="2"><br>${str_esito_message} 
							<br>&nbsp;<br>
						</td>
					</gene:campoScheda>
				</c:when>
				<c:otherwise>
					<gene:campoScheda>
						<td colSpan="2"><br>${str_preinvio}<br><br>
						Confermi l'operazione?<br><br>
						</td>
					</gene:campoScheda>
				</c:otherwise>
			</c:choose>


				
			
			</c:otherwise>
		</c:choose>

		

			<gene:campoScheda campo="CODGAR1" visibile="false" />
			<gene:campoScheda campo="NGARA" visibile="false" />
			<gene:campoScheda campo="CODCIG" visibile="false" />
			<gene:campoScheda campo="CODCIGAQ" entita="TORN" where="TORN.CODGAR=GARE.CODGAR1" visibile="false" />
			<gene:campoScheda campo="DITTA" visibile="false" />
			<gene:campoScheda campo="IMPAPP" visibile="false" />
			<gene:campoScheda campo="IAGGIU" visibile="false" />
			<gene:campoScheda campo="RIBAGG" visibile="false" />
			<gene:campoScheda campo="RIBOEPV" visibile="false" />
			<gene:campoScheda campo="MODLICG" visibile="false" />
			<gene:campoScheda campo="NOT_GAR" visibile="false" />
			<gene:campoScheda campo="CUPPRG" visibile="false" />
			<gene:campoScheda campo="DAATTO" visibile="false" />
			<gene:campoScheda campo="NREPAT" visibile="false" />
			
			<gene:campoScheda campo="IDFORNITORE" campoFittizio="true" visibile="false" definizione="T20" value="${idFornitore}"/>
			<gene:campoScheda campo="GRUPPO_CONTI" campoFittizio="true" visibile="false" definizione="T20" value=""/>
			<gene:campoScheda campo="COND_PAG" campoFittizio="true" visibile="false" definizione="T20" value=""/>
			<gene:campoScheda campo="MODO_PAG" campoFittizio="true" visibile="false" definizione="T20" value=""/>
			<gene:campoScheda campo="OGG_GARA" campoFittizio="true" visibile="false" definizione="T20" value=""/>
			<gene:campoScheda campo="TIPO_CONTR" campoFittizio="true" visibile="false" definizione="T20" value=""/>
			<gene:campoScheda campo="TIPO_FLUSSO" campoFittizio="true" visibile="false" definizione="T20" value=""/>
			<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica }" />
			<input id="servizio" type="hidden" value="WSERP" />
			<input id="idFornitore" type="hidden" value="" />
			<input id="gruppoConti" type="hidden" value="" />
			<input id="condPag" type="hidden" value="" />
			<input id="modoPag" type="hidden" value="" />
			<input id="oggGara" type="hidden" value="" />
			<input id="tipoContr" type="hidden" value="" />
										
			<c:choose>
				<c:when test='${RISULTATO eq "CALCOLOWARNING"}'>
				<gene:campoScheda>
					<td class="comandi-dettaglio" colSpan="2">
						<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</gene:campoScheda>
				</c:when>
				<c:when test='${RISULTATO eq "CALCOLOINFO"}'>
					<gene:campoScheda>
						<td class="comandi-dettaglio" colSpan="2">
							<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
					</gene:campoScheda>
				</c:when>				
				<c:otherwise>
				<gene:campoScheda>
					<td class="comandi-dettaglio" colSpan="2">
						<c:set var="visPulsanteConferma" value="false" />
						<c:choose>
							<c:when test='${controlloSuperato eq "true" and tipoWSERP ne "ATAC"}'>
								<c:set var="visPulsanteConferma" value="true" />
							</c:when>
							<c:when test='${isFornitoreERP eq "true"}'>
								<c:set var="visPulsanteConferma" value="true" />
							</c:when>					 
						</c:choose>
						<c:if test='${ (!empty ditta and ditta ne "") and (visPulsanteConferma eq "true") }'>
							<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">
						</c:if>
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</gene:campoScheda>
					<input type="hidden" id="elencoDitteSelezionate" name="elencoDitteSelezionate" value="${elencoDitteSelezionate }" />
					<input type="hidden" id="aqoper" name="aqoper" value="${aqoper }" />
				</c:otherwise>
			</c:choose>
			
		</gene:formScheda>
		
	</gene:redefineInsert>
	
	<gene:javaScript> 
	
		document.forms[0].jspPathTo.value="gare/gare/gare-popup-comunicaEsitoRda.jsp";
	
	    function annulla(){
	    	opener.historyReload();
			window.close();
		}
		
		function conferma(){
			var idFornitore = $("#idFornitore").val();
			document.forms[0].idFornitore.value=$("#idFornitore").val();
			setValue("IDFORNITORE", idFornitore);
			var gruppoConti = $("#p1").val();
			document.forms[0].gruppoConti.value=$("#p1").val();
			setValue("GRUPPO_CONTI", gruppoConti);
			var condPag = $("#condpag").val();
			document.forms[0].condPag.value=$("#condPag").val();
			setValue("COND_PAG", condPag);
			var modoPag = $("#p3").val();
			document.forms[0].modoPag.value=$("#p3").val();
			setValue("MODO_PAG", modoPag);
			var oggGara = $("#defcig").val();
			document.forms[0].oggGara.value=$("#oggGara").val();
			setValue("OGG_GARA", oggGara);
			var tipoContr = $("#p4").val();
			document.forms[0].tipoContr.value=$("#tipoContr").val();
			setValue("TIPO_CONTR", tipoContr);
						
			
			var isConferma ="true";
			
			<c:choose>
				<c:when test='${tipoWSERP eq "TPER"}'>
					if(idFornitore== null || idFornitore==''){
						if(condPag== null || condPag==''){
							isConferma ="false";
						 	alert('Valorizzare le condizioni di pagamento!');
						}
					}
					
					if(oggGara== null || oggGara==''){
						isConferma ="false";
					 	alert('Valorizzare la definizione del cig!');
					}
				</c:when>
				<c:when test='${tipoWSERP eq "AVM"}'>
					if(idFornitore== null || idFornitore==''){
						if(gruppoConti== null || gruppoConti==''){
							isConferma ="false";
						 	alert('Valorizzare il gruppo conti!');
						}
					}
				</c:when>
				<c:when test='${tipoWSERP eq "CAV"}'>
					if(idFornitore== null || idFornitore==''){
						if(gruppoConti== null || gruppoConti==''){
							isConferma ="false";
						 	alert('Valorizzare il gruppo conti!');
						}
						if(tipoContr== null || tipoContr==''){
							isConferma ="false";
						 	alert('Valorizzare il tipo contratto!');
						}
					}
				</c:when>
				<c:otherwise>
				</c:otherwise>
			</c:choose>
			
			if(isConferma =="true"){
				schedaConferma();
			}
		}
	


	$("#tabCreaFornitore").css("border-top","#A0AABA 1px solid");
	
	$("#tabCreaCig").css("border-top","#A0AABA 1px solid");
	$("#tabCreaCig").css("border-bottom","#A0AABA 1px solid");
	
	$("#msgctrl").css("border-bottom","#FFFFFF");

	<c:if test='${tipoWSERP eq "AVM"}'>
	_popolaWSERPTabellato("GRUPPO_CONTI","p1");
	</c:if>
	
	<c:if test='${tipoWSERP eq "CAV"}'>
	_popolaWSERPTabellato("CAV01","p1");
	_popolaWSERPTabellato("CAV02","condpag");
	_popolaWSERPTabellato("CAV03","p3");
	_popolaWSERPTabellato("CAV04","p4");
		<c:if test='${!empty tipoContrattoRda}'>
			$('#p4 option[value="${tipoContrattoRda}"').attr('selected','selected');
		</c:if>
	</c:if>
	

	<c:if test='${tipoWSERP eq "TPER"}'>
		_popolaWSERPListaCondPag("condpag")
		//_popolaWSERPDatoFisso("TIPO_FLUSSO","TIPO_FLUSSO");
	</c:if>
	
	
	</gene:javaScript>
	
	
	
	
</gene:template>

</div>

</c:otherwise>
</c:choose>