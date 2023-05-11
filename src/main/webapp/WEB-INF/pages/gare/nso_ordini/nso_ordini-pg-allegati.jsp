<%/*
       * Created on 02-Dec-2013
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

		<c:set var="contextPath" value="${pageContext.request.contextPath}" />

		<c:set var="id" value='${gene:getValCampo(key,"ID")}' scope="request" />

		<gene:redefineInsert name="head">
				<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
				<script type="text/javascript" src="${pageContext.request.contextPath}/js/nso-ordini.js?t=<%=System.currentTimeMillis()%>"></script>
		</gene:redefineInsert>
		<c:if test='${(requestScope.statoOrdine ne 1 && requestScope.statoOrdine ne 2)}'>
				<gene:redefineInsert name="schedaModifica" />
				<gene:redefineInsert name="pulsanteModifica" />
		 </c:if>
		
		<gene:formScheda entita="NSO_ORDINI" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreOrdiniNso" >
			<c:if test="${requestScope.statoOrdine eq 8}">
				<%/*
					se l'ordine è revocato non devo permettere alcuna modifica
				*/ %>
				<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
				<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
			</c:if>
			<gene:redefineInsert name="addToAzioni" >
				<c:if test='${(requestScope.statoOrdine eq 4 || requestScope.statoOrdine eq 5 || requestScope.statoOrdine eq 6) && requestScope.isPeriodoVariazione eq 1}' >
					<tr>
						<td class="vocemenulaterale">
								<a href="javascript:revocaOrdineNso();" id="menuValidaOrdine" title="Revoca Ordine" tabindex="1510">Revoca Ordine</a>
						</td>
					</tr>
				</c:if>
				<c:if test="${(requestScope.statoOrdine eq 1) || (requestScope.statoOrdine eq 2)}">
					<tr>
						<td class="vocemenulaterale">
								<a href="javascript:validaOrdine();" id="menuValidaOrdine" title="Controlla Dati Inseriti" tabindex="1510">Controlla Dati Inseriti</a>
						</td>
					</tr>
				</c:if>
			</gene:redefineInsert>
			<gene:redefineInsert name="addToDocumenti" />
			<gene:redefineInsert name="schedaNuovo" />
			<gene:redefineInsert name="pulsanteNuovo" />
			<c:if test='${requestScope.statoOrdine eq 3 || requestScope.statoOrdine eq 4}'>
				<gene:redefineInsert name="schedaModifica" />
				<gene:redefineInsert name="pulsanteModifica" />
			</c:if>
			
			<gene:campoScheda campo="ID" visibile="false"   />
			<gene:campoScheda campo="CODORD" visibile="false" />
			<gene:campoScheda campo="ID" entita="NSO_ALLEGATI" where="NSO_ALLEGATI.NSO_ORDINI_ID=NSO_ORDINI.ID"  visibile="false"   />
			<c:if test="${modo ne 'NUOVO'}">
				<c:set var="id" value='${gene:getValCampo(key,"ID")}' scope="request" />
				<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestioneOrdinantiNsoFunction", pageContext, "NSO_ALLEGATI", id)}'/>
			</c:if>
			
				<gene:campoScheda addTr="false">
					<tbody id="sezioneMultiplaNSO_ALLEGATI">
				</gene:campoScheda>	
				<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
					<jsp:param name="entita" value='NSO_ALLEGATI'/>
					<jsp:param name="chiave" value='${datiRiga.NSO_ALLEGATI_ID}'/>
					<jsp:param name="nomeAttributoLista" value='allegati' />
					<jsp:param name="idProtezioni" value="NSO_ALLEGATI" />
					<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/nso_allegati/allegato.jsp" />
					<jsp:param name="arrayCampi"
						value="'NSO_ALLEGATI_ID_', 'NSO_ALLEGATI_NSO_ORDINI_ID_', 'NSO_ALLEGATI_NPROGR_', 'NSO_ALLEGATI_TIPODOC_', 'NSO_ALLEGATI_DESCRIZIONE_', 'NSO_ALLEGATI_DATARILASCIO_', 'NSO_ALLEGATI_DATASCADENZA_', 'NSO_ALLEGATI_IDPRG_', 'NSO_ALLEGATI_IDDOCDIG_', 'W_DOCDIG_IDPRG_','W_DOCDIG_IDDOCDIG_', 'W_DOCDIG_DIGNOMDOC_','selezioneFile_'" />
					<jsp:param name="titoloSezione" value="Allegato" />
					<jsp:param name="titoloNuovaSezione" value="Nuovo allegato" />
					<jsp:param name="descEntitaVociLink" value="allegato" />
					<jsp:param name="msgRaggiuntoMax" value="gli allegati"/>
					<jsp:param name="usaContatoreLista" value="true"/>
					<jsp:param name="sezioneListaVuota" value="true" />
				</jsp:include>
				<gene:campoScheda addTr="false">
					</tbody>
				</gene:campoScheda>

			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
			<c:if test='${requestScope.statoOrdine eq 3 || requestScope.statoOrdine eq 4}'>
				<gene:redefineInsert name="schedaModifica" />
				<gene:redefineInsert name="pulsanteModifica" />
			</c:if>
			<gene:redefineInsert name="pulsanteNuovo" />
			<gene:redefineInsert name="schedaNuovo" />
			
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
		
			<div id="nso-dialog-verification" title="Verifica Ordine NSO" style="display:none">
			  <p id="nso-dialog-verification-content">
				
			  </p>
			</div>
			<div id="nso-dialog-revocation" title="Revoca Ordine NSO" style="display:none">
			  <p id="nso-dialog-revocation-content">
			  	Vuoi revocare l&#39;ordine?<br>Questa operazione non si pu&ograve; annullare.
			  </p>
			</div>

		
		<gene:javaScript>

			document.forms[0].encoding="multipart/form-data";
		
		
			function schedaConfermaUploadMultiplo(){
				document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
					schedaConferma();
			}
			
			function schedaAnnullaUploadMultiplo(){
				document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
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
            
		</gene:javaScript>
		
	



