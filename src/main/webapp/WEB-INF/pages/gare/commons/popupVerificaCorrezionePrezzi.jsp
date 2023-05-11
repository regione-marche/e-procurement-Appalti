<%
	/*
   * Created on: 28/09/2009
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
			Maschera per la verifica e correzione prezzi
								
			Creato da:	Marcello Caminiti
	 */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<fmt:setBundle basename="AliceResources" />

<c:set var="key" value="GARE.NGARA=T:${param.gara}" scope="request" />


<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
	<c:set var="sommaPrezziUnitari" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GestioneVerificaCorrezionePrezziFunction", pageContext, param.gara, param.ditta, param.codgar)}'/>
	<gene:setString name="titoloMaschera" value="Verifica e correzione prezzi della ditta aggiudicataria"/>
	
	<gene:redefineInsert name="buttons">
		<c:choose>
			<c:when test='${deltaImporti le "0.01" or coeffDiscordanzaArrotondato eq "1" or coeffDiscordanzaArrotondato eq "1,0" or empty sommaPrezziUnitari }'>
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
			</c:when>
			<c:otherwise>
				<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:correzionePrezzi();">&nbsp;
				<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;
			</c:otherwise>
		</c:choose>
	</gene:redefineInsert>

	<gene:redefineInsert name="corpo">
	<c:set var="modo" value="VISUALIZZA" scope="request" />
		<gene:formScheda entita="GARE" where="GARE.NGARA = '${param.gara}'" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupCorrezionePrezziUnitari">
			<gene:campoScheda>
			<td colSpan="2">&nbsp;I prezzi unitari offerti dalla ditta aggiudicataria vengono corretti in modo costante in base al coefficiente di
			                <br>&nbsp;discordanza tra l'importo ottenuto applicando il ribasso di aggiudicazione all'importo a base di gara e l'importo
			                <br>&nbsp;risultante dal dettaglio dei prezzi unitari.
			                
			<br>&nbsp;
			</td>
			</gene:campoScheda>
			<c:choose>
				<c:when test='${deltaImporti le "0.01" or coeffDiscordanzaArrotondato eq "1" or coeffDiscordanzaArrotondato eq "1.0" or empty sommaPrezziUnitari }'>
					<gene:campoScheda>
						<c:choose>
							<c:when test='${RISULTATO eq "CALCOLOESEGUITO"}'>
								<td colSpan="2"><br><b>&nbsp;I prezzi unitari risultano corretti</b><br>&nbsp;
								</td>
							</c:when>
							<c:when test='${empty sommaPrezziUnitari}'>
								<td colSpan="2"><br><b>&nbsp;Non risultano inseriti i prezzi unitari</b><br>&nbsp;
								</td>
							</c:when>
							<c:otherwise>
								<td colSpan="2"><br><b>&nbsp;I prezzi unitari risultano corretti</b><br>&nbsp;
								</td>
							</c:otherwise>
						</c:choose>
					</gene:campoScheda>
				</c:when>
			</c:choose>
			<gene:campoScheda campo="NGARA" visibile="false"/>
			<gene:campoScheda campo="DITTAO" campoFittizio="true" value="${param.ditta}"  visibile="false" definizione="T10;0"/>
			<gene:campoScheda campo="CODGAR" campoFittizio="true" value="${param.codgar}" visibile="false" definizione="T21;0"/>
			<gene:campoScheda campo="RIBAGG" visibile = "false"/>
			<gene:campoScheda campo="RIBASSO" title="Ribasso di aggiudicazione" modificabile="false" value="${ribasso}" definizione="F13.9;0;;PRC" campoFittizio="true"/>
			<gene:campoScheda campo="IMPORTO_RIBASSO" title="Importo ottenuto applicando il ribasso di aggiudicazione all'importo a base di gara" modificabile="false" value='${importoRibasso}' definizione="F15.5;0;;MONEY5" campoFittizio="true"/>
			<gene:campoScheda campo="IMPORTO_DETTAGLIO" title="Importo risultante dal dettaglio dei prezzi unitari" modificabile="false" value='${importoPrezziUnitari}' definizione="F15.5;0;;MONEY5" campoFittizio="true" />
			<gene:campoScheda campo="COEFF_ROUND" title="Coefficiente di discordanza" modificabile="false" value='${coeffDiscordanzaArrotondato}' definizione="F1.10" campoFittizio="true" />
			<gene:campoScheda campo="COEFFICIENTE" title="Coefficiente di discordanza" visibile="false" value='${coeffDiscordanza}' definizione="F1" campoFittizio="true" />
			<gene:campoScheda campo="ONPRGE" visibile = "false"/>
			<gene:campoScheda campo="IMPSIC" visibile = "false"/>
			<gene:campoScheda campo="SICINC" visibile = "false"/>
			<gene:campoScheda campo="IMPNRL" visibile = "false"/>
		</gene:formScheda>
	</gene:redefineInsert>
	<gene:javaScript>
		function annulla(){
			window.close();
		}
		
		function chiudi(){
			var risultato = "${RISULTATO}";
			if (risultato == "CALCOLOESEGUITO"){
				//historyReload() non va bene poichè viene ricaricata la pagina chiamante passando dal
				//gestore, cosa che non si vuole
				//opener.historyReload();
				var paginalista = opener.document.forms[0].pgCorrente.value = 0;
				opener.listaVaiAPagina(paginalista);
			}
			window.close();
		}
		
		
		function correzionePrezzi(){
			var numeroGara="${param.gara }";
			var codiceDitta="${param.ditta }";
			var codiceGara="${param.codgar }"; 
			
			document.forms[0].modo.value = "MODIFICA";
			document.forms[0].jspPathTo.value="gare/commons/popupVerificaCorrezionePrezzi.jsp?codgar=" + codiceGara + "&gara=" + numeroGara + "&ditta=" + codiceDitta;
			schedaConferma();
		}
	</gene:javaScript>
</gene:template>
</div>