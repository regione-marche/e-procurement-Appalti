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

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENEWEB" idMaschera="W_DISCUSS-scheda" >
	
	<gene:redefineInsert name="head" >
		<style type="text/css">
			input.bottone-azione-messaggio {
				background-color: #C40000;
				color: #FFFFFF;
				font: 12px Verdana, Arial, Helvetica, sans-serif;
				vertical-align: middle;
				font-weight: bold;
			}
		</style>
	</gene:redefineInsert>
	
	<c:set var="discid_p" value='${gene:getValCampo(key,"DISCID_P")}' scope="request"/>
	<c:if test='${discid_p eq ""}'>
		<c:set var="discid_p" value='${gene:getValCampo(keyParent,"DISCID_P")}' scope="request"/>
	</c:if>
	
	<c:set var="discid" value='${gene:getValCampo(key,"DISCID")}' scope="request"/>
	<c:if test='${discid eq ""}'>
		<c:set var="discid" value='${gene:getValCampo(keyParent,"DISCID")}' scope="request"/>
	</c:if>
 
 	<c:set var="getDiscMessOperatore" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetDiscMessOperatoreFunction",pageContext,discid_p,discid)}'/>
   	<c:set var="getDiscMessPubblicato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetDiscMessPubblicatoFunction",pageContext,discid_p,discid)}'/>
   	
	<c:set var="titoloCompleto" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleDiscussioniFunction",pageContext,"W_DISCUSS")}'/>
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
			<gene:pagina title="Messaggio" idProtezioni="messaggio" >
				<gene:formScheda entita="W_DISCUSS" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_DISCUSS" >
					<c:set var="entitaParent" value='${fn:substringBefore(keyParent,".")}' />
					<gene:campoScheda campo="DISCID" visibile="false" />
					<gene:campoScheda campo="DISCID_P" visibile="false" defaultValue='${discid_p}'/>
					<gene:campoScheda title="Messaggio" campo="DISCMESSTESTO" obbligatorio="true"/>
					<gene:campoScheda campo="DISCMESSOPE" visibile="false" defaultValue='${sessionScope.profiloUtente.id}' />
					<gene:campoScheda campo="SYSUTE" entita="USRSYS"  title="Mittente" definizione="T" where="USRSYS.SYSCON = W_DISCUSS.DISCMESSOPE" modificabile="false" defaultValue="${sessionScope.profiloUtente.nome}"/>
					<gene:campoScheda title="Data" campo="DISCMESSINS" modificabile="false" visibile="${modo ne 'NUOVO'}"/>
					<gene:campoScheda title="Pubblicato?" campo="DISCMESSPUBBL" visibile="false" />
		
					<input type="hidden" name="keyAdd" value="${param.keyAdd}" />
					
					<gene:campoScheda>
						<c:if test='${not (gene:checkProtFunz(pageContext, "MOD","SCHEDAMOD") and datiRiga.W_DISCUSS_DISCMESSPUBBL ne "1" and sessionScope.entitaPrincipaleModificabile eq "1" and (sessionScope.profiloUtente.id eq datiRiga.W_DISCUSS_DISCMESSOPE))}' >
							<gene:redefineInsert name="pulsanteModifica" />
							<gene:redefineInsert name="schedaModifica" />
						</c:if>
						<c:if test='${not (gene:checkProtFunz(pageContext, "INS","SCHEDANUOVO") and sessionScope.entitaPrincipaleModificabile eq "1")}' >
							<gene:redefineInsert name="pulsanteNuovo" />
							<gene:redefineInsert name="schedaNuovo" />
						</c:if>
						<c:if test='${not (gene:checkProtFunz(pageContext, "MOD","SCHEDAMOD") and datiRiga.W_DISCUSS_DISCMESSPUBBL ne "1" and sessionScope.entitaPrincipaleModificabile eq "1" and (sessionScope.profiloUtente.id eq datiRiga.W_DISCUSS_DISCMESSOPE))}' >
							<gene:redefineInsert name="pulsantePubblicaMessaggio" />
						</c:if>
						
						<td class="comandi-dettaglio" colSpan="2">
							<gene:insert name="addPulsanti"/>
							<c:choose>
								<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
									<gene:insert name="pulsanteSalva">
										<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
									</gene:insert>
									<gene:insert name="pulsanteAnnulla">
										<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
									</gene:insert>
								</c:when>
								<c:otherwise>
									<gene:insert name="pulsantePubblicaMessaggio" >
										<input type="button" class="bottone-azione-messaggio" value='Pubblica messaggio' title='Pubblica messaggio' onclick="javascript:pubblicaMessaggio(${datiRiga.W_DISCUSS_DISCID_P}, ${datiRiga.W_DISCUSS_DISCID})">
									</gene:insert>
									<gene:insert name="pulsanteModifica">
										<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
											<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
										</c:if>
									</gene:insert>
									<gene:insert name="pulsanteNuovo">
										<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
											<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovo()" id="btnNuovo">
										</c:if>
									</gene:insert>
								</c:otherwise>
							</c:choose>
							&nbsp;
						</td>
					</gene:campoScheda>
					
					<gene:javaScript>
						
						<c:if test='${modo eq "VISUALIZZA"}'>
						
							setMessaggioLetto(${discid_p},${discid},${sessionScope.profiloUtente.id});
						
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
							        data: "discid_p=" + discid_p + "&discid=" + discid + "&syscon=" + syscon + "&operazione=setMessaggioLetto",
							    });
							}
						</c:if>
					
						function pubblicaMessaggio(discid_p, discid) {
							if (confirm("Pubblicare il messaggio ? Dopo la pubblicazione tutti i dati, i destinatari e gli allegati verranno bloccati.")) {
								$.ajax({
							        type: "GET",
							        async: false,
							        beforeSend: function(x) {
									if(x && x.overrideMimeType) {
										x.overrideMimeType("application/json;charset=UTF-8");
								       }
									},
							        url: "${pageContext.request.contextPath}" + "/pg/EseguiOperazioniConversazione.do",
							        data: "discid_p=" + discid_p + "&discid=" + discid + "&operazione=pubblicaMessaggio",
							        complete: function(e){
							        	historyReload();
							        	selezionaPagina(pagina);
							        }
							    });
							  }
						}

					</gene:javaScript>
					
				</gene:formScheda>
			</gene:pagina>
			<gene:pagina title="Destinatari notifica email" idProtezioni="destinatari" >
				<table class="dettaglio-tab-lista">
					<tr>
						<td>
							<gene:formLista entita="W_DISCDEST" where='W_DISCDEST.DISCID_P = #W_DISCUSS.DISCID_P# AND W_DISCDEST.DISCID = #W_DISCUSS.DISCID#' sortColumn="2" tableclass="datilista" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_DISCDEST">
								<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>" width="50" visibile="${pubblicato eq 'false'}">
									<c:if test="${currentRow >= 0 and pubblicato eq 'false'}" >
										<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
											<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL") and pubblicato eq "false" and sessionScope.profiloUtente.id eq operatore}' >
												<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina" />
											</c:if>
										</gene:PopUp>
										<c:if test='${gene:checkProtFunz(pageContext, "DEL","LISTADELSEL") and pubblicato eq "false" and sessionScope.profiloUtente.id eq operatore}' >
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
								<gene:campoLista title="Diagnostica invio notifica" campo="DESTINVMESS" />
							</gene:formLista>
						</td>
					</tr>
					<c:choose>
						<c:when test="${sessionScope.profiloUtente.id eq operatore and pubblicato eq 'false'}">
							<gene:redefineInsert name="listaNuovo" />
							<tr>
								<td class="comandi-dettaglio" colSpan="2">
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
			</gene:pagina>
			<gene:pagina title="Allegati" idProtezioni="allegati" >
				<table class="dettaglio-tab-lista">
					<tr>
						<td>
							<gene:formLista entita="W_DISCALL" where='W_DISCALL.DISCID_P = #W_DISCUSS.DISCID_P# AND W_DISCALL.DISCID = #W_DISCUSS.DISCID#' sortColumn="2" tableclass="datilista" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_DISCALL">
								<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>" width="50" visibile="${pubblicato eq 'false'}">
									<c:if test="${currentRow >= 0 and pubblicato eq 'false'}" >
										<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
											<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza"/>
											<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD") and pubblicato eq "false" and sessionScope.profiloUtente.id eq operatore}' >
												<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" title="Modifica" />
											</c:if>
											<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL") and pubblicato eq "false" and sessionScope.profiloUtente.id eq operatore}' >
												<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina" />
											</c:if>
										</gene:PopUp>
										<c:if test='${gene:checkProtFunz(pageContext, "DEL","LISTADELSEL") and pubblicato eq "false" and sessionScope.profiloUtente.id eq operatore}' >
											<input type="checkbox" name="keys" value="${chiaveRiga}"  />
										</c:if>
									</c:if>
								</gene:campoLista>
								<gene:campoLista campo="DISCID_P" visibile="false" />
								<gene:campoLista campo="DISCID" visibile="false" />
								<gene:campoLista campo="ALLNUM" visibile="false" />
								<gene:campoLista title="Descrizione" campo="ALLNOTE" ordinabile="true" />
								<gene:campoLista title="Documento" campo="ALLNAME" visibile="true" />
								<gene:campoLista title="&nbsp;" width="24">
									<c:set var="nomDoc" value="${gene:string4Js(datiRiga.W_DISCALL_ALLNAME)}"/>
									<c:set var="nomDoc" value="${fn:replace(nomDoc,'\"','&#34;')}"/>
									<a style="align: center;" href="javascript:visualizzaFileAllegato('${datiRiga.W_DISCALL_DISCID_P}','${datiRiga.W_DISCALL_DISCID}','${datiRiga.W_DISCALL_ALLNUM}',${nomDoc});" title="Visualizza allegato" >
										<img width="16" height="16" title="Visualizza allegato" alt="Visualizza allegato" src="${pageContext.request.contextPath}/img/allegato.gif"/>
									</a>
								</gene:campoLista>

							</gene:formLista>
						</td>
					</tr>
					
					<c:choose>
						<c:when test="${sessionScope.profiloUtente.id eq operatore and pubblicato eq 'false'}">
							<gene:redefineInsert name="listaNuovo">
								<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:listaNuovo();" title="Aggiungi allegato" tabindex="1501">
												Aggiungi allegato</a></td>
									</tr>
								</c:if>
							</gene:redefineInsert>
						
							<tr>
								<td class="comandi-dettaglio" colSpan="2">
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
		
					function visualizzaFileAllegato(discid_p,discid,allnum,allname) {
						var href = "${pageContext.request.contextPath}/pg/VisualizzaDocumentoWDISCALL.do";
						document.location.href=href + "?" + csrfToken + "&discid_p=" + discid_p + "&discid=" + discid + "&allnum=" + allnum + "&allname=" + allname;
					}	
				
				</gene:javaScript>
								
			</gene:pagina>
		</gene:formPagine>
	</gene:redefineInsert>
</gene:template>