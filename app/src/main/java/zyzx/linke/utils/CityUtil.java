package zyzx.linke.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class CityUtil {

	private String mCity;
	private Context mContext;

	public CityUtil(Context contextPara) {
		mContext = contextPara;
	}

	public CityUtil(Context context, String city) {
		mContext = context;
		mCity = city;
	}

	public List<String> parseByPull() {
		InputStream inputStream = null;
		boolean cityNamePassed = false;
		boolean cityLevelPassed = false;
		boolean isThisDistricts = cityLevelPassed & cityNamePassed;
		boolean isDistrictsStart = false;
		List<String> resultLists = new ArrayList<String>();
		try {
			AssetManager assetManager = mContext.getAssets();
			inputStream = assetManager.open("chinaCity.xml");

			// 创建XmlPullParserFactory解析工厂
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			// 通过XmlPullParserFactory工厂类实例化一个XmlPullParser解析类
			XmlPullParser parser = factory.newPullParser();
			// 根据指定的编码来解析xml文档
			parser.setInput(inputStream, "utf-8");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					parser.nextTag();

				case XmlPullParser.START_TAG:
					if (parser.getName().equalsIgnoreCase("name")) {
						String name = parser.getText();
						if (name.contains(mCity)) {
							cityNamePassed = true;
						}

						if (isDistrictsStart) {
							resultLists.add(parser.getText());
						}

					} else if (parser.getName().equalsIgnoreCase("level")) {
						String level = parser.getText();
						if (level.equalsIgnoreCase("city")) {
							cityLevelPassed = true;
						}
					} else if (isThisDistricts
							&& parser.getName().equalsIgnoreCase("districts")) {
						isDistrictsStart = true;
					}

				case XmlPullParser.END_TAG:
					if (isThisDistricts
							&& parser.getName().equalsIgnoreCase("districts")
							&& isDistrictsStart) {

						break;
					}

				}
				parser.nextTag();
			}

			inputStream.close();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultLists;
	}

	public List<String> getItsDistricts() {

		List<String> resultLists = new ArrayList<String>();

		AssetManager assetManager = mContext.getAssets();
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open("ProvinceAndCity.xml");
			XMLDOMParser parser = new XMLDOMParser();
			Document doc = parser.getDocument(inputStream);
			NodeList cityList = doc.getElementsByTagName("City");

			Element cityChosen = null;
			for (int i = 0; i < cityList.getLength(); i++) {
				Element e = (Element) cityList.item(i);
				String cityName = e.getAttribute("name");
				if (cityName.contains(mCity)) {
					cityChosen = (Element) cityList.item(i);
					break;
				}
			}

			if (cityChosen != null) {
				NodeList areaList = cityChosen.getElementsByTagName("Area");
				for (int i = 0; i < areaList.getLength(); i++) {
					Element e = (Element) areaList.item(i);
					resultLists.add(e.getAttribute("name"));
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return resultLists;

	}

	public Document getDomElement(InputStream stream) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(stream);

		} catch (ParserConfigurationException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}
		// return DOM
		return doc;
	}

}
