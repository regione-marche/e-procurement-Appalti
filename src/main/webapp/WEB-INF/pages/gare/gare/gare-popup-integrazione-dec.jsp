<%
/*
 * Created on: 04-apr-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Scheda dei subbalti del lavoro */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${!empty RISULTATO}'>
		<c:set var="ngara" value='${NGARA}' />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${param.ngara}" />
	</c:otherwise>
</c:choose>
<c:set var="ngara" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select ngara from GARE where ngara=?", ngara)}'/>
<c:set var="codiceGara" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select codgar1 from GARE where ngara=?", ngara)}'/>

<c:set var="isAttivoModuloANACForm" value="${gene:callFunctionN('it.eldasoft.sil.pg.tags.funzioni.GetSeAttivoModuloANACFormFunction', pageContext, codiceGara)}"/>
<c:if test="${isAttivoModuloANACForm eq 'true'}">
	<c:set var="presenteAD5" value="${gene:callFunctionN('it.eldasoft.sil.pg.tags.funzioni.GetCountSchedeFunction', pageContext, 'inviataAD5', codiceGara) > 0}" />
</c:if>

<c:choose>
	<c:when test='${not empty param.ncont}'>
		<c:set var="ncont" value="${param.ncont}" />
	</c:when>
	<c:otherwise>
		<c:set var="ncont" value="${ncont}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codimp}'>
		<c:set var="codimp" value="${param.codimp}" />
	</c:when>
	<c:otherwise>
		<c:set var="codimp" value="${codimp}" />
	</c:otherwise>
</c:choose>

<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "PERI", "CODLAV")}'/>
<c:set var="isLavoroAssociato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.ControlloAssociazioneGaraLavoroFunction", pageContext, ngara)}'/>
<c:set var="integrazioneDec" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.integrazione.dec")}'/>

<c:set var="functionId" value="skip" />
<c:set var="parametriWhere" value=""/>
<c:if test="${!empty sessionScope.uffint}">
	<c:set var="filtroAbilitato" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "integrazioneLFS.filtroUffint")}'/>
	<c:if test="${filtroAbilitato eq '1' }">
		<c:set var="cenint" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetFiltroUffintDECFunction", pageContext, ngara)}'/>
		<c:if test="${cenint ne 'NO_FILTRO_UFFINT' }">
			<c:if test="${empty cenint }">
				<c:set var="cenint" value="${sessionScope.uffint}"/>
			</c:if>
			<c:set var="functionId" value="default" />
			<c:set var="parametriWhere" value="T:${cenint}"/>
		</c:if>
	</c:if>
</c:if>

<c:choose>
	<c:when test="${integrazioneDec ne '1'}">
		<c:set var="nomeIntegrazione" value="Monitoraggio OO.PP." />
	</c:when>
	<c:otherwise>
		<c:set var="nomeIntegrazione" value="Direzione Esecuzione Contratti" />
	</c:otherwise>
</c:choose>

<c:set var="inviateSchedeAggiudicazione" value='${gene:callFunctionN("it.eldasoft.sil.pg.tags.funzioni.npa.GetSeInviateSchedeAggiudicazioneFunction", pageContext, ngara, codimp)}'/>

<gene:template file="popup-template.jsp">
	
	<gene:setString name="titoloMaschera" value='Esecuzione contratto'/>	
	<gene:redefineInsert name="documentiAzioni"></gene:redefineInsert>
	
	<c:set var="singoloAppalto" value="2" scope="request" />

	<gene:redefineInsert name="corpo">
		<c:choose>
			<c:when test='${RISULTATO eq "CREAZIONEESEGUITA" || RISULTATO eq "AGGIORNAMENTOESEGUITO"}'>
				<c:set var="modo" value="APRI" scope="request" />	
			</c:when>
			<c:otherwise>
				<c:set var="modo" value="MODIFICA" scope="request" />
			</c:otherwise>
		</c:choose>
	
		<gene:formScheda entita="GARE" where="GARE.NGARA = '${ngara}'" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGAREDEC" >

			<c:set var="creaLavoriSingolo" value="true" />
			<c:set var="creaLavoriAssociato" value="${singoloAppalto eq '2'}" />
			<c:set var="creaLavori" value="${creaLavoriSingolo || creaLavoriAssociato}" />
			<c:set var="creaFornitureServiziSingolo" value="${integrazioneDec eq '1' || true}" />
			<c:set var="creaFornitureServiziAssociato" value="${singoloAppalto eq '2'}" />
			<c:set var="creaFornitureServizi" value="${creaFornitureServiziSingolo || creaFornitureServiziAssociato}" />	

			<gene:campoScheda campo="NGARA" visibile="false"/>
			<gene:campoScheda campo="CODGAR1" visibile="false" />
			<gene:campoScheda campo="CLAVOR" visibile="false" />
			<gene:campoScheda campo="NUMERA" visibile="false" />			
			<gene:campoScheda campo="NOT_GAR" visibile="false"/>
			<gene:campoScheda campo="NOMIMA" visibile="false" />
			<gene:campoScheda entita="V_GARE_TORN" campo="GENERE" modificabile="false" where="GARE.CODGAR1 = V_GARE_TORN.CODICE" visibile="false"/>
			<gene:campoScheda entita="TORN" campo="DESTOR" where="TORN.CODGAR = GARE.CODGAR1" visibile="false"/>
			<gene:campoScheda entita="TORN" campo="MODCONT" where="TORN.CODGAR = GARE.CODGAR1" visibile="false"/>

			<gene:campoScheda visibile="${isLavoroAssociato eq '1' && empty RISULTATO}">
				<td colspan="2">
					<div style="margin:6 0 6 0;">
						<br>
						Risulta gi&agrave; associato il contratto <b>${datiRiga.GARE_CLAVOR}/${datiRiga.GARE_NUMERA}</b> in ${nomeIntegrazione}.
						<br><br>
						<c:if test="${datiRettificati}">
							<font color='#0000FF'>
							<b>ATTENZIONE:</b> I dati del contratto sono stati rettificati in ${nomeIntegrazione} e procedendo verranno sovrascritti.
							</font>
							<br><br>
						</c:if>						
						Aggiornare con i dati correnti ?
						<br><br>
						<c:if test="${not inviateSchedeAggiudicazione && isAttivoModuloANACForm eq 'true'}">
							<div style="color: blue;margin:6 0 6 0;">
								<b>ATTENZIONE:</b> Non è stata inviata ad ANAC la scheda relativa alla fase di aggiudicazione.<br><br>
							</div>
						</c:if>
					</div>
				</td>
			</gene:campoScheda>
		
			<gene:campoScheda visibile="${RISULTATO eq 'CREAZIONEESEGUITA'}">
				<td colspan="2">
					<div style="margin:6 0 6 0;">
						<br>
						La creazione del nuovo contratto con codice <b>${datiRiga.GARE_CLAVOR}/${datiRiga.GARE_NUMERA}</b> &egrave; terminata con successo.
						<br><br>
					</div>
				</td>
			</gene:campoScheda>
		
			<gene:campoScheda visibile="${RISULTATO eq 'AGGIORNAMENTOESEGUITO'}">
				<td colspan="2">
					<div style="margin:6 0 6 0;">
						<br>
						L'aggiornamento del contratto <b>${datiRiga.GARE_CLAVOR}/${datiRiga.GARE_NUMERA}</b> in ${nomeIntegrazione} &egrave; terminato con successo.
						<br><br>
					</div>
				</td>
			</gene:campoScheda>

			<gene:gruppoCampi visibile="${empty RISULTATO}">
				<gene:campoScheda visibile="${isLavoroAssociato ne '1'}">
					<td colspan="2">
						<div style="margin:6 0 6 0;">
							<br>
							Creare un nuovo contratto in ${nomeIntegrazione} ?
							<br><br>
						</div>

						<c:if test="${!(inviateSchedeAggiudicazione eq 'true') && isAttivoModuloANACForm eq 'true'}">
							<div style="color: blue;margin:6 0 6 0;">
								<b>ATTENZIONE:</b> Non è stata inviata ad ANAC la scheda relativa alla fase di aggiudicazione.<br><br>
							</div>
						</c:if>
					</td>
				</gene:campoScheda>

				<c:if test="${isLavoroAssociato eq '1'}">
					<c:set var="sql" value="select notapp from appa where codlav=? and nappal=?"/>
					<c:set var="APPA_NOTAPP" value="${gene:callFunctionN('it.eldasoft.gene.tags.functions.GetSafeDataFromDB', pageContext, sql, datiRiga.GARE_CLAVOR, datiRiga.GARE_NUMERA)}"/>
					<c:set var="sql" value="select affidamdir from appa where codlav=? and nappal=?"/>
					<c:set var="APPA_AFFIDAMDIR" value="${gene:callFunctionN('it.eldasoft.gene.tags.functions.GetSafeDataFromDB', pageContext, sql, datiRiga.GARE_CLAVOR, datiRiga.GARE_NUMERA)}"/>
					<c:set var="sql" value="select tiplav from peri where codlav=?"/>
					<c:set var="PERI_TIPLAV" value="${gene:callFunctionN('it.eldasoft.gene.tags.functions.GetSafeDataFromDB', pageContext, sql, datiRiga.GARE_CLAVOR)}"/>
				</c:if>

				<gene:campoScheda title="Tipo contratto" entita="TORN" campo="TIPGEN" visibile="true" modificabile="false"/>			
				<gene:campoScheda title="Codice contratto" campo="APPA_CODLAV" obbligatorio="true" visibile='${isCodificaAutomatica eq "false"}' value="${isLavoroAssociato eq '1'? (datiRiga.GARE_CLAVOR).concat('/').concat(datiRiga.GARE_NUMERA)  : ''}" modificabile="${isLavoroAssociato ne '1'}" campoFittizio="true" definizione="T20;0"/>
				<gene:campoScheda title="N. appalto" campo="APPA_NAPPAL" visibile="false" campoFittizio="true" definizione="N7;0"/>
				<gene:campoScheda title="Tipo contratto" campo="APPA_TIPLAVG" modificabile="false" campoFittizio="true" definizione="N7;0;A1007" visibile="false"/>
				<gene:campoScheda title="Descrizione contratto" campo="APPA_NOTAPP" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote" value="${isLavoroAssociato eq '1'? APPA_NOTAPP : ''}" modificabile="${isLavoroAssociato ne '1'}" campoFittizio="true" definizione="T2000;0"/>
				<gene:campoScheda title="Tipo iniziativa" campo="PERI_TIPLAV" obbligatorio="true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPLAV" value="${isLavoroAssociato eq '1'? PERI_TIPLAV : ''}" modificabile="${isLavoroAssociato ne '1'}" campoFittizio="true" definizione="T100;0;A20s2"/>
				<gene:campoScheda title="Affidamento diretto con scheda AD5?" campo="APPA_AFFIDAMDIR" obbligatorio="true" visibile="${fn:startsWith(codiceGara, '$')}" value="${isLavoroAssociato eq '1'? APPA_AFFIDAMDIR : 0}" campoFittizio="true" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNo" definizione="T1;0"/>
				<gene:campoScheda title="" campo="ISATTIVOMODULOANACFORM" visibile="false" value="${isAttivoModuloANACForm}" campoFittizio="true" definizione="T10;0"/>

				<gene:campoScheda visibile="${(false && integrazioneDec ne '1' && isLavoroAssociato ne '1' && datiRiga.TORN_TIPGEN ne '1')}">
					<td colspan="2">
						<br>
						<div style="margin:6 0 6 0;">
							L'applicativo &egrave; configurato per l'integrazione con Monitoraggio OO.PP.: &egrave; 
							possibile creare nuovi contratti per 'Forniture' o 'Servizi' solamente associandoli ad una commessa esistente.
							<br>
						</div>
					</td>
				</gene:campoScheda>

				<c:set var="isVisibileLavoroEsistente" value="${isLavoroAssociato ne '1' and ((creaLavori eq 'true' && datiRiga.TORN_TIPGEN eq '1') || (creaFornitureServizi eq 'true' && datiRiga.TORN_TIPGEN ne '1'))}"/>
				<gene:campoScheda title="Collegare a una commessa esistente ?" 
					campo="LAVORO_ESISTENTE" 
					campoFittizio="true" 
					definizione="T1;0"
					visibile="${isVisibileLavoroEsistente}" >
					<c:if test="${isVisibileLavoroEsistente}">
						<gene:addValue value="2" descr="No" />
					</c:if>
					<c:if test="${isVisibileLavoroEsistente}">
						<gene:addValue value="1" descr="Si"/>
					</c:if>
				</gene:campoScheda>

				<gene:archivio titolo="commesse"
					inseribile="false"
					lista="lavo/appa/appa-lista-dec-peri-popup.jsp" 
					scheda="" 
					schedaPopUp="" 
					campi="PERI.CODLAV;PERI.TITSIL" 
					functionId="${functionId}"
					parametriWhere="${parametriWhere}"
					chiave="" >
					<gene:campoScheda title="Codice commessa" campo="PERI_CODLAV" obbligatorio="true" visibile="${isLavoroAssociato ne '1'}" campoFittizio="true" definizione="T20;0"/>
					<gene:campoScheda title="Descrizione" campo="PERI_TITSIL" campoFittizio="true" visibile="${isLavoroAssociato ne '1'}" definizione="T2000;0" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote"/>
				</gene:archivio>
			
			</gene:gruppoCampi>
			
			<input type="hidden" name="ncont" id="ncont" value="${ncont}" />

			<c:if test="${isLavoroAssociato ne '1'}">
				<gene:fnJavaScriptScheda funzione="gestioneLAVORO_ESISTENTE('#LAVORO_ESISTENTE#')" elencocampi="LAVORO_ESISTENTE" esegui="true" />
				<gene:fnJavaScriptScheda funzione="gestioneAPPA_NOTAPP('#V_GARE_TORN_GENERE#')" elencocampi="V_GARE_TORN_GENERE;GARE_NOT_GAR;TORN_DESTOR;GARE_NOMIMA" esegui="true" />
			</c:if>
				
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
					<c:choose>
						<c:when test="${empty RISULTATO}">
							<c:choose>
								<c:when test="${isLavoroAssociato eq '1'}">
									<INPUT type="button" class="bottone-azione" value="Aggiorna contratto" title="Aggiorna contratto" onclick="javascript:schedaConferma();">
									<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="window.close();">	
								</c:when>
								<c:otherwise>
									<c:if test="${(creaLavori eq 'true' && datiRiga.TORN_TIPGEN eq '1') || (creaFornitureServizi eq 'true' && datiRiga.TORN_TIPGEN ne '1') }">
										<INPUT type="button" class="bottone-azione" value="Crea contratto" title="Crea contratto" onclick="javascript:schedaConferma();">
									</c:if>
									<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="window.close();">
								</c:otherwise>
							</c:choose>						
						</c:when>
						<c:otherwise>
							<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="window.close();">
						</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</gene:campoScheda>
			<input type="hidden" name="codimp" value="${codimp}">
		</gene:formScheda>
	</gene:redefineInsert>
	
	<gene:javaScript>

		document.forms[0].jspPathTo.value = "/WEB-INF/pages/gare/gare/gare-popup-integrazione-dec.jsp";
		
		<c:if test='${RISULTATO eq "CREAZIONEESEGUITA"}'>
	    	<c:choose>
	    		<c:when test="${datiRiga.TORN_MODCONT eq '1' }">
	    			window.opener.historyReload();
	    		</c:when>
	    		<c:otherwise>
	    			window.opener.selezionaPagina(window.opener.document.forms[0].activePage.value);
	    		</c:otherwise>
	    	</c:choose>
	    	
		</c:if>
	
		$('#jsPopUpGARE_NOT_GAR').hide();
		$('#jsPopUpGARE_NOMIMA').hide();
		$('#jsPopUpTORN_DESTOR').hide();
		$('#jsPopUpTORN_TIPGEN').hide();
	
		function gestioneLAVORO_ESISTENTE(lavoro_esistente){
			document.getElementById("rowPERI_CODLAV").style.display = (lavoro_esistente == '1' ? '':'none');
			document.getElementById("rowPERI_TITSIL").style.display = (lavoro_esistente == '1' ? '':'none');
			document.getElementById("rowPERI_TIPLAV").style.display = (lavoro_esistente == '1' ? 'none':'');
			setValue("PERI_CODLAV","");
			setValue("PERI_TITSIL","");
			setValue("PERI_TIPLAV","");
			
			<c:if test='${isCodificaAutomatica eq "false"}'>
				document.getElementById("rowAPPA_CODLAV").style.display = (lavoro_esistente == '2' ? '':'none');
				setValue("APPA_CODLAV","");
			</c:if>
		}
		
		function gestioneAPPA_NOTAPP(genere) {
			var notapp = "";
			var not_gar = getValue("GARE_NOT_GAR");
			var destor = getValue("TORN_DESTOR");
			var nomima = getValue("GARE_NOMIMA");
			if (genere == "3") {
				notapp += "Gara divisa in lotti: " + destor;
			} else {
				notapp += not_gar;
			}
			if (nomima != "") {
				notapp += " [" + nomima + "]";
			}
			setValue("APPA_NOTAPP",notapp);
		}

		function disabilitaAFFIDAMDIR() {
			$('#APPA_AFFIDAMDIR').css('border-colojsPopUpr','#A3A6FF');
			$('#APPA_AFFIDAMDIR').css('border-width','1px');
			$('#APPA_AFFIDAMDIR').css('background-color','#E0E0E0');
			$('#APPA_AFFIDAMDIR').attr("disabled", true);
		}

		$(document).ready(function() {
			if (${isAttivoModuloANACForm eq 'true'}) {
				if (${presenteAD5 eq 'true'}) {
					$('#APPA_AFFIDAMDIR').val(1);
				} else {
					$('#APPA_AFFIDAMDIR').val(2);
				}
				disabilitaAFFIDAMDIR();
			}
		});

		var schedaConfermaOld=schedaConferma;
		schedaConferma=function(){
			$('#APPA_AFFIDAMDIR').removeAttr("disabled");
			schedaConfermaOld();
		}

	</gene:javaScript>
	
</gene:template>

