 <%
	/*
	 * Created on 20-Ott-2008
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

<gene:callFunction obj="it.eldasoft.sil.w3.tags.funzioni.GetValoriDropDownRequisitiFunction" />

<gene:formScheda entita="W3GARAREQ" gestisciProtezioni="true" 
	gestore="it.eldasoft.sil.w3.tags.gestori.submit.GestoreW3GARAREQ"
	plugin="it.eldasoft.sil.w3.tags.gestori.plugin.GestoreW3GARAREQ">
	<gene:campoScheda campo="NUMGARA" visibile="false" defaultValue='${gene:getValCampo(keyParent,"NUMGARA")}'/>
	<gene:campoScheda campo="NUMREQ" visibile="false" />	

	<gene:campoScheda>
		<td colspan="2"><b><br>SEZIONE I: DATI DEL REQUISITO<br><br></b></td>
	</gene:campoScheda>
	<gene:campoScheda>
		<td colspan="2"><b>I.1) Dati generali</b></td>
	</gene:campoScheda>
	<gene:campoScheda title="Requisito" campo="CODICE_DETTAGLIO" obbligatorio="true" modificabile='${modo eq "NUOVO"}' >
	<gene:addValue value="" descr="" />
		<c:if test='${!empty listaValoriRequisiti}'>
			<c:forEach items="${listaValoriRequisiti}" var="valoriRequisiti">
				<gene:addValue value="${valoriRequisiti[0]}"
					descr="${valoriRequisiti[1]}" />
			</c:forEach>
		</c:if>
	</gene:campoScheda>
	
	
	<gene:campoScheda campo="DESCRIZIONE" obbligatorio="true" />
	<gene:campoScheda campo="VALORE" />
	<gene:campoScheda campo="FLAG_ESCLUSIONE" defaultValue="2"/>
	<gene:campoScheda campo="FLAG_COMPROVA_OFFERTA" defaultValue="2"/>
	<gene:campoScheda campo="FLAG_AVVALIMENTO"  defaultValue="1"/>
	<gene:campoScheda campo="FLAG_BANDO_TIPO"  defaultValue="2"/>
	<gene:campoScheda campo="FLAG_RISERVATEZZA"  defaultValue="2"/>
	<gene:campoScheda entita="W3GARA" campo="STATO_SIMOG" visibile="false" where="W3GARA.NUMGARA = W3GARAREQ.NUMGARA"/>
	
	
	<gene:campoScheda>
		<td colspan="2"><b>I.2) Eventuali CIG associati <br>
			(in assenza di indicazioni, si intende che il requisito deve essere applicato a tutti i CIG associati alla gara)</b></td>
	</gene:campoScheda>

	<gene:campoScheda visibile="${modo eq 'VISUALIZZA' && empty datiW3GARAREQCIG}">
		<td style="padding-top: 2px; padding-bottom:2px;" colspan="2">Nessun CIG presente</td>
	</gene:campoScheda>

	<gene:callFunction obj="it.eldasoft.sil.w3.tags.funzioni.GestioneW3GARAREQCIGFunction" parametro="${gene:getValCampo(key, 'NUMGARA')};${gene:getValCampo(key, 'NUMREQ')}" />
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='W3GARAREQCIG'/>
		<jsp:param name="chiave" value='${key}'/>
		<jsp:param name="nomeAttributoLista" value='datiW3GARAREQCIG' />
		<jsp:param name="idProtezioni" value="W3GARAREQCIG" />
		<jsp:param name="sezioneListaVuota" value="false" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/w3/w3garareqcig/w3garareqcig-interno-scheda.jsp"/>
		<jsp:param name="arrayCampi" value="'W3GARAREQCIG_NUMGARA_', 'W3GARAREQCIG_NUMREQ_', 'W3GARAREQCIG_NUMCIG_', 'W3GARAREQCIG_CIG_', 'W3LOTT_OGGETTO_' "/>
		<jsp:param name="arrayVisibilitaCampi" value="false,false,false,true,true" />
		<jsp:param name="titoloSezione" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CIG n. " />
		<jsp:param name="titoloNuovaSezione" value=" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;altro CIG" />
		<jsp:param name="descEntitaVociLink" value="altro CIG" />
		<jsp:param name="msgRaggiuntoMax" value="e CIG"/>
		<jsp:param name="usaContatoreLista" value="true"/>
		<jsp:param name="numMaxDettagliInseribili" value="5"/>
	</jsp:include>
	
	<gene:campoScheda>
		<td colspan="2"><b>I.3) Eventuali documenti richiesti</b></td>
	</gene:campoScheda>

	<gene:campoScheda visibile="${modo eq 'VISUALIZZA' && empty datiW3GARAREQDOC}">
		<td style="padding-top: 2px; padding-bottom:2px;" colspan="2">Nessun documento presente</td>
	</gene:campoScheda>

	<gene:callFunction obj="it.eldasoft.sil.w3.tags.funzioni.GestioneW3GARAREQDOCFunction" parametro="${gene:getValCampo(key, 'NUMGARA')};${gene:getValCampo(key, 'NUMREQ')}" />
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='W3GARAREQDOC'/>
		<jsp:param name="chiave" value='${key}'/>
		<jsp:param name="nomeAttributoLista" value='datiW3GARAREQDOC' />
		<jsp:param name="idProtezioni" value="W3GARAREQDOC" />
		<jsp:param name="sezioneListaVuota" value="false" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/w3/w3garareqdoc/w3garareqdoc-interno-scheda.jsp"/>
		<jsp:param name="arrayCampi" value="'W3GARAREQDOC_NUMGARA_', 'W3GARAREQDOC_NUMREQ_', 'W3GARAREQDOC_NUMDOC_', 'W3GARAREQDOC_CODICE_TIPO_DOC_', 'W3GARAREQDOC_DESCRIZIONE_', 'W3GARAREQDOC_EMETTITORE_', 'W3GARAREQDOC_FAX_', 'W3GARAREQDOC_TELEFONO_', 'W3GARAREQDOC_MAIL_', 'W3GARAREQDOC_MAIL_PEC_' "/>
		<jsp:param name="arrayVisibilitaCampi" value="false,false,false,true,true,true,true,true,true,true" />
		<jsp:param name="titoloSezione" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Documento richiesto n. " />
		<jsp:param name="titoloNuovaSezione" value=" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Aggiungi altro documento richiesto" />
		<jsp:param name="descEntitaVociLink" value="Documento richiesto" />
		<jsp:param name="msgRaggiuntoMax" value="e documento richiesto"/>
		<jsp:param name="usaContatoreLista" value="true"/>
		<jsp:param name="numMaxDettagliInseribili" value="5"/>
	</jsp:include>
	
	<gene:redefineInsert name="pulsanteNuovo" />
	<gene:redefineInsert name="schedaNuovo" />
	
	<c:if test="${(datiRiga.W3GARA_STATO_SIMOG eq '5' or datiRiga.W3GARA_STATO_SIMOG eq '6' or datiRiga.W3GARA_STATO_SIMOG eq '7') }">
		<gene:redefineInsert name="pulsanteModifica" />
		<gene:redefineInsert name="schedaModifica" />
	</c:if>

	<gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
	</gene:campoScheda>
	
</gene:formScheda>


<gene:javaScript>

	modifySelectOption($("#W3GARAREQ_CODICE_DETTAGLIO"));

	function modifySelectOption(obj) {
		var obj_opt = obj.find("option");
		$.each(obj_opt, function( key, opt ) {
			var _title = opt.title;
			if (_title.length > 110) {
				 _title = _title.substring(0,110) + "...";
			}
			opt.text = _title;
		});
		obj.css("width","550px");	
	}

	
</gene:javaScript>
