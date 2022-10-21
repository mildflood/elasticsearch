USE [Idap]
GO

/****** Object:  View [norm2_ops].[ViewSolrMDASearch45]    Script Date: 12/9/2020 1:39:25 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE view [norm2_ops].[ViewSolrMDASearch45] as
select sub_cik, sub_name,sub_ticker,sub_sicdetail,office,sub_fstat,sub_egc,sub_esb,sub_esc,sub_inccountry,sub_form,sub_filed,sub_fy, sub_fp,sub_adsh,sub_act_standard, 
sub_adsh as id 
,sub_detail
,sub_isInline
from norm2_ops.ViewSolrFilingSearch45
--where sub_form like '10-K%' and sub_fy in (2018,2019,2020) AND office in('Office of Technology') --11.17.2020
where  (sub_form like'%10-K%' OR  sub_form like'%10-Q%' OR sub_form like'%S-%' OR  sub_form like'%20-F%' OR  sub_form like'%F-%') AND sub_filed>20171231 --order by sub_cik, sub_filed desc



GO


select top 1000 'https://www.sec.gov/Archives/edgar/data/'+convert(varchar(10),sub_cik)+'/'+REPLACE(sub_adsh,'-', '') +'/'+sub_adsh +'.txt' url from [norm2_ops].[ViewSolrMDASearch45]