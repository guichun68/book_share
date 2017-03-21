package zyzx.linke.base;

import java.io.IOException;
import java.util.Properties;

import zyzx.linke.model.IModel;
import zyzx.linke.presentation.IBookPresenter;
import zyzx.linke.presentation.IUserPresenter;

/**
 * 工厂
 * @author Administrator
 */
public class BeanFactoryUtil {
	public static Properties properties;

	static {
		properties = new Properties();
		try {
			properties.load(BeanFactoryUtil.class.getClassLoader().getResourceAsStream("bean.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取指定的实例
	 * @return
	 */
	public static <T> T getImpl(Class<T> clazz) {
		String key = clazz.getSimpleName();
		String className = properties.getProperty(key);

		if(key.equals("IUserPresenter")){
			if(GlobalParams.gUserPresenter!=null){
				return (T) GlobalParams.gUserPresenter;
			}
			try {
				GlobalParams.gUserPresenter = (IUserPresenter) Class.forName(className).newInstance();
				return (T) GlobalParams.gUserPresenter;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(key.equals("IModel")){
			if(GlobalParams.gModel!=null){
				return (T) GlobalParams.gModel;
			}
			try {
				GlobalParams.gModel = (IModel) Class.forName(className).newInstance();
				return (T) GlobalParams.gModel;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(key.equals("IBookPresenter")){
			if(GlobalParams.gBookPresenter!=null){
				return (T) GlobalParams.gBookPresenter;
			}
			try {
				GlobalParams.gBookPresenter = (IBookPresenter) Class.forName(className).newInstance();
				return (T) GlobalParams.gBookPresenter;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
