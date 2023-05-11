<%--
/*
 * Created on: 17-mag-2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */


--%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="opzioniUtenteAbilitate"
	value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#"
	scope="request" />

<c:set var="idDocumod" value='${gene:getValCampo(key, "G1DOCUMOD.ID")}' />
<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDatiG1documodFunction", pageContext, idDocumod)}'/>

<c:choose>
	<c:when test='${not empty param.gruppo}'>
		<c:set var="gruppo" value="${param.gruppo}" />
	</c:when>
	<c:otherwise>
		<c:set var="gruppo" value="${gruppo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.busta}'>
		<c:set var="busta" value="${param.busta}" />
	</c:when>
	<c:otherwise>
		<c:set var="busta" value="${busta}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${gruppo eq 3}'>
		<c:set var="titoloMaschera" value='Dettaglio modello Documenti busta ${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1013",busta,"false")}' />
	</c:when>
	<c:otherwise>
		<c:set var="titoloMaschera" value='Dettaglio modello ${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1064",gruppo,"false")}' />
	</c:otherwise>
</c:choose>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true"
	schema="GARE" idMaschera="G1DOCUMOD-scheda">

	<gene:setString name="titoloMaschera"
		value="${titoloMaschera}" />

	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="G1DOCUMOD" gestisciProtezioni="true"
			gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreG1DOCUMOD">
			<gene:campoScheda>
				<td colspan="2"><b>Dati generali</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="ID" visibile="false" />
			<gene:campoScheda campo="DESCRIZIONE" obbligatorio="true" />
			<gene:campoScheda campo="ESCLUSO" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 20}' />
			<gene:campoScheda campo="TIPOGARA" visibile='${datiRiga.G1DOCUMOD_GRUPPO ne 20}'/>
			<gene:campoScheda campo="TIPOPROC" visibile='${datiRiga.G1DOCUMOD_GRUPPO ne 20}'/>
			<gene:campoScheda campo="CRITLIC" visibile='${datiRiga.G1DOCUMOD_GRUPPO ne 20}'/>
			<gene:campoScheda campo="GARTEL" visibile='${datiRiga.G1DOCUMOD_GRUPPO ne 20}'/>
			<gene:campoScheda campo="LIMINF" />
			<gene:campoScheda campo="LIMSUP" />
			<gene:campoScheda campo="GRUPPO" defaultValue="${gruppo}" visibile='false' />
			<gene:campoScheda campo="BUSTA" defaultValue="${busta}" visibile='false' />

			<c:if test='${modo ne "NUOVO"}'>
				<gene:callFunction
					obj="it.eldasoft.sil.pg.tags.funzioni.GestioneG1arcdocumodFunction"
					parametro='${gene:getValCampo(key, "ID")}' />
			</c:if>

			<jsp:include
				page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp">
				<jsp:param name="entita" value='G1ARCDOCUMOD' />
				<jsp:param name="chiave" value='${gene:getValCampo(key, "ID")}' />
				<jsp:param name="nomeAttributoLista" value='documenti' />
				<jsp:param name="idProtezioni" value="G1ARCDOCUMOD" />
				<jsp:param name="jspDettaglioSingolo"
					value="/WEB-INF/pages/gare/g1arcdocumod/g1arcdocumod-dettaglio.jsp" />
				<jsp:param name="arrayCampi"
					value="'G1ARCDOCUMOD_NUMORD_', 'G1ARCDOCUMOD_FASE_', 'G1ARCDOCUMOD_DESCRIZIONE_', 'G1ARCDOCUMOD_ULTDESC_', 'G1ARCDOCUMOD_VISIBILITA_', 'G1ARCDOCUMOD_OBBLIGATORIO_', 'G1ARCDOCUMOD_IDDOCUMOD_', 'G1ARCDOCUMOD_ID_', 'G1ARCDOCUMOD_REQCAP_', 'G1ARCDOCUMOD_TIPODOC_', 'G1ARCDOCUMOD_CONTESTOVAL_', 'G1ARCDOCUMOD_MODFIRMA_', 'G1ARCDOCUMOD_IDSTAMPA_', 'G1ARCDOCUMOD_ALLMAIL_', 'G1ARCDOCUMOD_ALLMAIL__IDPRG_', 'G1ARCDOCUMOD_ALLMAIL__IDDOCDG_', 'W_DOCDIG_IDPRG_','W_DOCDIG_IDDOCDIG_', 'W_DOCDIG_DIGNOMDOC_','selezioneFile_'" />
				<jsp:param name="titoloSezione" value="Documento" />
				<jsp:param name="titoloNuovaSezione"
					value="Nuovo documento" />
				<jsp:param name="usaContatoreLista" value="true" />
				<jsp:param name="descEntitaVociLink" value="Documento" />
				<jsp:param name="msgRaggiuntoMax"
					value="i documenti" />
			</jsp:include>

			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
			<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
			<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
			<gene:redefineInsert name="schedaConferma">
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:schedaConfermaUploadMultiplo();" title="Salva modifiche" tabindex="1501">
							${gene:resource("label.tags.template.dettaglio.schedaConferma")}</a></td>
				</tr>
			</gene:redefineInsert>
			
			<gene:redefineInsert name="schedaAnnulla">
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:schedaAnnullaUploadMultiplo();" title="Annulla modifiche" tabindex="1502">
						${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}</a></td>
				</tr>
			</gene:redefineInsert>
			
			<gene:redefineInsert name="pulsanteSalva">
				<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConfermaUploadMultiplo();">
			</gene:redefineInsert>
			
			<gene:redefineInsert name="pulsanteAnnulla">
				<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnullaUploadMultiplo();">
			</gene:redefineInsert>
		</gene:formScheda>
	</gene:redefineInsert>

	<gene:javaScript>
	
			document.forms[0].encoding="multipart/form-data";
		
		
			function schedaConfermaUploadMultiplo(){
				document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken+"&gruppo=${gruppo}"+"&busta=${busta}";
					schedaConferma();
			}
			
			function schedaAnnullaUploadMultiplo(){
				document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken+"&gruppo=${gruppo}"+"&busta=${busta}";
				schedaAnnulla();
			}

            
            function scegliFile(indice) {
				var selezioneFile = document.getElementById("selFile[" + indice + "]").value;
				var lunghezza_stringa=selezioneFile.length;
				var posizione_barra=selezioneFile.lastIndexOf("\\");
				var nome=selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
				if(nome.length>100){
					alert("Il nome del file non può superare i 100 caratteri!");
					document.getElementById("selFile[" + indice + "]").value="";
					setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
				}else{
					setValue("W_DOCDIG_DIGNOMDOC_" + indice,nome);
				}
			}

			function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
				var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
				document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
			}
	
		<c:choose>
			<c:when test='${empty documenti}'>
				var idUltimoDocumento = 0;
				var maxIdDocumentoVisualizzabile = 5;
				<c:set var="numeroDocumenti" value="0" />
			</c:when>
			<c:otherwise>
				var idUltimoDocumento = ${fn:length(documenti)};
				var maxIdDocumentoVisualizzabile = ${fn:length(documenti)+5};
				<c:set var="numeroDocumenti" value="${fn:length(documenti)}" />
			</c:otherwise>
		</c:choose>

		<c:if test='${modo ne "VISUALIZZA"}'>
			
			var showNextElementoSchedaMultipla_Default = showNextElementoSchedaMultipla;
					
			//Quando si inserisce una nuova sezione si deve valorizzare il campo NUMORD
			//come NUMORD = max(NUMORD) + 1.
			//Nel caso si stia inserendo la prima sezione si sbianca in campo MAXPUN del
			//padre
			function showNextElementoSchedaMultipla_Custom(tipo, campi,visibilitaCampi){
			  var indice = eval("lastId" + tipo + "Visualizzata") + 1;
			  //alert(indice);
			  
			  var contatore=0;
			  var maxNumord = 0;
			  for(var j=1; j <= maxIdDocumentoVisualizzabile; j++){
				if(isObjShow("rowtitoloG1ARCDOCUMOD_" + j)){
				 contatore++;
				 var Numord = getValue("G1ARCDOCUMOD_NUMORD_" + j);
				 if (parseInt(Numord) > maxNumord)
				 	maxNumord = parseInt(Numord);
				}
			  }
			  
			  maxNumord = maxNumord +1;
			    
			  setValue("G1ARCDOCUMOD_NUMORD_" + indice, maxNumord);
			  
			  showNextElementoSchedaMultipla_Default(tipo, campi, visibilitaCampi);
			}
			
			showNextElementoSchedaMultipla =   showNextElementoSchedaMultipla_Custom;
			
		</c:if>            

	</gene:javaScript>
</gene:template>

