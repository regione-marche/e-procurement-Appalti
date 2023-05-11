
<%
	/*
	 * Created on 09-mar-2016
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */

%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>


<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />

<c:set var="idconfi" value="${param.idconfi}"/>
<c:set var="descri" value="${param.descri}"/>
<c:set var="key" value='WSDMCONFI.ID=N:${idconfi}' scope="request" />
<c:set var="keyParent" value='WSDMCONFI.ID=N:${idconfi}' scope="request" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request" />
<c:set var="codapp" value="${param.codapp}"/>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
<script type="text/javascript">
<!--

	var arrayProprieta = null;
	
	<%-- I valori attualmente gestiti sono:  --%>
	<%-- p		password  --%>
	<%-- b		valore booleano  --%>
	var tipoProprieta = null;

	// Azioni di pagina

	function gestisciSubmit(){
		var continua = true;
		//Controllo del titolo del documento associato
		
		if(continua)
			document.formProprieta.submit();
	}

	function annulla() {
		document.pagineForm.modalita.value = "visualizza";
		document.pagineForm.submit();
	}
	
-->

-->
</script>

<form action="${contextPath}/SalvaConfigurazioneWsdm.do" name="formProprieta" method="post" >

	<c:set var="portaleConfigurato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteUrlWsdmPortaleFunction", pageContext, param.idconfi)}' />
		
<script type="text/javascript">
<!--
	
	//IMPOTANTE: è stata definita la funzione javascrit "disabilitaProp" che impostando la prop8 a no, interviene sulla property 'wsdm.accediFascicoloDocumentale' e 'wsdm.gestioneStrutturaCompetente'.
	//           Poichè le properties sono individuate in base alla posizione consecutiva nella pagina, se si modifica l'ordine delle properties si deve modificare il riferimento
	//			 a tale property nella funzione
	
	
	//IMPORTATNTE: Tutta la gestione le sezioni dei parametri di login sono basate su funzioni scritte tenendo conto che i nomi dei campi hanno una struttura del tipo prop1, prop2,... quindi se si modifica ll'ordine dei campi
	//             si deve intervenire nelle funzioni che intervengono nella gestione!!!!
	
	var idconfi = '${param.idconfi}';
	
	//Valori delle property
	//1				wsdm.fascicoloprotocollo.url
	//2				wsdmconfigurazione.fascicoloprotocollo.url
	//3		-b		pg.wsdm.invioMailPec
	//4		-b		wsdm.protocolloSingoloInvito
	//5				wsdm.documentale.ur
	//6				wsdmconfigurazione.documentale.url
	//7		-b		wsdm.loginComune
	//8		b		pg.wsdm.applicaFascicolazione
	//9 	-b		wsdm.accediFascicoloDocumentale
	//10	-b		wsdm.gestioneStrutturaCompetente
	//11	b		wsdm.bloccoIndirizzoMittente
	//12	-b		wsdm.applicaRiservatezza
	//13	assAll	wsdm.associaDocumentiProtocollo
	//14	other	wsdm.invioMailPec.delay
	//15	-b		wsdm.gestioneERP
	//16			wsdm.fascicoloprotocollo.url
	//17	-b		wsdm.documentiDaProtocollo
	//18	-b		wsdm.accediCreaFascicolo
	//19	-b		wsdm.obbligoClassificaFascicolo
	//20	-b		wsdm.obbligoUfficioFascicolo
	//21	-b		wsdm.accediFascicoloDocumentaleCommessa
	//22	-b		wsdm.tabellatiJiride.letdir
	//23	-b		wsdm.tabellatiJiride.pecUffint
	//24    -b		wsdm.posizioneAllegatoComunicazione
	//25    -b      wsdm.firmaDocumenti
	//26    other   wsdm.firmaDocumenti.destinatario
	
	arrayProprieta = [[idconfi,"wsdm.fascicoloprotocollo.url"],[idconfi,"wsdmconfigurazione.fascicoloprotocollo.url"],[idconfi,"pg.wsdm.invioMailPec"],
	                  [idconfi,"wsdm.protocolloSingoloInvito"],[idconfi,"wsdm.documentale.url"],[idconfi,"wsdmconfigurazione.documentale.url"],
	                  [idconfi,"wsdm.loginComune"],[idconfi,"pg.wsdm.applicaFascicolazione"],[idconfi,"wsdm.accediFascicoloDocumentale"],
	                  [idconfi,"wsdm.gestioneStrutturaCompetente"],[idconfi,"wsdm.bloccoIndirizzoMittente"],[idconfi,"wsdm.applicaRiservatezza"],
	                  [idconfi,"wsdm.associaDocumentiProtocollo"],[idconfi,"wsdm.invioMailPec.delay"],[idconfi,"wsdm.gestioneERP"],
	                  [idconfi,"wsdm.fascicoloprotocollo.url"],[idconfi,"wsdm.documentiDaProtocollo"],[idconfi,"wsdm.accediCreaFascicolo"],
	                  [idconfi,"wsdm.obbligoClassificaFascicolo"],[idconfi,"wsdm.obbligoUfficioFascicolo"],[idconfi,"wsdm.accediFascicoloDocumentaleCommessa"],
	                  [idconfi,"wsdm.tabellatiJiride.letdir"],[idconfi,"wsdm.tabellatiJiride.pecUffint"],[idconfi,"wsdm.posizioneAllegatoComunicazione"],
	                  [idconfi,"wsdm.firmaDocumenti"],[idconfi,"wsdm.firmaDocumenti.destinatario"]];
											
	tipoProprieta = [ "","","-b","-b","","","-b", "b","-b","-b", "b","-b","-b","","-b","","-b","-b","-b","-b","-b","-b","-b","-b","-b","other"];											
	
	var condLoginComune;
	var condUrlProtocollo;
	var condUrlDocumentale;
	
	//Se viene impostato il campo a no, si deve disabilitare la prop9 (wsdm.accediFascicoloDocumentale) e la prop10(wsdm.gestioneStrutturaCompetente)
	function disabilitaProp(campo){
		
		if(campo.value==0){
			$( "#prop9" ).find('option[value="0"]').attr("selected",true);
			$('#prop9').attr('disabled', 'disabled');
			$( "#prop10" ).find('option[value="0"]').attr("selected",true);
			$('#prop10').attr('disabled', 'disabled');
			$( "#prop12" ).find('option[value="0"]').attr("selected",true);
			$('#prop12').attr('disabled', 'disabled');
			if($("#tiposistemaremoto").val()=="TITULUS"){
				$( "#prop17" ).find('option[value="0"]').attr("selected",true);
				$('#prop17').attr('disabled', 'disabled');
				
			}
			$( "#prop18" ).find('option[value="0"]').attr("selected",true);
			$('#prop18').attr('disabled', 'disabled');
			if($("#tiposistemaremoto").val()=="JIRIDE"){
				$( "#prop21" ).find('option[value="0"]').attr("selected",true);
				$('#prop21').attr('disabled', 'disabled');
			}
			
		}else{
			$('#prop9').removeAttr('disabled');
			$('#prop10').removeAttr('disabled');
			$('#prop12').removeAttr('disabled');
			if($("#tiposistemaremoto").val()=="TITULUS"){
				$('#prop17').removeAttr('disabled');
			}
			$('#prop18').removeAttr('disabled');
			if($("#tiposistemaremoto").val()=="JIRIDE"){
				$( "#prop21" ).removeAttr('disabled');
			}
		}
	}
	
	function disabilitaDaProp22(campo){
		if(campo.value==1){
			$("#prop11 option").eq(1).prop('selected', true);
			$("#prop11").attr('disabled', 'disabled');
			$("#prop23").removeAttr('disabled');
		} else {
			$("#prop11").removeAttr('disabled');
			$("#prop23 option").eq(0).prop('selected', true);
			$("#prop23").attr('disabled', 'disabled');
		}
	}
	
	function disabilitaDaProp25(campo){
		if(campo.value==1){
			$("#destinatarioFirma").show();
		} else {
			$("#destinatarioFirma").hide();
			$("#prop26").val('');
		}
	}
	
	function visualizzaPropDelay(campo){
		if(campo.value==0){
			$('#delayMailDoc').hide();
		   $('#prop14').val("");
		}else{
			if($("#tiposistemaremoto").val()=="JIRIDE" || $("#tiposistemaremoto").val()=="EASYDOC" || $("#tiposistemaremoto").val()=="PRISMA" || $("#tiposistemaremoto").val()=="JPROTOCOL"
				|| $("#tiposistemaremoto").val()!="ARCHIFLOWFA"){
				$('#delayMailDoc').show();
			}
		}
	}
	
	function gestisciSubmit(){
		if($("#tiposistemaremoto").val()=="ITALPROT"){
			if($('#prop25 :selected').val()=='1' && ($('#prop26').val()==null || $('#prop26').val()=="")){
				alert("Valorizzare il campo 'Intestazione destinatario fittizio per firma'");
				return;
			}
		}
		
		$('#prop9').removeAttr('disabled');
		$('#prop10').removeAttr('disabled');
		$('#prop12').removeAttr('disabled');
		$('#prop17').removeAttr('disabled');
		$('#prop18').removeAttr('disabled');
		$('#prop21').removeAttr('disabled');
		$('#prop11').removeAttr('disabled');
		$("#prop23").removeAttr('disabled');
		
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
				
				setWSLogin(syscon, servizio, username, password, ruolo, nome, cognome, codiceuo, idutente, idutenteuop, idconfi);
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
				
				setWSLogin(syscon, servizio, username, password, ruolo, nome, cognome, codiceuo, idutente, idutenteuop, idconfi);
			}
			
			if("${portaleConfigurato}" == "true"){
				$( "#prop16" ).val($( "#prop1" ).val());
			}else{
				$( "#prop16" ).prop( "disabled", true );
				$( "#chiave16" ).prop( "disabled", true );
				$( "#codapp16" ).prop( "disabled", true );
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
				var tabellatiInDB = $("#tabellatiInDB").val();
				if(tabellatiInDB != "TRUE"){
					_popolaTabellatoByUrl("ruolo","ruoloProt",$("#prop2").val());
					if($("#tiposistemaremoto").val()=="PALEO"){
						$('#cuoProt').empty();
						_popolaTabellatoByUrl("codiceuo","cuoProt",$("#prop2").val());
					}
				}else{
					_popolaTabellatoByParam("ruolo","ruoloProt", $("#tiposistemaremoto").val(),idconfi);
					if($("#tiposistemaremoto").val()=="PALEO"){
						$('#cuoProt').empty();
						_popolaTabellatoByParam("codiceuo","cuoProt", $("#tiposistemaremoto").val(),idconfi);
					}
				}
				
			}
		}
		
		if(condLoginComune && condUrlProtocollo ){
			//Si visualizzano i campi per la connessione per il servizio protocollo, in base al tipo di protocollo attivo
			gestioneCampiLoginConfigurazione("visualizza","Prot",$("#tiposistemaremoto").val());
			_getWSLoginConfigurazione("-1","FASCICOLOPROTOCOLLO","MOD",idconfi);
		} else if(!condLoginComune || !condUrlProtocollo ){
			//Si nascondono i campi per la connessione per il servizio protocollo
			gestioneCampiLoginConfigurazione("nascondi","Prot","");
		}
		
		if(condLoginComune && condUrlDocumentale){
			getWSTipoSistemaRemoto($("#prop6").val());
			if($("#tiposistemaremoto").val()=="PALEO" || $("#tiposistemaremoto").val()=="JIRIDE" || $("#tiposistemaremoto").val()=="IRIDE"){
				$("#servizio").val("DOCUMENTALE");
				$('#ruoloDoc').empty();
				var tabellatiInDB = $("#tabellatiInDB").val();
				if(tabellatiInDB != "TRUE"){
					_popolaTabellatoByUrl("ruolo","ruoloDoc",$("#prop6").val());
					if($("#tiposistemaremoto").val()=="PALEO"){
						$('#cuoDoc').empty();
						_popolaTabellatoByUrl("codiceuo","cuoDoc",$("#prop6").val());
					}
				}else{
					_popolaTabellatoByParam("ruolo","ruoloDoc", $("#tiposistemaremoto").val(),idconfi);
					if($("#tiposistemaremoto").val()=="PALEO"){
						$('#cuoDoc').empty();
						_popolaTabellatoByParam("codiceuo","cuoDoc", $("#tiposistemaremoto").val(),idconfi);
					}
				}
				
			}
		}
		
		
		if(condLoginComune && condUrlDocumentale){
			//Si visualizzano i campi per la connessione per il servizio documentale, in base al tipo di protocollo attivo
			gestioneCampiLoginConfigurazione("visualizza","Doc",$("#tiposistemaremoto").val());
			_getWSLoginConfigurazione("-1","DOCUMENTALE","MOD",idconfi);
		}else if(!condLoginComune || !condUrlDocumentale){
			//Si nascondono i campi per la connessione per il servizio documentale
			gestioneCampiLoginConfigurazione("nascondi","Doc","");
		}
		
		//Il campo "Applica gestione Struttura competente?" deve essere visibile solo per JIRIDE 
		var visualizzaStrutturaCompetente=false;
		var visualizzaRiservatezza=false;
		var visualizzabloccoModInd=false;
		var visualizzaAssociaDoc=false;
		var visualizzagestioneERP=false;
		var visualizzaInserimentoDocDaProtocollo=false;
		var visualizzaaccessoFunzCreaFascicolo=false;
		var visualizzaObbligatoriaClassifica=false;
		var visualizzaObbligatorioUfficio=false;
		var visualizzaFascicoloDocCommessa=false;
		var visualizzagestioneMittenteDaServizio=false;
		var visualizzaindMitenteUffint=false;
		var visualizzafirmaDocumento=false;
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
				visualizzagestioneERP = true;
				visualizzaFascicoloDocCommessa = true;
				visualizzagestioneMittenteDaServizio = true;
				visualizzaindMitenteUffint = true;
			}
			if($("#tiposistemaremoto").val()=="JIRIDE" || $("#tiposistemaremoto").val()=="ARCHIFLOW" || $("#tiposistemaremoto").val()=="ARCHIFLOWFA")
				visualizzabloccoModInd=true;
			
			if($("#tiposistemaremoto").val()=="TITULUS"){
				visualizzaInserimentoDocDaProtocollo=true;
				visualizzaObbligatoriaClassifica=true;
				visualizzaObbligatorioUfficio=true;
			}
			if($("#tiposistemaremoto").val()=="IRIDE" || $("#tiposistemaremoto").val()=="JIRIDE" || $("#tiposistemaremoto").val()=="ENGINEERING" ||  $("#tiposistemaremoto").val()=="ARCHIFLOW" || $("#tiposistemaremoto").val()=="INFOR"
				|| $("#tiposistemaremoto").val()=="JPROTOCOL" ||  $("#tiposistemaremoto").val()=="JDOC" ||  $("#tiposistemaremoto").val()=="ENGINEERINGDOC" ||  $("#tiposistemaremoto").val()=="ITALPROT"
				||  $("#tiposistemaremoto").val()=="LAPISOPERA")
				visualizzaaccessoFunzCreaFascicolo=true;
			if($("#tiposistemaremoto").val()=="ITALPROT"){
				visualizzafirmaDocumento=true;
			}
			if($("#tiposistemaremoto").val()=="INFOR"){
				visualizzaStrutturaCompetente=true;
			}
		}
		
		
	    if(!condUrlProtocollo || ($("#tiposistemaremoto").val()!="JIRIDE" && $("#tiposistemaremoto").val()!="EASYDOC" && $("#tiposistemaremoto").val()!="PRISMA" && $("#tiposistemaremoto").val()!="JPROTOCOL") 
	    		&& $("#tiposistemaremoto").val()!="ARCHIFLOWFA"){
		   $('#delayMailDoc').hide();
		   $('#prop14').val("");
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
		if(visualizzaObbligatoriaClassifica){
			$("#obblClassifica").show();
		}else{
			$("#obblClassifica").hide();
		}
		if(visualizzaObbligatorioUfficio){
			$("#obblUfficio").show();
		}else{
			$("#obblUfficio").hide();
		}
		if(visualizzagestioneERP){
			$("#gestioneERP").show();
		}else{
			$("#gestioneERP").hide();
		}
		if(visualizzaInserimentoDocDaProtocollo){
			$("#inserimentoDocDaProtocollo").show();
			if($('#prop8 :selected').val()!='0'){
				$('#prop17').removeAttr('disabled');
			}else{
				$( "#prop17" ).find('option[value="0"]').attr("selected",true);
				$('#prop17').attr('disabled', 'disabled');
			}
		}else{
			$( "#prop17" ).find('option[value="0"]').attr("selected",true);
			$("#inserimentoDocDaProtocollo").hide();
		}
		if(visualizzaaccessoFunzCreaFascicolo){
			$("#accessoFunzCreaFascicolo").show();
		}else{
			$("#accessoFunzCreaFascicolo").hide();
		}
		
		if(visualizzaFascicoloDocCommessa){
			$("#fascicoloDocCommessa").show();
		}else{
			$("#fascicoloDocCommessa").hide();
		}
		
		if(visualizzagestioneMittenteDaServizio){
			$("#mittenteDaServizio").show();
			if($('#prop22 :selected').val()!='1'){
				$('#prop11').removeAttr('disabled');
				$('#prop23').attr('disabled', 'disabled');
			}else{
				$('#prop11').attr('disabled', 'disabled');
				$('#prop23').removeAttr('disabled');
			}
		}else{
			$("#mittenteDaServizio").hide();
		}
		
		if(visualizzaindMitenteUffint) {
			$("#indMitenteUffint").show();
		}else{
			$("#indMitenteUffint").hide();
		}
		if(visualizzafirmaDocumento){
			$("#firmaDocumento").show();
			if($('#prop25 :selected').val()!='1')
				$("#destinatarioFirma").hide();
			else
				$("#destinatarioFirma").show();
		}else{
			$("#firmaDocumento").hide();
			$("#destinatarioFirma").hide();
		}
	}
	
	$(document).ready(function() {
		if (arrayProprieta != null && arrayProprieta.length > 0 &&
				tipoProprieta != null && tipoProprieta.length > 0   &&
				arrayProprieta.length <= tipoProprieta.length) {
			
			$.ajax({
				url: '${pageContext.request.contextPath}/GetWsdmProprieta.do',
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
								$( ("#idconfi" + (indice+1)) ).val(item.idconfi);
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
								$( ("#idconfi" + (indice+1)) ).val(item.idconfi);
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
					alert("Errore nel caricamento della propriet&agrave");
				}
			});
		
		   //IMPORTATNTE: Se la property 8 (pg.wsdm.applicaFascicolazione), la property 9 (wsdm.accediFascicoloDocumentale) e la property 10 (wsdm.gestioneStrutturaCompetente) 
			//		vengono spostate, modificare nel codice seguente il riferimento alle property
		   
		   if($('#prop8 :selected').val()=='0'){
			   $('#prop9').attr('disabled', 'disabled');
			   $('#prop10').attr('disabled', 'disabled');
			   $('#prop12').attr('disabled', 'disabled');
			   $('#prop18').attr('disabled', 'disabled');
			   $('#prop21').attr('disabled', 'disabled');
		   }else{
			   if($("#tiposistemaremoto").val()=="TITULUS"){
					$('#inserimentoDocDaProtocollo').show();
			   }
			   
		   }
		   
		   if($('#prop3 :selected').val()=='0'){
			   $('#delayMailDoc').hide();
			   $('#prop14').val("");
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
					<input type="hidden" id="codapp1" name="idconfi" value="${idconfi}" />
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
					<input type="hidden" id="codapp2" name="idconfi" value="${idconfi}" />
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
					<input type="hidden" id="codapp3" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave3" name="chiave" value="pg.wsdm.invioMailPec" maxlength="60" />
					<select id="prop3" name="valore" onchange="visualizzaPropDelay(this);">
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
				</td>
			</tr>
			
			<tr id="delayMailDoc">
				<td class="etichetta-dato" >
					<span id="titoloProp14" >Delay tra le chiamate di invio mail al documentale</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp14" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave14" name="chiave" value="wsdm.invioMailPec.delay" maxlength="60" />
					<input type="text" id="prop14" name="valore" size="24" maxlength="20" class="testo"/>
				</td>
			</tr>
			
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp4" >Protocollazione invito a procedure di gara per singolo destinatario?</span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp4" name="idconfi" value="${idconfi}" />
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
					<input type="hidden" id="codapp5" name="idconfi" value="${idconfi}" />
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
					<input type="hidden" id="codapp6" name="idconfi" value="${idconfi}" />
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
					<input type="hidden" id="codapp7" name="idconfi" value="${idconfi}" />
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
					<input type="hidden" id="codapp8" name="idconfi" value="${idconfi}" />
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
					<input type="hidden" id="codapp9" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave9" name="chiave" value="wsdm.accediFascicoloDocumentale" maxlength="60" />
					<select id="prop9" name="valore">
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
				</td>
			</tr>
			<tr id="fascicoloDocCommessa">
				<td class="etichetta-dato" >
					<span id="titoloProp21" >Accesso alla funzione 'Fascicolo documentale commessa/appalto'?</span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp21" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave21" name="chiave" value="wsdm.accediFascicoloDocumentaleCommessa" maxlength="60" />
					<select id="prop21" name="valore">
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
				</td>
			</tr>
			
			<tr id="accessoFunzCreaFascicolo" style="display:none;">
				<td class="etichetta-dato" >
					<span id="titoloProp18" >Accesso alla funzione 'Crea/Associa fascicolo'?</span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp18" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave18" name="chiave" value="wsdm.accediCreaFascicolo" maxlength="60" />
					<select id="prop18" name="valore">
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
					<input type="hidden" id="codapp10" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave10" name="chiave" value="wsdm.gestioneStrutturaCompetente" maxlength="60" />
					<select id="prop10" name="valore">
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
				</td>
			</tr>
			
			<tr id="riservatezza" style="display:none;">
				<td class="etichetta-dato" >
					<span id="titoloProp12" >Applica riservatezza dei dati?</span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp12" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave12" name="chiave" value="wsdm.applicaRiservatezza" maxlength="60" />
					<select id="prop12" name="valore">
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
				</td>
			</tr>
			
			<tr id="associaDoc" style="display:none;">
				<td class="etichetta-dato" >
					<span id="titoloProp13"> Associa documenti buste telematiche al relativo protocollo? </span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp13" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave13" name="chiave" value="wsdm.associaDocumentiProtocollo" maxlength="60" />
					<select id="prop13" name="valore">
						<option value='0'>No</option>
						<option value='1'>Sì, mediante collegamento elemento documentale al protocollo</option>
						<option value='2'>Sì, mediante aggiunta allegati nel protocollo</option>
					</select>
				</td>
			</tr>
			
			<tr id="obblClassifica" style="display:none;">
				<td class="etichetta-dato" >
					<span id="titoloProp19">Rendere obbligatorio 'Classifica fascicolo'? </span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp19" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave19" name="chiave" value="wsdm.obbligoClassificaFascicolo" maxlength="60" />
					<select id="prop19" name="valore">
						<option value='0'>No</option>
						<option value='1'>Sì</option>
					</select>
				</td>
			</tr>
			
			<tr id="obblUfficio" style="display:none;">
				<td class="etichetta-dato" >
					<span id="titoloProp20">Rendere obbligatorio 'Ufficio amministrazione organizzativa'? </span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp20" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave20" name="chiave" value="wsdm.obbligoUfficioFascicolo" maxlength="60" />
					<select id="prop20" name="valore">
						<option value='0'>No</option>
						<option value='1'>Sì</option>
					</select>
				</td>
			</tr>
			
			<tr id="mittenteDaServizio" style="display:none;">
				<td class="etichetta-dato" >
					<span id="titoloProp22">Lettura 'Mittente interno' e 'Indirizzo mittente' da servizio?</span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp22" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave22" name="chiave" value="wsdm.tabellatiJiride.letdir" maxlength="60" />
					<select id="prop22" name="valore" onchange="disabilitaDaProp22(this);">
						<option value='0'>No</option>
						<option value='1'>Sì</option>
					</select>
				</td>
			</tr>
			
			<tr id="indMitenteUffint" style="display:none;">
				<td class="etichetta-dato" >
					<span id="titoloProp23">Utilizzare 'Indirizzo mittente' ufficio intestatario? </span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp23" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave23" name="chiave" value="wsdm.tabellatiJiride.pecUffint" maxlength="60" />
					<select id="prop23" name="valore" >
						<option value='0'>No</option>
						<option value='1'>Sì</option>
					</select>
				</td>
			</tr>
			
			<tr id="bloccoModIndirizzoMit" style="display:none;">
				<td class="etichetta-dato" >
					<span id="titoloProp10" >Blocco modifica 'Indirizzo mittente'? </span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp11" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave11" name="chiave" value="wsdm.bloccoIndirizzoMittente" maxlength="60" />
					<select id="prop11" name="valore">
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
				</td>
			</tr>
			
			<tr id="posizioneTestoComunicazione">
				<td class="etichetta-dato" >
					<span id="titoloProp24" >Posizione testo comunicazione rispetto agli altri allegati della comunicazione?</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp24" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave24" name="chiave" value="wsdm.bloccoIndirizzoMittente" maxlength="60" />
					<select id="prop24" name="valore">
						<option value='1'>Prima degli altri allegati</option>
						<option value='0'>In coda agli altri allegati</option>
					</select>
				</td>
			</tr>
			
			<tr id="gestioneERP" style="display:none;">
				<td class="etichetta-dato" >
					<span id="titoloProp15" >Attiva selezione RdA (personalizzazione Porto Genova)?</span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp15" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave15" name="chiave" value="wsdm.gestioneERP" maxlength="60" />
					<select id="prop15" name="valore">
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
				</td>
			</tr>
			
			<tr id="inserimentoDocDaProtocollo" style="display:none;">
				<td class="etichetta-dato" >
					<span id="titoloProp17" >Attiva inserimento documenti di gara da protocollo?</span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp17" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave17" name="chiave" value="wsdm.documentiDaProtocollo" maxlength="60" />
					<select id="prop17" name="valore">
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
				</td>
			</tr>
			
			<tr id="firmaDocumento" style="display:none;">
				<td class="etichetta-dato" >
					<span id="titoloProp25" >Attiva funzione 'Firma documento'?</span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp25" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave25" name="chiave" value="wsdm.firmaDocumenti" maxlength="60" />
					<select id="prop25" name="valore" onchange="disabilitaDaProp25(this);">
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
				</td>
			</tr>
			
			<tr id="destinatarioFirma" style="display:none;">
				<td class="etichetta-dato" >
					<span id="titoloProp26" >Intestazione destinatario fittizio per firma (*)</span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp26" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="chiave26" name="chiave" value="wsdm.firmaDocumenti.destinatario" maxlength="60" />
					<input type="text" id="prop26" name="valore" size="80" maxlength="500" />
				</td>
			</tr>
			
			<input type="hidden" id="codapp16" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave16" name="chiave" value="protocollazione.wsdm.url" maxlength="60" />
			<input type="hidden" id="prop16" name="valore" size="80" maxlength="500" />
			
			<input id="tiposistemaremoto" type="hidden" value="" />
			<input id="servizio" type="hidden" value="" />
			<input id="descri" name="descri" type="hidden" value="${descri}"/>
			<input id="idconfi" type="hidden" value="${param.idconfi}" />
			<input id="tabellatiInDB" type="hidden" value="" />
			<input id="codapp" name="codapp" type="hidden" value="${codapp }" />
			
		<tr>
			<td class="comandi-dettaglio" colspan="2" >
				<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:gestisciSubmit()">
				<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla()">
				&nbsp;
			</td>
		</tr>
		
		<gene:redefineInsert name="addToAzioni" >
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:gestisciSubmit();" tabindex="1502" title="Salva">Salva</a>
			</td>
		</tr>		
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:annulla();" tabindex="1503" title="Annulla">Annulla</a>
			</td>
		</tr>
		</gene:redefineInsert>
</form>
	