
<%
  /*
			 * Created on 12-apr-2013
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<gene:template file="scheda-template.jsp">
	<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

	<gene:redefineInsert name="head" >
		<style type="text/css">
	 		TABLE.grigliamultipla {
		 		margin-left: 5px;
				margin-right: 0px;
				margin-top: 10px;
				margin-bottom: 15px;
				padding: 0px;
				width: 99%;
				font-size: 11px;
				border-collapse: collapse;
				border-left: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
			}
			
			TABLE.grigliamultipla THEAD TR TH {
				margin: 0px;
				padding: 2 5 2 5;
				text-align: center;
				border: #A0AABA 1px solid;
			}
			
			TABLE.grigliamultipla THEAD TR {
				background-color: #D8D8D8;
				height: 30px;
			}
			
			TABLE.grigliamultipla TBODY TR {
				margin-bottom: 0px;
				height: 30px;
			}
			
	
			TABLE.grigliamultipla THEAD TR TH.fase, TABLE.grigliamultipla TBODY TR TD.fase{
				text-align: center;
				vertical-align: middle;
				width: 230px;
			}
			
			TABLE.grigliamultipla THEAD TR TH.descrizione {
				width: 180px;
			}
			
			TABLE.grigliamultipla TBODY TR TD.descrizione {
				text-align: left;
				vertical-align: middle;
				width: 180px;
			}
			
			TABLE.grigliamultipla THEAD TR TH.data, TABLE.grigliamultipla TBODY TR TD.data{
				text-align: center;
				vertical-align: middle;
				width: 160px;
			}
			
			TABLE.grigliamultipla THEAD TR TH.esito, TABLE.grigliamultipla TBODY TR TD.esito{
				text-align: center;
				vertical-align: middle;
				width: 90px;
			}
			
			TABLE.grigliamultipla TBODY TR TD.esito[data-esito="0"]{
				background-color: #FFA3A3;
			}
			
			TABLE.grigliamultipla TBODY TR TD.esito[data-esito="1"]{
				background-color: #68FF7A;
			}
			
			TABLE.grigliamultipla TBODY TR TD.esito[data-esito="2"]{
				background-color: #FFDB59;
			}
			
			TABLE.grigliamultipla THEAD TR TH.messaggio{
				text-align: center;
				vertical-align: middle;
			}
			
			TABLE.grigliamultipla TBODY TR TD.messaggio{
				text-align: left;
				vertical-align: middle;
			}
			
			TABLE.grigliamultipla THEAD TR TH.invia, TABLE.grigliamultipla TBODY TR TD.invia{
				text-align: center;
				vertical-align: middle;
				width: 80px;
			}
			
			TABLE.grigliamultipla TBODY TR TD {
				padding: 2 2 2 2;
				text-align: center;
				border: #A0AABA 1px solid;
				vertical-align: bottom;
				margin-bottom: 3px;
			}
			
			input {
				padding-top:0px;
			}
			
			input.opzione {
				padding-top: 4px;
				padding-bottom: 4px;
				vertical-align: bottom;
			}
			
			span.titolo {
				font-weight: bold;
			}
		
		</style>
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value='Invio dei dati a ${param.nomeApplicativo}' />
	<c:set var="temp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.InizializzaPopupInviaVigilanzaFunction", pageContext, param.codgar, param.genereGara)}'/>
	<c:set var="temp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetStatoInviiVigilanzaFunction", pageContext, param.codgar)}'/>
	<c:set var="logincomune" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pl.vigilanza.ws.login.comune")}'/>
	<c:set var="ssoprotocollo" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "sso.protocollo")}'/>
	
	<gene:redefineInsert name="corpo">
	
		<form action="${contextPath}/pg/InviaVigilanza.do" method="post" name="formInviaVigilanza" >
			<input type="hidden" name="codgar" value="${param.codgar}" />
			<input type="hidden" name="nomeApplicativo" value="${param.nomeApplicativo}" />
			<input type="hidden" name="genereGara" value="${param.genereGara}" />
			<input type="hidden" name="uffint" value="${sessionScope.uffint}" />
			
			<table class="dettaglio-notab">
				<tr>
					<td colspan="2">
						<br>
						Questa funzione prepara ed invia a ${nomeApplicativoVigilanza} i dati del contratto selezionato per il successivo controllo ed invio al
						<b>SIMOG dell'Autorit&agrave; Nazionale Anticorruzione</b>
						<br>
					</td>
				</tr>
				
				<tr>
					<td colspan="2">
						<br>
						<b>Credenziali per la connessione al servizio</b>
					</td>
				</tr>

				<tr>
					<td colspan="2">
						<c:choose>
							<c:when test="${logincomune eq '1'}">
								<input class="opzione" id="logincomune" type="radio" name="credenziali" value="COMUNI" checked="checked" onchange="javascipt:gestioneCredenziali()">Credenziali comuni a tutti gli utenti (configurate tra i parametri di amministrazione)							
							</c:when>
							<c:otherwise>
								<input class="opzione" id="logincomune" type="radio" name="credenziali" value="COMUNI" disabled="disabled" onchange="javascipt:gestioneCredenziali()">Credenziali comuni a tutti gli utenti (configurate tra i parametri di amministrazione)	
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<c:choose>
							<c:when test="${logincomune eq '1'}">
								<input class="opzione" id="correnti" type="radio" name="credenziali" value="CORRENTI" onchange="javascipt:gestioneCredenziali()">Credenziali dell'utente applicativo connesso							
							</c:when>
							<c:otherwise>
								<input class="opzione" id="correnti" type="radio" name="credenziali" value="CORRENTI" checked="checked" onchange="javascipt:gestioneCredenziali()">Credenziali dell'utente applicativo connesso
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				
				<c:if test="${empty ssoprotocollo || ssoprotocollo eq '0'}">
					<tr>
						<td colspan="2">
							<input class="opzione" id="altre" type="radio" name="credenziali" value="ALTRE" onchange="javascipt:gestioneCredenziali()">Altre credenziali
						</td>
					</tr>
	
					<tr>	
						<td class="etichetta-dato">Utente</td>
						<td class="valore-dato">
							<input type="text" name="username" size="15" disabled/>
						</td>
					</tr>
					<tr>
						<td class="etichetta-dato">Password</td>
						<td class="valore-dato">
							<input type="password" name="password" size="15" disabled/>
						</td>
					</tr>
				</c:if>
				
				<c:choose>
					<c:when test="${isadesione eq '1' && corrispondenzaAnac eq 'true' }">
						<c:set var="labelMetodo3" value="Adesione accordo quadro/convenzione"/>
					</c:when>
					<c:otherwise>
						<c:set var="labelMetodo3" value="Aggiudicazione"/>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${accqua eq '1'|| altrisog eq '2' || altrisog eq '3'}">
						<c:set var="labelMetodo4" value="Stipula accordo quadro / convenzione"/>
					</c:when>
					<c:otherwise>
						<c:set var="labelMetodo4" value="Inizio contratto"/>
					</c:otherwise>
				</c:choose>
				
				<tr>
					<td colspan="2">
						<br>
						<span class="titolo">Fasi</span>
					</td>
				</tr>
				<input id="metodo" type="hidden" name="metodo" value=""/> 
				<tr>
					<td colspan="2">
						<table class="grigliamultipla">
							<thead>
								<tr>
									<th colspan="2" class="fase">Fase</th>
									<th rowspan="2" class="data">Data ultimo invio (o tentativo di invio)</th>
									<th rowspan="2" class="esito">Esito</th>
									<th rowspan="2" class="messaggio">Messaggio<br><br><div style="font-style: italic; font-weight: normal; text-align: left; margin-right: 100px;">* I messaggi restituiti dal servizio contattato per l'invio sono evidenzati con il simbolo <img height="18" width="18" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAUwwAAFMMBFXBNQgAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAALmSURBVEiJ7ZXNixxVFMV/9/WMDaKDQRhMvfeqSCa9EBlmwpDFEN2YbNwYNy7MxoWgTHaShQpZ+IHgPyDBBCVBZMBNFq5E3IQwEYlgyCAI04vuqh5lRgiS0f6Y6XddVFdXBWILLXHlhVrUve+ec8/h1S1RVR5mmIeK/p8TNBqN+r8Be1C/qRZ7ve7NJHEvTwPuvX2t3+9+KyK1BxIMBr0PRDgeguxNQyDCH8BJ7+1b9xVUFe8PP+e9HXpvL6oq0z7e23Xv7SBJouNFzuS+1a4CzeFQzxfESeLfjWO3NmnqJPFvx7F7s1RROwfshMDnImIAzP5+9zToEREudDqdP/NG+6yqXgCWJhGo6jLwkXNuGaDVat0VkfdBnonjaBXAqOoZgOGQjbJRPgP5WVUq04lxzj1ZJZidrb8BbBvDpUrvCMecGRHICvBLlmUZQBzHEdCA8HGapt1ckTvlvdsyhl/j2H4hIgLQbDZ/By4DK/Pz848BpGn6E7AXgq5AfossyPflXMMTAMbwXTkV74EeAWZAzsZxdKqojc6Zer2+MrItAD8Ygy0IDoE2SwJZyOX3tipuPF61JgQzft/f163cwrBQwWiGwKGCYAdYLL3WOwD9fn2pzPEhsDdSs5Fl2VdFbWamuAi1OyWBLoqwUxBsAycKX2HmVq5UVovjrVb2Zbfbj0JgOU2zk6p6UNonq8Bgbm7uNozXxRLIdkFwA3jCWtvIwVp3gQ2Q88eOPTVfAO3u7t7Lsux21aqjR6MEOKfKN5ubmwOAXq+3BDwCegPAiOg1AGN4vmw1r4rw6GAw+wkT4uDAXAEORMzr484xjrk2znlvb3pvf0uS5HDxiTvnzsZxdHryaohe8j56sXz3C97be967r4ucjAoNEf0R9Hq73Xlh0tR/FyJSc85eF+FpVVlM07QDUF1Ua95b9d6uTbPo4ti+k/dHr1Tz43XdbmcXQdbB3JpGQQiyAfJpu91Zv0/Z/z/9f4q/AOp2lHNTUYPgAAAAAElFTkSuQmCC"></img></div></th>
									<th rowspan="2" class="invia">Invia dati</th>
								</tr>
							</thead>
							
							<c:forEach items="${faseAnagrafica}" step="1" var="riga" varStatus="statusRiga" >
								<tr>
									<c:if test="${statusRiga.first}"><td colspan="2" class="fase" rowspan="${fn:length(faseAnagrafica)}">Anagrafica gara/lotti</td></c:if>
									<td class="data" data-numero-relativo="${statusRiga.index}">${riga[0]}</td>
									<td class="esito" data-numero-relativo="${statusRiga.index}" data-esito="${riga[1]}">${riga[2]}</td>
									<td class="messaggio" data-numero-relativo="${statusRiga.index}" >${riga[3]}</td>
									<c:if test="${statusRiga.first}">
									<td class="invia" rowspan="${fn:length(faseAnagrafica)}">
										<a class="link-generico" href="javascript:inviaDati('metodo1');"><img alt="Invia dati" src="${pageContext.request.contextPath}/img/Communication-47.png" width="20" height="20"></a>
									</td>
									</c:if>
								</tr>
							</c:forEach>
							
							<c:forEach items="${faseAggiudicazione}" step="1" var="riga" varStatus="statusRiga" >
								<tr>
									<c:if test="${statusRiga.first}"><td colspan="2" class="fase" rowspan="${fn:length(faseAggiudicazione)}">${labelMetodo3 }</td></c:if>
									<td class="data" data-numero-relativo="${statusRiga.index}">${riga[0]}</td>
									<td class="esito"  data-numero-relativo="${statusRiga.index}" data-esito="${riga[1]}">${riga[2]}</td>
									<td class="messaggio" data-numero-relativo="${statusRiga.index}" >${riga[3]}</td>
									<c:if test="${statusRiga.first}">
										<td class="invia" rowspan="${fn:length(faseAggiudicazione)}">
											<a class="link-generico" href="javascript:inviaDati('metodo2');"><img alt="Invia dati" src="${pageContext.request.contextPath}/img/Communication-47.png" width="20" height="20"></a>
										</td>
									</c:if>
							</tr>
							</c:forEach>
							
							<c:if test="${!(isadesione eq '1' && corrispondenzaAnac eq 'true')}">
								<c:forEach items="${faseEsito}" step="1" var="riga" varStatus="statusRiga" >
									<tr>
										<c:if test="${statusRiga.first}"><td colspan="2" class="fase" rowspan="${fn:length(faseEsito)}">Annullamento/non aggiudicazione</td></c:if>
										<td class="data">${riga[0]}</td>
										<td class="esito" data-esito="${riga[1]}">${riga[2]}</td>
										<td class="messaggio">${riga[3]}</td>
										<c:if test="${statusRiga.first}">
											<td class="invia" rowspan="${fn:length(faseEsito)}">
												<a class="link-generico" href="javascript:inviaDati('metodo3');"><img alt="Invia dati" src="${pageContext.request.contextPath}/img/Communication-47.png" width="20" height="20"></a>
											</td>
										</c:if>
									</tr>
								</c:forEach>
							</c:if>
							
							<c:if test="${faseInizioContrattoDisponibile ne 'false' }">
								<c:forEach items="${faseContratto}" step="1" var="riga" varStatus="statusRiga" >
									<tr>
										<c:if test="${statusRiga.first}"><td colspan="2" class="fase" rowspan="${fn:length(faseContratto)}">${labelMetodo4}</td></c:if>
										<td class="data" data-numero-relativo="${statusRiga.index}">${riga[0]}</td>
										<td class="esito"  data-numero-relativo="${statusRiga.index}" data-esito="${riga[1]}">${riga[2]}</td>
										<td class="messaggio" data-numero-relativo="${statusRiga.index}" >${riga[3]}</td>
										<c:if test="${statusRiga.first}">
											<td class="invia" rowspan="${fn:length(faseContratto)}">
												<a class="link-generico" href="javascript:inviaDati('metodo4');"><img alt="Invia dati" src="${pageContext.request.contextPath}/img/Communication-47.png" width="20" height="20"></a>
											</td>
										</c:if>
									</tr>
								</c:forEach>
							</c:if>
						</table>
					</td>
				</tr>

				
			</table>
		</form>	
	</gene:redefineInsert>

	<gene:javaScript>
		
		$('td.messaggio:contains("[WS]")').each(function () {
		    var t = $(this).text();
		    t = t.replace("[WS]","");
		    $(this).text(t);
			var _img = $("<img>",{"src": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAUwwAAFMMBFXBNQgAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAALmSURBVEiJ7ZXNixxVFMV/9/WMDaKDQRhMvfeqSCa9EBlmwpDFEN2YbNwYNy7MxoWgTHaShQpZ+IHgPyDBBCVBZMBNFq5E3IQwEYlgyCAI04vuqh5lRgiS0f6Y6XddVFdXBWILLXHlhVrUve+ec8/h1S1RVR5mmIeK/p8TNBqN+r8Be1C/qRZ7ve7NJHEvTwPuvX2t3+9+KyK1BxIMBr0PRDgeguxNQyDCH8BJ7+1b9xVUFe8PP+e9HXpvL6oq0z7e23Xv7SBJouNFzuS+1a4CzeFQzxfESeLfjWO3NmnqJPFvx7F7s1RROwfshMDnImIAzP5+9zToEREudDqdP/NG+6yqXgCWJhGo6jLwkXNuGaDVat0VkfdBnonjaBXAqOoZgOGQjbJRPgP5WVUq04lxzj1ZJZidrb8BbBvDpUrvCMecGRHICvBLlmUZQBzHEdCA8HGapt1ckTvlvdsyhl/j2H4hIgLQbDZ/By4DK/Pz848BpGn6E7AXgq5AfossyPflXMMTAMbwXTkV74EeAWZAzsZxdKqojc6Zer2+MrItAD8Ygy0IDoE2SwJZyOX3tipuPF61JgQzft/f163cwrBQwWiGwKGCYAdYLL3WOwD9fn2pzPEhsDdSs5Fl2VdFbWamuAi1OyWBLoqwUxBsAycKX2HmVq5UVovjrVb2Zbfbj0JgOU2zk6p6UNonq8Bgbm7uNozXxRLIdkFwA3jCWtvIwVp3gQ2Q88eOPTVfAO3u7t7Lsux21aqjR6MEOKfKN5ubmwOAXq+3BDwCegPAiOg1AGN4vmw1r4rw6GAw+wkT4uDAXAEORMzr484xjrk2znlvb3pvf0uS5HDxiTvnzsZxdHryaohe8j56sXz3C97be967r4ucjAoNEf0R9Hq73Xlh0tR/FyJSc85eF+FpVVlM07QDUF1Ua95b9d6uTbPo4ti+k/dHr1Tz43XdbmcXQdbB3JpGQQiyAfJpu91Zv0/Z/z/9f4q/AOp2lHNTUYPgAAAAAElFTkSuQmCC"});
		    _img.css("height","18");
		    _img.css("width","18");
		    $(this).prepend(_img);
		});
		
		function inviaDati(metodo) {
		
			var invia = "true";
			
			<c:if test="${empty ssoprotocollo || ssoprotocollo eq '0'}">
			var altre = document.formInviaVigilanza.altre;
			if (altre.checked) {
				var username = document.formInviaVigilanza.username;
				var password = document.formInviaVigilanza.password;
				
				if (username.value == "") {
					alert("Inserire l'utente");
					invia = "false";
				}
				
				if (password.value == "") {
					alert("Inserire la password");
					invia = "false";
				}
			}			
			</c:if>
			$("#metodo").val(metodo);	
			if (invia == "true") {
				document.formInviaVigilanza.submit();
				bloccaRichiesteServer();
			}
		}
						
		function gestioneCredenziali() {
			var altre = document.formInviaVigilanza.altre;
			
			if (altre.checked) {
				document.formInviaVigilanza.username.disabled = false;
				document.formInviaVigilanza.password.disabled = false;					
			} else {
				document.formInviaVigilanza.username.disabled = true;
				document.formInviaVigilanza.password.disabled = true;
				document.formInviaVigilanza.username.value = "";
				document.formInviaVigilanza.password.value = "";
			}			
		}
		
	</gene:javaScript>
	
	<gene:redefineInsert name="noteAvvisi"/>
	<gene:redefineInsert name="documentiAssociati"/>
	
</gene:template>
