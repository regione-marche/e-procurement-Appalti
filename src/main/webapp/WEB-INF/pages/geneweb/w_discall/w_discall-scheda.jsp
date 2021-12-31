<%
/*
 * Created on: 30/03/2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra che visualizza la conferma per l'inserimento delle pubblicazioni bando/esito predefinite
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="W_DISCALL-scheda" schema="GENEWEB">
	
	<c:set var="discid_p" value='${gene:getValCampo(key,"DISCID_P")}' scope="request"/>
	<c:if test='${discid_p eq ""}'>
		<c:set var="discid_p" value='${gene:getValCampo(keyParent,"DISCID_P")}' scope="request"/>
	</c:if>
	
	<c:set var="discid" value='${gene:getValCampo(key,"DISCID")}' scope="request"/>
	<c:if test='${discid eq ""}'>
		<c:set var="discid" value='${gene:getValCampo(keyParent,"DISCID")}' scope="request"/>
	</c:if>
	
	<c:set var="allnum" value='${gene:getValCampo(key,"ALLNUM")}' scope="request"/>
	<c:if test='${allnum eq ""}'>
		<c:set var="allnum" value='${gene:getValCampo(keyParent,"ALLNUM")}' scope="request"/>
	</c:if>
	
	<c:set var="getDiscMessOperatore" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetDiscMessOperatoreFunction",pageContext,discid_p,discid)}'/>
	
	<c:set var="titoloCompleto" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleDiscussioniFunction",pageContext,"W_DISCALL")}'/>
	<c:choose>
		<c:when test='${fn:length(titoloCompleto) > 120}'>
			<gene:setString name="titoloMaschera" value='${fn:substring(titoloCompleto,0,120)}...'/>
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value='${titoloCompleto}' /> 
		</c:otherwise>
	</c:choose>
	
	<gene:redefineInsert name="corpo">
	
		<gene:formScheda entita="W_DISCALL" gestisciProtezioni="true"
			gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_DISCALL" 
			plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreW_DISCALL" >
			
			<gene:campoScheda entita="W_DISCUSS_P" campo="DISCMESSOPE" visibile="false" where="W_DISCUSS_P.DISCID_P = W_DISCALL.DISCID_P" />
			<gene:campoScheda campo="DISCID_P" visibile="false" defaultValue='${discid_p}'/>
			<gene:campoScheda campo="DISCID" visibile="false" defaultValue='${discid}'/>
			<gene:campoScheda title="N°" campo="ALLNUM" visibile="false" modificabile="false"/>
			<gene:campoScheda title="Descrizione" campo="ALLNOTE" visibile="true" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote"/>
			<gene:campoScheda campo="ALLNAME" modificabile="false"/>

			<c:choose>
				<c:when test='${modo eq "VISUALIZZA" }'>
					<gene:campoScheda title="Visualizza allegato" >
						<c:if test="${modo eq 'VISUALIZZA' and !empty datiRiga.W_DISCALL_DISCID_P and !empty datiRiga.W_DISCALL_DISCID and !empty datiRiga.W_DISCALL_ALLNUM}">
							<c:set var="nomDoc" value="${gene:string4Js(datiRiga.W_DISCALL_ALLNAME)}"/>
							<c:set var="nomDoc" value="${fn:replace(nomDoc,'\"','&#34;')}"/>
							<a href="javascript:visualizzaFileAllegato('${datiRiga.W_DISCALL_DISCID_P}','${datiRiga.W_DISCALL_DISCID}','${datiRiga.W_DISCALL_ALLNUM}',${nomDoc});" title="Visualizza allegato" >
								<img width="16" height="16" title="Visualizza allegato" alt="Visualizza allegato" src="${pageContext.request.contextPath}/img/allegato.gif"/>
							</a>
						</c:if>
					</gene:campoScheda>
				</c:when>
				<c:when test='${modo eq "MODIFICA"}'>
					<gene:campoScheda title="Visualizza allegato" >
						<img width="24" height="24" title="" alt="" src="${pageContext.request.contextPath}/img/visualizzafilegrigio.gif"/>
					</gene:campoScheda>
				</c:when>
				<c:when test='${modo eq "NUOVO"}'>
					<gene:campoScheda title="Nome file (*)" >
						<input type="file" name="selezioneFile" id="selezioneFile" onchange="javascript:scegliFile(this.value);" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" />
					</gene:campoScheda>
					<gene:campoScheda title="File da allegare" campo="FILEDAALLEGARE" campoFittizio="true" visibile="false" definizione="T70;0" />			
				</c:when>
			</c:choose>

			<gene:campoScheda>
				<c:choose>
					<c:when test="${sessionScope.profiloUtente.id eq operatore}">
						
					</c:when>
					<c:otherwise>
						<gene:redefineInsert name="schedaNuovo" />
						<gene:redefineInsert name="schedaModifica" />
					</c:otherwise>
				</c:choose>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>						
			
		</gene:formScheda>

  	</gene:redefineInsert>

	<gene:javaScript>
	
		document.forms[0].encoding="multipart/form-data";
	
		function scegliFile(valore) {
			selezioneFile = document.getElementById("selezioneFile").value;
			setValue("FILEDAALLEGARE",selezioneFile);
			
			lunghezza_stringa=selezioneFile.length;
			posizione_barra=selezioneFile.lastIndexOf("\\");
			nome=selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
			if(nome.length>100){
				alert("Il nome del file non può superare i 100 caratteri!");
				document.getElementById("selezioneFile").value="";
				setValue("W_DISCALL_ALLNAME","");
			}else{
				setValue("W_DISCALL_ALLNAME",nome);
			}
		}
		
		function visualizzaFileAllegato(discid_p,discid,allnum,allname) {
			var href = "${pageContext.request.contextPath}/pg/VisualizzaDocumentoWDISCALL.do";
			document.location.href=href + "?" + csrfToken + "&discid_p=" + discid_p + "&discid=" + discid + "&allnum=" + allnum + "&allname=" + allname;
		}	
	
		var schedaConfermaOld=schedaConferma;
		schedaConferma=function(){
			if (${modo eq "NUOVO"}){
				if (document.forms[0].selezioneFile.value == "") {
					alert("Deve essere indicato il file da allegare.");				
				} else {
					schedaConfermaOld();
				}
			} else {
				schedaConfermaOld();
			}
		}

	</gene:javaScript>

</gene:template>




