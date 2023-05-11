
<%
	/*
	 * Created on 21-Ott-2008
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

<c:set var="digitalSignatureUrlCheck" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-check-url")}'/>
<c:set var="digitalSignatureProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:choose>
	<c:when test="${!empty digitalSignatureUrlCheck && !empty digitalSignatureProvider && (digitalSignatureProvider eq 1 || digitalSignatureProvider eq 2)}">
		<c:set var="digitalSignatureWsCheck" value='1'/>
	</c:when>
	<c:otherwise>
		<c:set var="digitalSignatureWsCheck" value='0'/>
	</c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
<c:set var="idprg" value='${gene:getValCampo(key,"IDPRG")}' scope="request"/>
<c:set var="idcom" value='${gene:getValCampo(key,"IDCOM")}' scope="request"/>
<c:set var="comkey1" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetCOMKEYGaraLottoFunction",pageContext,idprg,idcom)}' />

<c:set var="firmaProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:if test='${firmaProvider eq 2}'>
	<c:set var="firmaRemota" value="true"/>
</c:if>
<c:set var="comstato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetCOMSTATOFunction",pageContext,idprg,idcom)}' />
<c:set var="autorizzatoModificaComunicazione" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.AutorizzatoModificaComunicazioneFunction",pageContext,idprg,idcom,"true")}' />
<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, comkey1)}'/>
<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codgar, idconfi)}'/>
<c:set var="where" value="W_DOCDIG.DIGENT = 'W_INVCOM' AND W_DOCDIG.DIGKEY1 = '${idprg}' AND W_DOCDIG.DIGKEY2 = '${idcom}'"/>

<c:set var="richiestaFirma" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "documentiDb.richiestaFirma")}'/>
<c:if test="${integrazioneWSDM eq '1' && richiestaFirma ne '1'}">
	<c:set var="tipoWSDM" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetTipoWSDMFunction", pageContext, "FASCICOLOPROTOCOLLO", "NO",idconfi)}' />
	<c:if test="${tipoWSDM eq 'ITALPROT'}">
		<c:set var="firmaDocumento" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", firmaDocumenti,idconfi)}' scope="request"/>
		<c:if test="${firmaDocumento eq '1'}">
			<c:set var="richiestaFirma" value='1'/>
			<c:set var="whereW_INVCOM" value="W_INVCOM.IDPRG = 'PG' AND W_INVCOM.IDCOM = ${idcom}"/>
			<c:set var="oggettoComunic" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "COMMSGOGG", "W_INVCOM", whereW_INVCOM)}'/>
		</c:if>
	</c:if>
</c:if>

<c:choose>
	<c:when test='${not empty param.keyAdd}'>
		<c:set var="keyAdd" value="${param.keyAdd}"  />
	</c:when>
	<c:otherwise>
		<c:set var="keyAdd" value="${keyAdd}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.chiaveWSDM}'>
		<c:set var="chiaveWSDM" value="${param.chiaveWSDM}"  />
	</c:when>
	<c:otherwise>
		<c:set var="chiaveWSDM" value="${chiaveWSDM}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.entitaWSDM}'>
		<c:set var="entitaWSDM" value="${param.entitaWSDM}"  />
	</c:when>
	<c:otherwise>
		<c:set var="entitaWSDM" value="${entitaWSDM}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.idconfi}'>
		<c:set var="idconfi" value="${param.idconfi}"  />
	</c:when>
	<c:otherwise>
		<c:set var="idconfi" value="${idconfi}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.comdatins}'>
		<c:set var="comdatins" value="${param.comdatins}"  />
	</c:when>
	<c:otherwise>
		<c:set var="comdatins" value="${comdatins}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.cenint}'>
		<c:set var="cenint" value="${param.cenint}"  />
	</c:when>
	<c:otherwise>
		<c:set var="cenint" value="${cenint}" />
	</c:otherwise>
</c:choose>

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<c:if test="${richiestaFirma eq '1'}">
	<c:set var="numDocAttesaFirma" value="0"/>
</c:if>

<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.documenti.gara.js"></script>
</gene:redefineInsert>

<table class="dettaglio-tab-lista">
	<tr>
		<td><gene:formLista entita="W_DOCDIG" pagesize="0" sortColumn="${gene:if(comstato eq '1' and autorizzatoModificaComunicazione eq 'true',3,2)}"
			where="${where}" tableclass="datilista"
			gestisciProtezioni="true" gestore="">
			<c:if test="${comstato eq '1' and autorizzatoModificaComunicazione eq 'true'}">
				<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"
					width="50">
					<c:if test="${currentRow >= 0}">
						<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"
							onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItemResource
								resource="popupmenu.tags.lista.visualizza"
								title="Visualizza" />
							<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD")}'>
								<gene:PopUpItemResource
									resource="popupmenu.tags.lista.modifica"
									title="Modifica" />
							</c:if>
							<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}'>
								<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina"
									title="Elimina" />
							</c:if>
						</gene:PopUp>
						<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
							<input type="checkbox" name="keys" value="${chiaveRiga}" />
						</c:if>
					</c:if>
				</gene:campoLista>
			</c:if>
			<gene:campoLista campo="IDPRG" visibile="false"/>
			<gene:campoLista title="N°" width="40" campo="IDDOCDIG" visibile="false"/>
			<gene:campoLista campo="DIGENT" visibile="false"/>
			<gene:campoLista campo="DIGKEY1" visibile="false"/>
			<c:choose>
				<c:when test="${comstato eq '1' and autorizzatoModificaComunicazione eq 'true'}">
					<gene:campoLista campo="DIGDESDOC" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
				</c:when>
				<c:otherwise>
					<gene:campoLista campo="DIGDESDOC" />
				</c:otherwise>
			</c:choose>
			<gene:campoLista campo="DIGNOMDOC" />
			<c:if test="${richiestaFirma eq '1'}">
				<gene:campoLista campo="DIGFIRMA" visibile="false"/>
				<gene:campoLista title="&nbsp;" width="20" >
					<c:choose>
						<c:when test="${datiRiga.W_DOCDIG_DIGFIRMA eq '1'}">
							<img width="16" height="16" title="In attesa di firma" alt="In attesa di firma" src="${pageContext.request.contextPath}/img/isquantimod.png"/>
							<c:set var="numDocAttesaFirma" value="${numDocAttesaFirma + 1}"/>
						</c:when>
						<c:otherwise>
							<c:if test="${firmaDocumento eq '1' and autorizzatoModificaComunicazione and comstato eq '1'}">
								<a style="float:right;" href="javascript:preAperturaModaleRichiestaFirma('${datiRiga.W_DOCDIG_IDPRG}','${datiRiga.W_DOCDIG_IDDOCDIG}','${currentRow + 1}');">
								<img src="${pageContext.request.contextPath}/img/firmaRemota.png" title="Firma digitale del documento" alt="Firma documento" width="16" height="16" style="padding:2 4 6 4">
								</a>
							</c:if>
						</c:otherwise>
					</c:choose>
					
				</gene:campoLista>
			</c:if>
			<gene:campoLista title="&nbsp;" width="24">
				<c:set var="nomDoc" value="${gene:string4Js(datiRiga.W_DOCDIG_DIGNOMDOC)}"/>
				<c:set var="nomDoc" value="${fn:replace(nomDoc,'\"','&#34;')}"/>
				<a href="javascript:visualizzaFileAllegato('${datiRiga.W_DOCDIG_IDPRG}','${datiRiga.W_DOCDIG_IDDOCDIG}',${nomDoc});" title="Visualizza allegato" >
					<img width="24" height="24" title="Visualizza allegato" alt="Visualizza allegato" src="${pageContext.request.contextPath}/img/visualizzafile.gif"/>
				</a>
			</gene:campoLista>
			
			<c:if test='${comstato eq "1" and autorizzatoModificaComunicazione eq "true" and not empty firmaRemota and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.FirmaRemotaDocumenti")}'>
				<gene:campoLista title="&nbsp;" width="24">
					<a style="float:right;" href="javascript:openModal('${datiRiga.W_DOCDIG_IDPRG}','${datiRiga.W_DOCDIG_IDDOCDIG}',${nomDoc},'${pageContext.request.contextPath}');">
					<img src="${pageContext.request.contextPath}/img/firmaRemota.png" title="Firma digitale del documento" alt="Firma documento" width="16" height="16" style="padding:2 4 6 4">
					</a>
				</gene:campoLista>
			</c:if>
			<gene:campoLista visibile="false">
				<input type="hidden" name="INPUT_IDDOCDG_${currentRow}" id="INPUT_IDDOCDG_${currentRow}" value="${datiRiga.W_DOCDIG_IDDOCDIG}"/>
				<input type="hidden" name="INPUT_IDPRG_${currentRow}" id="INPUT_IDPRG_${currentRow}" value="${datiRiga.W_DOCDIG_IDPRG}"/>
				<input type="hidden" name="INPUT_DIGNOMDOC_${currentRow}" id="INPUT_DIGNOMDOC_${currentRow}" value="${datiRiga.W_DOCDIG_DIGNOMDOC}"/>
			</gene:campoLista>
			<input type="hidden" name="keyAdd" value="${keyAdd}" />
			<input type="hidden" name="chiaveWSDM" id="chiaveWSDM" value="${chiaveWSDM}"/>
			<input type="hidden" name="entitaWSDM" id="entitaWSDM" value="${entitaWSDM}"/>
			<input type="hidden" name="comdatins" id="comdatins" value="${comdatins}"/>
			<input type="hidden" name="cenint" id="cenint" value="${cenint}"/>
			<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}"/>
			<input type="hidden" name="integrazioneWSDM" id="integrazioneWSDM" value="${integrazioneWSDM}"/>
			
		</gene:formLista></td>
	</tr>
	
	<c:choose>
		<c:when test="${comstato eq '1' and autorizzatoModificaComunicazione eq 'true'}">
			<gene:redefineInsert name="listaNuovo">
				<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:listaNuovo();" title="Aggiungi allegato" tabindex="1501">
								Aggiungi allegato</a></td>
					</tr>
				</c:if>
			</gene:redefineInsert>

			<gene:redefineInsert name="addToAzioni" >
				<c:choose>
					<c:when test='${integrazioneWSDM =="1" and !protpres and compub==2}'>
						<tr>
							<td class="vocemenulaterale" >
								<a href="javascript:protocollacomunicazione('${idprg}','${idcom}');" title="Protocolla ed invia comunicazione" tabindex="1504">Protocolla ed invia comunicazione</a>
							</td>
						</tr>
					</c:when>
					<c:otherwise>
						<tr>
							<td class="vocemenulaterale" >
								<a href="javascript:inviacomunicazione('${idprg}','${idcom}');" title="Invia comunicazione" tabindex="1504">Invia comunicazione</a>
							</td>
						</tr>
					</c:otherwise>
				</c:choose>
				<c:if test='${richiestaFirma eq "1"}'>
					<tr id="rileggiDatiRow"  style="display: none;">
						<td class="vocemenulaterale" >
							<a href="javascript:rileggiDati();" title='Rileggi dati' tabindex="1510">Rileggi dati</a>
						</td>
					</tr>
					
				</c:if>	
				<c:if test="${gene:checkProtFunz(pageContext,'ALT','EsportaDocumentiBusta')}">
					<tr>
						<td class="vocemenulaterale">
							<a href='javascript:openModalDownloadDoc( "${comkey1}","", "${idcom}" , "${idprg}" ,"OUT","${pageContext.request.contextPath}" );' title='Esporta su file zip' tabindex="1511">
								Esporta su file zip
							</a>
						</td>
					</tr>
				</c:if>
			</gene:redefineInsert>
		
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<c:if test='${richiestaFirma eq "1"}'>
						<INPUT type="button"  class="bottone-azione" value='Rileggi dati' title='Rileggi dati' onclick="javascript:rileggiDati();" style="display: none;" id="btnRileggiDati">
					</c:if>
					<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
						<INPUT type="button"  class="bottone-azione" value='Aggiungi allegato' title='Aggiungi allegato' onclick="javascript:listaNuovo()">
					</c:if>
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
					</c:if>
				</td>
			</tr>
		</c:when>
		<c:otherwise>
			<gene:redefineInsert name="addToAzioni" >
				<c:if test='${richiestaFirma eq "1"}'>
					<tr id="rileggiDatiRow"  style="display: none;">
						<td class="vocemenulaterale" >
							<a href="javascript:rileggiDati();" title='Rileggi dati' tabindex="1510">Rileggi dati</a>
						</td>
					</tr>
					
				</c:if>	
				<c:if test="${gene:checkProtFunz(pageContext,'ALT','EsportaDocumentiBusta')}">
					<tr>
						<td class="vocemenulaterale">
							<a href='javascript:openModalDownloadDoc( "${comkey1}","", "${idcom}" ,"${idprg}","OUT","${pageContext.request.contextPath}" );' title='Esporta su file zip' tabindex="1511">
								Esporta su file zip
							</a>
						</td>
					</tr>
				</c:if>
			</gene:redefineInsert>
			<gene:redefineInsert name="addPulsanti" >
				<c:if test='${richiestaFirma eq "1"}'>
					<INPUT type="button"  class="bottone-azione" value='Rileggi dati' title='Rileggi dati' onclick="javascript:rileggiDati();" style="display: none;" id="btnRileggiDati">
				</c:if>
			</gene:redefineInsert>
			<gene:redefineInsert name="listaNuovo" />
			<gene:redefineInsert name="listaEliminaSelezione" />
			
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					&nbsp;
				</td>
			</tr>
			
		</c:otherwise>
	</c:choose>

</table>
	
	<jsp:include page="/WEB-INF/pages/gare/commons/modalPopupDownloadAllegatiComunicazioni.jsp" />
	<jsp:include page="/WEB-INF/pages/gene/system/firmadigitale/modalPopupFirmaDigitaleRemota.jsp" />
	
	<jsp:include page="/WEB-INF/pages/gare/commons/modalPopupFirmaDocumento.jsp">
		<jsp:param name="oggettoDoc" value="${titolo}"/>
		<jsp:param name="key1" value="${comkey1}"/>
	</jsp:include>
	
	<form name="formVisFirmaDigitale" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="gene/system/firmadigitale/verifica-firmadigitale.jsp" />
		<input type="hidden" name="idprg" id="idprg" value="" />
		<input type="hidden" name="iddocdig" id="iddocdig" value="" />
	</form>

<gene:javaScript>

	var cenint = "${cenint}";
	
	var autorizzatoTelematiche ="${autorizzatoTelematiche }";
	
		
	function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
		var vet = dignomdoc.split(".");
		var ext = vet[vet.length-1];
		ext = ext.toUpperCase();
		<c:choose>
			<c:when test="${digitalSignatureWsCheck eq 0}">
				if(ext=='P7M' || ext=='TSD'){
					document.formVisFirmaDigitale.idprg.value = idprg;
					document.formVisFirmaDigitale.iddocdig.value = iddocdig;
					document.formVisFirmaDigitale.submit();
				}else{
					var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
					document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
				}
			</c:when>
			<c:otherwise>
				if(ext=='P7M' || ext=='TSD' || ext=='XML' || ext=='PDF'){
					document.formVisFirmaDigitale.idprg.value = idprg;
					document.formVisFirmaDigitale.iddocdig.value = iddocdig;
					document.formVisFirmaDigitale.submit();
				}else{
					var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
					document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
				}
			</c:otherwise>
		</c:choose>
	}	
		
	function inviacomunicazione(idprg,idcom){
		if(autorizzatoTelematiche == "false"){
			alert("Non e' possibile procedere.\nLa funzione e' disponibile solo al Punto ordinante");
			return;
		}else{
			if(cenint == ""){
				alert("Non e' possibile procedere.\nDeve essere specificata la stazione appaltante");
				return;
			}else{
				var commodello = "${commodello}";
				href="href=geneweb/w_invcom/w_invcom-invia-popup.jsp&idprg=" + idprg + "&idcom=" + idcom + "&compub=${compub}" + "&cenint=" + cenint + "&commodello=" + commodello;
				openPopUpCustom(href, "inviacomunicazione", 550, 350, "no", "no");
			}
		}
	}
	
	function protocollacomunicazione(idprg,idcom) {
		var firmaDocumento="${firmaDocumento }";
		var numDocAttesaFirma="${numDocAttesaFirma}";
		 
		  
		if(autorizzatoTelematiche == "false"){
			alert("Non e' possibile procedere.\nLa funzione e' disponibile solo al Punto ordinante");
			return;
		}else{
			if(cenint == ""){
				alert("Non e' possibile procedere.\nDeve essere specificata la stazione appaltante");
				return;
			} else if(numDocAttesaFirma!=null && numDocAttesaFirma!="" && numDocAttesaFirma != "0"){
				alert("Non e' possibile procedere.\nVi sono degli allegati in attesa di firma");
				return;
			}else{
				document.formprotocollacomunicazione.idprg.value = idprg;
				document.formprotocollacomunicazione.idcom.value = idcom;
				var chiaveOriginale;
				var keyAdd="${keyAdd }";
				var vet = keyAdd.split(";");
				var dimVet = vet.length;
				if(dimVet==1){
					chiaveOriginale = keyAdd.substring(keyAdd.indexOf(":")+1);
				}else if(dimVet>1){
					chiaveOriginale = keyAdd.substring(keyAdd.indexOf(":")+1,vet[0].length);
				}
				document.formprotocollacomunicazione.chiaveOriginale.value = chiaveOriginale;
				document.formprotocollacomunicazione.submit();
			}
		}
	}
	
	var selezionaPaginaDefault = selezionaPagina;
	var selezionaPagina = selezionaPaginaCustom;
	function selezionaPaginaCustom(pageNumber){
		var keyAdd="${keyAdd }"
		var chiaveWSDM="${chiaveWSDM }";
		var entitaWSDM="${entitaWSDM }";
		var idconfi="${idconfi }";
		var comdatins ="${comdatins }";
		document.pagineForm.action += "&keyAdd=" + keyAdd + "&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM + "&comdatins=" + comdatins + "&cenint=" + cenint + "&idconfi=" + idconfi;
		selezionaPaginaDefault(pageNumber);
	}
	
	//si elimina l'orario dal valore
	var comdatins = "${comdatins }";
	if(comdatins!=null){
		var tmp = comdatins.split(" ");
		comdatins = tmp[0];
		formprotocollacomunicazione.comdatins.value=comdatins;
	}
	
	<c:if test='${richiestaFirma eq "1"}'>
		 var numDocAttesaFirma="${numDocAttesaFirma}";
		  if(numDocAttesaFirma != "0"){
		 	$("#rileggiDatiRow").show();
		 	$("#btnRileggiDati").show();
		 }
		 
		 function rileggiDati(){
			historyReload();
		}	
	
	</c:if>
	
	function preAperturaModaleRichiestaFirma(idprg,iddocdig,indiceRiga){
		var oggettoComunic = "${oggettoComunic }";
		var colonnaDesc=$('#colW_DOCDIG_DIGDESDOC_' + indiceRiga);
		var desc = colonnaDesc.find("span").text();
		$('#oggettoDocumento').val(oggettoComunic + " - " + desc);
		apriModaleRichiestaFirma(idprg, iddocdig,'');
	}
</gene:javaScript>

<form name="formprotocollacomunicazione" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/wsdm/wsdm-comunicazione.jsp" /> 
	<input type="hidden" name="entita" value="${entitaWSDM}" />
	<input type="hidden" name="key1" value='${chiaveWSDM}' />
	<input type="hidden" name="idconfi" value='${idconfi}' />
	<input type="hidden" name="key2" value='' /> 
	<input type="hidden" name="key3" value='' />
	<input type="hidden" name="key4" value='' />
	<input type="hidden" name="idprg" value="" />
	<input type="hidden" name="idcom" value="" />
	<input type="hidden" name="idcfg" value="${cenint}" />
	<input type="hidden" name="metodo" value="apri" />
	<input type="hidden" name="activePage" value="0" />
	<input type="hidden" name="chiaveOriginale" value="" />
	<input type="hidden" name="comdatins" value="" />
</form>
