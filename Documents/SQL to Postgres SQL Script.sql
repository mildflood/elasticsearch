USE [Idap]
GO
/****** Object:  Table [norm2_ops].[MAXDFeedBack]    Script Date: 4/25/2022 4:16:20 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [norm2_ops].[MAXDFeedBack](
	[FEEDBACKID] [int] IDENTITY(1,1) NOT NULL,
	[ISSUETYPE] [varchar](255) NULL,
	[CATEGORY] [varchar](255) NULL,
	[NAME] [varchar](255) NULL,
	[EMAIL] [varchar](255) NULL,
	[PHONE] [varchar](255) NULL,
	[MESSAGE] [varchar](255) NULL,
	[USERID] [varchar](50) NOT NULL,
	[dtCreated] [datetime] NULL DEFAULT (getdate()),
PRIMARY KEY CLUSTERED 
(
	[FEEDBACKID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [norm2_ops].[MAXDSAccuracyTestCases]    Script Date: 4/25/2022 4:16:20 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [norm2_ops].[MAXDSAccuracyTestCases](
	[COMPANY_NAME] [varchar](255) NULL,
	[YEAR] [varchar](255) NULL,
	[TERM_NAME] [varchar](255) NULL,
	[EXPECTED_VALUE] [varchar](255) NULL,
	[ACTUAL_VALUE] [varchar](255) NULL,
	[TEST_STATUS] [varchar](255) NULL,
	[LAST_RUN] [varchar](255) NULL,
	[COMMENTS] [varchar](50) NOT NULL,
 CONSTRAINT [accuracy_pk] UNIQUE NONCLUSTERED 
(
	[COMPANY_NAME] ASC,
	[YEAR] ASC,
	[TERM_NAME] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [norm2_ops].[MaxdsFeedback]    Script Date: 4/25/2022 4:16:20 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [norm2_ops].[MaxdsFeedback](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[created] [smalldatetime] NULL,
	[typeFeedBack] [varchar](60) NULL,
	[category] [varchar](60) NULL,
	[message] [varchar](max) NULL,
	[ip] [varchar](48) NULL,
	[status] [varchar](60) NULL,
	[userName] [varchar](50) NULL,
	[userEmail] [varchar](30) NULL,
	[userPhoneNumber] [varchar](30) NULL,
PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [norm2_ops].[MAXDSPreferences]    Script Date: 4/25/2022 4:16:20 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [norm2_ops].[MAXDSPreferences](
	[PREFERENCEID] [int] IDENTITY(1,1) NOT NULL,
	[COMPANYNAME] [varchar](max) NULL,
	[TERMNAME] [varchar](255) NULL,
	[CODE] [varchar](255) NULL,
	[PREFERENCENAME] [varchar](255) NULL,
	[RESULTS_LINK] [varchar](255) NULL DEFAULT ('NA'),
	[VALIDATION_STATUS] [varchar](255) NULL DEFAULT ('NA'),
	[RESEARCH_LINK] [varchar](255) NULL DEFAULT ('NA'),
	[USERID] [varchar](50) NOT NULL,
	[isQuaterly] [varchar](255) NULL DEFAULT ('FALSE'),
PRIMARY KEY CLUSTERED 
(
	[PREFERENCEID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [ak_company_name_code] UNIQUE NONCLUSTERED 
(
	[CODE] ASC,
	[USERID] ASC,
	[PREFERENCENAME] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [norm2_ops].[MAXDSPreferences46]    Script Date: 4/25/2022 4:16:20 PM ******/
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
/****** Object:  Table [norm2_ops].[MAXDSProfileTestCases]    Script Date: 4/25/2022 4:16:20 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [norm2_ops].[MAXDSProfileTestCases](
	[COMPANY_NAME] [varchar](255) NULL,
	[TERM_NAME] [varchar](255) NULL,
	[EXPECTED_VALUE] [varchar](255) NULL,
	[ACTUAL_VALUE] [varchar](255) NULL,
	[TEST_STATUS] [varchar](255) NULL,
	[LAST_RUN] [varchar](255) NULL,
	[COMMENTS] [varchar](50) NOT NULL,
	[YEAR] [nchar](10) NULL,
	[EXPRESSION_NAME] [nchar](255) NULL,
 CONSTRAINT [profile_pk] UNIQUE NONCLUSTERED 
(
	[COMPANY_NAME] ASC,
	[TERM_NAME] ASC,
	[YEAR] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [norm2_ops].[MAXDSSharedPreferences]    Script Date: 4/25/2022 4:16:20 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [norm2_ops].[MAXDSSharedPreferences](
	[PREFERENCEID] [int] IDENTITY(1,1) NOT NULL,
	[COMPANYNAME] [varchar](max) NULL,
	[TERMNAME] [varchar](255) NULL,
	[CODE] [varchar](255) NULL,
	[PREFERENCENAME] [varchar](255) NULL,
	[RESULTS_LINK] [varchar](255) NULL DEFAULT ('NA'),
	[VALIDATION_STATUS] [varchar](255) NULL DEFAULT ('NA'),
	[RESEARCH_LINK] [varchar](255) NULL DEFAULT ('NA'),
	[USERID] [varchar](50) NOT NULL,
	[isQuaterly] [varchar](255) NULL DEFAULT ('FALSE'),
PRIMARY KEY CLUSTERED 
(
	[PREFERENCEID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [norm2_ops].[MAXDSSharedPreferences46]    Script Date: 4/25/2022 4:16:20 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [norm2_ops].[MAXDSSharedPreferences46](
	[PREFERENCEID] [int] IDENTITY(1,1) NOT NULL,
	[COMPANYNAME] [varchar](max) NULL,
	[TERMNAME] [varchar](255) NULL,
	[CODE] [varchar](255) NULL,
	[PREFERENCENAME] [varchar](255) NULL,
	[RESULTS_LINK] [varchar](255) NULL DEFAULT ('NA'),
	[VALIDATION_STATUS] [varchar](255) NULL DEFAULT ('NA'),
	[RESEARCH_LINK] [varchar](255) NULL DEFAULT ('NA'),
	[USERID] [varchar](50) NOT NULL,
	[isQuaterly] [varchar](255) NULL DEFAULT ('FALSE'),
PRIMARY KEY CLUSTERED 
(
	[PREFERENCEID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
ALTER TABLE [norm2_ops].[MaxdsFeedback] ADD  DEFAULT ('open') FOR [status]
GO