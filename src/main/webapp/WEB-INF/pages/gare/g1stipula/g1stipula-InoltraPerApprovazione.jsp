<%
/*
 * Created on: 31-08-2010
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
		Finestra per l'attivazione della funzione 'Pubblica su portale Alice Gare'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:choose>
	<c:when test='${not empty requestScope.InoltroPerApprovazioneEseguito}' >
		<gene:template file="popup-message-template.jsp">
			<c:choose>
				<c:when test='${!empty param.idStipula}'>
					<c:set var="idStipula" value="${param.idStipula}" />
				</c:when>
				<c:otherwise>
					<c:set var="idStipula" value="${idStipula}" />
				</c:otherwise>
			</c:choose>
			<gene:redefineInsert name="corpo">
			<gene:setString name="titoloMaschera" value='Inoltra per approvazione' />
			<c:set var="contextPath" value="${pageContext.request.contextPath}" />
			<c:if test='${requestScope.InoltroPerApprovazioneEseguito eq "1"}' >
			</c:if>
			<c:set var="msg" value="Inoltro completato." />
				<c:if test='${InoltroPerApprovazioneEseguito eq "-1"}' >
					<c:set var="msg" value="${requestScope.msg }" />
				</c:if>
								
			
			<tr>
				<td colSpan="2">
					<br>
						<b>${msg}</b>
					<br>&nbsp;
					<br>&nbsp;
				</td>	
			<tr>
			
			<gene:redefineInsert name="buttons">
				<input type="button" class="bottone-azione"  id="cancel" value="Chiudi" title="Chiudi" onclick="chiudi();"/>&nbsp;&nbsp;
			</gene:redefineInsert>
			<gene:javaScript>
			
			<c:if test='${requestScope.InoltroPerApprovazioneEseguito eq "1"}' >		
				window.opener.historyReload();
				window.close();
			</c:if>
			
			window.onload = function () { 
				window.opener.bloccaRichiesteServer();
				window.opener.location = "${contextPath}/History.do?"+csrfToken+"&metodo=reload";
			}
			
			function chiudi(){
				window.close();
			}
			function conferma(){
				window.close();
			}
			function download(){
				document.formDownload.submit();
			}
			</gene:javaScript>
			</gene:redefineInsert>
		</gene:template>
	</c:when>
	<c:otherwise>

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${contextPath}/js/jHtmlArea-0.7.5.min.js"></script> 
	<link rel="Stylesheet" type="text/css" href="${contextPath}/css/jquery/jHtmlArea/jHtmlArea.css" />
	<script type="text/javascript" src="${contextPath}/js/common-gare.js"></script>
	
		<style type="text/css">
			label.error {
				float: none;
				color: white;
 				background-color: #E40000; 
				padding-left: 5px;
				padding-right: 5px;
				padding-top: 2px;
				padding-bottom: 2px;
				vertical-align: middle;
				margin-left: 5px;
 				border: 1px solid #BA0000; 
 				-moz-border-radius-topleft: 4px; 
 				-webkit-border-top-left-radius: 4px; 
 				-khtml-border-top-left-radius: 4px; 
 				border-top-left-radius: 4px; 
				-moz-border-radius-topright: 4px;
				-webkit-border-top-right-radius: 4px;
				-khtml-border-top-right-radius: 4px;
				border-top-right-radius: 4px;
 				-moz-border-radius-bottomleft: 4px; 
 				-webkit-border-bottom-left-radius: 4px; 
 				-khtml-border-bottom-left-radius: 4px; 
 				border-bottom-left-radius: 4px; 
				-moz-border-radius-bottomright: 4px;
				-webkit-border-bottom-right-radius: 4px;
				-khtml-border-bottom-right-radius: 4px;
				border-bottom-right-radius: 4px;
			}
		 
			.error {
				color:Red;
			}
			
			.realperson-challenge {
				display: inline-block;
				vertical-align: bottom;
				color: #000;
				padding-right: 5px;
			}
			.realperson-text {
				font-family: "Courier New",monospace;
				font-size: 5px;
				font-weight: bold;
				letter-spacing: -1px;
				line-height: 2px;
			}
			.realperson-regen {
				padding-top: 4px;
				font-size: 10px;
				text-align: left;
				padding-left: 10px;
				cursor: pointer;
				font-style: italic;
				color: #454545;
			}
			.realperson-disabled {
				opacity: 0.75;
				filter: Alpha(Opacity=75);
			}
			.realperson-disabled .realperson-regen {
				cursor: default;
			}
			
			.ui-autocomplete {
				max-height: 200px;
				overflow-y: auto;
				overflow-x: hidden;
				max-width: 700px;
			}
			
			.ui-corner-all, .ui-corner-top, .ui-corner-left, .ui-corner-tl { -moz-border-radius-topleft: 0px; -webkit-border-top-left-radius: 0px; -khtml-border-top-left-radius: 0px; border-top-left-radius: 0px; }
			.ui-corner-all, .ui-corner-top, .ui-corner-right, .ui-corner-tr { -moz-border-radius-topright: 0px; -webkit-border-top-right-radius: 0px; -khtml-border-top-right-radius: 0px; border-top-right-radius: 0px; }
			.ui-corner-all, .ui-corner-bottom, .ui-corner-left, .ui-corner-bl { -moz-border-radius-bottomleft: 0px; -webkit-border-bottom-left-radius: 0px; -khtml-border-bottom-left-radius: 0px; border-bottom-left-radius: 0px; }
			.ui-corner-all, .ui-corner-bottom, .ui-corner-right, .ui-corner-br { -moz-border-radius-bottomright: 0px; -webkit-border-bottom-right-radius: 0px; -khtml-border-bottom-right-radius: 0px; border-bottom-right-radius: 0px; }
			.ui-state-hover, .ui-widget-content .ui-state-hover, .ui-widget-header .ui-state-hover, .ui-state-focus, .ui-widget-content .ui-state-focus, .ui-widget-header .ui-state-focus { border: 1px solid #fbcb09; background: #fdf5ce; font-weight: bold; color: #AD3600; }

		</style>
	
			
</gene:redefineInsert>



<c:set var="modo" value="NUOVO" scope="request" />

	<c:choose>
		<c:when test='${!empty param.idStipula}'>
			<c:set var="idStipula" value="${param.idStipula}" />
		</c:when>
		<c:otherwise>
			<c:set var="idStipula" value="${idStipula}" />
		</c:otherwise>
	</c:choose>
	
	
	<gene:setString name="titoloMaschera" value='Inoltra per approvazione' />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="G1STIPULA" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInoltraPerApprovazione" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreInoltraPerApprovazione">
			
	<gene:campoScheda nome="msgPagina">
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO"}' >
					${requestScope.msg }
				</c:when>
				<c:otherwise>
					${requestScope.MsgConferma}
					<c:if test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "WARNING"}' >
						${requestScope.msg }
					</c:if>
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>

		<gene:campoScheda campo="ID" campoFittizio="true" defaultValue="${param.idStipula}" visibile="false" definizione="N12;0"/>
		<gene:campoScheda campo="STATO" campoFittizio="true" defaultValue="${requestScope.statoStipula}" visibile="false" definizione="N12;0"/>
		<gene:campoScheda campo="CONMITT" campoFittizio="true" defaultValue="${requestScope.sysconMittenteMail}" visibile="false" definizione="N12;0"/>
		
		
		<c:if test="${requestScope.controlloSuperato ne 'NO'}">
			<gene:campoScheda campo="COMMSGOGG" campoFittizio="true" definizione="T300;0;;;COMMSGOGG" obbligatorio="true" value="${requestScope.oggettoMail}"/>
			<gene:campoScheda campo="COMINTEST" campoFittizio="true" definizione="T2;0;;SN;COMINTEST" value="1" visibile="false"/>
			<gene:campoScheda nome="intestazione" visibile="true"> 
				<td class="etichetta-dato">Intestazione</td>
				<td class="valore-dato">Gentile <i>Utente</i>
				</td>
			</gene:campoScheda>
			<gene:campoScheda campo="COMMSGTES" campoFittizio="true" definizione="T2000;0;;CLOB;COMMSGTES" obbligatorio="true" value="${requestScope.testoMail}" gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoTestoComunicazioneHTML"/>
			
			<gene:campoScheda title="Attuale Contract Manager" campo="COMMITT" campoFittizio="true" definizione="T60;0;;;COMMITT" value="${requestScope.mittenteMail}" modificabile="false" visibile="true"/>
			
				<gene:campoScheda addTr="false">
				<tr id="sezionecodiceusr" style="display: none;">
					<td class="etichetta-dato">Nuovo Contract Manager (*)</td>
					<td class="valore-dato">
						<select id="codiceusrnuovo" name="codiceusrnuovo" ></select>
						<select id="codiceusrnuovo_filtro" name="codiceusrnuovo_filtro" style="display: none;"></select>
						<span id="codiceusr" name="codiceusr" style="display: none;"></span>
					</td>
				</tr>
			</gene:campoScheda>	
			
			<gene:campoScheda addTr="false">
				<tr id="rowG_PERMESSI_AVVISO">
					<td colspan="2">
						<br>
						<b>ATTENZIONE: </b>Il Contract Manager selezionato non possiede i privilegi di accesso alla stipula. Procedendo gli verranno assegnati automaticamente.
						<br><br>
					</td>
				</tr>
			</gene:campoScheda>
			
			<gene:campoScheda entita="USRSYS" campo="SYSCON" visibile="false"/> 


			
			
				
			
						
			<gene:fnJavaScriptScheda funzione="modifyHTMLEditor('#COMMSGTIP#')" elencocampi="COMMSGTIP" esegui="false" />
			<gene:fnJavaScriptScheda funzione="gestioneCOMINTEST('#COMINTEST#')" elencocampi="COMINTEST" esegui="true" />
			
			
		</c:if>
		
		
		<input type="hidden" name="idStipula" id="idStipula" value="${idStipula}" />
		<input type="hidden" name="idconfi" id="idconfi" value="${idconfi }" />
		
		
	</gene:formScheda>
  </gene:redefineInsert>
	
	<gene:redefineInsert name="buttons">
			<c:choose>
				<c:when test='${(not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO")}'>
					<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
				</c:when>
				<c:otherwise>
					<INPUT type="button" id="pulsanteConferma" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma()">&nbsp;
					<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
				</c:otherwise>
			</c:choose>
	</gene:redefineInsert>

	
	
	<gene:javaScript>
	
	 var tempo = 400;
	_inizializzaCodiceUsrFiltrato();
	
	_popolaTabellatoCodiceUsr();
	
	$("#sezionecodiceusr").show();
	$('#codiceusrnuovo').hide();
	$('#codiceusr').show();
	
	$("#rowG_PERMESSI_AVVISO").css("color", "blue")
	$("#rowG_PERMESSI_AVVISO").hide();


	
/**
 * Inizializzazione del widget per la gestione del filtro nella lista del codice utente
 * viene sfruttato il plugin autocomplete
 */
function _inizializzaCodiceUsrFiltrato() {
    $.widget( "custom.comboboxUsr", {
      _create: function() {
        this.wrapper = $( "<span>" )
          .addClass( "custom-combobox" )
          .insertAfter( this.element );
 
        this.element.hide();
        this._createAutocomplete();
        this._createShowAllButton();
      },
 
      _createAutocomplete: function() {
        var selected = this.element.children( ":selected" ),
          value = selected.val() ? selected.text() : "";
 
        this.input = $( "<input>" )
          .appendTo( this.wrapper )
          .val( value )
          .attr( {id: "codiceusr_filtro" , name:  "codiceusr_filtro", size: "45"})
          .addClass( "custom-combobox-input ui-widget ui-widget-content  ui-corner-left" )
          .autocomplete({
            delay: 0,
            minLength: 0,
            source: $.proxy( this, "_source" )
          })
          .tooltip({
            classes: {
              "ui-tooltip": "ui-state-highlight"
            }
          });
 
        this._on( this.input, {
          autocompleteselect: function( event, ui ) {
            ui.item.option.selected = true;
            this._trigger( "select", event, {
              item: ui.item.option
            });
			//alert(ui.item.option.value);
			//alert(ui.item.option.text);
			
            $('#codiceusrnuovo').val(ui.item.option.value);
            $('#codiceusrnuovo').trigger("change");
            $('#codiceusr_filtro').attr( "title", ui.item.option.text );
			
				$.ajax({
					type: "POST",
					async: false,
					dataType: "json",
					url: "pg/GetPermessiUtenteStipula.do",
					data : {
						idStipula: $("#idStipula").val(),
						cm: ui.item.option.value
					},
					success: function(json) {
						if (json) {
							if (json.esito == true) {
								$("#rowG_PERMESSI_AVVISO").hide();
							}else{
								$("#rowG_PERMESSI_AVVISO").show();
							}
						}
					},
					error: function(e) {
						alert('error');
						_nowait();
					}
				});					
						

			
          },
 
          autocompletechange: "_removeIfInvalid"
        });
      },
 
      _createShowAllButton: function() {
        var input = this.input,
          wasOpen = false;
 
        $( "<a>" )
          .attr( "tabIndex", -1 )
          .attr( "title", "Visualizza tutto" )
          .tooltip()
          .appendTo( this.wrapper )
          .button({
            icons: {
              primary: "ui-icon-triangle-1-s"
            },
            text: false
          })
          .removeClass( "ui-corner-all" )
          .addClass( "custom-combobox-toggle ui-corner-right" )
          .on( "mousedown", function() {
            wasOpen = input.autocomplete( "widget" ).is( ":visible" );
          })
          .on( "click", function() {
            input.trigger( "focus" );
 
            // Close if already visible
            if ( wasOpen ) {
              return;
            }
 
            // Pass empty string as value to search for, displaying all results
            input.autocomplete( "search", "" );
          });
      },
 
      _source: function( request, response ) {
        var matcher = new RegExp( $.ui.autocomplete.escapeRegex(request.term), "i" );
        response( this.element.children( "option" ).map(function() {
          var text = $( this ).text();
          if ( this.value && ( !request.term || matcher.test(text) ) )
            return {
              label: text,
              value: text,
              option: this
            };
        }) );
      },
 
      _removeIfInvalid: function( event, ui ) {
 
        // Selected an item, nothing to do
        if ( ui.item ) {
        	return;
        }else{
        	$('#codiceusrnuovo').val('');
        	$('#codiceufficionuovo').empty();
        	$('#codiceusr_filtro').attr( "title", "" );
            $('#codiceufficio_filtro').val('');
            $('#codiceufficio_filtro').attr( "title", "" );
        }
 
        // Search for a match (case-insensitive)
        var value = this.input.val(),
          valueLowerCase = value.toLowerCase(),
          valid = false;
        this.element.children( "option" ).each(function() {
          if ( $( this ).text().toLowerCase() === valueLowerCase ) {
            this.selected = valid = true;
            return false;
          }
        });
 
        // Found a match, nothing to do
        if ( valid ) {
        	return;
        }
 
        // Remove invalid value
        this.input
          .val( "" )
          .attr( "title", "Codice " + value + " non trovato nell'elenco" )
          .tooltip( "open" );
        this.element.val( "" );
        this._delay(function() {
          this.input.tooltip( "close" ).attr( "title", "" );
        }, 2500 );
        this.input.autocomplete( "instance" ).term = "";
        
        //Si deve sbiancare se valorizzato codice codiceusrnuovo
        $('#codiceusrnuovo').val("");
        $('#codiceufficionuovo').empty();
        $('#codiceusr_filtro').attr( "title", "" );
        $('#codiceufficio_filtro').val('');
        $('#codiceufficio_filtro').attr( "title", "" );
    	
      },
 
      _destroy: function() {
        this.wrapper.remove();
        this.element.show();
      }
    });
 
    $( "#codiceusrnuovo_filtro" ).comboboxUsr();
    
    $('.codiceusr_filtro').tooltip().click(function() {
        $('.codiceusr_filtro').tooltip( "close");
    });

    
  }
	
	
	function _popolaTabellatoCodiceUsr(){
	_wait();
	$.ajax({
		type: "POST",
		async: false,
		dataType: "json",
		url: "pg/GetListaUtentiStipula.do",
		data : {
			idStipula: $("#idStipula").val()
		},
		success: function(json) {
			if (json) {
				if (json.esito == true) {
					//json.data contiene la lista dei valori degli utenti
					if (json.data != null) {
						var numElementi = json.iTotalRecords;
						var vettoreDati = new Array(numElementi);
						$("#codiceusrnuovo").append($("<option/>", {value: "" ,text: "" }));
						$("#codiceusrnuovo_filtro").append($("<option/>", {value: "" ,text: "" }));
						$.map( json.data, function( item ) {
							vettoreDati.push(item.codiceusr + " - " + item.descrizioneusr);
						});
						//Ordinamento dei valori
						vettoreDati.sort();
						var codiceusr;
						var descrizione;
						var autori;
						vettoreDati.forEach(function (item, index, array) {
							//alert(item);
							codiceusr = item.split(" - ")[0];
							//alert(codiceusr);
							descrizione = item.substring(item.indexOf(" - ") + 3);
							//alert(descrizione);
							$("#codiceusrnuovo").append($("<option/>", {value: codiceusr, text: descrizione }));
							$("#codiceusrnuovo_filtro").append($("<option/>", {value: codiceusr, text: item }));
						});
						
						//Se  presente un solo valore nel tabellato allora lo si seleziona
						if($("#codiceusrnuovo option").length == 2 ){
							$("#codiceusrnuovo option").eq(1).prop('selected', true);
							$("#codiceusrnuovo_filtro option").eq(1).prop('selected', true);
							
						}
						
						_nowait();
					}
				} else {
					var messaggio = json.messaggio;
					_nowait();
					$('#codiceusrnuovo').find('option').not('[value=123]').remove();
					$('#codiceusrnuovo_filtro').empty();
					$('#codiceusr_filtro').val('');
					$('#codiceusr_filtro').attr('title','');
				}
			}
		},
		error: function(e) {
			var messaggio = "Non e' stato possibile caricare i valori degli utenti";
			_nowait();
			$('#codiceusrnuovo').find('option').not('[value=123]').remove();
			$('#codiceusrnuovo_filtro').empty();
			$('#codiceusr_filtro').val('');
			$('#codiceusr_filtro').attr('title','');
		}
	});
}
	
	
	
	
	
	
		function conferma() {
			codiceusrnuovo = $("#codiceusrnuovo").val();
			
			if(codiceusrnuovo!=null && codiceusrnuovo!='' && codiceusrnuovo!=""){
				setValue("USRSYS_SYSCON",codiceusrnuovo);
				document.forms[0].jspPathTo.value="gare/g1stipula/g1stipula-InoltraPerApprovazione.jsp";
				schedaConferma();
			}else{
				alert("Selezionare il nuovo Contract Manager");
			}
		}
		
		
		function annulla(){
			window.close();
		}
			
			function modifyHTMLEditor(valore){
				if (valore == '1')
				 	$('#COMMSGTES').htmlarea('hideHTMLView');
				else
				 	$('#COMMSGTES').htmlarea('showHTMLView');
			}
			
			
			
			function upperCase(campo, valore){
				document.getElementById(campo).value=valore.toUpperCase();
			}

			function gestioneCOMINTEST(comintest){
				document.getElementById("rowintestazione").style.display = (comintest=='1' ? '':'none');
			}
					
			function controlloOggettoTestoLettera(){
				var esito = "OK";
				var oggetto = getValue("COMMSGOGG");
				var testo = getValue("COMMSGTES");
				if(oggetto==null || oggetto==""){
					alert("Il campo 'Oggetto' è obbligatorio");
					esito = "NOK";
				} else if(testo==null || testo==""){
					alert("Il campo 'Testo' è obbligatorio");
					esito = "NOK";
				}
				return esito;
			}
		
		
		function _wait() {
			document.getElementById('bloccaScreen').style.visibility = 'visible';
			$('#bloccaScreen').css("width", $(document).width());
			$('#bloccaScreen').css("height", $(document).height());
			document.getElementById('wait').style.visibility = 'visible';
			$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200 });
		}

		function _nowait() {
			document.getElementById('bloccaScreen').style.visibility = 'hidden';
			document.getElementById('wait').style.visibility = 'hidden';
		}
		
		
	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>