<%
/*
 * Created on: 27/06/2017
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

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value='${param.ngara}' />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.necvan}'>
		<c:set var="necvan" value='${param.necvan}' />
	</c:when>
	<c:otherwise>
		<c:set var="necvan" value="${necvan}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.bloccoDati}'>
		<c:set var="bloccoDati" value='${param.bloccoDati}' />
	</c:when>
	<c:otherwise>
		<c:set var="bloccoDati" value="${bloccoDati}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.sezionitec}'>
		<c:set var="sezionitec" value='${param.sezionitec}' />
	</c:when>
	<c:otherwise>
		<c:set var="sezionitec" value="${sezionitec}" />
	</c:otherwise>
</c:choose>

<c:set var="MaxNumValori" value="5" />

<c:set var="codiceGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, ngara)}' />

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="G1CRIDEF-scheda">
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
	</gene:redefineInsert>
	<gene:setString name="titoloMaschera" value='Dettaglio assegnazione punteggio criterio di valutazione della gara ${ngara }'/>
	<gene:redefineInsert name="corpo">
		<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
		<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
		<gene:formScheda entita="G1CRIDEF" gestisciProtezioni="true"  gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreCriteriValutazione">
			
			<gene:gruppoCampi idProtezioni="DATGEN">
				<gene:campoScheda>
					<td colspan="2"><b>Dati generali</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="ID" visibile="false" modificabile="false"/>
				<c:set var="chiave" value='${gene:getValCampo(key, "ID")}' />
				<gene:campoScheda campo="NECVAN" visibile="false" value='${necvan}'/>
				<gene:campoScheda campo="NGARA" visibile="false" value='${ngara}'/>
				
				<gene:campoScheda campo="DESPAR" entita="GOEV" where="GOEV.NGARA='${ngara}' and GOEV.NECVAN=${necvan}" modificabile="false" title="Descrizione criterio o sub-criterio"/>
				<gene:campoScheda campo="MAXPUN" entita="GOEV" where="GOEV.NGARA='${ngara}' and GOEV.NECVAN=${necvan}" modificabile="false"/>
				<gene:campoScheda campo="TIPPAR" entita="GOEV" where="GOEV.NGARA='${ngara}' and GOEV.NECVAN=${necvan}" modificabile="false"/>
				<gene:campoScheda campo="OFFTEL" entita="TORN" where="TORN.CODGAR='${codiceGara}'" modificabile="false" visibile="false"/>
				<gene:campoScheda campo="CODGAR" entita="TORN" visibile="false" where="TORN.CODGAR='${codiceGara}'"/>
				
				<c:choose>
					<c:when test='${datiRiga.TORN_OFFTEL eq 2 or datiRiga.TORN_OFFTEL eq 3 or empty datiRiga.TORN_OFFTEL}'>
						<gene:campoScheda campo="FORMATO" modificabile="false" defaultValue="100"  obbligatorio="true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoCriterioFormato"/>
					</c:when>
					<c:otherwise>
						<gene:campoScheda campo="FORMATO" modificabile="true" obbligatorio="true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoCriterioFormato" >
							<gene:checkCampoScheda funzione='validazioneDaFormato("##")' obbligatorio="true" messaggio="Non è possibile impostare la modalita' di assegnazione del punteggio 'automatica' quando il formato del valore offerto è 'testo' o 'data' oppure non è definito" onsubmit="false"/>
						</gene:campoScheda>
					</c:otherwise>
				</c:choose>
				<gene:campoScheda campo="NUMDECI" obbligatorio="true">
					<gene:checkCampoScheda funzione='"##" < 6 && "##" > 0' obbligatorio="true" messaggio="Il valore specificato deve essere compreso tra 0 e 5." onsubmit="false"/>
				</gene:campoScheda>
				<gene:campoScheda campo="SICINC" entita="GARE" where="GARE.NGARA = '${ngara}'" modificabile='false'/>
				<gene:campoScheda campo="ULTDETLIC" entita="GARE1" where="GARE1.NGARA = '${ngara}'" modificabile='false' />
				
				<gene:campoScheda campo="MODPUNTI" modificabile="true" obbligatorio="true">
					<gene:checkCampoScheda funzione='validazioneDaModpunti("##")' obbligatorio="true" messaggio="Non è possibile impostare la modalita' di assegnazione del punteggio 'automatica' quando il formato del valore offerto è 'testo' o 'data' oppure non è definito" onsubmit="false"/>
				</gene:campoScheda>
				
				<gene:campoScheda campo="MODMANU" modificabile="true" obbligatorio="true"/>
				
				<gene:campoScheda campo="FORMULA" obbligatorio="true">
					<span style="float: right;">
					<a href="javascript:apriDocumento('${pageContext.request.contextPath}/doc/OEPV_formuleCriteriValutazione.pdf');" title="Consulta manuale" style="color:#002E82;">
					 <img width="16" height="16" title="Consulta manuale" alt="Consulta manuale" src="${pageContext.request.contextPath}/img/consultazioneManuale.png"/> Consulta manuale
					</a>
					</span>
				</gene:campoScheda>
				<gene:campoScheda campo="ESPONENTE" modificabile="true" obbligatorio="true"/>
				<gene:campoScheda campo="DESCRI" />

				<gene:fnJavaScriptScheda funzione='formatoOnChangeInit("#G1CRIDEF_FORMATO#")' elencocampi='G1CRIDEF_FORMATO' esegui="true" />
				<gene:fnJavaScriptScheda funzione='formatoOnChange("#G1CRIDEF_FORMATO#")' elencocampi='G1CRIDEF_FORMATO' esegui="false" />
				<gene:fnJavaScriptScheda funzione='formulaOnChange("#G1CRIDEF_FORMULA#")' elencocampi='G1CRIDEF_FORMULA' esegui="false" />
				<gene:fnJavaScriptScheda funzione='modpuntiOnChange("#G1CRIDEF_MODPUNTI#")' elencocampi='G1CRIDEF_MODPUNTI' esegui="true" />
				<gene:fnJavaScriptScheda funzione='setModManu("#G1CRIDEF_MODPUNTI#")' elencocampi='G1CRIDEF_MODPUNTI' esegui="true" />
				<gene:fnJavaScriptScheda funzione='gestioneSezioneValoriAmmessi("#G1CRIDEF_FORMULA#","#G1CRIDEF_FORMATO#")' elencocampi='G1CRIDEF_FORMULA;G1CRIDEF_FORMATO' esegui="true" />
				<gene:fnJavaScriptScheda funzione='gestioneEsponente("#G1CRIDEF_FORMULA#")' elencocampi='G1CRIDEF_FORMULA' esegui="true" />
			</gene:gruppoCampi>
			
			<gene:gruppoCampi>
				<gene:campoScheda nome="titoloSezioneListaValori">
					<td colspan="2"><b>Valori ammessi </b></td>
				</gene:campoScheda>
			</gene:gruppoCampi>
			
			<c:set var="result" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoriAmmessiFunction", pageContext,chiave)}' />
			<gene:campoScheda addTr="false" visibile='true'>
			<tr>
				<td colspan="2">
				<table id="tabella1" class="griglia" >
					</gene:campoScheda>	
						<gene:gruppoCampi>
								<jsp:include page="/WEB-INF/pages/commons/interno-sezione-multipla-singola-riga.jsp" >
									<jsp:param name="entita" value='G1CRIREG'/>
									<jsp:param name="chiave" value='${chiave}'/>
									<jsp:param name="nomeAttributoLista" value='datiG1CRIREG' />
									<jsp:param name="idProtezioni" value="G1CRIREG" />
									<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/g1cridef/riga-sez-valori-ammessi.jsp"/>
									<jsp:param name="arrayCampi" value="'G1CRIREG_PUNTUALE_', 'G1CRIREG_VALMIN_', 'G1CRIREG_VALMAX_', 'G1CRIREG_COEFFI_', 'G1CRIREG_IDCRIDEF', 'G1CRIREG_ID'"/>
									<jsp:param name="titoloSezione" value="valore" />
									<jsp:param name="titoloNuovaSezione" value="Nuovo valore" />
									<jsp:param name="descEntitaVociLink" value="valore" />
									<jsp:param name="msgRaggiuntoMax" value="i valori"/>
									<jsp:param name="sezioneListaVuota" value="false"/>
									<jsp:param name="sezioneEliminabile" value="true"/>
									<jsp:param name="sezioneInseribile" value="true"/>
									<jsp:param name="numMaxDettagliInseribili" value="${MaxNumValori}"/>
									<jsp:param name="datiModificabili" value="true"/>
								</jsp:include>
						</gene:gruppoCampi>
					<gene:campoScheda addTr="false" visibile='${gene:checkProt(pageContext, stringaControllo) }'>
				</table>
				</td>
			</tr>
			</gene:campoScheda>
			
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
				<c:if test='${ bloccoDati}'>
					<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
				</c:if>
				<td class="comandi-dettaglio" colSpan="2">
					<c:choose>
						<c:when test='${modo eq "MODIFICA"}'>
							<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
						</c:when>
						<c:otherwise>
							<INPUT type="button"  class="bottone-azione" value='Torna a lista criteri di valutazione' title='Torna a lista criteri di valutazione' onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;&nbsp;
							<c:if test='${(autorizzatoModifiche ne "2") and !(bloccoDati) and gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
							</c:if>
						</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</gene:campoScheda>
			
			<input type="hidden" name="ngara" value="${ngara }"/>
			<input type="hidden" name="necvan" value="${necvan }"/>
			<input type="hidden" name="bloccoDati" value="${bloccoDati }"/>
			<input type="hidden" name="sezionitec" value="${sezionitec }"/>
		</gene:formScheda>

	</gene:redefineInsert>
	<gene:javaScript>
			
			<c:if test='${datiRiga.TORN_OFFTEL eq 2 or datiRiga.TORN_OFFTEL eq 3 or empty datiRiga.TORN_OFFTEL}'>
				$("#G1CRIDEF_MODPUNTI option[value='2']").remove();
			</c:if>
			
			var gFormula = $("#G1CRIDEF_FORMULA").val();
			var gFormato = $("#G1CRIDEF_FORMATO").val();
			
			var righeVisualizzate = eval("lastIdG1CRIREGVisualizzata");
			var righeInseribili = righeVisualizzate+5;
			var MaxNumValori = righeVisualizzate+5;
			var countdownrighe = eval("lastIdG1CRIREGVisualizzata");
			var visualizzataSezioneAmmessi;
			var visualizzatoPuntuale;
			var offtel = $("OFFTEL_TORN").val();
			
			$('#rowLinkAddG1CRIREG td:eq(1)').attr("colspan","6");
			$('#rowLinkAddG1CRIREG td:eq(1)').css("width","auto");
			$('#rowMsgLastG1CRIREG td').attr("colspan","8");
				
			function formatoOnChangeInit(formato) {
				gFormula = $("#G1CRIDEF_FORMULA").val();
				showObj("rowLinkAddG1CRIREG", false);
				gestoreCampoFormula(formato);
				righeInseribili = 1;
				if (formato == 1 || formato == 4 || formato == 100) {
					var val = getValue("G1CRIDEF_MODPUNTI");
					//modpuntiOnChange(1);
					
				}else{
					$("#G1CRIDEF_MODPUNTI").prop('disabled', false);
					$("#G1CRIDEF_MODPUNTI").css('color', '#000');
					showObj("rowtitoloSezioneListaValori", true);
				}
				if(formato == 2 || formato == 6 || formato == 51 || formato == 52 || formato == 50){
					$("#rowG1CRIDEF_NUMDECI").show();
					var value = $("#G1CRIDEF_NUMDECI").val();	
					if(gFormula == 2){
						righeInseribili = MaxNumValori;
					}
					if((formato == 2 || formato == 52 || formato == 50) && !value){
						$("#G1CRIDEF_NUMDECI").val(2);
					}
				}else{
					$("#G1CRIDEF_NUMDECI").val("");
					$("#rowG1CRIDEF_NUMDECI").hide();
				}
				if(formato == 52 || formato == 50){
					if(gFormula == 2){
						righeInseribili = MaxNumValori;
					}
					showObj("rowGARE_SICINC", true);
					if(getValue("GARE1_ULTDETLIC")){
					showObj("rowGARE1_ULTDETLIC", true);}
					else{
						showObj("rowGARE1_ULTDETLIC", false);
					}
				}
				else{
					showObj("rowGARE_SICINC", false);
					showObj("rowGARE1_ULTDETLIC", false);
				}
				if(formato == 3){
					righeInseribili = MaxNumValori;
				}
				if(countdownrighe < MaxNumValori && righeVisualizzate < righeInseribili && (gFormato != 1 && gFormato != 4 && gFormato != 100)){showObj("rowLinkAddG1CRIREG", true);}
				gFormato = formato;
				
			}
			
			function formatoOnChange(formato){
				righeVisualizzate = 0;
				$("#G1CRIDEF_FORMULA").val("");
				gestioneEsponente("");
				countdownrighe = 0;
				showObj("rowMsgLastG1CRIREG", false);
				for (var cont = 1; cont <= MaxNumValori; cont++ ){
					hideElementoSchedaMultipla(cont, 'G1CRIREG', new Array('G1CRIREG_PUNTUALE_', 'G1CRIREG_VALMIN_', 'G1CRIREG_VALMAX_', 'G1CRIREG_COEFFI_', 'G1CRIREG_IDCRIDEF', 'G1CRIREG_ID'));
					setValue('G1CRIREG_DEL_G1CRIREG_' + cont, "1");
				}
				if (formato != 1 && formato != 4 && formato != 100) {
					showNextElementoSchedaMultipla('G1CRIREG', new Array('G1CRIREG_PUNTUALE_', 'G1CRIREG_VALMIN_', 'G1CRIREG_VALMAX_', 'G1CRIREG_COEFFI_', 'G1CRIREG_IDCRIDEF', 'G1CRIREG_ID'), new Array());
				}
			}
			
			function formulaOnChange(formula){
				if(gFormula == 2 && formula != 2){
					showObj("rowMsgLastG1CRIREG", false);
					righeVisualizzate = 0;
					countdownrighe = 0;
					for (var cont = 1; cont <= MaxNumValori; cont++ ){
						hideElementoSchedaMultipla(cont, 'G1CRIREG', new Array('G1CRIREG_PUNTUALE_', 'G1CRIREG_VALMIN_', 'G1CRIREG_VALMAX_', 'G1CRIREG_COEFFI_', 'G1CRIREG_IDCRIDEF', 'G1CRIREG_ID'));
						setValue('G1CRIREG_DEL_G1CRIREG_' + cont, "1");
					}
					if (gFormato != 1 && gFormato != 4 && gFormato != 100) {
						showNextElementoSchedaMultipla('G1CRIREG', new Array('G1CRIREG_PUNTUALE_', 'G1CRIREG_VALMIN_', 'G1CRIREG_VALMAX_', 'G1CRIREG_COEFFI_', 'G1CRIREG_IDCRIDEF', 'G1CRIREG_ID'), new Array());
					}
				}
				if(countdownrighe < MaxNumValori && righeVisualizzate < righeInseribili && (gFormato != 1 && gFormato != 4 && gFormato != 100)){showObj("rowLinkAddG1CRIREG", true);}
				gFormula = formula;
			}
			
			function gestioneSezioneValoriAmmessi(formula,formato){
				aggiornaVisualizzazioneValoriAmmessi(formula,formato);
			}
			
			function aggiornaVisualizzazioneValoriAmmessi(formula,formato){
				gFormato = formato;
				gFormula = formula;
				righeInseribili = 1;
				if(formato == 3){
					righeInseribili = MaxNumValori;
					visualizzataSezioneAmmessi = false;
					visualizzatoPuntuale = true;
					showObj("rowtitoloSezioneListaValori", true);
					if(countdownrighe < MaxNumValori && righeVisualizzate < righeInseribili){showObj("rowLinkAddG1CRIREG", true);}
					$("[id^='rowG1CRIREG_VALMIN']").each(function() {
						$(this).find("td:nth-child(1)").show();
						$(this).find("td:nth-child(2)").show();
						$(this).find("td:nth-child(3)").hide();
						$(this).find("td:nth-child(4)").hide();
						$(this).find("td:nth-child(4)").find("input:nth-child(2)").val("");
						$(this).find("td:nth-child(5)").hide();
						$(this).find("td:nth-child(6)").hide();
						$(this).find("td:nth-child(6)").find("input:nth-child(2)").val("");
					});
					if(formula != 1){
						$("[id^='rowG1CRIREG_VALMIN']").each(function() {
							$(this).find("td:nth-child(7)").hide();
							$(this).find("td:nth-child(8)").hide();
							$(this).find("td:nth-child(8)").find("input:nth-child(2)").val("");
						});
					}else{
						righeInseribili = MaxNumValori;
						$("[id^='rowG1CRIREG_VALMIN']").each(function() {
							$(this).find("td:nth-child(7)").show();
							$(this).find("td:nth-child(8)").show();
							});
					}
				}else{
					if(formato == 2 || formato == 5 || formato == 6 || formato == 51 || formato == 52 || formato == 50){
						visualizzataSezioneAmmessi = true;
						visualizzatoPuntuale = false;
						showObj("rowtitoloSezioneListaValori", true);
						if(countdownrighe < MaxNumValori && righeVisualizzate < righeInseribili){showObj("rowLinkAddG1CRIREG", true);}
						$("[id^='rowG1CRIREG_VALMIN']").each(function() {
							$(this).find("td:nth-child(1)").hide();
							$(this).find("td:nth-child(2)").hide();
							$(this).find("td:nth-child(2)").find("input:nth-child(2)").val("");
							$(this).find("td:nth-child(3)").show();
							$(this).find("td:nth-child(4)").show();
							$(this).find("td:nth-child(5)").show();
							$(this).find("td:nth-child(6)").show();
						});
						if(formula != 2){
							righeInseribili = 1;
							$("[id^='rowG1CRIREG_VALMIN']").each(function() {
							$(this).find("td:nth-child(7)").hide();
							$(this).find("td:nth-child(8)").hide();
							$(this).find("td:nth-child(8)").find("input:nth-child(2)").val("");
							});
						}else{
							righeInseribili = MaxNumValori;
							$("[id^='rowG1CRIREG_VALMIN']").each(function() {
							$(this).find("td:nth-child(7)").show();
							$(this).find("td:nth-child(8)").show();
							});
						}
					}else{
					visualizzataSezioneAmmessi = false;
					visualizzatoPuntuale = false;
					showObj("rowtitoloSezioneListaValori", false);
					showObj("rowLinkAddG1CRIREG", false);
					righeVisualizzate = 0;
					}
				}
				if(countdownrighe < MaxNumValori && righeVisualizzate < righeInseribili && (formato != 1 && formato != 4 && formato != 100) && (formula == 2 || formato == 3)){
					showObj("rowLinkAddG1CRIREG", true);
				}else{
					showObj("rowLinkAddG1CRIREG", false);
				}
			}
			
			function modpuntiOnChange(modpunti) {
				if (modpunti == 1 || modpunti == 3) {
					showObj("rowG1CRIDEF_MODMANU", true);
					showObj("rowG1CRIDEF_FORMULA", false);
					$("#G1CRIDEF_FORMULA").val("");
					formulaOnChange(0);
					aggiornaVisualizzazioneValoriAmmessi("",$("#G1CRIDEF_FORMATO").val());
				}else{
					showObj("rowG1CRIDEF_MODMANU", false);
					$("#G1CRIDEF_MODMANU", "").val("");
					showObj("rowG1CRIDEF_FORMULA", true);
				}
			}
			
			function setModManu(modpunti) {
				if (modpunti == 3) {
					$("#G1CRIDEF_MODMANU").prop('disabled', true);
					$("#G1CRIDEF_MODMANU").css('color', '#888');
					$("#G1CRIDEF_MODMANU", "").val(1);
				}else{
					$("#G1CRIDEF_MODMANU").prop('disabled', false);
					$("#G1CRIDEF_MODMANU").css('color', '#000');
				}
			}
			
			var showNextElementoSchedaMultipla_Default = showElementoSchedaMultipla;
			function showNextElementoSchedaMultipla_Custom(tipo, campi,visibilitaCampi){
				if(righeVisualizzate<MaxNumValori){righeVisualizzate = righeVisualizzate + 1;}
				countdownrighe = countdownrighe + 1;
				if(countdownrighe >= MaxNumValori){showObj("rowMsgLastG1CRIREG", true);showObj("rowLinkAddG1CRIREG", false);}
				showNextElementoSchedaMultipla_Default(countdownrighe, tipo, campi, visibilitaCampi);
				setValue('G1CRIREG_DEL_G1CRIREG_' + countdownrighe, "0");
			}
			showNextElementoSchedaMultipla = showNextElementoSchedaMultipla_Custom;
			
			var delElementoSchedaMultipla_default =	delElementoSchedaMultipla;	
			function delElementoSchedaMultipla_custom(id, label, tipo, campi){
				delElementoSchedaMultipla_default(id, label, tipo, campi);	
				righeVisualizzate = righeVisualizzate -1;
			}
			delElementoSchedaMultipla = delElementoSchedaMultipla_custom;
				
						
			function Comparator(a, b) {
			   if (a[1] < b[1]) return -1;
			   if (a[1] > b[1]) return 1;
			   return 0;
			 }
			
			var schedaConfermaDefault = schedaConferma;
			function schedaConfermaCustom(){
				var formula= $("#G1CRIDEF_FORMULA").val();
				if(formula==11 || formula==13 || formula==14 || formula==15){
					var esponente=$("#G1CRIDEF_ESPONENTE").val();
					if(esponente!=null && esponente!=""){
						var esponenteNum = parseFloat(esponente);
						if(esponenteNum<=0 && (formula==11 || formula==13)){
							alert("L'esponente deve essere maggiore di zero");
							return;
						}else if(esponenteNum<=1 && (formula==14 || formula==15)){
							alert("L'esponente deve essere maggiore di uno");
							return;
						}else{
							var esponenteString = esponenteNum.toString();
							if(esponenteString.includes(".")){
								var array = esponenteString.split(".");
								if(array[1].length > 1){
									alert("L'esponente puo' avere al piu' una cifra decimale");
									return;
								}
							}
						}
					}
				}
				$("#G1CRIDEF_MODPUNTI").prop('disabled', false);
				$("#G1CRIDEF_MODMANU").prop('disabled', false);
				var findedError;
				var valminValmaxTrovati;
				var coeffiTrovato;
				var puntualeCount = 0;
				if(visualizzataSezioneAmmessi){
					if(gFormula != 2){
						for(var n=1;n<=MaxNumValori;n++){
							var valmin = $("#G1CRIREG_VALMIN_" + n).val();
							var valminNum = parseFloat(valmin);
							var valmax = $("#G1CRIREG_VALMAX_" + n).val();
							var valmaxNum = parseFloat(valmax);
							if(valmin && valmax){
								if(valminNum >= valmaxNum){
									alert("Il limite inferiore dell'intervallo di valori ammessi deve essere minore del limite superiore");
									return;
								}
							}
						}
					}
					else{
						var index = 0;
						var array = [];
						for(var n=1;n<=MaxNumValori;n++){
							var visualizzato = $("#elementoSchedaMultiplaVisibileG1CRIREG_" + n).val();
							if(visualizzato == 1){
								var valmin = $("#G1CRIREG_VALMIN_" + n).val();
								var valminNum = parseFloat(valmin);
								var valmax = $("#G1CRIREG_VALMAX_" + n).val();
								var valmaxNum = parseFloat(valmax);
								var coeffi = $("#G1CRIREG_COEFFI_" + n).val();
								var coeffiNum = parseFloat(coeffi);
								if(coeffi && (valmin && valmax) && valminNum < valmaxNum){
									var temp = [valminNum,valmaxNum];
									array[index] = [];
									array[index] = temp;
									index++;				
								}else{
									alert("I valori ammessi per il criterio non sono definiti correttamente o in modo completo: deve essere definito almeno un intervallo di valori ammessi e per ogni intervallo deve essere valorizzato il limite inferiore e superiore e il relativo coefficiente");
									return;
								}
							}
						}
						if(array.length == 0){alert("I valori ammessi per il criterio non sono definiti correttamente o in modo completo: deve essere definito almeno un intervallo di valori ammessi e per ogni intervallo deve essere valorizzato il limite inferiore e superiore e il relativo coefficiente"); return;}
						array = array.sort(Comparator);
						for(var n = 0; n < array.length-1; n++){
							if(array[n][1] != array[n+1][0]){
								 alert("I valori ammessi per il criterio non sono definiti correttamente o in modo completo: gli intervalli di valori ammessi devono essere contigui");
								 return;
							}
						}
					}
				}
				if(visualizzatoPuntuale){
					index = 0;
					var array = [];
					for(var n=1;n<=MaxNumValori;n++){
						var puntuale = getValue("G1CRIREG_PUNTUALE_" + n);
						puntuale = $.trim(puntuale);
						var temp = $("#elementoSchedaMultiplaVisibileG1CRIREG_" + n).val();
						if(!puntuale && temp == 1){
							alert("I valori ammessi per il criterio non sono definiti correttamente o in modo completo: devono essere definiti almeno due valori e per ogni valore deve essere specificata la descrizione");
							return;}
						if(puntuale && temp ==1){
							if(array.indexOf(puntuale) >= 0){
								alert("I valori ammessi per il criterio non sono definiti correttamente o in modo completo: le descrizioni dei valori devono essere tutte diverse");
								return;
								
							}
							array[index] = puntuale;
							index++;
							if(gFormula == 1){
								var coeffi = $("#G1CRIREG_COEFFI_" + n).val();
								if(!coeffi){
									alert("I valori ammessi per il criterio non sono definiti correttamente o in modo completo: deve essere valorizzato il coefficiente per ogni valore");
									return;
								}
							}
							puntualeCount++;
						}
					}
					if(puntualeCount<2){alert("I valori ammessi per il criterio non sono definiti correttamente o in modo completo: devono essere definiti almeno due valori e per ogni valore deve essere specificata la descrizione"); return;}
				}
				schedaConfermaDefault();
			}
			$("#modpuntiFittizio").remove();
			
			schedaConferma = schedaConfermaCustom;
			
			var options;
			function gestoreCampoFormula(formato){
				
				var gFormula = $("#G1CRIDEF_FORMULA").val();
				
				if(options){
					options.each(function(){
					$(this).removeClass("removed");
					});
					options.appendTo(form);
				}
				form = $("#G1CRIDEF_FORMULA");
				if(formato == 3){
					$("#G1CRIDEF_FORMULA").find("option:nth-child(3)").addClass("removed");
					$("#G1CRIDEF_FORMULA").find("option:nth-child(4)").addClass("removed");
					$("#G1CRIDEF_FORMULA").find("option:nth-child(5)").addClass("removed");
					$("#G1CRIDEF_FORMULA").find("option:nth-child(6)").addClass("removed");
					$("#G1CRIDEF_FORMULA").find("option:nth-child(7)").addClass("removed");
					$("#G1CRIDEF_FORMULA").find("option:nth-child(8)").addClass("removed");
					$("#G1CRIDEF_FORMULA").find("option:nth-child(9)").addClass("removed");
					$("#G1CRIDEF_FORMULA").find("option:nth-child(10)").addClass("removed");
					$("#G1CRIDEF_FORMULA").find("option:nth-child(11)").addClass("removed");
					$("#G1CRIDEF_FORMULA").find("option:nth-child(12)").addClass("removed");
					$("#G1CRIDEF_FORMULA").find("option:nth-child(13)").addClass("removed");
					$("#G1CRIDEF_FORMULA").find("option:nth-child(14)").addClass("removed");
					$("#G1CRIDEF_FORMULA").find("option:nth-child(15)").addClass("removed");
					$("#G1CRIDEF_FORMULA").find("option:nth-child(16)").addClass("removed");
				}
				else{
					if(formato == 51){
						$("#G1CRIDEF_FORMULA").find("option:nth-child(2)").addClass("removed");
						$("#G1CRIDEF_FORMULA").find("option:nth-child(10)").addClass("removed");
						$("#G1CRIDEF_FORMULA").find("option:nth-child(11)").addClass("removed");
						$("#G1CRIDEF_FORMULA").find("option:nth-child(12)").addClass("removed");
						$("#G1CRIDEF_FORMULA").find("option:nth-child(13)").addClass("removed");
						$("#G1CRIDEF_FORMULA").find("option:nth-child(14)").addClass("removed");
						$("#G1CRIDEF_FORMULA").find("option:nth-child(15)").addClass("removed");
					}
					else{
						if(formato == 50 || formato == 52){
							$("#G1CRIDEF_FORMULA").find("option:nth-child(2)").addClass("removed");
							$("#G1CRIDEF_FORMULA").find("option:nth-child(4)").addClass("removed");
							$("#G1CRIDEF_FORMULA").find("option:nth-child(5)").addClass("removed");
							$("#G1CRIDEF_FORMULA").find("option:nth-child(6)").addClass("removed");
							$("#G1CRIDEF_FORMULA").find("option:nth-child(7)").addClass("removed");
							$("#G1CRIDEF_FORMULA").find("option:nth-child(8)").addClass("removed");
							$("#G1CRIDEF_FORMULA").find("option:nth-child(9)").addClass("removed");
						}
						else{
							$("#G1CRIDEF_FORMULA").find("option:nth-child(2)").addClass("removed");
							$("#G1CRIDEF_FORMULA").find("option:nth-child(9)").addClass("removed");
							$("#G1CRIDEF_FORMULA").find("option:nth-child(10)").addClass("removed");
							$("#G1CRIDEF_FORMULA").find("option:nth-child(11)").addClass("removed");
							$("#G1CRIDEF_FORMULA").find("option:nth-child(12)").addClass("removed");
							$("#G1CRIDEF_FORMULA").find("option:nth-child(13)").addClass("removed");
							$("#G1CRIDEF_FORMULA").find("option:nth-child(14)").addClass("removed");
							$("#G1CRIDEF_FORMULA").find("option:nth-child(15)").addClass("removed");
						}
					}
				}
				options = $("#G1CRIDEF_FORMULA").children().detach();
				options.not(".removed").appendTo(form);
				$("#G1CRIDEF_FORMULA").val(gFormula);
			}
			
			$( document ).ready(function() {
				showCriregSection();
			});
			
			function showCriregSection(){
				<c:choose>
					<c:when test='${modo eq "VISUALIZZA"}'>
					var cont = 0;
					$("[id^='rowG1CRIREG_VALMIN']").each(function() {
							if($(this).is(':visible')){
								cont ++;
							};
						});
						if(cont <= 0){
							$("#rowtitoloSezioneListaValori").hide();
							
						}else{
							$("#rowtitoloSezioneListaValori").show();
						}
					</c:when>
					<c:otherwise>
					if(righeVisualizzate < 1 && (gFormato != 1 && gFormato != 4 && gFormato != 100)){
						showNextElementoSchedaMultipla('G1CRIREG', new Array('G1CRIREG_PUNTUALE_', 'G1CRIREG_VALMIN_', 'G1CRIREG_VALMAX_', 'G1CRIREG_COEFFI_', 'G1CRIREG_IDCRIDEF', 'G1CRIREG_ID'), new Array());
					}
					</c:otherwise>
				</c:choose>
					if(countdownrighe < MaxNumValori && righeVisualizzate < righeInseribili && (gFormato != 1 && gFormato != 4 && gFormato != 100) && (gFormula == 2 || gFormato == 3)){
						showObj("rowLinkAddG1CRIREG", true);
					}
					else{
						showObj("rowLinkAddG1CRIREG", false);
					}
			}
			
			function gestioneEsponente(formula){
				if(formula==11 || formula==13 || formula==14 || formula==15){
					showObj("rowG1CRIDEF_ESPONENTE", true);
				}else{
					showObj("rowG1CRIDEF_ESPONENTE", false);
					$("#G1CRIDEF_ESPONENTE").val("");
				}
			}
			
			function validazioneDaModpunti(modpunti){
				var formato = $("#G1CRIDEF_FORMATO").val();
				return validazioneModpuntiFormato(modpunti,formato);
				
			}
			
			function validazioneDaFormato(formato){
				var modpunti = $("#G1CRIDEF_MODPUNTI").val();
				return validazioneModpuntiFormato(modpunti,formato);
				
			}
			
			function validazioneModpuntiFormato(modpunti,formato){
			 	var esitoControllo=true;
			 	if(modpunti==2 && (formato==1 || formato ==4 || formato == 100))
			 	 esitoControllo=false;
			 	return esitoControllo;
			 }
	</gene:javaScript>
</gene:template>