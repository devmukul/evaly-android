package bd.com.evaly.evalyshop.views.wrapViewPager;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import bd.com.evaly.evalyshop.listener.ObjectAtPositionInterface;

public abstract class ObjectAtPositionPagerAdapter extends FragmentStatePagerAdapter implements ObjectAtPositionInterface {
    protected SparseArray<Object> objects = new SparseArray<>();

    public ObjectAtPositionPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

//    @Override
//    public final Object instantiateItem(ViewGroup container, int position) {
//        Object object = instantiateItemObject(container, position);
//        objects.put(position, object);
//        return object;
//    }

//    public abstract Object instantiateItemObject(ViewGroup container, int position);

    @Override
    public final void destroyItem(ViewGroup container, int position, Object object) {
        objects.remove(position);
      //  destroyItemObject(container, position, object);
    }

//    public abstract void destroyItemObject(ViewGroup container, int position, Object object);

    @Override
    public Object getObjectAtPosition(int position) {
        return objects.get(position);
    }
}
