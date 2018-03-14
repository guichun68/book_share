package zyzx.linke.model.bean;

import java.util.List;

/**
 * Created by Austin on 2017-09-27.
 */

public class tt {

    /**
     * result : [94.838691711426]
     * result_num : 1
     * ext_info : {"faceliveness":1}
     * log_id : 3511702919092720
     */

    private int result_num;
    private ExtInfoBean ext_info;
    private long log_id;
    private List<Double> result;

    public int getResult_num() {
        return result_num;
    }

    public void setResult_num(int result_num) {
        this.result_num = result_num;
    }

    public ExtInfoBean getExt_info() {
        return ext_info;
    }

    public void setExt_info(ExtInfoBean ext_info) {
        this.ext_info = ext_info;
    }

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(long log_id) {
        this.log_id = log_id;
    }

    public List<Double> getResult() {
        return result;
    }

    public void setResult(List<Double> result) {
        this.result = result;
    }

    public static class ExtInfoBean {
        /**
         * faceliveness : 1.0
         */

        private double faceliveness;

        public double getFaceliveness() {
            return faceliveness;
        }

        public void setFaceliveness(double faceliveness) {
            this.faceliveness = faceliveness;
        }
    }
}
