USE [Idap]
GO
/****** Object:  Table [norm2_ops].[MaxdsFeedback]    Script Date: 4/25/2022 5:02:32 PM ******/
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
ALTER TABLE [norm2_ops].[MaxdsFeedback] ADD  DEFAULT ('open') FOR [status]
GO
