<%/*
   * Created on 18-ago-2009
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENEWEB" idMaschera="W_DISCUSS_P-scheda" >

	<gene:redefineInsert name="head">
		<style type="text/css">
		
			span.messaggiononletto {
				color: black;
				float: left;
				background-color: #FFA293; 
				margin-top: 5px;
				margin-bottom: 5px;
				padding-left: 5px;
				padding-right: 5px;
				padding-top: 1px;
				padding-bottom: 1px;
				border: 1px solid #C60010; 
				-moz-border-radius-topleft: 4px; 
				-webkit-border-top-left-radius: 4px; 
				-khtml-border-top-left-radius: 4px; 
				border-top-left-radius: 4px; 
			}
			
		</style>
	
	</gene:redefineInsert>
	
	<c:set var="titoloCompleto" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleDiscussioniFunction",pageContext,"W_DISCUSS_P")}'/>
	<c:choose>
		<c:when test='${fn:length(titoloCompleto) > 120}'>
			<gene:setString name="titoloMaschera" value='${fn:substring(titoloCompleto,0,120)}...'/>
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value='${titoloCompleto}' /> 
		</c:otherwise>
	</c:choose>
	

	<gene:redefineInsert name="corpo">
		<gene:formPagine gestisciProtezioni="true">
			<gene:pagina title="Conversazione" idProtezioni="conversazione" >
				<gene:formScheda entita="W_DISCUSS_P" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_DISCUSS_P" >
					<c:set var="entitaParent" value='${fn:substringBefore(keyParent,".")}' />
					<gene:campoScheda campo="DISCID_P" visibile="false" />
					<gene:campoScheda campo="DISCPRG" visibile="false" defaultValue='${sessionScope.moduloAttivo}' />
					<gene:campoScheda campo="DISCENT" visibile="false" defaultValue="${entitaParent}" />
					<gene:campoScheda campo="DISCKEY1" visibile="false" defaultValue='${gene:getValCampo(param.keyAdd, "DISCKEY1")}' />
					<gene:campoScheda campo="DISCKEY2" visibile="false" defaultValue='${gene:getValCampo(param.keyAdd, "DISCKEY2")}' />
					<gene:campoScheda campo="DISCKEY3" visibile="false" defaultValue='${gene:getValCampo(param.keyAdd, "DISCKEY3")}' />
					<gene:campoScheda campo="DISCKEY4" visibile="false" defaultValue='${gene:getValCampo(param.keyAdd, "DISCKEY4")}' />
					<gene:campoScheda campo="DISCKEY5" visibile="false" defaultValue='${gene:getValCampo(param.keyAdd, "DISCKEY5")}' />
					<gene:campoScheda campo="DISCMESSOPE" visibile="false" defaultValue='${sessionScope.profiloUtente.id}' />
					<gene:campoScheda title="Oggetto" campo="DISCOGGETTO" obbligatorio="true"/>
					<gene:campoScheda title="Creata da" campo="SYSUTE" entita="USRSYS" definizione="T" where="USRSYS.SYSCON = W_DISCUSS_P.DISCMESSOPE" modificabile="false" defaultValue="${sessionScope.profiloUtente.nome}"/>
					<gene:campoScheda title="Data inserimento" campo="DISCMESSINS" modificabile="false" visibile="${modo ne 'NUOVO'}"/>
		
					<input type="hidden" name="keyAdd" value="${param.keyAdd}" />
					<gene:campoScheda>
						<c:if test='${not (gene:checkProtFunz(pageContext, "MOD","SCHEDAMOD") and sessionScope.entitaPrincipaleModificabile eq "1" and (sessionScope.profiloUtente.id eq datiRiga.W_DISCUSS_DISCMESSOPE))}' >
							<gene:redefineInsert name="pulsanteModifica" />
							<gene:redefineInsert name="schedaModifica" />
						</c:if>
						<c:if test='${not (gene:checkProtFunz(pageContext, "INS","SCHEDANUOVO") and sessionScope.entitaPrincipaleModificabile eq "1")}' >
							<gene:redefineInsert name="pulsanteNuovo" />
							<gene:redefineInsert name="schedaNuovo" />
						</c:if>
						<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
					</gene:campoScheda>
		
				</gene:formScheda>
			</gene:pagina>
			<gene:pagina title="Messaggi" idProtezioni="messaggiconversazione" >
				<table class="dettaglio-tab-lista">
					<tr>
						<td>
							<gene:formLista entita="W_DISCUSS" where="W_DISCUSS.DISCID_P = #W_DISCUSS_P.DISCID_P# AND (W_DISCUSS.DISCMESSPUBBL = '1' OR W_DISCUSS.DISCMESSOPE = '${sessionScope.profiloUtente.id}')" sortColumn="-7" tableclass="datilista" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_DISCUSS">
								<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>" width="50">
									<c:set var="risultato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetMessaggiAltriDatiFunction",pageContext,datiRiga.W_DISCUSS_DISCID_P,datiRiga.W_DISCUSS_DISCID)}'/>
									<c:if test="${currentRow >= 0}" >
										<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
											<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza"/>
											<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD") and sessionScope.profiloUtente.id eq datiRiga.W_DISCUSS_DISCMESSOPE}' >
												<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" title="Modifica" />
											</c:if>
											<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL") and sessionScope.profiloUtente.id eq datiRiga.W_DISCUSS_DISCMESSOPE}' >
												<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina" />
											</c:if>
										</gene:PopUp>
										<c:if test='${gene:checkProtFunz(pageContext, "DEL","LISTADELSEL") and sessionScope.profiloUtente.id eq datiRiga.W_DISCUSS_DISCMESSOPE}' >
											<input type="checkbox" name="keys" value="${chiaveRiga}"  />
										</c:if>
									</c:if>
								</gene:campoLista>
								<gene:campoLista campo="DISCID" visibile="false" />
								<gene:campoLista campo="DISCID_P" visibile="false" />
								<gene:campoLista campo="DISCMESSOPE" visibile="false" />
								<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
								<gene:campoLista title="Mittente" campo="SYSUTE" entita="USRSYS" 
									definizione="T" 
									where="USRSYS.SYSCON=W_DISCUSS.DISCMESSOPE" 
									ordinabile="true" 
									href="${gene:if(visualizzaLink, link, '')}"/>
								<gene:campoLista title="Messaggio" campo="DISCMESSTESTO" ordinabile="true" >
									<c:if test="${messaggioLetto eq 'false'}">
										<br>
										<span class="messaggiononletto">Non letto</span>
									</c:if>
								</gene:campoLista>
								<gene:campoLista title="Data" campo="DISCMESSINS" ordinabile="true" />
								<gene:campoLista title="Allegati" campo="NUMEROALLEGATI" campoFittizio="true" definizione="N3;0" value="${numeroAllegati}"/>
								<gene:campoLista title="Pubblicato?" campo="DISCMESSPUBBL" ordinabile="true" visibile="false" />
							</gene:formLista>
						</td>
					</tr>
					<tr>
						<td class="comandi-dettaglio" colSpan="2">
							<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaPageNuovo")}' title='${gene:resource("label.tags.template.lista.listaPageNuovo")}' onclick="javascript:listaNuovo()">
							</c:if>
							<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
							</c:if>
							&nbsp;
						</td>
					</tr>
				</table>
				
			</gene:pagina>
		</gene:formPagine>
	</gene:redefineInsert>
</gene:template>