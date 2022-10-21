USE [Idap]
GO
/****** Object:  Table [dbo].[MapSic2Sector]    Script Date: 4/25/2022 7:44:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[MapSic2Sector](
	[sector_code] [varchar](2) NOT NULL,
	[sector] [varchar](200) NOT NULL,
 CONSTRAINT [PK_MapSic2Sector] PRIMARY KEY CLUSTERED 
(
	[sector_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[MapSic4Division]    Script Date: 4/25/2022 7:44:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[MapSic4Division](
	[sicFrom] [int] NOT NULL,
	[sicTo] [int] NOT NULL,
	[division] [varchar](200) NOT NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Sic4Map]    Script Date: 4/25/2022 7:44:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Sic4Map](
	[sSIC] [char](4) NOT NULL,
	[sAD] [varchar](50) NOT NULL,
	[sDescription] [varchar](max) NULL,
	[iFFI49] [smallint] NULL,
 CONSTRAINT [PK_Sic4Map] PRIMARY KEY CLUSTERED 
(
	[sSIC] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [norm1].[SubCache]    Script Date: 4/25/2022 7:44:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [norm1].[SubCache](
	[adsh] [char](20) NOT NULL,
	[cik] [bigint] NULL,
	[name] [varchar](160) NULL,
	[form] [varchar](20) NULL,
	[period] [char](8) NULL,
	[fy] [char](4) NULL,
	[fp] [char](2) NULL,
	[fyend] [char](4) NULL,
	[filed] [char](8) NULL,
	[accepted] [smalldatetime] NULL,
	[sic] [char](4) NULL,
	[bacity] [varchar](30) NULL,
	[bastpr] [char](2) NULL,
	[bacountry] [char](2) NULL,
	[bazip] [varchar](10) NULL,
	[bas1] [varchar](40) NULL,
	[bas2] [varchar](40) NULL,
	[baph] [varchar](20) NULL,
	[macity] [varchar](30) NULL,
	[mastpr] [char](2) NULL,
	[macountry] [char](2) NULL,
	[mazip] [varchar](12) NULL,
	[mas1] [varchar](40) NULL,
	[mas2] [varchar](40) NULL,
	[incstpr] [char](2) NULL,
	[inccountry] [char](2) NULL,
	[irsnumber] [char](9) NULL,
	[lei] [char](20) NULL,
	[prevrpt] [tinyint] NOT NULL,
	[detail] [tinyint] NOT NULL,
	[instance] [varchar](32) NULL,
	[afs] [varchar](50) NULL,
	[fstat] [char](5) NULL,
	[wksi] [tinyint] NOT NULL,
	[nciks] [smallint] NOT NULL,
	[aciks] [varchar](max) NULL,
	[former] [varchar](150) NULL,
	[changed] [char](8) NULL,
	[pubfloatusd] [decimal](28, 4) NULL,
	[floatdate] [char](8) NULL,
	[floataxis] [varchar](255) NULL,
	[floatmems] [tinyint] NULL,
	[ticker] [varchar](50) NULL,
	[otickers] [bit] NULL,
	[secad] [varchar](10) NULL,
	[xbrl_fye] [char](4) NULL,
	[filing_fye] [char](4) NULL,
	[vendor] [varchar](50) NULL,
	[bureau] [varchar](50) NULL,
	[isInline] [bit] NULL,
	[cust_pct] [numeric](14, 7) NULL,
	[act_standard] [varchar](30) NULL,
	[entitySmallBusiness] [varchar](10) NULL,
	[entityEmergingGrowthCompany] [varchar](10) NULL,
	[entityShellCompany] [varchar](10) NULL,
	[BS] [varchar](30) NULL,
	[IS] [varchar](30) NULL,
	[EQ] [varchar](30) NULL,
	[CF] [varchar](30) NULL,
	[CI] [varchar](30) NULL,
	[FN] [varchar](30) NULL,
	[stockExchange] [varchar](100) NULL,
	[country] [varchar](300) NULL,
 CONSTRAINT [PK_norm1_SubCache] UNIQUE CLUSTERED 
(
	[adsh] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [ops1].[IdapRole]    Script Date: 4/25/2022 7:44:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [ops1].[IdapRole](
	[idapRoleId] [varchar](20) NOT NULL,
	[description] [varchar](2000) NULL,
PRIMARY KEY CLUSTERED 
(
	[idapRoleId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [ops1].[IdapUser]    Script Date: 4/25/2022 7:44:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [ops1].[IdapUser](
	[idapUserId] [varchar](20) NOT NULL,
	[firstName] [varchar](100) NULL,
	[lastName] [varchar](100) NULL,
PRIMARY KEY CLUSTERED 
(
	[idapUserId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [ops1].[Profile]    Script Date: 4/25/2022 7:44:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [ops1].[Profile](
	[profileId] [varchar](20) NOT NULL,
	[name] [varchar](max) NOT NULL,
	[description] [varchar](max) NULL,
	[sortOrder] [varchar](10) NULL,
	[environmentId] [varchar](20) NULL,
PRIMARY KEY CLUSTERED 
(
	[profileId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [ops1].[Property]    Script Date: 4/25/2022 7:44:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [ops1].[Property](
	[propertyId] [int] IDENTITY(1,1) NOT NULL,
	[profileId] [varchar](20) NULL,
	[propertyKey] [varchar](800) NOT NULL,
	[propertyValue] [varchar](max) NOT NULL,
	[info] [varchar](max) NULL,
	[dtStart] [datetime] NOT NULL DEFAULT (getdate()),
	[dtEnd] [datetime] NOT NULL DEFAULT (CONVERT([datetime],'12/31/9999 23:59:59.997',(0))),
	[dtCreated] [datetime] NULL DEFAULT (getdate()),
	[dtUpdated] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[propertyId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [uq_profileId_propertyKey] UNIQUE NONCLUSTERED 
(
	[profileId] ASC,
	[propertyKey] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [ops1].[UserRole]    Script Date: 4/25/2022 7:44:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [ops1].[UserRole](
	[userRoleId] [int] IDENTITY(1,1) NOT NULL,
	[idapUserId] [varchar](20) NULL,
	[idapRoleId] [varchar](20) NULL,
	[profileId] [varchar](20) NULL,
	[dtStart] [datetime] NOT NULL DEFAULT (getdate()),
	[dtEnd] [datetime] NOT NULL DEFAULT (CONVERT([datetime],'12/31/9999 23:59:59.997',(0))),
	[dtLastLoggedIn] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[userRoleId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [raw0].[Instance]    Script Date: 4/25/2022 7:44:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [raw0].[Instance](
	[kAnum] [dbo].[anum] NOT NULL,
	[cik] [dbo].[int10] NULL,
	[otherCiks] [varchar](max) NULL,
	[instance] [varchar](32) NULL,
	[comments] [smallint] NULL,
	[vendor] [varchar](50) NULL,
	[identifier] [char](10) NULL,
	[ad] [varchar](10) NULL,
	[companyName] [varchar](160) NULL,
	[city] [varchar](30) NULL,
	[state] [char](2) NULL,
	[zip] [varchar](10) NULL,
	[acceptanceDatetime] [char](14) NULL,
	[formType] [varchar](20) NULL,
	[type] [varchar](20) NULL,
	[period] [dbo].[date8] NULL,
	[filingDate] [dbo].[date8] NULL,
	[dateOfFilingDateChange] [dbo].[date8] NULL,
	[fyEnd] [char](4) NULL,
	[tAttachment] [varchar](50) NULL,
	[sic] [char](4) NULL,
	[publicDocumentCount] [smallint] NULL,
	[irsNumber] [char](10) NULL,
	[stateOfIncorporation] [char](2) NULL,
	[act] [char](2) NULL,
	[fileNumber] [varchar](15) NULL,
	[filmNumber] [varchar](10) NULL,
	[bIneffective] [bit] NULL CONSTRAINT [DF_Instance_bIneffective]  DEFAULT ((0)),
	[sCorrection] [varchar](max) NULL,
	[sComments] [varchar](max) NULL,
	[iRelease] [smallint] NULL,
	[bLevel4] [bit] NULL,
	[iUsed] [smallint] NULL CONSTRAINT [DF_Instance_iUsed]  DEFAULT ((0)),
	[iAbstractCustomMember] [smallint] NULL CONSTRAINT [DF_Instance_iNumeric]  DEFAULT ((0)),
	[iNonabstractCustomMember] [smallint] NULL CONSTRAINT [DF_Instance_iMonetary]  DEFAULT ((0)),
	[iAbstractNoncustomMember] [smallint] NULL CONSTRAINT [DF_Instance_iTextblock]  DEFAULT ((0)),
	[iAbstractCustomNonmember] [smallint] NULL CONSTRAINT [DF_Instance_iCustomMember]  DEFAULT ((0)),
	[iNonabstractNoncustomMember] [smallint] NULL CONSTRAINT [DF_Instance_iCustomNonmember]  DEFAULT ((0)),
	[iNonabstractCustomNonmember] [smallint] NULL CONSTRAINT [DF_Instance_iCustomNonabstract]  DEFAULT ((0)),
	[iAbstractNoncustomNonmember] [smallint] NULL,
	[iNonabstractNoncustomNonmember] [smallint] NULL,
	[sAdsh20] [char](20) NULL,
	[uFolder] [varchar](max) NULL,
	[iMondayYear] [smallint] NULL,
	[iMondayMonth] [smallint] NULL,
	[iMondayWeek] [smallint] NULL,
	[iWeekday] [smallint] NULL,
	[sDay] [char](2) NULL,
	[fkRC] [int] NULL,
	[sRcEndDate] [dbo].[date8] NULL,
	[sDeiPeriodEndDate] [dbo].[date8] NULL,
	[sBsMaxDate] [dbo].[date8] NULL,
	[sErDate] [dbo].[date8] NULL,
	[sBsUom] [dbo].[name] NULL,
	[iMfp] [int] NULL,
	[iMfc] [int] NULL,
	[tStatus] [char](5) NULL,
	[tradingSymbol] [varchar](30) NULL,
	[iNegatedFact] [int] NULL,
	[iNegativeFact] [int] NULL,
	[iCalculations] [int] NULL CONSTRAINT [DF_Instance_iCalculations]  DEFAULT ((0)),
	[iNonnumericFact] [int] NULL CONSTRAINT [DF_Instance_iNonnumericFact]  DEFAULT ((0)),
	[iNumericFact] [int] NULL CONSTRAINT [DF_Instance_iNumericFact]  DEFAULT ((0)),
	[bureau] [varchar](50) NULL,
 CONSTRAINT [PK_Instance] PRIMARY KEY CLUSTERED 
(
	[kAnum] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  View [norm2_ops].[ViewSolrEntities]    Script Date: 4/25/2022 7:44:26 PM ******/
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
ALTER TABLE [ops1].[Profile]  WITH CHECK ADD FOREIGN KEY([environmentId])
REFERENCES [ops1].[Environment] ([environmentId])
GO
ALTER TABLE [ops1].[Property]  WITH CHECK ADD FOREIGN KEY([profileId])
REFERENCES [ops1].[Profile] ([profileId])
GO
ALTER TABLE [ops1].[UserRole]  WITH CHECK ADD FOREIGN KEY([idapRoleId])
REFERENCES [ops1].[IdapRole] ([idapRoleId])
GO
ALTER TABLE [ops1].[UserRole]  WITH CHECK ADD FOREIGN KEY([idapRoleId])
REFERENCES [ops1].[IdapRole] ([idapRoleId])
GO
ALTER TABLE [ops1].[UserRole]  WITH CHECK ADD FOREIGN KEY([idapUserId])
REFERENCES [ops1].[IdapUser] ([idapUserId])
GO
ALTER TABLE [ops1].[UserRole]  WITH CHECK ADD FOREIGN KEY([idapUserId])
REFERENCES [ops1].[IdapUser] ([idapUserId])
GO
ALTER TABLE [ops1].[UserRole]  WITH CHECK ADD FOREIGN KEY([profileId])
REFERENCES [ops1].[Profile] ([profileId])
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The four-digit SIC code. Not all SIC codes have any companies assigned to them.' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Sic4Map', @level2type=N'COLUMN',@level2name=N'sSIC'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The AD group(s) to which this SIC code is normally assigned.' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Sic4Map', @level2type=N'COLUMN',@level2name=N'sAD'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Uppercase text describing the SIC code.' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Sic4Map', @level2type=N'COLUMN',@level2name=N'sDescription'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Integer representing the Fama-French Industry 49 codes (FFI49) from 1 to 49. NULL if no industry.' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Sic4Map', @level2type=N'COLUMN',@level2name=N'iFFI49'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Accession Number. The 20-character string formed from the 18-digit number assigned by the Commission to each EDGAR submission.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'adsh'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Central Index Key (CIK). Ten digit number assigned by the Commission to each registrant that submits filings.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'cik'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Name of registrant. This corresponds to the name of the legal entity as recorded in EDGAR as of the filing date.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'name'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The submission type of the registrant''s filing.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'form'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Balance Sheet Date.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'period'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Fiscal Year Focus (as defined in EFM Ch. 6).' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'fy'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Fiscal Period Focus (as defined in EFM Ch. 6) within Fiscal Year. The 10-Q for the 1st, 2nd and 3rd quarters would have a fiscal period focus of Q1, Q2 (or H1), and Q3 (or M9) respectively, and a 10-K would have a fiscal period focus of FY.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'fp'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Fiscal Year End Date.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'fyend'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The date of the registrant''s filing with the Commission.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'filed'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The acceptance date and time of the registrant''s filing with the Commission. Filings accepted after 5:30pm EST are considered filed on the following business day.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'accepted'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Standard Industrial Classification (SIC). Four digit code assigned by the Commission as of the filing date, indicating the registrant''s type of business.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'sic'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The city of the registrant''s business address.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'bacity'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The state or province of the registrant''s business address, if field countryba is US or CA.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'bastpr'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The ISO 3166-1 country of the registrant''s business address.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'bacountry'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The zip code of the registrant''s business address.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'bazip'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The first line of the street of the registrant''s business address.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'bas1'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The second line of the street of the registrant''s business address.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'bas2'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The phone number of the registrant''s business address.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'baph'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The city of the registrant''s mailing address.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'macity'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The state or province of the registrant''s mailing address, if field countryma is US or CA.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'mastpr'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The ISO 3166-1 country of the registrant''s mailing address.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'macountry'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The zip code of the registrant''s mailing address.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'mazip'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The first line of the street of the registrant''s mailing address.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'mas1'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The second line of the street of the registrant''s mailing address.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'mas2'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The state or province of incorporation for the registrant, if countryinc is US or CA, otherwise NULL.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'incstpr'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The country of incorporation for the registrant.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'inccountry'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Employee Identification Number, 9 digit identification number assigned by the Internal Revenue Service to business entities operating in the United States.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'irsnumber'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Legal Entity Identifier of the company with CIK as given, or NULL' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'lei'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Previous Report –TRUE indicates that the submission information was subsequently amended prior to the end cutoff date of the data set.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'prevrpt'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'TRUE indicates that the XBRL submission contains quantitative disclosures within the footnotes and schedules at the required detail level (e.g., each amount).' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'detail'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the submitted XBRL Instance Document (EX-101.INS) type data file. The name often begins with the company ticker symbol.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'instance'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Accelerated Filer Status as it appears in the XBRL instance ''required context''.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'afs'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Filer status with the Commission at the time of submission: 1-LAF=Large Accelerated, 2-ACC=Accelerated, 3-SRA=Smaller Reporting Accelerated, 4-NON=Non-Accelerated, 5-SML=Smaller Reporting Filer, NULL=not assigned.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'fstat'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Well Known Seasoned Issuer (WKSI). An issuer that meets specific Commission requirements at some point during a 60-day period preceding the date the issuer satisfies its obligation to update its shelf registration statement.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'wksi'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Number of Central Index Keys (CIK) of registrants (i.e., business units) included in the consolidating entity''s submitted filing.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'nciks'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Additional CIKs of co-registrants included in a consolidating entity''s EDGAR submission, separated by spaces. If there are no other co-registrants (i.e., nciks = 1), the value of aciks is NULL. For a very small number of filers, the list of co-registrants is too long to fit in the field. Where this is the case, PARTIAL will appear at the end of the list indicating that not all co-registrants'' CIKs are included in the field; users should refer to the complete submission file for all CIK information.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'aciks'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Most recent former name of the registrant, if any.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'former'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Date of change from the former name, if any.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'changed'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Public float, in USD, if provided in this submission.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'pubfloatusd'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Date on which the public float was measured by the filer.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'floatdate'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'If the public float value was computed by summing across several tagged values, this indicates the nature of the summation.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'floataxis'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'If the public float was computed, the number of terms in the summation.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'floatmems'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Primary trading symbol' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'ticker'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'TRUE if there are other trading symbols' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'otickers'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'SEC Assistant Director (AD) of the filer, or NULL if not assigned' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'secad'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'dei value of ''CurrentFiscalYearEndDate'' in XBRL Document .' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'xbrl_fye'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'From raw0.Instance fyEnd. The four characters MMDD indicating the FISCAL-YEAR-END of the filer with the main CIK, from Submission Header hdr.sgml.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'filing_fye'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'From raw0.Instance vendor field. A "best guess" at the vendor product that produced the filing (or NULL) based on the comments and other clues. Examples: Bowne, XMark, CrossTag, Fujitsu. Set by the loader and subsequently updated by a trigger.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'vendor'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'From raw0.Instance bureau field.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'bureau'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indicates if it is an inline xbrl submission CHARINDEX(''_htm.xml'', instance) >0.' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'isInline'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Derived from formula based on raw0.Instance fields - cust_pct=(iNonabstractCustomNonmember * 1.0  / (iNonabstractCustomNonmember + iNonabstractNoncustomNonmember)) .' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'cust_pct'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Accounting Basis us-gaap,ifrs .' , @level0type=N'SCHEMA',@level0name=N'norm1', @level1type=N'TABLE',@level1name=N'SubCache', @level2type=N'COLUMN',@level2name=N'act_standard'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'18 digits of the accession number followed by two fractional digits indicating which of more than one XBRL instance in the submission. Set by the instance loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'kAnum'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Central Index Key. This cik is the xbrli:identifier used in the instance. Example: 789019. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'cik'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A space-separated list of CIKs other than the main cik, that appear in the SGML header as filers. Some filers have a dozen or more co-registrants. Example: Carnival Corp and Carnival Ltd have the CIKs 815097 and 1125259, 815097 appears in the instance, and 1125259 appears as otherCiks. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'otherCiks'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The EDGAR filename of the EX-100 or EX-101.INS attachment to the filing. EDGAR file names are limited to 32 characters and have other syntax restrictions. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'instance'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'NOT USED. The count of XML comment elements appearing in the instance. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'comments'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A "best guess" at the vendor product that produced the filing (or NULL) based on the comments and other clues. Examples: Bowne, XMark, CrossTag, Fujitsu. Set by the loader and subsequently updated by a trigger.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'vendor'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'No longer used. The ten character, zero-padded version of the cik. This is the value that appears in the xbrli:identifier elements of the instance. Example: 0000789019. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'identifier'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The Assistant Director (AD) to which corporate filings are assigned. See http://www.sec.gov/info/edgar/siccodes.htm. Examples: "5", "6", "2 & 3", "3 6 11". Hence this is a string. It is NULL or an empty string if it is an IM filing. Set initially by the loader and then subsequently updated by a trigger.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'ad'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Contents of the CONFORMED-NAME field corresponding to the filer whose CIK matches the CIK field. Example: "ARCHER DANIELS MIDLAND CO". Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'companyName'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Contents of the BUSINESS-ADDRESS/CITY for the filer of the CIK. Example: CLEVELAND. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'city'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Contents of the BUSINESS-ADDRESS/STATE for the filer of the CIK. This might not be a US state. See http://www.sec.gov/edgar/searchedgar/edgarstatecodes.htm. Example: Y8. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'state'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Contents of the BUSINESS-ADDRESS/ZIP for the filer of the CIK. Postal code is usually US Zip or Zip+4 but could be Canadian or other code. Example: "T2P 3M9". Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'zip'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'14 character representation of the second the filing was accepted in the form CCYYMMDDHHMMSS. Example: 20110315125207 is 12:52:07 on March 15, 2011. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'acceptanceDatetime'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The EDGAR submission type as given in the SGML header. See http://www.sec.gov/info/edgar/forms/edgform.pdf. Example: "10-12 G/A". Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'formType'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The normalized form type, found by trimming any "/A" indicator from the form type. Computable from FormMap table. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'type'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'For periodic filings, the date representing the end of the filing period. Note that this is NOT reliable. Example: 20110227 is 27 Feb 2011. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'period'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EDGAR submissions received after 5:30pm Eastern time receive a filing date that is the next business day, which could be up to four days later than the date in the acceptance datetime. For legal purposes it is the filing date that matters. Example: an acceptanceDatetime of 20111223173100 (one minute after 5:30pm before Christmas weekend) receives a filing date 20111227 (the next business day, a Tuesday). Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'filingDate'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EDGAR system errors or other issues may cause the filing date to be subsequently changed. Example: 20110311. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'dateOfFilingDateChange'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The four characters MMDD indicating the FISCAL-YEAR-END of the filer with the main CIK. This value is NOT reliable. Example: 0227 as fiscal year-end for a company whose true fiscal year end fell on March 1st. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'fyEnd'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The attachment type of the instance. At this time all the filings in IDAP are "EX-101" attachments. Voluntary filing program instances were attachment type "EX-100". Other attachment types are possible in the future. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'tAttachment'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'4-character SIC code, the ASSIGNED-SIC of the CIK in the Instance. A filer does not choose their own SIC, and the SIC of a CIK can change. See http://en.wikipedia.org/wiki/SIC_code. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'sic'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of documents attached to the entire filing. An XBRL document would typically have 6 or more, but exact number is not important. Large numbers of attachments indicate many graphics. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'publicDocumentCount'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The nine-digit IRS number of the company, which is required but not necessarily correct. Example: 411276891, 000000000. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'irsNumber'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Two-character state / province / country code where the company is incorporated, sometimes blank. Example: DE, Y8. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'stateOfIncorporation'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The act under which the filing is required. 33 means registration statement, periodic statements are 34, 40 are related to investment management. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'act'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A 9- to 12-character code indicating the SEC "file" that was started when a security is first registered, or an additional file derived from the base. Examples: 033-36383, 001-11307-01, For over 50 years a unique identifier called an SEC file number is internally assigned to each SEC registrant and/or filer. The first three digits of this SEC compound number, which are listed in this column, and called "SEC Key-File Number", indicates the type of registrant or filer or the act under which the filing was made (e.g. 801- represents an investment adviser). Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'fileNumber'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The 9-digit film number corresponding to the file number. Example: 11583796. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'filmNumber'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'1 if the instance has been superseded in its entirety by a subsequent EDGAR filing, or is otherwise not to be used in statistics or other analysis. Set by raw0.UpdateInstance procedure, which is called from trigger InstanceAfter1.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'bIneffective'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'NULL, or the string contents of any correction.sed file that was applied to the incoming instance before loading, or NULL. Occasionally, filings arrive that cannot due to differences between different validators. A file correction.sed may be applied to such files before processing, to enable loading. Example: "s/startDate>0006/startDate>2006/g" is a sed command that corrects the year 6. A.D. to 2006 A.D. where it appears in a startDate element. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'sCorrection'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'NULL, or the string-join with newline separators, of the first five XML comment elements in the original XBRL filing attachments in the order that they appear within these files, in this order: Instances, Schemas, Calculation, Definition, Label and Presentation linkbases. Example: "<!-- Prepared by EDGARFantastix version 2.23 --><!-- Prepared by EDGARFantastix 2.23 -->". Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'sComments'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The four-digit year of the latest standard namespace used in the instance. An instance using dei 2013 and rr 2012 would have an iRelease value of 2013. Set by the loader.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'iRelease'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'1 if the filing is a CF division filing that has detail tags as described in S-T 405. See EFM 6.7.12. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'bLevel4'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of elements used in any facts or contexts in the instance. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'iUsed'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of Abstract, Custom, Member elements used in any facts or contexts in the instance. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'iAbstractCustomMember'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of Nonabstract, Custom, Member elements used in any facts or contexts in the instance. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'iNonabstractCustomMember'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of Abstract, Noncustom, Member elements used in any facts or contexts in the instance. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'iAbstractNoncustomMember'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of Abstract, Custom, Nonmember elements used in any facts or contexts in the instance. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'iAbstractCustomNonmember'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of Nonabstract, Noncustom, Member elements used in any facts or contexts in the instance. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'iNonabstractNoncustomMember'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of Nonabstract, Custom, Nonmember elements used in any facts or contexts in the instance. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'iNonabstractCustomNonmember'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of Nonabstract, Noncustom, Nonmember elements used in any facts or contexts in the instance. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'iAbstractNoncustomNonmember'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of Nonabstract, Noncustom, Nonmember elements used in any facts or contexts in the instance. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'iNonabstractNoncustomNonmember'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The 20-character accession number with a dash. NULL until set by raw0.UpdateInstance. This is a useful flag to tell whether or not UpdateInstance has been called on the instance in question.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'sAdsh20'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'URL of the folder on the SEC EDGAR Web site where the instance and other files for this accession number are published. This could be computed from the cik and adsh and is stored here for quick access. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'uFolder'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The year in which the Monday of the week the filing was accepted falls. For load analysis it is useful to characterize the date a filing was accepted in terms of a week in the year, and day of that week. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'iMondayYear'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month in which the monday of the week the filing was accepted falls. Used when collecting weekly filing statistics. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'iMondayMonth'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The week (from 1-52) in which the filing was accepted. Used when collecting weekly filing statistics. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'iMondayWeek'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The weekday, from 1-7, on which the filing was accepted. Used when collecting weekly filing statistics. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'iWeekday'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The two-character string representing the day of the month on which the filing was accepted. Used when displaying filing statistics.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'sDay'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The kContext value of the required context. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'fkRC'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The period end date of the DEI elements (the "required context") that is supposed to match the date of the financial report, but often does not. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'sRcEndDate'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The dei:DocumentPeriodEndDate in the "required context" that is supposed to match the date of the financial report, but often does not. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'sDeiPeriodEndDate'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The date of the latest reported column on the balance sheet having more than 3 facts reported. If the balance sheet cannot be found, then the CF, EQ, IS and UN statements are tried. NULL for any instance not having a balance sheet. Computed by raw0.sBsMaxOnStatement and set in procedure raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'sBsMaxDate'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'For a financial statement instance, this has the date of the first 8-K earnings release that preceded (or was simultaneous with) the filing. This is derived from analyis of data in tables drawn from EDGAR, the data is not XBRL encoded. NULL if no release date has yet been found or the filing is not a financial statement.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'sErDate'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The predominant currency (unit of measure) on the balance sheet, according to the same criteria for sBsMaxDate. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'sBsUom'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of monetary elements that are presented in Statements and that have a fact whose end date is the same as that of the required context. This is set by procedure raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'iMfp'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of monetary elements that are presented in Statements and that have a fact whose end date is the same as that of the required context AND that participate in at least one calcuation relationship in any role. Set by raw0.UpdateInstance.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'iMfc'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EDGAR filer status for CF filingsne of the values 1-LAF, 2-ACC, 3-NON, 4-UNK or NULL if the filer status was inferrable from information in the EDGAR replica database. Computed by dbo.EdrFilerStatusEquivalent and set by raw0.UpdateInstance. This should be recomputed periodically because of updates to the EDGAR replica database.' , @level0type=N'SCHEMA',@level0name=N'raw0', @level1type=N'TABLE',@level1name=N'Instance', @level2type=N'COLUMN',@level2name=N'tStatus'
GO
