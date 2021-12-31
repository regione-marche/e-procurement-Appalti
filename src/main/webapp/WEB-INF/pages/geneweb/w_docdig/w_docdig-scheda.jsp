<%
/*
 * Created on: 04/06/2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra che visualizza la conferma per l'inserimento delle pubblicazioni bando/esito predefinite
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/jquery.documenti.gara.js"></script>

<c:set var="richiestaFirma" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "documentiDb.richiestaFirma")}'/>
<c:set var="formato" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "allegatiComunicazione.formato")}'/>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="W_DOCDIG-scheda" schema="GENEWEB">

	<c:set var="idprg" value='${gene:getValCampo(keyParent,"IDPRG")}' scope="request" />
	<c:set var="idcom" value='${gene:getValCampo(keyParent,"IDCOM")}' scope="request" />
	<c:set var="firmaRemota" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "firmaremota.auto.url")}'/>
	<c:set var="datins" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetCOMDATINSFunction",pageContext,idprg,idcom)}' />
	
	<c:choose>
		<c:when test='${not empty param.comtipo}'>
			<c:set var="comtipo" value="${param.comtipo}"  />
		</c:when>
		<c:otherwise>
			<c:set var="comtipo" value="${comtipo}" />
		</c:otherwise>
	</c:choose>
	

	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleComunicazioneFunction", pageContext, "W_DOCDIG")}' />
	
	<gene:redefineInsert name="corpo">
	
		<gene:formScheda entita="W_DOCDIG" gestisciProtezioni="true"
			gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_DOCDIG" 
			plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreW_DOCDIG" >
			
			<gene:campoScheda campo="IDPRG" visibile="false" defaultValue='${idprg}'/>
			<gene:campoScheda title="N°" campo="IDDOCDIG" visibile="false" modificabile="false"/>
			<gene:campoScheda campo="DIGENT" defaultValue="W_INVCOM" visibile="false" />
			<gene:campoScheda campo="DIGKEY1" defaultValue="${idprg}" visibile="false" />
			<gene:campoScheda campo="DIGKEY2" defaultValue="${idcom}" visibile="false" />
			<gene:campoScheda campo="DIGDESDOC" obbligatorio="true" visibile="true" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote"/>
			<gene:campoScheda campo="DIGNOMDOC" modificabile="false">
				<c:if test='${modo eq "VISUALIZZA" && richiestaFirma eq "1" && datiRiga.W_DOCDIG_DIGFIRMA eq "1"}'>
					<span style="float:right;"><img width="16" height="16" src="${pageContext.request.contextPath}/img/isquantimod.png"/>&nbsp;In attesa di firma</span>
				</c:if>
				<c:if test='${modo eq "MODIFICA" && richiestaFirma eq "1"}'>
					<span style="float:right;">Richiesta firma?<input type="checkbox" name="richiestaFirma" id="richiestaFirma" class="file" size="50" <c:if test="${datiRiga.W_DOCDIG_DIGFIRMA eq '1'}">checked</c:if> onchange="javascript:aggiornaRichiestaFirma(this);"></span>
				</c:if>
				<c:set var="nomDoc" value="${gene:string4Js(datiRiga.W_DOCDIG_DIGNOMDOC)}"/>
				<c:set var="nomDoc" value="${fn:replace(nomDoc,'\"','&#34;')}"/>
				<c:if test='${modo eq "VISUALIZZA" and not empty firmaRemota and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.FirmaRemotaDocumenti")}'>
					<a style="float:right;" href="javascript:openModal('${datiRiga.W_DOCDIG_IDPRG}','${datiRiga.W_DOCDIG_IDDOCDIG}',${nomDoc},'${pageContext.request.contextPath}');">
					<img src="${pageContext.request.contextPath}/img/firmaRemota.png" title="Firma digitale del documento" alt="Firma documento" width="16" height="16">
					<span title="Firma digitale del documento">Firma documento</span></a>
				</c:if>
			</gene:campoScheda>
			<c:if test="${richiestaFirma eq '1'}">
				<gene:campoScheda campo="DIGFIRMA" visibile='false'/>
			</c:if>
			<c:choose>
				<c:when test='${modo eq "VISUALIZZA" }'>
					<gene:campoScheda title="Visualizza allegato" >
						<c:if test="${modo eq 'VISUALIZZA' and !empty datiRiga.W_DOCDIG_IDPRG and !empty datiRiga.W_DOCDIG_IDDOCDIG}">
							<c:set var="nomDoc" value="${gene:string4Js(datiRiga.W_DOCDIG_DIGNOMDOC)}"/>
							<c:set var="nomDoc" value="${fn:replace(nomDoc,'\"','&#34;')}"/>
							<a href="javascript:visualizzaFileAllegato('${datiRiga.W_DOCDIG_IDPRG}','${datiRiga.W_DOCDIG_IDDOCDIG}',${nomDoc});" title="Visualizza allegato" >
								<img width="24" height="24" title="Visualizza allegato" alt="Visualizza allegato" src="${pageContext.request.contextPath}/img/visualizzafile.gif"/>
							</a>
						</c:if>
					</gene:campoScheda>
				</c:when>
				<c:when test='${modo eq "MODIFICA"}'>
					<gene:campoScheda title="Visualizza allegato" >
						<img width="24" height="24" title="" alt="" src="${pageContext.request.contextPath}/img/visualizzafilegrigio.gif"/>
					</gene:campoScheda>
				</c:when>
				<c:when test='${modo eq "NUOVO"}'>
					<gene:campoScheda title="Nome file (*)" >
						<input type="file" name="selezioneFile" id="selezioneFile" onchange="javascript:scegliFile(this.value);" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" />
						<c:if test="${richiestaFirma eq '1'}">
							<span style="float:right;">Richiesta firma?<input type="checkbox" name="richiestaFirma" id="richiestaFirma" class="file" size="50" onchange="javascript:aggiornaRichiestaFirma(this);"></span>
						</c:if>
					</gene:campoScheda>
					<gene:campoScheda title="File da allegare" campo="FILEDAALLEGARE" campoFittizio="true" visibile="false" definizione="T70;0" />			
				</c:when>
			</c:choose>
				<input type="hidden" name="comtipo" id="comtipo" value="${comtipo}"/>
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>		
			<gene:redefineInsert name="schedaNuovo" />				
			<gene:redefineInsert name="pulsanteNuovo" />
		</gene:formScheda>
		

		<jsp:include page="/WEB-INF/pages/gene/system/firmadigitale/modalPopupFirmaDigitaleRemota.jsp" />
		
		<form name="formVisFirmaDigitale" id="formVisFirmaDigitale" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
			<input type="hidden" name="href" value="gene/system/firmadigitale/verifica-firmadigitale.jsp" />
			<input type="hidden" name="idprg" id="idprg" value="" />
			<input type="hidden" name="iddocdig" id="iddocdig" value="" />
		</form>

		
  	</gene:redefineInsert>

	<gene:javaScript>
	
		//gestita al salvataggio del dettaglio dell'allegato (sia in inserimento che in modifica), 
    	//la visualizzazione della lista degli allegati invece della scheda di dettaglio stesso
		var salvataggioOK = '${requestScope.salvataggioOK}';
		if(salvataggioOK != '' && salvataggioOK){
			historyVaiIndietroDi(1);
		}
	
		document.forms[0].encoding="multipart/form-data";
	
		function scegliFile(valore) {
			selezioneFile = document.getElementById("selezioneFile").value;
			setValue("FILEDAALLEGARE",selezioneFile);
			
			lunghezza_stringa=selezioneFile.length;
			posizione_barra=selezioneFile.lastIndexOf("\\");
			nome=selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
			var formatoAllegati="${formato}";
			if(!controlloTipoFile(nome,formatoAllegati)){
				alert("Il formato del file selezionato non è valido.\nI formati consentiti sono: " + formatoAllegati);
				document.getElementById("selezioneFile").value="";
				setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
				return;
			}
			if(nome.length>100){
				alert("Il nome del file non può superare i 100 caratteri!");
				document.getElementById("selezioneFile").value="";
				setValue("W_DOCDIG_DIGNOMDOC","");
			}else{
				setValue("W_DOCDIG_DIGNOMDOC",nome);
			}
		}
		
		function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
			var vet = dignomdoc.split(".");
			var ext = vet[vet.length-1];
			ext = ext.toUpperCase();
			if(ext=='P7M' || ext=='TSD'){
				if(idprg == 'PA' && comtipo.value == 'FS12'){
					if($("#ckdate").size() == 0){
						var _input = $("<input/>", {"type": "hidden","id": "ckdate", "name": "ckdate", value:""});
						$("#formVisFirmaDigitale").append(_input);
					}
					document.formVisFirmaDigitale.ckdate.value = "${datins}";
				}
	  			document.formVisFirmaDigitale.idprg.value = idprg;
				document.formVisFirmaDigitale.iddocdig.value = iddocdig;
				document.formVisFirmaDigitale.submit();
			}else{
				var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
				document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
			}
		}	
	
		var schedaConfermaOld=schedaConferma;
		schedaConferma=function(){
			if (${modo eq "NUOVO"}){
				if (document.forms[0].selezioneFile.value == "") {
					alert("Deve essere indicato il file da allegare.");				
				} else {
					schedaConfermaOld();
				}
			} else {
				schedaConfermaOld();
			}
		}
		
		function aggiornaRichiestaFirma( checkbox){
			if(checkbox.checked){
				setValue("W_DOCDIG_DIGFIRMA","1");
			}else{
				setValue("W_DOCDIG_DIGFIRMA","");
			}
		}
		
	</gene:javaScript>

</gene:template>
