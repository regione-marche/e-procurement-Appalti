
<%
	/*
	 * Created on 03-Giu-2010
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

<%-- l'oggetto in sessione che contiene i filtri per comunicazioni inviate e ricevute e' il medesimo, per cui al primo accesso svuoto --%>
<c:if test="${not empty param.chiave}">
	<c:remove var="trovaW_INVCOM" scope="session" />
	<c:remove var="filtroComunicazioniOut" scope="session" />
	<c:remove var="filtroComunicazioniIn" scope="session" />
</c:if>

		<% //se la lista è su più pagine, quando si apre la prima volta è disponibile il parametro 'chiave' mentre quando
		   //si passa a un'altra pagina, si utilizza il parametro keyParent perchè il parametro 'chiave' non è più disponibile
		%>
		<c:choose>
			<c:when test='${not empty param.chiave}'>
				<c:set var="chiave" value='${param.chiave}'/>
			</c:when>
			<c:when test='${not empty param.keyParent}'>
				<c:set var="chiave" value='${param.keyParent}'/>
			</c:when>
		</c:choose>
		<c:choose>
			<c:when test='${not empty param.entitaWSDM}'>
				<c:set var="entitaWSDM" value='${param.entitaWSDM}'/>
			</c:when>
			<c:when test='${not empty keyParent}'>
				<c:set var="entitaWSDM" value='${entitaWSDM}'/>
			</c:when>
		</c:choose>
		<c:choose>
			<c:when test='${not empty param.chiaveWSDM}'>
				<c:set var="chiaveWSDM" value='${param.chiaveWSDM}'/>
			</c:when>
			<c:when test='${not empty keyParent}'>
				<c:set var="chiaveWSDM" value='${chiaveWSDM}'/>
			</c:when>
		</c:choose>
		<c:choose>
			<c:when test='${not empty param.idconfi}'>
				<c:set var="idconfi" value='${param.idconfi}'/>
			</c:when>
			<c:when test='${not empty keyParent}'>
				<c:set var="idconfi" value='${idconfi}'/>
			</c:when>
		</c:choose>
		<c:choose>
			<c:when test='${not empty param.idMeric}'>
				<c:set var="idMeric" value='${param.idMeric}'/>
			</c:when>
			<c:when test='${not empty keyParent}'>
				<c:set var="idMeric" value='${idMeric}'/>
			</c:when>
		</c:choose>
		
		<%--
		la prima volta che si apre la pagina dal dettaglio chiamante il param.chiave risulta valorizzato. 
		pertanto la prima esecuzione imposta la whereKey con il codice qui sotto, lo si inietta nel formLista, e poi si
		setta il valore anche via js nel trovaAddWhere in quanto le successive iterazioni cambia l'entita,non piu'
		l'entita' di partenza ma W_INVCOM(l'entità di partenza viene ricavata dall'entita presente nella chiave).
		 --%>
		<c:if test='${not empty chiave}'>
			<c:set var="campiKey" value='${fn:split(chiave,";")}' />
			<c:set var="addKeyRiga" value="" />
			<c:forEach begin="1" end="${fn:length(campiKey)}" step="1" varStatus="indicekey">
				<c:set var="strTmp" value='${fn:substringAfter(campiKey[indicekey.index-1], ":")}' />
				<c:choose>
					<c:when test="${indicekey.last}">
						<c:set var="addKeyRiga" value='${addKeyRiga}W_INVCOM.COMKEY${indicekey.index}=T:${strTmp}' />
						<c:set var="whereKey" value='${whereKey} W_INVCOM.COMKEY${indicekey.index}=${gene:concat(gene:concat("\'", strTmp), "\'")}' />
					</c:when>
					<c:otherwise>
						<c:set var="addKeyRiga" value='${addKeyRiga}W_INVCOM.COMKEY${indicekey.index}=T:${strTmp};' />
						<c:set var="whereKey" value='${whereKey} W_INVCOM.COMKEY${indicekey.index}=${gene:concat(gene:concat("\'", strTmp), "\'")} AND ' />
					</c:otherwise>
				</c:choose>
			</c:forEach>
			<c:choose>
				<c:when test='${not empty param.chiave}'>
					<c:set var="whereKey" value="${whereKey} AND W_INVCOM.COMENT='${param.entita}' " />
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${fn:contains(chiave,'TORN')}">
							<c:set var="whereKey" value="${whereKey} AND W_INVCOM.COMENT='TORN' " />
						</c:when>
						<c:when test="${fn:contains(chiave,'GARECONT')}">
							<c:set var="whereKey" value="${whereKey} AND W_INVCOM.COMENT='GARECONT' " />
						</c:when>
						<c:when test="${fn:contains(chiave,'NSO_ORDINI')}">
							<c:set var="whereKey" value="${whereKey} AND W_INVCOM.COMENT='NSO_ORDINI' " />
						</c:when>
						<c:otherwise>
							<c:set var="whereKey" value="${whereKey} AND W_INVCOM.COMENT='GARE' " />
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</c:if>
		
		
		<c:if test="${! empty sessionScope.filtroComunicazioniOut}">
			<c:set var="filtroComunicazioniOut" value=" and ${sessionScope.filtroComunicazioniOut}" scope="page" />
		</c:if>

		<c:set var="comkey1" value='${gene:getValCampo(addKeyRiga, "COMKEY1")}'/>
		<c:set var="coment" value='${fn:substringBefore(chiave,".")}' />
		<c:if test="${coment eq 'GAREAVVISI' }">
			<c:set var="coment" value='GARE' />
		</c:if>
		<c:set var="chiaveControlloAutorizzazioni" value='${comkey1}' />
		<%--
			Nel caso di ODA delle ricerche di mercato, nella chiave è presente il valore GARECONT.NGARA,
			ma il controllo per i permessi va fatto considerando l'id della ricerca di mercato!!! 
		--%>
		
		<c:if test="${coment eq 'GARECONT' }">
			<c:set var="chiaveControlloAutorizzazioni" value='${idMeric}' />
		</c:if>
		<c:set var="autorizzatoGestioneComunicazioneGaraLotto" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.AutorizzatoGestioneComunicazioneGaraLottoFunction",pageContext,coment,chiaveControlloAutorizzazioni)}' />

		<c:set var="genere" value='${param.genere}'/>
		<c:choose>
			<c:when test="${genere eq 1}">
				<c:set var="titoloMaschera" value="Comunicazioni inviate o da inviare per il lotto di gara ${comkey1}" />
			</c:when>
			<c:when test="${genere eq 10}">
				<c:set var="titoloMaschera" value="Comunicazioni inviate o da inviare per l'elenco ${comkey1}" />
			</c:when>
			<c:when test="${genere eq 20}">
				<c:set var="titoloMaschera" value="Comunicazioni inviate o da inviare per il catalogo ${comkey1}" />
			</c:when>
			<c:when test="${genere eq 11}">
				<c:set var="titoloMaschera" value="Comunicazioni inviate o da inviare per l'avviso ${comkey1}" />
			</c:when>
			<c:when test="${genere eq 4}">
				<c:set var="titoloMaschera" value="Comunicazioni inviate o da inviare per l'ordine di acquisto ${comkey1}" />
			</c:when>
			<c:when test="${genere eq 40}">
				<%-- Section added for NSO Communications --%>
				<c:set var="titoloMaschera" value="Comunicazioni inviate o da inviare per l'ordine ${param.COD_ORD}" />
			</c:when>
			<c:otherwise>
				<c:set var="titoloMaschera" value="Comunicazioni inviate o da inviare per la gara ${comkey1}" />
			</c:otherwise>
		</c:choose>

<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, comkey1)}'/>
<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codgar, idconfi)}'/>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" idMaschera="W_INVCOM-lista" schema="GENEWEB">
	<gene:setString name="titoloMaschera" value="${titoloMaschera}" />
	
	<gene:redefineInsert name="addToAzioni">
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:impostaFiltro();" title='Imposta filtro' tabindex="1503">
					Imposta filtro
				</a>
			</td>
		</tr>
	</gene:redefineInsert>
		
	<gene:redefineInsert name="corpo">
		
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>

		<c:choose>
			<c:when test="${entita eq 'GARECONT'}">
				<c:set var="tipo" value='7' />
				<c:set var="IsW_CONFCOMPopolata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsW_CONFCOMPopolataFunction",pageContext,tipo)}' />
			</c:when>
			<c:otherwise>
		
				<c:set var="chiaveGara" value="GARE.NGARA=T:${comkey1}" />
				<c:set var="tipo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction",  pageContext,chiaveGara)}' />
		
				<c:if test="${tipo eq 1}">
					<c:set var="tipo" value='2' />
				</c:if>		

				<c:if test="${! empty tipo}">
					<c:set var="IsW_CONFCOMPopolata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsW_CONFCOMPopolataFunction",pageContext,tipo)}' />
				</c:if>				
		
			</c:otherwise>
		</c:choose>

		<table class="lista">

		<c:if test="${!empty sessionScope.filtroComunicazioniOut}">
			<tr>
				<td style="font: 11px Verdana, Arial, Helvetica, sans-serif;">
					<br/>
					<img src="${pageContext.request.contextPath}/img/filtro.gif" alt="Filtro">&nbsp;<span style="color: #ff0028; font-weight: bold;">Lista filtrata</span>
					&nbsp;&nbsp;&nbsp;[ <a href="javascript:annullaFiltro();" ><IMG SRC="${pageContext.request.contextPath}/img/cancellaFiltro.gif" alt="Cancella filtro"></a>
					<a class="link-generico" href="javascript:annullaFiltro();">Cancella filtro</a> ]
				</td>
			</tr>
		</c:if>
			<tr>
				<td><gene:formLista entita="W_INVCOM" pagesize="20" 
					tableclass="datilista" gestisciProtezioni="true" sortColumn="-3" where="${whereKey}${filtroComunicazioniOut}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_INVCOM">
					<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"
						width="50">
						<c:if test="${currentRow >= 0}">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"
								onClick="chiaveRiga='${chiaveRigaJava}'">
								<gene:PopUpItemResource
									resource="popupmenu.tags.lista.visualizza"
									title="Visualizza" />
								<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD") && datiRiga.W_INVCOM_COMSTATO eq "1" && autorizzatoGestioneComunicazioneGaraLotto eq "true"}'>
									<gene:PopUpItemResource
										resource="popupmenu.tags.lista.modifica"
										title="Modifica" />
								</c:if>
								<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL") && autorizzatoGestioneComunicazioneGaraLotto eq "true" && datiRiga.W_INVCOM_COMSTATO ==1}'>
									<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina"
										title="Elimina" />
								</c:if>
								<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENEWEB.W_INVCOM-lista.copiaComunicazione") && autorizzatoGestioneComunicazioneGaraLotto eq "true" && (datiRiga.W_INVCOM_COMSTATO ne "15" && datiRiga.W_INVCOM_COMSTATO ne "14")}' >
									<gene:PopUpItem title="Copia comunicazione" href="copiaComunicazione('${chiaveRigaJava}','${datiRiga.W_INVCOM_COMDATINS }','${datiRiga.W_INVCOM_COMPUB }','0','${datiRiga.W_INVCOM_COMMODELLO }')" />
								</c:if>
								<c:if test='${autorizzatoGestioneComunicazioneGaraLotto eq "true" && datiRiga.W_INVCOM_COMSTATO eq "15"}' >
									<gene:PopUpItem title="Reinvia comunicazione" href="reinviaComunicazione('${chiaveRigaJava}','${datiRiga.W_INVCOM_COMDATINS }','${datiRiga.W_INVCOM_COMPUB }','0')" />
								</c:if>
								<c:if test='${datiRiga.W_INVCOM_COMSTATO eq "4" && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENEWEB.W_INVCOM-lista.copiaComunicazioneReinvio") && autorizzatoGestioneComunicazioneGaraLotto eq "true"}' >
									<gene:PopUpItem title="Copia per reinvio a destinatari con errore" href="copiaComunicazione('${chiaveRigaJava}','${datiRiga.W_INVCOM_COMDATINS }','${datiRiga.W_INVCOM_COMPUB }','1','${datiRiga.W_INVCOM_COMMODELLO }')" />
								</c:if>
								<c:if test='${datiRiga.W_INVCOM_COMSTATO eq "3" && datiRiga.W_INVCOM_COMPUB eq 1 && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENEWEB.W_INVCOM-lista.archiviaComunicazione") && autorizzatoGestioneComunicazioneGaraLotto eq "true"}' >
									<gene:PopUpItem title="Archivia comunicazione" href="archiviaComunicazione('${chiaveRigaJava}','${datiRiga.W_INVCOM_COMDATINS }')" />
								</c:if>
							</gene:PopUp>
							<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && autorizzatoGestioneComunicazioneGaraLotto eq "true" && datiRiga.W_INVCOM_COMSTATO ==1}'>
								<input type="checkbox" name="keys" value="${chiaveRiga}" />
							</c:if>
						</c:if>
					</gene:campoLista>
					<input type="hidden" name="keyAdd" value="${addKeyRiga}" />
					<input type="hidden" name="tipo" value="${tipo}" />
					<input type="hidden" name="chiaveWSDM" id="chiaveWSDM" value="${chiaveWSDM}" />
					<input type="hidden" name="entitaWSDM" id="entitaWSDM" value="${entitaWSDM}" />
					<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}" />
					<input type="hidden" name="genere" value="${genere}" />
					<input type="hidden" name="idMeric" id="idMeric" value="${idMeric}" />
					
					<gene:campoLista campo="IDPRG" visibile="false" />
					<gene:campoLista campo="IDCOM" visibile="false" title="N°" width="40" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
					<gene:campoLista campo="COMENT" visibile="false" />
					<gene:campoLista campo="COMKEY1" visibile="false" />
					<gene:campoLista campo="COMKEY2" visibile="false" />
					<gene:campoLista campo="COMKEY3" visibile="false" />
					<gene:campoLista campo="COMKEY4" visibile="false" />
					<gene:campoLista campo="COMKEY5" visibile="false" />
					<gene:campoLista campo="COMMSGOGG" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
					<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
					<gene:campoLista campo="COMPUB" width="100" visibile='${fn:contains(listaOpzioniDisponibili, "OP114#")}' gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoCOMPUB"/>
					<gene:campoLista campo="COMDATINS" width="150" definizione="D;0;;TIMESTAMP;COMDATINS" />
					<gene:campoLista campo="COMSTATO" width="80" />
					<gene:campoLista campo="COMNUMPROT" width="80" visibile="${integrazioneWSDM =='1'}" title="Num.prot."/>
					<gene:campoLista campo="COMMODELLO" visibile="false" />
				</gene:formLista></td>
			</tr>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<c:if test="${autorizzatoGestioneComunicazioneGaraLotto eq 'true'}">
						<gene:insert name="pulsanteListaInserisci">
							<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:listaNuovaComunicazione()">
							</c:if>
						</gene:insert>
						<gene:insert name="pulsanteListaEliminaSelezione">
							<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
							</c:if>
						</gene:insert>
					</c:if>
					&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>

	<c:choose>
		<c:when test="${autorizzatoGestioneComunicazioneGaraLotto eq 'false'}">
			<gene:redefineInsert name="listaNuovo" />
			<gene:redefineInsert name="listaEliminaSelezione" />
		</c:when>
		<c:otherwise>
			<gene:redefineInsert name="listaNuovo">
				<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:listaNuovaComunicazione();" title="Inserisci" tabindex="1501">
							${gene:resource("label.tags.template.lista.listaNuovo")}</a></td>
				</tr>
				</c:if>
			</gene:redefineInsert>
		</c:otherwise>
	</c:choose>
	
	<gene:javaScript>
		<c:if test='${not empty param.chiave}'>
			document.forms[0].keyParent.value="${param.chiave}";
			document.forms[0].trovaAddWhere.value="${whereKey}";
		</c:if>
		
		/*
		* con la variabile tipo indico il tipo della funzione da chiamare.
		* tipo:0 Copia comunicazione
		*      1 copia comunicazione per reinvio
		*/
		function copiaComunicazione(key,comdatins,compub,tipo,commodello){
			var chiave=key.split(";");
			var idprg = chiave[0].substr(chiave[0].lastIndexOf(":") + 1);
			var idcom = chiave[1].substr(chiave[1].lastIndexOf(":") + 1);
			href = "href=geneweb/w_invcom/w_invcom-popup-copiaComunicazione.jsp";
			href += "&idprg=" + idprg;
			href += "&idcom=" + idcom;
			href += "&comdatins=" + comdatins;
			href += "&compub=" + compub;
			href += "&tipo=" + tipo;
			href += "&commodello=" + commodello;
			openPopUpCustom(href, "copiaComunicazione", 600, 300, "yes", "yes");
		}
		
		function listaNuovaComunicazione(){
			var IsW_CONFCOMPopolata = "${IsW_CONFCOMPopolata}";
			var tipo = "${tipo}";
			var idconfi = "${idconfi}";
			var keyAdd = document.forms[0].keyAdd.value;
			var keyParent = document.forms[0].keyParent.value;
			var chiaveWSDM =  document.forms[0].chiaveWSDM.value;
			var entitaWSDM =  document.forms[0].entitaWSDM.value;
			if(IsW_CONFCOMPopolata == "true"){
				document.location.href = contextPath + "/pg/InitNuovaComunicazione.do?"+csrfToken+"&genere=" + tipo + "&keyAdd=" + keyAdd+"&keyParent=" + keyParent + "&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM + "&idconfi=" + idconfi;
			}else{
				document.forms[0].keyParent.value="${param.chiave}";
				listaNuovo();
			}
		}

		function impostaFiltro(){
			var comando = "href=geneweb/w_invcom/w_invcom-popup-filtro-trova.jsp&integrazioneWSDM=${integrazioneWSDM}";
			openPopUpCustom(comando, "impostaFiltro", 800, 320, "yes", "yes");
		}

		function annullaFiltro(){
			var comando = "href=gare/commons/popup-filtro.jsp&annulla=5";
			openPopUpCustom(comando, "impostaFiltro", 10, 10, "no", "no");
		}
		
		function reinviaComunicazione(){
			var comkey1 = "${comkey1}";
			href = "href=geneweb/w_invcom/w_invcom-popup-reinviaComunicazione.jsp&ngara="+comkey1;
			openPopUpCustom(href, "reinviaComunicazione", 700, 350, "yes", "yes");
		}
		
		
		function archiviaComunicazione(key,comdatins){
			var chiave=key.split(";");
			var idprg = chiave[0].substr(chiave[0].lastIndexOf(":") + 1);
			var idcom = chiave[1].substr(chiave[1].lastIndexOf(":") + 1);
			var comkey1 ="${comkey1 }";
			href = "href=geneweb/w_invcom/w_invcom-popup-archiviaComunicazione.jsp";
			href += "&idprg=" + idprg;
			href += "&idcom=" + idcom;
			href += "&comkey1=" + comkey1;
			href += "&comdatins=" + comdatins;
			openPopUpCustom(href, "archiviaComunicazione", 600, 300, "yes", "yes");
		}

	</gene:javaScript>
	
</gene:template>
