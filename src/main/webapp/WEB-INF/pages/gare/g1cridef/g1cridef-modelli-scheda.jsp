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
	<c:when test='${not empty param.idgoevmod}'>
		<c:set var="idGoevmod" value='${param.idgoevmod}' />
	</c:when>
	<c:otherwise>
		<c:set var="idGoevmod" value="${idgoevmod}" />
	</c:otherwise>
</c:choose>


<c:set var="MaxNumValori" value="5" />

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="G1CRIDEF-scheda">

	<gene:setString name="titoloMaschera" value='Dettaglio assegnazione punteggio criterio di valutazione'/>
	<gene:redefineInsert name="corpo">
		<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
		<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
		<gene:formScheda entita="G1CRIDEF" gestisciProtezioni="true">
			
			<gene:gruppoCampi idProtezioni="DATGEN">
				<gene:campoScheda>
					<td colspan="2"><b>Dati generali</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="ID" visibile="false" modificabile="false"/>
				<c:set var="chiave" value='${gene:getValCampo(key, "ID")}' />
				
				<gene:campoScheda campo="DESPAR" entita="GOEVMOD" where="GOEVMOD.ID = '${idGoevmod}'" modificabile="false" title="Descrizione criterio o sub-criterio"/>
				<gene:campoScheda campo="MAXPUN" entita="GOEVMOD" where="GOEVMOD.ID = '${idGoevmod}'" modificabile="false"/>
				<gene:campoScheda campo="TIPPAR" entita="GOEVMOD" where="GOEVMOD.ID = '${idGoevmod}'" modificabile="false"/>
				
				<gene:campoScheda campo="FORMATO" modificabile="true" obbligatorio="true" />
				
				<gene:campoScheda campo="NUMDECI" obbligatorio="true"/>
				<gene:campoScheda campo="MODPUNTI" modificabile="true" obbligatorio="true"/>
				<gene:campoScheda campo="MODMANU" modificabile="true" obbligatorio="true"/>
				<gene:campoScheda campo="FORMULA" obbligatorio="true"/>
				<gene:campoScheda campo="ESPONENTE" visibile="${datiRiga.G1CRIDEF_FORMULA eq '11' or datiRiga.G1CRIDEF_FORMULA eq '13' or datiRiga.G1CRIDEF_FORMULA eq '14' or datiRiga.G1CRIDEF_FORMULA eq '15' }"/>
				<gene:campoScheda campo="DESCRI" />

				<gene:fnJavaScriptScheda funzione='formatoOnChangeInit("#G1CRIDEF_FORMATO#")' elencocampi='G1CRIDEF_FORMATO' esegui="true" />
				<gene:fnJavaScriptScheda funzione='modpuntiOnChange("#G1CRIDEF_MODPUNTI#")' elencocampi='G1CRIDEF_MODPUNTI' esegui="true" />
				<gene:fnJavaScriptScheda funzione='aggiornaVisualizzazioneValoriAmmessi("#G1CRIDEF_FORMULA#","#G1CRIDEF_FORMATO#")' elencocampi='G1CRIDEF_FORMULA;G1CRIDEF_FORMATO' esegui="true" />
				
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
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button"  class="bottone-azione" value='Torna a lista criteri di valutazione' title='Torna a lista criteri di valutazione' onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;&nbsp;
					&nbsp;
				</td>
			</gene:campoScheda>
			<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
			<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
			
		</gene:formScheda>

	</gene:redefineInsert>
	<gene:javaScript>
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
				righeInseribili = 1;
				if (formato == 1 || formato == 4 || formato == 100) {
					var val = getValue("G1CRIDEF_MODPUNTI");
					modpuntiOnChange(1);
					if(offtel != 2){
						$("#G1CRIDEF_MODPUNTI").prop('disabled', true);
						$("#G1CRIDEF_MODPUNTI").css('color', '#888');
						$("#G1CRIDEF_MODPUNTI").val(1);
						$("#G1CRIDEF_FORMULA").val("");
					}
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
				gFormato = formato;
			}
			
			function formatoOnChange(formato){
				righeVisualizzate = 0;
				$("#G1CRIDEF_FORMULA").val("");
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
					}
				}
			}
			
			function modpuntiOnChange(modpunti) {
				if (modpunti == 1 || modpunti == 3) {
					showObj("rowG1CRIDEF_MODMANU", true);
					showObj("rowG1CRIDEF_FORMULA", false);
					$("#G1CRIDEF_MODMANU", "").val(1);
					$("#G1CRIDEF_FORMULA").val("");
					aggiornaVisualizzazioneValoriAmmessi("",$("#G1CRIDEF_FORMATO").val());
				}else{
					showObj("rowG1CRIDEF_MODMANU", false);
					$("#G1CRIDEF_MODMANU", "").val("");
					showObj("rowG1CRIDEF_FORMULA", true);
				}
			}
			
			$( document ).ready(function() {
				showCriregSection();
			});
			
			function showCriregSection(){
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
			}
			
	</gene:javaScript>
</gene:template>