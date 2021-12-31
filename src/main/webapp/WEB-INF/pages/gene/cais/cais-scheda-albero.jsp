<%
/*
 * Created on: 18/12/2013
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="CAIS-scheda">
	<gene:setString name="titoloMaschera" value="Dettaglio categoria d'iscrizione" />
	<gene:redefineInsert name="corpo">
	
		<gene:formScheda entita="CAIS" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreCAISSchedaAlbero">
		
			<c:set var="isRenameEnabled" value="false"/>
			<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.CAIS.RinominaCategoria")}' >
				<c:set var="isRenameEnabled" value="true"/>
			</c:if>
		
			<gene:campoScheda campo="CAISIM" obbligatorio="true" modificabile="${modo eq 'NUOVO' || isRenameEnabled}"/>
			<gene:campoScheda title="Codice categoria originale" campo="CAISIM_ORIGINALE" visibile="false" campoFittizio="true" definizione="T30;0" value="${datiRiga.CAIS_CAISIM}"/>
			
			<gene:campoScheda addTr="false">
				<tr id="rowMessaggioCategoriaEsistente">
					<td colspan="2" style="padding: 10 10 10 10;"> 
						Il codice categoria indicato &egrave; gi&agrave; assegnato. Utilizzare un codice differente.
						<br>
						<span>								
							<a class="link-generico" id="annullaModificaCodice1">Annullare la modifica effettuata al codice della categoria</a>
						</span>
					</td>
				</tr>
			</gene:campoScheda>
			
			<gene:campoScheda addTr="false">
				<tr id="rowMessaggioDipendenti">
					<td colspan="2" style="padding: 10 10 10 10;"> 
						E' stato indicato un nuovo codice per questa categoria. Tuttavia questa categoria &egrave; utilizzata in altre tabelle:
						<div style="width: 550px; padding-top: 3px;" id="messaggioDipendenti"></div>
						<br>
						<span>								
							<a class="link-generico" id="confermaModificaCodice">Confermare la modifica del codice della categoria</a>
							&nbsp;&nbsp;&nbsp;
							<a class="link-generico" id="annullaModificaCodice2">Annullare la modifica effettuata al codice della categoria</a>
						</span>
						<br>
					</td>
				</tr>
			</gene:campoScheda>
			
			<gene:campoScheda campo="DESCAT" obbligatorio="true" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote"/>
			
			<c:choose>
				<c:when test="${modo eq 'NUOVO'}">
					<gene:campoScheda campo="TITCAT" value="${param.titcat}" modificabile="${empty param.titcat}" visibile="${empty param.codliv1}"/>
				</c:when>
				<c:otherwise>
					<gene:campoScheda campo="TITCAT" visibile="${empty datiRiga.CAIS_CODLIV1}"/>
				</c:otherwise>
			</c:choose>
			<gene:campoScheda campo="ISARCHI" definizione="T1" obbligatorio="true" defaultValue="2" modificabile='${modoAperturaScheda ne "NUOVO"}'/>
			
			<gene:campoScheda campo="QUAOBB"  gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoGrigio" 
				definizione="T1" defaultValue="0" visibile="${param.tiplavg eq 1 || datiRiga.CAIS_TIPLAVG eq 1}" />
			<gene:campoScheda campo="ACONTEC" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoGrigio" 
				definizione="T1" defaultValue="0" visibile="${param.tiplavg eq 1 || datiRiga.CAIS_TIPLAVG eq 1}" />
				
			<c:choose>
				<c:when test="${modo eq 'NUOVO'}">
					<gene:campoScheda campo="TIPLAVG" value="${param.tiplavg}" visibile="false" />
					<gene:campoScheda campo="CODLIV1" value="${param.codliv1}" visibile="false" />	
					<gene:campoScheda campo="CODLIV2" value="${param.codliv2}" visibile="false" />	
					<gene:campoScheda campo="CODLIV3" value="${param.codliv3}" visibile="false" />
					<gene:campoScheda campo="CODLIV4" value="${param.codliv4}" visibile="false" />
				</c:when>
				<c:otherwise>
					<gene:campoScheda campo="TIPLAVG" visibile="false" />
					<gene:campoScheda campo="CODLIV1" visibile="false" />	
					<gene:campoScheda campo="CODLIV2" visibile="false" />	
					<gene:campoScheda campo="CODLIV3" visibile="false" />
					<gene:campoScheda campo="CODLIV4" visibile="false" />
				</c:otherwise>
			</c:choose>
			
			<gene:campoScheda campo="CAISORD" visibile="false"/>				
			
			<gene:redefineInsert name="schedaNuovo" />
			
			<gene:campoScheda>	
				<td class="comandi-dettaglio" colSpan="2">
					<c:choose>
						<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
							<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
						</c:when>
						<c:otherwise>
							<INPUT type="button" class="bottone-azione" value="Ritorna alla configurazione categorie" 
								title="Ritorna alla configurazione categorie" onclick="javascript:historyVaiIndietroDi(1);">
							<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
							</c:if>
						</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</gene:campoScheda>
			
		</gene:formScheda>

	</gene:redefineInsert>
	<gene:javaScript>
	
		document.forms[0].jspPathTo.value="gene/cais/cais-scheda-albero.jsp";
		$("#rowCAIS_CAISIM td:eq(0)").css("border-top","0px");

	    $("#rowMessaggioDipendenti").hide();
	    $("#rowMessaggioCategoriaEsistente").hide();
		
		$('#CAIS_CAISIM').change(function() {
			if ($('#CAIS_CAISIM').val() != $('#CAISIM_ORIGINALE').val()) {
				controlloCAISIM($('#CAIS_CAISIM').val());
			} else {
				$("input[value='Salva']").show();
				$("td.vocemenulaterale a[href='javascript:schedaConferma();']").parent().parent().show();
				$("#rowMessaggioDipendenti").hide();
				$("#messaggioDipendenti").html("");
				$("#rowMessaggioCategoriaEsistente").hide();
				$('#CAIS_CAISIM').val($('#CAISIM_ORIGINALE').val());
			}
	    });

		$('#confermaModificaCodice').click(function() {
			$("input[value='Salva']").show();
			$("td.vocemenulaterale a[href='javascript:schedaConferma();']").parent().parent().show();
			$("#rowMessaggioDipendenti").hide();
			$("#messaggioDipendenti").html("");
		});

		$('[id^="annullaModificaCodice"]').click(function() {
			$("input[value='Salva']").show();
			$("td.vocemenulaterale a[href='javascript:schedaConferma();']").parent().parent().show();
			$("#rowMessaggioDipendenti").hide();
			$("#messaggioDipendenti").html("");
			$("#rowMessaggioCategoriaEsistente").hide();
			$('#CAIS_CAISIM').val($('#CAISIM_ORIGINALE').val());
		});
	    
	    
	   	function controlloCAISIM(caisim) {
            $.ajax({
                type: "GET",
                dataType: "json",
                async: false,
                url: "${pageContext.request.contextPath}/pg/EsisteCAISCAISIM.do",
                data: "caisim=" + caisim,
                success: function(data){
					$("#rowMessaggioDipendenti").hide();
					$("#messaggioDipendenti").html("");
                	if (data.esisteCAISCAISIM == true) {
                    	$("#rowMessaggioCategoriaEsistente").show();
                    	$("input[value='Salva']").hide();
                    	$("td.vocemenulaterale a[href='javascript:schedaConferma();']").parent().parent().hide();
                    } else {
                    	$("#rowMessaggioCategoriaEsistente").hide();
                    	$("input[value='Salva']").show();
                    	$("td.vocemenulaterale a[href='javascript:schedaConferma();']").parent().parent().show();
                    	getArchivioCategorieDipendenti($('#CAISIM_ORIGINALE').val());
                    }
                },
                error: function(e){
                    alert("Codice categoria: errore durante il controllo di univocita'");
                }
            });
         }
	    
	    function getArchivioCategorieDipendenti(caisim) {
			$.ajax({
				type: "GET",
				dataType: "json",
				async: false,
				beforeSend: function(x) {
					if(x && x.overrideMimeType) {
						x.overrideMimeType("application/json;charset=UTF-8");
					}
				},
				url: "${pageContext.request.contextPath}/pg/GetArchivioCategorieDipendenti.do",
				data: "caisim=" + caisim,
				success: function(data){
					if (data && data.length > 0) {
						var msg = "";
						$.map( data, function( item ) {
							msg = msg + '<img alt="Tabella" src="img/chiudi.gif" height="14" width="14">  <i>' + item[2] + ' (' + item[1] + ')</i><br>';
						});
						$("#messaggioDipendenti").html(msg);
						$("#rowMessaggioDipendenti").show();
						$("input[value='Salva']").hide();
                        $("td.vocemenulaterale a[href='javascript:schedaConferma();']").parent().parent().hide();
					} else {
						$("#rowMessaggioDipendenti").hide();
						$("#messaggioDipendenti").html("");
						$("input[value='Salva']").show();
                    	$("td.vocemenulaterale a[href='javascript:schedaConferma();']").parent().parent().show();
					}
				},
				error: function(e){
					alert("Codice categoria: errore durante il controllo delle dipendenze");
				}
			});
		}
	    
		
	</gene:javaScript>
</gene:template>
