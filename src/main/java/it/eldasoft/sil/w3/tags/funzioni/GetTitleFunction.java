/*
 * Created on 20/ott/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.tags.funzioni;

import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.functions.AbstractGetTitleFunction;

public class GetTitleFunction extends AbstractGetTitleFunction {

  protected String getTitleInserimento(PageContext pageContext, String table) {

    return null;
  }

  protected String getTitleModifica(PageContext pageContext, String table,
      String keys) {

    return null;
  }

  public String[] initFunction() {
    return new String[] {
        "W3GARA|Nuova gara|{0}"
            + "||select w3gara.oggetto from w3gara where numgara = #W3GARA.NUMGARA#",
        "W3SMARTCIG|Nuova gara per SmartCig|{0}"
            + "||select w3smartcig.oggetto from w3smartcig where CODRICH = #W3SMARTCIG.CODRICH#",
        "W3LOTT|Nuovo lotto|{0} - Lotto n. {1}"
            + "||select w3gara.oggetto, w3lott.numlott from w3gara, w3lott where "
            + " w3gara.numgara = w3lott.numgara "
            + " and w3lott.numgara = #W3LOTT.NUMGARA# "
            + " and w3lott.numlott = #W3LOTT.NUMLOTT# ",
        "W3DACO|Nuovo contratto|{0} - {1}"
            + "||select cig, oggetto from w3daco where scheda_id = #W3DACO.SCHEDA_ID#",
        "W3FASI|Definizione di una nuova fase|{0} - {1} - {2}"
            + "||select w3daco.cig, w3daco.oggetto, tab1.tab1desc from w3fasi, w3daco, tab1 where w3fasi.scheda_id = #W3FASI.SCHEDA_ID#"
            + " and w3fasi.schedacompleta_id = #W3FASI.SCHEDACOMPLETA_ID# "
            + " and w3fasi.fase_esecuzione = #W3FASI.FASE_ESECUZIONE#"
            + " and w3fasi.num = #W3FASI.NUM# "
            + " and w3fasi.scheda_id = w3daco.scheda_id and tab1.tab1tip = w3fasi.fase_esecuzione and tab1.tab1cod = 'W3020'",
        "W3INCA|Nuovo incaricato/progettista|Incaricato/progettista n° {0} - {1}"
            + "||select w3inca.num, tecni.nomtec from tecni, w3inca where tecni.codtec = w3inca.codtec and w3inca.schedacompleta_id = #W3INCA.SCHEDACOMPLETA_ID#"
            + " and w3inca.sezione = #W3INCA.SEZIONE# and w3inca.num = #W3INCA.NUM#",
        "W3FINA|Nuovo finanziamento|Finanziamento {0}"
            + "||select tab2.tab2d2 from tab2, w3fina where w3fina.id_finanziamento = tab2.tab2tip "
            + " and tab2.tab2cod = 'W3z02' "
            + " and w3fina.schedacompleta_id = #W3FINA.SCHEDACOMPLETA_ID# and w3fina.num = #W3FINA.NUM#",
        "W3AGGI|Nuova impresa aggiudicataria/affidataria|Impresa aggiudicataria / affidataria n° {0} - {1}"
            + "||select w3aggi.num, impr.nomimp from impr, w3aggi where impr.codimp = w3aggi.codimp and w3aggi.schedacompleta_id = #W3AGGI.SCHEDACOMPLETA_ID#"
            + " and w3aggi.num = #W3AGGI.NUM#",
        "W3REQU|Nuova categoria scorporabile|Categoria scorporabile {0}"
            + "||select tab2.tab2d2 from tab2, w3requ where w3requ.id_categoria = tab2.tab2tip "
            + " and tab2.tab2cod = 'W3z03' "
            + " and w3requ.schedacompleta_id = #W3REQU.SCHEDACOMPLETA_ID# and w3requ.num = #W3REQU.NUM#",
        "W3STAP|Nuova stazione appaltante|Stazione appaltante {0}"
            + "||select den_sa from w3stap where w3stap.stap_id = #W3STAP.STAP_ID#",
        "W3AMMI|Nuova amministrazione aggiudicatrice|Amministrazione aggiudicatrice - {0}"
            + "||select uffint.nomein from uffint, w3ammi where uffint.codein = w3ammi.codein and w3ammi.codamm = #W3AMMI.CODAMM#",
        "W3FS1|Nuovo avviso di preinformazione|Avviso di preinformazione - {0}"
            + "||select w3fs1s2.title_contract from w3fs1s2 where w3fs1s2.id = #W3FS1.ID# and w3fs1s2.num = 1",
        "W3ANNEXB|Nuovo lotto|Lotto - {0}"
            + "||select w3annexb.title from w3annexb where w3annexb.id = #W3ANNEXB.ID# and w3annexb.num = #W3ANNEXB.NUM#",
        "W3FS2|Nuovo bando di gara|Bando di gara - {0}"
            + "||select w3fs2.title_contract from w3fs2 where w3fs2.id = #W3FS2.ID#",
        "W3FS3|Nuovo avviso di aggiudicazione|Avviso di aggiudicazione - {0}"
            + "||select w3fs3.title_contract from w3fs3 where w3fs3.id = #W3FS3.ID#",
        "W3FS8|Nuovo avviso sul profilo di committente|Avviso sul profilo di committente - {0}"
            + "||select w3fs8s2.title_contract from w3fs8s2 where w3fs8s2.id = #W3FS8.ID# and w3fs8s2.num = 1",
        "W3FS9|Nuovo bando di gara semplificato|Bando di gara semplificato - {0}"
            + "||select w3fs9.title_contract from w3fs9 where w3fs9.id = #W3FS9.ID#",
        "W3FS3AWARD|Nuova aggiudicazione|Aggiudicazione - {0}"
            + "||select w3fs3award.contract_title from w3fs3award where "
            + "w3fs3award.id = #W3FS3AWARD.ID# and w3fs3award.item = #W3FS3AWARD.ITEM#",
        "W3FS14|Nuova rettifica|Rettifica - {0}"
            + "||select v_w3simap.title_contract from v_w3simap, w3fs14 where "
            + " v_w3simap.id = w3fs14.id_rif and w3fs14.id = #W3FS14.ID# ",
        "W3SIMAPEMAIL|Nuovo invio SIMAP|Invio SIMAP n. {0}"
            + "||select num from w3simapemail where id = #W3SIMAPEMAIL.ID# and num = #W3SIMAPEMAIL.NUM# ",
        "GRP|Nuovo gruppo|Gruppo n. {0} - {1}"
            + "||select idgrp, descgrp from grp where idgrp = #GRP.IDGRP#",
        "UNIT|Nuova unit&agrave; organizzativa|Unit&agrave; organizzativa n. {0} - {1}"
            + "||select idunit, descunit from unit where idunit = #UNIT.IDUNIT#"};

  }

}
