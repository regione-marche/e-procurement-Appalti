
<%
	/*
	 * Created on 24-11-2023
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:choose>
	<c:when test='${not empty param.codGara}'>
		<c:set var="codGara" value="${param.codGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="codGara" value="${codGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.idStipula}'>
		<c:set var="idStipula" value="${param.idStipula}" />
	</c:when>
	<c:otherwise>
		<c:set var="idStipula" value="${idStipula}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codStipula}'>
		<c:set var="codStipula" value="${param.codStipula}" />
	</c:when>
	<c:otherwise>
		<c:set var="codStipula" value="${codStipula}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codContrattoErp}'>
		<c:set var="codContrattoErp" value="${param.codContrattoErp}" />
	</c:when>
	<c:otherwise>
		<c:set var="codContrattoErp" value="${codContrattoErp}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.idPadre}'>
		<c:set var="idPadre" value="${param.idPadre}" />
	</c:when>
	<c:otherwise>
		<c:set var="idPadre" value="${idPadre}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.modo}'>
		<c:set var="modo" value="${param.modo}" />
	</c:when>
	<c:otherwise>
		<c:set var="modo" value="${modo}" />
	</c:otherwise>
</c:choose>


<gene:template file="popup-template.jsp" gestisciProtezioni="false" schema="GARE">
	<gene:setString name="titoloMaschera" value="Associa contratto SAP" />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>

		<div id="zonaInputNumeroContratto">
	       <c:choose>
	       		<c:when test="${modo ne 'VISUALIZZA'}">
	       		   <br>
				   Inserendo il numero contratto Sap verranno riportate le informazioni relative al contratto. 
				   <br>   
				</c:when>
	       </c:choose>
	  	</div>


		<table class="lista" border=0>
			<tr>
			    <td colspan=2>
			        <c:choose>
			        	<c:when test="${modo ne 'VISUALIZZA'}">
						    <table style="font-family: Verdana, Arial, Helvetica, sans-serif;padding: 0px;border-spacing: 10px;font-size: 11px;">
						      <tr>
						         <td style="BACKGROUND-COLOR: #EFEFEF;color: #000000; font-weight: bold;">Numero contratto SAP</td>
							     <td><input class="testo" type="text" id="numeroContrattoErp" name="numeroContrattoErp" title="Numero Contratto SAP"  size="25" value="" maxlength="25"></td>	
							  </tr>
							</table>
						</c:when>
					</c:choose>
				</td>
			</tr>
			<tr> 
			    <td colSpan="2">
					<div id="erroreContrattoMancanteContainer">
					  <br><br>
					  <span id="messaggioContrattoMancante" style="color:red">
					  </span>
					  <br>
					</div>
				</td>
			</tr>
			<tr>
			    <td  colSpan="2">
					<div id="importResult">
						<span>Dati relativi al contratto sap:</span>
						<br>
						<table class="dettaglio-tab">
							<tr>
							    <td class="etichetta-dato"><span><b>Numero contratto: </b></span></td> 
							    <td><span id="numeroContratto"></span></td>
							</tr>
							<tr>
						        <td class="etichetta-dato"><b>Variante: </b></td>
						        <td><span id="variante"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Codice CIG: </b></td>
						        <td><span id="codiceCig"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Rilascio: </b></td>
						        <td><span id="rilascio"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Classe: </b></td>
						        <td><span id="classe"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Data creazione: </b></td>
						        <td><span id="dataCreazione"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Owner: </b></td>
						        <td><span id="owner"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Importo variante: </b></td>
						        <td><span id="importoVariante"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Importo contratto: </b></td>
						        <td><span id="importoContratto"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Data Rilascio: </b></td>
						        <td><span id="dataRilascio"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Area acquisti: </b></td>
						        <td><span id="areaAcquisti"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Rup: </b></td>
						        <td><span id="rup"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Oggetto: </b></td>
						        <td><span id="oggetto"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Trattativa SAP: </b></td>
						        <td><span id="trattativaSap"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Categoria merceologica prevalente: </b></td>
						        <td><span id="categoriaMerceologicaPrevalente"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Codice fornitore SAP: </b></td>
						        <td><span id="codiceFornitoreSap"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Piva: </b></td>
						        <td><span id="piva"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Denominazione fornitore: </b></td>
						        <td><span id="denominazioneFornitore"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Piva ( estero ): </b></td>
						        <td><span id="pivaEstero"></span></td>
						    </tr>
						    <tr>
						        <td class="etichetta-dato"><b>Data inizio validità: </b></td>
						        <td><span id="dataInizioValidita"></span></td>
						    </tr>

                        </table>
                        
						<div id="messaggioContainer">
							<span id="messaggio" style="color:blue"></span>
							<br>
						</div>
						<br>
					</div>	
			    </td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<c:choose>
					   <c:when test="${modo eq 'VISUALIZZA'}">
							<INPUT type="button"  id="Chiudi"  class="bottone-azione" value='Chiudi' title='Chiudi' onclick="window.close();">&nbsp;
					   </c:when>
					   <c:otherwise>
	    					<INPUT type="button"  id="Cerca"   class="bottone-azione" value='Cerca' title='Cerca' onclick="checkNumeroContrattoErp($('#numeroContrattoErp').val(),false);">&nbsp;
				    		<INPUT type="button"  id="Associa" class="bottone-azione" value='Associa' title='Associa contratto' onclick=" associaContrattoErp( $('#numeroContrattoErp').val() );">&nbsp;
							<INPUT type="button"  id="Chiudi"  class="bottone-azione" value='Chiudi' title='Chiudi' onclick="window.close();">&nbsp;					   
					   </c:otherwise>					
					</c:choose>
				</td>
			</tr>			
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
	    $("#importResult").hide();
	    $("#erroreContrattoMancanteContainer").hide();
	    $("#messaggio").hide();
	    $("#Associa").hide();	   
	    
		/*
		* Conversione di un oggetto Data in un oggetto Stringa con formato
		* gg/mm/aaaa 
		*/
		function estraiDataFormattata(input) {

		  if (!input || typeof input.time !== 'number') {
			console.log("Timestamp non valido o mancante");
			return null;
		  }

		  const data = new Date(input.time);

		  // Debug diretto sui valori numerici
		  const giorno = data.getDate();
		  const mese = data.getMonth() + 1;
		  const anno = data.getFullYear();


		  if (isNaN(giorno) || isNaN(mese) || isNaN(anno)) {
			console.log("Uno o più valori di data non validi");
			return null;
		  }

		  const dataFormattata = String(giorno).padStart(2, '0') + '/' 
                     + String(mese).padStart(2, '0') + '/' 
                     + anno;

		  return dataFormattata;
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
		
		function isStringaNumerica(stringa) {
		  // Usiamo l'espressione regolare per controllare se la stringa contiene solo numeri
		  // Il pattern ^\d+$ corrisponde a una stringa composta da uno o più numeri
		  var espressioneRegolare = /^\d+$/;
		  
		  // Usiamo il metodo test della RegExp per controllare se la stringa rispetta il pattern
		  return espressioneRegolare.test(stringa);
		}			    	    	    
		
		function checkNumeroContrattoErp(numContrattoErp,isVisualizzazione) {
		    _wait();
		    
		    if ( !isStringaNumerica(numContrattoErp) || (numContrattoErp.length!=10) ){
	           
	            $('#messaggioContrattoMancante').html("Attenzione: il valore inserito deve essere numerico e deve avere esattamente 10 caratteri");
                $("#erroreContrattoMancanteContainer").show();
	            _nowait();
	            return;
	        }//if
                   
			$.ajax({
				url: "pg/CheckNumeroContrattoErp.do",
				type: 'POST',
				data: {
				        numeroContrattoErp: numContrattoErp
				       ,idStipula: "${idStipula}"
				       ,isVisualizzazione: isVisualizzazione
				      },
				cache: false,
				dataType: "json",
				success: function(rispostaJson) {
				
				    var isContrattoErpGiaAssociato = Boolean(rispostaJson.isContrattoErpGiaAssociato);				 				   
				    
					if(rispostaJson.esito === 'ko'){
					    $('#messaggioContrattoMancante').html(rispostaJson.messaggio);
                        $("#erroreContrattoMancanteContainer").show();
                        $("#importResult").hide();
                                               
                        if (rispostaJson.attivaPulsanteAssocia){
                            $('#messaggioContrattoMancante').html(rispostaJson.messaggio+"<br>Procedere comunque con l'associazione?");
						    $("#Associa").show();
						    $("#Cerca").hide();
						    
						    // _nowait();
                        }//if
                        
					} else {
					    // console.log("max:"+rispostaJson.isContrattoErpGiaAssociato);
					    $("#erroreContrattoMancanteContainer").hide();
					    
					    var risposta  = rispostaJson.risposta;
					    
					    var codiceRda = risposta.n_doc;
						var tipoRdaErp = risposta.num_var;
						var codiceCig = risposta.cig;
						var codiceLavoro = risposta.rilascio;
						var tipologia = risposta.classe;
						
						var dataCreazioneRda;
						if (risposta.data_creazione === null){
						   dataCreazioneRda = null;
						} else {
					       dataCreazioneRda = estraiDataFormattata(risposta.data_creazione);
					    }//if
						
						var ufficioIntestatario = risposta.cogn_creatore + " " + risposta.nome_creatore;
						var importoOpzioni = risposta.imp_var00;
						var valoreStimato = risposta.imp_contratto;
						
						var dataConsegna;		
						if (risposta.dt_firma === null){
						   dataConsegna = null;
						} else {
					       dataConsegna = estraiDataFormattata(risposta.dt_firma);
					    }//if
						
						var gruppoAcquisti = risposta.area_acq;
						var richiedente = risposta.rup;
						var oggetto = risposta.oggetto;										
						var descrizione = risposta.trattativa + " " + risposta.decod_tratt;
						var codiceMateriale = risposta.cat_merc + " " + risposta.decod_classe;
						
						var dataDecorrenza = risposta.dt_inizio_val;
						if (risposta.dt_inizio_val === null){
						   dataDecorrenza = null;
						} else {
					       dataDecorrenza = estraiDataFormattata(risposta.dt_inizio_val);
					    }//if						
							
						var noteDecorrenzaDurata = risposta.dt_fine_val + "";
						var importoSicurezza = risposta.importo_Liquidato;
											    						    						   
					    $("#importResult").show();
					    
						$("#numeroContratto").text( codiceRda );
					    $("#variante").text( tipoRdaErp );
					    $("#codiceCig").text( codiceCig );					    
					    $("#rilascio").text( codiceLavoro );
					    $("#classe").text( tipologia );
					    $("#dataCreazione").text( dataCreazioneRda ); 
					    $("#owner").text( ufficioIntestatario );
					    $("#importoVariante").text( importoOpzioni );
					   
					    // TODO:ROS: da capire come far visualizzare correttamente l'importo 
					    //var valore = $('<fmt:formatNumber type="number" value="${importoOpzioni}" var="totFmt" minFractionDigits="2" maxFractionDigits="2" pattern="###,###,###,###,##0.00"></fmt:formatNumber>');
					    //$("#importoVariante").text( valore.text() );
					    
					    $("#importoContratto").text( valoreStimato );
					    					    
					    $("#dataRilascio").text( dataConsegna );
					    $("#areaAcquisti").text( gruppoAcquisti );					    
					    $("#rup").text( richiedente );						 
					    $("#oggetto").text( oggetto );						    
					    $("#trattativaErp").text( descrizione );					    
					    $("#categoriaMerceologicaPrevalente").text( codiceMateriale );
					    
					    //ROS:TODO: da inserire 
					    // WSERPAnagrafica.nome
					    // WSERPAnagrafica.partitaIva
					    // WSERPAnagrafica.cognome
					    // WSERPAnagrafica.codiceFiscale
					    
					    $("#dataInizioValidita").text( dataDecorrenza );
					    
					    $("#Associa").show();
					    $("#Cerca").hide();
					    
					    _nowait();
											    
					}
				},
				error: function(e) {
					alert('Errore!: non ho ricevuto risposta');
					_nowait();
				},
				complete: function() {
				   _nowait();
				}
			});
		}
		
		function associaContrattoErp(numContrattoErp){
		  var codGara    = "${codGara}";	
		  var codStipula = "${codStipula}";	
		  var idStipula  = "${idStipula}";
		  var idPadre    = "${idPadre}";
		  
		  // Suddividi la stringa in giorno, mese e anno
		  var dataString = $("#dataRilascio").text();
		  console.log("dataString="+dataString);
		  const [giorno, mese, anno] = dataString.split("/");		  
		
		  // Crea un oggetto Date
		  // Attenzione: il mese inizia da 0, quindi sottrai 1 al valore del mese
		  // const dataCreazione = new Date(anno, mese - 1, giorno);	
		  
		    if ( !isStringaNumerica(numContrattoErp) || (numContrattoErp.length!=10) ){
	           
	            $('#messaggioContrattoMancante').html("Attenzione: il valore inserito deve essere numerico e deve avere esattamente 10 caratteri");
                $("#erroreContrattoMancanteContainer").show();
	            _nowait();
	            return;
	        }//if		  	  		   
		    
			$.ajax({
				url: "pg/AssociaInfoContrattoErp.do",
				type: 'POST',
				data: {
				        numeroContrattoErp: numContrattoErp
				       ,codGara: codGara
				       ,codStipula: codStipula
				       ,idStipula: idStipula
				       ,data: dataString
				       ,idPadre: idPadre
				      },
				cache: false,
				dataType: "json",
				success: function(rispostaJson) {
				            
							if(rispostaJson.esito === 'ko'){
					    		$('#messaggioContrattoMancante').html(rispostaJson.risposta);
                        		$("#Associa").hide();
					            $("#Cerca").hide();	
                        	} else {											    
				    			opener.historyReload();
		            			window.close();		
		            		}//if		    
				},
				error: function(e) {
					alert('Errore nella funzione di associazione ');
				},
				complete: function() {
					// _nowait();
				}
			});		  
		}
		
		function visualizzaContratto(){		
			if (  ${not empty codContrattoErp} ){
			   $("#importResult").show();
			   checkNumeroContrattoErp("${codContrattoErp}", true);
			}
		}
		
		visualizzaContratto();
		
				
	</gene:javaScript>
</gene:template>

