<%/*
   * Created on 20-nov-2014
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI EDIT
  // DEL DETTAGLIO DI UN DOCUMENTO ASSOCIATO RELATIVA AI DATI EFFETTIVI
%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
<script type="text/javascript">
<!--
	
	//IMPOTANTE: è stata definita la funzione javascrit "disabilitaProp" che impostando la prop8 a no, interviene sulla property 'wsdm.accediFascicoloDocumentale' e 'wsdm.gestioneStrutturaCompetente'.
	//           Poichè le properties sono individuate in base alla posizione consecutiva nella pagina, se si modifica l'ordine delle properties si deve modificare il riferimento
	//			 a tale property nella funzione
	
	
	//IMPORTATNTE: Tutta la gestione le sezioni dei parametri di login sono basate su funzioni scritte tenendo conto che i nomi dei campi hanno una struttura del tipo prop1, prop2,... quindi se si modifica ll'ordine dei campi
	//             si deve intervenire nelle funzioni che intervengono nella gestione!!!!
	
	arrayProprieta = [["PG","wsdm.fascicoloprotocollo.url"],["PG","wsdmconfigurazione.fascicoloprotocollo.url"],["PG","pg.wsdm.invioMailPec"],["PG","wsdm.protocolloSingoloInvito"],["PG","wsdm.documentale.url"],["PG","wsdmconfigurazione.documentale.url"],["PG","wsdm.loginComune"],["PG","pg.wsdm.applicaFascicolazione"],["PG","wsdm.accediFascicoloDocumentale"],["PG","wsdm.gestioneStrutturaCompetente"],["PG","wsdm.bloccoIndirizzoMittente"],["PG","wsdm.stazioneAppaltante"],["PG","wsdm.applicaRiservatezza"],["PG","wsdm.associaDocumentiProtocollo"],["PG","wsdm.invioMailPec.delay"]];
											
	tipoProprieta = [ "","","-b","-b","","","-b", "b","-b","-b", "b", "tab","-b","-b",""];											
	
	var condLoginComune;
	var condUrlProtocollo;
	var condUrlDocumentale;
	
	function getListaEnti(indice) {
		var result = false;
			$.ajax({
				type: "GET",
				dataType: "json",
				async: false,
				beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				   }
				},
				url: "${pageContext.request.contextPath}/pg/GetWSDMListaEnti.do",
				success: function(data){
					if (data) {
						$.map( data, function( item ) {
							var descrEnte = item[0].value; 
							if (descrEnte!=null && descrEnte.length > 90) {
								descrEnte = descrEnte.substring(0,90) + "...";
							}
							$( ("#prop" + (indice+1)) ).append("<option value="+item[2].value+">"+item[2].value + " - " + descrEnte +"</option>");
						});
					} 
				},
				error: function(e){
					alert("Ente: errore durante la lettura delle informazioni");
				}
			});

			return result;
	}
	
	//Se viene impostato il campo a no, si deve disabilitare la prop9 (wsdm.accediFascicoloDocumentale) e la prop10(wsdm.gestioneStrutturaCompetente)
	function disabilitaProp(campo){
		if(campo.value==0){
			$( "#prop9" ).find('option[value="0"]').attr("selected",true);
			$('#prop9').attr('disabled', 'disabled');
			$( "#prop10" ).find('option[value="0"]').attr("selected",true);
			$('#prop10').attr('disabled', 'disabled');
			$( "#prop13" ).find('option[value="0"]').attr("selected",true);
			$('#prop13').attr('disabled', 'disabled');
		}else{
			$('#prop9').removeAttr('disabled');
			$('#prop10').removeAttr('disabled');
			$('#prop13').removeAttr('disabled');
		}
	}
	
	function visualizzaPropDelay(campo){
		if(campo.value==0){
			$('#delayMailDoc').hide();
		   $('#prop15').val("");
		}else{
			if($("#tiposistemaremoto").val()=="JIRIDE" || $("#tiposistemaremoto").val()=="EASYDOC" || $("#tiposistemaremoto").val()=="PRISMA" || $("#tiposistemaremoto").val()=="JPROTOCOL"){
				$('#delayMailDoc').show();
			}
		}
	}
	
	function gestisciSubmit(){
		$('#prop9').removeAttr('disabled');
		$('#prop10').removeAttr('disabled');
		$('#prop13').removeAttr('disabled');
		
		if ($('form[name="formProprieta"]').validate().form()) {
			if(condLoginComune && condUrlProtocollo ){
				var syscon="-1";
				var servizio="FASCICOLOPROTOCOLLO";
				var username=$("#usernameProt").val();
				var password=$("#pwdProt").val();
				var ruolo=$("#ruoloProt option:selected").val();
				var nome=$("#nomeProt").val();
				var cognome=$("#cognomeProt").val();
				var codiceuo=$("#cuoProt option:selected").val();
				var idutente=$("#idUtenteProt").val();
				var idutenteuop=$("#iduoProt").val();
				setWSLogin(syscon, servizio, username, password, ruolo, nome, cognome, codiceuo, idutente, idutenteuop);
			}
			
			if(condLoginComune && condUrlDocumentale ){
				var syscon="-1";
				var servizio="DOCUMENTALE";
				var username=$("#usernameDoc").val();
				var password=$("#pwdDoc").val();
				var ruolo=$("#ruoloDoc option:selected").val();
				var nome=$("#nomeDoc").val();
				var cognome=$("#cognomeDoc").val();
				var codiceuo=$("#cuoDoc option:selected").val();
				var idutente=$("#idUtenteDoc").val();
				var idutenteuop=$("#iduoDoc").val();
				setWSLogin(syscon, servizio, username, password, ruolo, nome, cognome, codiceuo, idutente, idutenteuop);
			}
			document.formProprieta.submit();
		}
		
	}
	
	function testURL(valore, tns) {
		var urlValido=false;
		if (valore != null && valore != "" && valore != "undefined") {
			if (_testURL(valore, tns) == true) {
				urlValido=true;
			} 
		}
		return urlValido;
	}
	
	//La funzione è basata sul seguente ordine dei campi:
	// 	prop1: url protocollo
	// 	prop2: url configurazione protocollo
	// 	prop5: url documentale
	// 	prop6: url configurazione documentale
	// 	prop7: Parametri di connessione comuni a tutti gli utenti
	
	function gestioneCampiLogin(valore,nomeCampo){
		//alert(valore.value);
		
		if(nomeCampo=="LoginComune"){
			if(valore.value=="1")
				condLoginComune=true;
			else
				condLoginComune=false;
			if(testURL($("#prop1").val(),"dm.ws.eldasoft.maggioli.it") && testURL($("#prop2").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlProtocollo=true;
			else if(!testURL($("#prop1").val(),"dm.ws.eldasoft.maggioli.it") || !testURL($("#prop2").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlProtocollo=false;
			if(testURL($("#prop5").val(),"dm.ws.eldasoft.maggioli.it") && testURL($("#prop6").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlDocumentale=true;
			else if(!testURL($("#prop5").val(),"dm.ws.eldasoft.maggioli.it") || !testURL($("#prop6").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlDocumentale=false;
		}else if(nomeCampo=="urlProt"){
			if($('#prop7 option:selected').val()=="1")
				condLoginComune=true;
			else
				condLoginComune=false;
			if(testURL(valore.value,"dm.ws.eldasoft.maggioli.it") && testURL($("#prop2").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlProtocollo=true;
			else if(!testURL(valore.value,"dm.ws.eldasoft.maggioli.it") || !testURL($("#prop2").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlProtocollo=false;
			if(testURL($("#prop5").val(),"dm.ws.eldasoft.maggioli.it") && testURL($("#prop6").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlDocumentale=true;
			else if(!testURL($("#prop5").val(),"dm.ws.eldasoft.maggioli.it") || !testURL($("#prop6").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlDocumentale=false;
		}else if(nomeCampo=="urlConfProt"){
			if($('#prop7 option:selected').val()=="1")
				condLoginComune=true;
			else
				condLoginComune=false;
			if(testURL($("#prop1").val(),"dm.ws.eldasoft.maggioli.it") && testURL(valore.value,"conf.ws.eldasoft.maggioli.it"))
				condUrlProtocollo=true;
			else if(!testURL($("#prop1").val(),"dm.ws.eldasoft.maggioli.it") || !testURL(valore.value,"conf.ws.eldasoft.maggioli.it"))
				condUrlProtocollo=false;
			if(testURL($("#prop5").val(),"dm.ws.eldasoft.maggioli.it") && testURL($("#prop6").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlDocumentale=true;
			else if(!testURL($("#prop5").val(),"dm.ws.eldasoft.maggioli.it") || !testURL($("#prop6").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlDocumentale=false;
		}else if(nomeCampo=="urlDoc"){
			if($('#prop7 option:selected').val()=="1")
				condLoginComune=true;
			else
				condLoginComune=false;
			if(testURL($("#prop1").val(),"dm.ws.eldasoft.maggioli.it") && testURL($("#prop2").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlProtocollo=true;
			else if(!testURL($("#prop1").val(),"dm.ws.eldasoft.maggioli.it") || !testURL($("#prop2").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlProtocollo=false;
			if(testURL(valore.value,"dm.ws.eldasoft.maggioli.it") && testURL($("#prop6").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlDocumentale=true;
			else if(!testURL(valore.value,"dm.ws.eldasoft.maggioli.it") || testURL($("#prop6").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlDocumentale=false;
		}else if(nomeCampo=="urlDocConf"){
			if($('#prop7 option:selected').val()=="1")
				condLoginComune=true;
			else
				condLoginComune=false;
			if(testURL($("#prop1").val(),"dm.ws.eldasoft.maggioli.it") && testURL($("#prop2").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlProtocollo=true;
			else if(!testURL($("#prop1").val(),"dm.ws.eldasoft.maggioli.it") || !testURL($("#prop2").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlProtocollo=false;
			if(testURL($("#prop5").val(),"dm.ws.eldasoft.maggioli.it") && testURL(valore.value,"conf.ws.eldasoft.maggioli.it"))
				condUrlDocumentale=true;
			else if(!testURL($("#prop5").val(),"dm.ws.eldasoft.maggioli.it") || !testURL(valore.value,"conf.ws.eldasoft.maggioli.it"))
				condUrlDocumentale=false;
		}else if(nomeCampo=="apertura"){
			if($('#prop7 option:selected').val()=="1")
				condLoginComune=true;
			else
				condLoginComune=false;
			if(testURL($("#prop1").val(),"dm.ws.eldasoft.maggioli.it") && testURL($("#prop2").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlProtocollo=true;
			else if(!testURL($("#prop1").val(),"dm.ws.eldasoft.maggioli.it") || !testURL($("#prop2").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlProtocollo=false;
			if(testURL($("#prop5").val(),"dm.ws.eldasoft.maggioli.it") && testURL($("#prop6").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlDocumentale=true;
			else if(!testURL($("#prop5").val(),"dm.ws.eldasoft.maggioli.it") || !testURL($("#prop6").val(),"conf.ws.eldasoft.maggioli.it"))
				condUrlDocumentale=false;
			
			
		}
		
		if(condLoginComune && condUrlProtocollo ){
			getWSTipoSistemaRemoto($("#prop2").val());
			if($("#tiposistemaremoto").val()=="PALEO" || $("#tiposistemaremoto").val()=="JIRIDE" || $("#tiposistemaremoto").val()=="IRIDE"){
				$("#servizio").val("FASCICOLOPROTOCOLLO");
				$('#ruoloProt').empty();
				_popolaTabellatoByUrl("ruolo","ruoloProt",$("#prop2").val());
				if($("#tiposistemaremoto").val()=="PALEO"){
					$('#cuoProt').empty();
					_popolaTabellatoByUrl("codiceuo","cuoProt",$("#prop2").val());
				}
				
			}
		}
		
		if(condLoginComune && condUrlProtocollo ){
			//Si visualizzano i campi per la connessione per il servizio protocollo, in base al tipo di protocollo attivo
			gestioneCampiLoginConfigurazione("visualizza","Prot",$("#tiposistemaremoto").val());
			_getWSLoginConfigurazione("-1","FASCICOLOPROTOCOLLO","MOD");
		} else if(!condLoginComune || !condUrlProtocollo ){
			//Si nascondono i campi per la connessione per il servizio protocollo
			gestioneCampiLoginConfigurazione("nascondi","Prot","");
		}
		
		if(condLoginComune && condUrlDocumentale){
			getWSTipoSistemaRemoto($("#prop6").val());
			if($("#tiposistemaremoto").val()=="PALEO" || $("#tiposistemaremoto").val()=="JIRIDE" || $("#tiposistemaremoto").val()=="IRIDE"){
				$("#servizio").val("DOCUMENTALE");
				$('#ruoloDoc').empty();
				_popolaTabellatoByUrl("ruolo","ruoloDoc",$("#prop6").val());
				if($("#tiposistemaremoto").val()=="PALEO"){
					$('#cuoDoc').empty();
					_popolaTabellatoByUrl("codiceuo","cuoDoc",$("#prop6").val());
				}
			}
		}
		
		
		if(condLoginComune && condUrlDocumentale){
			//Si visualizzano i campi per la connessione per il servizio documentale, in base al tipo di protocollo attivo
			gestioneCampiLoginConfigurazione("visualizza","Doc",$("#tiposistemaremoto").val());
			_getWSLoginConfigurazione("-1","DOCUMENTALE","MOD");
		}else if(!condLoginComune || !condUrlDocumentale){
			//Si nascondono i campi per la connessione per il servizio documentale
			gestioneCampiLoginConfigurazione("nascondi","Doc","");
		}
		
		//Il campo "Applica gestione Struttura competente?" deve essere visibile solo per JIRIDE 
		var visualizzaStrutturaCompetente=false;
		var visualizzaRiservatezza=false;
		var visualizzabloccoModInd=false;
		var visualizzaAssociaDoc=false;
		if(condUrlProtocollo || condUrlDocumentale){
			if($("#tiposistemaremoto").val()==null || $("#tiposistemaremoto").val() ==''){
				var url = $("#prop2").val();
				if(condUrlDocumentale)
					url = $("#prop6").val();
				getWSTipoSistemaRemoto(url);
			}
			if($("#tiposistemaremoto").val()=="JIRIDE"){
				visualizzaStrutturaCompetente=true;
				visualizzaRiservatezza=true;
				visualizzaAssociaDoc = true;
			}
			if($("#tiposistemaremoto").val()=="JIRIDE" || $("#tiposistemaremoto").val()=="ARCHIFLOW" || $("#tiposistemaremoto").val()=="ARCHIFLOWFA")
				visualizzabloccoModInd=true;
					
		}
		
		
	    if(!condUrlProtocollo || ($("#tiposistemaremoto").val()!="JIRIDE" && $("#tiposistemaremoto").val()!="EASYDOC" && $("#tiposistemaremoto").val()!="PRISMA" && $("#tiposistemaremoto").val()!="JPROTOCOL")){
		   $('#delayMailDoc').hide();
		   $('#prop15').val("");
	    }else{
			if($('#prop3 :selected').val()=='1'){
				$('#delayMailDoc').show();
			}
	    }
		if(visualizzaStrutturaCompetente){
			$("#strutturaCompetente").show();
		}else{
			$("#strutturaCompetente").hide();
		}
		if(visualizzaRiservatezza){
			$("#riservatezza").show();
		}else{
			$("#riservatezza").hide();
		}
		if(visualizzabloccoModInd){
			$("#bloccoModIndirizzoMit").show();
		}else{
			$("#bloccoModIndirizzoMit").hide();
		}
		if(visualizzaAssociaDoc){
			$("#associaDoc").show();
		}else{
			$("#associaDoc").hide();
		}
	}
	
	$(document).ready(function() {
		if (arrayProprieta != null && arrayProprieta.length > 0 &&
				tipoProprieta != null && tipoProprieta.length > 0   &&
				arrayProprieta.length <= tipoProprieta.length) {
			
			$.ajax({
				url: '${pageContext.request.contextPath}/GetProprieta.do',
				type: 'POST',
				async: false,
				dataType: 'json',
				data: { arrayProp: arrayProprieta },
				success: function(data) {
					if (data && data.length > 0) {
						var indice = 0;
						$.map( data, function( item ) {
							if ("passw" == tipoProprieta[indice]) {
								if ("" != "" + item.valore) {
									$( ("#prop"  + (indice+1)) ).html("Password impostata");
								} else {
									$( ("#prop"  + (indice+1)) ).html("Password non impostata");
								}
							}else if ("b" == tipoProprieta[indice] || "-b" == tipoProprieta[indice]) {
								$( ("#titoloProp" + (indice+1)) ).attr('title', item.chiave);
								$( ("#codapp" + (indice+1)) ).val(item.codapp);
								$( ("#chiave"+ (indice+1)) ).val(item.chiave);
								var valore =item.valore;
								if((valore=="" || valore== null) && "b" == tipoProprieta[indice]){
									valore='1';
								}else if((valore=="" || valore== null) && "-b" == tipoProprieta[indice]){
									valore='0';
								}
								$( ("#prop"  + (indice+1)) ).find('option[value="' + valore + '"]').attr("selected",true);
							}else if ("tab" == tipoProprieta[indice]) {
								var valore =item.valore;
								getListaEnti(indice);
								$( ("#prop"  + (indice+1)) ).find('option[value="' + valore + '"]').attr("selected",true);
							} else {
								$( ("#titoloProp" + (indice+1)) ).attr('title', item.chiave);
								$( ("#codapp" + (indice+1)) ).val(item.codapp);
								$( ("#chiave"+ (indice+1)) ).val(item.chiave);
								$( ("#prop"  + (indice+1)) ).val(item.valore);
							}
							indice++;
						});
						
						//All'apertura della pagina si chiama la funzione per la gestione della visualizzazione dei campi login
						gestioneCampiLogin(null,'apertura');
						_validateWSLoginConfigurazione();
					}
				},
				error: function() {
					alert("Errore nel caricamento della propriet&agrave; (codapp=" + codiceApplicazione + "-chiave=" + chiave );
				}
			});
		
		   //IMPORTATNTE: Se la property 8 (pg.wsdm.applicaFascicolazione), la property 9 (wsdm.accediFascicoloDocumentale) e la property 10 (wsdm.gestioneStrutturaCompetente) 
			//		vengono spostate, modificare nel codice seguente il riferimento alle property
		   
		   if($('#prop8 :selected').val()=='0'){
			   $('#prop9').attr('disabled', 'disabled');
			   $('#prop10').attr('disabled', 'disabled');
			   $('#prop13').attr('disabled', 'disabled');
		   }
		   
		   if($('#prop3 :selected').val()=='0'){
			   $('#delayMailDoc').hide();
			   $('#prop15').val("");
		   }				
		}
	});
-->
</script>

			<% // Valorizzare con il nome del gestore predisposto per il salvataggio delle proprieta' nella W_CONFIG. %>
			<% // (indicare package e classe). Il gestore deve estendere la classe %>
			<% // it.eldasoft.gene.web.struts.w_config.AbstractGestoreProprieta    %>
			<input type="hidden" name="gestoreProprieta" value="" />

			<tr>
				<td colspan="2">
					<b><br>Protocollo</b>
				</td>
			</tr>
			
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp1" >Indirizzo URL servizio WSDM</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp1" name="codapp" value="PG" />
					<input type="hidden" id="chiave1" name="chiave" value="wsdm.fascicoloprotocollo.url" maxlength="60" />
					<input type="text" id="prop1" name="valore" size="80" maxlength="500" onchange="javascript:gestioneCampiLogin(this,'urlProt');"/>
					<br>&nbsp;&nbsp;&nbsp;(Esempio: <i>http://&lt;nomeserver&gt;:8080/EldasoftWSDM/services/WSDM</i>)
				</td>
			</tr>
			
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp2" >Indirizzo URL servizio configurazione WSDM</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp2" name="codapp" value="PG" />
					<input type="hidden" id="chiave2" name="chiave" value="wsdmconfigurazione.fascicoloprotocollo.url" maxlength="60" />
					<input type="text" id="prop2" name="valore" size="80" maxlength="500" onchange="javascript:gestioneCampiLogin(this,'urlConfProt');"/>
					<br>&nbsp;&nbsp;&nbsp;(Esempio: <i>http://&lt;nomeserver&gt;:8080/EldasoftWSDM/services/WSDMConfigurazione</i>)
				</td>
			</tr>
			
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp3" >Invio mail protocollo in uscita in carico al documentale?</span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp3" name="codapp" value="PG" />
					<input type="hidden" id="chiave3" name="chiave" value="pg.wsdm.invioMailPec" maxlength="60" />
					<select id="prop3" name="valore" onchange="visualizzaPropDelay(this);">
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
				</td>
			</tr>
			
			<tr id="delayMailDoc">
				<td class="etichetta-dato" >
					<span id="titoloProp15" >Delay tra richieste ripetute di invio mail (in millisecondi)</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp15" name="codapp" value="PG" />
					<input type="hidden" id="chiave15" name="chiave" value="wsdm.invioMailPec.delay" maxlength="60" />
					<input type="text" id="prop15" name="valore" size="24" maxlength="20" class="testo"/>
				</td>
			</tr>
			
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp4" >Protocollazione invito a procedure di gara per singolo destinatario?</span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp4" name="codapp" value="PG" />
					<input type="hidden" id="chiave4" name="chiave" value="wsdm.protocolloSingoloInvito" maxlength="60" />
					<select id="prop4" name="valore">
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<b><br>Archiviazione documentale</b>
				</td>
			</tr>
			
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp5" >Indirizzo URL servizio WSDM</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp5" name="codapp" value="PG" />
					<input type="hidden" id="chiave5" name="chiave" value="wsdm.documentale.url" maxlength="60" />
					<input type="text" id="prop5" name="valore" size="80" maxlength="500" onchange="javascript:gestioneCampiLogin(this,'urlDoc');"/>
					<br>&nbsp;&nbsp;&nbsp;(Esempio: <i>http://&lt;nomeserver&gt;:8080/EldasoftWSDM/services/WSDM</i>)
				</td>
			</tr>
			
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp6" >Indirizzo URL servizio configurazione WSDM</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp6" name="codapp" value="PG" />
					<input type="hidden" id="chiave6" name="chiave" value="wsdmconfigurazione.documentale.url" maxlength="60" />
					<input type="text" id="prop6" name="valore" size="80" maxlength="500" onchange="javascript:gestioneCampiLogin(this,'urlDocConf');"/>
					<br>&nbsp;&nbsp;&nbsp;(Esempio: <i>http://&lt;nomeserver&gt;:8080/EldasoftWSDM/services/WSDMConfigurazione</i>)
				</td>
			</tr>
			
			
			<tr>
				<td colspan="2">
					<b><br>Modalit&agrave; di connessione ai servizi</b>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp7" >Parametri di connessione comuni a tutti gli utenti?</span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp7" name="codapp" value="PG" />
					<input type="hidden" id="chiave7" name="chiave" value="wsdm.loginComune" maxlength="60" />
					<select id="prop7" name="valore" onchange="javascript:gestioneCampiLogin(this,'LoginComune');">
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
				</td>
			</tr>	
				
			<tr id="parametriProtRiga">
				<td colspan="2">
					<b><br>Parametri di connessione al servizio 'Protocollo'</b>
				</td>
			</tr>
			<tr id="utenteProtRiga">
				<td class="etichetta-dato" >
					Utente
				</td>
				<td class="valore-dato">
					<input id="usernameProt" name="usernameProt" title="Utente" class="testo" type="text" size="24" value="" maxlength="100"/>
				</td>
			</tr>
			<tr id="pwdProtRiga">
				<td class="etichetta-dato" >
					Password
				</td>
				<td class="valore-dato">
					<input id="pwdProt" name="pwdProt" title="pwdProt" class="testo" type="password" size="24" value="" maxlength="100" />
				</td>
			</tr>
			<tr id="ruoloProtRiga">
				<td class="etichetta-dato" >
					Ruolo
				</td>
				<td class="valore-dato">
					<select id="ruoloProt" name="ruoloProt"></select>
				</td>
			</tr>
			<tr id="nomeProtRiga">
				<td class="etichetta-dato" >
					Nome
				</td>
				<td class="valore-dato">
					<input id="nomeProt" name="nomeProt" title="Nome" class="testo" type="text" size="24" value="" maxlength="20"/>
				</td>
			</tr>
			<tr id="cognomeProtRiga">
				<td class="etichetta-dato" >
					Cognome
				</td>
				<td class="valore-dato">
					<input id="cognomeProt" name="cognomeProt" title="Cognome" class="testo" type="text" size="24" value="" maxlength="20"/>
				</td>
			</tr>
			<tr id="cuoProtRiga">
				<td class="etichetta-dato" >
					Codice unità organizzativa
				</td>
				<td class="valore-dato">
					</span><select id="cuoProt" name="cuoProt"></select>
				</td>
			</tr>
			<tr id="idUtenteProtRiga">
				<td class="etichetta-dato" >
					Identificativo utente
				</td>
				<td class="valore-dato">
					<input id="idUtenteProt" name="idUtenteProt" title="Identificativo utente" class="testo" type="text" size="24" value="" maxlength="20"/>
				</td>
			</tr>
			
			<tr id="iduoProtRiga">
				<td class="etichetta-dato" >
					Identificativo unit&agrave; operativa
				</td>
				<td class="valore-dato">
					<input id="iduoProt" name="iduoProt" title="Identificativo unit&agrave; operativa" class="testo" type="text" size="24" value="" maxlength="20"/>
				</td>
			</tr>
			
			<tr id="parametriDocRiga">
				<td colspan="2">
					<b><br>Parametri di connessione al servizio 'Archiviazione documentale'</b>
				</td>
			</tr>
			<tr id="utenteDocRiga">
				<td class="etichetta-dato" >
					Utente
				</td>
				<td class="valore-dato">
					<input id="usernameDoc" name="usernameDoc" title="Utente" class="testo" type="text" size="24" value="" maxlength="100"/>
				</td>
			</tr>
			<tr id="pwdDocRiga">
				<td class="etichetta-dato" >
					Password
				</td>
				<td class="valore-dato">
					<input id="pwdDoc" name="pwdDoc" title="pwdProt" class="testo" type="password" size="24" value="" maxlength="100" />
				</td>
			</tr>
			<tr id="ruoloDocRiga">
				<td class="etichetta-dato" >
					Ruolo
				</td>
				<td class="valore-dato">
					<select id="ruoloDoc" name="ruoloDoc"></select>
				</td>
			</tr>
			<tr id="nomeDocRiga">
				<td class="etichetta-dato" >
					Nome
				</td>
				<td class="valore-dato">
					<input id="nomeDoc" name="nomeDoc" title="Nome" class="testo" type="text" size="24" value="" maxlength="20"/>
				</td>
			</tr>
			<tr id="cognomeDocRiga">
				<td class="etichetta-dato" >
					Cognome
				</td>
				<td class="valore-dato">
					<input id="cognomeDoc" name="cognomeDoc" title="Cognome" class="testo" type="text" size="24" value="" maxlength="20"/>
				</td>
			</tr>
			<tr id="cuoDocRiga">
				<td class="etichetta-dato" >
					Codice unità organizzativa
				</td>
				<td class="valore-dato">
					</span><select id="cuoDoc" name="cuoDoc"></select>
				</td>
			</tr>
			<tr id="idUtenteDocRiga">
				<td class="etichetta-dato" >
					Identificativo utente
				</td>
				<td class="valore-dato">
					<input id="idUtenteDoc" name="idUtenteDoc" title="Identificativo utente" class="testo" type="text" size="24" value="" maxlength="20"/>
				</td>
			</tr>
			<tr id="iduoDocRiga">
				<td class="etichetta-dato" >
					Identificativo unit&agrave; operativa
				</td>
				<td class="valore-dato">
					<input id="iduoDoc" name="iduoDoc" title="Identificativo unit&agrave; operativa" class="testo" type="text" size="24" value="" maxlength="20"/>
				</td>
			</tr>

			<tr>
				<td colspan="2">
					<b><br>Altri parametri</b>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp8" >Applica fascicolazione?</span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp8" name="codapp" value="PG" />
					<input type="hidden" id="chiave8" name="chiave" value="pg.wsdm.applicaFascicolazione" maxlength="60" />
					<select id="prop8" name="valore" onchange="disabilitaProp(this);">
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp9" >Accesso alla funzione 'Fascicolo documentale'?</span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp9" name="codapp" value="PG" />
					<input type="hidden" id="chiave9" name="chiave" value="wsdm.accediFascicoloDocumentale" maxlength="60" />
					<select id="prop9" name="valore">
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
				</td>
			</tr>
			
			<tr id="strutturaCompetente" style="display:none;">
				<td class="etichetta-dato" >
					<span id="titoloProp10" >Applica gestione Struttura competente? </span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp10" name="codapp" value="PG" />
					<input type="hidden" id="chiave10" name="chiave" value="wsdm.gestioneStrutturaCompetente" maxlength="60" />
					<select id="prop10" name="valore">
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
				</td>
			</tr>
			
			<tr id="riservatezza" style="display:none;">
				<td class="etichetta-dato" >
					<span id="titoloProp13" >Applica riservatezza dei dati?</span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp13" name="codapp" value="PG" />
					<input type="hidden" id="chiave13" name="chiave" value="wsdm.applicaRiservatezza" maxlength="60" />
					<select id="prop13" name="valore">
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
				</td>
			</tr>
			
			<tr id="associaDoc" style="display:none;">
				<td class="etichetta-dato" >
					<span id="titoloProp14"> Associa documenti buste telematiche al relativo protocollo? </span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp14" name="codapp" value="PG" />
					<input type="hidden" id="chiave14" name="chiave" value="wsdm.associaDocumentiProtocollo" maxlength="60" />
					<select id="prop14" name="valore">
						<option value='0'>No</option>
						<option value='1'>Sì, mediante collegamento elemento documentale al protocollo</option>
						<option value='2'>Sì, mediante aggiunta allegati nel protocollo</option>
					</select>
				</td>
			</tr>
			
			<tr id="bloccoModIndirizzoMit" style="display:none;">
				<td class="etichetta-dato" >
					<span id="titoloProp10" >Blocco modifica 'Indirizzo mittente'? </span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp11" name="codapp" value="PG" />
					<input type="hidden" id="chiave11" name="chiave" value="wsdm.bloccoIndirizzoMittente" maxlength="60" />
					<select id="prop11" name="valore">
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
				</td>
			</tr>
			
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp11" >Stazione appaltante per cui viene gestita l'integrazione<br> (non valorizzare se integrazione gestita sempre)</span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp12" name="codapp" value="PG" />
					<input type="hidden" id="chiave12" name="chiave" value="wsdm.stazioneAppaltante" maxlength="60" />
				      	<select id="prop12" name="valore">
			    	  		<option value="">&nbsp;</option>
			      		</select>
				</td>
			</tr>
			
			<input id="tiposistemaremoto" type="hidden" value="" />
			<input id="servizio" type="hidden" value="" />