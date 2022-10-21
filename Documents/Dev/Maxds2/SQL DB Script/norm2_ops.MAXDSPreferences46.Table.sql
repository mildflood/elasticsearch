USE [Idap]
GO
/****** Object:  Table [norm2_ops].[MAXDSPreferences46]    Script Date: 4/25/2022 5:02:32 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [norm2_ops].[MAXDSPreferences46](
	[PREFERENCEID] [int] IDENTITY(1,1) NOT NULL,
	[COMPANYNAME] [varchar](900) NULL,
	[TERMNAME] [varchar](255) NULL,
	[CODE] [varchar](255) NULL,
	[PREFERENCENAME] [varchar](255) NULL,
	[RESULTS_LINK] [varchar](255) NULL DEFAULT ('NA'),
	[VALIDATION_STATUS] [varchar](255) NULL DEFAULT ('NA'),
	[RESEARCH_LINK] [varchar](255) NULL DEFAULT ('NA'),
	[USERID] [varchar](50) NOT NULL,
	[isQuaterly] [varchar](255) NULL DEFAULT ('FALSE'),
	[FSQV_LINK] [varchar](255) NULL DEFAULT ('NA'),
	[dt] [datetime] NULL DEFAULT (getdate()),
PRIMARY KEY CLUSTERED 
(
	[PREFERENCEID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [ak_company_name_code_MAXDSPreferences46] UNIQUE NONCLUSTERED 
(
	[CODE] ASC,
	[USERID] ASC,
	[PREFERENCENAME] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
