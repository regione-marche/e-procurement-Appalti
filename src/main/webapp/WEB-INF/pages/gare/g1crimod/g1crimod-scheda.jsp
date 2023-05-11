<%/*
       * Created on 04-Giu-2010
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


<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="G1CRIMOD-scheda" schema="GARE">
	<c:set var="entita" value="G1CRIMOD" />
	<gene:setString name="titoloMaschera" value="Dettaglio modello di criteri di valutazione per OEPV" />
		
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="G1CRIMOD" gestisciProtezioni="true"
		gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreAggiornaModelli">
		
			<c:set var="id" value='${datiRiga.G1CRIMOD_ID}'/>
			<c:set var="titolo" value='${datiRiga.G1CRIMOD_TITOLO}'/>
			
			<gene:campoScheda campo="ID" modificabile="false" visibile="false"/>
			<gene:campoScheda campo="TITOLO" obbligatorio="true"/>
			<gene:campoScheda campo="DESCRI" />
			<c:choose>
				<c:when test='${modo eq "NUOVO"}'>
					<gene:archivio titolo="gare e lotti di gara OEPV"
						lista="gare/gare/lista-gareOepv-popup.jsp"
						scheda=""
						schedaPopUp=""
						campi="GARE.NGARA"
						functionId="g1crimod"
						chiave=""
						formName="formGareALotti"
						inseribile="false">
						<gene:campoScheda campo="NGARA" title="Gara da cui importare i criteri di valutazione" definizione="T21;0" campoFittizio="true" obbligatorio="true" />
					</gene:archivio>
				</c:when>
			</c:choose>
			
			<gene:campoScheda>	
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
						<gene:insert name="pulsanteDettaglio">
							<INPUT type="button" class="bottone-azione" value="Visualizza criteri" title="Visualizza criteri" onclick="javascript:apriDettaglio()">
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
			
		</gene:formScheda>
	</gene:redefineInsert>

	<gene:javaScript>
	function apriDettaglio(titolo){
		link =  '${pageContext.request.contextPath}/ApriPagina.do?'+csrfToken+'&href=gare/goevmod/goevmod-lista.jsp&idcrimod=${id}&tipoCriterio=1&titolo=${titolo}';
		document.location.href = link;
	}
	</gene:javaScript>


</gene:template>
