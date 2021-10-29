--SET search_path = public, pg_catalog;

SET statement_timeout = 0;
-- decommentare la riga seguente in caso di lancio da interprete di comando direttamente da linux
--SET client_encoding = 'LATIN1';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

-- inserimento associazione (utente,profilo)
Insert into W_ACCPRO (ID_ACCOUNT,COD_PROFILO) select 48,'PG_GARE' from W_GENCHIAVI where TABELLA='W_GRUPPI' and not exists (select * from W_PROFILI where cod_profilo = 'PG_GARE');
Insert into W_ACCPRO (ID_ACCOUNT,COD_PROFILO) select 49,'PG_GARE' from W_GENCHIAVI where TABELLA='W_GRUPPI' and not exists (select * from W_PROFILI where cod_profilo = 'PG_GARE');
Insert into W_ACCPRO (ID_ACCOUNT,COD_PROFILO) select 50,'PG_GARE' from W_GENCHIAVI where TABELLA='W_GRUPPI' and not exists (select * from W_PROFILI where cod_profilo = 'PG_GARE');
-- inserimento eventuale gruppo
Update W_GENCHIAVI Set CHIAVE=CHIAVE+1 where TABELLA='W_GRUPPI' and not exists (select 1 from W_GRUPPI where cod_profilo='PG_GARE');
Insert into W_GRUPPI (ID_GRUPPO,NOME,DESCR,COD_PROFILO) Select (select CHIAVE from W_GENCHIAVI where TABELLA='W_GRUPPI'),'Gare e procedure di affidamento','default per il profilo PG_GARE','PG_GARE' from W_GENCHIAVI where tabella='W_GRUPPI' and not exists (select * from W_GRUPPI where cod_profilo='PG_GARE');
-- inserimento associazione (utente,gruppo)
Insert into W_ACCGRP (ID_ACCOUNT,ID_GRUPPO,PRIORITA) select 48,ID_GRUPPO,0 from W_GRUPPI where COD_PROFILO='PG_GARE'  and not exists (select * from W_ACCGRP A inner join W_GRUPPI G on A.id_gruppo=G.id_gruppo where G.cod_profilo='PG_GARE' and A.id_account=48);
Insert into W_ACCGRP (ID_ACCOUNT,ID_GRUPPO,PRIORITA) select 49,ID_GRUPPO,0 from W_GRUPPI where COD_PROFILO='PG_GARE'  and not exists (select * from W_ACCGRP A inner join W_GRUPPI G on A.id_gruppo=G.id_gruppo where G.cod_profilo='PG_GARE' and A.id_account=49);
Insert into W_ACCGRP (ID_ACCOUNT,ID_GRUPPO,PRIORITA) select 50,ID_GRUPPO,0 from W_GRUPPI where COD_PROFILO='PG_GARE'  and not exists (select * from W_ACCGRP A inner join W_GRUPPI G on A.id_gruppo=G.id_gruppo where G.cod_profilo='PG_GARE' and A.id_account=50);

-- pulizia profilo eseguita sempre
Delete from W_PROAZI where COD_PROFILO='PG_GARE';
Delete from W_PROFILI where COD_PROFILO='PG_GARE';

-- inserimento (o reinserimento) testata e configurazione profilo
INSERT INTO W_PROFILI (COD_PROFILO,CODAPP,NOME,DESCRIZIONE,FLAG_INTERNO,DISCRIMINANTE,COD_CLIENTE,CRC) VALUES ('PG_GARE','PG','Gare e procedure di affidamento','Gestione completa di tutti i dati e tutte le funzionalitą per l''espletamento delle procedure di affidamento',1,null,0,1374775406);

INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','COLS','VIS','*',1,4179291998);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','COLS','VIS','GARE.GARE.APPLEGREGG',0,1492670541);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','COLS','VIS','GARE.GARE.CODCOM',0,2400001676);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','COLS','VIS','GARE.GARE.SICINC',1,189868548);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','COLS','VIS','GARE.GARSTR.*',0,32615009);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','COLS','MOD','GARE.TORN.APPLEGREG',0,2580938351);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','COLS','VIS','GARE.TORN.APPLEGREG',0,3188313100);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','COLS','VIS','GARE.V_GARE_TORN.ISLOTTI',0,2877818399);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','COLS','MOD','LAVO.APPA.CODCUA',0,2255588908);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','COLS','VIS','LAVO.APPA.CODCUA',0,110420766);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','FUNZ','VIS','ALT.GARE.AcquisisciAggiornamentiPortale',1,3326380055);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','FUNZ','VIS','ALT.GARE.GARE-scheda.PUBBANDO.PubblicaSuPortale',1,46146152);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','FUNZ','VIS','ALT.GARE.GARE-scheda.PUBESITO.PubblicaSuPortale',1,4043402262);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','FUNZ','VIS','ALT.GARE.GARE.InviaComunicazioni',1,641164644);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','FUNZ','VIS','ALT.GARE.RichiestaCIG',1,2819618578);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','FUNZ','VIS','ALT.GARE.RichiestaCIG.GestioneRUPCentriCosto',1,1837126338);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','FUNZ','VIS','ALT.GARE.TORN-OFFUNICA-scheda.PUBBANDO.PubblicaSuPortale',1,106736653);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','FUNZ','VIS','ALT.GARE.TORN-OFFUNICA-scheda.PUBESITO.PubblicaSuPortale',1,4120765043);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','FUNZ','VIS','ALT.GARE.TORN-scheda.PUBBANDO.PubblicaSuPortale',1,3659309325);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','FUNZ','VIS','DEL.GARE.TORN-scheda.LOTTI.LISTADELSEL',0,530712220);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','FUNZ','VIS','INS.GARE.GARE-scheda.ALTRIDATI.INS-TRSTRADA',0,1103018212);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','FUNZ','VIS','INS.W3.W3AZIENDAUFFICIO-lista-popup.nuovo',0,1528018326);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','FUNZ','VIS','INS.W3.W3USRSYS-Scheda.INS-W3AZIENDAUFFICIO',0,389692718);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','FUNZ','VIS','INS.W3.W3USRSYS-Scheda.SCHEDANUOVO',0,1827014000);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','MENU','VIS','LAVORI',0,295892177);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','PAGE','VIS','GARE.GARE-scheda.CRITERIELEDITTE',0,3734977376);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','SEZ','VIS','GARE.GARE-scheda.ALTRIDATI.TRSTRADA',0,3349940915);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','SUBMENU','VIS','ARCHIVI.Archivio-uffici-intestatari',1,3519115947);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','SUBMENU','VIS','ARCHIVI.Archivio-utenti',0,3100152466);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','SUBMENU','VIS','STRUMENTI.GareContrattiAdempimenti',1,265197075);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','TABS','VIS','*',1,1947117961);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','TABS','VIS','GARE.GARSTR',0,718659715);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','TABS','VIS','GARE.V_DITTE_PRIT',0,2225114875);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE','TABS','VIS','GARE.V_GARE_NSCAD',0,762248012);
