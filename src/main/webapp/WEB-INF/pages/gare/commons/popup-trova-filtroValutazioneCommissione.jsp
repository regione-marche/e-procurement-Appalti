<%
/*
 * Created on: 14-11-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Form per impostare un filtro sulla lista delle categorie */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<gene:template file="popup-template.jsp" >
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:setString name="titoloMaschera" value="Verifica dettaglio valutazione completato"/>
	
	<c:choose>
		<c:when test='${not empty param.ngara}'>
			<c:set var="ngara" value="${param.ngara}" />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${ngara}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.codgar}'>
			<c:set var="codgar" value="${param.codgar}" />
		</c:when>
		<c:otherwise>
			<c:set var="codgar" value="${codgar}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.tippar}'>
			<c:set var="tippar" value="${param.tippar}" />
		</c:when>
		<c:otherwise>
			<c:set var="tippar" value="${tippar}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.dittePerPagina}'>
			<c:set var="dittePerPagina" value="${param.dittePerPagina}" />
		</c:when>
		<c:otherwise>
			<c:set var="dittePerPagina" value="${dittePerPagina}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.lottoPlicoUnico}'>
			<c:set var="lottoPlicoUnico" value="${param.lottoPlicoUnico}" />
		</c:when>
		<c:otherwise>
			<c:set var="lottoPlicoUnico" value="${lottoPlicoUnico}" />
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test='${lottoPlicoUnico ne 1}'>
			<c:set var="codiceGaraDitg" value="DITG.NGARA5" />
			<c:set var="codiceGara" value="${ngara}" />
		</c:when>
		<c:otherwise>
			<c:set var="codiceGaraDitg" value="DITG.CODGAR5" />
			<c:set var="codiceGara" value="${codgar}" />
		</c:otherwise>
	</c:choose>
	
	<c:remove var="trovaDITG"/>
	
	<c:set var="modo" value="MODIFICA" scope="request" />
	
	<c:set var="isValutazioneCommissione" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsValutazioneCommissioneFunction",pageContext,codiceGara)}' />
	
	<gene:redefineInsert name="corpo">
	
	<c:if test="${param.reimposta}">		
		<c:set var="codiceTecnicoUtente" value="" scope="request" />
		<c:set var="nomeTecnicoUtente" value="" scope="request" />
	</c:if>
	
	<br>
		Mediante questa funzione viene impostato il filtro sulle ditte in gara con dettaglio valutazione non completato.
		<c:if test="${isValutazioneCommissione}">
			<br>Specificando i riferimenti del commissario, è possibile individuare le ditte per cui tale commissario non ha ancora espresso valutazione.  
		</c:if>
		<br>Confermi l'operazione?
		<br><br>

		<gene:formTrova entita="DITG"  >	
		
			<c:if test="${isValutazioneCommissione}">		
				<gene:campoTrova campo="CODFOF" defaultValue="${codiceTecnicoUtente}" entita="GFOF" from="G1CRIDEF,GOEV" where="GOEV.NGARA = DITG.NGARA5 AND G1CRIDEF.NGARA = DITG.NGARA5 AND GOEV.TIPPAR = ${tippar} AND GOEV.NECVAN = G1CRIDEF.NECVAN AND G1CRIDEF.MAXPUN >0 AND G1CRIDEF.MODPUNTI = '1' AND GFOF.NGARA2 = ${codiceGaraDitg} AND GFOF.ESPGIU = '1' AND NOT EXISTS (SELECT 1 FROM G1CRIVALCOM G1, G1CRIVAL V WHERE V.IDCRIDEF= G1CRIDEF.ID AND V.NGARA=DITG.NGARA5 AND V.DITTAO=DITG.DITTAO AND G1.IDGFOF = GFOF.ID AND G1.IDCRIVAL = V.ID AND G1.COEFFI IS NOT NULL) " />
				<gene:campoTrova campo="NOMFOF" defaultValue="${nomeTecnicoUtente}" entita="GFOF" from="G1CRIDEF,GOEV" where="GOEV.NGARA = DITG.NGARA5 AND G1CRIDEF.NGARA = DITG.NGARA5 AND GOEV.TIPPAR = ${tippar} AND GOEV.NECVAN = G1CRIDEF.NECVAN AND G1CRIDEF.MAXPUN >0 AND G1CRIDEF.MODPUNTI = '1' AND GFOF.NGARA2 = ${codiceGaraDitg} AND GFOF.ESPGIU = '1' AND NOT EXISTS (SELECT 1 FROM G1CRIVALCOM G1, G1CRIVAL V WHERE V.IDCRIDEF= G1CRIDEF.ID AND V.NGARA=DITG.NGARA5 AND V.DITTAO=DITG.DITTAO AND G1.IDGFOF = GFOF.ID AND G1.IDCRIVAL = V.ID AND G1.COEFFI IS NOT NULL) " />
			</c:if>	
			
			<input type="hidden" name="ngara" value="${ngara}">
			<input type="hidden" name="lottoPlicoUnico" value="${lottoPlicoUnico}">
			<input type="hidden" name="dittePerPagina" value="${dittePerPagina}">
			<input type="hidden" name="tippar" value="${tippar}">
			<input type="hidden" name="codgar" value="${codgar}">
			<input type="hidden" name="reimposta" value="true">
			
		</gene:formTrova>
		
		<gene:javaScript>	
			
			<c:if test="${!isValutazioneCommissione}">
			$(".opzioniTrova").closest("tr").hide();
			$("#contenitore-comandi-dettaglio").children().eq(1).hide();
			</c:if>	
		
			document.forms[0].jspPathTo.value="gare/commons/popup-filtro-valutazione.jsp";
			document.forms[0].action+= "?tipo=Valutazione";
			
			var dittePerPAgina="${dittePerPagina}";
			var indiceSelezionato =  document.getElementById('risultatiPerPagina').selectedIndex;
            document.getElementById("risultatiPerPagina").options[indiceSelezionato].value = dittePerPAgina;
            document.getElementById("risultatiPerPagina").options[indiceSelezionato].innerHTML = dittePerPAgina;
			document.getElementById("risultatiPerPagina").disabled=true
			
			var trovaEseguiDefault = trovaEsegui;
			function trovaEseguiCustom() {
				var c0 = $("#Campo0").val();
				var c1 = $("#Campo1").val();
				
				if(!c0 && !c1){
					document.forms[0].campiCount.value+=1;
					
					<c:choose>
						<c:when test="${isValutazioneCommissione}">
							document.forms[0].filtro.value = " EXISTS (SELECT 1 FROM GFOF GF, GOEV G,G1CRIDEF C LEFT JOIN G1CRIVAL V ON V.IDCRIDEF= C.ID AND V.NGARA=DITG.NGARA5 AND V.DITTAO=DITG.DITTAO WHERE G.NGARA = DITG.NGARA5 AND C.NGARA = DITG.NGARA5 "
							+ "AND G.TIPPAR = ${tippar} AND G.NECVAN = C.NECVAN AND GF.NGARA2 = ${codiceGaraDitg} AND C.MAXPUN >0 AND C.MODPUNTI = '1' AND V.COEFFI IS NULL AND GF.ESPGIU = '1' AND "
							+ "NOT EXISTS (SELECT 1 FROM G1CRIVALCOM G1 WHERE G1.IDGFOF  = GF.ID AND G1.IDCRIVAL = V.ID AND COEFFI IS NOT NULL))";					
						</c:when>	
						<c:otherwise>			
							document.forms[0].filtro.value = " EXISTS (SELECT 1 FROM G1CRIDEF C,GOEV G WHERE G.NGARA = DITG.NGARA5 AND C.NGARA = DITG.NGARA5 AND G.TIPPAR = ${tippar} AND G.NECVAN = C.NECVAN "
							+ " AND C.MAXPUN >0 AND C.MODPUNTI = '1' AND NOT EXISTS (SELECT V.ID FROM G1CRIVAL V WHERE V.IDCRIDEF=C.ID AND V.NGARA=DITG.NGARA5 AND V.DITTAO=DITG.DITTAO AND COEFFI IS NOT NULL))";
						</c:otherwise>	
					</c:choose>		
					
				}
				trovaEseguiDefault();
			}
			trovaEsegui = trovaEseguiCustom;
			
		</gene:javaScript>

	</gene:redefineInsert>

</gene:template>
