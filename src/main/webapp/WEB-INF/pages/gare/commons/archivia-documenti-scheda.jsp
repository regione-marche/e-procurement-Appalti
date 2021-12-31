
<%
	/*
	 * Created on 20-Ott-2008
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

<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="documentiAssociatiDB" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.documentiAssociatiDB")}'/>
<c:set var="exportDocumenti" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.pathArchivioDocumentiGara")}'/>
<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNDocumentaleFunction", pageContext, param.codgar, param.idconfi)}' />
<c:set var="exportCOS" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "cos.sftp.url")}'/>
<c:set var="isRiservatezzaAttiva" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsRiservatezzaAttivaFunction", pageContext, param.codice, param.idconfi )}' />
<c:if test='${integrazioneWSDM eq "1"}'>
	<c:set var="tipoWSDM" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetTipoWSDMFunction", pageContext, "DOCUMENTALE", "NO",param.idconfi)}' />
	<c:set var="associaDoc" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", "wsdm.associaDocumentiProtocollo",param.idconfi)}'/>
</c:if>
<c:set var="abilitazioneTrasmissioneWSDM" value='${integrazioneWSDM eq "1" and tipoWSDM == "PALEO" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TrasmettiAOperatoriInterni")}' />
<gene:template file="scheda-template.jsp">

	<gene:redefineInsert name="head" >
		<script type="text/javascript">
			var _contextPath="${pageContext.request.contextPath}";
		</script>
	
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/dataTable/dataTable/jquery.dataTables.css" >
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsd.trasmettiOperatori.css" >
		
		<script type="text/javascript" src="${contextPath}/js/jquery.validate.min.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.easytabs.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.archiviadoc.js?v=${sessionScope.versioneModuloAttivo}"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.trasmettioperatori.js"></script>
		
		
		<style type="text/css">
		
			TABLE.schedagperm {
				margin-top: 5px;
				margin-bottom: 5px;
				padding: 0px;
				font-size: 11px;
				border-collapse: collapse;
				border-left: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
			}
	
			TABLE.schedagperm TR.intestazione {
				background-color: #CCE0FF;
				border-bottom: 1px solid #A0AABA;
			}
			
			TABLE.schedagperm TR.intestazione TD, TABLE.schedagperm TR.intestazione TH {
				padding: 5 2 5 2;
				text-align: center;
				font-weight: bold;
				border-left: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-bottom: 1px solid #A0AABA;
				height: 30px;
			}
		
			TABLE.schedagperm TR.sezione {
				background-color: #EFEFEF;
				border-bottom: 1px solid #A0AABA;
			}
			
			TABLE.schedagperm TR.sezione TD, TABLE.schedagperm TR.sezione TH {
				padding: 5 2 5 2;
				text-align: left;
				font-weight: bold;
				height: 25px;
			}
		
			TABLE.schedagperm TR {
				background-color: #FFFFFF;
			}
	
			TABLE.schedagperm TR TD {
				padding-left: 3px;
				padding-top: 1px;
				padding-bottom: 1px;
				padding-right: 3px;
				text-align: left;
				border-left: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-bottom: 1px solid #A0AABA;
				height: 25px;
				font: 11px Verdana, Arial, Helvetica, sans-serif;
			}
			
			TABLE.schedagperm TR.intestazione TH.codice, TABLE.schedagperm TR TD.codice {
				width: 20px;
			}

			TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.codfisc {
				width: 100px;
			}

			TABLE.schedagperm TR.intestazione TH.descr, TABLE.schedagperm TR TD.descr {
				word-break:break-all;
				width: 200px;
			}
			
			TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.datadescr {
				word-break:break-word;
				width: 50px;
				text-align: center;
			}
			TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.stato {
				width: 100px;
				text-align: center;
			}
			TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.ck {
				width: 50px;
				text-align: center;
			}
			
			
			img.img_titolo {
				padding-left: 8px;
				padding-right: 8px;
				width: 24px;
				height: 24px;
				vertical-align: middle;
			}
			
			.dataTables_length, .dataTables_filter {
				padding-bottom: 5px;
			}

			.dataTables_empty {
				padding-top: 6px;
			}
				
			div.tooltip {
				width: 300px;
				margin-top: 3px;
				margin-bottom:3px;
				border: 1px solid #A0AABA;
				padding: 10px;
				display: none;
				position: absolute;
				z-index: 1000;
				background-color: #F4F4F4;
			}
			

			input.search {
				height: 16px;
				font: 11px Verdana, Arial, Helvetica, sans-serif;
				background-color: #FFFFFF;
				color: #000000;
				vertical-align: middle;
				border: 1px #366A9B solid;
				width: 98%;
				font-style: italic;
			}
				
		</style>
		
	</gene:redefineInsert>
	
	<c:choose>
		<c:when test='${param.genere eq "20"}'>
			<c:set var="genereTitolo" value="del catalogo"/>
		</c:when>
		<c:when test='${param.genere eq "10"}'>
			<c:set var="genereTitolo" value="dell' elenco"/>
		</c:when>
		<c:when test='${param.genere eq "11"}'>
			<c:set var="genereTitolo" value="dell'avviso"/>
		</c:when>
		<c:otherwise>
			<c:set var="genereTitolo" value="della gara"/>
		</c:otherwise>
	</c:choose>
	

	<gene:setString name="titoloMaschera" value="Archivia documenti ${genereTitolo} ${param.codice}" />
	
	<gene:redefineInsert name="corpo">

			<input type="hidden" name="codice" id="codice" value="${param.codice}" />
			<input type="hidden" name="codgar" id="codgar" value="${param.codgar}" />
			<input type="hidden" name="genere" id="genere" value="${param.genere}" />
			<input type="hidden" name="operation" id="operation" value="VISUALIZZA" />
			<input type="hidden" id="contextPath" name="contextPath" value="${contextPath}" />
			<input type="hidden" id="documentiAssociatiDB" name="documentiAssociatiDB" value="${documentiAssociatiDB}" />
			<input type="hidden" id="gartel" name="gartel" value="${param.gartel}" />
			<input type="hidden" id="isRiservatezzaAttiva" name="isRiservatezzaAttiva" value="${isRiservatezzaAttiva}" />
			<input type="hidden" id="idconfi" name="idconfi" value="${param.idconfi}" />
			
		<table class="dettaglio-notab">
			<tr>
				<td>
					<br>
					<div id="documentiContainer" style="margin-left:8px; width: 98%"></div>
				    <br>
				</td>
			</tr>
			<tr>	
				<td class="comandi-dettaglio">
					<c:choose>
						<c:when test='${isNavigazioneDisattiva ne "1"}'>
							<c:if test='${integrazioneWSDM eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TrasferisciAlDocumentale")}'>
								<INPUT type="button" id="pulsantearchprot" class="bottone-azione" value='Trasferisci al documentale' title='Trasferisci al documentale'>
							</c:if>	
							<c:if test='${!empty exportCOS and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TrasferisciCos")}'>
								<INPUT type="button" id="pulsantearchcos" class="bottone-azione" value='Trasferisci a sistema conservazione' title='Trasferisci a sistema conservazione'>
							</c:if>
							<c:if test='${!empty exportDocumenti and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ExportDocumenti")}'>
								<INPUT type="button" id="pulsanteexpdoc" class="bottone-azione" value='Export su file zip' title='Export su file zip'>
							</c:if>
						</c:when>
						<c:otherwise>
						</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>

	<gene:redefineInsert name="addToAzioni" >
		<tr>
			<c:choose>
		        <c:when test='${isNavigazioneDisattiva ne "1"}'>
		        	<c:if test='${param.gartel eq "1" and associaDoc eq "1"}'>
		        	<tr>
			        	<td class="vocemenulaterale">
			        		<c:if test='${integrazioneWSDM eq "1" and  gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AssociaDocDitteAProtocollo")}'>
								<a href="#" id="menuassociadoc" title="Associa documenti buste telematiche a protocollo" tabindex="1512">Associa documenti buste telematiche a protocollo</a>
							</c:if>	
					  	</td>
					  </tr>
		        	</c:if>
		        	<tr>
			        	<td class="vocemenulaterale">
			        		<c:if test='${integrazioneWSDM eq "1" and  gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TrasferisciAlDocumentale")}'>
								<a href="#" id="menuarchprot" title="Trasferisci al documentale" tabindex="1513">Trasferisci al documentale</a>
							</c:if>	
					  	</td>
					  </tr>
		        	<tr>
						<td class="vocemenulaterale">
							<c:if test='${abilitazioneTrasmissioneWSDM}'>
								<a href="javascript:apriTrasmettiAOperatoriInterni();" id="menutrasmetti" title="Trasmetti a operatori interni" tabindex="1514">Trasmetti a operatori interni</a>
							</c:if>	
						</td>
					</tr>
					<tr>
						<td class="vocemenulaterale">
							<c:if test='${!empty exportCOS and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TrasferisciCos")}'>
								<a href="#" id="menuexpcos" title="Trasferisci a sistema conservazione" tabindex="1515">Trasferisci a sistema conservazione</a>
							</c:if>	
						</td>
					</tr>
			        <tr>	
			        	<td class="vocemenulaterale">
							<c:if test='${!empty exportDocumenti and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ExportDocumenti")}'>
								<a href="#" id="menuexpdoc" title="Export su file zip" tabindex="1516">Export su file zip</a>
							</c:if>
					  	</td>
					  </tr>
		        </c:when>
			    <c:otherwise>
			    </c:otherwise>
			</c:choose>
		</tr>
	</gene:redefineInsert>
	
	
	


	
	<gene:redefineInsert name="noteAvvisi"/>
	<gene:redefineInsert name="documentiAssociati"/>

</gene:template>

	<div id="mascheraParametriWSDM" title="Trasferisci al documentale" style="display:none">

			<form id="richiestawslogin">
				<table class="dettaglio-notab">
					<input id="syscon" type="hidden" value="${profiloUtente.id}" /> 
					<input id="servizio" type="hidden" value="DOCUMENTALE" />
					<input id="tiposistemaremoto" type="hidden" value="" />
					<input id="tabellatiInDB" type="hidden" value="" />
					<input type="hidden" name="integrazioneWSDM" id="integrazioneWSDM" value="${integrazioneWSDM}" />
					<input type="hidden" name="gestioneFascicoloWSDM" id="gestioneFascicoloWSDM" value="" />
					<input id="modoapertura" type="hidden" value="MODIFICA" /> 
					<input id="entita" type="hidden" value="${param.entita}" /> 
					<input id="key1" type="hidden" value="${param.key1}" /> 
					<input id="key2" type="hidden" value="${param.key2}" />
					<input id="key3" type="hidden" value="${param.key3}" />
					<input id="key4" type="hidden" value="${param.key4}" />
					
					<input id="idprg" type="hidden" value="PG" />
					<input id="idcom" type="hidden" value="${param.idcom}"/>
					<input id="tipoPagina" type="hidden" name="tipoPagina" value="DOCUMENTAZIONE" />
					<input id="chiaveOriginale" type="hidden" value="${param.chiaveOriginale}" />
					<input id="trasmissioneAbilitata" name="trasmissioneAbilitata" type="hidden" value="${abilitazioneTrasmissioneWSDM}" /> 
					<input id="idprofiloutente" type="hidden" value="${profiloUtente.id}"/>
					
					<tr id=sezioneMessaggi style="display: none;">
						<td colspan="2">
							<div id="messagiTrasmissione"></div>
						</td>
					</tr>
					<tr id=sezioneErrori style="display: none;">
						<td colspan="2">
							<div class="error" id="erroriTrasmissione"></div>
						</td>
					</tr>		
					<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp"></jsp:include>
				</table>
			</form>		
			
			<form id="richiestainserimentoprotocollo">
				<table class="dettaglio-notab">
				<tr id="sezionedatidocumentale">
					<td colspan="2">
						<br>
						<b>Dati dell'elemento documentale </b>
					</td>				
				</tr>
				<tr style="display: none;">
					<td class="etichetta-dato">Oggetto</td>
					<td class="valore-dato">
						<input id="oggettodocumento" name="oggettodocumento"/>
					</td>						
				</tr>
				<tr>
					<td class="etichetta-dato">Classifica</td>
					<td class="valore-dato">
						<select id="classificadocumento" name="classificadocumento"></select>
					</td>						
				</tr>
				<tr>
					<td class="etichetta-dato">Codice registro</td>
					<td class="valore-dato">
						<select id="codiceregistrodocumento" name="codiceregistrodocumento"></select>
					</td>						
				</tr>
				<tr>
					<td class="etichetta-dato">Tipo documento</td>
					<td class="valore-dato">
						<select id="tipodocumento" name="tipodocumento"></select>
					</td>						
				</tr>
				<tr>
					<td class="etichetta-dato">Mittente interno</td>
					<td class="valore-dato">
						<select id="mittenteinterno" name="mittenteinterno" style="max-width:450px"></select>
					</td>						
				</tr>
				<tr style="display: none;" id="rigaSottotipo">
					<td class="etichetta-dato">Sottotipo</td>
					<td class="valore-dato">
						<select id="sottotipo" name="sottotipo"></select>
					</td>						
				</tr>
				<tr id="trMezzo" style="display: none;">
					<td class="etichetta-dato">Mezzo</td>
					<td class="valore-dato">
						<select id="mezzo" name="mezzo"></select>
					</td>						
				</tr>
				<tr id="trSupporto" style="display: none;">
					<td class="etichetta-dato">Supporto</td>
					<td class="valore-dato">
						<select id="supporto" name="supporto"></select>
					</td>						
				</tr>
				<tr style="display: none;">
					<td class="etichetta-dato" style="display: none;">Ingresso/uscita</td>
					<td class="valore-dato" style="display: none;" >
						<select id="inout" name="inout">
							<option value="OUT">Uscita</option>
						</select>
					</td>						
				</tr>
				<tr style="display: none;">
					<td class="etichetta-dato" style="display: none;">Societa</td>
					<td class="valore-dato" style="display: none;" >
						<input id="societa" name="societa"/>
					</td>						
				</tr>
				<tr style="display: none;">
					<td class="etichetta-dato" style="display: none;">RUP</td>
					<td class="valore-dato" style="display: none;" >
						<input id="RUP" name="RUP"/>
					</td>						
				</tr>
				<tr style="display: none;">
					<td class="etichetta-dato" style="display: none;">CodiceGaraLotto</td>
					<td class="valore-dato" style="display: none;" >
						<input id="codicegaralotto" name="codicegaralotto"/>
					</td>						
				</tr>
				<tr style="display: none;">
					<td class="etichetta-dato" style="display: none;">Cig</td>
					<td class="valore-dato" style="display: none;" >
						<input id="cig" name="cig"/>
					</td>						
				</tr>
				<tr style="display: none;">
					<td class="etichetta-dato" style="display: none;">DestinatarioPrincipale</td>
					<td class="valore-dato" style="display: none;" >
						<input id="destinatarioprincipale" name="destinatarioprincipale"/>
					</td>						
				</tr>
				<tr>
					<td class="etichetta-dato">Classifica</td>
					<td class="valore-dato">
						<select id="idtitolazione" name="idtitolazione"></select>
					</td>						
				</tr>
				<tr style="display: none;">
					<td class="etichetta-dato">Unit&agrave; operativa mittente</td>
					<td class="valore-dato">
						<select id="idunitaoperativamittente" name="idunitaoperativamittente"></select>
					</td>						
				</tr>
				<tr style="display: none;">
					<td class="etichetta-dato" style="display: none;">Unit&agrave; operativa destinataria</td>
					<td class="valore-dato" style="display: none;">
						<input id="idunitaoperativadestinataria" name="idunitaoperativadestinataria"/>
					</td>						
				</tr>				
				<tr id="sezionedatifascicolo">
					<td colspan="2">
						<b><br>Dati del fascicolo</b>
						<div style="display: none;" class="error" id="documentifascicolomessaggio"></div>
					</td>
				</tr>
				<tr style="display: none;">
					<td class="etichetta-dato">Inserire il nuovo elemento documentale in un fascicolo ?</td>
					<td class="valore-dato">
						<select id="inserimentoinfascicolo" name="inserimentoinfascicolo">
							<option value=""></option>
							<option value="NO">No, non inserire</option>
							<option value="SI_FASCICOLO_ESISTENTE">Si, inserire nel fascicolo gi&agrave; associato</option>
							<option value="SI_FASCICOLO_NUOVO">Si, inserire in un nuovo fascicolo</option>
						</select>
					</td>						
				</tr>
				<tr>
					<td class="etichetta-dato">Anno fascicolo</td>
					<td class="valore-dato"><input id="annofascicolo" name="annofascicolo" title="Anno fascicolo" class="testo" type="text" size="6" value="" maxlength="4">&nbsp;<a href="javascript:gestioneletturafascicoloPrisma();" id="linkleggifascicoloPrisma" style="display: none;">Leggi fascicolo</a></td>
				</tr>
				<tr>
					<td class="etichetta-dato">Numero fascicolo</td>
					<td class="valore-dato"><input id="numerofascicolo" name="numerofascicolo" title="Numero fascicolo" class="testo" type="text" size="24" value="" maxlength="100"></td>
				</tr>
				<tr>
					<td class="etichetta-dato">Codice fascicolo</td>
					<td class="valore-dato"><input id="codicefascicolo" name="codicefascicolo" title="Codice fascicolo" class="testo" type="text" size="47" value="" maxlength="100">&nbsp;<a href="javascript:gestioneletturafascicolo();" id="linkleggifascicolo" style="display: none;">Leggi fascicolo</a></td>
				</tr>
				<tr>
					<td class="etichetta-dato">Oggetto</td>
					<td class="valore-dato"><span id="oggettofascicolo" name="oggettofascicolo"></span></td>
				</tr>
				<tr>
					<td class="etichetta-dato">Oggetto</td>
					<td class="valore-dato">
						<textarea id="oggettofascicolonuovo" name="oggettofascicolonuovo" title="Oggetto fascicolo" class="testo" rows="4" cols="45"></textarea>
					</td>
				</tr>		
				<tr>
					<td class="etichetta-dato">Classifica</td>
					<td class="valore-dato"><span id="classificafascicolodescrizione" name="classificafascicolodescrizione" title="Classifica"></span></td>
				</tr>
				<tr>
					<td class="etichetta-dato">Classifica <span id="classificaObbligatoriaNuovo" name="classificaObbligatoriaNuovo" >(*)</span></td>
					<td class="valore-dato">
						<select id="classificafascicolonuovo" name="classificafascicolonuovo"></select>
						<input id="classificafascicolonuovoPrisma" name="classificafascicolonuovoPrisma" title="Classifica fascicolo" class="testo" type="text" size="24" value="" maxlength="100" style="display: none;">
						<div style="display: none;" class="error" id="classificafascicolonuovomessaggio"></div>
						<input type="hidden" id="classificadescrizione"  name="classificadescrizione"/>
						<input type="hidden" id="voce"  name="voce"/>
					</td>
				</tr>
				<tr id="trtipofascicolo" style="display: none;">
					<td class="etichetta-dato">Tipo</td>
					<td class="valore-dato">
						<select id="tipofascicolonuovo" name="tipofascicolonuovo"></select>
						<span id="tipofascicolo" name="tipofascicolo" style="display: none;" title="Tipo"></span>
					</td>
				</tr>
				<tr>
					<td class="etichetta-dato">Descrizione</td>
					<td class="valore-dato"><span id="descrizionefascicolo" name="descrizionefascicolo"></span></td>
				</tr>
				<tr>
					<td class="etichetta-dato">Descrizione</td>
					<td class="valore-dato">
						<textarea id="descrizionefascicolonuovo" name="descrizionefascicolonuovo" title="Descrizione fascicolo" class="testo" rows="4" cols="45"></textarea>
					</td>
				</tr>
				<tr id="sezioneamministrazioneorganizzativa" style="display: none;">
					<td colspan="2">
						<b><br>Amministrazione organizzativa</b>
						<div style="display: none;" class="error" id="amministrazioneorganizzativamessaggio"></div>
					</td>
				</tr>
				<tr id="sezionecodiceaoo" style="display: none;">
					<td class="etichetta-dato">Codice AOO</td>
					<td class="valore-dato">
						<select id="codiceaoonuovo" name="codiceaoonuovo"></select>
						<select id="codiceaoonuovo_filtro" name="codiceaoonuovo_filtro" style="display: none;"></select>
						<span id="codiceaoo" name="codiceaoo" style="display: none;"></span>
						<input type="hidden" name="codiceaoodes" id="codiceaoodes" value="" />
					</td>
				</tr>
				<tr id="sezionecodiceufficio" style="display: none;">
					<td class="etichetta-dato">Codice ufficio <span id="ufficioObbligatorioNuovo" name="ufficioObbligatorioNuovo" >(*)</span></td>
					<td class="valore-dato">
						<select id="codiceufficionuovo" name="codiceufficionuovo"></select>
						<select id="codiceufficionuovo_filtro" name="codiceufficionuovo_filtro" style="display: none;"></select>
						<span id="codiceufficio" name="codiceufficio" style="display: none;"></span>
						<input type="hidden" name="codiceufficiodes" id="codiceufficiodes" value="" />
					</td>
				</tr>
				
				<!-- Campi per JDOC -->
				<tr style="display: none;" id="rigaAcronimoRup">
					<td class="etichetta-dato" >Acronimo RUP </td>
					<td class="valore-dato"  >
						<span id="acronimoRup" name="acronimoRup"></span>
					</td>						
				</tr>
				<tr style="display: none;" id = "rigaNomeRup">
					<td class="etichetta-dato" >Nome Rup  </td>
					<td class="valore-dato"  >
						<span id="nomeRup" name="nomeRup"></span>
					</td>						
				</tr>
				
				<!-- Campi per JDOC -->
				
				<tr id="sezionestrutturacompetente" style="display: none;">
					<td colspan="2">
						<b><br>Struttura competente</b>
						<div style="display: none;" class="error" id="strutturacompetentemessaggio"></div>
					</td>
				</tr>
				<tr id="sezionestruttura" style="display: none;">
					<td class="etichetta-dato">Struttura</td>
					<td class="valore-dato">
						<select id="strutturaonuovo" name="strutturaonuovo" style="max-width:450px"></select>
						<span id="struttura" name="struttura" style="display: none;"></span>
					</td>
				</tr>
				<tr style="display: none;">
					<td class="etichetta-dato">Tipo collegamento</td>
					<td class="valore-dato">
						<select id="tipocollegamento" name="tipocollegamento" style="max-width:450px"></select>
					</td>
				</tr>
			</table>
		</form>
		
		<br>
		<form id="formTrasmissione" style="display: none;">
			<table class="dettaglio-notab">
				<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-datiTrasmissioneOperatori.jsp">
					<jsp:param name="tipoPagina" value="ARC"/>
				</jsp:include>	
			</table>
			
		</form>
		
	</div>
	
	<div id="mascheraConfermaCOS" title="Trasferisci a sistema conservazione" style="display:none">

		<form id="confermaCOS">
			<table class="class="lista">
				<tr id="trMessaggioCOS">
				<br>
				<span id="messaggioSuccess"><b>Confermi il trasferimento dei documenti selezionati al sistema di conservazione digitale?</b></span>
				<span id="messaggioErrorConfig"><b>Per procedere al trasferimento dei documenti selezionati è necessario configurare i seguenti parametri:</b></span>	
				<ul id="elencoErroriConfig">
					
				</ul>
				<span id="messaggioErrorGara"><b>Per procedere al trasferimento dei documenti selezionati è necessario specificare i seguenti dati nella gara:</b></span>	
				<ul id="elencoErroriGara">
					
				</ul>
				</tr>
			</table>

		</form>		
		
	</div>
	
	
		<div id="DIV_DESCR_ERRORE" class="ui-widget ui-widget-content ui-corner-all" style="position:absolute; width:400px; height:220px; z-index:99999; display:none;">
			<table class="dettaglio-notab">
				<tr>
					<td class="valore-dato" style="padding-top: 8px; border-bottom: 0px;">
						<div id="DIV_DESCRIZIONE" rows="15" cols="40" style="resize: none;"></div>
					</td>
				</tr>
				<tr>
					<td class="valore-dato" style="border-bottom: 0px; border-top: 0px;">
						<input type="button" style="font-weight: bold;" class="bottone-azione" title="Chiudi" value="Chiudi" onclick="javascript:$('#DIV_DESCR_ERRORE').hide(200);" />	
					</td>
				</tr>
			</table>
		</div>


