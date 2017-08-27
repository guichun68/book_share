package zyzx.linke.model.bean;

/**检查更新时返回给app的json
 * @author Austin
 *
 */
public class UpdateBeanVO {
    private int code;
	private String url;
	private String des;
	private String forceUpdate;
	private String apkFileName;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public String getForceUpdate() {
		return forceUpdate;
	}
	public void setForceUpdate(String forceUpdate) {
		this.forceUpdate = forceUpdate;
	}


    public String getApkFileName() {
        return apkFileName;
    }

    public void setApkFileName(String apkFileName) {
        this.apkFileName = apkFileName;
    }
}
