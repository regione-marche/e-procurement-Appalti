<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<c:set var="isConfigCAISAbilitata" value='${gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.Configurazione-CAIS")}' scope="page" />
<c:set var="isConfigIntegrazioneGare" value='${gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.Configurazione-integrazioneWSDM")}' scope="page" />
<c:set var="isConfigIntegrazioneWSERP" value='${gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.Configurazione-integrazioneWSERP")}' scope="page" />
<c:set var="isConfigW_CONFCOMAbilitata" value='${gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.Configurazione-W_CONFCOM")}' scope="page" />
<c:set var="isConfigG1CRIMODAbilitata" value='${gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.Configurazione-G1CRIMOD")}' scope="page" />
<c:set var="isConfigARCHDOCGAbilitata" value='${gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.Configurazione-ARCHDOCG")}' scope="page" />
<c:set var="isConfigCONFOPECOAbilitata" value='${gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.Configurazione-CONFOPECO")}' scope="page" />
<c:set var="isConfigDETMOTAbilitata" value='${gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.Configurazione-DETMOT")}' scope="page" />
<c:set var="isConfigCATPUBAbilitata" value='${gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.Configurazione-CATPUB")}' scope="page" />
<c:set var="isConfigCATSCAAbilitata" value='${gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.Configurazione-CATSCA")}' scope="page" />
<c:set var="isConfigVIGILANZAAbilitata" value='${gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.Configurazione-VIGILANZA")}' scope="page" />


	<c:if test='${isConfigCAISAbilitata}'>
	$(function() {
				$("#configurazione").append(
"				<p>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=gene/cais/cais-lista.jsp&filtroArchiviata=2'+ '&' + csrfToken;\">\n"+
"					<img alt=\"Categorie d'iscrizione\" src=\"${contextPath}/img/Content-33.png\"></a>\n"+
"					&nbsp;&nbsp;&nbsp;\n"+
"					<b>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=gene/cais/cais-lista.jsp&filtroArchiviata=2'+ '&' + csrfToken;\">Categorie d'iscrizione</a>\n"+
"					</b>\n"+
"				</p>\n"
				);
			});
			
	</c:if>
	
	<c:if test='${isConfigDETMOTAbilitata}'>
	$(function() {
				$("#configurazione").append(
"				<p>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=gare/detmot/detmot-lista.jsp'+ '&' + csrfToken;\">\n"+
"					<img alt=\"Dettagli motivo esclusione ditte in gara\" src=\"${contextPath}/img/DettagliMotivoEsclusione.png\"></a>\n"+
"					&nbsp;&nbsp;&nbsp;\n"+
"					<b>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=gare/detmot/detmot-lista.jsp'+ '&' + csrfToken;\">Dettagli motivo esclusione ditte in gara</a>\n"+
"					</b>\n"+
"				</p>\n"
				);
			});
	</c:if>
	
	<c:if test='${isConfigARCHDOCGAbilitata}'>
	$(function() {
				$("#configurazione").append(
"				<p>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=gare/archdocg/archdocg-lista.jsp'+ '&' + csrfToken;\">\n"+
"					<img alt=\"Documentazione di gara\" src=\"${contextPath}/img/ArchivioDocumentazione.png\"></a>\n"+
"					&nbsp;&nbsp;&nbsp;\n"+
"					<b>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=gare/archdocg/archdocg-lista.jsp'+ '&' + csrfToken;\">Documentazione di gara</a>\n"+
"					</b>\n"+
"				</p>\n"
				);
			});
	</c:if>

	<c:if test='${isConfigW_CONFCOMAbilitata}'>
	$(function() {
				$("#configurazione").append(
"				<p>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=geneweb/w_confcom/modelliComunicazioni-lista.jsp'+ '&' + csrfToken;\">\n"+
"					<img alt=\"Modelli di comunicazioni\" src=\"${contextPath}/img/ModelliComunicazioni.png\"></a>\n"+
"					&nbsp;&nbsp;&nbsp;\n"+
"					<b>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=geneweb/w_confcom/modelliComunicazioni-lista.jsp'+ '&' + csrfToken;\">Modelli di comunicazioni</a>\n"+
"					</b>\n"+
"				</p>\n"
				);
			});
	</c:if>
	
	<c:if test='${isConfigG1CRIMODAbilitata}'>
	$(function() {
				$("#configurazione").append(
"				<p>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=gare/g1crimod/g1crimod-lista.jsp'+ '&' + csrfToken;\">\n"+
"					<img alt=\"Modelli di criteri\" src=\"${contextPath}/img/modelliCriteriOepv.png\"></a>\n"+
"					&nbsp;&nbsp;&nbsp;\n"+
"					<b>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=gare/g1crimod/g1crimod-lista.jsp'+ '&' + csrfToken;\">Modelli di criteri di valutazione per OEPV</a>\n"+
"					</b>\n"+
"				</p>\n"
				);
			});
	</c:if>
	
	<c:if test="${isConfigIntegrazioneGare }">
		$(function() {
					$("#parametri").append(
	"					<p>\n"+
	"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=geneweb/wsdmconfi/wsdmconfi-lista.jsp';\">\n"+
	" 					<img alt=\"Gestione parametri\" src=\"${contextPath}/img/configurazioneWSDM.png\"></a>\n"+
	"					&nbsp;&nbsp;&nbsp;\n"+
	"					<b>\n"+
	"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=geneweb/wsdmconfi/wsdmconfi-lista.jsp';\">Integrazione con sistema di protocollazione e gestione documentale</a>\n"+
	"					</b>\n"+
	"					</p>\n"
					);
				});
	</c:if>
	
	<c:if test="${isConfigIntegrazioneWSERP}">
		$(function() {
					$("#parametri").append(
	"					<p>\n"+
	"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=geneweb/w_config/dettConfig.jsp&detail=WSERP'+ '&' + csrfToken;\">\n"+
	" 					<img alt=\"Gestione parametri\" src=\"${contextPath}/img/configurazioneWSERP.png\"></a>\n"+
	"					&nbsp;&nbsp;&nbsp;\n"+
	"					<b>\n"+
	"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=geneweb/w_config/dettConfig.jsp&detail=WSERP'+ '&' + csrfToken;\">Integrazione con sistema ERP</a>\n"+
	"					</b>\n"+
	"					</p>\n"
					);
				});
		
	
	</c:if>
	<c:if test='${isConfigVIGILANZAAbilitata}'>
		$(function() {
					$("#parametri").append(
	"					<p>\n"+
	"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=geneweb/w_config/dettConfig.jsp&detail=Vigilanza'+ '&' + csrfToken;\">\n"+
	" 					<img alt=\"Gestione parametri\" src=\"${contextPath}/img/Status-30.png\"></a>\n"+
	"					&nbsp;&nbsp;&nbsp;\n"+
	"					<b>\n"+
	"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=geneweb/w_config/dettConfig.jsp&detail=Vigilanza'+ '&' + csrfToken;\">Integrazione con Vigilanza</a>\n"+
	"					</b>\n"+
	"					</p>\n"
					);
				});
	</c:if>
	<c:if test='${isConfigCONFOPECOAbilitata}'>
	$(function() {
				$("#parametri").append(
"				<p>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=gare/confopeco/confopeco-lista.jsp'+ '&' + csrfToken;\">\n"+
"					<img alt=\"Configurazione selezione da elenco operatori\" src=\"${contextPath}/img/ConfigurazioneElencoOperatori.png\"></a>\n"+
"					&nbsp;&nbsp;&nbsp;\n"+
"					<b>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=gare/confopeco/confopeco-lista.jsp'+ '&' + csrfToken;\">Configurazione selezione da elenco operatori</a>\n"+
"					</b>\n"+
"				</p>\n"
				);
			});
	</c:if>
	
	<c:if test='${isConfigCATPUBAbilitata}'>
	$(function() {
				$("#configurazione").append(
"				<p>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=gare/catpub/catpub-lista.jsp'+ '&' + csrfToken;\">\n"+
"					<img alt=\"Pubblicazioni bando ed esito di gara\" src=\"${contextPath}/img/PubblicazioniBandoEsito.png\"></a>\n"+
"					&nbsp;&nbsp;&nbsp;\n"+
"					<b>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=gare/catpub/catpub-lista.jsp'+ '&' + csrfToken;\">Pubblicazioni bando ed esito di gara</a>\n"+
"					</b>\n"+
"				</p>\n"
				);
			});
	</c:if>
	
	
	<c:if test='${isConfigCATSCAAbilitata}'>
	$(function() {
				$("#parametri").append(
"				<p>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=gare/catsca/catsca-lista.jsp'+ '&' + csrfToken;\">\n"+
"					<img alt=\"Configurazione scadenze gara\" src=\"${contextPath}/img/ConfigurazioneScadenzeGara.png\"></a>\n"+
"					&nbsp;&nbsp;&nbsp;\n"+
"					<b>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?href=gare/catsca/catsca-lista.jsp'+ '&' + csrfToken;\">Configurazione scadenze gara</a>\n"+
"					</b>\n"+
"				</p>\n"
				);
			});
	</c:if>
		
	