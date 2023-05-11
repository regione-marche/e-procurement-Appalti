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
		
			span.messaggiogenerico {
				color: black;
				float: left;
				margin-top: 1px;
				margin-bottom: 1px;
				padding-left: 3px;
				padding-right: 3px;
				padding-top: 1px;
				padding-bottom: 1px;
				-moz-border-radius-topleft: 4px; 
				-webkit-border-top-left-radius: 4px; 
				-khtml-border-top-left-radius: 4px; 
				border-top-left-radius: 4px;
			}
		
			span.messaggiononletto {
				background-color: #FFA293;
				border: 1px solid #C60010; 
			}
			
			span.messaggioletto {
				background-color: #59C600; 
				border: 1px solid #397F00; 
			}
			
			span.messaggiononpubblicato {
				margin-right: 3px;
				background-color: #FFDD00; 
				border: 1px solid #FF8800; 
			}
			
		</style>
	
	</gene:redefineInsert>
	
	
	<c:set var="discid_p" value='${gene:getValCampo(key,"DISCID_P")}' scope="request"/>
	<c:if test='${discid_p eq ""}'>
		<c:set var="discid_p" value='${gene:getValCampo(keyParent,"DISCID_P")}' scope="request"/>
	</c:if>
 
 	<c:set var="getDiscOperatore" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDiscOperatoreFunction",pageContext,discid_p)}'/>
	<c:set var="getDiscDestType" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDiscDestTypeFunction",pageContext,discid_p)}'/>
	<c:set var="getDiscDisckey1Type" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDiscDisckey1Function",pageContext,discid_p)}'/>	
	<c:set var="getDiscent" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDiscentFunction",pageContext,discid_p)}'/>

	
	<c:set var="titoloCompleto" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleDiscussioniFunction",pageContext,"W_DISCUSS_P")}'/>
	<c:choose>
		<c:when test="${modo eq 'NUOVO'}">
			<c:choose>
				<c:when test='${fn:length(titoloCompleto) > 100}'>
					<gene:setString name="titoloMaschera" value='${fn:substring(titoloCompleto,0,100)}...'/>
				</c:when>
				<c:otherwise>
					<gene:setString name="titoloMaschera" value='${titoloCompleto}' /> 
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
			<c:when test='${fn:length(titoloCompleto) > 100}'>
				<gene:setString name="titoloMaschera" value='Conversazione ${fn:substring(titoloCompleto,0,100)}...'/>
			</c:when>
			<c:otherwise>
				<gene:setString name="titoloMaschera" value='Conversazione ${titoloCompleto}' /> 
			</c:otherwise>
			</c:choose>
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
					<gene:campoScheda title="Destinatari delle notifiche email" campo="DISCDESTTYPE" obbligatorio="true">
						<gene:addValue value="1" descr="Tutti gli utenti"/>
						<gene:addValue value="2" descr="Solo alcuni utenti"/>
					</gene:campoScheda>
		
					<input type="hidden" name="keyAdd" value="${param.keyAdd}" />
					<gene:campoScheda>
						<gene:redefineInsert name="schedaNuovo" />
						<gene:redefineInsert name="pulsanteNuovo" />
						
						<c:choose>
						<c:when test='${datiRiga.W_DISCUSS_P_DISCENT eq "GARE"}' >
							<c:set var="entita" value="V_GARE_TORN"/>
							<c:set var="inputFiltro" value="CODGAR=T:$${datiRiga.W_DISCUSS_P_DISCKEY1}"/>
							<c:set var="filtroCampoEntita" value="codgar=#CODGAR#"/>
						</c:when>
						<c:otherwise>
							<c:set var="entita" value="V_GARE_STIPULA"/>
							<c:set var="inputFiltro" value="IDSTIPULA=T:${datiRiga.W_DISCUSS_P_DISCKEY1}"/>
							<c:set var="filtroCampoEntita" value="idstipula=#IDSTIPULA#"/>
						</c:otherwise>
						</c:choose>
						
						<c:if test='${modoAperturaScheda eq "VISUALIZZA"}'>
							<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, entita)}' />
							<c:set var="autorizzatoModifiche" value="1" scope="request" />
							<c:if test='${!empty (filtroLivelloUtente)}'>
							<gene:sqlSelect nome="autori" parametri="${inputFiltro}" tipoOut="VectorString" >
								select autori from g_permessi where ${filtroCampoEntita} and g_permessi.syscon = ${profiloUtente.id}
							</gene:sqlSelect>
							<c:if test='${!empty autori}'>
								<c:set var="autorizzatoModifiche" value="${autori[0]}" scope="request" />
							</c:if>
							</c:if>
						</c:if>
						<c:if test='${not((gene:checkProtFunz(pageContext, "MOD","SCHEDAMOD")) and (autorizzatoModifiche ne "2" and sessionScope.profiloUtente.id eq datiRiga.W_DISCUSS_P_DISCMESSOPE )) }' >
							<gene:redefineInsert name="pulsanteModifica" />
							<gene:redefineInsert name="schedaModifica" />
						</c:if>
						<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
					</gene:campoScheda>
		
				</gene:formScheda>
			</gene:pagina>
			
			<gene:pagina title="Destinatari notifica email" idProtezioni="destinatari" visibile="${discdesttype eq 2}">
				<table class="dettaglio-tab-lista">
					<tr>
						<td>
							<gene:formLista entita="W_DISCDEST" where='W_DISCDEST.DISCID_P = #W_DISCUSS_P.DISCID_P# AND W_DISCDEST.DISCID = -1' sortColumn="2" tableclass="datilista" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_DISCDEST">
							
								<c:choose>
								<c:when test='${datiRiga.W_DISCUSS_P_DISCENT eq "GARE"}' >
									<c:set var="entita" value="V_GARE_TORN"/>
									<c:set var="inputFiltro" value="CODGAR=T:$${datiRiga.W_DISCUSS_P_DISCKEY1}"/>
									<c:set var="filtroCampoEntita" value="codgar=#CODGAR#"/>
								</c:when>
								<c:otherwise>
									<c:set var="entita" value="V_GARE_STIPULA"/>
									<c:set var="inputFiltro" value="IDSTIPULA=T:${datiRiga.W_DISCUSS_P_DISCKEY1}"/>
									<c:set var="filtroCampoEntita" value="idstipula=#IDSTIPULA#"/>
								</c:otherwise>
								</c:choose>
							
								<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, entita)}' />
								<c:set var="autorizzatoModifiche" value="1" scope="request" />
								<c:if test='${!empty (filtroLivelloUtente)}'>
									<gene:sqlSelect nome="autori" parametri="${inputFiltro}" tipoOut="VectorString" >
										select autori from g_permessi where ${filtroCampoEntita} and g_permessi.syscon = ${profiloUtente.id}
									</gene:sqlSelect>
									<c:if test='${!empty autori}'>
										<c:set var="autorizzatoModifiche" value="${autori[0]}" scope="request" />
									</c:if>
								</c:if>
								<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>" width="50">
									<c:if test="${currentRow >= 0}" >
										<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
											<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL") and sessionScope.profiloUtente.id eq operatore and autorizzatoModifiche ne "2"}' >
												<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina" />
											</c:if>
										</gene:PopUp>
										<c:if test='${gene:checkProtFunz(pageContext, "DEL","LISTADELSEL") and sessionScope.profiloUtente.id eq operatore and autorizzatoModifiche ne "2"}' >
											<input type="checkbox" name="keys" value="${chiaveRiga}"  />
										</c:if>
									</c:if>
								</gene:campoLista>
								<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
								<gene:campoLista campo="DISCID_P" visibile="false" />
								<gene:campoLista campo="DISCID" visibile="false" />
								<gene:campoLista campo="DESTNUM" visibile="false" />
								<gene:campoLista campo="DESTID" visibile="false" />
								<gene:campoLista title="Destinatario" campo="DESTNAME" visibile="true" />
								<gene:campoLista title="Indirizzo email" campo="DESTMAIL" ordinabile="true" />
								<gene:campoLista entita="W_DISCUSS_P" campo="DISCENT" where="W_DISCDEST.DISCID_P = W_DISCUSS_P.DISCID_P" visibile="false" />
								<gene:campoLista entita="W_DISCUSS_P" campo="DISCKEY1" where="W_DISCDEST.DISCID_P = W_DISCUSS_P.DISCID_P" visibile="false" />
							</gene:formLista>
						</td>
					</tr>
					<c:choose>
						<c:when test='${sessionScope.profiloUtente.id eq operatore and autorizzatoModifiche ne "2"}'>
							<gene:redefineInsert name="listaNuovo">
								<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:aggiungiDestinatari('${discid_p}','${disckey1}','${discent}');" title="Aggiungi destinatari" tabindex="1504">Aggiungi destinatari</a>
										</td>
									</tr>
								</c:if>
							</gene:redefineInsert>


							<tr>
								<td class="comandi-dettaglio" colSpan="2">
									<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
										<INPUT type="button"  class="bottone-azione" value='Aggiungi destinatari' title='Aggiungi  destinatari' onclick="javascript:aggiungiDestinatari('${discid_p}','${disckey1}','${discent}');">&nbsp;&nbsp;									
									</c:if>
									<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
										<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
									</c:if>
									&nbsp;
								</td>
							</tr>
						</c:when>
						<c:otherwise>
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
				
				<gene:javaScript>
					function aggiungiDestinatari(discid_p, disckey1, entitaParent){
						openPopUpCustom("href=geneweb/w_discuss_p/w_discuss_p-aggiungi-destinatari-popup.jsp&modo=NUOVO"+"&discid_p=" + discid_p + "&disckey1=" + disckey1 + "&" + "&entitaParent=" + entitaParent + "&" + csrfToken, "aggiungidestinatari", 850, 500, "yes", "yes");
					}
				
				</gene:javaScript>
				
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
											<c:if test="${datiRiga.W_DISCUSS_DISCMESSPUBBL != '1'}">
												<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD") and sessionScope.profiloUtente.id eq datiRiga.W_DISCUSS_DISCMESSOPE}' >
													<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" title="Modifica" />
												</c:if>
												<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL") and sessionScope.profiloUtente.id eq datiRiga.W_DISCUSS_DISCMESSOPE}' >
													<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina" />
												</c:if>
											</c:if>
										</gene:PopUp>
										<c:if test="${datiRiga.W_DISCUSS_DISCMESSPUBBL != '1'}">
											<c:if test='${gene:checkProtFunz(pageContext, "DEL","LISTADELSEL") and sessionScope.profiloUtente.id eq datiRiga.W_DISCUSS_DISCMESSOPE}' >
												<input type="checkbox" name="keys" value="${chiaveRiga}"  />
											</c:if>
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
									<br>
									<c:if test="${messaggioPubblicato eq 'false'}">
										<span class="messaggiogenerico messaggiononpubblicato">Messaggio non inviato</span>
									</c:if>
									
									<c:if test="${sessionScope.profiloUtente.id ne datiRiga.W_DISCUSS_DISCMESSOPE}">
										<c:choose>
											<c:when test="${messaggioLetto eq 'false'}">
												<span class="messaggiogenerico messaggiononletto" id="spannonletto_${datiRiga.W_DISCUSS_DISCID}_${datiRiga.W_DISCUSS_DISCID}">
													<a style="text-decoration: none;" href="javascript:setMessaggioLetto('${datiRiga.W_DISCUSS_DISCID_P}','${datiRiga.W_DISCUSS_DISCID}','${sessionScope.profiloUtente.id}');"  >
														Non letto
													</a>
												</span>
											</c:when>
											<c:otherwise>
												<span class="messaggiogenerico messaggioletto" id="spanletto_${datiRiga.W_DISCUSS_DISCID}_${datiRiga.W_DISCUSS_DISCID}">
													<a style="text-decoration: none;" href="javascript:setMessaggioNonLetto('${datiRiga.W_DISCUSS_DISCID_P}','${datiRiga.W_DISCUSS_DISCID}','${sessionScope.profiloUtente.id}');"  >
														Letto
													</a>
												</span>
											</c:otherwise>
										</c:choose>
									</c:if>
								</gene:campoLista>
								<gene:campoLista title="Data invio" campo="DISCMESSINS" ordinabile="true" />
								<gene:campoLista title="N.allegati" campo="NUMEROALLEGATI" campoFittizio="true" definizione="N3;0" value="${numeroAllegati}"/>
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
				
				<gene:javaScript>
					function setMessaggioLetto(discid_p, discid, syscon) {
						$.ajax({
					        type: "GET",
					        async: false,
					        beforeSend: function(x) {
							if(x && x.overrideMimeType) {
								x.overrideMimeType("application/json;charset=UTF-8");
						       }
							},
					        url: "${pageContext.request.contextPath}" + "/pg/EseguiOperazioniConversazione.do",
					        data: "discid_p=" + discid_p + "&discid=" + discid + "&syscon=" + syscon + "&operazione=setMessaggioLetto"
					    });
					    historyReload();
					}
					
					function setMessaggioNonLetto(discid_p, discid, syscon) {
						$.ajax({
					        type: "GET",
					        async: false,
					        beforeSend: function(x) {
							if(x && x.overrideMimeType) {
								x.overrideMimeType("application/json;charset=UTF-8");
						       }
							},
					        url: "${pageContext.request.contextPath}" + "/pg/EseguiOperazioniConversazione.do",
					        data: "discid_p=" + discid_p + "&discid=" + discid + "&syscon=" + syscon + "&operazione=setMessaggioNonLetto"
					    });
					    historyReload();
					}
					
				</gene:javaScript>
				
			</gene:pagina>
		</gene:formPagine>
	</gene:redefineInsert>
</gene:template>