package zyzx.linke.model;

/**
 * Created by austin on 2017/5/3.
 * Desc: 省市行政区划表 PO
 */

public class Area {
    private Integer id;
    private String areacode;
    private Integer depth;
    private String name;
    private Integer parentid;
    private String zipcode;

    public Area(){}

    public Area(Integer id, String areacode, Integer depth, String name, Integer parentid, String zipcode) {
        this.id = id;
        this.areacode = areacode;
        this.depth = depth;
        this.name = name;
        this.parentid = parentid;
        this.zipcode = zipcode;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAreacode() {
        return areacode;
    }

    public void setAreacode(String areacode) {
        this.areacode = areacode;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentid() {
        return parentid;
    }

    public void setParentid(Integer parentid) {
        this.parentid = parentid;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Area) {
            if (this.getId().equals(((Area) obj).getId())) {
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Area{" +
                "id=" + id +
                ", areacode='" + areacode + '\'' +
                ", depth=" + depth +
                ", name='" + name + '\'' +
                ", parentid=" + parentid +
                ", zipcode='" + zipcode + '\'' +
                '}';
    }
}
