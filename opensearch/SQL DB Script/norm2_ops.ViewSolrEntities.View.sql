USE [Idap]
GO
/****** Object:  View [norm2_ops].[ViewSolrEntities]    Script Date: 4/25/2022 5:02:32 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO





CREATE VIEW [norm2_ops].[ViewSolrEntities]
--select * from [norm2_ops].[ViewSolrEntities] where cik=6769 --Status Indeterminate
AS
SELECT a.cik AS id
	,dbo.sZeroPad(a.cik, 10) AS cik
	,b.otherCiks AS altCik
	,a.companyName
	,a.tradingSymbol
	,a.entityId
	,a.sic
	,(
		SELECT division
		FROM dbo.MapSic4Division
		WHERE (
				a.sic >= sicFrom
				AND a.sic < sicTo
				)
		) AS division
	,d.sector AS sector
	,c.sDescription AS industry
	,a.geoState
	,a.filerCategory
	,ef.earliestFilingDate
	,a.latestFilingDate
 
FROM (
	SELECT cik
		,filerCategory
		,ticker AS tradingSymbol
		,name AS companyName
		,sic
		,bastpr AS geoState
		,dbo.sZeroPad(cik, 10) AS entityId
		,latestFilingDate
 
	FROM (
		SELECT cik
			,ticker
			,name
			,sic
			,bastpr
			 ,filed AS latestFilingDate
			--,fy AS latestFY
			--,fp AS latestFQ
			,ROW_NUMBER() OVER (
				PARTITION BY cik ORDER BY filed DESC
				) r
			,(norm1.SubCache.afs) AS filerCategory
		FROM norm1.SubCache
		) s
	WHERE 1 = 1
		AND r = 1
	) a
LEFT JOIN (
	SELECT cik
		,earliestFilingDate
 
	FROM (
		SELECT cik
			,filed AS earliestFilingDate
			--,fy AS earliestFY
			--,fp AS earliestFQ
			,ROW_NUMBER() OVER (
				PARTITION BY cik ORDER BY filed ASC
				) r
		FROM norm1.SubCache
		) s
	WHERE 1 = 1
		AND r = 1
	) AS ef ON a.cik = ef.cik
LEFT JOIN (
	SELECT cik
		,MAX(otherCiks) AS otherCiks
	FROM [raw0].[Instance]
	WHERE otherCiks <> ''
	GROUP BY cik
	) b ON a.cik = b.cik
LEFT JOIN dbo.Sic4Map c ON a.sic = c.sSIC
LEFT JOIN [dbo].[MapSic2Sector] d ON LEFT(a.sic, 2) = d.sector_code
/*










     SELECT   a.cik as id,
	 dbo.sZeroPad(a.cik, 10) AS cik,
              b.otherCiks AS altCik,
              a.companyName,
              a.tradingSymbol,
              a.entityId,
              a.sic,
     (
         SELECT division
         FROM   dbo.MapSic4Division
         WHERE  (a.sic >= sicFrom AND a.sic < sicTo)
     ) AS division,
              d.sector AS sector,
              c.sDescription AS industry,
              a.geoState,
              a.filerCategory
     FROM
     (
/*
	 SELECT norm1.SubCache.cik AS cik,
                MAX(norm1.SubCache.name) AS companyName,
			  --em.[Cik2Ticker](norm1.SubCache.cik) AS tradingSymbol,
			max( norm1.SubCache.ticker )as tradingSymbol, --8/16 get it from subCache
                dbo.sZeroPad(cik, 10) AS entityId,
                MAX(norm1.SubCache.sic) AS sic,
                MAX(norm1.SubCache.bastpr) AS geoState
			   ,MAX(norm1.SubCache.afs) AS filerCategory
         FROM   norm1.SubCache  
		 WHERE  cik=6769
		 GROUP BY cik  
		
*/

 
	SELECT cik,filerCategory,  ticker AS tradingSymbol,name AS companyName,sic,bastpr AS geoState, dbo.sZeroPad(cik, 10) AS entityId 
	FROM (
		SELECT cik  ,ticker,name,sic,bastpr
			,ROW_NUMBER() OVER (
				PARTITION BY cik ORDER BY filed DESC
				) r
			,(norm1.SubCache.afs) AS filerCategory
		FROM norm1.SubCache
		) s
	WHERE 1 = 1
		AND r = 1
 
--WHERE sc.cik=6769 /*APACHE CORP*/
	 
     ) a
     LEFT JOIN
     (
         SELECT cik,
                MAX(otherCiks) AS otherCiks
         FROM   [raw0].[Instance]
         WHERE  otherCiks <> ''
         GROUP BY cik
     ) b ON a.cik = b.cik
     LEFT JOIN dbo.Sic4Map c ON a.sic = c.sSIC
     LEFT JOIN [dbo].[MapSic2Sector] d ON LEFT(a.sic, 2) = d.sector_code 
 --where  a.cik =0001500198---a.cik=6769


*/
GO
