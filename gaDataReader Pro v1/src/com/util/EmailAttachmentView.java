package com.util;

import java.io.File;

public class EmailAttachmentView
	{
		private String mimeType;
		private String fileName;
		private byte[] content;
		private File lFile;
		private String attachmentText = "";

		public String getAttachmentText()
			{
				return attachmentText;
			}

		public void setAttachmentText( String attachmentText )
			{
				this.attachmentText = attachmentText;
			}

		public String getMimeType()
			{
				return mimeType;
			}

		public String getFileName()
			{
				return fileName;
			}

		public byte[] getContent()
			{
				return content;
			}

		public File getFile()
			{
				return this.lFile;
			}

		public void setMimeType( String mimeType )
			{
				this.mimeType = mimeType;
			}

		public void setFileName( String fileName )
			{
				this.fileName = fileName;
			}

		public void setContent( byte[] content )
			{
				this.content = content;
			}

		public void setFile( File lFile )
			{
				this.lFile = lFile;
			}

	}
