<%
/*
 * Created on: 25/05/2009
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

<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />

<c:choose>
	<c:when test='${not empty param.condizioniBloccoNonTelematica}'>
		<c:set var="condizioniBloccoNonTelematica" value='${param.condizioniBloccoNonTelematica}' />
	</c:when>
	<c:otherwise>
		<c:set var="condizioniBloccoNonTelematica" value="${condizioniBloccoNonTelematica}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.tipoCriterio}'>
		<c:set var="tipoCriterio" value='${param.tipoCriterio}' />
	</c:when>
	<c:otherwise>
		<c:set var="tipoCriterio" value="${tipoCriterio}" />
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

<c:if test="${tipoCriterio eq 2}">
	<c:set var="abilitataGestionePrezzo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1149", "1", "true")}'/>
</c:if>


<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="GOEV-scheda">

	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "GOEV")}'/>
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="GOEV" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGOEV" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInizializzazioniGoev">
			
			<gene:gruppoCampi idProtezioni="GOEV">
				<gene:campoScheda>
					<td colspan="2"><b>Dati generali</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="NGARA" visibile="false" value='${fn:substringAfter(keyParent, ":")}'/>
				<gene:campoScheda campo="NECVAN" visibile="false" />
				<gene:campoScheda campo="NORPAR" value='${gene:if(modo eq "NUOVO",newNorpar,datiRiga.GOEV_NORPAR)}'/>
				<gene:campoScheda campo="TIPPAR" visibile="false" defaultValue="${tipoCriterio }"/>
				<gene:campoScheda campo="DESPAR" obbligatorio="true"/>
				<c:if test="${tipoCriterio eq '1' and sezionitec eq '1'}">
					<gene:campoScheda campo="SEZTEC" obbligatorio="true"/>
				</c:if>
				<c:if test="${tipoCriterio eq '2' and abilitataGestionePrezzo eq '1'}">
					<gene:campoScheda campo="ISNOPRZ" visibile="false"  />
					<c:choose>
						<c:when test="${modoAperturaScheda eq 'VISUALIZZA' }">
							<gene:campoScheda campo="ISNOPRZ_FIT" campoFittizio="true" definizione="T30" title="Ai fini del calcolo soglia anomalia, criterio relativo a" value='${gene:if(datiRiga.GOEV_ISNOPRZ eq "1", "Altri elementi di valutazione" ,"Prezzo")}' />
						</c:when>
						<c:otherwise>
							<gene:campoScheda >
								<td class="etichetta-dato">Ai fini del calcolo soglia anomalia, criterio relativo a</td>
								<td class="valore-dato">
								<select id="ISNOPRZ_FIT" name="ISNOPRZ_FIT" title="Ai fini del calcolo soglia anomalia, criterio relativo a" onchange="javascript:aggiornaIsnoprz(this);">
								<option value="2" title="Prezzo" <c:if test="${datiRiga.GOEV_ISNOPRZ ne '1'}">selected="selected"</c:if> >Prezzo</option>
								<option value="1" title="Altri elementi di valutazione" <c:if test="${datiRiga.GOEV_ISNOPRZ eq '1'}">selected="selected"</c:if> >Altri elementi di valutazione</option>
								</select>
								</td>
							</gene:campoScheda>
						</c:otherwise>
					</c:choose>
				</c:if>
				
				<gene:campoScheda campo="MAXPUN" obbligatorio="true"/>
				<gene:campoScheda campo="MAXPUN_FIT" entita="GOEV" campoFittizio="true" definizione="F8.3;0;;;G1_MAXPUE" modificabile="false" visibile='${!(modo eq "VISUALIZZA")}' value="${datiRiga.GOEV_MAXPUN}" />
				<gene:campoScheda campo="MINPUN" />
				<gene:campoScheda campo="LIVPAR" visibile="false" defaultValue="1"/>
				<gene:campoScheda campo="NORPAR1" visibile="false" defaultValue="0"/>
			</gene:gruppoCampi>
			
			<c:choose>
				<c:when test='${modo eq "NUOVO"}' >
					<c:set var="numeroGara" value='${gene:getValCampo(keyParent, "GARE.NGARA")}' />
					<c:set var="norpar" value="${newNorpar}" scope="request" />
				</c:when>
				<c:otherwise>
					<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneSubCriteriFunction" parametro="${key}" />	
					<c:set var="numeroGara" value='${gene:getValCampo(key, "GOEV.NGARA")}' />
					<c:set var="numeroCriterio" value='${gene:getValCampo(key, "GOEV.NECVAN")}' />
					<c:set var="norpar" value="${datiRiga.GOEV_NORPAR}" scope="request" />
				</c:otherwise>
			</c:choose>
			
			<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
				<jsp:param name="entita" value='GOEV'/>
				<jsp:param name="chiave" value='${numeroGara};${numeroCriterio}'/>
				<jsp:param name="nomeAttributoLista" value='subCriteri' />
				<jsp:param name="idProtezioni" value="SUBCRIT" />
				<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/goev/sub-criterio.jsp"/>
				<jsp:param name="arrayCampi" value="'GOEV_NORPAR1_', 'GOEV_DESPAR_', 'GOEV_MAXPUN_','GOEV_NORPAR_'"/>		
				<jsp:param name="sezioneListaVuota" value="false" />
				<jsp:param name="titoloSezione" value="Sub-criterio" />
				<jsp:param name="titoloNuovaSezione" value="Nuovo sub-criterio" />
				<jsp:param name="descEntitaVociLink" value="sub-criterio" />
				<jsp:param name="msgRaggiuntoMax" value="i sub-criteri"/>
				<jsp:param name="usaContatoreLista" value="true" />
				<jsp:param name="funzEliminazione" value="delSubcriterio" />
				<jsp:param name="sezioneListaVuota" value="false" />
				<jsp:param name="tipoCriterioPadre" value="${tipoCriterio }" />
			</jsp:include>
			
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
				<c:if test='${param.bloccoPubblicazione eq true or condizioniBloccoNonTelematica}'>
					<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
					<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
				</c:if>
				<td class="comandi-dettaglio" colSpan="2">
					<gene:insert name="addPulsanti"/>
					<c:choose>
					<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
						<gene:insert name="pulsanteSalva">
							<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
						</gene:insert>
						<gene:insert name="pulsanteAnnulla">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
						</gene:insert>
				
					</c:when>
					<c:otherwise>
						<gene:insert name="pulsanteModifica">
							<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") && param.bloccoPubblicazione ne true && !condizioniBloccoNonTelematica}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
							</c:if>
						</gene:insert>
						<gene:insert name="pulsanteNuovo">
							<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO") && param.bloccoPubblicazione ne true && !condizioniBloccoNonTelematica}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovo()" id="btnNuovo">
							</c:if>
						</gene:insert>
					</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</gene:campoScheda>
			
			<input type="hidden" name="condizioniBloccoNonTelematica" value="${condizioniBloccoNonTelematica }"/>
			<input type="hidden" name="tipoCriterio" value="${tipoCriterio }"/>
			<input type="hidden" name="sezionitec" value="${sezionitec }"/>
			
		</gene:formScheda>

	</gene:redefineInsert>
	<gene:javaScript>
		
		var salvataggioOK = '${requestScope.salvataggioOK}';
			if(salvataggioOK != '' && salvataggioOK){
				historyVaiIndietroDi(1);
			}
		
		function aggiornaIsnoprz(select){
			setValue("GOEV_ISNOPRZ",select.value);
		}
		
		<c:choose>
			<c:when test='${empty subCriteri}'>
				var idUltimoSubcriterio = 0;
				var maxIdSubcriterioVisualizzabile = 5;
				<c:set var="numeroSubcriteri" value="0" />
			</c:when>
			<c:otherwise>
				var idUltimoSubcriterio = ${fn:length(subCriteri)};
				var maxIdSubcriterioVisualizzabile = ${fn:length(subCriteri)+5};
				<c:set var="numerosubCriteri" value="${fn:length(subCriteri)}" />
			</c:otherwise>
		</c:choose>
		
		
		if(idUltimoSubcriterio>0){
			showObj("rowG1CRIDEF_DESCRI", false);
			showObj("rowG1CRIDEF_MODPUNTI", false);
			showObj("rowG1CRIDEF_MODMANU", false);
			showObj("rowDET", false);
		}
		
		<c:if test='${modo ne "VISUALIZZA"}'>
				
				
			var showNextElementoSchedaMultipla_Default = showNextElementoSchedaMultipla;
					
			//Quando si inserisce una nuova sezione si deve valorizzare il campo NORPAR1
			//come NORPAR1 = max(NORPAR1) + 1.
			//Nel caso si stia inserendo la prima sezione si sbianca in campo MAXPUN del
			//padre
			function showNextElementoSchedaMultipla_Custom(tipo, campi,visibilitaCampi){
			  var indice = eval("lastId" + tipo + "Visualizzata") + 1;
			  //alert(indice);
			  
			  var contatore=0;
			  var maxNorpar1 = 0;
			  for(var j=1; j <= maxIdSubcriterioVisualizzabile; j++){
				if(isObjShow("rowtitoloSUBCRIT_" + j)){
				 contatore++;
				 var norpar1 = getValue("GOEV_NORPAR1_" + j);
				 if (parseInt(norpar1) > maxNorpar1)
				 	maxNorpar1 = parseInt(norpar1);
				}
			  }
			  
			  maxNorpar1 = maxNorpar1 +1;
			  
			  if(contatore == 0) {
			     setValue("GOEV_MAXPUN", "");
			     setValue("GOEV_MAXPUN_FIT", "");
			     showObj("rowGOEV_MAXPUN", false );
				 showObj("rowGOEV_MAXPUN_FIT",true );
			     
			  }
			  
			  setValue("GOEV_NGARA_" + indice, "${numeroGara}");
			  setValue("GOEV_LIVPAR_" + indice, "2");
			  setValue("GOEV_NORPAR_" + indice, "${norpar}");
			  setValue("GOEV_LIVPAR", "3");			  
			  setValue("GOEV_NORPAR1_" + indice, maxNorpar1);
			  
			  showNextElementoSchedaMultipla_Default(tipo, campi, visibilitaCampi);
			}
			
			showNextElementoSchedaMultipla =   showNextElementoSchedaMultipla_Custom;
			
			
			livpar = getValue("GOEV_LIVPAR");
			if (livpar == 3 || livpar == "3"){
				//document.getElementById("GOEV_MAXPUN").disabled = true;
				showObj("rowGOEV_MAXPUN", false);
				showObj("rowGOEV_MAXPUN_FIT", true);
			}else {
				showObj("rowGOEV_MAXPUN", true);
				showObj("rowGOEV_MAXPUN_FIT", false);
			}
			
		</c:if>
			
		
		//Quando si modifica il valore del campo NORPAR del criterio padre
		//vengono aggiornati i campi NORPAR dei subcriteri
		function aggiornaNorparSubcriteri(norpar){
			for(var j=1; j <= maxIdSubcriterioVisualizzabile; j++){
				if(isObjShow("rowtitoloSUBCRIT_" + j)){
					setValue("GOEV_NORPAR_" + j, norpar);
				}	
			}
					
		}
		//Viene calcolato il campo MAXPUN del padre come
		//somma dei MAXPUN dei subcriteri
		function aggiornaMaxpunPadre(){
			var totMaxpun;
			var subcriteri=0;
			for(var j=1; j <= maxIdSubcriterioVisualizzabile; j++){
				if(isObjShow("rowtitoloSUBCRIT_" + j)){
					var maxpun = getValue("GOEV_MAXPUN_" + j);
					subcriteri++;
					if (maxpun != null && maxpun != ""){
						if (totMaxpun == null)
							totMaxpun = 0;
						totMaxpun = parseFloat(totMaxpun) + parseFloat(maxpun);
					}
				}	
			}
			if (totMaxpun != null){
				setValue("GOEV_MAXPUN", totMaxpun);
				setValue("GOEV_MAXPUN_FIT", totMaxpun);
			}else{
				setValue("GOEV_MAXPUN", "");
				setValue("GOEV_MAXPUN_FIT", "");
			}	
				
			return subcriteri;
		}
		
		// Customizzazione della funzione delElementoSchedaMultipla per effettuare il
		// ricalcolo del campo GOEV_MAXPUN
		function delSubcriterio(id, label, tipo, campi){
			if(confirm("Procedere con l'eliminazione ?")){
				hideElementoSchedaMultipla(id, tipo, campi, false);
			  setValue(label + id, "1");
			  var subcriteri;
			  subcriteri = aggiornaMaxpunPadre();
			  
			  if (subcriteri == 0 || subcriteri == '0'){
			  	//document.getElementById("GOEV_MAXPUN").disabled = false;
			  	showObj("rowGOEV_MAXPUN", true);
				showObj("rowGOEV_MAXPUN_FIT", false);
			  	setValue("GOEV_LIVPAR", "1");
			  }
			}
		}
		
		var schedaConferma_Default = schedaConferma;
		
		function schedaConferma_Custom(){
			var maxpun=parseFloat(getValue("GOEV_MAXPUN"));
			var minpun=parseFloat(getValue("GOEV_MINPUN"));
			
			if(minpun!="" && maxpun!="" && minpun>maxpun){
				outMsg("La soglia minima è maggiore del punteggio massimo", "ERR");
				onOffMsg();
			}else{
				schedaConferma_Default();
			}
		}
		
		schedaConferma = schedaConferma_Custom;
	</gene:javaScript>
</gene:template>