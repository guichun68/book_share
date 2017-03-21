package zyzx.linke.base;

import zyzx.linke.base.GlobalParams;
import zyzx.linke.model.IModel;

/**
 * Created by austin on 2017/3/19.
 */

public class IPresenter {

    private IModel mModel;

    protected IModel getModel(){
        if(mModel ==null){
            mModel = GlobalParams.getgModel();
        }
        return mModel;
    }

}
