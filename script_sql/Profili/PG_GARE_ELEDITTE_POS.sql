--SET search_path = public, pg_catalog;

SET statement_timeout = 0;
-- decommentare la riga seguente in caso di lancio da interprete di comando direttamente da linux
--SET client_encoding = 'LATIN1';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

-- inserimento associativa all'utente MANAGER
Insert into W_ACCPRO (ID_ACCOUNT,COD_PROFILO) select 48,'PG_GARE_ELEDITTE' from W_GENCHIAVI where TABELLA='W_GRUPPI' and not exists (select * from W_PROFILI where cod_profilo = 'PG_GARE_ELEDITTE');
Insert into W_ACCPRO (ID_ACCOUNT,COD_PROFILO) select 49,'PG_GARE_ELEDITTE' from W_GENCHIAVI where TABELLA='W_GRUPPI' and not exists (select * from W_PROFILI where cod_profilo = 'PG_GARE_ELEDITTE');
Insert into W_ACCPRO (ID_ACCOUNT,COD_PROFILO) select 50,'PG_GARE_ELEDITTE' from W_GENCHIAVI where TABELLA='W_GRUPPI' and not exists (select * from W_PROFILI where cod_profilo = 'PG_GARE_ELEDITTE');
-- inserimento eventuale gruppo
Update W_GENCHIAVI Set CHIAVE=CHIAVE+1 where TABELLA='W_GRUPPI' and not exists (select 1 from W_GRUPPI where cod_profilo='PG_GARE_ELEDITTE');
Insert into W_GRUPPI (ID_GRUPPO,NOME,DESCR,COD_PROFILO) Select (select CHIAVE from W_GENCHIAVI where TABELLA='W_GRUPPI'),'Elenchi operatori economici','default per il profilo PG_GARE_ELEDITTE','PG_GARE_ELEDITTE' from W_GENCHIAVI where tabella='W_GRUPPI' and not exists (select * from W_GRUPPI where cod_profilo='PG_GARE_ELEDITTE');
-- inserimento associazione (utente,gruppo)
Insert into W_ACCGRP (ID_ACCOUNT,ID_GRUPPO,PRIORITA) select 48,ID_GRUPPO,0 from W_GRUPPI where COD_PROFILO='PG_GARE_ELEDITTE'  and not exists (select * from W_ACCGRP A inner join W_GRUPPI G on A.id_gruppo=G.id_gruppo where G.cod_profilo='PG_GARE_ELEDITTE' and A.id_account=48);
Insert into W_ACCGRP (ID_ACCOUNT,ID_GRUPPO,PRIORITA) select 49,ID_GRUPPO,0 from W_GRUPPI where COD_PROFILO='PG_GARE_ELEDITTE'  and not exists (select * from W_ACCGRP A inner join W_GRUPPI G on A.id_gruppo=G.id_gruppo where G.cod_profilo='PG_GARE_ELEDITTE' and A.id_account=49);
Insert into W_ACCGRP (ID_ACCOUNT,ID_GRUPPO,PRIORITA) select 50,ID_GRUPPO,0 from W_GRUPPI where COD_PROFILO='PG_GARE_ELEDITTE'  and not exists (select * from W_ACCGRP A inner join W_GRUPPI G on A.id_gruppo=G.id_gruppo where G.cod_profilo='PG_GARE_ELEDITTE' and A.id_account=50);

-- pulizia profilo eseguita sempre
Delete from W_PROAZI where COD_PROFILO='PG_GARE_ELEDITTE';
Delete from W_PROFILI where COD_PROFILO='PG_GARE_ELEDITTE';

-- inserimento (o reinserimento) testata e configurazione profilo
INSERT INTO W_PROFILI (COD_PROFILO,CODAPP,NOME,DESCRIZIONE,FLAG_INTERNO,DISCRIMINANTE,COD_CLIENTE,CRC) VALUES ('PG_GARE_ELEDITTE','PG','Elenchi operatori economici','Sistema per l''iscrizione degli operatori economici agli elenchi per categorie SOA o merceologiche (o albi fornitori), qualificazione e selezione degli operatori',1,NULL,0,3686066284);

INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','MOD','GARE.DOCUMGARA.BUSTA',0,2610374576);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','VIS','GARE.DOCUMGARA.BUSTA',0,1326173024);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','MOD','GARE.DOCUMGARA.FASGAR',0,1179545152);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','MOD','GARE.PUBBTERM.DSORTEGGIO',0,2000460712);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','MOD','GARE.PUBBTERM.NPUBAVVBAN',0,4127419470);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','MOD','GARE.PUBBTERM.OSORTEGGIO',0,1411194092);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','MOD','GARE.V_DITGAMMIS.FASGAR',0,3430308729);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','VIS','*',1,4179291998);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','VIS','GARE.DOCUMGARA.FASGAR',0,3226017912);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','VIS','GARE.GARE.CLAVOR',0,1810123361);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','VIS','GARE.GARE.CODCOM',0,2400001676);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','VIS','GARE.GARE.NUMERA',0,3701939251);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','VIS','GARE.GARSTR.*',0,32615009);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','VIS','GARE.PUBBTERM.DSORTEGGIO',0,2916800558);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','VIS','GARE.PUBBTERM.NPUBAVVBAN',0,753217480);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','VIS','GARE.PUBBTERM.OSORTEGGIO',0,2398838634);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','VIS','GARE.V_DITGAMMIS.FASGAR',0,1250757906);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','COLS','VIS','GARE.V_GARE_TORN.ISLOTTI',0,2877818399);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','FUNZ','VIS','ALT.GARE.GARE-scheda.PUBBANDO.InsertPredefiniti',0,1724237528);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','FUNZ','VIS','ALT.GARE.GARE-scheda.PUBBANDO.PubblicaSuPortale',1,46146152);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','FUNZ','VIS','ALT.GARE.GARE.AssociaGaraAppalto',0,3149509144);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','FUNZ','VIS','ALT.GARE.GARE.InviaComunicazioni',1,641164644);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','FUNZ','VIS','ALT.GARE.TORN-OFFUNICA-scheda.PUBBANDO.PubblicaSuPortale',1,106736653);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','FUNZ','VIS','ALT.GARE.TORN-scheda.PUBBANDO.PubblicaSuPortale',1,3659309325);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','FUNZ','VIS','ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare',1,1928126868);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','FUNZ','VIS','DEL.GARE.TORN-scheda.LOTTI.LISTADELSEL',0,530712220);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','FUNZ','VIS','INS.GARE.GARE-scheda.ALTRIDATI.INS-TRSTRADA',0,1103018212);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','FUNZ','VIS','INS.GARE.V_GARE_TORN-trova.TROVANUOVO',0,4178177032);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','MASC','VIS','GARE.V_GARE_TORN-lista',0,3390890272);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','MENU','VIS','LAVORI',0,295892177);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','PAGE','VIS','GARE.GARE-scheda.CRITERIELEDITTE',1,2845314038);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','SEZ','VIS','GARE.GARE-scheda.ALTRIDATI.TRSTRADA',0,3349940915);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','SEZ','VIS','GARE.GARE-scheda.DATIGEN.RILA',0,41193655);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','SUBMENU','VIS','ARCHIVI.Archivio-uffici-intestatari',1,3519115947);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','SUBMENU','VIS','ARCHIVI.Archivio-utenti',0,3100152466);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','SUBMENU','VIS','GARE.Trova-elenchi',1,802567953);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','SUBMENU','VIS','GARE.Trova-gare',0,2624564260);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','TABS','VIS','*',1,1947117961);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','TABS','VIS','GARE.GARSTR',0,718659715);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','TABS','VIS','GARE.V_DITTE_PRIT',0,2225114875);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE','TABS','VIS','GARE.V_GARE_NSCAD',0,762248012);