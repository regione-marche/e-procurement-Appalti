<%
/*
 * Created on: 20-apr-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 /*
  * Pagina a scheda relativa alla fase 'Inviti' del wizard Ricezione offerte
  *
  * Osservazione: questa jsp e' stata copiata e modifica per le gare a lotti ad
  * offerta unica (vedi fasiRicezione_Inviti-OffertaUnica.jsp)
  */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="numeroGara" value='${gene:getValCampo(key, "NGARA")}'/>
<jsp:include page="./defStepWizardFasiGara.jsp" />

<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestioneFaseAstaElettronicaFunction", pageContext, numeroGara,bustalotti)}'/>


<c:choose>
	<c:when test="${bustalotti eq '1' }">
		<c:set var="condizioniAmmissione" value='FUNZ.VIS.VIS.GARE.GARE-APERTURAOFFERTEAGGIUDPROV.AMMISSIONE'/>
		<c:set var="condizioniInvito" value='FUNZ.VIS.VIS.GARE.GARE-APERTURAOFFERTEAGGIUDPROV.INVITO'/>
		<c:set var="condizioniSvolgimento" value='FUNZ.VIS.VIS.GARE-APERTURAOFFERTEAGGIUDPROV.SVOLGIMENTO'/>
	</c:when>
	<c:otherwise>
		<c:set var="condizioniAmmissione" value='FUNZ.VIS.VIS.GARE.GARE-scheda.FASIGARA.AMMISSIONE'/>
		<c:set var="condizioniInvito" value='FUNZ.VIS.VIS.GARE.GARE-scheda.FASIGARA.INVITO'/>
		<c:set var="condizioniSvolgimento" value='FUNZ.VIS.VIS.GARE.GARE-scheda.FASIGARA.SVOLGIMENTO'/>
	</c:otherwise>
</c:choose>

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>

	
	<tr>
		<td>
			<br>
			<c:if test='${gene:checkProt(pageContext,condizioniAmmissione)}'>
			<input type="radio" value="1" name="paginaAsta" id="ammissione" <c:if test='${pgAsta eq 1}'>checked="checked"</c:if> onclick="javascript:cambiaPaginaAsta(1);" />
			 1. Ammissione ditte all'asta elettronica
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</c:if>
			<c:if test='${gene:checkProt(pageContext,condizioniInvito)}'>
			<input type="radio" value="2" name="paginaAsta" id="invito" <c:if test='${pgAsta eq 2}'>checked="checked"</c:if> onclick="javascript:cambiaPaginaAsta(2);" />
			 2. Invito
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</c:if>
			<c:if test='${gene:checkProt(pageContext,condizioniSvolgimento) and faseGara>= 7}'>
			<input type="radio" value="3" name="paginaAsta" id="Svolgimento" <c:if test='${pgAsta eq 3}'>checked="checked"</c:if> onclick="javascript:cambiaPaginaAsta(3);" />
			 3. Svolgimento
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</c:if>
			<br><br>
		</td>
	</tr>
	<c:choose>	
		<c:when test="${pgAsta eq 1 }">
			<jsp:include page="./fasiGara-AstaElettronica-AmmissioneDitte.jsp" />
		</c:when>
		<c:when test="${pgAsta eq 2 }">
			<jsp:include page="./fasiGara-AstaElettronica-Invito.jsp" />
		</c:when>
		<c:when test="${pgAsta eq 3 }">
			<jsp:include page="./fasiGara-AstaElettronica-Svolgimento.jsp" />
		</c:when>
	</c:choose>
	
					
<gene:javaScript>
	
	setPgAsta("${pgAsta}");
		
	function cambiaPaginaAsta(tipo){
		var paginaFasiGara = "${param.paginaFasiGara}";
		document.forms[0].entita.value="GARE";
		document.forms[0].pgAsta.value=tipo;
		
		document.forms[0].metodo.value="apri";
		<c:if test="${pgAsta eq 1 }">
			if((tipo==2 || tipo==3) && paginaFasiGara=="aperturaOffAggProvLottoOffUnica")
			document.forms[0].metodo.value="leggi";
		</c:if>
		
		
		document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
		if(document.forms[0].key.value==null || document.forms[0].key.value=="")
			document.forms[0].key.value = document.forms[0].keyParent.value;
		bloccaRichiesteServer();
		document.forms[0].submit();
		
	}
	
	function ulterioriCampiAsta(indiceRiga, chiaveRiga){
		setUpdateLista("${updateLista }");
		setFaseCalcolata("7");	
		setPaginaAttivaWizard("${step7_5Wizard}");
		setGaraLottoUnico(${garaLottoUnico});	
		ulterioriCampi(indiceRiga, chiaveRiga);
	}
	
	<c:if test="${pgAsta eq 1 and updateLista eq 1}">
		for(var i=1; i <= ${currentRow}+1; i++){
			document.getElementById("V_DITGAMMIS_AMMGAR_" + i).onchange = aggiornaPerCambioAmmessaGara;
		}
		document.getElementById("numeroDitte").value = ${currentRow}+1;
		document.getElementById("numeroDitteTotali").value = ${datiRiga.rowCount};
	</c:if>
	
	<c:if test="${pgAsta eq 2}">
		
		
		function scegliFile(indice) {
			var selezioneFile = document.getElementById("selFile[" + indice + "]").value;
			var lunghezza_stringa=selezioneFile.length;
			var posizione_barra=selezioneFile.lastIndexOf("\\");
			var nome=selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
			
			var formatoAllegati="${formatoAllegati}";
			if(!controlloTipoFile(nome,formatoAllegati)){
				alert("Il formato del file selezionato non è valido.\nI formati consentiti sono: " + formatoAllegati);
				document.getElementById("selFile[" + indice + "]").value="";
				setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
				return;
			}
			
			if(nome.length>100){
				alert("Il nome del file non può superare i 100 caratteri!");
				document.getElementById("selFile[" + indice + "]").value="";
				setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
			}else{
				setValue("W_DOCDIG_DIGNOMDOC_" + indice,nome);
				$("#spanRichiestaFirma_" + indice).show();
			}
		}
		
		function scegliFileDocumentale(param1,param2,indice) {
			var selezioneFile = param1;
			var lunghezza_stringa=selezioneFile.length;
			var posizione_barra=selezioneFile.lastIndexOf("\\");
			var nome=selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
			var tipoDoc="${tipoDoc}";
			if(tipoDoc=="6"){
				var formatoAllegati="${formatoAllegati}";
				if(!controlloTipoFile(nome,formatoAllegati)){
					alert("Il formato del file selezionato non è valido.\nI formati consentiti sono: " + formatoAllegati);
					document.getElementById("selFile[" + indice + "]").value="";
					setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
					return;
				}
			}
			if(nome.length>100){
				alert("Il nome del file non può superare i 100 caratteri!");
				document.getElementById("selFile[" + indice + "]").value="";
				setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
			}else{
				setValue("W_DOCDIG_DIGNOMDOC_" + indice,nome);
				$("#spanRichiestaFirma_" + indice).show();
			}
		}
		
		function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
			var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
			document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
		}
		
		
		var conferma_Default = conferma;
		function conferma_Custom(){
			$('[id^="DOCUMGARA_ALLMAIL_"]').attr('disabled',false);
			var controlloFasiOk = true;
			for(var i=1; i < maxIdAEFASIVisualizzabile ; i++){
				if(isObjShow("rowtitoloAEFASI_" + i)){
					var dataIniString = getValue("AEFASI_DATAORAINI_" + i);
					var dataFinString = getValue("AEFASI_DATAORAFINE_" + i);
					var durmax = getValue("AEFASI_DURMAX_" + i);
					if(dataIniString!= null && dataIniString!="" && durmax != null && durmax !=""){
						var dataIni = creaData(dataIniString);
						var dataFin = new Date(dataIni.getTime() + durmax * 60000 + 59*1000);
						var indiceInizio = 1;
						if(i==1)
							indiceInizio = 2;
						var indiceFine = maxIdAEFASIVisualizzabile -1;
						if(i==maxIdAEFASIVisualizzabile -1)
							indiceFine -= 1;	
						for(var j=indiceInizio; j <= indiceFine && i!= j; j++){
							if(isObjShow("rowtitoloAEFASI_" + j)){
								var dataIniStringTmp = getValue("AEFASI_DATAORAINI_" + j);
								var durmaxTmp = getValue("AEFASI_DURMAX_" + j);
								if(dataIniStringTmp!= null && dataIniStringTmp!="" && durmaxTmp != null && durmaxTmp !=""){
									var dataIniTmp = creaData(dataIniStringTmp);
									var dataFinTmp = new Date(dataIniTmp.getTime() + durmaxTmp * 60000 + 59*1000);
									if((dataIni<=dataFinTmp && dataIni>=dataIniTmp) || (dataFin<=dataFinTmp && dataFin>=dataIniTmp) ||
										(dataIni>=dataIniTmp && dataFin<=dataFinTmp) || (dataIni<=dataIniTmp && dataFin>=dataFinTmp)){
										controlloFasiOk= false;
										break;
									}
								}
							}
						}
						if(!controlloFasiOk){
							break;
						}else{
							setValue("AEFASI_DATAORAINI_FIT_" + i,dataIniString);
							setValue("AEFASI_DATAORAFINE_FIT_" + i,dataFinString);
						}
					}	
				}
			}
			if(!controlloFasiOk){
 	 			outMsg("Ci sono delle fasi di asta elettronica con intervalli temporali sovrapposti", "ERR");
				onOffMsg();
 	 		}else {
				document.forms[0].encoding="multipart/form-data";
				document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
				conferma_Default();	
			}
			
		}
		conferma =   conferma_Custom;
		
		
		/*
		* Viene creato un oggetto di tipo Date a partire da
		* una stringa nel formato: gg/mm/aaaa hh:mm:ss 
		*/
		function creaData(valore){
			var tmp= valore.split(' ');
			//le parti del vettore sono:
			// tmp[0]= gg/mm/aaaa
			// tmp[1]= hh:mm:ss
			var tmp1 = tmp[0].split('/');
			var giorno = tmp1[0];
			var mese = tmp1[1];
			var anno = tmp1[2];
			tmp1 = tmp[1].split(':');
			var ore=tmp1[0];
			var minuti=tmp1[1];
			var secondi=tmp1[2];
			var data = new Date(anno, mese-1, giorno, ore, minuti, secondi, 0);
			return data;
		}
		
		/*
		* Aggiornamento dei campi DATAORAINI e  DATAORAFINE
		*/
		function aggiornaDate(datini,oraini,durmin,indice){
			if(datini!=null && datini!= ""){
				// datini nel formato gg/mm/aaaa
				var tmp = datini.split('/');
				var g = tmp[0];
				var m = tmp[1];
				var a = tmp[2];
				var h=0;
				var mi=0
				var s =0;
				if(oraini!=null && oraini != ""){
					//oraini nel formato hh:mm
					var tmp1 = oraini.split(':');
					h = tmp1[0];
					mi = tmp1[1];
				}
				var data = new Date(a, m-1, g, h, mi, s, 0);
				setValue("AEFASI_DATAORAINI_" + indice,creaDataString(data));
				setValue("AEFASI_DATAORAINI_FIT_" + indice,creaDataString(data));
				if(durmin!=null && durmin!=""){
					data = new Date(data.getTime() + durmin * 60000 + 59*1000);
				}
				setValue("AEFASI_DATAORAFINE_" + indice,creaDataString(data));
				setValue("AEFASI_DATAORAFINE_FIT_" + indice,creaDataString(data));
			}else{
				setValue("AEFASI_DATAORAINI_" + indice,"");
				setValue("AEFASI_DATAORAFINE_" + indice,"");
				setValue("AEFASI_DATAORAINI_FIT_" + indice,"");
				setValue("AEFASI_DATAORAFINE_FIT_" + indice,"");
			}
		}
		
		/*
		* Conversione di un oggetto Data in un oggetto Stringa con formato
		* gg/mm/aaaa hh:mm:ss
		*/
		function creaDataString(data){
			var g = data.getDate();
			var m = data.getMonth() + 1;
			var a = data.getFullYear();
			var h = data.getHours();
			var mm = data.getMinutes();
			var s = data.getSeconds();
			var dataString = g + "/" + m + "/" + a + " " + h +":" + mm +":" + s;
			return dataString;  
		}
		
		
	</c:if>
	
	function impostaGaraNonAggiudicata(ngara,codgar1,esineg,datneg,npannrevagg){
		var href="href=gare/commons/popup-ImpostaGaraNonAggiudicata.jsp&ngara=" + ngara + "&codgar1=" + codgar1 + "&esineg=" + esineg + "&datneg=" + datneg + "&npannrevagg=" + npannrevagg;
		href+="&isLottoOffUnica=Si";
		openPopUpCustom(href, "impostaGaraNonAggiudicata", 700, 400, "yes", "yes");
	}
	
	<c:if test='${updateLista eq 1 and pgAsta eq 1}'>
		if(document.getElementById("invito")!=null)
			document.getElementById("invito").disabled=true;
		if(document.getElementById("Svolgimento")!=null)
			document.getElementById("Svolgimento").disabled=true;
	</c:if>
	<c:if test='${modo eq "MODIFICA" and pgAsta eq 2}'>
		if(document.getElementById("ammissione")!=null)
			document.getElementById("ammissione").disabled=true;
		if(document.getElementById("Svolgimento")!=null)
			document.getElementById("Svolgimento").disabled=true;
	</c:if>
	
	<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO") and gestioneUrl eq "true"}'>
		
		var showNextElementoSchedaMultipla_Default = showNextElementoSchedaMultipla;
		function showNextElementoSchedaMultipla_Custom(tipo, campi,visibilitaCampi){
			showNextElementoSchedaMultipla_Default(tipo, campi,visibilitaCampi);
			var indice = eval("lastId" + tipo + "Visualizzata");
			$("#rowDOCUMGARA_URLDOC_" + indice).hide();
			$('#DOCUMGARA_ALLMAIL_' + indice).attr('disabled',false);
		}
		showNextElementoSchedaMultipla =   showNextElementoSchedaMultipla_Custom;
	</c:if>
	
</gene:javaScript>
		