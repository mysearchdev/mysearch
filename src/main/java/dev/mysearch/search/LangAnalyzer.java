/**

Copyright (C) 2022 MySearch.Dev contributors (dev@mysearch.dev) 
Copyright (C) 2022 Sergey Nechaev (serg.nechaev@gmail.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 

*/
package dev.mysearch.search;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bg.BulgarianAnalyzer;
import org.apache.lucene.analysis.bn.BengaliAnalyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.ca.CatalanAnalyzer;
import org.apache.lucene.analysis.ckb.SoraniAnalyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.da.DanishAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.el.GreekAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.et.EstonianAnalyzer;
import org.apache.lucene.analysis.eu.BasqueAnalyzer;
import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.analysis.fi.FinnishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.ga.IrishAnalyzer;
import org.apache.lucene.analysis.gl.GalicianAnalyzer;
import org.apache.lucene.analysis.hi.HindiAnalyzer;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.hy.ArmenianAnalyzer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.lt.LithuanianAnalyzer;
import org.apache.lucene.analysis.lv.LatvianAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;
import org.apache.lucene.analysis.te.TeluguAnalyzer;
import org.apache.lucene.analysis.th.ThaiAnalyzer;
import org.apache.lucene.analysis.tr.TurkishAnalyzer;

public class LangAnalyzer {

	private static Map<Lang, Analyzer> analyzers = new HashMap<>();
	
	public static Analyzer get(Lang l) {

		if (analyzers.containsKey(l)) {
			return analyzers.get(l);
		}

		Analyzer analyzer = null;

		if (l == Lang.ar) analyzer = new ArabicAnalyzer(); else 
		if (l == Lang.bg) analyzer = new BulgarianAnalyzer(); else 
		if (l == Lang.bn) analyzer = new BengaliAnalyzer(); else
		if (l == Lang.br) analyzer = new BrazilianAnalyzer(); else // brazilian
		if (l == Lang.ca) analyzer = new CatalanAnalyzer(); else // catalan
		if (l == Lang.ckb) analyzer = new SoraniAnalyzer(); else // Sorani Kurdish
		if (l == Lang.cz) analyzer = new CzechAnalyzer(); else // czech
		if (l == Lang.da) analyzer = new DanishAnalyzer(); else // danish
		if (l == Lang.de) analyzer = new GermanAnalyzer(); else // german
		if (l == Lang.el) analyzer = new GreekAnalyzer(); else // greek
		if (l == Lang.en) analyzer = new EnglishAnalyzer(); else // english
		if (l == Lang.es) analyzer = new SpanishAnalyzer(); else // spanish
		if (l == Lang.et) analyzer = new EstonianAnalyzer(); else // estonian
		if (l == Lang.eu) analyzer = new BasqueAnalyzer(); else // basque
		if (l == Lang.fa) analyzer = new PersianAnalyzer(); else // persian
		if (l == Lang.fi) analyzer = new FinnishAnalyzer(); else // finnish
		if (l == Lang.fr) analyzer = new FrenchAnalyzer(); else // french
		if (l == Lang.ga) analyzer = new IrishAnalyzer(); else // irish
		if (l == Lang.gl) analyzer = new GalicianAnalyzer(); else // galician
		if (l == Lang.hi) analyzer = new HindiAnalyzer(); else // hindi
		if (l == Lang.hu) analyzer = new HungarianAnalyzer(); else // hungarian
		if (l == Lang.hy) analyzer = new ArmenianAnalyzer(); else // armenian
		if (l == Lang.id) analyzer = new IndonesianAnalyzer(); else // indonesian
		if (l == Lang.it) analyzer = new ItalianAnalyzer(); else // italian
		if (l == Lang.lt) analyzer = new LithuanianAnalyzer(); else // lithuanian
		if (l == Lang.lv) analyzer = new LatvianAnalyzer(); else // latvian
		if (l == Lang.nl) analyzer = new DutchAnalyzer(); else // dutch
		if (l == Lang.no) analyzer = new NorwegianAnalyzer(); else // norwegian
		if (l == Lang.pt) analyzer = new PortugueseAnalyzer(); else // portugese
		if (l == Lang.ro) analyzer = new RomanianAnalyzer(); else // romanian
		if (l == Lang.ru) analyzer = new RussianAnalyzer(); else // russian
		if (l == Lang.sv) analyzer = new SwedishAnalyzer(); else // swedish
		if (l == Lang.te) analyzer = new TeluguAnalyzer(); else // telugu
		if (l == Lang.th) analyzer = new ThaiAnalyzer(); else // thai
		if (l == Lang.tr) analyzer = new TurkishAnalyzer(); // turkish

		analyzers.put(l, analyzer);

		return analyzer;
	}

}
