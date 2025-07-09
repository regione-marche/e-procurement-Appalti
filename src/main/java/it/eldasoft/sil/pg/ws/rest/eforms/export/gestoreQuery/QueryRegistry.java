package it.eldasoft.sil.pg.ws.rest.eforms.export.gestoreQuery;


import java.util.Map;
import java.util.HashMap;

/**
 * Mantiene una mappa statica che associa (genere, code) alla query da eseguire.
 */
public final class QueryRegistry {

    // Mappa statica: key = GenereCodeKey, value = stringa SQL
    private static final Map<GenereCodeKey, String> queriesMap = new HashMap<>();

    static {
      
      queriesMap.put(
          new GenereCodeKey(1, PathJsonEform.ROOT),
          "SELECT * FROM * WHERE *;"
      );
      
      //da finire
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_CONTRACTINGAUTHORITY_BUYER),
          "select u.tipoamm, u.attprinc, u.profco, u.attsett, u.codein "
          + "from torn t "
          + "inner join uffint u on u.codein = t.cenint "
          + "where t.codgar = ? "
      );
      
      
      
      queriesMap.put(
          new GenereCodeKey(1, PathJsonEform.GET_LOTTI),
          "select ngara from gare where codgar1 != ngara and codgar1 = ? "
            + "order by cast(codiga as decimal(10,0)) asc "
          );
      queriesMap.put(
          new GenereCodeKey(3, PathJsonEform.GET_LOTTI),
          "select ngara from gare where codgar1 != ngara and codgar1 = ? "
            + "order by cast(codiga as decimal(10,0)) asc "
          );
      
      queriesMap.put(
          new GenereCodeKey(2, PathJsonEform.GET_LOTTI),
          "select ngara from gare where codgar1 = ? "
            + "order by cast(codiga as decimal(10,0)) asc "
          );
      
      queriesMap.put(
          new GenereCodeKey(10, PathJsonEform.GET_LOTTI),
          "select ngara from gare where codgar1 = ? "
            + "order by cast(codiga as decimal(10,0)) asc "
          );
      
      queriesMap.put(
          new GenereCodeKey(11, PathJsonEform.GET_LOTTI),
          "select ngara from gare where codgar1 = ? "
            + "order by cast(codiga as decimal(10,0)) asc "
          );
      
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GET_LOTTI_CAN),
          "select g.ngara "
          + "from g1npascheda s "
          + "inner join g1npalottischeda ls on ls.idscheda = s.id "
          + "inner join gare g on g.ngara = ls.ngara "
          + "where s.uuid_eform = ? "
          + "and g.codgar1 = ? "
          + "order by cast(g.codiga as decimal(10,0)) asc "
          );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GET_LOTTI_CONTRATTO),
          "select ga.ngara "
          + "from gare ga "
          + "inner join garecont gc on gc.codimp = ga.ditta and ((gc.ngara=ga.ngara and gc.ncont=1) or "
          + "        (gc.ngara=ga.codgar1 "
          + "        and (gc.ngaral is null or gc.ngaral=ga.ngara))) "
          + "and ga.ditta is not null "
          + "and gc.ngara = ? "
          + "and gc.ncont = ? "
          );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_SETTLEDCONTRACT),
          "select gc.ngara, gc.ncont "
          + "from g1npascheda s "
          + "inner join g1npalottischeda ls on ls.idscheda = s.id "
          + "inner join gare g on g.ngara = ls.ngara "
          + "inner join garecont gc on gc.codimp = g.ditta and ((gc.ngara=g.ngara and gc.ncont=1) or "
          + "        (gc.ngara=g.codgar1 "
          + "        and (gc.ngaral is null or gc.ngaral=g.ngara))) "
          + "where s.uuid_eform = ? "
          + "and g.codgar1 = ? "
          + "and g.ditta is not null "
          + "group by gc.ngara, gc.ncont "
          + "order by gc.ngara, gc.ncont "
          );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_SETTLEDCONTRACT_AD1),
          "select gc.ngara, gc.ncont "
          + "from gare g "
          + "inner join garecont gc on gc.codimp = g.ditta and ((gc.ngara=g.ngara and gc.ncont=1) or "
          + "        (gc.ngara=g.codgar1 "
          + "        and (gc.ngaral is null or gc.ngaral=g.ngara))) "
          + "where g.codgar1 = ? "
          + "and g.ditta is not null "
          + "group by gc.ngara, gc.ncont "
          + "order by gc.ngara, gc.ncont "
          );
      
      queriesMap.put(
          new GenereCodeKey(1, PathJsonEform.GR_PROCEDURE_DESCRIPTION),
          "select destor,tipgen,destor from torn where codgar = ? "
          );
      
      queriesMap.put(
          new GenereCodeKey(3, PathJsonEform.GR_PROCEDURE_DESCRIPTION),
          "select destor,tipgen,destor from torn where codgar = ? "
          );
      
      queriesMap.put(
          new GenereCodeKey(2, PathJsonEform.GR_PROCEDURE_DESCRIPTION),
          "select g.not_gar,t.tipgen,g.not_gar from gare g inner join torn t on t.codgar = g.codgar1 where g.codgar1 = ? "
          );
      
      queriesMap.put(
          new GenereCodeKey(10, PathJsonEform.GR_PROCEDURE_DESCRIPTION),
          "select oggetto,tipoele,oggetto from garealbo where codgar = ? "
          );
      
      queriesMap.put(
          new GenereCodeKey(11, PathJsonEform.GR_PROCEDURE_DESCRIPTION),
          "select oggetto,tipoapp,oggetto from gareavvisi where codgar = ? "
          );
      
      
      
      
      
      //Riporto piu' volte not_gar per evitare di "giocare" con gli indici durante il for
      queriesMap.put(
          new GenereCodeKey(100, PathJsonEform.GR_LOT_DESCRIPTION),
          "select g.not_gar, g.not_gar, t.tipgen "
            + "from torn t "
            + "inner join gare g on t.codgar = g.codgar1 "
            + "where g.ngara = ? "
          );
      
      queriesMap.put(
          new GenereCodeKey(2, PathJsonEform.GR_LOT_DESCRIPTION),
          "select g.not_gar, g.not_gar, t.tipgen "
            + "from torn t "
            + "inner join gare g on t.codgar = g.codgar1 "
            + "where g.ngara = ? "
          );
      
      queriesMap.put(
          new GenereCodeKey(10, PathJsonEform.GR_LOT_DESCRIPTION),
          "select oggetto, oggetto, tipoele from garealbo where ngara = ? "
          );
      
      queriesMap.put(
          new GenereCodeKey(11, PathJsonEform.GR_LOT_DESCRIPTION),
          "select oggetto, oggetto, tipoapp from gareavvisi where ngara = ? "
          );
      
      
      
      
      queriesMap.put(
          new GenereCodeKey(1, PathJsonEform.GR_PROCEDURE_PROCEDURE),
          "select g.tipgarg, t.iterga "
          + "from gare g "
          + "inner join torn t on t.codgar = g.codgar1 "
          + "where g.codgar1 = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(3, PathJsonEform.GR_PROCEDURE_PROCEDURE),
          "select g.tipgarg, t.iterga "
          + "from gare g "
          + "inner join torn t on t.codgar = g.codgar1 and g.ngara = t.codgar "
          + "where g.codgar1 = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(2, PathJsonEform.GR_PROCEDURE_PROCEDURE),
          "select g.tipgarg, t.iterga "
          + "from gare g "
          + "inner join torn t on t.codgar = g.codgar1 "
          + "where g.codgar1 = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(10, PathJsonEform.GR_PROCEDURE_PROCEDURE),
          "select g.tipgarg, t.iterga "
          + "from gare g "
          + "inner join torn t on t.codgar = g.codgar1 "
          + "where g.codgar1 = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(11, PathJsonEform.GR_PROCEDURE_PROCEDURE),
          "select g.tipgarg, t.iterga "
          + "from gare g "
          + "inner join torn t on t.codgar = g.codgar1 "
          + "where g.codgar1 = ? "
      );
      
      
      
      // codice genere zero perche' va bene per tutti i casi
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_EUFUNDS_INDICATOR),
          "select t.apfinfc "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_EUFUNDS),
          "select t.progeu "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      
      
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_RESERVEDPARTICIPATION),
          "select affiris from gare1 where ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_PROCEDURE_MAXLOTS),
          "select nofdit, ngadit, offlot "
          + "from torn "
          + "where codgar = ?"
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_CANDIDATES),
          "select t.minope, t.maxope "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_PROCEDURE_ACCELERATED),
          "select prourg, motacc "
          + "from torn "
          + "where codgar = ?"
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_GPAAGREEMENT),
          "select t.accappub "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_VARIANTS),
          "select t.ammvar "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_PUBLICOPENINGEVENT),
          "select t.desoff, t.oesoff "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_PUBLICOPENINGEVENT_RETTIFICA),
          "SELECT p.desoff, p.oesoff "
          + "FROM g1nparet p "
          + "INNER JOIN g1npascheda s ON s.id = p.idnpascheda "
          + "WHERE s.uuid_eform = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_FRAMEWORKAGREEMENT_USE),
          "select t.accqua "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_DPS_USE),
          "select t.issda "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_AUCTIONTERMS),
          "select t.ricastae "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      
      
      
      
      //Ho messo zero perche' questa query va bene per tutti i generi (PROCEDURE)
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_PROCEDURE_SCOPE_MAINCLASSIFICATION),
          "select cast(min(ga.codcpv) as varchar(20)) as codcpv "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "inner join GARCPV ga on ga.ngara = g.ngara "
          + "where ga.tipcpv = '1' "
          + "and t.codgar = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_PROCEDURE_SCOPE_ADDITIONALCLASSIFICATION),
          "select ga.codcpv "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "inner join GARCPV ga on ga.ngara = g.ngara "
          + "where ga.tipcpv = '2' "
          + "and t.codgar = ? "
      );

      //Ho messo zero perche' questa query va bene per tutti i generi (LOT)
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_SCOPE_MAINCLASSIFICATION),
          "select codcpv from GARCPV where tipcpv = '1' and ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_SCOPE_ADDITIONALCLASSIFICATION),
          "select codcpv from GARCPV where tipcpv = '2' and ngara = ? "
      );
      
      //--------------------------------------------------------
      //Per company il genere non e' legato al genere della gara
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_COMPANY_UFFICI),
          "select nomein, cfein, ivaein, indweb "
          + "from uffint "
          + "where codein = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(1, PathJsonEform.GR_COMPANY_UFFICI),
          "WITH uffintFiltered AS ( "
          + "  SELECT u1.*, p.numpun AS p_numpun, p.orgricorso AS p_orgricorso, "
          + "         p.nompun AS p_nompun, p.cfein AS p_cfein, p.ivaein AS p_ivaein "
          + "  FROM uffint u1 "
          + "  LEFT JOIN punticon p ON u1.codein = p.codein "
          + "  WHERE p.numpun = ? "
          + "    AND u1.codein = ? "
          + ") "
          + "SELECT * "
          + "FROM ( "
          + "  SELECT p.nompun, u.cfein, u.ivaein, p.indweb, p.numpun "
          + "  FROM uffintFiltered u1 "
          + "  INNER JOIN uffint u ON u.codein = u1.codein "
          + "  LEFT JOIN punticon p ON u.codein = p.codein "
          + "  WHERE (p.orgricorso IS NULL OR p.orgricorso != '1') "
          + "    and u1.p_numpun = p.numpun "
          + "  UNION "
          + "  SELECT p.nompun, p.cfein, p.ivaein, p.indweb, p.numpun "
          + "  FROM uffintFiltered u1 "
          + "  INNER JOIN uffint u ON u.codein = u1.codein "
          + "  LEFT JOIN punticon p ON u.codein = p.codein "
          + "  WHERE p.orgricorso is not null "
          + "    and p.orgricorso = '1' "
          + "    and u1.p_numpun = p.numpun "
          + ") b "
          + "ORDER BY numpun ASC "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_COMPANY_OPERATORI),
          "select nomest, cfimp, pivimp, indweb "
          + "from impr "
          + "where codimp = ? "
      );
      
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_COMPANY_ADDRESS_UFFICI),
          "select u.citein, u.proein, u.codnaz, t.codice, u.viaein, u.nciein, u.capein "
          + "from uffint u "
          + "left join tabnuts t on t.siglaprov = u.proein "
          + "where u.codein = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(1, PathJsonEform.GR_COMPANY_ADDRESS_UFFICI),
          "select p.citein, p.proein, p.codnaz, t.codice, p.viaein, p.nciein, p.capein "
          + "from uffint u "
          + "left join punticon p on u.codein = p.codein "
          + "left join tabnuts t on t.siglaprov = p.proein "
          + "where p.numpun = ? "
          + "and u.codein = ? "
      );
      
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_COMPANY_ADDRESS_OPERATORI),
          "select i.locimp, i.proimp, i.nazimp, t.codice, i.indimp, i.nciimp, i.capimp "
          + "from impr i "
          + "left join tabnuts t on t.siglaprov = i.proimp "
          + "where i.codimp = ? "
      );
      
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_COMPANY_CONTACT_UFFICI),
          "select emai2in, emaiin, telein, faxein "
          + "from uffint "
          + "where codein = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(1, PathJsonEform.GR_COMPANY_CONTACT_UFFICI),
          "select p.emai2in, p.emaiin, p.telein, p.faxein "
          + "from  uffint u "
          + "left join punticon p on u.codein = p.codein "
          + "where p.numpun = ? "
          + "and u.codein = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_COMPANY_CONTACT_OPERATORI),
          "select emai2ip, emaiip, telimp, faximp "
          + "from impr "
          + "where codimp = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_COMPANY_ECONOMICOPERATOREXCLUSIVE),
          "select cladim "
          + "from impr "
          + "where codimp = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_REVIEWORG),
          "select p.numpun, p.codein "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "inner join punticon p on t.cenint = p.codein "
          + "where p.orgricorso IS NOT NULL "
          + "and p.orgricorso = '1' "
          + "and p.numpun IS NOT NULL "
          + "and t.orgricorso = p.numpun "
          + "and g.ngara = ? "
      );
      
      
      
      //--------------------------------------------------------
      
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_ORGANISATIONS_UFFICI_CN_PIN),
          "WITH tornFiltered AS ( "
          + "    SELECT * "
          + "    FROM torn "
          + "    WHERE codgar = ? "
          + ") "
          + "SELECT * "
          + "FROM ( "
          + "    SELECT p.numpun, u.codein, p.orgricorso "
          + "    FROM tornFiltered t "
          + "    INNER JOIN uffint u ON u.codein = t.cenint "
          + "    LEFT JOIN punticon p ON u.codein = p.codein "
          + "    WHERE p.numpun IS NOT NULL "
          + "    AND ((t.pcooff IS NOT NULL AND p.numpun = t.pcooff) "
          + "    or (t.pcodoc IS NOT NULL AND p.numpun = t.pcodoc) "
          + "    or (t.pcopre IS NOT NULL AND p.numpun = t.pcopre)) "
          + "    AND (p.orgricorso IS NULL OR (p.orgricorso IS NOT NULL AND p.orgricorso != '1')) "
          + "    UNION "
          + "    SELECT 0 AS numpun, u.codein, null AS orgricorso "
          + "    FROM tornFiltered t "
          + "    INNER JOIN uffint u ON u.codein = t.cenint "
          + "    UNION "
          + "    SELECT p.numpun, p.codein, p.orgricorso "
          + "    FROM tornFiltered t "
          + "    LEFT JOIN punticon p ON t.cenint = p.codein "
          + "    WHERE p.numpun IS NOT NULL "
          + "    AND p.orgricorso IS NOT NULL "
          + "    AND p.orgricorso = '1' "
          + "    AND t.orgricorso = p.numpun "
          + ") b "
          + "ORDER BY numpun ASC "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_ORGANISATIONS_UFFICI_CAN),
          "WITH tornFiltered AS ( "
          + "    SELECT * "
          + "    FROM torn "
          + "    WHERE codgar = ? "
          + ") "
          + "SELECT * "
          + "FROM ( "
          + "    SELECT p.numpun, u.codein, p.orgricorso "
          + "    FROM tornFiltered t "
          + "    INNER JOIN uffint u ON u.codein = t.cenint "
          + "    LEFT JOIN punticon p ON u.codein = p.codein "
          + "    WHERE p.numpun IS NOT NULL "
          + "    AND (t.pcodoc IS NOT NULL AND p.numpun = t.pcodoc) "
          + "    AND (p.orgricorso IS NULL OR (p.orgricorso IS NOT NULL AND p.orgricorso != '1')) "
          + "    UNION "
          + "    SELECT 0 AS numpun, u.codein, null AS orgricorso "
          + "    FROM tornFiltered t "
          + "    INNER JOIN uffint u ON u.codein = t.cenint "
          + "    UNION "
          + "    SELECT p.numpun, p.codein, p.orgricorso "
          + "    FROM tornFiltered t "
          + "    LEFT JOIN punticon p ON t.cenint = p.codein "
          + "    WHERE p.numpun IS NOT NULL "
          + "    AND p.orgricorso IS NOT NULL "
          + "    AND p.orgricorso = '1' "
          + "    AND t.orgricorso = p.numpun "
          + ") b "
          + "ORDER BY numpun ASC "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_ORGANISATIONS_OPERATORI),
          "select g.ditta, i.tipimp "
          + "from gare g "
          + "inner join impr i on i.codimp = g.ditta "
          + "where g.ditta is not null "
          + "and g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_ORGANISATIONS_OPERATORI_ACCORDO_QUADRO),
          "select d.dittao, i.tipimp "
          + "from ditgaq d "
          + "inner join gare g on g.ngara = d.ngara "
          + "inner join impr i on i.codimp = d.dittao "
          + "where g.esineg is null "
          + "and g.ditta is not null "
          + "and g.ngara = ? "
      );
      
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_FRAMEWORKAGREEMENT),
          "select aqoper, aqnumope from gare1 where ngara = ? "
      );
      
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT),
          "select coalesce(codiga,'1') from gare where ngara = ?"
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOTRESULT_1),
          "select codiga from gare where ngara = ?"
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_PLANNEDDURATION),
          "select g.teutil, g.temesi, t.accqua, t.aqdurata, t.aqtempo, g.dinlavg "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ?"
      );
      
      queriesMap.put(
          new GenereCodeKey(1, PathJsonEform.GR_PROCEDURE_SCOPE),
          "select valmax from v_gare_importi where codgar = ? and ngara is null "
      );
      
      queriesMap.put(
          new GenereCodeKey(3, PathJsonEform.GR_PROCEDURE_SCOPE),
          "select valmax from v_gare_importi where codgar = ? and ngara is null "
      );
      
      queriesMap.put(
          new GenereCodeKey(2, PathJsonEform.GR_PROCEDURE_SCOPE),
          "select valmax from v_gare_importi where codgar = ? "
      );
      queriesMap.put(
          new GenereCodeKey(10, PathJsonEform.GR_PROCEDURE_SCOPE),
          "select cast(null as numeric) from garealbo where codgar = ? "
      );
      queriesMap.put(
          new GenereCodeKey(11, PathJsonEform.GR_PROCEDURE_SCOPE),
          "select impavv from gareavvisi where codgar = ?"
      );
      
      
      
      
      
      queriesMap.put(
          new GenereCodeKey(100, PathJsonEform.GR_LOT_SCOPE),
          "select valmax from v_gare_importi where ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(2, PathJsonEform.GR_LOT_SCOPE),
          "select valmax from v_gare_importi where ngara = ? "
      );
      queriesMap.put(
          new GenereCodeKey(10, PathJsonEform.GR_LOT_SCOPE),
          "select cast(null as numeric) from garealbo where ngara = ? "
      );
      queriesMap.put(
          new GenereCodeKey(11, PathJsonEform.GR_LOT_SCOPE),
          "select impavv from gareavvisi where ngara = ?"
      );
      
      
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_DEADLINES1),
          "select "
          + "  t.dteoff, "
          + "  t.oteoff, "
          + "  case "
          + "    when t.iterga in (2, 4) then t.dtepar "
          + "    else null "
          + "  end as dtepar, "
          + "  case "
          + "    when t.iterga in (2, 4) then t.otepar "
          + "    else null "
          + "  end as otepar "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_DEADLINES1_RETTIFICA),
          "SELECT p.dteoff, p.oteoff, p.dtepar, p.otepar, p.desoff, p.oesoff "
          + "FROM g1nparet p "
          + "INNER JOIN g1npascheda s ON s.id = p.idnpascheda "
          + "WHERE s.uuid_eform = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_DEADLINE),
          "select t.riesame "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_DEADLINES2),
          "select t.dtepar, t.otepar "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(1, PathJsonEform.GR_PROCEDURE_PLACEOFPERFORMANCE),
          "select codnuts, CAST(null as VARCHAR(2)), CAST(null as VARCHAR(120)), CAST(null as VARCHAR(500)), CAST(null as NUMERIC(7)) "
          + "from torn "
          + "where codgar = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(3, PathJsonEform.GR_PROCEDURE_PLACEOFPERFORMANCE),
          "select t.codnuts, g.prosla, g.loclav, g.nomssl, g1.locnaz "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 and g.ngara = t.codgar "
          + "inner join gare1 g1 on g1.ngara = g.ngara "
          + "where g.codgar1 = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(2, PathJsonEform.GR_PROCEDURE_PLACEOFPERFORMANCE),
          "select t.codnuts, g.prosla, g.loclav, g.nomssl, g1.locnaz "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "inner join gare1 g1 on g1.ngara = g.ngara "
          + "where g.codgar1 = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(10, PathJsonEform.GR_PROCEDURE_PLACEOFPERFORMANCE),
          "select codnuts, CAST(null as VARCHAR(2)), CAST(null as VARCHAR(120)), CAST(null as VARCHAR(500)), CAST(null as NUMERIC(7)) "
          + "from torn "
          + "where codgar = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(11, PathJsonEform.GR_PROCEDURE_PLACEOFPERFORMANCE),
          "select codnuts, CAST(null as VARCHAR(2)), CAST(null as VARCHAR(120)), CAST(null as VARCHAR(500)), CAST(null as NUMERIC(7)) "
          + "from torn "
          + "where codgar = ? "
      );
      
      
      queriesMap.put(
          new GenereCodeKey(100, PathJsonEform.GR_LOT_PLACEOFPERFORMANCE),
          "select t.codnuts, g.prosla, g.loclav, g.nomssl, g1.locnaz "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "inner join gare1 g1 on g1.ngara = g.ngara "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(2, PathJsonEform.GR_LOT_PLACEOFPERFORMANCE),
          "select t.codnuts, g.prosla, g.loclav, g.nomssl, g1.locnaz "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "inner join gare1 g1 on g1.ngara = g.ngara "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(10, PathJsonEform.GR_LOT_PLACEOFPERFORMANCE),
          "select t.codnuts, CAST(null as VARCHAR(2)), CAST(null as VARCHAR(120)), CAST(null as VARCHAR(500)), CAST(null as NUMERIC(7)) "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(11, PathJsonEform.GR_LOT_PLACEOFPERFORMANCE),
          "select t.codnuts, CAST(null as VARCHAR(2)), CAST(null as VARCHAR(120)), CAST(null as VARCHAR(500)), CAST(null as NUMERIC(7)) "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_AWARDCRITERIA_CRITERION),
          "select g.modlicg, go.tippar, go.despar, go.maxpun, go.minpun "
          + "from gare g "
          + "left join goev go on go.ngara = g.ngara and go.livpar != 2 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_PROCUREMENTTYPE_STRATEGIC),
          "select t.isgreen, t.isrecycle, t.desgreen "
          + "from gare g "
          + "inner join torn t on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_SELECTIONCRITERIA),
          "select t.selope "
          + "from gare g "
          + "inner join torn t on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_TENDERRECEIPT),
          "select t.pcooff, t.pcopre, t.cenint, t.iterga "
          + "from gare g "
          + "inner join torn t on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_ADDINFO),
          "select t.pcodoc, t.cenint "
          + "from gare g "
          + "inner join torn t on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_WINNER),
          "select g.esineg, g1.giustneg "
          + "from gare g "
          + "inner join gare1 g1 on g1.ngara = g.ngara "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_NOTICERESULT_SECTION),
          "select sum(iaggiu) from gare where codgar1 = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOTTENDER_SUBCONTRACTING),
          "select ricsub from ditg where ngara5 = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_TENDER),
          "select g.iaggiu, d.numordpl "
          + "from ditg d "
          + "inner join gare g on g.ngara = d.ngara5 and g.codgar1 = d.codgar5 "
          + "where g.esineg is null and d.invoff = '1' "
          + "and g.ditta is not null "
          + "and g.ngara = ? "
          + "and d.dittao = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_TENDER_ACCORDO_QUADRO),
          "select da.iaggiu, d.numordpl "
          + "from ditg d "
          + "inner join gare g on g.ngara = d.ngara5 and g.codgar1 = d.codgar5 "
          + "inner join ditgaq da on da.ngara = d.ngara5 and da.dittao = d.dittao "
          + "where g.esineg is null and d.invoff = '1' "
          + "and g.ditta is not null "
          + "and g.ngara = ? "
          + "and d.dittao = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOTRESULT_RECEIVEDSUBMISSIONS),
          "select count(*) "
          + "from ditg d "
          + "inner join gare g on g.ngara = d.ngara5 and g.codgar1 = d.codgar5 "
          + "where g.esineg is null and d.invoff = '1' "
          + "and g.ngara = ? "
      );
      
      /* ci servira successivamente per i nuovi campi
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOTTENDER),
          "select d.codgar5, d.dittao, d.ngara5 "
          + "from ditg d "
          + "inner join gare g on g.ngara = d.ngara5 and g.codgar1 = d.codgar5 "
          + "where g.esineg is null and d.invoff = '1' "
          + "and g.ngara = ? "
      );
      */
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOTTENDER_SUBCONTRACTING),
          "select d.ricsub "
          + "from ditg d "
          + "inner join gare g on g.ngara = d.ngara5 and g.codgar1 = d.codgar5 "
          + "where g.esineg is null and d.invoff = '1' "
          + "and g.ditta is not null "
          + "and g.ngara = ? "
          + "and d.dittao = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOTTENDER),
          "select d.dittao, i.tipimp "
          + "from ditg d "
          + "inner join gare g on g.ngara = d.ngara5 and g.codgar1 = d.codgar5 "
          + "inner join impr i on i.codimp = g.ditta "
          + "where g.esineg is null and d.invoff = '1' "
          + "and g.ditta is not null "
          + "and g.ditta = d.dittao "
          + "and g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOTTENDER_ACCORDO_QUADRO),
          "select d.dittao, i.tipimp "
          + "from ditgaq d "
          + "inner join gare g on g.ngara = d.ngara "
          + "inner join impr i on i.codimp = d.dittao "
          + "where g.esineg is null "
          + "and g.ditta is not null "
          + "and g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_SETTLEDCONTRACTTENDERREFERENCE),
          "select d.dittao "
          + "from ditg d "
          + "inner join gare g on g.ngara = d.ngara5 and g.codgar1 = d.codgar5 "
          + "where g.esineg is null and d.invoff = '1' "
          + "and g.ditta is not null "
          + "and g.ditta = d.dittao "
          + "and g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_SETTLEDCONTRACTTENDERREFERENCE_ACCORDO_QUADRO),
          "select d.dittao "
            + "from ditgaq d "
            + "inner join gare g on g.ngara = d.ngara "
            + "where g.esineg is null "
            + "and g.ditta is not null "
            + "and g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOTRESULT_TENDER_ID_REF),
          "select d.dittao "
          + "from ditg d "
          + "inner join gare g on g.ngara = d.ngara5 and g.codgar1 = d.codgar5 "
          + "where g.esineg is null and d.invoff = '1' "
          + "and g.ditta is not null "
          + "and g.ditta = d.dittao "
          + "and g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOTRESULT_TENDER_ID_REF_ACCORDO_QUADRO),
          "select d.dittao "
            + "from ditgaq d "
            + "inner join gare g on g.ngara = d.ngara "
            + "where g.esineg is null "
            + "and g.ditta is not null "
            + "and g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_TENDERINGPARTY),
          "select g.ditta, i.nomest, i.tipimp "
          + "from gare g "
          + "inner join impr i on i.codimp = g.ditta "
          + "where g.ditta is not null "
          + "and g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_TENDERINGPARTY_ACCORDO_QUADRO),
          "select d.dittao, i.nomest, i.tipimp "
          + "from ditgaq d "
          + "inner join gare g on g.ngara = d.ngara "
          + "inner join impr i on i.codimp = d.dittao "
          + "where g.esineg is null "
          + "and g.ditta is not null "
          + "and g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_SETTLEDCONTRACT_1),
          "select max(a.dattoa), a.daatto, a.nrepat, a.accqua "
          + "from ( "
          + "select ga.dattoa, ga.daatto, ga.nrepat, t.accqua "
          + "from gare ga, garecont gc, torn t "
          + "where gc.codimp = ga.ditta "
          + "and ((gc.ngara=ga.ngara and gc.ncont=1) or "
          + "    (gc.ngara=ga.codgar1 "
          + "        and (gc.ngaral is null or gc.ngaral=ga.ngara))) "
          + "and ga.ditta is not null "
          + "and ga.codgar1 = t.codgar "
          + "and gc.ngara = ? "
          + "and gc.ncont = ? "
          + "group by ga.dattoa, ga.daatto, ga.nrepat, t.accqua "
          + ") a "
          + "group by a.daatto, a.nrepat, a.accqua "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_PROCEDURE_DIRECTAWARD),
          "select motaggidir "
          + "from torn "
          + "where codgar = ?"
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOTRESULT_CONTRACT_ID_REF),
          "select gc.ngara, gc.ncont "
          + "from gare ga, garecont gc "
          + "where gc.codimp = ga.ditta "
          + "and ((gc.ngara=ga.ngara and gc.ncont=1) or "
          + "     (gc.ngara=ga.codgar1 "
          + "      and (gc.ngaral is null or gc.ngaral=ga.ngara))) "
          + "and ga.ditta is not null "
          + "and ga.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_CONTRACTEXTENSION),
          "select numrin, ammopz, desopz, ammrin, desrin from gare1 where ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_RESERVEDEXECUTION),
          "select esecris from gare1 where ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_POSTAWARDPROCESS),
          "select t.ordiele "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(0, PathJsonEform.GR_LOT_TENDERVALIDITY),
          "select t.valoff "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      
      //GR_PART solo per genere 11
      queriesMap.put(
          new GenereCodeKey(11, PathJsonEform.GR_PART_DESCRIPTION),
          "select OGGETTO, TIPOAPP from GAREAVVISI where ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(11, PathJsonEform.GR_PART_SCOPE_MAINCLASSIFICATION),
          "select codcpv from GARCPV where tipcpv = '1' and ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(11, PathJsonEform.GR_PART_SCOPE_ADDITIONALCLASSIFICATION),
          "select codcpv from GARCPV where tipcpv = '2' and ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(11, PathJsonEform.GR_PART_REVIEWORG),
          "select p.numpun, p.codein "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "inner join punticon p on t.cenint = p.codein "
          + "where p.numpun IS NOT NULL "
          + "and p.orgricorso IS NOT NULL "
          + "and t.orgricorso = p.numpun "
          + "and p.orgricorso = '1' "
          + "and g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(11, PathJsonEform.GR_PART_SCOPE),
          "select impavv from gareavvisi where ngara = ?"
      );
      
      queriesMap.put(
          new GenereCodeKey(11, PathJsonEform.GR_PART_FRAMEWORKAGREEMENT),
          "select t.accqua "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(11, PathJsonEform.GR_PART_DPS),
          "select t.issda "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
      queriesMap.put(
          new GenereCodeKey(11, PathJsonEform.GR_PART_PLACEOFPERFORMANCE),
          "select t.codnuts "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "where g.ngara = ? "
      );
      
        // Esempio: genere = 1 e code = "GR-Procedure"
      /*
        queriesMap.put(
            new GenereCodeKey(1, PathJsonEform.GR_PROCEDURE),
            "SELECT /*campi FROM torn, v_gare_importi WHERE torn.codgar = ? AND v_gare_importi.codgar = torn.codgar AND ...;"
        );
      */
        // ... aggiungere qui gli altri (genere, code) necessari
    }

    // Costruttore privato per evitare istanziazioni
    private QueryRegistry() {
    }

    /**
     * Ritorna la query SQL associata alla coppia (genere, code).
     * @throws IllegalArgumentException se non esiste una query definita per quella coppia.
     */
    public static String getQuery(int genere, PathJsonEform code) {
        GenereCodeKey key = new GenereCodeKey(genere, code);
        String query = queriesMap.get(key);
        if (query == null) {
            throw new IllegalArgumentException(
                "Nessuna query definita per genere=" + genere + " e code=" + code
            );
        }
        return query;
    }
}
