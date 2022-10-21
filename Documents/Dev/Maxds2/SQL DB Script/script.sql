USE [Idap]
GO
/****** Object:  Table [ops1].[Environment]    Script Date: 4/25/2022 8:36:44 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [ops1].[Environment](
	[environmentId] [varchar](20) NOT NULL,
	[name] [varchar](max) NOT NULL,
	[description] [varchar](max) NULL,
	[sortOrder] [varchar](10) NULL,
PRIMARY KEY CLUSTERED 
(
	[environmentId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [ops1].[IdapRole]    Script Date: 4/25/2022 8:36:44 PM ******/
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
/****** Object:  Table [ops1].[IdapUser]    Script Date: 4/25/2022 8:36:44 PM ******/
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
/****** Object:  Table [ops1].[Profile]    Script Date: 4/25/2022 8:36:44 PM ******/
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
/****** Object:  Table [ops1].[Property]    Script Date: 4/25/2022 8:36:44 PM ******/
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
/****** Object:  Table [ops1].[UserRole]    Script Date: 4/25/2022 8:36:44 PM ******/
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
