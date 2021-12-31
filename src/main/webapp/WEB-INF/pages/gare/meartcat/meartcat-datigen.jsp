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
		
			<gene:formScheda entita="MEARTCAT" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreMEARTCAT" >
				
				<input type="hidden" name="opes_ngara" value="${param.opes_ngara}" />
				<input type="hidden" name="opes_nopega" value="${param.opes_nopega}" />
				<input type="hidden" name="cais_caisim" value="${param.cais_caisim}" />
				<input type="hidden" name="cais_descat" value="${param.cais_descat}" />	
				
				<gene:campoScheda campo="NGARA" visibile="false" defaultValue="${param.opes_ngara}"/>
				<gene:campoScheda campo="CODGAR1" entita="GARE"  where="GARE.NGARA = MEARTCAT.NGARA" visibile="false" />
				<gene:campoScheda campo="NOPEGA" visibile="false" defaultValue="${param.opes_nopega}"/>
				
				<gene:campoScheda>
					<td colspan="2">
						<b>Dati generali dell'articolo</b>
						<span id="messaggioCodiceArticoloAssegnato" style="display: none; color: #FF0000;">
							<br><br>
							Il codice articolo indicato &egrave; gi&agrave; assegnato. Utilizzare un codice articolo differente.
							<br><br>
						</span>
					</td>
				</gene:campoScheda>
				<gene:campoScheda campo="ID" visibile="false" />
				<gene:campoScheda campo="TIPO" obbligatorio="true" modificabile="${modo eq 'NUOVO'}"/>
				
				<gene:campoScheda addTr="false" visibile="${modo eq 'NUOVO'}">
					<tr id="messaggioCodiceArticolo">
						<td colspan="2" style="padding: 10 10 10 5;">
							<span id="messaggioAssegnazioneManuale">
								Il codice articolo viene assegnato automaticamente al salvataggio della scheda. 
								<br>
								E', tuttavia, possibile <a id="assegnaCodiceArticoloManualmente" class="link-generico">assegnare il codice articolo manualmente</a>.
							</span>
							<span id="messaggioAssegnazioneAutomatica" style="display: none;">
								Attiva nuovamente l'assegnazione <a id="assegnaCodiceArticoloAutomaticamente" class="link-generico">automatica del codice articolo</a>
							</span>
					</tr>
				</gene:campoScheda>

				<gene:campoScheda campo="COD" obbligatorio="true" />
				<gene:campoScheda title="Codice originale articolo" campo="COD_ORIGINALE" visibile="false" campoFittizio="true" definizione="T30;0" value="${datiRiga.MEARTCAT_COD}"/>
				<gene:campoScheda campo="DESCR" obbligatorio="true"/>
				<gene:campoScheda campo="DESCRTECN" obbligatorio="true"/>
				<gene:campoScheda campo="STATO" obbligatorio="true" defaultValue="1"/>															
					
				<gene:campoScheda>
					<td colspan="2">
						<br>
						<b>Informazioni necessarie per la caratterizzazione del prodotto</b>
					</td>
				</gene:campoScheda>	
				<gene:campoScheda title="Immagine ?" campo="OBBLIMG" obbligatorio="true" defaultValue="2"/>					
				<gene:campoScheda title="Descrizione aggiuntiva ?" campo="OBBLDESCAGG" obbligatorio="true" defaultValue="2"/>
				<gene:campoScheda title="Dimensioni ?" campo="OBBLDIM" obbligatorio="true" defaultValue="2"/>
				<gene:campoScheda title="Certificazioni ?" campo="OBBLCERTIF" obbligatorio="true" defaultValue="2"/>
				<gene:campoScheda campo="CERTIFRICH" obbligatorio="true" />
				
				<c:if test="${modo ne 'NUOVO'}">
					<c:set var="id" value='${gene:getValCampo(key,"ID")}' scope="request" />
					<c:set var="result" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoriMEALLARTCATFunction", pageContext, id)}' />
				</c:if>
				
				<c:if test="${modo eq 'NUOVO'  || modo eq 'MODIFICA' || (modo eq 'VISUALIZZA' && !empty  datiMEALLARTCAT)}"> 
				
					<gene:campoScheda addTr="false">
						<tbody id="sezioneMultiplaMEALLARTCAT">
					</gene:campoScheda>
					
					<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
						<jsp:param name="entita" value='MEALLARTCAT'/>
						<jsp:param name="chiave" value='${datiRiga.MEARTCAT_ID}'/>
						<jsp:param name="nomeAttributoLista" value='datiMEALLARTCAT' />
						<jsp:param name="idProtezioni" value="MEALLARTCAT" />
						<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/meallartcat/meallartcat-interno-scheda.jsp"/>
						<jsp:param name="arrayCampi" value="'MEALLARTCAT_ID_', 'MEALLARTCAT_IDARTCAT_', 'MEALLARTCAT_IDPRG_','MEALLARTCAT_IDDOCDIG_', 'W_DOCDIG_IDPRG_', 'W_DOCDIG_IDDOCDIG_', 'W_DOCDIG_DIGNOMDOC_','selezioneFile_'"/>		
						<jsp:param name="titoloSezione" value="Facsimile certificato" />
						<jsp:param name="titoloNuovaSezione" value="Nuovo facsimile certificato" />
						<jsp:param name="descEntitaVociLink" value="facsimile certificato" />
						<jsp:param name="msgRaggiuntoMax" value="i facsimile"/>
						<jsp:param name="usaContatoreLista" value="true"/>
						<jsp:param name="sezioneListaVuota" value="false" />
					</jsp:include>
	
					<gene:campoScheda addTr="false">
						</tbody>
					</gene:campoScheda>
				
				</c:if>
				
				<gene:campoScheda title="Scheda tecnica ?" campo="OBBLSCHTECN" obbligatorio="true" defaultValue="2"/>
				<gene:campoScheda title="Garanzia ?" campo="OBBLGAR" obbligatorio="true" defaultValue="2"/>
				
				<gene:campoScheda>
					<td colspan="2">
						<br>
						<b>Prezzi e unit&agrave; di misura</b>
					</td>
				</gene:campoScheda>	
				<gene:campoScheda campo="PRZUNITPER" obbligatorio="true" />
				<gene:campoScheda campo="UNIMISPRZ" obbligatorio="true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoUnitaMisuraME007"/>
				<gene:campoScheda campo="DECUNIMISPRZ" obbligatorio="true" >
					<gene:checkCampoScheda funzione='(toVal("#MEARTCAT_DECUNIMISPRZ#") >= 0 && toVal("#MEARTCAT_DECUNIMISPRZ#") <= 5)' 
						messaggio='Il valore \"N.max.decimali prezzo unitario\" deve essere compreso fra 0 e 5' obbligatorio="true" />
				</gene:campoScheda>
				<gene:campoScheda campo="UNIMISACQ" obbligatorio="true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoUnitaMisuraME007">
					<gene:calcoloCampoScheda funzione='#MEARTCAT_UNIMISPRZ#' elencocampi="MEARTCAT_UNIMISPRZ" />
				</gene:campoScheda>
				<gene:campoScheda campo="DECUNIMISACQ" obbligatorio="true" >
					<gene:calcoloCampoScheda funzione='#MEARTCAT_DECUNIMISPRZ#' elencocampi="MEARTCAT_DECUNIMISPRZ" />
					<gene:checkCampoScheda funzione='(toVal("#MEARTCAT_DECUNIMISACQ#") >= 0 && toVal("#MEARTCAT_DECUNIMISACQ#") <= 5)' 
						messaggio='Il valore \"N.max.decimali prezzo unitario per lacquisto\" deve essere compreso fra 0 e 5' obbligatorio="true" />
				</gene:campoScheda>
				<gene:campoScheda campo="QMINUNIMIS" >
					<gene:checkCampoScheda funzione='(toVal("#MEARTCAT_QMINUNIMIS#") >= 0)' 
						messaggio='Il valore \"Lotto minimo per unità di misura: valore minimo\" deve essere maggiore di 0' obbligatorio="true" />
				</gene:campoScheda>
				<gene:campoScheda campo="QMAXUNIMIS" >
					<gene:checkCampoScheda funzione='(toVal("#MEARTCAT_QMAXUNIMIS#") >= 0)' 
						messaggio='Il valore \"Lotto minimo per unità di misura: valore massimo\" deve essere maggiore di 0' obbligatorio="true" />
				</gene:campoScheda>
				<gene:campoScheda campo="QUNIMISACQ" obbligatorio="true" >
					<gene:checkCampoScheda funzione='(toVal("#MEARTCAT_QUNIMISACQ#") >= 0)' 
						messaggio='Il valore \"Lotto minimo per unità di misura \" deve essere maggiore di 0' obbligatorio="true" />
				</gene:campoScheda>
				
				<gene:campoScheda>
					<td colspan="2">
						<br>
						<b>Altri dati</b>
					</td>
				</gene:campoScheda>
				<gene:campoScheda campo="COLORE" />
				<gene:campoScheda campo="TEMPOMAXCONS" obbligatorio="true" />
				<gene:campoScheda campo="UNIMISTEMPOCONS" obbligatorio="true" />
				<gene:campoScheda campo="GPP" obbligatorio="true"/>
				<gene:campoScheda campo="CHKPROD" obbligatorio="true"/>
				<gene:campoScheda campo="NOTE" />

				<gene:campoScheda>
					<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${datiRiga.GARE_CODGAR1}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
					<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
				</gene:campoScheda>

				<c:if test="${sessionScope.listachiamante eq 'gare-pg-lista-articoli'}">
					<gene:redefineInsert name="pulsanteNuovo" />
					<gene:redefineInsert name="schedaNuovo" />
				</c:if>
				
				<c:if test="${modo ne 'NUOVO'}">
					<c:set var="resultdiritti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDirittiMEARTCATFunction",pageContext,id)}'/>
				</c:if>
				<c:if test="${!empty isMEARTCATCancellabile && isMEARTCATCancellabile eq 'false'}">
					<gene:redefineInsert name="pulsanteModifica" />
					<gene:redefineInsert name="schedaModifica" />
				</c:if>
				
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


				
				
				<gene:fnJavaScriptScheda funzione="gestioneTIPO('#MEARTCAT_TIPO#')" elencocampi="MEARTCAT_TIPO" esegui="true" />
				<gene:fnJavaScriptScheda funzione="gestioneOBBLCERTIF('#MEARTCAT_OBBLCERTIF#')" elencocampi="MEARTCAT_OBBLCERTIF" esegui="true" />
				<gene:fnJavaScriptScheda funzione="gestionePRZUNITPER('#MEARTCAT_PRZUNITPER#')" elencocampi="MEARTCAT_PRZUNITPER" esegui="true" />
								
			</gene:formScheda>
		
	
		
		<gene:javaScript>

			document.forms[0].encoding="multipart/form-data";
		
			<c:if test="${modo eq 'NUOVO'}">
				if ($("#MEARTCAT_COD").val() == null || $("#MEARTCAT_COD").val() == "") {
					$("#rowMEARTCAT_COD").hide();
					$("#messaggioAssegnazioneManuale").show();
					$("#messaggioAssegnazioneAutomatica").hide();
				} else {
					$("#rowMEARTCAT_COD").show();
					$("#messaggioAssegnazioneManuale").hide();
					$("#messaggioAssegnazioneAutomatica").show();
				}
			</c:if>
			
			<c:if test="${modo eq 'VISUALIZZA'}">
				$('[id^="rowtitoloMEALLARTCAT_"]').hide();
			</c:if>
		
			function schedaConfermaUploadMultiplo(){
				document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
				var QMINUNIMIS = getValue("MEARTCAT_QMINUNIMIS");
				var QMAXUNIMIS = getValue("MEARTCAT_QMAXUNIMIS");
				if(!confrontoValori(QMINUNIMIS,QMAXUNIMIS)){
					clearMsg();
					outMsg("Lotto minimo per unità di misura: il valore minimo deve essere minore o uguale del valore massimo", "ERR");
					onOffMsg();
				}else{
					schedaConferma();
				}
			}
			
			function schedaAnnullaUploadMultiplo(){
				document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
				schedaAnnulla();
			}

			$('#assegnaCodiceArticoloManualmente').click(function() {
				$("#messaggioAssegnazioneManuale").hide();
				$("#messaggioAssegnazioneAutomatica").show();
		    	$("#rowMEARTCAT_COD").show();
		    });
		    
		    $('#assegnaCodiceArticoloAutomaticamente').click(function() {
		    	$("#messaggioAssegnazioneManuale").show();
				$("#messaggioAssegnazioneAutomatica").hide();
		    	$("#rowMEARTCAT_COD").hide();
		    	$("#MEARTCAT_COD").val('');
		    	$("#messaggioCodiceArticoloAssegnato").hide();
		    	$("input[value='Salva']").show();
                $("td.vocemenulaterale a[href='javascript:schedaConferma();']").parent().parent().show();
		    });
		
			$('#MEARTCAT_COD').change(function() {
				if ($('#MEARTCAT_COD').val() != $('#COD_ORIGINALE').val()) {
				   	esisteMEARTCAT_COD($('#MEARTCAT_NGARA').val(),$('#MEARTCAT_COD').val());
				} else {
					$("#messaggioCodiceArticoloAssegnato").hide();
					$("input[value='Salva']").show();
					$("td.vocemenulaterale a[href='javascript:schedaConferma();']").parent().parent().show();
				}
		    });
		
			function gestioneTIPO(value) {
				if (value == '1') {
					$("#rowMEARTCAT_GPP").show();
					$("#rowMEARTCAT_CHKPROD").show();
					$("#rowMEARTCAT_COLORE").show();
					$("#rowMEARTCAT_OBBLDIM").show();
				} else {
					$("#rowMEARTCAT_GPP").hide();
					$("#rowMEARTCAT_CHKPROD").hide();
					$("#rowMEARTCAT_COLORE").hide();
					$("#rowMEARTCAT_OBBLDIM").hide();
					$("#MEARTCAT_COLORE").val('');
					$("#MEARTCAT_GPP").val('');
					$("#MEARTCAT_CHKPROD").val('');
					$("#MEARTCAT_OBBLDIM").val('2');
				}
			}

			function gestioneOBBLCERTIF(value) {
				if (value == '1') {
					$("#rowMEARTCAT_CERTIFRICH").show();
					$("#sezioneMultiplaMEALLARTCAT").show();
				} else {
					$("#rowMEARTCAT_CERTIFRICH").hide();
					$("#MEARTCAT_CERTIFRICH").val('');
					$("#sezioneMultiplaMEALLARTCAT").hide();
				}
			}
			
			function gestionePRZUNITPER(value) {
				if (value == '1') {
					$("#rowMEARTCAT_QUNIMISACQ").show();
				} else {
					$("#rowMEARTCAT_QUNIMISACQ").hide();
					if (value == '4')
						$("#MEARTCAT_QUNIMISACQ").val('0');
					else
						$("#MEARTCAT_QUNIMISACQ").val('');
				}
				
				if (value == '3') {
					$("#rowMEARTCAT_UNIMISACQ").show();
					$("#rowMEARTCAT_DECUNIMISACQ").show();
					$("#rowMEARTCAT_QMINUNIMIS").show();
					$("#rowMEARTCAT_QMAXUNIMIS").show();
				}else if(value == '2'){ 
					$("#rowMEARTCAT_QMINUNIMIS").show();
					$("#rowMEARTCAT_QMAXUNIMIS").show();
					$("#rowMEARTCAT_UNIMISACQ").hide();
					$("#rowMEARTCAT_DECUNIMISACQ").hide();
					$("#MEARTCAT_UNIMISACQ").val($("#MEARTCAT_UNIMISPRZ").val());
					$("#MEARTCAT_DECUNIMISACQ").val($("#MEARTCAT_DECUNIMISPRZ").val());
					//$("#MEARTCAT_QMINUNIMIS").val('');
					//$("#MEARTCAT_QMAXUNIMIS").val('');
				}else {
					$("#rowMEARTCAT_UNIMISACQ").hide();
					$("#rowMEARTCAT_DECUNIMISACQ").hide();
					$("#rowMEARTCAT_QMINUNIMIS").hide();
					$("#rowMEARTCAT_QMAXUNIMIS").hide();
					$("#MEARTCAT_UNIMISACQ").val($("#MEARTCAT_UNIMISPRZ").val());
					$("#MEARTCAT_DECUNIMISACQ").val($("#MEARTCAT_DECUNIMISPRZ").val());
					$("#MEARTCAT_QMINUNIMIS").val('');
					$("#MEARTCAT_QMAXUNIMIS").val('');
				}
			}
			
			function esisteMEARTCAT_COD(ngara, cod) {
                $.ajax({
                    type: "GET",
                    dataType: "json",
                    async: false,
                    beforeSend: function(x) {
        			if(x && x.overrideMimeType) {
            			x.overrideMimeType("application/json;charset=UTF-8");
				       }
    				},
                    url: "${pageContext.request.contextPath}/pg/EsisteMEARTCAT_COD.do",
                    data: "ngara=" + ngara + "&cod=" + cod,
                    success: function(data){
                    	if (data.esisteMEARTCAT_COD == true) {
                   			$("#MEARTCAT_COD").parent().append($("#messaggioCodiceArticoloAssegnato"));
                        	$("#messaggioCodiceArticoloAssegnato").show();
                        	$("input[value='Salva']").hide();
                        	$("td.vocemenulaterale a[href='javascript:schedaConferma();']").parent().parent().hide();
                        } else {
                        	$("#messaggioCodiceArticoloAssegnato").hide();
                        	$("input[value='Salva']").show();
                        	$("td.vocemenulaterale a[href='javascript:schedaConferma();']").parent().parent().show();
                        }
                    },
                    error: function(e){
                        alert("Codice articolo: errore durante il controllo di univocita'");
                    }
                });
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
            
            function confrontoValori(valore1,valore2){
            	if(valore1=="" || valore1==null || valore2 == "" || valore2==null)
            		return true;
            	else if(parseInt(valore1) <= parseInt(valore2))
            		return true;
            	else
            		return false;
            }
            
            var selezionaPaginaDefault = selezionaPagina;
			var selezionaPagina = selezionaPaginaCustom;
			function selezionaPaginaCustom(pageNumber){
				var ngara=getValue("MEARTCAT_NGARA");
				var listachiamante = "${sessionScope.listachiamante}";
				var isRicercaMercato = "false";
				if(listachiamante=="meric-pg-articoli-albero.jsp" || listachiamante=="meric-pg-articoli-carrello.jsp")
					isRicercaMercato = "true";
				document.pagineForm.action += "&ngara=" + ngara + "&isRicercaMercato=" + isRicercaMercato;
				selezionaPaginaDefault(pageNumber);
			}
		</gene:javaScript>
		
	



