<%
/*
 * Created on: 30/06/2015
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>



<c:set var="idalbo" value='${gene:getValCampo(keyParent,"ID")}' scope="request" />
<c:if test="${modo ne 'NUOVO'}">
	<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneRuoliFunction" parametro='${gene:getValCampo(key, "ID")}' />
</c:if>
<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="COMMNOMIN-scheda">

	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction",pageContext,"COMMNOMIN")}'/>
	
	<c:choose>
		<c:when test="${modo eq 'VISUALIZZA'}">
			<c:set var="defaultSezioneListaVuota" value="false"/>
		</c:when>
		<c:otherwise>
			<c:set var="defaultSezioneListaVuota" value="true"/>
		</c:otherwise>
	</c:choose>
		
<gene:redefineInsert name="head" >
	<style type="text/css">
		
		TABLE.grigliaqe {
			margin-left: 0px;
			margin-right: 0px;
			margin-top: 2px;
			margin-bottom: 2px;
			padding: 0px;
			width: 100%;
			font-size: 11px;
			border-collapse: collapse;
		}
		
		TABLE.grigliaqe TR {
			background-color: #FFFFFF;
		}
		
		TABLE.grigliaqe TR.intestazione, TABLE.grigliaqe TR.riepilogo {
			background-color: #EFEFEF;
		}
		
		TABLE.grigliaqe TR.intestazione TH { 
			padding: 4 2 4 2;
			text-align: center;
			border: 1px solid #A0AABA;	
			height: 20px;
			font-weight: normal;
		}
				
		TABLE.grigliaqe TR TD {
			padding-left: 2px;
			padding-right: 2px;
			padding-top: 2px;
			padding-bottom: 2px;
			height: 25px;
			text-align: left;
			border: 1px solid #A0AABA;
		}
	
		TABLE.grigliaqe TR.intestazione TH.voce, TABLE.grigliaqe TR TD.voce {
			width: 180px;
			padding-left:10px;
		}
		
		TABLE.grigliaqe TR.intestazione TH.inviti, TABLE.grigliaqe TR TD.inviti {
			width: 180px;
			padding-left:10px;
		}		
	</style>
</gene:redefineInsert>
	
	<gene:redefineInsert name="corpo">
	
		<gene:formScheda entita="COMMNOMIN" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInitCommnomin" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreCOMMNOMIN" >

			<c:choose>
				<c:when test="${modo ne 'VISUALIZZA' || !empty datiCOMMRUOLI}">
					<c:set var="vis" value="true"/>
				</c:when>
				<c:otherwise>
					<c:set var="vis" value="false"/>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${empty datiCOMMRUOLI}">
					<c:set var="defaultSezioneListaVuota" value="true"/>
				</c:when>
				<c:otherwise>
					<c:set var="defaultSezioneListaVuota" value="false"/>
				</c:otherwise>
			</c:choose>	
			<c:choose>
				<c:when test="${modo eq 'VISUALIZZA' && empty datiCOMMRUOLI}">
					<c:set var="presenze" value=""/>
				</c:when>
				<c:otherwise>
					<c:set var="presenze" value="0"/>
				</c:otherwise>
			</c:choose>		

			<gene:gruppoCampi idProtezioni="COMMNOMIN">
				<gene:campoScheda>
					<td colspan="2"><b>Dati generali</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="IDALBO" value="${idalbo}" visibile="false"/>
				<gene:campoScheda campo="NUMORD" visibile="false"/>
				<gene:campoScheda campo="ID" visibile="false"/>
				<gene:archivio titolo="Tecnici"	lista="gene/tecni/tecni-lista-popup.jsp" scheda="gene/tecni/tecni-scheda.jsp" 
				schedaPopUp="gene/tecni/tecni-scheda-popup.jsp" campi="TECNI.CODTEC;TECNI.NOMTEC" chiave="COMMNOMIN_CODTEC" 
				inseribile="true" where="NOT EXISTS (SELECT * FROM COMMNOMIN WHERE COMMNOMIN.CODTEC = TECNI.CODTEC AND IDALBO = '${idalbo}')">
					<gene:campoScheda campo="CODTEC" title="Codice tecnico" obbligatorio="true" />
					<gene:campoScheda campo="NOMTEC" campoFittizio="true" definizione="T161;;;;NOMTEC1" title="Nome" value="${requestScope.nomeTecnico}" />
				</gene:archivio>

				<gene:archivio titolo="Ufficio intestatario"
					lista="gene/uffint/uffint-lista-popup.jsp"
					scheda="gene/uffint/uffint-scheda.jsp"
					schedaPopUp="gene/uffint/uffint-scheda-popup.jsp"
					campi="UFFINT.CODEIN;UFFINT.NOMEIN" chiave="COMMNOMIN_CODEIN"
					inseribile="true">
					<gene:campoScheda campo="CODEIN" obbligatorio="true"/>
					<gene:campoScheda campo="NOMEIN" entita="UFFINT" where="UFFINT.CODEIN = COMMNOMIN.CODEIN"  value="${nomeStruttura}" />
				</gene:archivio>	
				<gene:campoScheda campo="LIVELLO" />
				<gene:campoScheda campo="DATAAB" />
			</gene:gruppoCampi>
	
			<gene:gruppoCampi idProtezioni="COMMNOMIN">
				<gene:campoScheda>
					<td colspan="2"><b>Ruoli</b></td>
				</gene:campoScheda>
				<!-- DETTAGLIO -->
				<gene:campoScheda addTr="false">
					<tr>
						<td colspan="2" style="border-bottom: 0px;">
							<table class="grigliaqe">
							<tr class="intestazione">
								<th colspan="2" class="ruolo">Ruolo</th>
								<th colspan="2" class="num_inviti">Numero presenze in commissione</th>									
							</tr>
				</gene:campoScheda>				
				
				<jsp:include page="/WEB-INF/pages/commons/interno-sezione-multipla-singola-riga.jsp" >
					<jsp:param name="entita" value='COMMRUOLI'/>								
					<jsp:param name="chiave" value='${modo eq "NUOVO" ? "" : gene:getValCampo(key, "ID")}'/>
					<jsp:param name="idalbo" value='${idalbo}'/>
					<jsp:param name="presenze" value='${presenze}'/>
					<jsp:param name="nomeAttributoLista" value='datiCOMMRUOLI' />
					<jsp:param name="idProtezioni" value="COMMR" />
					<jsp:param name="sezioneListaVuota" value="${defaultSezioneListaVuota}" />
					<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/commnomin/commnomin-interno-scheda.jsp"/>
					<jsp:param name="arrayCampi" value="'COMMRUOLI_ID_','COMMRUOLI_IDALBO_','COMMRUOLI_IDNOMIN_','COMMRUOLI_RUOLO_','COMMRUOLI_INVITI_'"/>
					<jsp:param value="arrayVisibilitaCampi" name="false,false,false,true,true"/>
					<jsp:param name="titoloSezione" value="<br>Voce di spesa n. " />
					<jsp:param name="titoloNuovaSezione" value="<br>Nuova voce di spesa" />
					<jsp:param name="descEntitaVociLink" value="ruolo" />
					<jsp:param name="msgRaggiuntoMax" value=" ruoli"/>
					<jsp:param name="usaContatoreLista" value="true"/>
					<jsp:param name="numMaxDettagliInseribili" value="5"/>
					<jsp:param name="sezioneInseribile" value="true"/>
					<jsp:param name="sezioneEliminabile" value="true"/>
					<jsp:param name="funzEliminazione" value="eliminaRUOLO"/>
				</jsp:include>	
				
				<gene:campoScheda addTr="false">	
						</table>
					</td>
				</tr>
				</gene:campoScheda>					
			</gene:gruppoCampi>
			
			<gene:campoScheda>
					<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
			<gene:redefineInsert name="pulsanteSalva">
				<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConfermaNominativo();">
			</gene:redefineInsert>
			
		<gene:javaScript>
			$(".contenitore-dettaglio").width("700"); 
		
			$('table.grigliaqe tr td.valore-dato').removeClass('valore-dato');	
			$('[id^="COMMRUOLI_RUOLO_"]').parent().addClass("voce");
			$('[id^="COMMRUOLI_INVITI_"]').parent().addClass("inviti");
			
			$('[id^="COMMRUOLI_RUOLO_"]').width("90%");
			$('[id^="COMMRUOLI_INVITI_"]').width("90%");
			
			function eliminaRUOLO(id, label, tipo, campi) {			
				delElementoSchedaMultipla(id, label, tipo, campi);	
				$('#'+"row" + tipo +"_" + id).hide();			
			};
			<c:if test='${modoAperturaScheda ne "VISUALIZZA"}'>
				$('#rowLinkAddCOMMRUOLI td').attr("colspan","4");
			</c:if>
			$('span[id^="rowtitoloCOMMR_"]').css('float','right');
			$('span[id^="rowtitoloCOMMR_"]').css('padding-right','10px');
	
			$('#rowLinkAddCOMMR').find( "td" ).css("border","none");
			
			// funzione che ridefinisce il salvataggio
			function schedaConfermaNominativo(){
				clearMsg();
				var continua = true;  				
				var isEmpty = false;
				var canContinue = false;
				$('tr[id^="rowtitoloCOMMR_"]').each(function() {					
					if($(this).is(":visible")){
						var valEl = $(this).find('select[id^="COMMRUOLI_RUOLO_"]').val();
						//alert(valEl);	
						if(valEl != "" && valEl != null){
							canContinue = true;
						}else{
							isEmpty = true;
							return false;
						}						
					}					
				});					
				if(isEmpty){
					continua = false;
					outMsg("Attenzione, completare tutti i ruoli del nominativo", "ERR");
					onOffMsg();
				}else if(!canContinue){
					continua = false;
					outMsg("Attenzione, selezionare almeno un ruolo per il nominativo", "ERR");
					onOffMsg();
				}
											
				if(continua){
				  schedaConferma();
				}
			}
		</gene:javaScript>
		</gene:formScheda>
	</gene:redefineInsert>
</gene:template>