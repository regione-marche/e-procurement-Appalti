<%
			/*
 
				Descrizione:
					Interno delle scheda di documenti_verifichea
				Creato da:
					Cristian Febas
			*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="idStipula" value='${gene:getValCampo(keyParent,"ID")}' scope="request" />
<c:set var="idDocStipula" value='${gene:getValCampo(key,"ID")}' scope="request" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="richiestaFirma" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "documentiDb.richiestaFirma")}'/>
<c:set var="firmaProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:if test='${firmaProvider eq 2}'>
	<c:set var="firmaRemota" value="true"/>
</c:if>
<c:set var="digitalSignatureUrlCheck" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-check-url")}'/>
<c:set var="digitalSignatureProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:choose>
	<c:when test="${!empty digitalSignatureUrlCheck && !empty digitalSignatureProvider && (digitalSignatureProvider eq 1 || digitalSignatureProvider eq 2)}">
		<c:set var="digitalSignatureWsCheck" value='1'/>
	</c:when>
	<c:otherwise>
		<c:set var="digitalSignatureWsCheck" value='0'/>
	</c:otherwise>
</c:choose>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="DOCSTIPULA-scheda">
<c:choose>
	<c:when test='${modo eq "NUOVO"}'>
		<c:set var="titolo" value='Nuovo documento stipula'/>
	</c:when>
	<c:otherwise>
		<c:set var="titolo" value='Documento stipula'/>
	</c:otherwise>
</c:choose>
	


<gene:setString name="titoloMaschera" value="${titolo}" />

<gene:redefineInsert name="corpo">
	<gene:formScheda entita="G1DOCSTIPULA" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreG1DOCSTIPULA">
	
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="V_GARE_STIPULA"/>
			<jsp:param name="inputFiltro" value="${keyParent}"/>
			<jsp:param name="filtroCampoEntita" value="idstipula=${idStipula}"/>
		</jsp:include>
	
		<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
		<gene:campoScheda campo="ID" visibile="false" />
		<gene:campoScheda campo="IDSTIPULA" defaultValue="${idStipula}" visibile="false" />
		<gene:campoScheda campo="NUMORD" visibile="false"/>
		<gene:campoScheda campo="FASE" obbligatorio="true"/>
		<gene:campoScheda campo="TITOLO" obbligatorio="true"/>
		<gene:campoScheda campo="DESCRIZIONE" />
		<gene:campoScheda campo="NOTE" />
		<gene:campoScheda campo="VISIBILITA" obbligatorio="true" modificabile="${datiRiga.G1DOCSTIPULA_STATODOC == 1}"/>
		<gene:campoScheda campo="OBBLIGATORIO" />
		<gene:campoScheda campo="FORMATO" />
		<gene:campoScheda campo="STATO" entita="G1STIPULA" where="G1DOCSTIPULA.IDSTIPULA=G1STIPULA.ID" visibile="false"/>
		
		
		<gene:campoScheda campo="IDPRG" visibile="false" entita="W_DOCDIG"
		 where="W_DOCDIG.IDPRG='PG' AND W_DOCDIG.DIGENT='G1DOCSTIPULA' AND W_DOCDIG.DIGKEY1='${idDocStipula}'" />
		<gene:campoScheda campo="IDDOCDIG" visibile="false" entita="W_DOCDIG"
		 where="W_DOCDIG.IDPRG='PG' AND W_DOCDIG.DIGENT='G1DOCSTIPULA' AND W_DOCDIG.DIGKEY1='${idDocStipula}'" />
		<gene:campoScheda campo="DIGDESDOC" visibile="false" entita="W_DOCDIG"
		 where="W_DOCDIG.IDPRG='PG' AND W_DOCDIG.DIGENT='G1DOCSTIPULA' AND W_DOCDIG.DIGKEY1='${idDocStipula}'" />
		 
			<gene:campoScheda campo="DIGNOMDOC" entita="W_DOCDIG" visibile='true' modificabile="false"
		 	where="W_DOCDIG.IDPRG='PG' AND W_DOCDIG.DIGENT='G1DOCSTIPULA' AND W_DOCDIG.DIGKEY1='${idDocStipula}'" href="javascript:
		 	visualizzaFileAllegato('${datiRiga.W_DOCDIG_IDPRG}','${datiRiga.W_DOCDIG_IDDOCDIG}', '${datiRiga.W_DOCDIG_DIGNOMDOC}');" speciale='${datiRiga.G1DOCSTIPULA_STATODOC == "1" && modo eq "MODIFICA"}'>
				<c:if test='${datiRiga.G1DOCSTIPULA_STATODOC == "1" && modo eq "MODIFICA"}'>
					<gene:popupCampo titolo="Cancella file allegato" href="cancellaFile();" />
				</c:if>
				<c:if test='${ modo eq "VISUALIZZA" and param.autorizzatoModifiche ne "2" and not empty firmaRemota and (datiRiga.G1DOCSTIPULA_VISIBILITA eq 1 or datiRiga.G1DOCSTIPULA_VISIBILITA eq 2) }'>
					<a style="float:right;" href="javascript:openModal('${datiRiga.W_DOCDIG_IDPRG}','${datiRiga.W_DOCDIG_IDDOCDIG}','${datiRiga.W_DOCDIG_DIGNOMDOC}', '${contextPath}','');">
					<img src="${contextPath}/img/firmaRemota.png" title="Firma digitale del documento" alt="Firma documento" width="16" height="16">
					<span title="Firma digitale del documento">Firma documento</span></a>
				</c:if>
			</gene:campoScheda>
			<gene:campoScheda addTr="false" visibile="${modo ne 'VISUALIZZA'}" >
			<tr id="selFile">
					<td class="etichetta-dato">Nome file</td>
					<td class="valore-dato">
				<input type="file" name="selezioneFile" id="selezioneFile" onchange="javascript:scegliFile(this.value);" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" value=''/>
					</td>
				</tr>
				
			</gene:campoScheda>
			<gene:campoScheda title="File da allegare" campo="FILEDAALLEGARE" campoFittizio="true" visibile="false" definizione="T70;0" />
			<gene:campoScheda title="Flag file cancellato" campo="FILECANCELLATO" campoFittizio="true" visibile="false" definizione="T10;0" />

		<gene:campoScheda campo="STATODOC" defaultValue="1" modificabile="false"/>

		<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
		<gene:campoScheda>
			<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
		</gene:campoScheda>
		
		<gene:fnJavaScriptScheda funzione="gestioneAllegato('#G1DOCSTIPULA_VISIBILITA#','#G1DOCSTIPULA_STATODOC#','${modo}')" elencocampi="G1DOCSTIPULA_VISIBILITA;G1DOCSTIPULA_STATODOC" esegui="true" />		
		
	</gene:formScheda>
	
	<form name="formVisFirmaDigitale" id="formVisFirmaDigitale" action="${contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="gene/system/firmadigitale/verifica-firmadigitale.jsp" />
		<input type="hidden" name="idprg" id="idprg" value="" />
		<input type="hidden" name="iddocdig" id="iddocdig" value="" />
	</form>
	
	<jsp:include page="/WEB-INF/pages/gene/system/firmadigitale/modalPopupFirmaDigitaleRemota.jsp" />
	
</gene:redefineInsert>
<gene:javaScript>

	


	document.forms[0].encoding="multipart/form-data";
	
	<c:if test='${modo eq "MODIFICA" || modo eq "NUOVO"}'>
		var dignomdoc= getValue("W_DOCDIG_DIGNOMDOC");
		var selezioneFile = document.getElementById("selezioneFile").value;
	
		if((dignomdoc != null && dignomdoc != "") || (selezioneFile != null && selezioneFile != "")){
			document.getElementById("G1DOCSTIPULA_FORMATO").disabled = true;
		}
	</c:if>
	
	var salvataggioOK = '${requestScope.salvataggioOK}';
	if(salvataggioOK != '' && salvataggioOK){
		historyVaiIndietroDi(1);
	}	
	
	function scegliFile(valore) {
		var selezioneFile = document.getElementById("selezioneFile").value;
		var lunghezza_stringa = selezioneFile.length;
		var posizione_barra = selezioneFile.lastIndexOf("\\");
		var nome = selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
		var stato= 'OK';
		var formato=document.getElementById("G1DOCSTIPULA_FORMATO").value;
		var ext = nome.substring(nome.lastIndexOf('.')+1);
		var dignomdoc= getValue("W_DOCDIG_DIGNOMDOC");
		if(nome.length>100){
			alert("Il nome del file non può superare i 100 caratteri!");
			stato='KO';
		}
		if (formato == 1 && (ext.indexOf('P7M')==-1 && ext.indexOf('TSD')==-1 && ext.indexOf('PDF')==-1 && ext.indexOf('XML')==-1)){
			alert("Il formato del file selezionato non è valido.\nI formati consentiti sono: P7M;TSD;PDF;XML");
			stato='KO';
		}
		if (formato == 2 && (ext.indexOf('PDF')==-1 )){
			alert("Il formato del file selezionato non è valido.\nI formati consentiti sono: PDF");
			stato='KO';
		}
		if (formato == 4 && (ext.indexOf('XLS')==-1 && ext.indexOf('XSLX')==-1 && ext.indexOf('ODS')==-1)){
			alert("Il formato del file selezionato non è valido.\nI formati consentiti sono: XLS;XLSX;ODS");
			stato='KO';
		}
		if(stato=='OK'){
			setValue("FILEDAALLEGARE" ,nome);
			document.getElementById("G1DOCSTIPULA_FORMATO").disabled = true;
		}
		else{
			setValue("FILEDAALLEGARE","");
			document.getElementById("selezioneFile").value="";
			if(dignomdoc == null || dignomdoc == ""){
				document.getElementById("G1DOCSTIPULA_FORMATO").disabled = false;
			}
		}
		
	}

	function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
		var vet = dignomdoc.split(".");
		var ext = vet[vet.length-1];
		ext = ext.toUpperCase();
		<c:choose>
			<c:when test="${digitalSignatureWsCheck eq 0}">
				if(ext=='P7M' || ext=='TSD'){
					document.formVisFirmaDigitale.idprg.value = idprg;
					document.formVisFirmaDigitale.iddocdig.value = iddocdig;
					document.formVisFirmaDigitale.submit();
				}else{
					var href = "${contextPath}/pg/VisualizzaFileAllegato.do";
					document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
				}
			</c:when>
			<c:otherwise>
				if(ext=='P7M' || ext=='TSD' || ext=='XML' || ext=='PDF'){
					document.formVisFirmaDigitale.idprg.value = idprg;
					document.formVisFirmaDigitale.iddocdig.value = iddocdig;
					document.formVisFirmaDigitale.submit();
				}else{
					var href = "${contextPath}/pg/VisualizzaFileAllegato.do";
					document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
				}
			</c:otherwise>
	</c:choose>
	}
	
	function gestioneAllegato(visibilita,statodoc,modo) {
		if ((visibilita == '' || visibilita == '3') && (statodoc < 4)){
			$("#rowW_DOCDIG_DIGNOMDOC").hide();
			$("#selFile").hide();
			$("#FILEDAALLEGARE").val('');
		} else if((visibilita == '' || visibilita == '3') && (statodoc >= 4)){
			$("#selFile").hide();
		}else{
			$("#rowW_DOCDIG_DIGNOMDOC").show();
			$("#selFile").show();
		}
	}
		
	function cancellaFile(){
		setValue("W_DOCDIG_DIGNOMDOC", "");
		setValue("FILECANCELLATO" ,"true");
		setValue("FILEDAALLEGARE","");
		setValue("selezioneFile","");
		document.getElementById("G1DOCSTIPULA_FORMATO").disabled = false;
	}
	
	var schedaConfermaDefault = schedaConferma;
	
	function schedaConfermaCustom() {
		document.getElementById("G1DOCSTIPULA_FORMATO").disabled = false;
		schedaConfermaDefault();
	}
	
	var schedaConferma = schedaConfermaCustom;

</gene:javaScript>

</gene:template>
