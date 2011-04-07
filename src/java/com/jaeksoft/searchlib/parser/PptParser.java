/**   
 * License Agreement for Jaeksoft OpenSearchServer
 *
 * Copyright (C) 2008-2011 Emmanuel Keller / Jaeksoft
 * 
 * http://www.open-search-server.com
 * 
 * This file is part of Jaeksoft OpenSearchServer.
 *
 * Jaeksoft OpenSearchServer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * Jaeksoft OpenSearchServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Jaeksoft OpenSearchServer. 
 *  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.jaeksoft.searchlib.parser;

import java.io.IOException;

import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.record.TextHeaderAtom;
import org.apache.poi.hslf.usermodel.SlideShow;

public class PptParser extends Parser {

	private static ParserFieldEnum[] fl = { ParserFieldEnum.title,
			ParserFieldEnum.note, ParserFieldEnum.body, ParserFieldEnum.other,
			ParserFieldEnum.filename, ParserFieldEnum.content_type };

	public PptParser() {
		super(fl);
	}

	@Override
	protected void parseContent(LimitInputStream inputStream)
			throws IOException {

		SlideShow ppt = new SlideShow(inputStream);
		Slide[] slides = ppt.getSlides();
		for (Slide slide : slides) {
			TextRun[] textRuns = slide.getTextRuns();
			for (TextRun textRun : textRuns) {
				ParserFieldEnum field;
				switch (textRun.getRunType()) {
				case TextHeaderAtom.TITLE_TYPE:
				case TextHeaderAtom.CENTER_TITLE_TYPE:
					field = ParserFieldEnum.title;
					break;
				case TextHeaderAtom.NOTES_TYPE:
					field = ParserFieldEnum.note;
					break;
				case TextHeaderAtom.BODY_TYPE:
				case TextHeaderAtom.CENTRE_BODY_TYPE:
				case TextHeaderAtom.HALF_BODY_TYPE:
				case TextHeaderAtom.QUARTER_BODY_TYPE:
					field = ParserFieldEnum.body;
					break;
				case TextHeaderAtom.OTHER_TYPE:
				default:
					field = ParserFieldEnum.other;
					break;
				}
				String[] frags = textRun.getText().split("\\n");
				for (String frag : frags)
					addField(field, frag.replaceAll("\\s+", " "));
			}
		}
		langDetection(10000, ParserFieldEnum.body);

	}

	@Override
	protected void parseContent(LimitReader reader) throws IOException {
		throw new IOException("Unsupported");
	}

	@Override
	public ParserFieldEnum[] getParserFieldList() {
		return fl;
	}

}
